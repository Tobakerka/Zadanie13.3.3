import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

public class ServerChat {

    ArrayList <Client> clients = new ArrayList<>();
    ServerSocket serverSocket;
    int port = 1212;
    String ip = "212.192.43.73";

    public ServerChat() throws IOException {

        try {

            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            new CustomException("не удалось создать сокет сервера");
        }
    }

    public ServerChat(int port) {

        try {
        serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            new CustomException("не удалось создать сокет сервера");
        }
        this.port = port;
        this.ip = ip;
    }

    public void run() {

        while (true) {

            System.out.println("Адрес сервера : " + ip);
            System.out.println("Сервер запущен на порту " + port);
            System.out.println("Ожидание подключения...");

            try {
                Socket socket = serverSocket.accept();
                System.out.println("Новый клиент подключен");
                Client client = new Client(socket);
                clients.add(client);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                System.out.println("Сервер завершил работу");
            }
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

        public void run() {

            Date date = new Date();
            SimpleDateFormat formater = new SimpleDateFormat("HH:mm:ss");
            System.out.println("Клиент: " + this.socket.toString() + " подключился в " + formater.format(date));

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

                String hour = formater.format(date);
                for (Client client : clients) {

                    client.output.println(hour + ": " + this.name + " подключился к чату");

                }

                String message = "";

                while (!message.equals("exit")) {

                    Date dateChat = new Date();
                    formater = new SimpleDateFormat("HH:mm:ss");
                    message = input.nextLine();
                    hour = formater.format(dateChat);
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

                            is.close();
                            os.close();
                            socket.close();
                            clients.remove(this);

                            break;
                        }
                        default: {

                            for (Client client : clients) {

                                client.output.println(hour + ": " + name + ": " + message);
                            }
                        }

                    }

                }

                for (Client client : clients) {

                    client.output.println(this.name + " отключился от чата");
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
