package com.evartem.remsimon.data.types.pinging;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static com.evartem.remsimon.data.types.pinging.PingingTaskResult.NO_ERROR;

public class JavaPinger implements Pinger {

    @Override
    public PingingTaskResult ping(PingingTaskSettings pingSettings) {
        InetAddress ia;

        try {
            ia = InetAddress.getByName(pingSettings.getPingAddress());
        } catch (UnknownHostException e) {
            return new PingingTaskResult(false, 0, PingingTaskResult.ERROR_INVALID_ADDRESS, "Invalid URL");
        }

        try {
            long startTime = System.nanoTime();
            final boolean reached = ia.isReachable(null, 0, pingSettings.getPingTimeoutMs());
            return new PingingTaskResult(reached,
                    Float.valueOf((System.nanoTime() - startTime) / 1e6f).longValue(),
                    !reached ? PingingTaskResult.ERROR_TIMEOUT : NO_ERROR,
                    !reached ? "Timed Out" : "");

        } catch (Exception e) {
            return new PingingTaskResult(false, 0, PingingTaskResult.ERROR_IO, "IOException: " + e.getMessage());
        }
    }
}
