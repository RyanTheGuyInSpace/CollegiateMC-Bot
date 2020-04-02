package utilities;

import main.CollegiateMC;
import me.kbrewster.mojangapi.MojangAPI;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MCNameUtils {

	public static boolean nameExists(String name) {

		String url = "https://api.mojang.com/users/profiles/minecraft/" + name + "";

		try {
			@SuppressWarnings("deprecation")
			String UUIDJson = IOUtils.toString(new URL(url));
			String uuid;

			JSONObject UUIDObject = (JSONObject) JSONValue.parseWithException(UUIDJson);
			uuid = UUIDObject.get("id").toString();
			return true;

		} catch (Exception e) {
			return false;
		}
	}

	public static String getUUIDFromIGN(String ign) {
		String url = "https://api.mojang.com/users/profiles/minecraft/" + ign + "";

		try {
			@SuppressWarnings("deprecation")
			String UUIDJson = IOUtils.toString(new URL(url));

			JSONObject UUIDObject = (JSONObject) JSONValue.parseWithException(UUIDJson);
			String uuid = UUIDObject.get("id").toString();
			return MojangAPI.addDashes(uuid);

		} catch (Exception e) {
			return "error";  //change this to disallow insertion if the Mojang API isn't reachable
		}
	}

	public static boolean nameIsInDatabase(String name, Connection conn) {

		ResultSet rs;

		try {
			rs = conn.createStatement().executeQuery("SELECT mc_ign " +
					"FROM userinfo " +
					"WHERE mc_ign = '" + name + "'");

			return rs.next(); //Someone else has already registered that name

		} catch (SQLException e) {

			e.printStackTrace();
			return false;

		}
	}
}

