package me.johnnywoof.client;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.BufferOverflowException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

public class ClientExample {

	private static final List<String> quitCmds = Arrays.asList("quit", "stop", "exit");

	public static void main(String[] args) throws Exception {

		Charset charset = Charset.forName("UTF-8");

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		DatagramSocket clientSocket = new DatagramSocket();

		System.out.println("Please enter the server's IP address:");

		InetAddress ip = InetAddress.getByName(br.readLine());

		System.out.println("Please enter the server's port number:");

		int serverPort = Integer.parseInt(br.readLine());

		String input;

		System.out.println("Ready to accept commands. Type any player's username or UUID for a conversion.");

		while ((input = br.readLine()) != null) {

			if (quitCmds.contains(input.toLowerCase())) {
				break;
			}

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			PrintWriter w = new PrintWriter(new OutputStreamWriter(out, charset));

			w.println("PROFILE");
			w.println(input);

			w.close();

			System.out.println("Fetching profile for \"" + input + "\"...");

			//Filler
			if (out.size() < 128) {
				out.write(new byte[(128 - out.size())]);
			} else if (out.size() > 128) {
				throw new BufferOverflowException();
			}

			out.close();

			byte[] sendData = out.toByteArray();

			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, serverPort);
			clientSocket.send(sendPacket);

			byte[] receiveData = new byte[128];

			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receivePacket);

			BufferedReader rr = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(receivePacket.getData()), charset));

			String l;

			System.out.println("---Server response---");

			while ((l = rr.readLine()) != null && !l.isEmpty()) {

				System.out.println(l);

			}

			System.out.println("----------------");

			rr.close();

		}

		clientSocket.close();

		System.out.println("Thank you and goodbye.");

		System.exit(0);

	}

}
