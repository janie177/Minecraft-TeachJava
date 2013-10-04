package com.censoredsoftware.teachjava.location;

import com.censoredsoftware.teachjava.data.DataManager;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.*;

public class TJLocation implements ConfigurationSerializable
{
	private UUID id;
	String world;
	Double X;
	Double Y;
	Double Z;
	Float pitch;
	Float yaw;
	String region;

	public TJLocation()
	{}

	public TJLocation(UUID id, ConfigurationSection conf)
	{
		this.id = id;
		world = conf.getString("world");
		X = conf.getDouble("X");
		Y = conf.getDouble("Y");
		Z = conf.getDouble("Z");
		pitch = Float.parseFloat(conf.getString("pitch"));
		yaw = Float.parseFloat(conf.getString("yaw"));
		region = conf.getString("region");
	}

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("world", world);
		map.put("X", X);
		map.put("Y", Y);
		map.put("Z", Z);
		map.put("pitch", String.valueOf(pitch));
		map.put("yaw", String.valueOf(yaw));
		map.put("region", region);
		return map;
	}

	public void generateId()
	{
		id = UUID.randomUUID();
	}

	void setWorld(String world)
	{
		this.world = world;
	}

	void setX(Double X)
	{
		this.X = X;
	}

	void setY(Double Y)
	{
		this.Y = Y;
	}

	void setZ(Double Z)
	{
		this.Z = Z;
	}

	void setYaw(Float yaw)
	{
		this.yaw = yaw;
	}

	void setPitch(Float pitch)
	{
		this.pitch = pitch;
	}

	void setRegion(Region region)
	{
		this.region = region.toString();
	}

	public Location toLocation() throws NullPointerException
	{
		return new Location(Bukkit.getServer().getWorld(this.world), this.X, this.Y, this.Z, this.yaw, this.pitch);
	}

	public UUID getId()
	{
		return this.id;
	}

	public Double getX()
	{
		return this.X;
	}

	public Double getY()
	{
		return this.Y;
	}

	public Double getZ()
	{
		return this.Z;
	}

	public String getWorld()
	{
		return this.world;
	}

	public Region getRegion()
	{
		return Region.Util.getRegion(toLocation());
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException();
	}

	public static class Util
	{
		public static void save(TJLocation location)
		{
			DataManager.locations.put(location.getId(), location);
		}

		public static void delete(UUID id)
		{
			DataManager.locations.remove(id);
		}

		public static TJLocation load(UUID id)
		{
			return DataManager.locations.get(id);
		}

		public static TJLocation create(String world, double X, double Y, double Z, float yaw, float pitch)
		{
			TJLocation trackedLocation = new TJLocation();
			trackedLocation.generateId();
			trackedLocation.setWorld(world);
			trackedLocation.setX(X);
			trackedLocation.setY(Y);
			trackedLocation.setZ(Z);
			trackedLocation.setYaw(yaw);
			trackedLocation.setPitch(pitch);
			trackedLocation.setRegion(Region.Util.getRegion((int) X, (int) Z, world));
			save(trackedLocation);
			return trackedLocation;
		}

		public static TJLocation create(Location location)
		{
			return create(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
		}

		public static Set<TJLocation> loadAll()
		{
			return Sets.newHashSet(DataManager.locations.values());
		}

		public static TJLocation get(final Location location)
		{
			try
			{
				return Iterators.find(loadAll().iterator(), new Predicate<TJLocation>()
				{
					@Override
					public boolean apply(TJLocation tracked)
					{
						return location.getX() == tracked.getX() && location.getY() == tracked.getY() && location.getBlockZ() == tracked.getZ() && location.getWorld().getName().equals(tracked.getWorld());
					}
				});
			}
			catch(NoSuchElementException ignored)
			{}
			return create(location);
		}

		/**
		 * Returns a set of blocks in a radius of <code>radius</code> at the provided <code>location</code>.
		 * 
		 * @param location the center location to getDesign the blocks from.
		 * @param radius the radius around the center block from which to getDesign the blocks.
		 * @return Set<Block>
		 */
		public static Set<Block> getBlocks(Location location, int radius)
		{
			// Define variables
			Set<Block> blocks = Sets.newHashSet();
			blocks.add(location.getBlock());

			for(int x = 0; x <= radius; x++)
				blocks.add(location.add(x, 0, x).getBlock());

			return blocks;
		}

		public static List<Location> getCirclePoints(Location center, final double radius, final int points)
		{
			final World world = center.getWorld();
			final double X = center.getX();
			final double Y = center.getY();
			final double Z = center.getZ();
			List<Location> list = new ArrayList<Location>();
			for(int i = 0; i < points; i++)
			{
				double x = X + radius * Math.cos((2 * Math.PI * i) / points);
				double z = Z + radius * Math.sin((2 * Math.PI * i) / points);
				list.add(new Location(world, x, Y, z));
			}
			return list;
		}

		public static double distanceFlat(Location location1, Location location2)
		{
			if(!location1.getWorld().equals(location2.getWorld())) return 99999;
			double Y = location1.getY();
			Location location3 = location2.clone();
			location3.setY(Y);
			return location1.distance(location3);
		}
	}
}
