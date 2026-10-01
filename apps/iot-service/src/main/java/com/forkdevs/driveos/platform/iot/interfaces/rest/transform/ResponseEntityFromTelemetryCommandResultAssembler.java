package com.forkdevs.driveos.platform.iot.interfaces.rest.transform;

import com.forkdevs.driveos.platform.iot.application.commandservices.TelemetryCommandFailure;
import com.forkdevs.driveos.platform.iot.domain.model.aggregates.TelemetrySnapshot;
import com.forkdevs.driveos.platform.shared.application.result.Result;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.util.List;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Assembler class responsible for transforming the Result of a Telemetry command execution
 * into an appropriate ResponseEntity for REST API responses.
 */
public final class ResponseEntityFromTelemetryCommandResultAssembler {

    private ResponseEntityFromTelemetryCommandResultAssembler() {}

    /**
     * Transforms the Result of a Telemetry command execution into a ResponseEntity.
     * @param result The Result of the command execution
     * @param messageSource The MessageSource used to retrieve localized error messages
     * @return A ResponseEntity containing either the list of TelemetrySnapshotResource (on success) or a ProblemDetail with localized error message (on failure)
     */
    public static ResponseEntity<?> toResponseEntityFromResult(
            Result<List<TelemetrySnapshot>, TelemetryCommandFailure> result,
            MessageSource messageSource) {

        return result.fold(
                snapshots -> new ResponseEntity<>(
                        snapshots.stream()
                                .map(TelemetrySnapshotResourceFromAggregateAssembler::toResourceFromAggregate)
                                .toList(),
                        HttpStatus.CREATED
                ),
                failure -> {
                    HttpStatus status = statusFromFailure(failure);
                    String localizedMessage = messageFromFailure(failure, messageSource);
                    return ResponseEntity.status(status).body(
                            ProblemDetail.forStatusAndDetail(status, localizedMessage)
                    );
                }
        );
    }

    private static HttpStatus statusFromFailure(TelemetryCommandFailure failure) {
        return switch (failure) {
            case TelemetryCommandFailure.NotFound(String _) -> HttpStatus.NOT_FOUND;
            case TelemetryCommandFailure.InvalidState(String _) -> HttpStatus.BAD_REQUEST;
        };
    }

    private static String messageFromFailure(TelemetryCommandFailure failure, MessageSource messageSource) {
        String messageKey = switch (failure) {
            case TelemetryCommandFailure.NotFound(String message) -> message;
            case TelemetryCommandFailure.InvalidState(String message) -> message;
        };
        try {
            return messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            return messageKey;
        }
    }
}
