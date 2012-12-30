package no.runsafe.nchat.events;

import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.event.IConfigurationChanged;
import no.runsafe.framework.event.player.IPlayerJoinEvent;
import no.runsafe.framework.server.event.player.RunsafePlayerJoinEvent;
import no.runsafe.framework.server.player.RunsafePlayer;
import no.runsafe.nchat.handlers.ChatHandler;
import org.bukkit.configuration.ConfigurationSection;

public class JoinEvent implements IPlayerJoinEvent, IConfigurationChanged
{
	public JoinEvent(ChatHandler chatHandler)
	{
		this.chatHandler = chatHandler;
	}

	@Override
	public void OnPlayerJoinEvent(RunsafePlayerJoinEvent runsafePlayerJoinEvent)
	{
		RunsafePlayer player = runsafePlayerJoinEvent.getPlayer();
		runsafePlayerJoinEvent.setJoinMessage(this.chatHandler.formatPlayerSystemMessage(this.joinServerMessage, player));

		// Set their name in the tab list
		player.setPlayerListName(this.chatHandler.convertColors(this.chatHandler.getTabListPrefix(player)) + player.getName());
	}

	@Override
	public void OnConfigurationChanged(IConfiguration iConfiguration)
	{
		this.joinServerMessage = iConfiguration.getConfigValueAsString("chatMessage.joinServer");
	}

	private String joinServerMessage;
	private ChatHandler chatHandler;
}
