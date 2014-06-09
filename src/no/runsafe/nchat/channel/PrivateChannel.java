package no.runsafe.nchat.channel;

import no.runsafe.framework.api.command.ICommandExecutor;
import no.runsafe.framework.api.log.IConsole;
import no.runsafe.framework.api.player.IPlayer;

public class PrivateChannel extends BasicChatChannel
{
	public PrivateChannel(ChannelManager manager, IConsole console, String name, ICommandExecutor player1, ICommandExecutor player2)
	{
		super(console, manager, name);
		members.put(player1.getName(), player1);
		members.put(player2.getName(), player2);
	}

	@Override
	public boolean Join(ICommandExecutor player)
	{
		return false;
	}

	@Override
	public boolean Leave(ICommandExecutor player)
	{
		return false;
	}

	@Override
	protected void SendFiltered(ICommandExecutor from, String message)
	{
		ICommandExecutor to = null;
		for (ICommandExecutor member : members.values())
			if (!member.getName().equals(from.getName()))
			{
				to = member;
				break;
			}

		if (to instanceof IPlayer)
		{
			if(!((IPlayer) to).isOnline() || (from instanceof IPlayer && ((IPlayer) from).shouldNotSee((IPlayer)to)))
			{
				from.sendMessage(String.format("&cThe player %s is currently offline.", ((IPlayer) to).getPrettyName()));
				return;
			}
		}
		SendMessage(from, to, manager.FormatPrivateMessageFrom(from, to, message));
		SendMessage(to, from, manager.FormatPrivateMessageTo(from, to, message));
		console.logInformation(manager.FormatPrivateMessageLog(from, to, message));
	}
}
