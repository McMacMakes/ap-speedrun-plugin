package com.apspeedrun;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.ChatMessageType;
import net.runelite.api.events.*;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.util.Text;

import java.util.*;
import java.util.function.Consumer;

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

	Widget optionsParentWidget;

	private final Consumer<MenuEntry> DISABLED = e -> { };

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event) {
		// If the entry is disabled, consume the event.
		if (event.getMenuEntry().onClick() == DISABLED) {
			event.consume();
			return;
		}
	}

	@Subscribe
	public void  onMenuEntryAdded(MenuEntryAdded menuEntryAdded)
	{
		MenuEntry menuEntry = menuEntryAdded.getMenuEntry();

		if (menuEntry.getType() == MenuAction.WIDGET_CONTINUE) handleMenuEntry(menuEntry);
	}

	private void handleMenuEntry(MenuEntry menuEntry) {
		boolean match = Objects.equals(menuEntry.getOption(), "Continue") &&
				currentLockedWidgets.containsKey(menuEntry.getParam0()) &&
				currentLockedWidgets.get(menuEntry.getParam0()).getId() == menuEntry.getParam1();
		if(match) disableMenuEntry(menuEntry);
	}

	private void disableMenuEntry(MenuEntry menuEntry)
	{
		log.info("option:{}",menuEntry.getOption());
		log.info("target:{}",menuEntry.getTarget());
		String option = Text.removeFormattingTags(menuEntry.getOption());
		String target = Text.removeFormattingTags(menuEntry.getTarget());
		menuEntry.setOption("<col=808080>(Locked) " + option);
		menuEntry.setTarget("<col=808080>" + target);
		menuEntry.onClick(DISABLED);
	}


	@Subscribe
	public void  onWidgetLoaded(WidgetLoaded widgetLoaded)
	{
		int groupId = widgetLoaded.getGroupId();
		if (groupId == InterfaceID.CHATMENU)
		{

			Widget widget = client.getWidget(InterfaceID.Chatmenu.OPTIONS);

            if (widget != null) {
				optionsParentWidget = widget;
            } else log.info("widget with ID {} not found",InterfaceID.Chatmenu.OPTIONS);
        }
	}

	public void onWidgetClosed(WidgetClosed widgetClosed){
		if (widgetClosed.getGroupId() == InterfaceID.CHATMENU) {
			currentLockedWidgets.clear();
			optionsParentWidget = null;
		}
	}

	private void disableChatOptionWidget(Widget widget)
	{
		widget.setTextColor(8421504);
		widget.setOnKeyListener(null);
	}

	List<String> lockedChatOptions = List.of("What's wrong?","Nice hat!","blah");
	Map<Integer, Widget> currentLockedWidgets = new HashMap<>();


	private boolean isLockedChatOption(Widget optionWidget)
	{
		return lockedChatOptions.contains(optionWidget.getText());
	}

	private void handleOptionsParentWidget()
	{
		if (optionsParentWidget != null){
			Widget[] dynamicChildWidgets = optionsParentWidget.getDynamicChildren();
			if (dynamicChildWidgets != null) {
				for (int i = 0; i < dynamicChildWidgets.length; i++) {
					Widget child = dynamicChildWidgets[i];
					if (isLockedChatOption(child)) {
						currentLockedWidgets.putIfAbsent(i,child);
						disableChatOptionWidget(child);
					}
				}
			}

		}
	}

	@Subscribe
	public void onClientTick(ClientTick clientTick){
		handleOptionsParentWidget();
	}

	@Subscribe
	public void onChatMessage(ChatMessage chatMessage)
	{
		if (chatMessage.getType()!=ChatMessageType.GAMEMESSAGE) return;
		detectQuestCompletion(config.questName(), chatMessage);
	}

	public void detectQuestCompletion(String questName, ChatMessage chatMessage)
	{
		if (chatMessage.getMessage().contains("completed") && chatMessage.getMessage().contains(questName))
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Successfully detected completion.", null);
	}

	@Provides
	APSpeedRunConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(APSpeedRunConfig.class);
	}
}
