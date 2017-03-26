package core;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import com.google.gson.Gson;
import com.google.gson.reflect.*;
import java.io.FileWriter;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;

class Server {

    private BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in));
    private List<User> users = Collections.synchronizedList(new ArrayList<User>());
    private List<String> names,
                         sums;
    private Writer namesw,
                   sumsw;
    private Gson json = new Gson();
    private boolean empty = false;
    private Type type = new TypeToken<List<String>>() {}.getType();

    {
        try (
                FileReader frNames = new FileReader("Users");
                FileReader frSums = new FileReader("src//main//java//core//hashsum");
        ) {
            if (new File("Users").length() == 0
                    || new File("src//main//java//core//hashsum").length() == 0) {
                System.out.println("No errors, and file empty");
                sums = new ArrayList<String>();
                names = new ArrayList<String>();
            } else {
                empty = true;
                names = json.fromJson(frNames, type);
                sums = json.fromJson(frSums, type);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Server() {
        try {
            namesw = new FileWriter("Users");
            sumsw = new FileWriter("src//main//java//core//hashsum");
            ServerSocket server = new ServerSocket(27055);

            new Thread(() -> {
                try {
                    while(!(consoleIn.readLine()).equals("da nu nahui")) //just checks whether readLine is "da nu nahui" or smthing else

                    synchronized (users) {
                        for(User u : users) {
                            u.println("exit");
                        }
                    }
                    Thread.sleep(500);
                    json.toJson(names, namesw);
                    json.toJson(sums, sumsw);
                    namesw.close();
                    sumsw.close();
                    server.close();
                    System.exit(0);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

            while (true) {
                Socket socket = server.accept();
                User u = new User(socket);
                Thread t = new Thread(u);
                t.setDaemon(true);
                users.add(u);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String Encrypt(String sum) {
        return sum;
    }

    private String Decrypt(String sum) {
        return sum;
    }

    private boolean checkName(String name) {
        for(String n : names)
            if(n.equals(name))
                return true;
        return false;
    }

    private boolean checkSum(String sum) {
        String encrypted = Encrypt(sum);
        for(String s : sums)
            if(s.equals(encrypted))
                return true;
        return false;
    }

    private class User implements Runnable {
        private BufferedReader in;
        private PrintWriter out;
        private Socket connection;
        private String name;

        User(Socket connection) {
            this.connection = connection;
        }

        public String getName() {
            return name;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                out = new PrintWriter(connection.getOutputStream(), true);
                out.println("Type your username: ");
                name = in.readLine();
                {
                    String password = "";
                    if (empty) {
                        if (checkName(name)) {
                            out.println("Welcome back, " + name + ", type your password ");
                            password = in.readLine();
                            if (checkSum(name + password))
                                out.println("Hello, " + name);
                            else {
                                out.println("Wrong password");
                                close();
                            }
                        } else {
                            newUser();
                        }
                    } else {
                        newUser();
                    }
                }


                print4All("new user: " + name);


                String msg = "";
                while ((msg = in.readLine()) != null) {
                    print4All(name + ": " + msg);
                }
            } catch (IOException e) {
                //TODO smthing
            } finally {
                print4All(name + ": has left");
                close();
            }
        }

        private void println(String str) {
            out.println(str);
        }

        private void print4All(String str) {
            synchronized (users) {
                for (User u : users)
                    if (u != this) u.out.println(str);
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

        private void newUser() throws IOException {
            out.println("Hello, new user, type your password: ");
            String password = in.readLine();
            synchronized (users) {
                sums.add(Encrypt(name + password));
                names.add(name);
            }
        }
    }
}
