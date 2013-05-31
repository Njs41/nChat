package no.runsafe.nchat.handlers;

import no.runsafe.framework.configuration.IConfiguration;
import no.runsafe.framework.event.IConfigurationChanged;
import no.runsafe.framework.output.ChatColour;
import no.runsafe.framework.output.IOutput;
import no.runsafe.framework.server.ICommandExecutor;
import no.runsafe.framework.server.RunsafeServer;
import no.runsafe.framework.server.player.RunsafePlayer;

import java.util.HashMap;
import java.util.logging.Level;

public class WhisperHandler implements IConfigurationChanged
{
	public WhisperHandler(IOutput console)
	{
		this.console = console;
		this.lastWhisperList = new HashMap<String, String>();
	}

	public void sendWhisper(ICommandExecutor sender, RunsafePlayer toPlayer, String message)
	{
		if (!(this.enableColorCodes || sender.hasPermission("runsafe.nchat.colors")))
			message = ChatColour.Strip(message);

		sender.sendColouredMessage(
			this.whisperToFormat.replace("#target", toPlayer.getPrettyName()).replace("#message", message)
		);

		String senderName = (sender instanceof RunsafePlayer ? ((RunsafePlayer) sender).getPrettyName() : this.consoleWhisperName);

		toPlayer.sendColouredMessage(
			this.whisperFromFormat.replace("#source", senderName).replace("#message", message)
		);

		if (sender instanceof RunsafePlayer)
			this.setLastWhisperedBy(toPlayer, (RunsafePlayer) sender);

		console.writeColoured(
			"%s -> %s: %s",
			Level.INFO,
			senderName,
			toPlayer.getPrettyName(),
			message
		);
	}

	private void setLastWhisperedBy(RunsafePlayer player, RunsafePlayer whisperer)
	{
		this.lastWhisperList.put(player.getName(), whisperer.getName());
	}

	public void deleteLastWhisperedBy(RunsafePlayer player)
	{
		if (this.lastWhisperList.containsKey(player.getName()))
			this.lastWhisperList.remove(player.getName());
	}

	public RunsafePlayer getLastWhisperedBy(RunsafePlayer player)
	{
		String playerName = player.getName();

		if (this.lastWhisperList.containsKey(playerName))
		{
			RunsafePlayer whisperer = RunsafeServer.Instance.getPlayer(this.lastWhisperList.get(playerName));
			if (whisperer != null)
				return whisperer;
		}
		return null;
	}

	public boolean canWhisper(RunsafePlayer player, RunsafePlayer target)
	{
		return (target.isOnline() && player.canSee(target));
	}

	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		this.whisperToFormat = configuration.getConfigValueAsString("chatMessage.whisperTo");
		this.whisperFromFormat = configuration.getConfigValueAsString("chatMessage.whisperFrom");
		this.enableColorCodes = configuration.getConfigValueAsBoolean("nChat.enableColorCodes");
		this.consoleWhisperName = configuration.getConfigValueAsString("console.whisper");
	}

	private boolean enableColorCodes;
	private String whisperToFormat;
	private String whisperFromFormat;
	private final IOutput console;
	private final HashMap<String, String> lastWhisperList;
	private String consoleWhisperName;
}
