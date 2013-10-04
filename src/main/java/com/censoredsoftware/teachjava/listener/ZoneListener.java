package com.censoredsoftware.teachjava.listener;

import com.censoredsoftware.teachjava.player.TJPlayer;
import com.censoredsoftware.teachjava.util.Zones;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class ZoneListener implements Listener
{
	@EventHandler(priority = EventPriority.MONITOR)
	public void onWorldSwitch(PlayerChangedWorldEvent event)
	{
		Player player = event.getPlayer();
		TJPlayer tJPlayer = TJPlayer.Util.getPlayer(player);

		// Leaving a disabled world
		if(Zones.isNoRealityWorld(event.getFrom()) && !Zones.isNoRealityWorld(player.getWorld()))
		{
			player.sendMessage(ChatColor.YELLOW + "TeachJava is enabled in this world.");

			// First join
			if(tJPlayer == null)
			{
				tJPlayer = TJPlayer.Util.create(player);

				player.setMaxHealth(40);
				player.setHealthScaled(true);
				player.setHealthScale(6);
			}

			// Set their last login-time
			Long now = System.currentTimeMillis();
			tJPlayer.setLastLoginTime(now);
		}

		// Entering a disabled world
		else if(!Zones.isNoRealityWorld(event.getFrom()) && Zones.isNoRealityWorld(player.getWorld()))
		{
			player.sendMessage(ChatColor.GRAY + "TeachJava is disabled in this world.");

			// Set their last logout-time
			Long now = System.currentTimeMillis();
			TJPlayer.Util.getPlayer(event.getPlayer()).setLastLogoutTime(now);
		}
	}
}
