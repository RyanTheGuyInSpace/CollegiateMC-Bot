package commands.stats;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import main.CollegiateMC;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatsCommand extends Command {

	private static Connection conn;

	public StatsCommand(Connection conn) {
		this.name = "stats";
		this.conn = conn;
		this.help = "Base command for viewing player stats";
		this.arguments = "[minecraft username]";
	}

	@Override
	protected void execute(CommandEvent event) {

		String username = event.getMessage().getContentRaw().replaceFirst("!stats ", "");

		ResultSet rs = null;

		try {
			rs = conn.createStatement().executeQuery("SELECT mc_ign " +
					"FROM userinfo " +
					"WHERE mc_ign = '" + username + "'");

			if (rs.next()) {
				event.getTextChannel().sendMessage("Records for user `" + username + "` have been successfully located").queue();
			} else {
				event.getTextChannel().sendMessage("No Record found").queue();
			}




		} catch (SQLException e) {

			e.printStackTrace();
			event.getTextChannel().sendMessage("Player `" + username + "` not found.").queue();

		}

	}
}
