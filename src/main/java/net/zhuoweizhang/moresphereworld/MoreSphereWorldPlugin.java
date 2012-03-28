package net.zhuoweizhang.moresphereworld;

import java.io.File;

import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import net.zhuoweizhang.moresphereworld.block.SphereBlockListener;
import net.zhuoweizhang.moresphereworld.config.SphereWorldConfig;

public class MoreSphereWorldPlugin extends JavaPlugin {

    public Spheres spheres = new Spheres();

    public File spheresFile;

    public SphereBlockListener blockListener;

    public Logger log;

    public void onDisable() {
    }

    public void onEnable() {

        log = this.getLogger();

        FileConfiguration config = this.getConfig();
        config.options().copyDefaults(true);

        SphereWorldConfig.initialize(config);

        this.saveConfig();

        blockListener = new SphereBlockListener(this);

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(blockListener, this);

	if (!SphereWorldConfig.otherworld) {
	    spheresFile = new File(getDataFolder(), "spheres.data");
	    spheres.readSphereList(spheresFile, getServer());
	    // Create Attach Listener / Attach MyShereGenerator
	    if (spheres.getSphereList().size() < 1) {
		log.info("[SphereWorld] NOT Loaded");
		return;
	    }
	}
    }

}

