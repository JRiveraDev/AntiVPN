package org.runnerer.mcleaks.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.runnerer.core.AntiPlugin;
import org.runnerer.core.common.utils.C;
import org.runnerer.mcleaks.MCLeaksAPI;

public class MCLeaksListener
{

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        MCLeaksAPI api = AntiPlugin.mcLeaksAPI;

        api.checkAccount(event.getPlayer().getUniqueId(), isMCLeaks ->
                System.out.println("Got: " + isMCLeaks), Throwable::printStackTrace);

        if (api.checkAccount(event.getPlayer().getUniqueId()).isBot())
        {
            event.getPlayer().kickPlayer(C.Red + "MCLeaks is not allowed on this network.");
        }
    }
}
