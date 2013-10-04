package com.censoredsoftware.teachjava.listener;

import com.censoredsoftware.teachjava.util.Zones;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ChatListener implements Listener
{
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		String message = event.getMessage();
		message = message.substring(1);
		String[] args = message.split("\\s+");

		if(!Zones.inNoTeachJavaZone(event.getPlayer().getLocation()) && args[0].toLowerCase().equals("tell")) event.setCancelled(true);
	}
}
