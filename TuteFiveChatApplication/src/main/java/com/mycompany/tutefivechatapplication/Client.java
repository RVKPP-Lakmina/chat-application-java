/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tutefivechatapplication;

/**
 *
 * @author pramesh
 */
import java.net.Socket;
import java.io.IOException;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Client {

    private static final String host = "localHost";
    private static final int port = 12455;
    private static final String privateMsgIndicator = "/pm";
    private static final Logger logs = Logger.getLogger(Client.class.getName());
    private static final String exitCommand = "exit";

    public static void main(String[] args) {

        try (Socket clientSocket = new Socket(host, port)) {
            logs.info("The Client is conneted to" + host + port);

            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter writter = new PrintWriter(clientSocket.getOutputStream(), true);

            logs.info(reader.readLine());

            Scanner inputData = new Scanner(System.in);

            String name = inputData.nextLine();

            writter.println(name);

            Thread serverListener = new Thread(() -> {
                try {
                    /* This is a loop where the client continuously reads messages from the server and prints them to the console. */
                    String serverMessage;
                    while ((serverMessage = reader.readLine()) != null) {
                        System.out.println(serverMessage);
                    }
                } catch (IOException e) {
                    /* Any IOExceptions are caught and their stack trace is printed to the console. */
                    logs.log(Level.SEVERE, "Error is Encountered in main Method" + e);
                }
            });
            serverListener.start();

            String message;

            while (true) {
                logs.info("\"Enter your message (type 'exit' to leave or '/pm [name] [message]' for private chat): ");

                message = inputData.nextLine();

                writter.println(message);

                if (message.toLowerCase().equals(exitCommand)) {
                    break;
                }

            }

            inputData.close();
            clientSocket.close();

        } catch (IOException e) {
            logs.log(Level.SEVERE, "Error is Encountered in main Method" + e);
        }
    }

    private static void clientMessageHandler(BufferedReader reader) {
        String serverMessage;
        try {

            while ((serverMessage = reader.readLine()) != null) {

                logs.info(serverMessage);
            }

        } catch (IOException e) {
            logs.log(Level.SEVERE, "Error is Encountered in clientMessageHandler Method" + e);
        }

    }

}
