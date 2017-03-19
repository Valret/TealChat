package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


class Main {
    public static void main(String[] args) throws IOException {
        System.out.println(new Object(){}.getClass().getEnclosingClass());
        Server server;
        Client client;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String mode = br.readLine().toLowerCase();
        switch(mode) {
            case "s":
                server = new Server();
                break;
            case "c":
                client = new Client();
                break;
            default:
                System.out.println("нах*й");
        }
    }
}
