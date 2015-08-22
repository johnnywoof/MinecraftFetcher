package me.johnnywoof;

import java.nio.charset.Charset;
import java.util.regex.Pattern;

public class Utils {

	public static final Charset CHARSET = Charset.forName("UTF-8");

	private static final Pattern usernamePattern = Pattern.compile("^[a-zA-Z0-9_-]{3,16}$");

	public static boolean isValidUUID(String uuid) {
		return (uuid.length() - uuid.replace(".", "").length()) == 5;
	}

	/**
	 * Validate username with regular expression
	 *
	 * @param username The username for validation
	 * @return ture if valid, false if invalid
	 */
	public static boolean isValidUsername(String username) {

		return username != null && usernamePattern.matcher(username).matches();

	}

}
