package main;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import commands.identification.IGNCommand;
import commands.misc.ReactCommand;
import commands.misc.SQLTest;
import commands.stats.StatsCommand;
import commands.tickets.Close;
import commands.tickets.Issue;
import commands.tickets.Suggestion;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class CollegiateMC {

	public static void main(String[] args) throws IOException, LoginException, InterruptedException {
		String TOKEN = "";
		String mySQLURL = "";
		String mySQLUser = "";
		String mySQLPass = "";

		File config = new File("./bot.properties");  //makes the file
		FileReader configReader = new FileReader(config);
		Properties botConfig = new Properties();
		botConfig.load(configReader);

		if (config.exists()) {

			System.out.println("Reading bot.properties...");

			TOKEN = botConfig.getProperty("token");
			mySQLURL = botConfig.getProperty("mysql-url");
			mySQLUser = botConfig.getProperty("mysql-user");
			mySQLPass = botConfig.getProperty("mysql-pass");
			configReader.close();

			//load properties
		}

		Connection conn = sqlConnect(mySQLURL, mySQLUser, mySQLPass);

		CommandClientBuilder builder = new CommandClientBuilder();

		builder.setPrefix("!");

		String ACTIVITY = "carefully";
		builder.setActivity(Activity.watching(ACTIVITY));

		EventWaiter waiter = new EventWaiter();

		builder.setOwnerId("131499209760178176");

		builder.useHelpBuilder(false);

		builder.addCommands(new Issue(),
				new Close(),
				new Suggestion(),
				new ReactCommand(),
				new SQLTest(conn),  //make all of these take the same connection
				new StatsCommand(conn),
				new IGNCommand(waiter, conn));

		JDA jda = new JDABuilder(AccountType.BOT)
				.setToken(TOKEN)
				.addEventListeners(builder.build(), new ChatListener(), waiter)
				.build().awaitReady();
	}

	private static Connection sqlConnect(String url, String user, String pass) {

		Connection conn = null;

		try {
			conn = DriverManager.getConnection(url, user, pass);
			System.out.println("Successfully connected to database!");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	}

