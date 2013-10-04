package com.censoredsoftware.teachjava.listener;

import com.censoredsoftware.teachjava.player.TJPlayer;
import com.censoredsoftware.teachjava.util.Zones;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener
{
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		if(Zones.inNoRealityZone(event.getPlayer().getLocation())) return;

		// Define variables
		Player player = event.getPlayer();
		TJPlayer tJPlayer = TJPlayer.Util.getPlayer(player);

		// First join
		if(tJPlayer == null)
		{
			tJPlayer = TJPlayer.Util.create(player);
		}
		else
		{
			// Set Health
			player.setHealth(tJPlayer.getLastHealth());
			player.setMaxHealth(40);
			player.setHealthScaled(true);
			player.setHealthScale(6);
		}

		// Set their last login-time
		Long now = System.currentTimeMillis();
		tJPlayer.setLastLoginTime(now);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent event)
	{
		if(Zones.inNoRealityZone(event.getPlayer().getLocation())) return;

		// Set their last logout-time
		Long now = System.currentTimeMillis();
		TJPlayer.Util.getPlayer(event.getPlayer()).setLastLogoutTime(now);
		TJPlayer.Util.getPlayer(event.getPlayer()).setLastHealth(event.getPlayer().getHealth());
	}
}
