/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.tutefivechatapplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.net.Socket;

/**
 *
 * @author pramesh
 */
public class HandleClient implements Runnable {

    private Socket clientData;
    private static final Logger logs = Logger.getLogger(HandleClient.class.getName());
    String dateAndTime;
    ArrayList<String> clientNames;
    ArrayList<Socket> clients;

    HandleClient(Socket clientData, String dateAndTime, ArrayList<String> clientNames , ArrayList<Socket> clients ) {
        this.clientData = clientData;
        this.clientNames = clientNames;
        this.dateAndTime = this.dateAndTime;
        this.clients = clients;
    }

    @Override
    public void run() {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientData.getInputStream())); PrintWriter writer = new PrintWriter(clientData.getOutputStream(), true);) {

            String getClientName = "Please Enter the Name: ";
            writer.println(getClientName);

            String clientName = reader.readLine();
            clientNames.add(clientName);

            String clientConnectedIndetifierMsg = clientName + "has connected to the server at" + this.dateAndTime;
            logs.info(clientConnectedIndetifierMsg);

            broadCastMessages(clientConnectedIndetifierMsg, clientData);

        } catch (IOException e) {
            logs.log(Level.SEVERE, "This error is encounter in handleClient method => " + e);

        }

    }

    private void broadCastMessages(String message, Socket excludeClient) {

//        for (int i = 0; i < clients.size(); i++){
//            Socket client = clients.get(i);
//        }
        for (Socket client : clients) {

            if (client != excludeClient) {

                try (PrintWriter outputMessage = new PrintWriter(client.getOutputStream(), true)) {

                    outputMessage.println(message);

                } catch (IOException e) {
                    logs.log(Level.SEVERE, "This Error is enountered in broadCastMessages Method => " + e);
                }

            }

        }

    }

}
