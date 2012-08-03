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

package org.sphereworld;

//* IMPORTS: JDK/JRE
	import java.io.File;
	import java.io.FileInputStream;
	import java.io.FileOutputStream;
	import java.io.InputStream;
	import java.io.ObjectInputStream;
	import java.io.ObjectOutputStream;
	import java.io.OutputStream;
	import java.lang.Math;
	import java.net.URL;
	import java.net.URLDecoder;
	import java.nio.channels.FileChannel;
	import java.util.ArrayList;
	import java.util.Arrays;
	import java.util.Collections;
	import java.util.List;
	import java.util.logging.Logger;
	import java.util.Random;
//* IMPORTS: BUKKIT
	import org.bukkit.configuration.file.FileConfiguration;
	import org.bukkit.configuration.file.YamlConfiguration;
	import org.bukkit.craftbukkit.CraftWorld;
	import org.bukkit.plugin.java.JavaPlugin;
	import org.bukkit.World;
	import org.bukkit.WorldCreator;
//* IMPORTS: SPOUT
	//* NOT NEEDED
//* IMPORTS: OTHER
	import net.minecraft.server.BiomeBase;
	import net.minecraft.server.ChunkCoordIntPair;
	import net.minecraft.server.ChunkPosition;
	import net.minecraft.server.WorldChunkManager;

public class SphereWorld extends JavaPlugin
{
	public SphereListener blockListener;
	public Logger log;
	public SphereCleaner sphereCleaner;
	public SpherePopulator populator;
	public World world = null;
	public ChunkQueue chunkQueue = new ChunkQueue();
	public SaveQueue saveQueue = new SaveQueue(this);
	public ChunkCoordIntPair strongholdCoords[] = new ChunkCoordIntPair[3];
	private BiomeBase strongholdBiomes[] = (new BiomeBase[]
	{
		BiomeBase.DESERT, BiomeBase.FOREST, BiomeBase.EXTREME_HILLS,
		BiomeBase.SWAMPLAND, BiomeBase.TAIGA, BiomeBase.ICE_PLAINS,
		BiomeBase.ICE_MOUNTAINS, BiomeBase.DESERT_HILLS,
		BiomeBase.FOREST_HILLS, BiomeBase.SMALL_MOUNTAINS,
		BiomeBase.JUNGLE, BiomeBase.JUNGLE_HILLS
	});
	private List villageSpawnBiomes = Arrays.asList(new BiomeBase[]
	{
		BiomeBase.PLAINS, BiomeBase.DESERT
	});
	private List templeBiomes = Arrays.asList(new BiomeBase[]
	{
		BiomeBase.DESERT, BiomeBase.DESERT_HILLS, BiomeBase.JUNGLE
	});

	private boolean calculatedStrongholds = false;

	public void onDisable()
	{
		if(sphereCleaner != null)
			sphereCleaner.stop();

		saveChunkQueue();
	}

	public void onEnable()
	{
		log = this.getLogger();

		readConfig();
		loadChunkQueue();

		populator = new SpherePopulator(this);
		blockListener = new SphereListener(this);
		blockListener.register();

		createWorld();

		if(chunkQueue.getChunkList().isEmpty())
			return;

		if(sphereCleaner == null)
			sphereCleaner = new SphereCleaner(this, world);

		log.info("Cleaning the chunks in " + SphereWorldConfig.world);
		sphereCleaner.start();

		if(saveQueue == null)
			saveQueue = new SaveQueue(this);

		saveQueue.start();
	}

	public boolean canSpawnStronghold(int x, int z)
	{
		if(world == null)
			return false;

		if(!calculatedStrongholds)
		{
			Random random = new Random();
			random.setSeed(SphereWorldConfig.worldSeed);
			double d = random.nextDouble() * Math.PI * 2D;

			net.minecraft.server.World defaultWorld = ((CraftWorld) world).getHandle();
			ChunkPosition chunkposition = null;

			for (int k = 0; k < strongholdCoords.length; k++)
			{
				double d1 = (1.25D + random.nextDouble()) * 32D;
				int chunkX = (int)Math.round(Math.cos(d) * d1);
				int chunkZ = (int)Math.round(Math.sin(d) * d1);
				ArrayList arraylist = new ArrayList();
				Collections.addAll(arraylist, strongholdBiomes);
				int xPos = (chunkX << 4) + 8;
				int zPos = (chunkZ << 4) + 8;
				WorldChunkManager manager = defaultWorld.getWorldChunkManager();
				chunkposition = manager.a(xPos, zPos, 112, arraylist, random);

				if (chunkposition != null)
				{
					chunkX = chunkposition.x >> 4;
					chunkZ = chunkposition.z >> 4;
				}

				strongholdCoords[k] = new ChunkCoordIntPair(chunkX, chunkZ);
				d += (Math.PI * 2D) / (double)strongholdCoords.length;
			}

			calculatedStrongholds = true;
		}

		ChunkCoordIntPair achunkcoordintpair[] = strongholdCoords;
		int i = achunkcoordintpair.length;

		for (int j = 0; j < i; j++)
		{
			ChunkCoordIntPair chunkcoordintpair = achunkcoordintpair[j];

			if (x == chunkcoordintpair.x && z == chunkcoordintpair.z)
			{
				return true;
			}
		}

		return false;
	}

	public boolean canSpawnVillage(int x, int z)
	{
		if(world == null)
			return false;

		byte byte0 = 32;
		byte byte1 = 8;
		int currentX = x;
		int currentZ = z;

		if (x < 0)
			x -= byte0 - 1;

		if (z < 0)
			z -= byte0 - 1;

		int k = x / byte0;
		int l = z / byte0;
		net.minecraft.server.World defaultWorld = ((CraftWorld) world).getHandle();
		Random random = defaultWorld.D(k, l, 0x9e7f70);
		k *= byte0;
		l *= byte0;
		k += random.nextInt(byte0 - byte1);
		l += random.nextInt(byte0 - byte1);
		x = currentX;
		z = currentZ;

		if (x == k && z == l)
		{
			WorldChunkManager manager = defaultWorld.getWorldChunkManager();
			boolean flag = manager.a(x * 16 + 8, z * 16 + 8, 0, villageSpawnBiomes);

			if (flag)
				return true;
		}

		return false;
	}

	public boolean canSpawnTemple(int x, int z)
	{
		if(world == null)
			return false;

		int i = 32;
		int j = 8;

		int k = x;
		int m = z;
		if (x < 0) x -= i - 1;
		if (z < 0) z -= i - 1;

		int n = x / i;
		int i1 = z / i;
		net.minecraft.server.World defaultWorld = ((CraftWorld) world).getHandle();
		Random localRandom = defaultWorld.D(n, i1, 14357617);
		n *= i;
		i1 *= i;
		n += localRandom.nextInt(i - j);
		i1 += localRandom.nextInt(i - j);
		x = k;
		z = m;

		if ((x == n) && (z == i1))
		{
			WorldChunkManager manager = defaultWorld.getWorldChunkManager();
			boolean bool = manager.a(x * 16 + 8, z * 16 + 8, 0, templeBiomes);

			if (bool)
			{
				return true;
			}
		}

		return false;
	}

	public void readConfig()
	{
		copyConfig();
		File configFile = new File(getDataFolder(), "config.yml");
		
		try{
			if(configFile.exists())
			{
				InputStream stream = new FileInputStream(configFile);
				YamlConfiguration config;
				config = YamlConfiguration.loadConfiguration(stream);
				SphereWorldConfig.initialize(config);
			}
			else
			{
				FileConfiguration config = getConfig();
				SphereWorldConfig.initialize(config);
			}
		}
		catch(Exception e)
		{
			FileConfiguration config = getConfig();
			SphereWorldConfig.initialize(config);
		}
	}

	public boolean copyConfig()
	{
		File sourceFile;
		File destinationFile;
		try
		{
			destinationFile = new File(getDataFolder(), "config.yml");

			if(destinationFile.exists())
				return false;

			destinationFile.createNewFile();

			InputStream inputStream = getClass().getResourceAsStream("/config.yml");
			OutputStream out = new FileOutputStream(destinationFile);
			byte buffer[] = new byte[1024];
			int length;

			while((length = inputStream.read(buffer)) > 0)
				out.write(buffer, 0, length);

			out.close();
			inputStream.close();
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}

	public void loadChunkQueue()
	{
		File chunkQueueFile;

		try
		{
			chunkQueueFile = new File(getDataFolder(), "ChunkQueue.bin");

			if(!chunkQueueFile.exists())
				return;

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

		saveChunkQueue();
	}

	public void saveChunkQueue()
	{
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

	public void createWorld()
	{
		WorldCreator worldCreator = new WorldCreator(SphereWorldConfig.world);
		worldCreator = worldCreator.environment(SphereWorldConfig.worldEnvironment);
		worldCreator = worldCreator.seed(SphereWorldConfig.worldSeed);
		this.world = worldCreator.createWorld();
	}
}
