package me.johnnywoof;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Map;
import java.util.UUID;

public class MinecraftFetcher extends Thread {

	private final String ip;
	private final int port;
	public final Map<UUID, String> cache = new CacheMap<>(60000);
	public final DatagramSocket serverSocket;

	public MinecraftFetcher(String ip, int port) throws SocketException {

		this.ip = ip;
		this.port = port;
		this.serverSocket = new DatagramSocket(null);

	}

	@Override
	public void run() {

		try {

			this.serverSocket.bind(new InetSocketAddress(ip, port));

		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(-1);
			return;
		}

		byte[] byteData = new byte[128];

		while (Start.RUNNING) {

			System.out.println("Awaiting incoming data...");

			DatagramPacket receivePacket = new DatagramPacket(byteData, byteData.length);

			try {

				serverSocket.receive(receivePacket);

			} catch (IOException e) {
				e.printStackTrace();
				/*Corrupt packet?*/
				continue;
			}

			System.out.println("Fully read byte array input stream. Starting a client handler thread...");

			new ClientHandler(this, receivePacket).start();

		}

		serverSocket.close();

	}

}
