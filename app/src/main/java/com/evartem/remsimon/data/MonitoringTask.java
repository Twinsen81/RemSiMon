package com.evartem.remsimon.data;

import java.util.concurrent.TimeUnit;

public interface MonitoringTask {

    String getDescription();
    void setDescription(String description);

    /* Returns a non-empty string when an important notification should be shown to the user,
     * otherwise returns an empty string. */
    default String getNotification()  {return "";}

    /* A call with active = true makes the task start issuing network requests on a separate worker thread.
     * With active = false - the thread is stopped, no requests are being issued. */
    void setActive(boolean active);

    boolean isActive();

    /* Returns current status of the monitoring task as text string, e.g. "PING OK. Last successful ping at 12:34, 24 Jan" */
    String getStatusText();


}
