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
	//* NOT NEEDED
//* IMPORTS: BUKKIT
	import org.bukkit.configuration.file.FileConfiguration;
	import org.bukkit.World;
//* IMPORTS: SPOUT
	//* NOT NEEDED
//* IMPORTS: OTHER
	//* NOT NEEDED

public class SphereWorldConfig
{

	public static String world;
	public static int minHeight;
	public static int maxHeight;
	public static int minRadius;
	public static int maxRadius;
	public static int glassType;
	public static boolean useGlowstone;
	public static int sphereChance;
	public static long worldSeed;
	public static int sphereSeed;
	public static int glassBlockId;
	public static boolean noWater;
	public static boolean noFloorSpawn;
	public static boolean verboseOutput;
	public static World.Environment worldEnvironment;

	public static void initialize(FileConfiguration config)
	{
		world				= config.getString("WorldName", "Spheres");
		minHeight			= config.getInt("MinimumHeight", 34);
		maxHeight			= config.getInt("MaximumHeight", 96);
		minRadius			= config.getInt("MinimumRadius", 8);
		maxRadius			= config.getInt("MaximumRadius", 32);
		glassType			= config.getInt("GlassType", 1);
		useGlowstone		= config.getBoolean("UseGlowstone", false);
		sphereChance		= config.getInt("SphereSpawnChance", 80);
		worldSeed			= config.getLong("WorldSeed", 12345);
		sphereSeed			= config.getInt("SphereSeed", 12345);
		glassBlockId		= config.getInt("GlassBlockID", 20);
		noFloorSpawn		= !config.getBoolean("UseWorldFloor", false);
		noWater				= !config.getBoolean("FloorUsesWater", false);
		verboseOutput		= config.getBoolean("VerboseConsoleOutput", false);
		worldEnvironment	= World.Environment.valueOf(config.getString("WorldType").toUpperCase());
	}
}