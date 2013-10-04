package com.censoredsoftware.teachjava.data;

import com.censoredsoftware.teachjava.TeachJava;
import com.censoredsoftware.teachjava.helper.ConfigFile;
import com.censoredsoftware.teachjava.location.TJLocation;
import com.censoredsoftware.teachjava.player.Death;
import com.censoredsoftware.teachjava.player.TJPlayer;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;

public class DataManager
{
	// Data
	public static ConcurrentMap<String, TJPlayer> players;
	public static ConcurrentMap<UUID, TJLocation> locations;
	public static ConcurrentMap<UUID, Death> deaths;
	public static ConcurrentMap<UUID, TimedData> timedData;
	public static ConcurrentMap<UUID, ServerData> serverData;
	private static ConcurrentMap<String, HashMap<String, Object>> tempData;

	static
	{
		for(File file : File.values())
			file.getConfigFile().loadToData();
		tempData = Maps.newConcurrentMap();
	}

	public static void save()
	{
		for(File file : File.values())
			file.getConfigFile().saveToFile();
	}

	public static void flushData()
	{
		// Kick everyone
		for(Player player : Bukkit.getOnlinePlayers())
			player.kickPlayer(ChatColor.GREEN + "Data has been reset, you can rejoin now.");

		// Clear the data
		locations.clear();
		players.clear();
		deaths.clear();
		timedData.clear();
		tempData.clear();
		serverData.clear();

		save();

		// Reload the PLUGIN
		Bukkit.getServer().getPluginManager().disablePlugin(TeachJava.PLUGIN);
		Bukkit.getServer().getPluginManager().enablePlugin(TeachJava.PLUGIN);
	}

	/*
	 * Temporary data
	 */
	public static boolean hasKeyTemp(String key, String subKey)
	{
		return tempData.containsKey(key) && tempData.get(key).containsKey(subKey);
	}

	public static Object getValueTemp(String key, String subKey)
	{
		if(tempData.containsKey(key)) return tempData.get(key).get(subKey);
		else return null;
	}

	public static void saveTemp(String key, String subKey, Object value)
	{
		if(!tempData.containsKey(key)) tempData.put(key, new HashMap<String, Object>());
		tempData.get(key).put(subKey, value);
	}

	public static void removeTemp(String key, String subKey)
	{
		if(tempData.containsKey(key) && tempData.get(key).containsKey(subKey)) tempData.get(key).remove(subKey);
	}

	/*
	 * Timed data
	 */
	public static void saveTimed(String key, String subKey, Object data, Integer seconds)
	{
		// Remove the data if it exists already
		TimedData.Util.remove(key, subKey);

		// Create and save the timed data
		TimedData timedData = new TimedData();
		timedData.generateId();
		timedData.setKey(key);
		timedData.setSubKey(subKey);
		timedData.setData(data.toString());
		timedData.setSeconds(seconds);
		DataManager.timedData.put(timedData.getId(), timedData);
	}

	public static void removeTimed(String key, String subKey)
	{
		TimedData.Util.remove(key, subKey);
	}

	public static boolean hasTimed(String key, String subKey)
	{
		return TimedData.Util.find(key, subKey) != null;
	}

	public static Object getTimedValue(String key, String subKey)
	{
		return TimedData.Util.find(key, subKey).getData();
	}

	public static long getTimedExpiration(String key, String subKey)
	{
		return TimedData.Util.find(key, subKey).getExpiration();
	}

	/*
	 * Server data
	 */
	public static void saveServerData(String key, String subKey, Object data)
	{
		// Remove the data if it exists already
		ServerData.Util.remove(key, subKey);

		// Create and save the timed data
		ServerData serverData = new ServerData();
		serverData.generateId();
		serverData.setKey(key);
		serverData.setSubKey(subKey);
		serverData.setData(data.toString());
		DataManager.serverData.put(serverData.getId(), serverData);
	}

	public static void removeServerData(String key, String subKey)
	{
		ServerData.Util.remove(key, subKey);
	}

	public static boolean hasServerData(String key, String subKey)
	{
		return ServerData.Util.find(key, subKey) != null;
	}

	public static Object getServerDataValue(String key, String subKey)
	{
		return ServerData.Util.find(key, subKey).getData();
	}

	public static enum File
	{
		PLAYER(new ConfigFile<String, TJPlayer>()
		{
			@Override
			public TJPlayer create(String string, ConfigurationSection conf)
			{
				return new TJPlayer(string, conf);
			}

			@Override
			public ConcurrentMap<String, TJPlayer> getLoadedData()
			{
				return DataManager.players;
			}

			@Override
			public String getSavePath()
			{
				return TeachJava.SAVE_PATH;
			}

			@Override
			public String getSaveFile()
			{
				return "players.yml";
			}

			@Override
			public Map<String, Object> serialize(String string)
			{
				return getLoadedData().get(string).serialize();
			}

			@Override
			public String convertFromString(String stringId)
			{
				return stringId;
			}

			@Override
			public void loadToData()
			{
				players = loadFromFile();
			}
		}), LOCATION(new ConfigFile<UUID, TJLocation>()
		{
			@Override
			public TJLocation create(UUID uuid, ConfigurationSection conf)
			{
				return new TJLocation(uuid, conf);
			}

			@Override
			public ConcurrentMap<UUID, TJLocation> getLoadedData()
			{
				return DataManager.locations;
			}

			@Override
			public String getSavePath()
			{
				return TeachJava.SAVE_PATH;
			}

			@Override
			public String getSaveFile()
			{
				return "locations.yml";
			}

			@Override
			public Map<String, Object> serialize(UUID uuid)
			{
				return getLoadedData().get(uuid).serialize();
			}

			@Override
			public UUID convertFromString(String stringId)
			{
				return UUID.fromString(stringId);
			}

			@Override
			public void loadToData()
			{
				locations = loadFromFile();
			}
		}), DEATH(new ConfigFile<UUID, Death>()
		{
			@Override
			public Death create(UUID uuid, ConfigurationSection conf)
			{
				return new Death(uuid, conf);
			}

			@Override
			public ConcurrentMap<UUID, Death> getLoadedData()
			{
				return DataManager.deaths;
			}

			@Override
			public String getSavePath()
			{
				return TeachJava.SAVE_PATH;
			}

			@Override
			public String getSaveFile()
			{
				return "deaths.yml";
			}

			@Override
			public Map<String, Object> serialize(UUID uuid)
			{
				return getLoadedData().get(uuid).serialize();
			}

			@Override
			public UUID convertFromString(String stringId)
			{
				return UUID.fromString(stringId);
			}

			@Override
			public void loadToData()
			{
				deaths = loadFromFile();
			}
		}), TIMED_DATA(new ConfigFile<UUID, TimedData>()
		{
			@Override
			public TimedData create(UUID uuid, ConfigurationSection conf)
			{
				return new TimedData(uuid, conf);
			}

			@Override
			public ConcurrentMap<UUID, TimedData> getLoadedData()
			{
				return DataManager.timedData;
			}

			@Override
			public String getSavePath()
			{
				return TeachJava.SAVE_PATH;
			}

			@Override
			public String getSaveFile()
			{
				return "timeddata.yml";
			}

			@Override
			public Map<String, Object> serialize(UUID uuid)
			{
				return getLoadedData().get(uuid).serialize();
			}

			@Override
			public UUID convertFromString(String stringId)
			{
				return UUID.fromString(stringId);
			}

			@Override
			public void loadToData()
			{
				timedData = loadFromFile();
			}
		}), SERVER_DATA(new ConfigFile<UUID, ServerData>()
		{
			@Override
			public ServerData create(UUID uuid, ConfigurationSection conf)
			{
				return new ServerData(uuid, conf);
			}

			@Override
			public ConcurrentMap<UUID, ServerData> getLoadedData()
			{
				return DataManager.serverData;
			}

			@Override
			public String getSavePath()
			{
				return TeachJava.SAVE_PATH;
			}

			@Override
			public String getSaveFile()
			{
				return "serverdata.yml";
			}

			@Override
			public Map<String, Object> serialize(UUID uuid)
			{
				return getLoadedData().get(uuid).serialize();
			}

			@Override
			public UUID convertFromString(String stringId)
			{
				return UUID.fromString(stringId);
			}

			@Override
			public void loadToData()
			{
				serverData = loadFromFile();
			}
		});

		private ConfigFile save;

		private File(ConfigFile save)
		{
			this.save = save;
		}

		public ConfigFile getConfigFile()
		{
			return save;
		}
	}
}
