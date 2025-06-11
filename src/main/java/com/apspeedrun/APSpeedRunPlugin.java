package com.apspeedrun;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.Objects;

@Slf4j
@PluginDescriptor(
	name = "AP Quest Speedrun Plugin"
)
public class APSpeedRunPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private APSpeedRunConfig config;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Example started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Example stopped!");
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		String questName = "Cook's Assistant";
		if (chatMessage.getType()!=ChatMessageType.GAMEMESSAGE) return;
		if (chatMessage.getMessage().contains("completed") && chatMessage.getMessage().contains(questName))
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Successfully detected completion.", null);
	}

	@Provides
	APSpeedRunConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(APSpeedRunConfig.class);
	}
}
