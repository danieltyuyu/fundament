package com.forkdevs.driveos.platform.shared.application.result;

import java.util.Optional;
import java.util.function.Function;

/**
 * A sealed interface representing the result of an operation that can either be a success or a failure. It provides methods to create success and failure instances, check the type of result, and extract the success value or failure error. The fold method allows for handling both cases in a functional style.
 * @param <T> the type of the success value
 * @param <E> the type of the failure error
 */
public sealed interface Result<T, E> permits Result.Success, Result.Failure {

    /**
     * Creates a new success result with the given value.
     * @param value the value of the success result
     * @return a new success result containing the given value
     * @param <T> the type of the success value
     * @param <E> the type of the failure error
     */
    static <T, E> Result<T, E> success(T value) {
        return new Success<>(value);
    }

    /**
     * Creates a new failure result with the given error.
     * @param error the error of the failure result
     * @return a new failure result containing the given error
     * @param <T> the type of the success value
     * @param <E> the type of the failure error
     */
    static <T, E> Result<T, E> failure(E error) {
        return new Failure<>(error);
    }

    /**
     * Checks if the result is a success. Returns true if this result is an instance of Success, and false otherwise.
     * @return true if this result is a success, false otherwise
     */
    default boolean isSuccess() {
        return this instanceof Success<?, ?>;
    }

    /**
     * Checks if the result is a failure. Returns true if this result is an instance of Failure, and false otherwise.
     * @return true if this result is a failure, false otherwise
     */
    default boolean isFailure() {
        return this instanceof Failure<?, ?>;
    }

    /**
     * Extracts the success value from this result if it is a success, and returns an empty Optional if it is a failure. If this result is an instance of Success, it casts it to Success<T, E> and retrieves the value, returning it wrapped in an Optional. If this result is not a success, it returns Optional.empty().
     * @return an Optional containing the success value if this result is a success, or an empty Optional if this result is a failure
     */
    default Optional<T> success() {
        if (this instanceof Success<?, ?> success) {
            @SuppressWarnings("unchecked")
            T value = ((Success<T, E>) success).value();
            return Optional.of(value);
        }
        return Optional.empty();
    }

    /**
     * Extracts the failure error from this result if it is a failure, and returns an empty Optional if it is a success. If this result is an instance of Failure, it casts it to Failure<T, E> and retrieves the error, returning it wrapped in an Optional. If this result is not a failure, it returns Optional.empty().
     * @return an Optional containing the failure error if this result is a failure, or an empty Optional if this result is a success
     */
    default Optional<E> failure() {
        if (this instanceof Failure<?, ?> failure) {
            @SuppressWarnings("unchecked")
            E error = ((Failure<T, E>) failure).error();
            return Optional.of(error);
        }
        return Optional.empty();
    }

    /**
     * Applies the given functions to this result, depending on whether it is a success or a failure. If this result is a success, it applies the onSuccess function to the success value and returns the result. If this result is a failure, it applies the onFailure function to the failure error and returns the result. This method allows for handling both cases in a functional style, without needing to check the type of result explicitly.
     * @param onSuccess the function to apply if this result is a success, which takes the success value as input and returns a result of type R
     * @param onFailure the function to apply if this result is a failure, which takes the failure error as input and returns a result of type R
     * @return the result of applying the appropriate function to this result, depending on whether it is a success or a failure
     * @param <R> the type of the result returned by the onSuccess and onFailure functions
     */
    default <R> R fold(Function<? super T, ? extends R> onSuccess, Function<? super E, ? extends R> onFailure) {
        if (this instanceof Success<?, ?> success) {
            @SuppressWarnings("unchecked")
            T value = ((Success<T, E>) success).value();
            return onSuccess.apply(value);
        }
        E error = ((Failure<T, E>) this).error();
        return onFailure.apply(error);
    }

    /**
     * A record representing a successful result, containing a value of type T. This record implements the Result interface, allowing it to be used as a success case in the Result type. The value field holds the success value, and can be accessed using the value() method generated by the record.
     * @param value the value of the successful result
     * @param <T> the type of the success value
     * @param <E> the type of the failure error
     */
    record Success<T, E>(T value) implements Result<T, E> {
    }

    /**
     * A record representing a failed result, containing an error of type E. This record implements the Result interface, allowing it to be used as a failure case in the Result type. The error field holds the failure error, and can be accessed using the error() method generated by the record.
     * @param error the error of the failed result
     * @param <T> the type of the success value
     * @param <E> the type of the failure error
     */
    record Failure<T, E>(E error) implements Result<T, E> {
    }

}
