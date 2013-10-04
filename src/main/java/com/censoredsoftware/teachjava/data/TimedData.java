package com.censoredsoftware.teachjava.data;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Sets;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TimedData implements ConfigurationSerializable
{
	private UUID id;
	private String key;
	private String subKey;
	private String data;
	private String type;
	private long expiration;

	public TimedData()
	{}

	public TimedData(UUID id, ConfigurationSection conf)
	{
		this.id = id;
		key = conf.getString("key");
		subKey = conf.getString("subKey");
		data = conf.getString("data");
		type = conf.getString("type");
		expiration = conf.getLong("expiration");
	}

	@Override
	public Map<String, Object> serialize()
	{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("key", key);
		map.put("subKey", subKey);
		map.put("data", data);
		map.put("type", type);
		map.put("expiration", expiration);
		return map;
	}

	public void generateId()
	{
		id = UUID.randomUUID();
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	public void setSubKey(String subKey)
	{
		this.subKey = subKey;
	}

	public void setData(Object data)
	{
		this.data = data.toString();
		this.type = data.getClass().getName();
	}

	public void setSeconds(Integer seconds)
	{
		this.expiration = System.currentTimeMillis() + (seconds * 1000);
	}

	public void setMinutes(Integer minutes)
	{
		this.expiration = System.currentTimeMillis() + (minutes * 60000);
	}

	public void setHours(Integer hours)
	{
		this.expiration = System.currentTimeMillis() + (hours * 3600000);
	}

	public UUID getId()
	{
		return this.id;
	}

	public String getKey()
	{
		return this.key;
	}

	public String getSubKey()
	{
		return this.subKey;
	}

	public Object getData()
	{
		if(this.type.equalsIgnoreCase("string")) return this.data;
		if(this.type.equalsIgnoreCase("integer")) return Integer.parseInt(this.data);
		if(this.type.equalsIgnoreCase("boolean")) return Boolean.parseBoolean(this.data);
		return this.data;
	}

	public long getExpiration()
	{
		return this.expiration;
	}

	public void delete()
	{
		DataManager.timedData.remove(id);
	}

	@Override
	public boolean equals(final Object obj)
	{
		if(this == obj) return true;
		if(obj == null || getClass() != obj.getClass()) return false;
		final TimedData other = (TimedData) obj;
		return Objects.equal(this.id, other.id) && Objects.equal(this.key, other.key) && Objects.equal(this.subKey, other.subKey) && Objects.equal(this.data, other.data);
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(this.id, this.key, this.subKey, this.data);
	}

	@Override
	public String toString()
	{
		return Objects.toStringHelper(this).add("id", this.id).add("key", this.key).add("subkey", this.subKey).add("data", this.data).toString();
	}

	@Override
	public Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException();
	}

	public static class Util
	{
		public static TimedData get(UUID id)
		{
			return DataManager.timedData.get(id);
		}

		public static Set<TimedData> getAll()
		{
			return Sets.newHashSet(DataManager.timedData.values());
		}

		public static TimedData find(String key, String subKey)
		{
			if(findByKey(key) == null) return null;

			for(TimedData data : findByKey(key))
				if(data.getSubKey().equals(subKey)) return data;

			return null;
		}

		public static Set<TimedData> findByKey(final String key)
		{
			return Sets.newHashSet(Collections2.filter(getAll(), new Predicate<TimedData>()
			{
				@Override
				public boolean apply(TimedData timedData)
				{
					return timedData.getKey().equals(key);
				}
			}));
		}

		public static void remove(String key, String subKey)
		{
			if(find(key, subKey) != null) find(key, subKey).delete();
		}

		/**
		 * Updates all timed data.
		 */
		public static void updateTimedData()
		{
			for(TimedData data : Collections2.filter(Util.getAll(), new Predicate<TimedData>()
			{
				@Override
				public boolean apply(TimedData data)
				{
					return data.getExpiration() <= System.currentTimeMillis();
				}
			}))
				data.delete();
		}
	}
}
