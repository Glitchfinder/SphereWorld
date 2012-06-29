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
	import java.lang.Boolean;
	import java.lang.Integer;
	import java.lang.Runnable;
	import java.util.ArrayList;
	import java.util.Collections;
	import java.util.Comparator;
	import java.util.Random;
//* IMPORTS: BUKKIT
	import org.bukkit.block.Block;
	import org.bukkit.craftbukkit.CraftWorld;
	import org.bukkit.generator.BlockPopulator;
	import org.bukkit.Location;
	import org.bukkit.util.Vector;
//* IMPORTS: SPOUT
	//* NOT NEEDED
//* IMPORTS: OTHER
	import net.minecraft.server.Chunk;
	import net.minecraft.server.ChunkSection;
	import net.minecraft.server.World;

public class SphereCleaner implements Runnable
{
	private MoreSphereWorldPlugin plugin;
	private int taskID;
	private org.bukkit.World world;
	private World defaultWorld;
	private Random random = new Random(SphereWorldConfig.sphereSeed);

	public ChunkQueue chunkQueue;
	private Vector vector = null;

	public SphereCleaner(MoreSphereWorldPlugin plugin, org.bukkit.World world, ChunkQueue chunkQueue)
	{
		this.plugin = plugin;
		taskID = -1;
		this.world = world;
		this.defaultWorld = ((CraftWorld) world).getHandle();
		this.chunkQueue = chunkQueue;
		int chunkCount;
	}

	private Comparator<Sphere> COMPARATOR = new Comparator<Sphere>() 
	{
		// This is where the sorting happens.
		public int compare(Sphere sphere1, Sphere sphere2)
		{
			double value1 = sphere1.getVector().distance(vector) - sphere1.getSize();
			double value2 = sphere2.getVector().distance(vector) - sphere2.getSize();
			return (int) (value1 - value2);
		}
	};

	public void start()
	{
		if(taskID >= 0)
			return;

		taskID = plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, this, 1);
		return;
	}

	public void run()
	{
		if(plugin.chunkQueue.getChunkList().isEmpty())
		{
			plugin.log.info("Finished cleaning the current chunks.");
			stop();
			return;
		}

		int chunkX;
		int chunkZ;

		String chunkName;
		String[] chunkCoords;

		try
		{
			chunkName = plugin.chunkQueue.getChunkList().get(0);
			chunkCoords = chunkName.split(":");
			if(chunkCoords.length < 2)
			{
				plugin.chunkQueue.getChunkList().remove(chunkName);
				stop();
				start();
				return;
			}

			chunkX = Integer.parseInt(chunkCoords[0]);
			chunkZ = Integer.parseInt(chunkCoords[1]);
		}
		catch(Exception e)
		{
			plugin.log.info("Unable to clean the current chunk. The chunk queue file may be corrupt.");
			stop();
			return;
		}

		final org.bukkit.Chunk chunk = world.getChunkAt(chunkX, chunkZ);

		int chunkBlockX = chunkX * 16;
		int chunkBlockZ = chunkZ * 16;

		if (!defaultWorld.isLoaded(chunkBlockX, 0, chunkBlockZ))
		{
			plugin.chunkQueue.getChunkList().remove(chunkName);
			stop();
			start();
			return;
		}

		Chunk defaultChunk = defaultWorld.getChunkAt(chunkX, chunkZ);
		ChunkSection[] blockData = defaultChunk.h();

		int seed = 0;
		int height = 0;
		int radius = 0;

		int xDifference = (chunkX * 16) % 20;
		int zDifference = (chunkZ * 16) % 20;

		double baseX = ((double) chunkX * 16D) - ((double) xDifference);
		double baseZ = ((double) chunkZ * 16D) - ((double) zDifference);

		int radiusDifference = SphereWorldConfig.maxRadius % 20;
		int currentRadius = SphereWorldConfig.maxRadius + (20 - radiusDifference);

		double currentXMin = baseX - currentRadius;
		double currentZMin = baseZ - currentRadius;
		double currentXMax = baseX + currentRadius + 20;
		double currentZMax = baseZ + currentRadius + 20;

		Spheres spheres = new Spheres();
		spheres.getSphereList().clear();

		for (double x = currentXMin; x < currentXMax; x += 20)
		{
			for (double z = currentZMin; z < currentZMax; z += 20)
			{
				Boolean makenew = true;
				seed = SphereWorldConfig.maxHeight - SphereWorldConfig.minHeight;

				random.setSeed( (long) (x * 341873128712L + z * 132897987541L));

				height = random.nextInt(seed) + SphereWorldConfig.minHeight;
				Location location = new Location(null, x + random.nextInt(20), height, z + random.nextInt(20));

				if (random.nextInt(100) > 5)
					makenew = false;


				if (random.nextInt(100) > SphereWorldConfig.sphereChance)
					makenew = false;

				if (makenew)
				{
					Sphere newSphere = new Sphere();

					seed = SphereWorldConfig.maxRadius - SphereWorldConfig.minRadius;
					radius = random.nextInt(seed) + SphereWorldConfig.minRadius;

					newSphere.setSize(radius);

					newSphere.setVector(new Vector(location.getX(), location.getY(), location.getZ()));
					newSphere.setWorld(SphereWorldConfig.world);
					newSphere.setX(location.getX());
					newSphere.setY(location.getY());
					newSphere.setZ(location.getZ());
					spheres.addSphereToList(newSphere);
					newSphere = null;
				}
			}
		}

		boolean hassphere = !spheres.getSphereList().isEmpty();

		final int worldHeight = 256;
		final int water = (SphereWorldConfig.noWater? 0 : 9);

		if (SphereWorldConfig.glassType == 3 && spheres.getSphereList().size() > 1) {
			vector = new Vector((double) chunkX * 16 + 8 , (double) 64, (double) chunkZ * 16 + 8);
			Collections.sort(spheres.getSphereList(), COMPARATOR);
		}

		int glassType = 0;
		boolean randomType = false;

		if (!spheres.getSphereList().isEmpty())
			randomType = spheres.getSphereList().get(0).getSize() % 2 == 1;

		if (SphereWorldConfig.glassType == 1 || (SphereWorldConfig.glassType == 3 && randomType))
			glassType = 1;
		else if (SphereWorldConfig.glassType == 2 || (SphereWorldConfig.glassType == 3 && !randomType))
			glassType = 2;

		try{
			for (int z = 0; z < 16; ++z)
			{
				for (int x = 0; x < 16; ++x)
				{
					for (int y = worldHeight - 1; y >= 0; --y)
					{	
						if (!world.isChunkLoaded(chunk) && !world.loadChunk(chunkX, chunkZ, false))
						{
							plugin.chunkQueue.getChunkList().remove(chunkName);
							return;
						}

						final int sectionId = (y >> 4);

						if (blockData[sectionId] == null)
							blockData[sectionId] = new ChunkSection((sectionId) << 4);

						final int currentBlockId = blockData[sectionId].a(x, y & 0xf, z);

						if (y == 1 && !SphereWorldConfig.noFloorSpawn)
						{
							defaultChunk.f(x, y, z);
							blockData[sectionId].a(x, y & 0xf, z, water);
							continue;
						}
						else if (y == 0 && !SphereWorldConfig.noFloorSpawn)
						{
							defaultChunk.f(x, y, z);
							blockData[sectionId].a(x, y & 0xf, z, 7);
							continue;
						}
						else if (y <= 1)
						{
							defaultChunk.f(x, y, z);
							blockData[sectionId].a(x, y & 0xf, z, 0);
							continue;
						}

						if(!hassphere)
						{
							defaultChunk.f(x, y, z);
							blockData[sectionId].a(x, y & 0xf, z, 0);
							continue;
						}

						ArrayList<Boolean> glass = new ArrayList<Boolean>();

						final double blockX = (double) chunkX * 16 + x;
						final double blockY = (double) y;
						final double blockZ = (double) chunkZ * 16 + z;

						final Vector blockVector = new Vector(blockX, blockY, blockZ);
						double distance = 0;

						boolean keep = false;
						boolean glowstone = false;

						for (Sphere sphere : spheres.getSphereList())
						{
							distance = sphere.getVector().distance(blockVector);

							if(distance >= sphere.getSize())
								continue;

							keep = true;

							if(glassType == 0 || (glassType == 2 && y >= 64))
								break;

							if(distance <= sphere.getSize() - 1.1)
							{
								glass.add(false);
								continue;
							}

							if (sphere.getX() == chunkX * 16 + x || sphere.getZ() ==  chunkZ * 16 + z)
								glowstone = true;

							glass.add(true);
						}

						if (!keep && currentBlockId != 0 && glass.isEmpty())
						{
							defaultChunk.f(x, y, z);
							blockData[sectionId].a(x, y & 0xf, z, 0);
							continue;
						}
						else if(!glass.isEmpty() && !glass.contains(false))
						{
							if (!SphereWorldConfig.useGlowstone)
							{
								defaultChunk.f(x, y, z);
								blockData[sectionId].a(x, y & 0xf, z, SphereWorldConfig.glassBlockId);
								continue;
							}

							if (glowstone)
							{
								defaultChunk.f(x, y, z);
								blockData[sectionId].a(x, y & 0xf, z, 89);
								continue;
							}

							defaultChunk.f(x, y, z);
							blockData[sectionId].a(x, y & 0xf, z, SphereWorldConfig.glassBlockId);
							continue;
						}
						if (!keep && currentBlockId != 0)
						{
							defaultChunk.f(x, y, z);
							blockData[sectionId].a(x, y & 0xf, z, 0);
							continue;
						}
						else if (!keep)
							continue;
					}
				}
			}
		}
		catch(Exception e)
		{
			plugin.log.info("An error occurred while cleaning the chunk at " + chunkX + ", " + chunkZ + "." );
			e.printStackTrace();
			return;
		}

		try
		{
			plugin.chunkQueue.getChunkList().remove(chunkName);
		}
		catch(Exception e) {}

		defaultChunk.a(blockData);
		world.refreshChunk(chunkX, chunkZ);
		world.unloadChunkRequest(chunkX, chunkZ, true);

		if(SphereWorldConfig.verboseOutput)
			plugin.log.info("Sphere chunk at " + chunkX + ", " + chunkZ + " has been cleaned." );

		taskID = plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, this, 2);
		return;
	}

	public void stop()
	{
		if(taskID < 0)
			return;

		plugin.getServer().getScheduler().cancelTask(taskID);
		taskID = -1;
	}
}