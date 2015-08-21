package me.johnnywoof;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

public class UUIDFetcher {

	private static final Gson gson = new Gson();

	private UUIDFetcher() {
	}

	/**
	 * Returns the UUIDProfile for the player username.
	 *
	 * @param username The username.
	 * @return The UUID Profile, null if not found.
	 */
	public static UUIDProfile getUUID(String username) {

		try {

			URL obj = new URL("https://api.mojang.com/users/profiles/minecraft/" + username);
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			con.setRequestMethod("GET");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("Accept-Charset", "utf-8");
			con.setRequestProperty("User-Agent", "minecraft");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("DNT", "1");
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			StringBuilder response = new StringBuilder();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			con.disconnect();

			String finalResponse = response.toString();

			//We got some reasonable response
			if (finalResponse.length() > 40) {

				return gson.fromJson(finalResponse, UUIDProfile.class);
				//return convertUUID(finalResponse.substring(7, 39));

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}

	/**
	 * Fetches the user's profile from the session servers using the player's UUID
	 *
	 * @param uuid The UUID
	 * @return The user's profile, null if an error occurred.
	 */
	public static UUIDProfile fetchProfile(UUID uuid) {

		try {

			URL obj = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + convertUUID(uuid));
			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

			con.setRequestMethod("GET");
			con.setRequestProperty("Accept", "application/json");
			con.setRequestProperty("Accept-Charset", "utf-8");
			con.setRequestProperty("User-Agent", "minecraft");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("DNT", "1");
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

			StringBuilder response = new StringBuilder();

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			con.disconnect();

			return gson.fromJson(response.toString(), UUIDProfile.class);

		} catch (IOException | JsonSyntaxException e) {

			e.printStackTrace();
			//TODO Fallback method?

		}

		return null;

	}

	/**
	 * Converts a UUID to a string UUID without regex
	 *
	 * @param uuid The UUID
	 * @return The converted string UUID
	 */
	public static String convertUUID(UUID uuid) {

		ArrayList<Character> characters = new ArrayList<>(32);

		char[] chars = uuid.toString().toCharArray();

		for (char c : chars) {

			if (c != '-') {

				characters.add(c);

			}

		}

		chars = new char[characters.size()];

		for (int i = 0; i < characters.size(); i++) {

			chars[i] = characters.get(i);

		}

		return new String(chars);

	}

	/**
	 * Converts a String to a UUID
	 *
	 * @param uuid The string to be converted
	 * @return The result
	 */
	public static UUID convertUUID(String uuid) {
		return UUID.fromString(uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-" + uuid.substring(16, 20) + "-" + uuid.substring(20, 32));
	}

}
