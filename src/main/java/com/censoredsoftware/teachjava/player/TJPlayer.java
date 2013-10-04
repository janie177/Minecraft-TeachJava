package com.censoredsoftware.teachjava.player;

import com.censoredsoftware.teachjava.data.DataManager;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.*;

public class TJPlayer implements ConfigurationSerializable
{
	private String player;
	private long lastLoginTime, lastLogoutTime;
	private double lastHealth;
	private Set<String> deaths;

	public TJPlayer()
	{
		deaths = Sets.newHashSet();
	}

	public TJPlayer(String player, ConfigurationSection conf)
	{
		this.player = player;
		lastLoginTime = conf.getLong("lastLoginTime");
		if(conf.isDouble("lastHealth")) lastHealth = conf.getDouble("lastHealth");
		if(conf.isLong("lastLogoutTime")) lastLogoutTime = conf.getLong("lastLogoutTime");
		else lastLogoutTime = 0;
		deaths = Sets.newHashSet(conf.getStringList("deaths"));
	}

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("lastLoginTime", lastLoginTime);
		map.put("lastLogoutTime", lastLogoutTime);
		map.put("lastHealth", lastHealth);
		map.put("deaths", Lists.newArrayList(deaths));
		return map;
	}

	void setPlayer(String player)
	{
		this.player = player;
	}

	public OfflinePlayer getOfflinePlayer()
	{
		return Bukkit.getOfflinePlayer(this.player);
	}

	public void setLastLoginTime(Long time)
	{
		this.lastLoginTime = time;
		Util.save(this);
	}

	public long getLastLoginTime()
	{
		return this.lastLoginTime;
	}

	public void setLastLogoutTime(long time)
	{
		this.lastLogoutTime = time;
		Util.save(this);
	}

	public void setLastHealth(double health)
	{
		this.lastHealth = health;
		Util.save(this);
	}

	public double getLastHealth()
	{
		return this.lastHealth;
	}

	public String getPlayerName()
	{
		return player;
	}

	public void addDeath()
	{
		deaths.add(new Death(this).getId().toString());
		Util.save(this);
	}

	public void addDeath(TJPlayer attacker)
	{
		deaths.add(new Death(this, attacker).getId().toString());
		Util.save(this);
	}

	public Collection<Death> getDeaths()
	{
		return Collections2.transform(deaths, new Function<String, Death>()
		{
			@Override
			public Death apply(String s)
			{
				try
				{
					return Death.Util.load(UUID.fromString(s));
				}
				catch(Exception ignored)
				{}
				return null;
			}
		});
	}

	public void remove()
	{
		// First we need to kick the player if they're online
		if(getOfflinePlayer().isOnline()) getOfflinePlayer().getPlayer().kickPlayer(ChatColor.RED + "Your player save has been cleared.");

		// Now we clear the TJPlayer save itself
		Util.delete(getPlayerName());
	}

	public static class Util
	{
		public static TJPlayer create(Player player)
		{
			TJPlayer playerSave = new TJPlayer();
			playerSave.setPlayer(player.getName());
			playerSave.setLastLoginTime(player.getLastPlayed());
			save(playerSave);

			player.setMaxHealth(40);
			player.setHealth(40);
			player.setHealthScaled(true);
			player.setHealthScale(6);

			return playerSave;
		}

		public static void save(TJPlayer player)
		{
			DataManager.players.put(player.getPlayerName(), player);
		}

		public static void delete(String playerName)
		{
			DataManager.players.remove(playerName);
		}

		public static TJPlayer getPlayer(OfflinePlayer player)
		{
			return getPlayer(player.getName());
		}

		public static TJPlayer getPlayer(String player)
		{
			return DataManager.players.containsKey(player) ? DataManager.players.get(player) : null;
		}
	}
}
