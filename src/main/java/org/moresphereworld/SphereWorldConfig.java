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
	import java.util.ArrayList;
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
	public static int airBlockId;
	public static boolean noWater;
	public static boolean noFloorSpawn;
	public static boolean verboseOutput;
	public static World.Environment worldEnvironment;
	public static int oreShellId;
	public static ArrayList<Boolean> useOreBubbles	= new ArrayList<Boolean>();
	public static ArrayList<Integer> minOreRadius	= new ArrayList<Integer>();
	public static ArrayList<Integer> maxOreRadius	= new ArrayList<Integer>();
	public static ArrayList<Integer> oreSpawnChance	= new ArrayList<Integer>();
	public static ArrayList<Integer> oreBlockId	= new ArrayList<Integer>();
	public static boolean preserveStrongholds;
	public static boolean preserveVillages;

	public static void initialize(FileConfiguration config)
	{
		world			= config.getString("WorldName", "Spheres");
		minHeight		= config.getInt("MinimumHeight", 34);
		maxHeight		= config.getInt("MaximumHeight", 96);
		minRadius		= config.getInt("MinimumRadius", 8);
		maxRadius		= config.getInt("MaximumRadius", 32);
		glassType		= config.getInt("GlassType", 1);
		useGlowstone		= config.getBoolean("UseGlowstone", false);
		sphereChance		= config.getInt("SphereSpawnChance", 80);
		worldSeed		= config.getLong("WorldSeed", 12345);
		sphereSeed		= config.getInt("SphereSeed", 12345);
		glassBlockId		= config.getInt("GlassBlockID", 20);
		airBlockId		= config.getInt("AirBlockID", 0);
		noFloorSpawn		= !config.getBoolean("UseWorldFloor", false);
		noWater			= !config.getBoolean("FloorUsesWater", false);
		verboseOutput		= config.getBoolean("VerboseConsoleOutput", false);
		worldEnvironment	= World.Environment.valueOf(config.getString("WorldType").toUpperCase());
		preserveStrongholds	= config.getBoolean("PreserveStrongholds", true);
		preserveVillages	= config.getBoolean("PreserveVillages", true);
		oreShellId		= config.getInt("OreBubbles.ShellBlockID", 1);
		useOreBubbles.add(config.getBoolean("OreBubbles.Gold.SpawnBubbles", true));
		useOreBubbles.add(config.getBoolean("OreBubbles.Iron.SpawnBubbles", true));
		useOreBubbles.add(config.getBoolean("OreBubbles.Coal.SpawnBubbles", true));
		useOreBubbles.add(config.getBoolean("OreBubbles.Lapis.SpawnBubbles", true));
		useOreBubbles.add(config.getBoolean("OreBubbles.Diamond.SpawnBubbles", true));
		useOreBubbles.add(config.getBoolean("OreBubbles.Redstone.SpawnBubbles", true));
		useOreBubbles.add(config.getBoolean("OreBubbles.Emerald.SpawnBubbles", false));
		useOreBubbles.add(config.getBoolean("OreBubbles.Netherrack.SpawnBubbles", true));
		useOreBubbles.add(config.getBoolean("OreBubbles.SoulSand.SpawnBubbles", true));
		useOreBubbles.add(config.getBoolean("OreBubbles.Glowstone.SpawnBubbles", true));
		useOreBubbles.add(config.getBoolean("OreBubbles.EndStone.SpawnBubbles", true));
		minOreRadius.add(config.getInt("OreBubbles.Gold.MinimumRadius", 4));
		minOreRadius.add(config.getInt("OreBubbles.Iron.MinimumRadius", 6));
		minOreRadius.add(config.getInt("OreBubbles.Coal.MinimumRadius", 10));
		minOreRadius.add(config.getInt("OreBubbles.Lapis.MinimumRadius", 5));
		minOreRadius.add(config.getInt("OreBubbles.Diamond.MinimumRadius", 4));
		minOreRadius.add(config.getInt("OreBubbles.Redstone.MinimumRadius", 8));
		minOreRadius.add(config.getInt("OreBubbles.Emerald.MinimumRadius", 4));
		minOreRadius.add(config.getInt("OreBubbles.Netherrack.MinimumRadius", 10));
		minOreRadius.add(config.getInt("OreBubbles.SoulSand.MinimumRadius", 10));
		minOreRadius.add(config.getInt("OreBubbles.Glowstone.MinimumRadius", 10));
		minOreRadius.add(config.getInt("OreBubbles.EndStone.MinimumRadius", 10));
		maxOreRadius.add(config.getInt("OreBubbles.Gold.MaximumRadius", 8));
		maxOreRadius.add(config.getInt("OreBubbles.Iron.MaximumRadius", 10));
		maxOreRadius.add(config.getInt("OreBubbles.Coal.MaximumRadius", 14));
		maxOreRadius.add(config.getInt("OreBubbles.Lapis.MaximumRadius", 8));
		maxOreRadius.add(config.getInt("OreBubbles.Diamond.MaximumRadius", 8));
		maxOreRadius.add(config.getInt("OreBubbles.Redstone.MaximumRadius", 12));
		maxOreRadius.add(config.getInt("OreBubbles.Emerald.MaximumRadius", 8));
		maxOreRadius.add(config.getInt("OreBubbles.Netherrack.MaximumRadius", 16));
		maxOreRadius.add(config.getInt("OreBubbles.SoulSand.MaximumRadius", 16));
		maxOreRadius.add(config.getInt("OreBubbles.Glowstone.MaximumRadius", 16));
		maxOreRadius.add(config.getInt("OreBubbles.EndStone.MaximumRadius", 16));
		oreSpawnChance.add(config.getInt("OreBubbles.Gold.SpawnChance", 35));
		oreSpawnChance.add(config.getInt("OreBubbles.Iron.SpawnChance", 80));
		oreSpawnChance.add(config.getInt("OreBubbles.Coal.SpawnChance", 80));
		oreSpawnChance.add(config.getInt("OreBubbles.Lapis.SpawnChance", 50));
		oreSpawnChance.add(config.getInt("OreBubbles.Diamond.SpawnChance", 35));
		oreSpawnChance.add(config.getInt("OreBubbles.Redstone.SpawnChance", 50));
		oreSpawnChance.add(config.getInt("OreBubbles.Emerald.SpawnChance", 25));
		oreSpawnChance.add(config.getInt("OreBubbles.Netherrack.SpawnChance", 45));
		oreSpawnChance.add(config.getInt("OreBubbles.SoulSand.SpawnChance", 45));
		oreSpawnChance.add(config.getInt("OreBubbles.Glowstone.SpawnChance", 50));
		oreSpawnChance.add(config.getInt("OreBubbles.EndStone.SpawnChance", 35));
		oreBlockId.add(14);
		oreBlockId.add(15);
		oreBlockId.add(16);
		oreBlockId.add(21);
		oreBlockId.add(56);
		oreBlockId.add(73);
		oreBlockId.add(129);
		oreBlockId.add(87);
		oreBlockId.add(88);
		oreBlockId.add(89);
		oreBlockId.add(121);
	}
}