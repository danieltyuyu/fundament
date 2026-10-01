package com.forkdevs.driveos.platform.billing.interfaces.rest;

import com.forkdevs.driveos.platform.billing.domain.model.valueobjects.QuoteCommandFailure;
import com.forkdevs.driveos.platform.billing.application.commandservices.QuoteCommandService;
import com.forkdevs.driveos.platform.billing.interfaces.rest.resources.CreateQuoteResource;
import com.forkdevs.driveos.platform.billing.interfaces.rest.resources.UpdateQuoteResource;
import com.forkdevs.driveos.platform.billing.interfaces.rest.transform.CreateQuoteCommandFromResourceAssembler;
import com.forkdevs.driveos.platform.billing.interfaces.rest.transform.UpdateQuoteCommandFromResourceAssembler;
import com.forkdevs.driveos.platform.billing.interfaces.rest.transform.QuoteResourceFromAggregateAssembler;
import com.forkdevs.driveos.platform.billing.application.queryservices.QuoteQueryService;
import com.forkdevs.driveos.platform.billing.domain.model.queries.GetQuoteByIdQuery;
import com.forkdevs.driveos.platform.billing.domain.model.commands.ApproveQuoteCommand;
import com.forkdevs.driveos.platform.billing.domain.model.commands.CancelQuoteCommand;
import com.forkdevs.driveos.platform.billing.domain.model.queries.GetQuotesByBranchIdQuery;
import com.forkdevs.driveos.platform.shared.domain.model.valueobjects.BranchId;
import com.forkdevs.driveos.platform.billing.interfaces.rest.resources.QuoteResource;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing billing quotes.
 * This controller provides endpoints for creating and interacting with quotes.
 * It delegates command handling to the QuoteCommandService.
 */
@RestController
@RequestMapping(value = "/api/v1/quotes", produces = "application/json")
@Tag(name = "Quotes", description = "Endpoints for managing billing quotes")
public class QuotesController {

    private final QuoteCommandService commandService;
    private final QuoteQueryService queryService;
    private final org.springframework.context.MessageSource messageSource;

    public QuotesController(QuoteCommandService commandService, QuoteQueryService queryService, org.springframework.context.MessageSource messageSource) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.messageSource = messageSource;
    }

    /**
     * Handles the creation of a new quote.
     * 
     * @param resource The resource payload containing quote creation details.
     * @return A ResponseEntity with the created quote resource and a 201 CREATED status, 
     *         or an appropriate error status based on the business failure.
     */
    @PostMapping
    @Operation(summary = "Create a new quote", description = "Creates a new Quote based on a Work Order")
    public ResponseEntity<?> createQuote(@Valid @RequestBody CreateQuoteResource resource) {
        var command = CreateQuoteCommandFromResourceAssembler.toCommandFromResource(resource);
        var result = commandService.handle(command);
        if (result.isSuccess()) {
            var quoteResource = QuoteResourceFromAggregateAssembler.toResourceFromAggregate(result.success().get());
            return new ResponseEntity<>(quoteResource, HttpStatus.CREATED);
        }
        return toErrorResponse(result.failure().get());
    }

    /**
     * Handles the update of a quote's discount percentage.
     * 
     * @param id The unique identifier of the quote to update.
     * @param resource The resource payload containing the new discount percentage.
     * @return A ResponseEntity with the updated quote resource and a 200 OK status, 
     *         or an appropriate error status based on the business failure.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update quote discount", description = "Updates the discount percentage of an existing DRAFT quote")
    public ResponseEntity<?> updateQuoteDiscount(@PathVariable UUID id, @Valid @RequestBody UpdateQuoteResource resource) {
        var command = UpdateQuoteCommandFromResourceAssembler.toCommandFromResource(id, resource);
        var result = commandService.handle(command);
        if (result.isSuccess()) {
            var quoteResource = QuoteResourceFromAggregateAssembler.toResourceFromAggregate(result.success().get());
            return ResponseEntity.ok(quoteResource);
        }
        return toErrorResponse(result.failure().get());
    }

    /**
     * Handles the approval of an existing DRAFT quote.
     * 
     * @param id The unique identifier of the quote to approve.
     * @return A ResponseEntity with the approved quote resource and a 200 OK status, 
     *         or an appropriate error status based on the business failure.
     */
    @PostMapping("/{id}/approvals")
    @Operation(summary = "Approve a quote", description = "Approves a Quote, transitioning its state from DRAFT to APPROVED")
    public ResponseEntity<?> approveQuote(@PathVariable UUID id) {
        var command = new ApproveQuoteCommand(id);
        var result = commandService.handle(command);
        if (result.isSuccess()) {
            var quoteResource = QuoteResourceFromAggregateAssembler.toResourceFromAggregate(result.success().get());
            return ResponseEntity.ok(quoteResource);
        }
        return toErrorResponse(result.failure().get());
    }

    /**
     * Handles the cancellation of an existing quote.
     * 
     * @param id The unique identifier of the quote to cancel.
     * @return A ResponseEntity with the canceled quote resource and a 200 OK status, 
     *         or an appropriate error status based on the business failure.
     */
    @PostMapping("/{id}/cancellations")
    @Operation(summary = "Cancel a quote", description = "Cancels a Quote, transitioning its state to CANCELED")
    public ResponseEntity<?> cancelQuote(@PathVariable UUID id) {
        var command = new CancelQuoteCommand(id);
        var result = commandService.handle(command);
        if (result.isSuccess()) {
            var quoteResource = QuoteResourceFromAggregateAssembler.toResourceFromAggregate(result.success().get());
            return ResponseEntity.ok(quoteResource);
        }
        return toErrorResponse(result.failure().get());
    }

    /**
     * Handles the retrieval of a quote by its ID.
     * 
     * @param id The unique identifier of the quote.
     * @return A ResponseEntity with the quote resource and a 200 OK status, 
     *         or a 404 NOT FOUND status if the quote does not exist.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get quote by ID", description = "Retrieves a Quote by its unique identifier")
    public ResponseEntity<QuoteResource> getQuoteById(@PathVariable UUID id) {
        var query = new GetQuoteByIdQuery(id);
        var quote = queryService.handle(query);
        if (quote.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        var resource = QuoteResourceFromAggregateAssembler.toResourceFromAggregate(quote.get());
        return ResponseEntity.ok(resource);
    }

    /**
     * Handles the retrieval of all quotes associated with a specific branch.
     * 
     * @param branchId The unique identifier of the branch.
     * @return A ResponseEntity containing a list of quote resources and a 200 OK status.
     */
    @GetMapping(params = "branchId")
    @Operation(summary = "Get quotes by branch ID", description = "Retrieves all Quotes belonging to a specific branch")
    public ResponseEntity<List<QuoteResource>> getQuotesByBranchId(@RequestParam UUID branchId) {
        var query = new GetQuotesByBranchIdQuery(new BranchId(branchId));
        var quotes = queryService.handle(query);
        var resources = quotes.stream()
                .map(QuoteResourceFromAggregateAssembler::toResourceFromAggregate)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resources);
    }

    private ResponseEntity<?> toErrorResponse(QuoteCommandFailure failure) {
        return switch (failure) {
            case WORK_ORDER_NOT_FOUND -> {
                String message = messageSource.getMessage("billing.error.quote.workOrderNotFound", null, org.springframework.context.i18n.LocaleContextHolder.getLocale());
                yield com.forkdevs.driveos.platform.shared.interfaces.rest.transform.ErrorResponseAssembler.toErrorResponseFromApplicationError(com.forkdevs.driveos.platform.shared.application.result.ApplicationError.notFound("quote", message));
            }
            case INVALID_QUOTE_DATA -> {
                String message = messageSource.getMessage("billing.error.quote.invalidData", null, org.springframework.context.i18n.LocaleContextHolder.getLocale());
                yield com.forkdevs.driveos.platform.shared.interfaces.rest.transform.ErrorResponseAssembler.toErrorResponseFromApplicationError(com.forkdevs.driveos.platform.shared.application.result.ApplicationError.validationError("quote", message));
            }
            case QUOTE_ALREADY_EXISTS_FOR_WORK_ORDER -> {
                String message = messageSource.getMessage("billing.error.quote.alreadyExistsForWorkOrder", null, org.springframework.context.i18n.LocaleContextHolder.getLocale());
                yield com.forkdevs.driveos.platform.shared.interfaces.rest.transform.ErrorResponseAssembler.toErrorResponseFromApplicationError(com.forkdevs.driveos.platform.shared.application.result.ApplicationError.conflict("quote", message));
            }
            case QUOTE_NOT_FOUND -> {
                String message = messageSource.getMessage("billing.error.quote.notFound", null, org.springframework.context.i18n.LocaleContextHolder.getLocale());
                yield com.forkdevs.driveos.platform.shared.interfaces.rest.transform.ErrorResponseAssembler.toErrorResponseFromApplicationError(com.forkdevs.driveos.platform.shared.application.result.ApplicationError.notFound("quote", message));
            }
            case INVALID_QUOTE_STATE -> {
                String message = messageSource.getMessage("billing.error.quote.invalidStateForUpdate", null, org.springframework.context.i18n.LocaleContextHolder.getLocale());
                yield com.forkdevs.driveos.platform.shared.interfaces.rest.transform.ErrorResponseAssembler.toErrorResponseFromApplicationError(com.forkdevs.driveos.platform.shared.application.result.ApplicationError.conflict("quote", message));
            }
        };
    }
}
