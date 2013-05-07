package no.runsafe.nchat;

import no.runsafe.framework.RunsafeConfigurablePlugin;
import no.runsafe.nchat.command.*;
import no.runsafe.nchat.database.MuteDatabase;
import no.runsafe.nchat.events.*;
import no.runsafe.nchat.handlers.*;

public class Core extends RunsafeConfigurablePlugin
{
	@Override
	protected void PluginSetup()
	{
		// TODO: Re-implement channel functionality
		// TODO: In-game whisper monitoring. Do we want this?
		// TODO: Implement whispering from the console.

		// Core
		this.addComponent(Globals.class);
		this.addComponent(DeathParser.class);

		// Database
		this.addComponent(MuteDatabase.class);

		// Handlers
		this.addComponent(ChatChannelHandler.class);
		this.addComponent(ChatHandler.class);
		this.addComponent(MuteHandler.class);
		this.addComponent(WhisperHandler.class);
		this.addComponent(SpamHandler.class);
		this.addComponent(RegionHandler.class);

		// Commands
		//this.addComponent(ChannelCommand.class);
		this.addComponent(MuteCommand.class);
		this.addComponent(UnMuteCommand.class);
		this.addComponent(PlayerDeath.class);
		this.addComponent(PuppetCommand.class);
		this.addComponent(EmoteCommand.class);
		this.addComponent(WhisperCommand.class);
		this.addComponent(ReplyCommand.class);
		this.addComponent(PlayerListRefreshCommand.class);

		// Events
		this.addComponent(JoinEvent.class);
		this.addComponent(LeaveEvent.class);
		this.addComponent(KickEvent.class);
		this.addComponent(ChatEvent.class);
		this.addComponent(VanishEvent.class);
	}
}
