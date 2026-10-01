package com.forkdevs.driveos.platform.operations.domain.model.valueobjects;

/**
 * Enum representing the status of a Work Order Task, with defined transitions between states.
 * The possible statuses are PENDING, DOING, and COMPLETED, each with specific rules for transitioning to the next status.
 * @author Joel Huamani Estefanero
 */
public enum WorkOrderTaskStatus {
    PENDING {
        @Override
        public boolean canTransitionTo(WorkOrderTaskStatus next) {
            return next == DOING;
        }
    },
    DOING {
        @Override
        public boolean canTransitionTo(WorkOrderTaskStatus next) {
            return next == COMPLETED;
        }
    },
    COMPLETED {
        @Override
        public boolean canTransitionTo(WorkOrderTaskStatus next) {
            return next == DOING;
        }
    };

    private static final String INVALID_TRANSITION_MESSAGE_WORK_ORDER_TASK = "operations.error.workOrderTaskStatus.invalidTransition";

    /**
     * Determines if the current status can transition to the specified next status based on defined rules for each status.
     * @param next the next WorkOrderTaskStatus to transition to other enum status
     * @return true if the transition is valid according to the rules defined for each status, false otherwise
     */
    public abstract boolean canTransitionTo(WorkOrderTaskStatus next);

    public WorkOrderTaskStatus transitionTo(WorkOrderTaskStatus next) {
        if (!canTransitionTo(next)) {
            throw new IllegalStateException(INVALID_TRANSITION_MESSAGE_WORK_ORDER_TASK);
        }
        return next;
    }
}
