package utils;

import java.util.Random;

public class TicketID {

	private static Random rand = new Random();
	private static String channelName;

	public static String generateIssueChannelName() {

		channelName = "ticket-" + rand.nextInt(9999);
		return channelName;

	}

	public static String generateSuggestionChannelName() {

		channelName = "suggestion-" + rand.nextInt(9999);
		return channelName;

	}

	public static String generateQuestionChannelName() {

		channelName = "question-" + rand.nextInt(9999);
		return channelName;

	}

}
