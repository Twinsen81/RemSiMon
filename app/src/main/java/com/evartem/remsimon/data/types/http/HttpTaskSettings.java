package com.evartem.remsimon.data.types.http;

/**
 * Defines various settings for the http task type.
 * The getters and setters are synchronized since the UI will
 * write settings from the UI thread, but the task will read them from
 * the worker thread
 */
public class HttpTaskSettings {
    private String httpAddress = "https://api.github.com/orgs/square/repos?page=1&per_page=1";
    private String displayLayout = "default"; // Defines how the received data is parsed and displayed
    private int historyDepth = 5; // How many last http responses (i.e. the extracted fields) will be saved to provide the trend of changing values
    private String fields = ""; // Comma separated names of fields to extract from the response

    public synchronized String getFields() { return fields; }

    public synchronized void setFields(String fields) { this.fields = fields; }

    public synchronized int getHistoryDepth() {
        return historyDepth;
    }

    public synchronized void setHistoryDepth(int historyDepth) {
        this.historyDepth = historyDepth;
    }

    public synchronized String getDisplayLayout() {
        return displayLayout;
    }

    public synchronized void setDisplayLayout(String displayLayout) {
        this.displayLayout = displayLayout;
    }

    public synchronized String getHttpAddress() {
        return httpAddress;
    }

    public synchronized void setHttpAddress(String httpAddress) {
        this.httpAddress = httpAddress;
    }


    public synchronized HttpTaskSettings clone() {
        HttpTaskSettings clone = new HttpTaskSettings();
        clone.setHttpAddress(getHttpAddress());
        clone.setDisplayLayout(getDisplayLayout());
        clone.setHistoryDepth(getHistoryDepth());
        clone.setFields(getFields());
        return clone;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof HttpTaskSettings &&
                httpAddress.equals(((HttpTaskSettings) obj).httpAddress) &&
                httpAddress.equals(((HttpTaskSettings) obj).getHttpAddress()) &&
                displayLayout.equals(((HttpTaskSettings) obj).getDisplayLayout()) &&
                historyDepth == ((HttpTaskSettings) obj).historyDepth &&
                fields.equals(((HttpTaskSettings) obj).getFields());
    }
}
