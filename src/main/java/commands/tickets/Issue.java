package commands.tickets;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.entities.TextChannel;
import permissions.TicketPermissions;
import utils.TicketID;

import java.util.Objects;

public class Issue extends Command {

	public Issue() {
		this.name = "issue";
		this.help = "Used to report an issue to staff";
		this.cooldown = 14400; // 4 hour cooldown
	}

	protected void execute(CommandEvent event) {

		String newChannelName = TicketID.generateIssueChannelName();

		event.getGuild()
				.createTextChannel(newChannelName)
				.addPermissionOverride(event.getMember(), TicketPermissions.allowedPerms, TicketPermissions.disallowedPerms)//TODO be EXTREMELY specific about ALL the permissions
				.setParent(event.getGuild().getCategoryById(675397888829816842L))
				.queue(
						textChannel -> {textChannel.sendMessage(event.getAuthor().getAsMention() + " " +
								Objects.requireNonNull(event.getGuild().getRoleById(676950246856851467L)).getAsMention() + "\nYour ticket has been created. Describe your issue(s) " +
								"as clearly as you can and you will be assisted by a support member as soon as possible.").queue();
						});

		event.getMessage().delete().queue();

		//TODO make sure that no duplicate channel names are created.

	}



}
