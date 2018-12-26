package com.evartem.remsimon.data.util;


import android.support.annotation.Nullable;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import timber.log.Timber;

/**
 * A simple Timber tree that prints to the standard output streams
 * It's useful in JUnit tests where the Android's Log class is not available
 */
public final class StandardOutputLoggingTree extends Timber.Tree {
    @Override
    protected void log(int priority, @org.jetbrains.annotations.Nullable String tag, @NotNull String message, @Nullable Throwable t) {
        String message2Print = message;

        if (tag != null && tag.length() > 0)
            message2Print = tag + ": " + message2Print;

        if (t != null)
            message2Print += "\r\n" + t.toString();

        if (priority >= Log.WARN)
            System.err.println(message2Print);
        else
            System.out.println(message2Print);
    }
}
