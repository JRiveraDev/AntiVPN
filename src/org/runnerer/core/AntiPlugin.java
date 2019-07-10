package org.runnerer.core;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.runnerer.antivpn.AntiVPNHelper;
import org.runnerer.mcleaks.MCLeaksAPI;

import java.util.concurrent.TimeUnit;

public class AntiPlugin extends JavaPlugin
{

    public static AntiPlugin instance;
    public static MCLeaksAPI mcLeaksAPI;

    @Override
    public void onEnable()
    {
        instance = this;
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new AntiVPNHelper(), this);

        MCLeaksAPI api = MCLeaksAPI.builder().threadCount(2).expireAfter(10, TimeUnit.MINUTES).build();
        mcLeaksAPI = api;

    }
}
