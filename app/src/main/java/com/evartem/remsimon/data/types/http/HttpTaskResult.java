package com.evartem.remsimon.data.types.http;

import com.evartem.remsimon.data.types.base.TaskResult;
import com.google.common.collect.EvictingQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class HttpTaskResult extends TaskResult {
    public boolean responseOK = false; // Last request was successful?
    public List<String> responses = new ArrayList<>();
}
