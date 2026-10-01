package com.forkdevs.driveos.platform.iot.interfaces.rest.transform;

import com.forkdevs.driveos.platform.iot.application.commandservices.Obd2DeviceCommandFailure;
import com.forkdevs.driveos.platform.iot.domain.model.aggregates.Obd2Device;
import com.forkdevs.driveos.platform.shared.application.result.Result;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Assembler class responsible for transforming the Result of an OBD2 Device command execution into an appropriate ResponseEntity for REST API responses.
 */
public final class ResponseEntityFromObd2DeviceCommandResultAssembler {

    private ResponseEntityFromObd2DeviceCommandResultAssembler() {}

    /**
     * Transforms the Result of an OBD2 Device command execution into a ResponseEntity with a custom success status.
     * @param result The Result of the command execution
     * @param successStatus The HttpStatus to return on success
     * @param messageSource The MessageSource used to retrieve localized error messages
     * @return A ResponseEntity containing either the Obd2DeviceResource (on success) or a ProblemDetail with localized error message (on failure)
     */
    public static ResponseEntity<?> toResponseEntityFromResult(
            Result<Obd2Device, Obd2DeviceCommandFailure> result,
            HttpStatus successStatus,
            MessageSource messageSource) {

        return result.fold(
                obd2Device -> new ResponseEntity<>(
                        Obd2DeviceResourceFromAggregateAssembler.toResourceFromAggregate(obd2Device),
                        successStatus
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

    /**
     * Transforms the Result of an OBD2 Device command execution into a ResponseEntity.
     * @param result The Result of the command execution
     * @param messageSource The MessageSource used to retrieve localized error messages
     * @return A ResponseEntity containing either the Obd2DeviceResource (on success) or a ProblemDetail with localized error message (on failure)
     */
    public static ResponseEntity<?> toResponseEntityFromResult(
            Result<Obd2Device, Obd2DeviceCommandFailure> result,
            MessageSource messageSource) {
        return toResponseEntityFromResult(result, HttpStatus.CREATED, messageSource);
    }

    private static HttpStatus statusFromFailure(Obd2DeviceCommandFailure failure) {
        return switch (failure) {
            case Obd2DeviceCommandFailure.Duplicate(String _) -> HttpStatus.CONFLICT;
            case Obd2DeviceCommandFailure.NotFound(String _) -> HttpStatus.NOT_FOUND;
            case Obd2DeviceCommandFailure.InvalidState(String _) -> HttpStatus.BAD_REQUEST;
        };
    }

    private static String messageFromFailure(Obd2DeviceCommandFailure failure, MessageSource messageSource) {
        String messageKey = switch (failure) {
            case Obd2DeviceCommandFailure.Duplicate(String message) -> message;
            case Obd2DeviceCommandFailure.NotFound(String message) -> message;
            case Obd2DeviceCommandFailure.InvalidState(String message) -> message;
        };
        try {
            return messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            return messageKey;
        }
    }
}
