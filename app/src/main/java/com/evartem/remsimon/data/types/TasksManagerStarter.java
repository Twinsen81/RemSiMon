package com.evartem.remsimon.data.types;

import com.evartem.remsimon.data.TasksManager;

/**
 * An interface to start the tasks manager once when the app starts.
 * Separates starting the manager from using it for handling tasks.
 */
public interface TasksManagerStarter {

    void startManager();
    TasksManager getManager();

}
