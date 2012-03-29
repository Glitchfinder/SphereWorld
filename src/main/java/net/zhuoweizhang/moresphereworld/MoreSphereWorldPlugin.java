package net.zhuoweizhang.moresphereworld;

import java.io.File;

import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import net.zhuoweizhang.moresphereworld.block.SphereBlockListener;
import net.zhuoweizhang.moresphereworld.chunk.SphereChunkGenerator;
import net.zhuoweizhang.moresphereworld.chunk.SphereBlockPopulator;
import net.zhuoweizhang.moresphereworld.config.SphereWorldConfig;

public class MoreSphereWorldPlugin extends JavaPlugin {

    public Spheres spheres = new Spheres();

    public File spheresFile;

    public SphereBlockListener blockListener;

    public Logger log;

    //public SphereChunkGenerator generator;

    public SphereBlockPopulator spherePopulator;

    private GenerateListener generateListener;

    public void onDisable() {
    }

    public void onEnable() {

        log = this.getLogger();

        PluginManager pluginManager = getServer().getPluginManager();

        FileConfiguration config = this.getConfig();
        config.options().copyDefaults(true);

        SphereWorldConfig.initialize(config);

        this.saveConfig();

        //generator = new SphereChunkGenerator();

	if (!SphereWorldConfig.otherworld) {
	    spheresFile = new File(getDataFolder(), "spheres.data");
	    spheres.readSphereList(spheresFile, getServer());
	    // Create Attach Listener / Attach MyShereGenerator
	    if (spheres.getSphereList().size() < 1) {
		log.info("[MoreSphereWorld] NOT Loaded - Unable to read spheres.data file. It may be corrupt.");
		return;
	    }
	}

        generateListener = new GenerateListener();
        pluginManager.registerEvents(generateListener, this);

        spherePopulator = new SphereBlockPopulator();
        spherePopulator.spheres = this.spheres;

        getServer().createWorld(new WorldCreator(SphereWorldConfig.world));

        blockListener = new SphereBlockListener(this);


        pluginManager.registerEvents(blockListener, this);


    }

    public boolean onCommand(CommandSender sender, Command command, java.lang.String label, java.lang.String[] args) {
	if (command.getName().equalsIgnoreCase("sphere")) {
	    if (!(sender instanceof Player)) {
		log.info("This command cannot be used in the console.");
		return true;
	    }
	    Player player = (Player) sender;
	    Vector v = player.getLocation().toVector();
	    for (Sphere s: spheres.getSphereList()) {
		if(s.getV().distance(v) < s.getSize())
		{
		    player.sendMessage("Sphere you are in has");
		    player.sendMessage("Center x:"+ s.getX() +" y:"+ s.getY()+ "z:"+s.getZ());
		    player.sendMessage("Radius r:"+ s.getSize());
		    player.sendMessage("From x,z:"+ (s.getX() - s.getSize()) +","+(s.getZ() - s.getSize()));
		    player.sendMessage("To x,z:"+ (s.getX() + s.getSize()) +","+(s.getZ() + s.getSize()));
		}
	    }
	    return true;
	}
        return false;
    }

    private class GenerateListener implements Listener {
        @EventHandler
        public void onWorldInit(WorldInitEvent event) {
            if (event.getWorld().getName().equals(SphereWorldConfig.world)) {
                event.getWorld().getPopulators().add(spherePopulator);
            }
        }
    }

}

