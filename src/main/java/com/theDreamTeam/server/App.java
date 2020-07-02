package com.theDreamTeam.server;

import java.io.IOException;
import java.util.Scanner;

public class App {

    public static final DataBase database = new DataBase();

    static int port = 6666;

    public static void main(String[] args) {
        try {
            Server server = new Server(port);
            DataBase.connect();
            System.out.println("Connected To Database.");
            server.listen();
            System.out.println("Server Is Listening To Port #" + port + " ...");
        } catch (IOException e) {
            System.out.println("Cannot Run Server.");
        }
    }


}
