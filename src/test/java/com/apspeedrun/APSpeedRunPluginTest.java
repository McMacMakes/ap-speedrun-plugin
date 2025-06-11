package com.apspeedrun;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class APSpeedRunPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(APSpeedRunPlugin.class);
		RuneLite.main(args);
	}
}