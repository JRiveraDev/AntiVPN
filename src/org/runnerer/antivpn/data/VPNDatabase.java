package org.runnerer.antivpn.data;

import java.util.HashMap;

public class VPNDatabase
{

    private static HashMap<String, Boolean> ips = new HashMap<>();

    public static void addIp(String ip, boolean b)
    {
        ips.put(ip, b);
    }

    public static boolean getIp(String ip)
    {
        return ips.get(ip);
    }

    public static boolean isIpCached(String ip)
    {
        return ips.containsKey(ip);
    }
}
