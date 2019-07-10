package org.runnerer.antivpn;

import org.apache.commons.io.IOUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.json.JSONObject;
import org.runnerer.antivpn.data.VPNDatabase;
import org.runnerer.core.AntiPlugin;
import org.runnerer.core.common.utils.C;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

public class AntiVPNHelper implements Listener
{

    public static void start(Player player)
    {
        if (player.hasPermission("vpn.bypass"))
        {
            return;
        }

        String ip = player.getAddress().getAddress().getHostAddress();

        if (VPNDatabase.isIpCached(ip) && VPNDatabase.getIp(ip))
        {
            player.kickPlayer(C.Red + "VPN usage is prohibited. Please contact an admin if you feel this is incorrect.");
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(AntiPlugin.instance, new Runnable()
        {

            @Override
            public void run()
            {
                runCheck(player);
            }
        });
    }

    public static void runCheck(Player player)
    {
        String address = player.getAddress().getAddress().getHostAddress();

        try
        {
            if (!isVPN(address)) return;

            Bukkit.getScheduler().runTask(AntiPlugin.instance, new Runnable()
            {
                @Override
                public void run()
                {
                    player.kickPlayer(C.Red + "VPN usage is prohibited. Please contact an admin if you feel this is incorrect.");
                }
            });
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static boolean isVPN(String ip) throws Exception
    {
        if (VPNDatabase.isIpCached(ip))
        {
            return VPNDatabase.getIp(ip);
        }

        JSONObject json = new JSONObject(IOUtils.toString(new URL("https://api.iplegit.com/info?ip=" + ip), Charset.forName("UTF-8")));

        if (json == null) return false;


        if (Boolean.parseBoolean(json.get("bad").toString()))
        {

            VPNDatabase.addIp(ip, true);
            return true;
        }

        VPNDatabase.addIp(ip, false);
        return false;
    }

    @EventHandler
    public void onLogin(PlayerJoinEvent event)
    {
        start(event.getPlayer());
    }
}
