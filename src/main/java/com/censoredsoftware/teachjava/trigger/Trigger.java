package com.censoredsoftware.teachjava.trigger;

public interface Trigger
{
	public void processSync();

	public void processAsync();

	public static class Util
	{
		/**
		 * List of all triggers.
		 */
		public static Trigger[] getAll()
		{
			return new Trigger[] { /* STUFF GOES HERE */};
		}
	}
}
