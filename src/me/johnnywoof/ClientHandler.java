package me.johnnywoof;

import java.io.*;
import java.net.DatagramPacket;
import java.nio.BufferOverflowException;
import java.util.Map;
import java.util.UUID;

public class ClientHandler extends Thread {

	private static final int responseSize = 128;

	private final MinecraftFetcher minecraftFetcher;
	private final DatagramPacket receivePacket;

	public ClientHandler(MinecraftFetcher minecraftFetcher, DatagramPacket receivePacket) {
		this.minecraftFetcher = minecraftFetcher;
		this.receivePacket = receivePacket;
	}

	@Override
	public void run() {

		String command = null;
		String payload = null;

		System.out.println("Reading input...");

		try (BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(this.receivePacket.getData()), Utils.CHARSET))) {

			command = br.readLine();
			payload = br.readLine();

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Command: \"" + command + "\"");
		System.out.println("Payload: \"" + payload + "\"");

		//Ignore bad data
		if (command == null || command.length() != 7 || payload == null || payload.length() > 32) {
			System.out.println("Detected bad data. Ignoring packet.");
			return;
		}

		ByteArrayOutputStream responseData = new ByteArrayOutputStream(responseSize);

		try (PrintWriter w = new PrintWriter(responseData)) {

			System.out.println("Writing response...");

			w.println(command);
			w.println(payload);

			switch (command) {

				case "PROFILE":

					if (payload.length() <= 16) {

						if (Utils.isValidUsername(payload)) {

							UUIDProfile profile = new UUIDProfile();
							UUID uuid = null;

							for (Map.Entry<UUID, String> en : this.minecraftFetcher.cache.entrySet()) {

								if (en.getValue().equalsIgnoreCase(payload)) {

									uuid = en.getKey();
									profile.name = en.getValue();
									break;

								}

							}

							if (uuid == null) {

								profile = UUIDFetcher.fetchProfile(payload);

								if (profile != null)
									this.minecraftFetcher.cache.put(UUIDFetcher.convertUUID(profile.id), profile.name);

							} else {

								profile.id = UUIDFetcher.convertUUID(uuid);

							}

							if (profile != null) {

								w.println("SUCCESS");
								w.println(profile.id);
								w.println(profile.name);

							} else {

								w.println("ERROR");
								w.println("Profile not found.");

							}

						} else {

							w.println("ERROR");
							w.println("Invalid username.");

						}

					} else {

						if (payload.length() == 32) {

							UUID uuid = UUIDFetcher.convertUUID(payload);
							String username = this.minecraftFetcher.cache.get(uuid);
							UUIDProfile profile = new UUIDProfile();
							profile.id = payload;

							if (username == null) {

								profile = UUIDFetcher.fetchProfile(uuid);

								if (profile != null)
									this.minecraftFetcher.cache.put(UUIDFetcher.convertUUID(profile.id), profile.name);

							} else {

								profile.name = username;

							}

							if (profile != null) {

								w.println("SUCCESS");
								w.println(profile.id);
								w.println(profile.name);

							} else {

								w.println("ERROR");
								w.println("Profile not found.");

							}

						} else {

							w.println("ERROR");
							w.println("Invalid UUID.");

						}

					}

					break;
				default:
					w.println("ERROR");
					w.println("Unknown command.");
					break;

			}

			w.close();

			if (responseData.size() < responseSize) {

				//Fill the remaining buffer
				responseData.write(new byte[(responseSize - responseData.size())]);

			} else if (responseData.size() > responseSize) {
				throw new BufferOverflowException();
			}

		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		System.out.println("Sending response...");

		byte[] response = responseData.toByteArray();

		try {

			this.minecraftFetcher.serverSocket.send(new DatagramPacket(response, response.length, this.receivePacket.getSocketAddress()));

		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Response sent. Thread should be terminated now.");

	}

}
