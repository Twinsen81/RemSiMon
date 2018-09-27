package com.evartem.remsimon.data.types.pinging;

public interface Pinger {
    PingingTaskResult ping(PingingTaskSettings pingSettings);
}
