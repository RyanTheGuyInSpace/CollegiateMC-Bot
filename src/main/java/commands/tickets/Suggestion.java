package commands.tickets;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import permissions.TicketPermissions;
import utils.TicketID;

public class Suggestion extends Command {

	public Suggestion() {
		this.name = "suggestion";
		this.help = "Used to file a suggestion for something related to the server";
		this.cooldown = 14400;  // 4 hour cooldown
	}

	@Override
	protected void execute(CommandEvent event) {

		String newChannelName = TicketID.generateSuggestionChannelName();

		event.getGuild()
				.createTextChannel(newChannelName)
				.addPermissionOverride(event.getMember(), TicketPermissions.allowedPerms, TicketPermissions.disallowedPerms)
				//TODO be EXTREMELY specific about ALL the permissions
				.setParent(event.getGuild().getCategoryById(675397926431752205L))
				.queue(
						textChannel -> {textChannel.sendMessage(event.getAuthor().getAsMention() + "\nYour ticket has been created. " +
								"Please carefully describe your suggestion and a team member will get back to you as soon as possible.\n" +
								"Thank you for taking interest in the improvement of CollegiateMC!").queue();
						});

		event.getMessage().delete().queue();

		//TODO make sure that no duplicate channel names are created.
		//TODO limit channels
	}
}
