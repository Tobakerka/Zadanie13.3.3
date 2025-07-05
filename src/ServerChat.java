import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ServerChat {

    ArrayList <Client> clients = new ArrayList<>();
    ServerSocket serverSocket;
    int port = 1212;

    public ServerChat() throws IOException {

        serverSocket = new ServerSocket(port);
    }

    public void run() throws IOException {

        while (true) {
            System.out.println("Ожидание подключения...");
            Socket socket = serverSocket.accept();
            System.out.println("Новый клиент подключен");
            Client client = new Client(socket);
            clients.add(client);
        }
    }

    class Client implements Runnable {

        InputStream is;
        OutputStream os;
        Scanner input;
        PrintStream output;
        String name = "";
        Socket socket;
        Client(Socket socket) {

            this.socket = socket;
            new Thread(this).start();

        }

        @Override
        public void run() {

            try {
                is = socket.getInputStream();
                os = socket.getOutputStream();

                input = new Scanner(is);
                output = new PrintStream(os);

                output.println("Добро пожаловать в чат!");
                while (name.equals("")) {
                    output.println("Введите имя:");
                    name = input.nextLine();
                }

                for (Client client : clients) {

                    client.output.println(client.name + " подключился к чату");
                }

                String message = "";

                while (!message.equals("exit")) {

                    message = input.nextLine();
                    switch (message) {

                        case "help": {
                            output.println("help - выводит список команд");
                            output.println("exit - отключается от чата");
                            break;
                        }
                        case "clients": {
                            output.println("Список клиентов:\n");
                            for (Client client : clients) {

                                output.println(name);
                            }
                            break;
                        }
                        case "exit": {
                            output.println("До свидания!");
                        }
                        default: {

                            for (Client client : clients) {

                                client.output.println(name + ": " + message);
                            }
                        }

                    }

                }

                for (Client client : clients) {

                    client.output.println(client.name + " отключился от чата");
                }

                is.close();
                os.close();
                socket.close();
                clients.remove(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
