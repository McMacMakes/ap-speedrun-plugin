package com.apspeedrun;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("example")
public interface APSpeedRunConfig extends Config
{
	@ConfigItem(
		keyName = "questName",
		name = "Goal Quest Name",
		description = "The name of the quest required to complete the final goal."
	)
	default String questName()
	{
		return "Cook's Assistant";
	}
}
