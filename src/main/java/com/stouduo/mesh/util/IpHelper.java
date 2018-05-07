package com.stouduo.mesh.util;

import java.net.InetAddress;

public class IpHelper {

    public static String getHostIp() throws Exception {

        return InetAddress.getLocalHost().getHostAddress();
    }
}
