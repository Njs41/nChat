package no.runsafe.nchat.emotes;

import no.runsafe.framework.api.IConfiguration;
import no.runsafe.framework.api.IServer;
import no.runsafe.framework.api.command.ICommandExecutor;
import no.runsafe.framework.api.event.player.IPlayerCommandPreprocessEvent;
import no.runsafe.framework.api.event.plugin.IConfigurationChanged;
import no.runsafe.framework.api.filesystem.IPluginDataFile;
import no.runsafe.framework.api.filesystem.IPluginFileManager;
import no.runsafe.framework.api.player.IAmbiguousPlayer;
import no.runsafe.framework.api.player.IPlayer;
import no.runsafe.framework.minecraft.event.player.RunsafePlayerCommandPreprocessEvent;
import no.runsafe.nchat.channel.IChatChannel;
import no.runsafe.nchat.chat.EmoteEvent;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmoteHandler implements IPlayerCommandPreprocessEvent, IConfigurationChanged
{
	public EmoteHandler(IPluginFileManager fileManager, IServer server)
	{
		this.server = server;
		emoteFile = fileManager.getFile("emotes.txt");
	}

	@Override
	public void OnConfigurationChanged(IConfiguration configuration)
	{
		maxEmotes = configuration.getConfigValueAsInt("antiSpam.maxEmotes");
		maxEmotesPeriod = configuration.getConfigValueAsInt("antiSpam.maxEmotesPeriod");
		maxEmotesMessage = configuration.getConfigValueAsString("antiSpam.maxEmotesMessage");
		emotes.clear(); // Clear existing emotes.

		List<String> definitions = emoteFile.getLines(); // Grab all emotes from the file.
		for (String definition : definitions)
		{
			EmoteDefinition emote = new EmoteDefinition(definition);
			emotes.put(emote.getEmote(), emote); // Add the emote to the list.
		}
		emoteChecker = Pattern.compile("^/(" + StringUtils.join(emotes.keySet(), '|') + ")( (\\S+)|)");
	}

	@Override
	public void OnBeforePlayerCommand(RunsafePlayerCommandPreprocessEvent event)
	{
		if (event.isCancelled())
			return;

		if (executeEmote(null, event.getPlayer(), event.getPlayer(), event.getMessage()))
			event.cancel();
	}

	public boolean executeEmote(IChatChannel channel, ICommandExecutor executor, IPlayer player, CharSequence command)
	{
		Matcher matcher = emoteChecker.matcher(command);
		if (matcher.matches())
		{
			EmoteDefinition emote = emotes.get(matcher.group(1));
			IPlayer targetPlayer = matcher.groupCount() > 2 ? server.getPlayer(matcher.group(3)) : null;
			if (targetPlayer == null)
				rateLimitEmote(new EmoteEvent(channel, player, command.toString(), null, emote.getSingleEmote()));
			else if (targetPlayer instanceof IAmbiguousPlayer)
				executor.sendColouredMessage(targetPlayer.toString());
			else
				rateLimitEmote(new EmoteEvent(channel, player, command.toString(), targetPlayer, emote.getTargetEmote()));
			return true;
		}
		return false;
	}

	private void rateLimitEmote(EmoteEvent emote)
	{
		String player = emote.getPlayer().getName();
		if (maxEmotes > 0 && maxEmotesPeriod > 0)
		{
			if (!limiter.containsKey(player))
			{
				limiter.put(player, new ArrayList<DateTime>(1));
				limiter.get(player).add(DateTime.now());
			}
			else
			{
				List<DateTime> expired = new ArrayList<DateTime>(0);
				for(DateTime time : limiter.get(player))
				{
					if (time.plusSeconds(maxEmotesPeriod).isBeforeNow())
						expired.add(time);
				}
				limiter.get(player).removeAll(expired);
				if (limiter.get(player).size() >= maxEmotes)
				{
					emote.getPlayer().sendColouredMessage(maxEmotesMessage);
					return;
				}
				limiter.get(player).add(DateTime.now());
			}
		}
		emote.Fire();
	}

	private final IServer server;
	private final IPluginDataFile emoteFile;
	private final Map<String, EmoteDefinition> emotes = new HashMap<String, EmoteDefinition>(0);
	private final Map<String, List<DateTime>> limiter = new HashMap<String, List<DateTime>>(0);
	private int maxEmotes;
	private int maxEmotesPeriod;
	private String maxEmotesMessage;
	private Pattern emoteChecker;
}