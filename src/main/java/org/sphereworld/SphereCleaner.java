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
	import java.lang.Boolean;
	import java.lang.Integer;
	import java.lang.Runnable;
	import java.util.ArrayList;
	import java.util.List;
	import java.util.Random;
//* IMPORTS: BUKKIT
	import org.bukkit.craftbukkit.CraftWorld;
	import org.bukkit.Location;
	import org.bukkit.scheduler.BukkitScheduler;
	import org.bukkit.util.Vector;
//* IMPORTS: SPOUT
	//* NOT NEEDED
//* IMPORTS: OTHER
	import net.minecraft.server.Chunk;
	import net.minecraft.server.ChunkSection;
	import net.minecraft.server.World;

public class SphereCleaner implements Runnable
{
	private SphereWorld plugin;
	private int taskID;
	private org.bukkit.World world;
	private World defaultWorld;
	private SphereWorldConfig config = new SphereWorldConfig();
	private List<String> chunkQueue;
	private int air			= config.airBlockId;
	private int water		= config.noWater ? air : 9;
	private int glass		= config.glassBlockId;
	private int glassType		= config.glassType;
	private boolean useGlowstone	= config.useGlowstone;
	private boolean chunksRemaining = false;

	private Vector vector = null;

	public SphereCleaner(SphereWorld plugin, org.bukkit.World world)
	{
		this.plugin = plugin;
		taskID = -1;
		this.world = world;
		this.defaultWorld = ((CraftWorld) world).getHandle();
	}

	public void start()
	{
		if(taskID >= 0)
			return;

		BukkitScheduler scheduler = plugin.getServer().getScheduler();
		taskID = scheduler.scheduleSyncDelayedTask(plugin, this, 1);
		return;
	}

	public void run()
	{
		for (int chunkCount = 0; chunkCount < 5; chunkCount++)
		{
			if(!processChunk())
				return;

		}

		softStop();
	}

	private boolean isChunkReady(int chunkX, int chunkZ)
	{
		if(world == null || defaultWorld == null)
			return false;

		byte count = 0;

		for(int x = chunkX - 1; x <= chunkX + 1; x++)
		{
			for(int z = chunkZ - 1; z <= chunkZ + 1; z++)
			{
				int blockX = x * 16;
				int blockZ = z * 16;

				if (!defaultWorld.isLoaded(blockX, 1, blockZ))
					continue;

				count += 1;
			}
		}

		if(count >= 9)
		{
			chunksRemaining = false;
			return true;
		}
		
		if(chunksRemaining)
			return true;

		return false;
	}

	public boolean processChunk()
	{
		int chunkX = 0;
		int chunkZ = 0;

		String chunkName = "";

		if(plugin.chunkQueue.getChunkList().isEmpty())
		{
			softStop();
			return false;
		}

		try
		{
			boolean ready = false;

			for(int i = 0; i < plugin.chunkQueue.getChunkList().size(); i++)
			{
				chunkName = plugin.chunkQueue.getChunkList().get(i);
				String[] coords = chunkName.split(":");
				if(coords.length < 2)
				{
					plugin.chunkQueue.removeChunk(chunkName);
					softStop();
					return false;
				}

				chunkX = Integer.parseInt(coords[0]);
				chunkZ = Integer.parseInt(coords[1]);

				ready = isChunkReady(chunkX, chunkZ);

				if(ready)
					break;
			}

			if(!ready)
			{
				softStop();
				chunksRemaining = true;
				return false;
			}
		}
		catch(Exception e)
		{
			String message = "Unable to clean the current chunk. The chunk queue file ";
			message += "may be corrupt.";
			plugin.log.info(message);
			stop();
			return false;
		}

		final org.bukkit.Chunk chunk = world.getChunkAt(chunkX, chunkZ);

		Spheres spheres = new Spheres();
		spheres.getSphereList().clear();

		ArrayList<Vector> villages = new ArrayList<Vector>();
		boolean villageFound = detVillages(villages, chunkX, chunkZ);

		Vector stronghold = new Vector();
		boolean strongholdFound = detStrongholds(stronghold, chunkX, chunkZ);

		protTemples(spheres, chunkX, chunkZ);

		int baseX = (chunkX * 16) - ((chunkX * 16) % 20);
		int baseZ = (chunkZ * 16) - ((chunkZ * 16) % 20);

		int xMin = baseX - (config.maxRadius + (20 - (config.maxRadius % 20)));
		int zMin = baseZ - (config.maxRadius + (20 - (config.maxRadius % 20)));
		int xMax = baseX + (config.maxRadius + (20 - (config.maxRadius % 20))) + 20;
		int zMax = baseZ + (config.maxRadius + (20 - (config.maxRadius % 20))) + 20;

		int offset = 0;

		if(strongholdFound && stronghold != null)
			offset = 60;

		if(villageFound && !villages.isEmpty())
			offset = 60;

		for(int x = xMin - offset; x < xMax + offset; x += 20)
		{
			for(int z = zMin - offset; z < zMax + offset; z += 20)
			{
				if(strongholdFound && stronghold != null)
					protStrongholds(stronghold, spheres, x, z);

				if(villageFound && !villages.isEmpty())
					protVillages(villages, spheres, x, z);

				if(x < xMin || x >= xMax)
					continue;

				if(z < zMin || z >= zMax)
					continue;

				generateSpheres(spheres, x, z);
				generateOres(spheres, x, z);
			}
		}

		Chunk defaultChunk = defaultWorld.getChunkAt(chunkX, chunkZ);
		ChunkSection[] blockData = defaultChunk.i();

		for(int z = 0; z < 16; ++z)
		{
			for(int x = 0; x < 16; ++x)
			{
				for(int y = world.getMaxHeight() - 1; y >= 0; --y)
				{
					if(cleanChunk(blockData, spheres, x, y, z, chunkX, chunkZ))
						defaultChunk.f(x, y, z);
				}
			}
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
		{
			String message = "Sphere chunk at " + chunkX + ", " + chunkZ;
			message += " has been cleaned.";
			plugin.log.info(message);
		}

		return true;
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

		chunksRemaining = false;
		plugin.log.info("Finished cleaning the current chunks.");
		return;
	}

	
	private Random getRandom(int x, int z)
	{
		long seed = (x * 341873128712L + z * 132897987541L) ^ config.sphereSeed;
		return new Random(seed);
	}

	private void protStrongholds(Vector stronghold, Spheres spheres, int x, int z)
	{
		if(stronghold == null || !config.preserveStrongholds || spheres == null)
			return;

		Random random = getRandom(x, z);

		int xPos = x + random.nextInt(20);
		int zPos = z + random.nextInt(20);
		Vector currentVector = new Vector(xPos, 10, zPos);

		if(stronghold.distance(currentVector) > 48)
			return;

		Sphere newSphere = new Sphere();

		newSphere.setSize(48);
		newSphere.setWorld(config.world);
		newSphere.setX(xPos);
		newSphere.setY(50);
		newSphere.setZ(zPos);
		newSphere.setProtectedStructure(true);

		spheres.addSphereToList(newSphere);
	}

	private void protVillages(List<Vector> villages, Spheres spheres, int x, int z)
	{
		if(villages == null || !config.preserveVillages || spheres == null)
			return;

		Random random = getRandom(x, z);

		int xPos = x + random.nextInt(20);
		int zPos = z + random.nextInt(20);
		Vector currentVector = new Vector(xPos, 64, zPos);

		for(Vector village : villages)
		{
			if(village == null)
				continue;

			if(village.distance(currentVector) <= 80)
			{
				Sphere newSphere = new Sphere();

				newSphere.setSize(48);
				newSphere.setWorld(config.world);
				newSphere.setX(xPos);
				newSphere.setY(64);
				newSphere.setZ(zPos);
				newSphere.setProtectedStructure(true);

				spheres.addSphereToList(newSphere);
				return;
			}
		}
	}

	private void protTemples(Spheres spheres, int chunkX, int chunkZ)
	{
		if(!config.preserveTemples || spheres == null)
			return;

		for(int x = chunkX - 8; x <= chunkX + 8; x++)
		{
			for(int z = chunkZ - 8; z <= chunkZ + 8; z++)
			{
				if(!plugin.canSpawnTemple(x, z))
					continue;

				Sphere newSphere = new Sphere();

				newSphere.setSize(48);
				newSphere.setWorld(config.world);
				newSphere.setX((x * 16) + 8);
				newSphere.setY(64);
				newSphere.setZ((z * 16) + 8);
				newSphere.setProtectedStructure(true);

				spheres.addSphereToList(newSphere);
			}
		}
	}

	private void generateSpheres(Spheres spheres, int x, int z)
	{
		if(spheres == null)
			return;

		Random random = getRandom(x, z);

		if(random.nextInt(100) > 5)
			return;

		if(random.nextInt(100) > config.sphereChance)
			return;

		int height = random.nextInt(config.maxHeight - config.minHeight);
		height += config.minHeight;
		int radius = random.nextInt(config.maxRadius - config.minRadius);
		radius += config.minRadius;

		Sphere newSphere = new Sphere();

		newSphere.setSize(radius);
		newSphere.setWorld(config.world);
		newSphere.setX(x + random.nextInt(20));
		newSphere.setY(height);
		newSphere.setZ(z + random.nextInt(20));

		spheres.addSphereToList(newSphere);
	}

	private void generateOres(Spheres spheres, int x, int z)
	{
		if(spheres == null)
			return;

		Random random = getRandom(x, z);

		if(random.nextInt(100) > 90 || random.nextInt(200) > 15)
			return;

		int ore = random.nextInt(15);

		try
		{
			if(!config.useOreBubbles.get(ore))
				return;

			if(random.nextInt(100) > config.oreSpawnChance.get(ore))
				return;

			int baseHeight = config.maxOreHeight.get(ore);
			baseHeight -= config.minOreHeight.get(ore);
			int height = random.nextInt(baseHeight) + config.minOreHeight.get(ore);

			int baseRadius = config.maxOreRadius.get(ore);
			baseRadius -= config.minOreRadius.get(ore);
			int radius = random.nextInt(baseRadius) + config.minOreRadius.get(ore);

			int oreId = config.oreBlockId.get(ore);

			Sphere newSphere = new Sphere();

			newSphere.setSize(radius);
			newSphere.setWorld(config.world);
			newSphere.setX(x + random.nextInt(20));
			newSphere.setY(height);
			newSphere.setZ(z + random.nextInt(20));
			newSphere.setOreSphere(true);
			newSphere.setOreId(oreId);
			newSphere.setOreShellId(config.oreShellId.get(ore));

			spheres.addSphereToList(newSphere);
		}
		catch(Exception e) {}
	}

	private boolean detStrongholds(Vector stronghold, int chunkX, int chunkZ)
	{
		if(!config.preserveStrongholds || stronghold == null)
		{
			stronghold = null;
			return false;
		}

		if(plugin.strongholdCoords[0] == null && config.preserveStrongholds)
			plugin.canSpawnStronghold(0, 0);

		for(int i = 0; i < plugin.strongholdCoords.length; i++)
		{
			if(plugin.strongholdCoords[i] == null)
				continue;

			int xPos = plugin.strongholdCoords[i].x;
			int zPos = plugin.strongholdCoords[i].z;

			double xPow = Math.pow((xPos - chunkX), 2);
			double zPow = Math.pow((zPos - chunkZ), 2);

			if(Math.sqrt(xPow + zPow) > 5)
				continue;

			stronghold.setX((xPos * 16) + 8);
			stronghold.setY(10);
			stronghold.setZ((zPos * 16) + 8);
			return true;
		}

		return false;
	}

	private boolean detVillages(ArrayList<Vector> villages, int chunkX, int chunkZ)
	{
		if(!config.preserveVillages || villages == null)
			return false;

		boolean villageFound = false;

		for(int x = chunkX - 8; x <= chunkX + 8; x++)
		{
			for(int z = chunkZ - 8; z <= chunkZ + 8; z++)
			{
				if(!plugin.canSpawnVillage(x, z))
					continue;

				villageFound = true;
				villages.add(new Vector((x * 16) + 8, 64, (z * 16) + 8));
			}
		}

		if(villageFound && !villages.isEmpty())
			return true;

		return false;
	}

	private boolean cleanChunk(
		ChunkSection[] blockData,
		Spheres spheres,
		int x,
		int y,
		int z,
		int chunkX,
		int chunkZ)
	{
		if(blockData == null || spheres == null)
			return false;

		boolean hasSphere = !spheres.getSphereList().isEmpty();

		final int sectionIndex = (y >> 4);

		if(blockData[sectionIndex] == null)
			blockData[sectionIndex] = new ChunkSection((sectionIndex) << 4);

		final int blockId = blockData[sectionIndex].a(x, y & 15, z);

		if(y == 1 && !config.noFloorSpawn && blockId != water)
		{
			blockData[sectionIndex].a(x, y & 15, z, water);
			return true;
		}
		else if(y == 0 && !config.noFloorSpawn && blockId != 7)
		{
			blockData[sectionIndex].a(x, y & 15, z, 7);
			return true;
		}
		else if(y <= 1 && blockId != air)
		{
			blockData[sectionIndex].a(x, y & 15, z, air);
			return true;
		}

		if(!hasSphere && blockId != air)
		{
			blockData[sectionIndex].a(x, y & 15, z, air);
			return true;
		}

		ArrayList<Boolean> edge = new ArrayList<Boolean>();

		int blockX = (chunkX * 16) + x;
		int blockY = y;
		int blockZ = (chunkZ * 16) + z;

		Vector blockVector = new Vector(blockX, blockY, blockZ);

		boolean keep = false;
		boolean glowstone = false;
		int shell = 0;
		int ore = 0;
		boolean protectedStructure = false;

		for(Sphere sphere : spheres.getSphereList())
		{
			double distance = sphere.getVector().distance(blockVector);

			if(distance >= sphere.getSize())
				continue;

			keep = true;

			if(sphere.isOreSphere())
			{
				edge.add(true);
				shell = sphere.getOreShellId();
				ore = sphere.getOreId();
			}

			if(glassType == 0 && (blockId == 9 || blockId == 11))
				edge.add(true);

			if(glassType == 0 && y < 64 && blockId == 0)
				edge.add(true);

			if(glassType == 0 || (glassType == 2 && y >= 64))
				continue;

			if(distance <= sphere.getSize() - 1.1)
			{
				edge.add(false);
				continue;
			}

			if(sphere.getX() == blockX || sphere.getZ() == blockZ)
				glowstone = true;

			edge.add(true);
		}

		if (!keep && edge.isEmpty() && blockId != air)
		{
			blockData[sectionIndex].a(x, y & 15, z, air);
			return true;
		}
		else if(!edge.isEmpty() && !edge.contains(false))
		{
			if(shell != 0)
			{
				blockData[sectionIndex].a(x, y & 15, z, shell);
				return true;
			}

			if (!useGlowstone && blockId != glass)
			{
				blockData[sectionIndex].a(x, y & 15, z, glass);
				return true;
			}

			if(useGlowstone && glowstone && blockId != 89)
			{
				blockData[sectionIndex].a(x, y & 15, z, 89);
				return true;
			}

			if(blockId == glass)
				return false;

			blockData[sectionIndex].a(x, y & 15, z, glass);
			return true;
		}

		if(keep && ore != 0)
		{
			blockData[sectionIndex].a(x, y & 15, z, ore);
			return true;
		}

		if (!keep && blockId != air)
		{
			blockData[sectionIndex].a(x, y & 15, z, air);
			return true;
		}

		return false;
	}
}