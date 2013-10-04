package com.censoredsoftware.teachjava.util;

import com.censoredsoftware.teachjava.TeachJava;
import org.bukkit.Location;
import org.bukkit.World;

public class Zones
{
	public static boolean inNoTeachJavaZone(Location location)
	{
		return isNoTeachJavaWorld(location.getWorld());
	}

	public static boolean isNoTeachJavaWorld(World world)
	{
		return TeachJava.DISABLED_WORLDS.contains(world.getName());
	}
}
