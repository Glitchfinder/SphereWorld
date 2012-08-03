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
	public static ArrayList<Boolean> useOreBubbles	= new ArrayList<Boolean>();
	public static ArrayList<Integer> minOreHeight	= new ArrayList<Integer>();
	public static ArrayList<Integer> maxOreHeight	= new ArrayList<Integer>();
	public static ArrayList<Integer> minOreRadius	= new ArrayList<Integer>();
	public static ArrayList<Integer> maxOreRadius	= new ArrayList<Integer>();
	public static ArrayList<Integer> oreSpawnChance	= new ArrayList<Integer>();
	public static ArrayList<Integer> oreBlockId	= new ArrayList<Integer>();
	public static ArrayList<Integer> oreShellId	= new ArrayList<Integer>();
	public static boolean preserveStrongholds;
	public static boolean preserveVillages;
	public static boolean preserveTemples;

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
		String environmentName	= config.getString("WorldType");
		worldEnvironment	= World.Environment.valueOf(environmentName.toUpperCase());
		preserveStrongholds	= config.getBoolean("PreserveStrongholds", true);
		preserveVillages	= config.getBoolean("PreserveVillages", true);
		preserveTemples		= config.getBoolean("PreserveTemples", true);
		String ore 		= "OreBubbles.";
		useOreBubbles.add(config.getBoolean(ore + "Gold.SpawnBubbles", true));
		useOreBubbles.add(config.getBoolean(ore + "Iron.SpawnBubbles", true));
		useOreBubbles.add(config.getBoolean(ore + "Coal.SpawnBubbles", true));
		useOreBubbles.add(config.getBoolean(ore + "Lapis.SpawnBubbles", true));
		useOreBubbles.add(config.getBoolean(ore + "Diamond.SpawnBubbles", true));
		useOreBubbles.add(config.getBoolean(ore + "Redstone.SpawnBubbles", true));
		useOreBubbles.add(config.getBoolean(ore + "Emerald.SpawnBubbles", false));
		useOreBubbles.add(config.getBoolean(ore + "Netherrack.SpawnBubbles", true));
		useOreBubbles.add(config.getBoolean(ore + "SoulSand.SpawnBubbles", true));
		useOreBubbles.add(config.getBoolean(ore + "Glowstone.SpawnBubbles", true));
		useOreBubbles.add(config.getBoolean(ore + "EndStone.SpawnBubbles", true));
		useOreBubbles.add(config.getBoolean(ore + "Sand.SpawnBubbles", true));
		useOreBubbles.add(config.getBoolean(ore + "Clay.SpawnBubbles", true));
		useOreBubbles.add(config.getBoolean(ore + "MossStone.SpawnBubbles", true));
		useOreBubbles.add(config.getBoolean(ore + "Obsidian.SpawnBubbles", true));
		minOreHeight.add(config.getInt(ore + "Gold.MinimumHeight", 34));
		minOreHeight.add(config.getInt(ore + "Iron.MinimumHeight", 34));
		minOreHeight.add(config.getInt(ore + "Coal.MinimumHeight", 34));
		minOreHeight.add(config.getInt(ore + "Lapis.MinimumHeight", 34));
		minOreHeight.add(config.getInt(ore + "Diamond.MinimumHeight", 34));
		minOreHeight.add(config.getInt(ore + "Redstone.MinimumHeight", 34));
		minOreHeight.add(config.getInt(ore + "Emerald.MinimumHeight", 34));
		minOreHeight.add(config.getInt(ore + "Netherrack.MinimumHeight", 34));
		minOreHeight.add(config.getInt(ore + "SoulSand.MinimumHeight", 34));
		minOreHeight.add(config.getInt(ore + "Glowstone.MinimumHeight", 34));
		minOreHeight.add(config.getInt(ore + "EndStone.MinimumHeight", 34));
		minOreHeight.add(config.getInt(ore + "Sand.MinimumHeight", 34));
		minOreHeight.add(config.getInt(ore + "Clay.MinimumHeight", 34));
		minOreHeight.add(config.getInt(ore + "MossStone.MinimumHeight", 34));
		minOreHeight.add(config.getInt(ore + "Obsidian.MinimumHeight", 34));
		maxOreHeight.add(config.getInt(ore + "Gold.MaximumHeight", 96));
		maxOreHeight.add(config.getInt(ore + "Iron.MaximumHeight", 96));
		maxOreHeight.add(config.getInt(ore + "Coal.MaximumHeight", 96));
		maxOreHeight.add(config.getInt(ore + "Lapis.MaximumHeight", 96));
		maxOreHeight.add(config.getInt(ore + "Diamond.MaximumHeight", 96));
		maxOreHeight.add(config.getInt(ore + "Redstone.MaximumHeight", 96));
		maxOreHeight.add(config.getInt(ore + "Emerald.MaximumHeight", 96));
		maxOreHeight.add(config.getInt(ore + "Netherrack.MaximumHeight", 96));
		maxOreHeight.add(config.getInt(ore + "SoulSand.MaximumHeight", 96));
		maxOreHeight.add(config.getInt(ore + "Glowstone.MaximumHeight", 96));
		maxOreHeight.add(config.getInt(ore + "EndStone.MaximumHeight", 96));
		maxOreHeight.add(config.getInt(ore + "Sand.MaximumHeight", 96));
		maxOreHeight.add(config.getInt(ore + "Clay.MaximumHeight", 96));
		maxOreHeight.add(config.getInt(ore + "MossStone.MaximumHeight", 96));
		maxOreHeight.add(config.getInt(ore + "Obsidian.MaximumHeight", 96));
		minOreRadius.add(config.getInt(ore + "Gold.MinimumRadius", 4));
		minOreRadius.add(config.getInt(ore + "Iron.MinimumRadius", 6));
		minOreRadius.add(config.getInt(ore + "Coal.MinimumRadius", 10));
		minOreRadius.add(config.getInt(ore + "Lapis.MinimumRadius", 5));
		minOreRadius.add(config.getInt(ore + "Diamond.MinimumRadius", 4));
		minOreRadius.add(config.getInt(ore + "Redstone.MinimumRadius", 8));
		minOreRadius.add(config.getInt(ore + "Emerald.MinimumRadius", 4));
		minOreRadius.add(config.getInt(ore + "Netherrack.MinimumRadius", 10));
		minOreRadius.add(config.getInt(ore + "SoulSand.MinimumRadius", 10));
		minOreRadius.add(config.getInt(ore + "Glowstone.MinimumRadius", 10));
		minOreRadius.add(config.getInt(ore + "EndStone.MinimumRadius", 10));
		minOreRadius.add(config.getInt(ore + "Sand.MinimumRadius", 8));
		minOreRadius.add(config.getInt(ore + "Clay.MinimumRadius", 8));
		minOreRadius.add(config.getInt(ore + "MossStone.MinimumRadius", 6));
		minOreRadius.add(config.getInt(ore + "Obsidian.MinimumRadius", 6));
		maxOreRadius.add(config.getInt(ore + "Gold.MaximumRadius", 8));
		maxOreRadius.add(config.getInt(ore + "Iron.MaximumRadius", 10));
		maxOreRadius.add(config.getInt(ore + "Coal.MaximumRadius", 14));
		maxOreRadius.add(config.getInt(ore + "Lapis.MaximumRadius", 8));
		maxOreRadius.add(config.getInt(ore + "Diamond.MaximumRadius", 8));
		maxOreRadius.add(config.getInt(ore + "Redstone.MaximumRadius", 12));
		maxOreRadius.add(config.getInt(ore + "Emerald.MaximumRadius", 8));
		maxOreRadius.add(config.getInt(ore + "Netherrack.MaximumRadius", 16));
		maxOreRadius.add(config.getInt(ore + "SoulSand.MaximumRadius", 16));
		maxOreRadius.add(config.getInt(ore + "Glowstone.MaximumRadius", 16));
		maxOreRadius.add(config.getInt(ore + "EndStone.MaximumRadius", 16));
		maxOreRadius.add(config.getInt(ore + "Sand.MaximumRadius", 12));
		maxOreRadius.add(config.getInt(ore + "Clay.MaximumRadius", 12));
		maxOreRadius.add(config.getInt(ore + "MossStone.MaximumRadius", 10));
		maxOreRadius.add(config.getInt(ore + "Obsidian.MaximumRadius", 10));
		oreSpawnChance.add(config.getInt(ore + "Gold.SpawnChance", 35));
		oreSpawnChance.add(config.getInt(ore + "Iron.SpawnChance", 80));
		oreSpawnChance.add(config.getInt(ore + "Coal.SpawnChance", 80));
		oreSpawnChance.add(config.getInt(ore + "Lapis.SpawnChance", 50));
		oreSpawnChance.add(config.getInt(ore + "Diamond.SpawnChance", 35));
		oreSpawnChance.add(config.getInt(ore + "Redstone.SpawnChance", 50));
		oreSpawnChance.add(config.getInt(ore + "Emerald.SpawnChance", 25));
		oreSpawnChance.add(config.getInt(ore + "Netherrack.SpawnChance", 45));
		oreSpawnChance.add(config.getInt(ore + "SoulSand.SpawnChance", 45));
		oreSpawnChance.add(config.getInt(ore + "Glowstone.SpawnChance", 50));
		oreSpawnChance.add(config.getInt(ore + "EndStone.SpawnChance", 35));
		oreSpawnChance.add(config.getInt(ore + "Sand.SpawnChance", 40));
		oreSpawnChance.add(config.getInt(ore + "Clay.SpawnChance", 45));
		oreSpawnChance.add(config.getInt(ore + "MossStone.SpawnChance", 40));
		oreSpawnChance.add(config.getInt(ore + "Obsidian.SpawnChance", 35));
		oreShellId.add(config.getInt(ore + "Gold.ShellBlockID", 1));
		oreShellId.add(config.getInt(ore + "Iron.ShellBlockID", 1));
		oreShellId.add(config.getInt(ore + "Coal.ShellBlockID", 1));
		oreShellId.add(config.getInt(ore + "Lapis.ShellBlockID", 1));
		oreShellId.add(config.getInt(ore + "Diamond.ShellBlockID", 1));
		oreShellId.add(config.getInt(ore + "Redstone.ShellBlockID", 1));
		oreShellId.add(config.getInt(ore + "Emerald.ShellBlockID", 1));
		oreShellId.add(config.getInt(ore + "Netherrack.ShellBlockID", 1));
		oreShellId.add(config.getInt(ore + "SoulSand.ShellBlockID", 1));
		oreShellId.add(config.getInt(ore + "Glowstone.ShellBlockID", 1));
		oreShellId.add(config.getInt(ore + "EndStone.ShellBlockID", 1));
		oreShellId.add(config.getInt(ore + "Sand.ShellBlockID", 1));
		oreShellId.add(config.getInt(ore + "Clay.ShellBlockID", 1));
		oreShellId.add(config.getInt(ore + "MossStone.ShellBlockID", 1));
		oreShellId.add(config.getInt(ore + "Obsidian.ShellBlockID", 1));
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
		oreBlockId.add(12);
		oreBlockId.add(82);
		oreBlockId.add(48);
		oreBlockId.add(49);
	}
}