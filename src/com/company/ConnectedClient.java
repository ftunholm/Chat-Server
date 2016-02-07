package com.company;

import java.io.*;
import java.net.Socket;

/**
 * Created by LanfeaR on 2016-02-07.
 */
public class ConnectedClient implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private BufferedWriter out;
    private String nickname;

    public ConnectedClient(Socket clientSocket) throws IOException {
        nickname = "";
        this.clientSocket = clientSocket;
        this.out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), "UTF-8"));
        write("NICK?");
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String line;
        try {
            InputHelper helper = new InputHelper(this);
            while (true) {
                line = in.readLine();
                helper.processInput(line);
                if (in.read() == -1) {
                    disconnect();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String msg) throws IOException {
        out.write(msg + "\r\n");
        out.flush();
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getNickname() {
        return this.nickname;
    }
    private void disconnect() throws IOException {
        ServerConnection.removeClient(getNickname());
        clientSocket.close();
        ServerConnection.broadcastMessage("QUIT " + getNickname());
    }
}
