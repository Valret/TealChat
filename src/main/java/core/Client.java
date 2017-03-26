package core;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.InetAddress;

class Client {
    private Socket         server;
    private PrintWriter    out;
    private BufferedReader consoleIn,
                           in;

    Client() throws IOException {
        try {
            String ip = "127.0.0.1";
            consoleIn = new BufferedReader(
                    new InputStreamReader(System.in));

            System.out.println("Connect via: default - localhost, 'i' - custom ip, 't' - turing's ip");
            switch (consoleIn.readLine().toLowerCase()) {
                case "i": {
                    System.out.println("write the ip adress");
                    ip = consoleIn.readLine();
                }
                break;
                case "t":
                    ip = "83.24.99.149";
            }

            server = new Socket(ip, 27055);
            in = new BufferedReader(
                    new InputStreamReader(server.getInputStream()));
            out = new PrintWriter(server.getOutputStream(), true);
            System.out.println("connected");



            Thread getter = new Thread(new MsgGetter());
            getter.start();

            String str = "";
            while (true) out.println(consoleIn.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            close();
        }
    }

    private void close() {
        try {
            server.close();
            in.close();
            out.close();
            out.println("exit");
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MsgGetter implements Runnable {
        public void run() {
            try {
                String msg = "";
                while(!(msg = in.readLine()).equals("exit")) System.out.println(msg);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                close();
            }
        }
    }
}
