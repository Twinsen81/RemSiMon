package com.evartem.remsimon.data.types.http;

import com.evartem.remsimon.data.types.base.TaskResult;

import java.util.ArrayList;
import java.util.List;

public class HttpTaskResult extends TaskResult {
    public boolean responseOK = false; // Last request was successful?
    public List<String> responses = new ArrayList<>();

    public HttpTaskResult(boolean responseOK, List<String> responses) {
        this.responseOK = responseOK;
        this.responses = responses;
    }

    public HttpTaskResult(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
