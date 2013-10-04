package com.censoredsoftware.teachjava.util;

import com.google.common.collect.Lists;

import java.util.*;

public class Maps2
{
	public static <K, V extends Comparable<? super V>> LinkedHashMap<K, V> sortByValue(Map<K, V> map, boolean reverse)
	{
		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<K, V>>()
		{
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2)
			{
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		if(reverse) list = Lists.reverse(list);

		LinkedHashMap<K, V> result = new LinkedHashMap<K, V>();
		for(Map.Entry<K, V> entry : list)
			result.put(entry.getKey(), entry.getValue());
		return result;
	}
}
