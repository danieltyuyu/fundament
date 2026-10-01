package com.forkdevs.driveos.platform.billing.application.internal.commandservices;

import com.forkdevs.driveos.platform.billing.application.commandservices.VoucherCommandService;
import com.forkdevs.driveos.platform.billing.application.outboundservices.FacthubGateway;
import com.forkdevs.driveos.platform.billing.application.outboundservices.IssuerQueryGateway;
import com.forkdevs.driveos.platform.billing.application.outboundservices.WorkOrderQueryGateway;
import com.forkdevs.driveos.platform.billing.domain.model.aggregates.Voucher;
import com.forkdevs.driveos.platform.billing.domain.model.commands.AddPaymentCommand;
import com.forkdevs.driveos.platform.billing.domain.model.commands.GenerateVoucherCommand;
import com.forkdevs.driveos.platform.billing.domain.model.valueobjects.QuoteStatus;
import com.forkdevs.driveos.platform.billing.domain.model.valueobjects.VoucherCommandFailure;
import com.forkdevs.driveos.platform.billing.domain.repositories.QuoteRepository;
import com.forkdevs.driveos.platform.billing.domain.repositories.VoucherRepository;
import com.forkdevs.driveos.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the VoucherCommandService interface.
 * Orchestrates business logic for creating and paying invoices/receipts using outbound ports.
 */
@Service
public class VoucherCommandServiceImpl implements VoucherCommandService {

    private final VoucherRepository voucherRepository;
    private final QuoteRepository quoteRepository;
    private final FacthubGateway facthubGateway;
    private final IssuerQueryGateway issuerQueryGateway;
    private final WorkOrderQueryGateway workOrderQueryGateway;

    public VoucherCommandServiceImpl(
            VoucherRepository voucherRepository,
            QuoteRepository quoteRepository,
            FacthubGateway facthubGateway,
            IssuerQueryGateway issuerQueryGateway,
            WorkOrderQueryGateway workOrderQueryGateway) {
        this.voucherRepository = voucherRepository;
        this.quoteRepository = quoteRepository;
        this.facthubGateway = facthubGateway;
        this.issuerQueryGateway = issuerQueryGateway;
        this.workOrderQueryGateway = workOrderQueryGateway;
    }

    @Override
    @Transactional
    public Result<Voucher, VoucherCommandFailure> handle(GenerateVoucherCommand command) {
        // 1. Get and validate Quote
        var quoteOpt = quoteRepository.findById(command.quoteId());
        if (quoteOpt.isEmpty()) {
            return Result.failure(VoucherCommandFailure.QUOTE_NOT_FOUND);
        }
        var quote = quoteOpt.get();
        if (quote.getStatus() != QuoteStatus.APPROVED) {
            return Result.failure(VoucherCommandFailure.QUOTE_NOT_APPROVED);
        }

        // 2. Query Issuer RUC via Outbound Gateway
        var issuerRucOpt = issuerQueryGateway.getIssuerTaxIdByBranchId(quote.getBranchId().value());
        if (issuerRucOpt.isEmpty()) {
            return Result.failure(VoucherCommandFailure.ISSUER_NOT_FOUND);
        }
        String issuerRuc = issuerRucOpt.get();

        // 3. Issue Voucher via Facthub
        var externalInvoiceIdOpt = facthubGateway.issueVoucher(
                issuerRuc,
                command.type(),
                command.customerDocumentType(),
                command.customerDocumentNumber(),
                command.customerName(),
                getDetailedBillingItems(quote)
        );

        if (externalInvoiceIdOpt.isEmpty()) {
            return Result.failure(VoucherCommandFailure.FACTHUB_ISSUANCE_FAILED);
        }

        // 4. Create and save Voucher Aggregate
        try {
            var voucher = new Voucher(
                    command.quoteId(),
                    command.type(),
                    command.customerDocumentType(),
                    command.customerDocumentNumber(),
                    command.customerName(),
                    quote.getTotalAmount(),
                    externalInvoiceIdOpt.get()
            );

            var savedVoucher = voucherRepository.save(voucher);
            return Result.success(savedVoucher);
        } catch (IllegalArgumentException e) {
            return Result.failure(VoucherCommandFailure.INVALID_VOUCHER_DATA);
        }
    }

    @Override
    @Transactional
    public Result<Voucher, VoucherCommandFailure> handle(AddPaymentCommand command) {
        var voucherOpt = voucherRepository.findById(command.voucherId());
        if (voucherOpt.isEmpty()) {
            return Result.failure(VoucherCommandFailure.VOUCHER_NOT_FOUND);
        }

        var voucher = voucherOpt.get();
        var quoteOpt = quoteRepository.findById(voucher.getQuoteId());
        if (quoteOpt.isEmpty()) {
            return Result.failure(VoucherCommandFailure.QUOTE_NOT_FOUND);
        }
        var branchId = quoteOpt.get().getBranchId().value();

        try {
            voucher.addPayment(command.amount(), command.method(), branchId);
            var savedVoucher = voucherRepository.save(voucher);
            return Result.success(savedVoucher);
        } catch (IllegalStateException e) {
            if (e.getMessage().contains("already paid")) {
                return Result.failure(VoucherCommandFailure.VOUCHER_ALREADY_PAID);
            }
            if (e.getMessage().contains("canceled")) {
                return Result.failure(VoucherCommandFailure.VOUCHER_CANCELED);
            }
            if (e.getMessage().contains("exceeds")) {
                return Result.failure(VoucherCommandFailure.PAYMENT_EXCEEDS_TOTAL_DEBT);
            }
            return Result.failure(VoucherCommandFailure.INVALID_VOUCHER_DATA);
        } catch (IllegalArgumentException e) {
            return Result.failure(VoucherCommandFailure.INVALID_VOUCHER_DATA);
        }
    }

    @Override
    public Result<Voucher, VoucherCommandFailure> handle(com.forkdevs.driveos.platform.billing.domain.model.commands.RemovePaymentCommand command) {
        var voucherOpt = voucherRepository.findById(command.voucherId());
        
        if (voucherOpt.isEmpty()) {
            return Result.failure(VoucherCommandFailure.VOUCHER_NOT_FOUND);
        }

        var voucher = voucherOpt.get();

        try {
            voucher.removePayment(command.paymentId());
            var savedVoucher = voucherRepository.save(voucher);
            return Result.success(savedVoucher);
        } catch (IllegalArgumentException e) {
            return Result.failure(VoucherCommandFailure.PAYMENT_NOT_FOUND);
        } catch (IllegalStateException e) {
            return Result.failure(VoucherCommandFailure.VOUCHER_CANCELED);
        }
    }

    @Override
    @Transactional
    public Result<Voucher, VoucherCommandFailure> handle(com.forkdevs.driveos.platform.billing.domain.model.commands.ProcessCheckoutCommand command) {
        // 1. Get and validate Quote
        var quoteOpt = quoteRepository.findById(command.quoteId());
        if (quoteOpt.isEmpty()) {
            return Result.failure(VoucherCommandFailure.QUOTE_NOT_FOUND);
        }
        var quote = quoteOpt.get();
        if (quote.getStatus() != QuoteStatus.APPROVED) {
            return Result.failure(VoucherCommandFailure.QUOTE_NOT_APPROVED);
        }

        // 2. Query Issuer RUC via Outbound Gateway
        var issuerRucOpt = issuerQueryGateway.getIssuerTaxIdByBranchId(quote.getBranchId().value());
        if (issuerRucOpt.isEmpty()) {
            return Result.failure(VoucherCommandFailure.ISSUER_NOT_FOUND);
        }
        String issuerRuc = issuerRucOpt.get();

        // 3. Issue Voucher via Facthub
        var externalInvoiceIdOpt = facthubGateway.issueVoucher(
                issuerRuc,
                command.type(),
                command.customerDocumentType(),
                command.customerDocumentNumber(),
                command.customerName(),
                getDetailedBillingItems(quote)
        );

        if (externalInvoiceIdOpt.isEmpty()) {
            return Result.failure(VoucherCommandFailure.FACTHUB_ISSUANCE_FAILED);
        }

        // 4. Create Voucher Aggregate
        try {
            var voucher = new Voucher(
                    command.quoteId(),
                    command.type(),
                    command.customerDocumentType(),
                    command.customerDocumentNumber(),
                    command.customerName(),
                    quote.getTotalAmount(),
                    externalInvoiceIdOpt.get()
            );

            // 5. Add full payment to the Voucher
            voucher.addPayment(quote.getTotalAmount(), command.method(), quote.getBranchId().value());

            // 6. Save the fully paid Voucher
            var savedVoucher = voucherRepository.save(voucher);
            return Result.success(savedVoucher);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return Result.failure(VoucherCommandFailure.INVALID_VOUCHER_DATA);
        }
    }

    private List<FacthubGateway.FacthubItem> getDetailedBillingItems(com.forkdevs.driveos.platform.billing.domain.model.aggregates.Quote quote) {
        List<FacthubGateway.FacthubItem> items = new ArrayList<>();
        
        var workOrderOpt = workOrderQueryGateway.getWorkOrderSummary(quote.getWorkOrderId());
        if (workOrderOpt.isPresent()) {
            var workOrderSummary = workOrderOpt.get();
            for (var item : workOrderSummary.items()) {
                items.add(new FacthubGateway.FacthubItem(
                        item.description(),
                        item.quantity(),
                        item.unitPrice().amount()
                ));
            }
        }
        
        // Fallback to summary item if no items could be resolved
        if (items.isEmpty()) {
            items.add(new FacthubGateway.FacthubItem(
                    "Servicios de taller automotriz según orden " + quote.getWorkOrderId(),
                    1,
                    quote.getTotalAmount().amount()
            ));
        }
        
        return items;
    }
}
