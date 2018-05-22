package com.stouduo.mesh.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IpHelper {

    public static String getHostIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return "0.0.0.0";
    }
}
