package com.evartem.remsimon.data.types.pinging;

/**
 * An interface for PING implementations
 */
public interface Pinger {
    /**
     * Pings the address provided in the param
     * @param pingSettings The address to ping and the timeout (ms) - max time to wait for PING command response
     * @return
     */
    PingingTaskResult ping(PingingTaskSettings pingSettings);
}
