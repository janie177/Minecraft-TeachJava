package com.censoredsoftware.teachjava.util;

import com.censoredsoftware.teachjava.TeachJava;
import org.bukkit.Location;
import org.bukkit.World;

public class Zones
{
	public static boolean inNoRealityZone(Location location)
	{
		return isNoRealityWorld(location.getWorld());
	}

	public static boolean isNoRealityWorld(World world)
	{
		return TeachJava.DISABLED_WORLDS.contains(world.getName());
	}
}
