import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main (String[] args) {

        mainMenu();
    }

    public static void mainMenu() {

        boolean isServer = true;
        while (isServer) {
            System.out.println("Меню:\n");

            System.out.println("1 - Запустить сервер");
            System.out.println("2 - Задать порт");
            System.out.println("3 - Выход");

            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            input.charAt(0);

            switch (input) {
                case "1": {

                    try {
                        new ServerChat().run();
                    } catch (IOException e) {
                        System.err.println("Ошибка: " + e);
                    }

                    break;
                }
                case "2": {

                    boolean isPort = true;

                    System.out.println("Введите порт: ");
                    Scanner scanPort = new Scanner(System.in);
                    String port = "0";
                    port = scanPort.nextLine();
                    if (Integer.parseInt(port) > 0 && Integer.parseInt(port) < 99999) {

                        isPort = false;
                        new ServerChat(Integer.parseInt(port)).run();
                    } else {
                        System.out.println("Неверный ввод порта");
                        isPort = true;
                    }
                    break;

                }
                case "3": {

                    System.out.println("Выход");
                    isServer = false;
                    break;
                }
                default: {
                    System.out.println("Неверный ввод");
                }
            }
        }

    }
}
