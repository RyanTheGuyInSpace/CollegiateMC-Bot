package commands.tickets;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class Close extends Command {

	public Close() {
		this.name = "close";
		this.help = "For use only inside ticket channels: closes the ticket";
	}

	@Override
	protected void execute(CommandEvent event) {


		if (event.getTextChannel().getName().startsWith("ticket-") || event.getTextChannel().getName().startsWith("question-")) {
			event.getTextChannel().delete().queue();
		} else {
			event.getMessage().delete().queue();
		}
	}
}
