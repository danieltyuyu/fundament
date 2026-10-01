package com.forkdevs.driveos.platform.billing.application.internal.commandservices;

import com.forkdevs.driveos.platform.billing.application.commandservices.QuoteCommandService;
import com.forkdevs.driveos.platform.billing.application.outboundservices.WorkOrderQueryGateway;
import com.forkdevs.driveos.platform.billing.domain.model.aggregates.Quote;
import com.forkdevs.driveos.platform.billing.domain.model.commands.CreateQuoteCommand;
import com.forkdevs.driveos.platform.billing.domain.model.valueobjects.QuoteCommandFailure;
import com.forkdevs.driveos.platform.billing.domain.repositories.QuoteRepository;
import com.forkdevs.driveos.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

/**
 * Implementation of the QuoteCommandService interface.
 * Handles the business use cases for Quote operations, acting as an orchestrator 
 * between domain aggregates, outbound gateways, and persistence repositories.
 */
@Service
public class QuoteCommandServiceImpl implements QuoteCommandService {

    private final QuoteRepository quoteRepository;
    private final WorkOrderQueryGateway workOrderQueryGateway;

    public QuoteCommandServiceImpl(QuoteRepository quoteRepository, WorkOrderQueryGateway workOrderQueryGateway) {
        this.quoteRepository = quoteRepository;
        this.workOrderQueryGateway = workOrderQueryGateway;
    }

    @Override
    public Result<Quote, QuoteCommandFailure> handle(CreateQuoteCommand command) {
        var workOrderOpt = workOrderQueryGateway.getWorkOrderSummary(command.workOrderId());

        if (workOrderOpt.isEmpty()) {
            return Result.failure(QuoteCommandFailure.WORK_ORDER_NOT_FOUND);
        }

        var workOrderSummary = workOrderOpt.get();
        
        try {
            var quote = new Quote(command, workOrderSummary.totalAmount());
            var savedQuote = quoteRepository.save(quote);
            return Result.success(savedQuote);
        } catch (IllegalArgumentException e) {
            return Result.failure(QuoteCommandFailure.INVALID_QUOTE_DATA);
        }
    }

    @Override
    public Result<Quote, QuoteCommandFailure> handle(com.forkdevs.driveos.platform.billing.domain.model.commands.UpdateQuoteDiscountCommand command) {
        var quoteOpt = quoteRepository.findById(command.quoteId());
        
        if (quoteOpt.isEmpty()) {
            return Result.failure(QuoteCommandFailure.QUOTE_NOT_FOUND);
        }

        var quote = quoteOpt.get();

        try {
            quote.updateDiscount(command.discountPercentage());
            var savedQuote = quoteRepository.save(quote);
            return Result.success(savedQuote);
        } catch (IllegalStateException e) {
            return Result.failure(QuoteCommandFailure.INVALID_QUOTE_STATE);
        } catch (IllegalArgumentException e) {
            return Result.failure(QuoteCommandFailure.INVALID_QUOTE_DATA);
        }
    }

    @Override
    public Result<Quote, QuoteCommandFailure> handle(com.forkdevs.driveos.platform.billing.domain.model.commands.ApproveQuoteCommand command) {
        var quoteOpt = quoteRepository.findById(command.quoteId());
        
        if (quoteOpt.isEmpty()) {
            return Result.failure(QuoteCommandFailure.QUOTE_NOT_FOUND);
        }

        var quote = quoteOpt.get();

        try {
            quote.approve();
            var savedQuote = quoteRepository.save(quote);
            return Result.success(savedQuote);
        } catch (IllegalStateException e) {
            return Result.failure(QuoteCommandFailure.INVALID_QUOTE_STATE);
        }
    }

    @Override
    public Result<Quote, QuoteCommandFailure> handle(com.forkdevs.driveos.platform.billing.domain.model.commands.CancelQuoteCommand command) {
        var quoteOpt = quoteRepository.findById(command.quoteId());
        
        if (quoteOpt.isEmpty()) {
            return Result.failure(QuoteCommandFailure.QUOTE_NOT_FOUND);
        }

        var quote = quoteOpt.get();

        try {
            quote.cancel();
            var savedQuote = quoteRepository.save(quote);
            return Result.success(savedQuote);
        } catch (IllegalStateException e) {
            return Result.failure(QuoteCommandFailure.INVALID_QUOTE_STATE);
        }
    }
}
