package main;

import net.dv8tion.jda.api.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ChatListener extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {

		/**
		 * Chat listener for the #literally-just-counting channel
		 */

		if (event.getChannelType().isGuild()) {
			if (event.getTextChannel().getId().equals("582757760253558820") && (event.getAuthor().getIdLong() != 690221354506911825L)) {

				StringBuilder response = new StringBuilder();

				if (event.getMessage().getContentRaw().contains("/")) {

					String[] message = event.getMessage().getContentRaw().split(" ");

					for (String value : message) {
						if (value.contains("/")) {
							try {
								String[] multiNumber = value.split("/");
								for (int i = 0; i <= (multiNumber.length - 1); i++) {
									if (i != multiNumber.length - 1) {
										response.append(Long.parseLong(multiNumber[i]) + 1).append("/");
									} else {
										response.append(Long.parseLong(multiNumber[i]) + 1);
									}
								}
								event.getTextChannel().sendMessage(response.toString()).queue();
							} catch (Exception e) {
								// Do nothing
							}
						}
					}
				} else {
					try {
						long num = Long.parseLong(event.getMessage().getContentRaw());
						num++;
						event.getTextChannel().sendMessage("" + num).queue();
					} catch (Exception e) {
						// Do nothing
					}
				}
			}
		}



		/**
		 * -----------------------------------------------------------------------------------------------------------------------------
		 */

		/**
		 * TODO create an external text file with prohibited words/phrases in it.
		 * Scan the text file on every message and delete the message if it contains
		 * a prohibited word/phrase.
		 */

	}

	@Override
	public void onTextChannelCreate(TextChannelCreateEvent event) {

	}

	@Override
	public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {


		if (event.getMessageIdLong() == 690579899450130482L) {

			if (event.getReactionEmote().getAsCodepoints().equals("U+1f7e2")) {  //large_green_circle - student role

				//Grants the student role
				event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(690388794582499408L)).queue();

				try {

					//Removes the guest role
					event.getGuild().removeRoleFromMember(event.getMember(), event.getGuild().getRoleById(690233358281605186L)).queue();

					//Removes the guest student role
					event.getGuild().getTextChannelById(690233700541399049L).retrieveMessageById(690579899450130482L).complete()
							.removeReaction("\uD83D\uDD35", event.getUser()).queue();
				} catch (Exception e) {
					// They didn't have the guest role. No action needed.
				}

			} else if (event.getReactionEmote().getAsCodepoints().equals("U+1f535")) { //large_blue_circle - guest role

				//Grants the guest role
				event.getGuild().addRoleToMember(event.getMember(), event.getGuild().getRoleById(690233358281605186L)).queue();

				try {

					// Removes student role
					event.getGuild().removeRoleFromMember(event.getMember(), event.getGuild().getRoleById(690388794582499408L)).queue();

					//removes green circle reaction
					event.getGuild().getTextChannelById(690233700541399049L).retrieveMessageById(690579899450130482L).complete()
							.removeReaction("\uD83D\uDFE2", event.getUser()).queue();

				} catch (Exception e) {
					// They didn't have the student role. No action needed.
				}
			}
		}
	}

	@Override
	public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event) {

		if (event.getMessageIdLong() == 690579899450130482L) {

			if (event.getReactionEmote().getAsCodepoints().equals("U+1f7e2")) {  //large_green_circle - student role

				//Removes the student role
				event.getGuild().removeRoleFromMember(event.getMember(), event.getGuild().getRoleById(690388794582499408L)).queue();

			} else if (event.getReactionEmote().getAsCodepoints().equals("U+1f535")) { //large_blue_circle - guest role

				//Removes the guest role
				event.getGuild().removeRoleFromMember(event.getMember(), event.getGuild().getRoleById(690233358281605186L)).queue();
			}
		}
	}
}
