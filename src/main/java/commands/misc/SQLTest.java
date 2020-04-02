package commands.misc;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import main.CollegiateMC;
import me.kbrewster.exceptions.APIException;
import me.kbrewster.mojangapi.MojangAPI;
import okio.Buffer;
import org.apache.commons.io.IOUtils;
import utils.UUIDToUsername;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;

import java.sql.Connection;
import java.util.UUID;

public class SQLTest extends Command {

	private static Connection conn;

	public SQLTest(Connection conn) {
		this.name = "sql";
		this.conn = conn;
	}

	@Override
	protected void execute(CommandEvent event) {

		String query = event.getMessage().getContentRaw().replaceFirst("!sql ", "");
		//System.out.println(query);

		StringBuilder builder = new StringBuilder();

		ResultSet rs = null;

		try {
			rs = conn.createStatement().executeQuery("SELECT player " +
					"FROM stats_commands_performed " +
					"WHERE amount > 5");

			if (rs.next()) {
				InputStream stream = rs.getBinaryStream("player");

				byte[] uuid = IOUtils.toByteArray(stream);

				for (byte b : uuid) {
					builder.append(String.format("%02x", b));
				}

				String id = MojangAPI.addDashes(builder.toString());

				String username = UUIDToUsername.getUsername(id);

				// Print the player UUID as text
				//event.getTextChannel().sendMessage(builder.toString() + ", also known as " + username).queue();
				//event.getTextChannel().sendMessage(username).queue();
			}



		} catch (SQLException | IOException e) {
			e.printStackTrace();
			event.getTextChannel().sendMessage("Failed to retrieve the stuff!").queue();
		}


	}
}
