package com.evartem.remsimon.data.types.base;

/**
 * Represents data that every task must provide upon completion
 */
public class TaskResult {

    /**
     * The time when the task completed successfully the first time (on creation or after a failure)
     */
    public long firstSuccessTime = 0;

    /**
     * The most recent time when the task completed successfully
     */
    public long lastSuccessTime = 0;

    public static final int NO_ERROR = 0;
    public static final int ERROR_INVALID_ADDRESS = 1;
    public static final int ERROR_TIMEOUT = 2;
    public static final int ERROR_IO = 3;

    /**
     * If the task failed, the reason is written here as a code
     */
    public int errorCode = NO_ERROR;

    /**
     * Detailed description of the failure
     */
    public String errorMessage = "";
}
