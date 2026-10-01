package com.forkdevs.driveos.platform.billing.application.internal.queryservices;

import com.forkdevs.driveos.platform.billing.application.queryservices.VoucherQueryService;
import com.forkdevs.driveos.platform.billing.domain.model.aggregates.Voucher;
import com.forkdevs.driveos.platform.billing.domain.model.queries.GetVoucherByIdQuery;
import com.forkdevs.driveos.platform.billing.domain.repositories.VoucherRepository;
import com.forkdevs.driveos.platform.billing.domain.model.queries.GetVouchersByBranchIdQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List;

/**
 * Implementation of the VoucherQueryService interface.
 * Exposes methods to retrieve vouchers by their ID or filter them by their branch ID.
 * Queries are executed in a read-only transactional context to optimize performance.
 */
@Service
public class VoucherQueryServiceImpl implements VoucherQueryService {

    private final VoucherRepository voucherRepository;

    public VoucherQueryServiceImpl(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    /**
     * Retrieves a specific Voucher by its unique identifier.
     * 
     * @param query The query object containing the voucher ID to search for.
     * @return An Optional containing the Voucher if found, or empty otherwise.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Voucher> handle(GetVoucherByIdQuery query) {
        return voucherRepository.findById(query.voucherId());
    }

    /**
     * Retrieves a list of all Vouchers emitted by a specific branch.
     * 
     * @param query The query object containing the target branch ID.
     * @return A list of Vouchers associated with the branch.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Voucher> handle(GetVouchersByBranchIdQuery query) {
        return voucherRepository.findByBranchId(query.branchId());
    }
}
