package me.johnnywoof.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
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

			System.out.println("Sending data: \"" + input + "\"");

			//Filler
			while (input.length() < 36) {
				input += " ";
			}

			byte[] sendData = input.getBytes(charset);

			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip, serverPort);
			clientSocket.send(sendPacket);

			byte[] receiveData;

			if (input.length() <= 16) {
				receiveData = new byte[32];//UUID
			} else {
				receiveData = new byte[16];//Username
			}

			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receivePacket);

			String modifiedSentence = new String(receivePacket.getData(), charset);
			System.out.println("Server's response: " + modifiedSentence);

		}

		clientSocket.close();

		System.out.println("Thank you and goodbye.");

		System.exit(0);

	}

}
