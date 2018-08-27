package com.evartem.remsimon.data;

public class PingingTask implements MonitoringTask {
    private long id;
    private String description = "";
    private volatile boolean active = false;

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        if (description != null)
            this.description = description;
        else
            this.description = "";
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String getStatusText() {
        return (active ? "Active: ": "Not active: ") + getDescription();
    }
}
