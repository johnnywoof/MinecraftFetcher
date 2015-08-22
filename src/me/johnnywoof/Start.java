package me.johnnywoof;

import java.net.SocketException;
import java.util.Scanner;

public class Start {

	public static boolean RUNNING = true;

	public static void main(String[] args) throws SocketException {

		if (args.length < 2) {
			System.out.println("Command line usage: java -jar MinecraftFetcher.jar <Host or IP address to bind to> <port to run on>");
			System.exit(0);
			return;
		}

		System.out.println("Starting the minecraft fetcher server version 1.0...");

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

}
