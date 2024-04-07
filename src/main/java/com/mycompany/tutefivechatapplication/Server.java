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
import java.net.ServerSocket;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.io.IOException;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
//import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Server {

    private static final int port = 12455;
    private static final Logger logs = Logger.getLogger(Server.class.getName());
    private static ArrayList<Socket> clients = new ArrayList<Socket>();
    private static ArrayList<String> clientNames = new ArrayList<String>();
    private static final String exitCommand = "exit";
    private static final String privateMsgIndicator = "/pm";

    public static void main(String[] args) throws IOException {

        ServerSocket server = new ServerSocket(port);
        logs.info("Server Is Listining to the Port: " + port);

        while (true) {
            Socket clientData = server.accept();
            clients.add(clientData);

            Thread clientTread = new Thread(() -> handleClient(clientData));
            clientTread.start();
        }

    }

    private static void handleClient(Socket clientData) {

        try (
                /* A BufferedReader object named 'input' is created to read data from the client. It is initialized with an InputStreamReader which is constructed with the input stream of the client's socket. */
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientData.getInputStream())); /* A PrintWriter object named 'output' is created to send data to the client. It is initialized with the output stream of the client's socket. The second argument 'true' passed to the PrintWriter indicates that the PrintWriter should automatically flush its buffer after every write operation. */ PrintWriter writer = new PrintWriter(clientData.getOutputStream(), true);) {

            String getClientName = "Please Enter the Name: ";
            writer.println(getClientName);

            String clientName = reader.readLine();
            clientNames.add(clientName);

            String clientConnectedIndetifierMsg = clientName + " has connected to the server at" + getTheCurrentDateAndTime();
            logs.info(clientConnectedIndetifierMsg);

            broadCastMessages(clientConnectedIndetifierMsg, clientData);

            String clientMessage;

            while ((clientMessage = reader.readLine()) != null) {

                if (clientMessage.equalsIgnoreCase(exitCommand)) {
                    break;
                }

                if (clientMessage.toLowerCase().startsWith(privateMsgIndicator)) {
                    /*
                    ARRAY SPLIT METHOD EXAMPLE
                        _ = underScore
                        " " = empty space String
                    
                    String message = "Hy hellow World dsadf dsfes";
                    String[] array = message.split(" ", 3);
                    ["hy", "hellow", "World dsadf dsfes"];
                    
                    /pm samatha this message from samantha
                    
                    ["/pm", "samatha", "this message from samantha"]
                     */
                    String[] clientMessageParts = clientMessage.split(" ", 3);
                    String receiverName = clientMessageParts[1];
                    String privateMsg = clientMessageParts[2];
                    sendPrivateMessages(clientName, receiverName, privateMsg);

                } else {

                    clientConnectedIndetifierMsg = clientName + " : " + clientMessage;
                    logs.info(clientConnectedIndetifierMsg);
                    broadCastMessages(clientConnectedIndetifierMsg, clientData);
                }

            }

            clientNames.remove(clientName);
            clients.remove(clientData);

            clientConnectedIndetifierMsg = clientName + " : " + "Left the conversation";
            logs.info(clientConnectedIndetifierMsg);

            broadCastMessages(clientConnectedIndetifierMsg, null);

        } catch (IOException e) {
            logs.log(Level.SEVERE, "This error is encounter in handleClient method => " + e);

        }

    }

    private static String getTheCurrentDateAndTime() {
        String pattern = "yyyy-MM-dd HH:mm:ss";

        try {
            SimpleDateFormat standardDateFormatter = new SimpleDateFormat(pattern);
            return standardDateFormatter.format(new Date());

        } catch (Exception e) {
            logs.log(Level.SEVERE, "This Error is enountered in getTheCurrentDateAndTime Method => " + e);
            return "";
        }
    }

    private static void broadCastMessages(String message, Socket excludeClient) {

//        for (int i = 0; i < clients.size(); i++){
//            Socket client = clients.get(i);
//        }
        for (Socket clientSocket : clients) {

            System.err.println(clientSocket);

            if (clientSocket != excludeClient) {

                try {
                    PrintWriter outputMessage = new PrintWriter(clientSocket.getOutputStream(), true);
                    System.err.println("Testing");
                    outputMessage.println(message);

                } catch (IOException e) {
                    logs.log(Level.SEVERE, "This Error is enountered in broadCastMessages Method => " + e);
                }

            }

        }

    }

    private static void sendPrivateMessages(String userName, String receiverName, String messageToOther) {

        int receiverIndex = clientNames.indexOf(receiverName);

        if (receiverIndex != -1) {

            Socket receiveSocketDetails = clients.get(receiverIndex);

            try (PrintWriter outputMessage = new PrintWriter(receiveSocketDetails.getOutputStream(), true)) {

                String message = "[privete message from" + userName + "to" + receiverName + "]: " + messageToOther;
                outputMessage.println(message);

            } catch (IOException e) {
                logs.log(Level.SEVERE, "This Error is enountered in sendPrivateMessages Method => " + e);
            }

        }

    }
}
