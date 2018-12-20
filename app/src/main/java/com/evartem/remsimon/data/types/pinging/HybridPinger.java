package com.evartem.remsimon.data.types.pinging;

import android.util.Patterns;

import com.stealthcopter.networktools.Ping;
import com.stealthcopter.networktools.ping.PingResult;

import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

public class HybridPinger implements Pinger {

    @Inject
    public HybridPinger() { }

    @Override

    public PingingTaskResult ping(PingingTaskSettings pingSettings) {

        if (!isValidUrl(pingSettings.getPingAddress()))
            return new PingingTaskResult(PingingTaskResult.ERROR_INVALID_ADDRESS, "Not a valid URL");

        PingResult pingResult;
        try {
            pingResult = Ping.onAddress(pingSettings.getPingAddress()).setTimeOutMillis(pingSettings.getPingTimeoutMs()).doPing();
        } catch (UnknownHostException e) {
            return new PingingTaskResult(PingingTaskResult.ERROR_INVALID_ADDRESS, "Unknown host");
        }

        return new PingingTaskResult(pingResult.isReachable,
                Float.valueOf(pingResult.timeTaken * 1000).longValue(),
                pingResult.isReachable ? 0 : PingingTaskResult.ERROR_TIMEOUT, pingResult.getError());
    }

    /**
     * This is used to check the given URL is valid or not.
     *
     * @param url
     * @return true if url is valid, false otherwise.
     */
    public static boolean isValidUrl(String url) {
        Pattern p = Patterns.WEB_URL;
        if (p != null) {
            Matcher m = p.matcher(url.toLowerCase());
            return m.matches();
        }
        return false;
    }

}
