package commands.identification;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import main.CollegiateMC;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.react.PrivateMessageReactionAddEvent;
import utilities.MCNameUtils;

import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class IGNCommand extends Command {

	private final EventWaiter waiter;
	private final Connection conn;

	public IGNCommand(EventWaiter waiter, Connection conn) {
		this.waiter = waiter;
		this.guildOnly = false;
		this.name = "ign";
		this.help = "Begins the process of registering your Minecraft IGN to your Discord account";
		this.conn = conn;
	}

	@Override
	protected void execute(CommandEvent event) {

		ResultSet rs = null;

		try {
			rs = conn.createStatement().executeQuery("SELECT mc_ign " +
					"FROM userinfo " +
					"WHERE discord_id = " + event.getAuthor().getId());

			if (rs.next()) {  //if the user is already registered in the database

				//Send 'username already registered' embed and return
				String ign = rs.getString("mc_ign");

				event.getAuthor().openPrivateChannel().queue(
						channel -> channel.sendMessage(getAlreadyRegisteredEmbed(event, ign)).queue()
				);

				return;
			} else {  //if the user isn't already registered
				// Do nothing.
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		User user = event.getAuthor();
		EmbedBuilder builder = new EmbedBuilder();
		builder
				.setTitle("CollegiateMC IGN Registration")
				.setThumbnail("https://gamepedia.cursecdn.com/minecraft_gamepedia/b/be/Name_Tag.png")
				.setColor(Color.CYAN)
				.setDescription("Hey, " + event.getAuthor().getAsMention() + "!\n\nIn order to experience everything " +
						"that CollegiateMC has to \noffer, we just need to know your Minecraft in-game name.\n\n" +
						"**To begin, react with**  :white_check_mark:")
				.setFooter("If no reaction is made within 30 seconds, this session will expire",
						event.getSelfUser().getAvatarUrl());

		event.getAuthor().openPrivateChannel().complete().sendMessage(builder.build()).queue(
				m -> m.addReaction("U+2705").queue()
		);


		if (event.getChannelType().isGuild()) {
			event.getMessage().delete().queue();
		}


		waiter.waitForEvent(PrivateMessageReactionAddEvent.class,
				e -> e.getReactionEmote().getAsCodepoints().equals("U+2705")
						&& (e.getUser() != e.getJDA().getSelfUser())
						&& e.getUser().equals(user),

				e -> stepTwo(e, event, builder, user, conn),

				30, TimeUnit.SECONDS,
				() -> event.getAuthor().openPrivateChannel().queue(
						channel -> channel.sendMessage(getExpireEmbed()).queue()));

	}

	private void stepTwo(PrivateMessageReactionAddEvent e, CommandEvent event, EmbedBuilder embed, User user, Connection conn) {
		e.getChannel().sendMessage(
				embed.clear().setTitle("IGN Registration")
						.setColor(Color.CYAN)
						.addField("Step (1/2)","\nRespond in this channel with *only* your Minecraft in-game name" , true)
						.setThumbnail("https://gamepedia.cursecdn.com/minecraft_gamepedia/b/be/Name_Tag.png")
						.setFooter("If no message is sent within 30 seconds, this session will expire",
								event.getSelfUser().getAvatarUrl()).build())
				.queue();

		waiter.waitForEvent(PrivateMessageReceivedEvent.class,
				e2 -> !e2.getMessage().toString().replaceAll(" ", "").contains(" ")
						&& (e2.getAuthor() != e2.getJDA().getSelfUser()) //makes sure the bot doesn't trigger the event
						&& e2.getAuthor().equals(user),
				e2 -> stepThree(e2.getMessage().getContentRaw(), user, embed, event, conn),
				30, TimeUnit.SECONDS,
				() -> event.getAuthor().openPrivateChannel().queue(channel -> channel.sendMessage(
						getExpireEmbed()
				).queue()));

	}

	private void stepThree(String mc_ign, User user, EmbedBuilder embed, CommandEvent event, Connection conn) {

		embed.clear()
				.setTitle("IGN Registration")
				.setThumbnail("https://gamepedia.cursecdn.com/minecraft_gamepedia/b/be/Name_Tag.png")
				.setColor(Color.CYAN)
				.addField("Step (2/2)", "\nAlmost there! To confirm your username `" + mc_ign + "`, respond in this channel with *only* your " +
						"Minecraft in-game name one more time", true)
				.setFooter("If no message is sent within 30 seconds, this session will expire",
						event.getSelfUser().getAvatarUrl());

		user.openPrivateChannel().queue(
				channel -> channel.sendMessage(embed.build()).queue()
		);

		waiter.waitForEvent(PrivateMessageReceivedEvent.class,
				e3 -> (e3.getAuthor() != e3.getJDA().getSelfUser())
						&& e3.getAuthor().equals(user),
				e3 -> stepFour(e3, mc_ign, user, conn),
				30, TimeUnit.SECONDS,
				() -> event.getAuthor().openPrivateChannel().queue(channel -> channel.sendMessage(
						getExpireEmbed()
				).queue()));

	}


	private void stepFour(PrivateMessageReceivedEvent e, String ign, User user, Connection conn) {

		if (e.getMessage().getContentRaw().replaceAll(" ", "").equals(ign) && MCNameUtils.nameExists(ign)) {
			// If the responses match AND the name exists

			if (MCNameUtils.nameIsInDatabase(ign, conn)) {

				e.getAuthor().openPrivateChannel().queue(
						channel -> channel.sendMessage(nameTakenEmbed(ign)).queue()
				);

				return;
			}

			String uuid;

			try {
				uuid = MCNameUtils.getUUIDFromIGN(ign);

				conn.createStatement().executeUpdate(
						"INSERT INTO userinfo (discord_id, mc_ign, mc_uuid) " +
								"VALUES ('"+ e.getAuthor().getId() + "', '"
								+ ign + "', '"
								+ uuid + "')");

			} catch (Exception exc) {
				exc.printStackTrace();
				return;
			}

			user.openPrivateChannel().queue(
					channel -> channel.sendMessage(getSuccessEmbed(ign)).queue()
			);

		} else if (e.getMessage().getContentRaw().replaceAll(" ", "").equals(ign) && !MCNameUtils.nameExists(ign)){
			//if the names match, but the username doesn't exist

			// Send 'does not exist' message and make offer to restart with !ign
			user.openPrivateChannel().queue(
					privateChannel -> privateChannel.sendMessage(getNonExistentUsernameEmbed(ign)).queue()
			);

		} else if (!e.getMessage().getContentRaw().replaceAll(" ", "").equals(ign)){
			//if the responses don't match

			user.openPrivateChannel().queue(
					channel -> channel.sendMessage(getMismatchEmbed(ign, e.getMessage().getContentRaw().replaceAll(" ", ""))).queue()
			);
		}

	}

	private MessageEmbed getSuccessEmbed(String ign) {

		EmbedBuilder builder = new EmbedBuilder();

		builder
				.setTitle("IGN Successfully Registered!")
				.setDescription("Success! `" + ign + "` has been registered as your in-game name!\n\n" +
						"We hope you enjoy CollegiateMC!")
				.setColor(Color.GREEN)
				.setThumbnail("https://gamepedia.cursecdn.com/minecraft_gamepedia/b/be/Name_Tag.png");

		return builder.build();

	}

	private MessageEmbed getExpireEmbed() {

		EmbedBuilder builder = new EmbedBuilder();

		builder
				.setTitle("IGN Registration Expired")
				.setDescription("Your IGN Registration has been cancelled because a response\n" +
						"wasn't sent quickly enough. To try again, type `!ign`.")
				.setColor(Color.YELLOW)
				.setThumbnail("https://gamepedia.cursecdn.com/minecraft_gamepedia/b/be/Name_Tag.png");

		return builder.build();

	}

	private MessageEmbed getMismatchEmbed(String firstIGN, String mismatchIGN) {

		EmbedBuilder builder = new EmbedBuilder();

		builder
				.setTitle("IGN Mismatch")
				.setDescription("Your last response `" + mismatchIGN + "` does not match your\n" +
						"initial response, `" + firstIGN +"`. To try again, type `!ign`.")
				.setColor(Color.RED)
				.setThumbnail("https://gamepedia.cursecdn.com/minecraft_gamepedia/b/be/Name_Tag.png")
				.setFooter("Note: Registration is case sensitive");

		return builder.build();

	}

	private MessageEmbed getNonExistentUsernameEmbed(String ign) {
		EmbedBuilder builder = new EmbedBuilder();

		builder
				.setTitle("Username Does Not Exist")
				.setDescription("You're trying to register the in-game name `" + ign + "`, which " +
						"does not exist. To try again, type `ign`.\n\n" +
						"**Please note that registering an IGN that does not belong to you is a bannable offense.**")
				.setColor(Color.RED)
				.setThumbnail("https://gamepedia.cursecdn.com/minecraft_gamepedia/b/be/Name_Tag.png");

		return builder.build();
	}

	private MessageEmbed getAlreadyRegisteredEmbed(CommandEvent event, String ign) {

		EmbedBuilder builder = new EmbedBuilder();

		builder
				.setTitle("IGN Already Registered")
				.setDescription("Hey, " + event.getAuthor().getAsMention() + "!\n\n" +
						"You've already registered your Minecraft username as `" + ign + "`.\n\n" +
						"If this is a mistake, please contact a staff member.")
				.setColor(Color.CYAN)
				.setThumbnail("https://gamepedia.cursecdn.com/minecraft_gamepedia/b/be/Name_Tag.png");

		return builder.build();

	}

	private MessageEmbed nameTakenEmbed(String ign) {

		EmbedBuilder builder = new EmbedBuilder();

		builder
				.setTitle("IGN Already Registered")
				.setDescription("Someone else has already registered `" + ign + "` as their own!\n\n" +
						"If this is an error, please promptly notify a staff member so we may " +
						"resolve this issue. We apologize for the inconvenience.")
				.setColor(Color.RED)
				.setThumbnail("https://gamepedia.cursecdn.com/minecraft_gamepedia/b/be/Name_Tag.png");

		return builder.build();

	}
}
