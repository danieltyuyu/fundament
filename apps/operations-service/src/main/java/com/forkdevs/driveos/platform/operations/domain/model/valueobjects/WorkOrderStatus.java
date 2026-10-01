package com.forkdevs.driveos.platform.operations.domain.model.valueobjects;

/**
 * Enum representing the status of a Work Order, with defined transitions between states.
 * Each enum constant implements the canTransitionTo method to enforce these rules.
 * @author Joel Huamani Estefanero
 */
public enum WorkOrderStatus {
    PENDING {
        @Override
        public boolean canTransitionTo(WorkOrderStatus next) {
            return next == IN_PROGRESS;
        }
    },
    IN_PROGRESS {
        @Override
        public boolean canTransitionTo(WorkOrderStatus next) {
            return next == COMPLETED;
        }
    },
    COMPLETED {
        @Override
        public boolean canTransitionTo(WorkOrderStatus next) {
            return next == PAID || next == IN_PROGRESS;
        }
    },
    PAID {
        @Override
        public boolean canTransitionTo(WorkOrderStatus next) {
            return false;
        }
    };

    private static final String INVALID_TRANSITION_MESSAGE_WORK_ORDER = "operations.error.workOrderStatus.invalidTransition";

    /**
     * Determines if the current status can transition to the specified next status based on defined rules for each state.
     * @param next the next WorkOrderStatus to transition to
     * @return true if the transition is allowed, false otherwise
     */
    public abstract boolean canTransitionTo(WorkOrderStatus next);

    /**
     * Transitions to the specified next status if the transition is valid according to the canTransitionTo method. If the transition is invalid, an IllegalStateException is thrown with a specific error message.
     * @param next the next WorkOrderStatus to transition to if the transition is valid
     * @return the next WorkOrderStatus if the transition is valid
     */
    public WorkOrderStatus transitionTo(WorkOrderStatus next) {
        if (!canTransitionTo(next)) {
            throw new IllegalStateException(INVALID_TRANSITION_MESSAGE_WORK_ORDER);
        }
        return next;
    }
}
