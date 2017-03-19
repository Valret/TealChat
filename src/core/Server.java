package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

class Server {

    private List<User> users = Collections.synchronizedList(new ArrayList<User>());

    public Server() {
        try {
                ServerSocket server = new ServerSocket(27055);
                while(true) {
                    User u = new User(server.accept());
                    Thread t = new Thread(u);
                    users.add(u);
                    t.start();
                }
                //closeAll();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void closeAll() {
    }

    private class User implements Runnable {

        private BufferedReader in;
        private PrintWriter out;
        private Socket connection;
        private String name;

        User(Socket connection) {
            this.connection = connection;
        }
        

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                out = new PrintWriter(connection.getOutputStream(), true);
                name = in.readLine();


                print4All("new user: " + name);

                while(true) {
                    String str = in.readLine();
                    print4All(name + ": " + str);
                }
            } catch(IOException e) {}
            finally {
                print4All(name + ": has left");
                close();
            }
        }

        private void print4All(String str) {
            synchronized (users) {
                for(User u : users)
                    if(u != this) u.out.println(str);
            }
            System.out.println(str);
        }

        private void close() {
            try {
                in.close();
                out.close();
                connection.close();
                users.remove(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
