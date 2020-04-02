package commands.misc;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class ReactCommand extends Command {

	public ReactCommand() {
		this.name = "react";
	}

	@Override
	protected void execute(CommandEvent event) {

		event.getTextChannel().retrieveMessageById(690579899450130482L).complete().addReaction("U+1F7E2").queue();
		event.getTextChannel().retrieveMessageById(690579899450130482L).complete().addReaction("U+1F535").queue();

	}
}
