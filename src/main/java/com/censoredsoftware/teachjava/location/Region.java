package com.censoredsoftware.teachjava.location;

import com.google.common.base.Objects;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Set;

public class Region
{
	public final static int REGION_LENGTH = 512;
	public final static int HALF_REGION_LENGTH = REGION_LENGTH / 2;

	private final int x;
	private final int z;
	private final String world;

	private Region(int x, int z, String world)
	{
		this.x = x;
		this.z = z;
		this.world = world;
	}

	public int getX()
	{
		return x;
	}

	public int getZ()
	{
		return z;
	}

	public String getWorld()
	{
		return world;
	}

	public Location getCenter()
	{
		World world = Bukkit.getWorld(this.world);
		return new Location(world, x, world.getHighestBlockYAt(x, z), z);
	}

	public Set<Region> getGreaterRegionArea()
	{
		Region[] area = new Region[9];
		area[0] = new Region(x - REGION_LENGTH, z - REGION_LENGTH, world);
		area[1] = new Region(x - REGION_LENGTH, z, world);
		area[2] = new Region(x - REGION_LENGTH, z + REGION_LENGTH, world);
		area[3] = new Region(x, z - REGION_LENGTH, world);
		area[4] = this;
		area[5] = new Region(x, z + REGION_LENGTH, world);
		area[6] = new Region(x + REGION_LENGTH, z - REGION_LENGTH, world);
		area[7] = new Region(x + REGION_LENGTH, z, world);
		area[8] = new Region(x + REGION_LENGTH, z + REGION_LENGTH, world);
		return Sets.newHashSet(area);
	}

	@Override
	public String toString()
	{
		return (new StringBuilder()).append("world:").append(world).append(",").append("x:").append(x).append(",").append("z:").append(z).toString();
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(world, x, z);
	}

	@Override
	public boolean equals(Object object)
	{
		return object instanceof Region && toString().equals(object.toString());
	}

	private static int getRegionCoordinate(int number)
	{
		int temp = number % REGION_LENGTH;
		if(temp >= HALF_REGION_LENGTH) return number + REGION_LENGTH - temp;
		return number - temp;
	}

	public static class Util
	{
		public static Region getRegion(Location location)
		{
			return new Region(getRegionCoordinate(location.getBlockX()), getRegionCoordinate(location.getBlockZ()), location.getWorld().getName());
		}

		public static Region getRegion(int X, int Z, String world)
		{
			return new Region(getRegionCoordinate(X), getRegionCoordinate(Z), world);
		}

		public static Region fromString(String string)
		{
			String[] args = StringUtils.split(string, ",");
			if(args.length == 3)
			{
				String WORLD = args[0].substring(6);
				Integer X = null;
				Integer Z = null;

				try
				{
					X = Integer.parseInt(args[1].substring(2));
					Z = Integer.parseInt(args[2].substring(2));
				}
				catch(Throwable ignored)
				{}

				if(X != null && Z != null) return Util.getRegion(X, Z, WORLD);
			}
			throw new IllegalArgumentException("Not a valid region.");
		}
	}
}
