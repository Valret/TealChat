package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Grob on 3/11/17.
 *
 *
 */
public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println(new Object(){}.getClass().getEnclosingClass());
        Server server;
        Client client;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String consoleIn = br.readLine().toLowerCase();
        if(consoleIn.equals("s"))
            server = new Server();
        else if(consoleIn.equals("c"))
            client = new Client();
        else System.out.println("нахуй");
    }
}
