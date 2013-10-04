package com.censoredsoftware.teachjava.player;

import com.censoredsoftware.teachjava.data.DataManager;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import java.util.*;

public class Death implements ConfigurationSerializable
{
	private UUID id;
	private long deathTime;
	private String killed, attacking;

	public Death(TJPlayer killed)
	{
		deathTime = System.currentTimeMillis();
		id = UUID.randomUUID();
		this.killed = killed.getPlayerName();
		Util.save(this);
	}

	public Death(TJPlayer killed, TJPlayer attacking)
	{
		deathTime = System.currentTimeMillis();
		id = UUID.randomUUID();
		this.killed = killed.getPlayerName();
		this.attacking = attacking.getPlayerName();
		Util.save(this);
	}

	public Death(UUID id, ConfigurationSection conf)
	{
		this.id = id;
		deathTime = conf.getLong("deathTime");
		killed = conf.getString("killed");
		if(conf.isString("attacking")) attacking = conf.getString("attacking");
	}

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("deathTime", deathTime);
		map.put("killed", killed);
		if(attacking != null) map.put("attacking", attacking);
		return map;
	}

	public UUID getId()
	{
		return id;
	}

	public long getDeathTime()
	{
		return deathTime;
	}

	public String getKilled()
	{
		return killed;
	}

	public String getAttacking()
	{
		return attacking;
	}

	public static class Util
	{
		public static Death load(UUID id)
		{
			return DataManager.deaths.get(id);
		}

		public static void save(Death death)
		{
			DataManager.deaths.put(death.id, death);
		}

		public static void delete(UUID id)
		{
			DataManager.deaths.remove(id);
		}

		public static Set<Death> getRecentDeaths(int seconds)
		{
			final long time = System.currentTimeMillis() - (seconds * 1000);
			return Sets.newHashSet(Iterables.filter(Iterables.concat(Collections2.transform(Sets.newHashSet(Bukkit.getOnlinePlayers()), new Function<Player, Collection<Death>>()
			{
				@Override
				public Collection<Death> apply(Player player)
				{
					try
					{
						return TJPlayer.Util.getPlayer(player).getDeaths();
					}
					catch(Exception ignored)
					{}
					return null;
				}
			})), new Predicate<Death>()
			{
				@Override
				public boolean apply(Death death)
				{
					return death.getDeathTime() >= time;
				}
			}));
		}

		public static Collection<Death> getRecentDeaths(TJPlayer player, int seconds)
		{
			final long time = System.currentTimeMillis() - (seconds * 1000);
			return Collections2.filter(player.getDeaths(), new Predicate<Death>()
			{
				@Override
				public boolean apply(Death death)
				{
					return death.getDeathTime() >= time;
				}
			});
		}
	}
}
