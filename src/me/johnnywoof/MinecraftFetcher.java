package me.johnnywoof;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.regex.Pattern;

public class MinecraftFetcher extends Thread {

	public static boolean RUNNING = true;
	public static final String VERSION = "1.0";
	private static final Pattern usernamePattern = Pattern.compile("^[a-zA-Z0-9_-]{3,16}$");

	private final String ip;
	private final int port;

	public MinecraftFetcher(String ip, int port) {

		this.ip = ip;
		this.port = port;

	}

	@Override
	public void run() {
		Charset charset = Charset.forName("UTF-8");
		Map<UUID, String> cache = new CacheMap<>(60000);

		DatagramSocket serverSocket;

		try {

			serverSocket = new DatagramSocket(null);
			serverSocket.bind(new InetSocketAddress(ip, port));

		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(-1);
			return;
		}

		byte[] byteData = new byte[36];

		while (RUNNING) {

			DatagramPacket receivePacket = new DatagramPacket(byteData, byteData.length);

			try {

				serverSocket.receive(receivePacket);

			} catch (IOException e) {
				e.printStackTrace();
				/*Corrupt packet?*/
				continue;
			}

			String data = new String(byteData, charset).trim();

			int length = data.length();

			if ((length == 32 || length == 36) && isValidUUID(data)) {

				UUID uuid;

				try {
					uuid = (length == 32 ? UUIDFetcher.convertUUID(data) : UUID.fromString(data));
				} catch (IllegalArgumentException e) {
					continue;
				}

				String responseUsername = null;

				if (cache.containsKey(uuid)) {

					responseUsername = cache.get(uuid);

				} else {

					UUIDProfile uuidProfile = UUIDFetcher.fetchProfile(uuid);

					if (uuidProfile != null) {
						cache.put(UUIDFetcher.convertUUID(uuidProfile.id), uuidProfile.name);
						responseUsername = uuidProfile.name;
					}

				}

				if (responseUsername != null) {

					while (responseUsername.length() < 16) {
						responseUsername += " ";
					}

				} else {

					responseUsername = "****************";

				}

				byte[] response = responseUsername.getBytes(charset);

				try {
					serverSocket.send(new DatagramPacket(response, response.length, receivePacket.getSocketAddress()));
				} catch (IOException e) {
					e.printStackTrace();
					//continue;
				}

			} else if (length <= 16 && isValidUsername(data)) {//Check username validation

				UUIDProfile uuidProfile = UUIDFetcher.getUUID(data);

				String responseUUID;

				if (uuidProfile != null) {
					cache.put(UUIDFetcher.convertUUID(uuidProfile.id), uuidProfile.name);
					responseUUID = uuidProfile.id;
				} else {
					continue;
				}

				byte[] response = responseUUID.getBytes(charset);

				try {
					serverSocket.send(new DatagramPacket(response, response.length, receivePacket.getSocketAddress()));
				} catch (IOException e) {
					e.printStackTrace();
					//continue;
				}

			}

		}

		serverSocket.close();
		cache.clear();

	}

	public static void main(String[] args) {

		if (args.length < 2) {
			System.out.println("Command line usage: java -jar MinecraftFetcher.jar -<Host or IP address to bind to> -<port to run on>");
			System.exit(0);
			return;
		}

		System.out.println("Starting the minecraft fetcher server version " + VERSION + "...");

		new MinecraftFetcher(args[0], Integer.parseInt(args[1])).start();

		Scanner scanner = new Scanner(System.in);

		System.out.println("Server has started.");

		while (RUNNING && scanner.hasNextLine()) {

			switch (scanner.nextLine().toLowerCase()) {

				case "stop":
				case "exit":
					System.out.println("Stopping...");
					RUNNING = false;
					break;
				case "help":
					System.out.println("The only commands are \"stop\" and \"exit\", which will stop the server.");
					break;
				default:
					System.out.println("Unknown command. Enter \"help\" for help.");
					break;

			}

		}

	}

	private static boolean isValidUUID(String uuid) {
		return (uuid.length() - uuid.replace(".", "").length()) == 5;
	}

	/**
	 * Validate username with regular expression
	 *
	 * @param username The username for validation
	 * @return ture if valid, false if invalid
	 */
	private static boolean isValidUsername(String username) {

		return username != null && usernamePattern.matcher(username).matches();

	}

}
