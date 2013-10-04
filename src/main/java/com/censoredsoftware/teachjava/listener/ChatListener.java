package com.censoredsoftware.teachjava.listener;

import com.censoredsoftware.teachjava.location.Region;
import com.censoredsoftware.teachjava.util.Randoms;
import com.censoredsoftware.teachjava.util.Zones;
import com.google.common.collect.Lists;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;

public class ChatListener implements Listener
{
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event)
	{
		String message = event.getMessage();
		Player player = event.getPlayer();

		if(Zones.inNoRealityZone(event.getPlayer().getLocation())) return;

		// Chat only with people in the same region
		event.getRecipients().clear();
		event.getRecipients().addAll(Region.Util.getPlayersInRegionWithSameDialect(player));

		// Scramble dialects
		scrambleDialect(player, event.getFormat(), event.getMessage());
	}

	private static void scrambleDialect(Player player, String format, String message)
	{
		String similarMessage = message;
		String differentMessage = "";

		List<String> words = Lists.newArrayList(message.split("\\s+"));

		// Define Similar message
		int replace = Randoms.generateIntRange(1, words.size()) - 1;
		int remove = Randoms.generateIntRange(1, words.size()) - 1;
		similarMessage.replace(words.get(replace), Randoms.generateString(words.get(replace).length()));
		similarMessage.replace(" " + words.get(remove), "").replace(words.get(remove), "");

		// Define Different message
		for(String word : words)
			differentMessage += " " + Randoms.generateString(word.length());

		// Send messages
		for(Player inRegion : Region.Util.getPlayersInRegionWithSimilarDialect(player))
			inRegion.sendMessage(format.replace(message, similarMessage));
		for(Player inRegion : Region.Util.getPlayersInRegionWithDifferentDialect(player))
			inRegion.sendMessage(format.replace(message, differentMessage));
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		String message = event.getMessage();
		message = message.substring(1);
		String[] args = message.split("\\s+");

		if(!Zones.inNoRealityZone(event.getPlayer().getLocation()) && args[0].toLowerCase().equals("tell")) event.setCancelled(true);
	}
}
