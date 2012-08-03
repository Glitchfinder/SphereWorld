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
	import java.util.Map;
//* IMPORTS: BUKKIT
	import org.bukkit.configuration.Configuration;
	import org.bukkit.configuration.ConfigurationSection;
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

	public static void initialize(Configuration config)
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
		String ore 		= "Meteors";

		ConfigurationSection oreSection = config.getConfigurationSection(ore);
		Map<String, Object>	oreValues = oreSection.getValues(false);

		if(oreValues.isEmpty())
			return;

		for(Object key : oreValues.keySet())
		{
			if(!(key instanceof String))
				continue;

			String name = (String) key;
			if(!oreValues.containsKey(name))
				continue;

			Object sectionObject = oreValues.get(name);
			if(!(sectionObject instanceof ConfigurationSection))
				continue;

			ConfigurationSection section = (ConfigurationSection) sectionObject;

			useOreBubbles.add(section.getBoolean("SpawnBubbles", false));
			minOreHeight.add(section.getInt("MinimumHeight", 34));
			maxOreHeight.add(section.getInt("MaximumHeight", 96));
			minOreRadius.add(section.getInt("MinimumRadius", 4));
			maxOreRadius.add(section.getInt("MaximumRadius", 8));
			oreSpawnChance.add(section.getInt("SpawnChance", 35));
			oreShellId.add(section.getInt("ShellBlockID", 1));
			oreBlockId.add(section.getInt("CoreBlockID", 1));
		}
	}
}
