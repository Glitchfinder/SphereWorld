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
	import java.util.List;
	import java.util.Random;
//* IMPORTS: BUKKIT
	import org.bukkit.craftbukkit.CraftWorld;
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
	private List<String> chunkQueue;

	private Vector vector = null;

	public SphereCleaner(MoreSphereWorldPlugin plugin, org.bukkit.World world)
	{
		this.plugin = plugin;
		taskID = -1;
		this.world = world;
		this.defaultWorld = ((CraftWorld) world).getHandle();
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

		taskID = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this, 1);
		return;
	}

	public void run()
	{
		for (int chunkCount = 0; chunkCount < 5; chunkCount++)
		{
			int chunkX = 0;
			int chunkZ = 0;

			String chunkName = "";
			String[] chunkCoords;

			if(plugin.chunkQueue.getChunkList().isEmpty())
				softStop();
			try
			{
				byte count;
				int chunkBlockX, chunkBlockZ;

				for(int i = 0; i < plugin.chunkQueue.getChunkList().size(); i++)
				{
					chunkName = plugin.chunkQueue.getChunkList().get(0);
					chunkCoords = chunkName.split(":");
					if(chunkCoords.length < 2)
					{
						plugin.chunkQueue.removeChunk(chunkName);
						softStop();
						return;
					}

					chunkX = Integer.parseInt(chunkCoords[0]);
					chunkZ = Integer.parseInt(chunkCoords[1]);

					count = 0;

					for(int x = chunkX - 1; x <= chunkX + 1; x++)
					{
						for(int z = chunkZ - 1; z <= chunkZ + 1; z++)
						{
							chunkBlockX = x * 16;
							chunkBlockZ = z * 16;

							if (!defaultWorld.isLoaded(chunkBlockX, 1, chunkBlockZ))
								continue;

							count += 1;
						}
					}

					if(count >= 9)
						break;
				}
			}
			catch(Exception e)
			{
				plugin.log.info("Unable to clean the current chunk. The chunk queue file may be corrupt.");
				stop();
				return;
			}

			final org.bukkit.Chunk chunk = world.getChunkAt(chunkX, chunkZ);

			Chunk defaultChunk = defaultWorld.getChunkAt(chunkX, chunkZ);
			ChunkSection[] blockData = defaultChunk.h();

			int seed = 0;
			int height = 0;
			int radius = 0;

			int xDifference = (chunkX * 16) % 20;
			int zDifference = (chunkZ * 16) % 20;

			int baseX = (chunkX * 16) - xDifference;
			int baseZ = (chunkZ * 16) - zDifference;

			int radiusDifference = SphereWorldConfig.maxRadius % 20;
			int currentRadius = SphereWorldConfig.maxRadius + (20 - radiusDifference);

			int currentXMin = baseX - currentRadius;
			int currentZMin = baseZ - currentRadius;
			int currentXMax = baseX + currentRadius + 20;
			int currentZMax = baseZ + currentRadius + 20;

			int maxRadius = 0;
			int minRadius = 0;

			int minDistance = 0;
			double distance = 0;

			Spheres spheres = new Spheres();
			spheres.getSphereList().clear();

			for (int x = currentXMin; x < currentXMax; x += 20)
			{
				for (int z = currentZMin; z < currentZMax; z += 20)
				{
					seed = SphereWorldConfig.maxHeight - SphereWorldConfig.minHeight;

					random.setSeed( (long) ((x * 341873128712L + z * 132897987541L) ^ SphereWorldConfig.sphereSeed));

					height = random.nextInt(seed) + SphereWorldConfig.minHeight;
					Location location = new Location(null, x + random.nextInt(20), height, z + random.nextInt(20));

					if (random.nextInt(100) > 5)
						continue;

					if (random.nextInt(100) > SphereWorldConfig.sphereChance)
						continue;

					Sphere newSphere = new Sphere();

					seed = SphereWorldConfig.maxRadius - SphereWorldConfig.minRadius;
					radius = random.nextInt(seed) + SphereWorldConfig.minRadius;

					newSphere.setSize(radius);

					newSphere.setWorld(SphereWorldConfig.world);
					newSphere.setX(location.getX());
					newSphere.setY(location.getY());
					newSphere.setZ(location.getZ());
					spheres.addSphereToList(newSphere);
					newSphere = null;
				}
			}

			for (int x = currentXMin; x < currentXMax; x += 20)
			{
				for (int z = currentZMin; z < currentZMax; z += 20)
				{
					seed = SphereWorldConfig.maxHeight - SphereWorldConfig.minHeight;

					random.setSeed( (long) ((x * 341873128712L + z * 132897987541L) ^ (SphereWorldConfig.sphereSeed * 2)));

					height = random.nextInt(seed) + SphereWorldConfig.minHeight;
					Location location = new Location(null, x + random.nextInt(20), height, z + random.nextInt(20));

					int oreSelection = random.nextInt(11);

					if (random.nextInt(200) > 20)
						continue;

					int randomChance = random.nextInt(100);

					try
					{
						if(!SphereWorldConfig.useOreBubbles.get(oreSelection))
							continue;

						if (randomChance > SphereWorldConfig.oreSpawnChance.get(oreSelection))
							continue;

						Sphere newSphere = new Sphere();

						maxRadius = SphereWorldConfig.maxOreRadius.get(oreSelection);
						minRadius = SphereWorldConfig.minOreRadius.get(oreSelection);

						seed = maxRadius - minRadius;
						radius = random.nextInt(seed) + minRadius;

						newSphere.setSize(radius);

						boolean generate = true;

						for(Sphere oldSphere : spheres.getSphereList())
						{
							minDistance = newSphere.getSize() + oldSphere.getSize();

							distance = newSphere.getVector().distance(oldSphere.getVector());
						}

						if(!generate)
							continue;

						newSphere.setWorld(SphereWorldConfig.world);
						newSphere.setX(location.getX());
						newSphere.setY(location.getY());
						newSphere.setZ(location.getZ());

						newSphere.setOreSphere(true);
						newSphere.setOreId(SphereWorldConfig.oreBlockId.get(oreSelection));
						newSphere.setOreShellId(SphereWorldConfig.oreShellId);

						spheres.addSphereToList(newSphere);
						newSphere = null;
					}
					catch (Exception e) {}
				}
			}

			boolean stronghold = false;
			Vector strongholdVector = null;
			int strongholdX, strongholdZ, strongholdDistance;
			double xPow, zPow;

			if(plugin.strongholdCoords[0] == null)
				plugin.canSpawnStronghold(0, 0);

			boolean village = false;
			ArrayList<Vector> villageVectors = new ArrayList<Vector>();

			for(int x = chunkX - 8; x <= chunkX + 8; x++)
			{
				for(int z = chunkZ - 8; z <= chunkZ + 8; z++)
				{
					if(!plugin.canSpawnVillage(defaultWorld, x, z))
						continue;

					village = true;
					villageVectors.add(new Vector(((x * 16) + 8), 64, ((z * 16) + 8)));
				}
			}

			for(int i = 0; i < plugin.strongholdCoords.length; i++)
			{
				if(plugin.strongholdCoords[i] == null)
					continue;

				strongholdX = plugin.strongholdCoords[i].x;
				strongholdZ = plugin.strongholdCoords[i].z;
				xPow = Math.pow((strongholdX - chunkX), 2);
				zPow = Math.pow((strongholdZ - chunkZ), 2);
				strongholdDistance = (int) Math.sqrt(xPow + zPow);

				if(strongholdDistance > 5)
					continue;

				stronghold = true;
				strongholdVector = new Vector(((strongholdX * 16) + 8), 10, ((strongholdZ * 16) + 8));
			}

			if((stronghold && strongholdVector != null) || (village && !villageVectors.isEmpty()))
			{
				for (int x = currentXMin - 60; x < currentXMax + 60; x += 20)
				{
					for (int z = currentZMin - 60; z < currentZMax + 60; z += 20)
					{
						random.setSeed( (long) ((x * 341873128712L + z * 132897987541L) ^ SphereWorldConfig.sphereSeed));

						height = 50;
						Location location = new Location(null, x + random.nextInt(20), height, z + random.nextInt(20));

						Vector currentLocation = new Vector(location.getX(), 10, location.getZ());

						if(stronghold && strongholdVector != null && strongholdVector.distance(currentLocation) <= 48)
						{
							Sphere newSphere = new Sphere();

							radius = 48;

							newSphere.setSize(radius);

							newSphere.setWorld(SphereWorldConfig.world);
							newSphere.setX(location.getX());
							newSphere.setY(location.getY());
							newSphere.setZ(location.getZ());
							newSphere.setProtectedStructure(true);
							spheres.addSphereToList(newSphere);
							newSphere = null;
						}

						if(!village || villageVectors.isEmpty())
							continue;

						height = 64;
						location = new Location(null, x + random.nextInt(20), height, z + random.nextInt(20));

						currentLocation = new Vector(location.getX(), 64, location.getZ());

						for(Vector currentVillage : villageVectors)
						{
							if(currentVillage.distance(currentLocation) <= 88)
							{
								Sphere newSphere = new Sphere();

								radius = 48;

								newSphere.setSize(radius);

								newSphere.setWorld(SphereWorldConfig.world);
								newSphere.setX(location.getX());
								newSphere.setY(location.getY());
								newSphere.setZ(location.getZ());
								newSphere.setProtectedStructure(true);
								spheres.addSphereToList(newSphere);
								newSphere = null;
								break;
							}
						}
					}
				}
			}

			boolean hassphere = !spheres.getSphereList().isEmpty();

			final int worldHeight = 256;

			final int airId		= SphereWorldConfig.airBlockId;
			final int waterId	= (SphereWorldConfig.noWater? airId : 9);
			final int glassId	= SphereWorldConfig.glassBlockId;

			final boolean useGlowstone = SphereWorldConfig.useGlowstone;

			if (SphereWorldConfig.glassType == 3 && spheres.getSphereList().size() > 1)
			{
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

			try
			{
				for (int z = 0; z < 16; ++z)
				{
					for (int x = 0; x < 16; ++x)
					{
						for (int y = worldHeight - 1; y >= 0; --y)
						{
							final int sectionId = (y >> 4);

							if (blockData[sectionId] == null)
								blockData[sectionId] = new ChunkSection((sectionId) << 4);

							final int currentBlockId = blockData[sectionId].a(x, y & 0xf, z);

							if (y == 1 && !SphereWorldConfig.noFloorSpawn && currentBlockId != waterId)
							{
								defaultChunk.f(x, y, z);
								blockData[sectionId].a(x, y & 0xf, z, waterId);
								continue;
							}
							else if (y == 0 && !SphereWorldConfig.noFloorSpawn && currentBlockId != 7)
							{
								defaultChunk.f(x, y, z);
								blockData[sectionId].a(x, y & 0xf, z, 7);
								continue;
							}
							else if (y <= 1 && currentBlockId != airId)
							{
								defaultChunk.f(x, y, z);
								blockData[sectionId].a(x, y & 0xf, z, airId);
								continue;
							}

							if(!hassphere && currentBlockId != airId)
							{
								defaultChunk.f(x, y, z);
								blockData[sectionId].a(x, y & 0xf, z, airId);
								continue;
							}

							ArrayList<Boolean> glass = new ArrayList<Boolean>();

							final double blockX = (double) chunkX * 16 + x;
							final double blockY = (double) y;
							final double blockZ = (double) chunkZ * 16 + z;

							final Vector blockVector = new Vector(blockX, blockY, blockZ);

							boolean keep = false;
							boolean glowstone = false;
							int shellId = 0;
							int oreId = 0;

							for (Sphere sphere : spheres.getSphereList())
							{
								distance = sphere.getVector().distance(blockVector);

								if(distance >= sphere.getSize())
									continue;

								keep = true;

								if(sphere.isOreSphere())
								{
									shellId = sphere.getOreShellId();
									oreId = sphere.getOreId();
								}

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

							if (!keep && glass.isEmpty() && currentBlockId != airId)
							{
								defaultChunk.f(x, y, z);
								blockData[sectionId].a(x, y & 0xf, z, airId);
								continue;
							}
							else if(!glass.isEmpty() && !glass.contains(false))
							{
								if (shellId != 0)
								{
									defaultChunk.f(x, y, z);
									blockData[sectionId].a(x, y & 0xf, z, shellId);
									continue;
								}

								if (!useGlowstone && currentBlockId != glassId)
								{
									defaultChunk.f(x, y, z);
									blockData[sectionId].a(x, y & 0xf, z, glassId);
									continue;
								}

								if (useGlowstone && glowstone && currentBlockId != 89)
								{
									defaultChunk.f(x, y, z);
									blockData[sectionId].a(x, y & 0xf, z, 89);
									continue;
								}

								if(currentBlockId == glassId)
									continue;

								defaultChunk.f(x, y, z);
								blockData[sectionId].a(x, y & 0xf, z, glassId);
								continue;
							}

							if(keep && oreId != 0)
							{
								defaultChunk.f(x, y, z);
								blockData[sectionId].a(x, y & 0xf, z, oreId);
								continue;
							}

							if (!keep && currentBlockId != airId)
							{
								defaultChunk.f(x, y, z);
								blockData[sectionId].a(x, y & 0xf, z, airId);
								continue;
							}
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
				plugin.chunkQueue.removeChunk(chunkName);
			}
			catch(Exception e) {}

			defaultChunk.a(blockData);
			defaultChunk.initLighting();
			world.refreshChunk(chunkX, chunkZ);

			if(SphereWorldConfig.verboseOutput)
				plugin.log.info("Sphere chunk at " + chunkX + ", " + chunkZ + " has been cleaned." );

		}

		softStop();
		return;
	}

	public void stop()
	{
		if(taskID < 0)
			return;

		plugin.getServer().getScheduler().cancelTask(taskID);
		taskID = -1;
	}

	public void softStop()
	{
		stop();

		if(!plugin.chunkQueue.getChunkList().isEmpty())
		{
			start();
			return;
		}

		plugin.log.info("Finished cleaning the current chunks.");
		return;
	}
}