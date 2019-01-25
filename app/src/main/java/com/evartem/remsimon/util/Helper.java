package com.evartem.remsimon.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.Pair;

import com.evartem.remsimon.R;
import com.evartem.remsimon.data.types.base.TaskType;

import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Helper {

    private static PeriodFormatter periodFormatter = new PeriodFormatterBuilder()
            .appendDays()
            .appendSuffix(" d ")
            .appendHours()
            .appendSuffix(" h ")
            .appendMinutes()
            .appendSuffix(" m ")
            .appendSeconds()
            .appendSuffix(" s ")
            .toFormatter();

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd MMMM HH:mm:ss");

    public static  String formatPeriod(long periodMs, Resources resources) {
        if (periodMs > 94608000000L) return resources.getString(R.string.neverup);
        return periodFormatter.print(new Period(periodMs));
    }

    public static String formatDateTime(long dateTimeMs, Resources resources) {
        if (dateTimeMs == 0) return resources.getString(R.string.never);
        return dateTimeFormatter.print(dateTimeMs);
    }

    private static final Map<String, Integer> uiTaskTypeNames = new HashMap<>();

    static
    {
        uiTaskTypeNames.put(TaskType.PINGING, R.string.taskType_Pinging);
        uiTaskTypeNames.put(TaskType.HTTP, R.string.taskType_Http);
    }

    /**
     *  Returns types of tasks available for creation.
     * @param context The context to access the resources
     * @return The list of pairs: internal type name - UI type name
     */
    public static Pair<List<String>, List<String>> getAllTypes(Context context) {
        Pair<List<String>, List<String>> names = new Pair<>(new ArrayList<>(), new ArrayList<>());
        for (Map.Entry<String, Integer> name: uiTaskTypeNames.entrySet()) {
            names.first.add(name.getKey());
            names.second.add(context.getResources().getString(name.getValue()));
        }
        return names;
    }

}
