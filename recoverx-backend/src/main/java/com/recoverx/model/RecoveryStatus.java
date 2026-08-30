package com.recoverx.model;

public enum RecoveryStatus {
    /** Originally-successful payments never enter the recovery pipeline. */
    NOT_APPLICABLE,
    /** Failed payment, not yet processed by the recovery engine. */
    PENDING,
    /** Recovery action succeeded - money recovered. */
    RECOVERED,
    /** A non-retry action (e.g. asking the customer to update a card) did not resolve. */
    UNRECOVERED,
    /** Safety policy stopped execution (retry limit, low confidence) - routed to a human. */
    ESCALATED
}
