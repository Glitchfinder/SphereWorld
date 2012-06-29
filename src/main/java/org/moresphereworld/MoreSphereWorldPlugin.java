/*
 * Copyright (c) 2011-2012 Thomas Bucher <ToasterKTN>,
 * Zhuowei Zhang <zhuowei>, and Sean Porter <glitchkey@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.moresphereworld;

//* IMPORTS: JDK/JRE
	import java.io.File;
	import java.io.FileInputStream;
	import java.io.FileOutputStream;
	import java.io.ObjectInputStream;
	import java.io.ObjectOutputStream;
	import java.util.logging.Logger;
//* IMPORTS: BUKKIT
	import org.bukkit.configuration.file.FileConfiguration;
	import org.bukkit.plugin.java.JavaPlugin;
	import org.bukkit.plugin.PluginManager;
	import org.bukkit.World;
	import org.bukkit.WorldCreator;
//* IMPORTS: SPOUT
	//* NOT NEEDED
//* IMPORTS: OTHER
	//* NOT NEEDED

public class MoreSphereWorldPlugin extends JavaPlugin
{
	public SphereListener blockListener;
	public Logger log;
	public SphereCleaner sphereCleaner;
	public ChunkQueue chunkQueue= new ChunkQueue();

	public void onDisable()
	{
		if(sphereCleaner != null)
			sphereCleaner.stop();

		try {
			File chunkQueueFile = new File(getDataFolder(), "ChunkQueue.bin");
			FileOutputStream fos = new FileOutputStream(chunkQueueFile);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(chunkQueue);
			out.close();
			log.info("Successfully saved the chunk cleaning queue.");
		} catch (Exception e) {
			log.info("Unable to save the chunk cleaning queue. It may be corrupt.");
		}
	}

	public void onEnable()
	{
		log = this.getLogger();
		PluginManager pluginManager = getServer().getPluginManager();

		FileConfiguration config = this.getConfig();
		config.options().copyDefaults(true);
		SphereWorldConfig.initialize(config);
		this.saveConfig();

		
		blockListener = new SphereListener(this);
		blockListener.register();
		
		try
		{
			File chunkQueueFile = new File(getDataFolder(), "ChunkQueue.bin");
			FileInputStream fis = new FileInputStream(chunkQueueFile);
			ObjectInputStream in = new ObjectInputStream(fis);
			chunkQueue = (ChunkQueue) in.readObject();
			in.close();
			log.info("Successfully loaded the chunk cleaning queue.");
		}
		catch(Exception e)
		{
			log.info("Unable to read the chunk cleaning queue. It may be corrupt.");
		}

		try {
			File chunkQueueFile = new File(getDataFolder(), "ChunkQueue.bin");
			FileOutputStream fos = new FileOutputStream(chunkQueueFile);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(chunkQueue);
			out.close();
		} catch (Exception e) {}

		WorldCreator worldCreator = new WorldCreator(SphereWorldConfig.world);
		worldCreator = worldCreator.environment(SphereWorldConfig.worldEnvironment);
		worldCreator = worldCreator.seed(SphereWorldConfig.worldSeed);

		World world = getServer().createWorld(worldCreator);
		
		if(chunkQueue.getChunkList().isEmpty())
			return;

		if(sphereCleaner == null)
			sphereCleaner = new SphereCleaner(this, world, this.chunkQueue);

		log.info("Cleaning the chunks in " + SphereWorldConfig.world);
		sphereCleaner.start();
    }
}