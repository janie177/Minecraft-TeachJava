package com.censoredsoftware.teachjava;

import com.censoredsoftware.teachjava.data.DataManager;
import com.censoredsoftware.teachjava.data.ThreadManager;
import com.censoredsoftware.teachjava.util.Messages;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Class for the Bukkit plugin.
 */
public class TeachJavaPlugin extends JavaPlugin
{
	/**
	 * The Bukkit enable method.
	 */
	@Override
	public void onEnable()
	{
		// Load the game engine.
		TeachJava.load();

		// Print success!
		Messages.info("Successfully enabled.");
	}

	/**
	 * The Bukkit disable method.
	 */
	@Override
	public void onDisable()
	{
		// Save all the data.
		DataManager.save();

		// Cancel all threads, Event calls, and connections.
		ThreadManager.stopThreads();
		HandlerList.unregisterAll(this);

		Messages.info("Successfully disabled.");
	}
}
