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
	import java.io.FileOutputStream;
	import java.io.ObjectOutputStream;
	import java.lang.Runnable;
	import java.lang.String;
//* IMPORTS: BUKKIT
	import org.bukkit.scheduler.BukkitScheduler;
//* IMPORTS: SPOUT
	//* NOT NEEDED
//* IMPORTS: OTHER
	//* NOT NEEDED

public class SaveQueue implements Runnable
{
	private SphereWorld plugin;
	private int taskID;

	public SaveQueue(SphereWorld plugin)
	{
		this.plugin = plugin;
		taskID = -1;
	}

	public void start()
	{
		if(taskID >= 0)
			return;

		BukkitScheduler scheduler = plugin.getServer().getScheduler();
		taskID = scheduler.scheduleSyncDelayedTask(plugin, this, 20);
		return;
	}

	public void run()
	{
		try {
			File chunkQueueFile = new File(plugin.getDataFolder(), "ChunkQueue.bin");
			FileOutputStream fos = new FileOutputStream(chunkQueueFile);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(plugin.chunkQueue);
			out.close();
		} catch (Exception e) {
			String message = "Unable to save the chunk cleaning queue. ";
			message += "It may be corrupt.";
			plugin.log.info(message);
		}

		softStop();
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

		return;
	}
}