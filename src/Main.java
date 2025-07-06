import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main (String[] args) {

        while (true) {
            try {
                mainMenu();
            } catch (CustomException e) {
                System.err.println("Ошибка " + e.getMessage());
            }
        }
    }

    public static void mainMenu() throws CustomException {

        boolean isServer = true;
        while (isServer) {
            System.out.println("Меню:\n");

            System.out.println("1 - Запустить сервер на стандартном порту: 1212 и стандантному адресу 192.168.1.188");
            System.out.println("2 - Задать порт и адрес");
            System.out.println("0 - Выход");

            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            input.charAt(0);

            switch (input) {
                case "1": {

                    try {

                        new ServerChat().run();

                    } catch (IOException e) {
                        throw new CustomException("при запуске сервера");
                    }

                    break;
                }
                case "2": {

                    String address = "192.168.1.188";
                    System.out.print("Введите адрес: ");
                    Scanner scanAddress = new Scanner(System.in);
                    address = scanAddress.nextLine();


                    Pattern pattern = Pattern.compile("^[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}$");
                    Matcher matcher = pattern.matcher(address);
                    if (!matcher.find()) {

                        throw new CustomException("Неверный адрес");
                    }

                    System.out.print("Введите порт: ");
                    Scanner scanPort = new Scanner(System.in);
                    String port = "0";

                    if (scanPort.hasNextInt()) {

                        port = scanPort.nextLine();
                        if (Integer.parseInt(port) <0 || Integer.parseInt(port) > 99999) {

                            throw new CustomException("Неверный порт");
                        }
                        new ServerChat(address, Integer.parseInt(port)).run();
                    } else {

                        throw new CustomException("Неверный порт");
                    }

                    break;

                }
                case "0": {

                    System.out.println("Выход");
                    isServer = false;
                    System.exit(0);
                    break;
                }
                default: {
                    System.out.println("Неверный ввод");
                }
            }
        }

    }
}
