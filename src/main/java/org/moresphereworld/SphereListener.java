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
	import java.lang.String;
//* IMPORTS: BUKKIT
	import org.bukkit.Chunk;
	import org.bukkit.craftbukkit.CraftWorld;
	import org.bukkit.event.EventHandler;
	import org.bukkit.event.EventPriority;
	import org.bukkit.event.Listener;
	import org.bukkit.event.world.ChunkPopulateEvent;
	import org.bukkit.event.world.WorldInitEvent;
	import org.bukkit.plugin.PluginManager;
	import org.bukkit.World;
//* IMPORTS: SPOUT
	//* NOT NEEDED
//* IMPORTS: OTHER
	//* NOT NEEDED

public class SphereListener implements Listener
{
	public MoreSphereWorldPlugin plugin;

	public SphereListener(MoreSphereWorldPlugin instance)
	{
		plugin = instance;
	}

	public void register()
	{
		PluginManager manager;

		manager = plugin.getServer().getPluginManager();
		manager.registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onChunkPopulate(ChunkPopulateEvent event)
	{
		if (!event.getChunk().getWorld().getName().equalsIgnoreCase(SphereWorldConfig.world))
			return;

		Chunk chunk = event.getChunk();
		World world = event.getChunk().getWorld();
		net.minecraft.server.World defaultWorld = ((CraftWorld) world).getHandle();

		for (int x = chunk.getX() - 1; x <= chunk.getX() + 1; x++)
		{
			for (int z = chunk.getZ() - 1; z <= chunk.getZ() + 1; z++)
			{
				if (!defaultWorld.isLoaded(x * 16, 1, z * 16))
					continue;

				Chunk currentChunk = world.getChunkAt(x, z);
				String chunkName = x + ":" + z;
				this.plugin.chunkQueue.addChunk(chunkName);
			}
		}

		if(this.plugin.sphereCleaner == null)
			this.plugin.sphereCleaner = new SphereCleaner(this.plugin, event.getWorld());

		this.plugin.sphereCleaner.start();
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onWorldInit(WorldInitEvent event)
	{
		if (!event.getWorld().getName().equalsIgnoreCase(SphereWorldConfig.world))
			return;

		if(event.getWorld().getPopulators().contains(this.plugin.populator))
			return;

		event.getWorld().getPopulators().add(this.plugin.populator);
	}
}