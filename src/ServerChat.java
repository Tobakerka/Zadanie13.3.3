import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

import static java.lang.Thread.sleep;

public class ServerChat {

    ArrayList <Client> clients = new ArrayList<>();
    ServerSocket serverSocket;
    int port = 1212;
    String ip = "192.168.1.188";

    public ServerChat() throws IOException {

        try {

            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            if (serverSocket == null) {
                throw new IOException("Не удалось создать сокет сервера");
            }
        }

    }

    public ServerChat(String adress, int port) throws CustomException {

        this.ip = adress;
        try {
        serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new CustomException("не удалось создать сокет сервера");
        }
        this.port = port;


    }

    public void run() throws CustomException {

        System.out.println("Адрес сервера : " + ip);
        System.out.println("Сервер запущен на порту " + port);
        while (true) {

            System.out.println("Ожидание подключения...");

            try {
                Socket socket = serverSocket.accept();
                System.out.println("Новый клиент подключен");
                Client client = new Client(socket);
                clients.add(client);
            } catch (IOException e) {
                new CustomException("подключение клиента не удалось");
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
            Thread thread = new Thread(this);
            thread.start();
        }

        public void run() {
            Thread threadDemon = new Thread(new Runnable() {

                @Override
                public void run() {

                    try {
                        while (true) {
                            sleep(1000);
                            int read = socket.getInputStream().read();
                            if (read == -1) {
                                String tempName = clients.get(clients.indexOf(Client.this)).name;
                                for (Client client : clients) {
                                    client.output.println(tempName + " отключился от чата");
                                }
                                clients.remove(Client.this);
                                is.close();
                                os.close();
                            }
                        }
                    } catch (IOException e) {
                        System.out.println("Клиент отключился");
                    } catch (RuntimeException e) {
                        System.out.println("Разорвано соединение");
                    } catch (InterruptedException e) {
                        System.out.println("Ошибка потока");
                    }

                }
            });
            threadDemon.setDaemon(true);
            threadDemon.start();

            Date date = new Date();
            SimpleDateFormat formater = new SimpleDateFormat("HH:mm:ss");


            try {
                is = socket.getInputStream();
                os = socket.getOutputStream();

                input = new Scanner(is);
                output = new PrintStream(os);

                output.println("Добро пожаловать в чат!");
                output.println("Общий чат: ");
                while (name.equals("")) {

                    boolean isName = true;
                    output.println("Введите имя:");
                    String tempName = input.nextLine();

                    for (Client client : clients) {
                        if (tempName.equals(client.name)) {
                            output.println("Введите другое имя!");
                            isName = false;
                        }
                    }

                    if (!isName) {
                        name = "";
                        isName = true;
                    } else {
                        name = tempName;
                    }
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
                    if (message.equals("")) {
                        continue;
                    }

                    hour = formater.format(dateChat);
                    switch (message) {

                        case "*помощь": {
                            output.println("*помощь - выводит список команд");
                            output.println("*клиенты - выводит список клиентов");
                            output.println("*лс - отправка личного сообщения");
                            output.println("*комната - создание комнаты");
                            output.println("*выход - отключается от чата");
                            break;
                        }
                        case "*клиенты": {
                            output.println("Список клиентов:\n");
                            output.println("****************************************");
                            for (Client client : clients) {

                                output.println(client.name);
                            }
                            output.println("****************************************");
                            break;
                        }
                        case "*лс" : {

                            output.println();
                            output.println("Список клиентов:\n");
                            Client clientLs = null;
                            output.println("****************************************");

                            for (Client client : clients) {

                                output.println(client.name);
                            }
                            output.println("****************************************");

                            output.println();
                            System.out.println("Введите имя клиента: ");

                            String nameClientPrivate = input.nextLine();

                            boolean isClient = false;
                            for (Client client : clients) {

                                if (nameClientPrivate.equals(client.name)) {
                                    isClient = true;
                                    clientLs = client;
                                    output.print(client.name+ ": сообщение: ");
                                    String privateMassege = input.nextLine();
                                    if (privateMassege.equals("")) {
                                        output.println("Отмена отправки сообщения\n\tОбщий чат: ");
                                        continue;
                                    }
                                    clientLs.output.println("Вам личное сообщение от: " + this.name + "\n\rЛС: " + formater.format(dateChat) + " : " + privateMassege);
                                    this.output.println("личное сообщение отправлено: " + clientLs.name);
                                    output.println("Общий чат: ");
                                }
                            }

                            if (isClient == false) {
                                output.println("Такого клиента нет!");
                            }


                            break;
                        }
                        case "*комната" : {

                            output.println();
                            output.println("Список клиентов:\n");
                            Client clientLs = null;
                            output.println("****************************************");

                            for (Client client : clients) {

                                output.println(client.name);
                            }
                            output.println("****************************************");

                            output.println();
                            System.out.println("Введите имя клиента к которому нужно подключиться: ");

                            String nameClientPrivate = input.nextLine();

                            boolean isClient = false;
                            for (Client client : clients) {

                                if (nameClientPrivate.equals(client.name)) {
                                    isClient = true;
                                    clientLs = client;

                                    String privateMessage = "";
                                    output.println("для выхода введите ***");
                                    while (!privateMessage.equals("***")) {
                                        output.print(client.name + ": сообщение: ");
                                        privateMessage = input.nextLine();
                                        if (privateMessage.equals("")) {
                                            continue;
                                        }
                                        clientLs.output.println("Вам личное сообщение от: " + this.name + "\n\rЛС: " + formater.format(dateChat) + " : " + privateMessage);
                                        this.output.println("личное сообщение отправлено: " + clientLs.name);
                                    }
                                    output.println("Общий чат: ");
                                }
                            }

                            if (isClient == false) {
                                output.println("Такого клиента нет!");
                            }

                            break;
                        }
                        case "*выход": {

                            output.println("До свидания!");

                            for (Client client : clients) {

                                client.output.println(this.name + " отключился от чата");
                            }


                            is.close();
                            os.close();
                            input.close();
                            output.close();
                            socket.close();
                            clients.remove(this);

                            return; // выход из цикла
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
                input.close();
                output.close();
                socket.close();
                clients.remove(this);
                System.out.println("Клиент отключился");
            } catch (RuntimeException e) {
                new CustomException("ошибка ввода/вывода");
            } catch (IOException e) {
                new CustomException("ошибка ввода/вывода");
            }
        }
    }

}
