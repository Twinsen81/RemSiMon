package com.evartem.remsimon.data;


/**
 * A common interface that each task of the app must implement
 */
public interface MonitoringTask {

    String getDescription();

    void setDescription(String description);

    /**
     * Returns a non-empty string when an important notification should be shown to the user
     *
     * @return An important notification to show to the user, if no notification available at the moment - empty string
     */
    default String getNotification() {
        return "";
    }

    /**
     * Starts or stops the task's work
     *
     * @param active true = makes the task start issuing network requests on a separate worker thread,
     *               false = the thread is stopped, no requests are being issued
     */
    void setActive(boolean active);

    boolean isActive();

    /**
     * Returns current status of the monitoring task as text string
     *
     * @return A short description of the current status of the task, e.g. "PING OK. Last successful ping at 12:34, 24 Jan"
     */
    String getStatusText();

    /**
     * @return The type of this task as defined in {@link TaskType}
     */
    String getType();


    String getId();
}
