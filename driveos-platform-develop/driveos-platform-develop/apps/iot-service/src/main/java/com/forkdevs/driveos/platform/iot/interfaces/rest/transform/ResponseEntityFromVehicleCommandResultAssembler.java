package com.forkdevs.driveos.platform.iot.interfaces.rest.transform;

import com.forkdevs.driveos.platform.iot.application.commandservices.VehicleCommandFailure;
import com.forkdevs.driveos.platform.iot.domain.model.aggregates.Vehicle;
import com.forkdevs.driveos.platform.iot.domain.model.aggregates.VehicleRegistration;
import com.forkdevs.driveos.platform.shared.application.result.Result;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

/**
 * Assembler class responsible for transforming the Result of a Vehicle command execution into an appropriate ResponseEntity for REST API responses.
 */
public final class ResponseEntityFromVehicleCommandResultAssembler {

    private ResponseEntityFromVehicleCommandResultAssembler() {}

    /**
     * Transforms the Result of a Vehicle command execution into a ResponseEntity with a custom success status.
     * @param result The Result of the command execution
     * @param successStatus The HttpStatus to return on success
     * @param messageSource The MessageSource used to retrieve localized error messages
     * @return A ResponseEntity containing either the VehicleRegistrationResource (on success) or a ProblemDetail with localized error message (on failure)
     */
    public static ResponseEntity<?> toResponseEntityFromRegistrationResult(
            Result<VehicleRegistration, VehicleCommandFailure> result,
            HttpStatus successStatus,
            MessageSource messageSource) {

        return result.fold(
                vehicleReg -> new ResponseEntity<>(
                        VehicleRegistrationResourceFromAggregateAssembler.toResourceFromAggregate(vehicleReg),
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
     * Transforms the Result of a Vehicle command execution into a ResponseEntity.
     * @param result The Result of the command execution
     * @param messageSource The MessageSource used to retrieve localized error messages
     * @return A ResponseEntity containing either the VehicleRegistrationResource (on success) or a ProblemDetail with localized error message (on failure)
     */
    public static ResponseEntity<?> toResponseEntityFromRegistrationResult(
            Result<VehicleRegistration, VehicleCommandFailure> result,
            MessageSource messageSource) {
        return toResponseEntityFromRegistrationResult(result, HttpStatus.CREATED, messageSource);
    }

    /**
     * Transforms the Result of a Vehicle update command execution into a ResponseEntity with a custom success status.
     * @param result The Result of the command execution
     * @param successStatus The HttpStatus to return on success
     * @param messageSource The MessageSource used to retrieve localized error messages
     * @return A ResponseEntity containing either the VehicleResource (on success) or a ProblemDetail with localized error message (on failure)
     */
    public static ResponseEntity<?> toResponseEntityFromVehicleResult(
            Result<Vehicle, VehicleCommandFailure> result,
            HttpStatus successStatus,
            MessageSource messageSource) {

        return result.fold(
                vehicle -> new ResponseEntity<>(
                        VehicleResourceFromAggregateAssembler.toResourceFromAggregate(vehicle),
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
     * Transforms the Result of a Vehicle update command execution into a ResponseEntity.
     * @param result The Result of the command execution
     * @param messageSource The MessageSource used to retrieve localized error messages
     * @return A ResponseEntity containing either the VehicleResource (on success) or a ProblemDetail with localized error message (on failure)
     */
    public static ResponseEntity<?> toResponseEntityFromVehicleResult(
            Result<Vehicle, VehicleCommandFailure> result,
            MessageSource messageSource) {
        return toResponseEntityFromVehicleResult(result, HttpStatus.OK, messageSource);
    }

    /**
     * Transforms the Result of a Vehicle command deletion (Void) execution into a ResponseEntity.
     * @param result The Result of the command execution
     * @param messageSource The MessageSource used to retrieve localized error messages
     * @return A ResponseEntity with 204 No Content (on success) or a ProblemDetail (on failure)
     */
    public static ResponseEntity<?> toResponseEntityFromVoidResult(
            Result<Void, VehicleCommandFailure> result,
            MessageSource messageSource) {

        return result.fold(
                _void -> ResponseEntity.noContent().build(),
                failure -> {
                    HttpStatus status = statusFromFailure(failure);
                    String localizedMessage = messageFromFailure(failure, messageSource);
                    return ResponseEntity.status(status).body(
                            ProblemDetail.forStatusAndDetail(status, localizedMessage)
                    );
                }
        );
    }

    private static HttpStatus statusFromFailure(VehicleCommandFailure failure) {
        return switch (failure) {
            case VehicleCommandFailure.Duplicate(String _) -> HttpStatus.CONFLICT;
            case VehicleCommandFailure.NotFound(String _) -> HttpStatus.NOT_FOUND;
            case VehicleCommandFailure.InvalidState(String _) -> HttpStatus.BAD_REQUEST;
        };
    }

    private static String messageFromFailure(VehicleCommandFailure failure, MessageSource messageSource) {
        String messageKey = switch (failure) {
            case VehicleCommandFailure.Duplicate(String message) -> message;
            case VehicleCommandFailure.NotFound(String message) -> message;
            case VehicleCommandFailure.InvalidState(String message) -> message;
        };
        try {
            return messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            return messageKey;
        }
    }
}
