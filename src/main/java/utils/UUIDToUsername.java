package utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class UUIDToUsername {

	public static String getUsername(String UUID) {

		String url = "https://api.mojang.com/user/profiles/" + UUID + "/names";

		try {
			Document doc = Jsoup.connect(url).get();
			Element body = doc.body();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return "Failed";


	}
}
