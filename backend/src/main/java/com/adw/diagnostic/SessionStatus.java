package com.adw.diagnostic;

/**
 * Lifecycle status of a diagnostic session. The full reasoning state machine
 * (INTAKE ... REPORTING) arrives in Fase 3; Fase 1 only opens and closes.
 */
public enum SessionStatus {
    OPEN,
    CLOSED
}
