package banking;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Account account = null;
        String cardNumber;
        String PIN;
        int menu = -1;
        CardConnection cardConnection = CardConnection.getCardConnection(args[1]);
        cardConnection.createTable();
        do {
            Scanner scanner = new Scanner(System.in);

            System.out.print("1. Create an account\n" +
                    "2. Log into account\n" +
                    "0. Exit\n" +
                    ">");

            menu = scanner.nextInt();
            switch (menu) {
                case 1:
                    account = new Account();
                    System.out.println("\nYour card has been created");
                    System.out.println(account);
                    System.out.println("");
                    cardConnection.insertData(account.getCardNumber(), account.getPIN(), account.getBalance());
                    break;
                case 2:
                    System.out.print("\nEnter your card number:\n>");
                    scanner.nextLine();
                    cardNumber = scanner.nextLine();
                    System.out.print("Enter your PIN:\n>");
                    PIN = scanner.nextLine();


                    if (cardConnection.checkCustomer(cardNumber, PIN)) {
                        int semiMenu = 0;
                        account = cardConnection.getAccount(cardNumber);
                        System.out.println("\nYou have successfully logged in!\n");
                        do {
                            System.out.print("1. Balance\n" +
                                    "2. Add income\n" +
                                    "3. Do transfer\n" +
                                    "4. Close account\n" +
                                    "5. Log out\n" +
                                    "0. Exit\n" +
                                    ">");
                            semiMenu = scanner.nextInt();
                            switch (semiMenu) {
                                case 1:
                                    System.out.println("\nBalance: " + account.getBalance() + "\n");
                                    break;
                                case 2:
                                    System.out.print("\nEnter income:\n>");
                                    account.setBalance(account.getBalance() + scanner.nextInt());
                                    cardConnection.addIncome(account.getCardNumber(), account.getBalance());
                                    System.out.println("Income was added!\n");
                                    break;
                                case 3:
                                    System.out.println("\nTransfer");
                                    System.out.print("Enter card number:\n>");
                                    scanner.nextLine();
                                    cardNumber = scanner.nextLine();

                                    if (account.algLuhn(cardNumber)) {
                                        if (cardConnection.findCard(cardNumber)) {
                                            System.out.print("Enter how much money you want to transfer:\n>");
                                            int transferBalance = scanner.nextInt();
                                            if (transferBalance <= account.getBalance()) {
                                                cardConnection.transferMoney(cardNumber, account.getCardNumber(), transferBalance);
                                                System.out.println("Success!\n");
                                            } else {
                                                System.out.println("Not enough money!");
                                            }
                                        } else {
                                            System.out.println("Such a card does not exist.");
                                        }
                                    } else {
                                        System.out.println("Probably you made a mistake in the card number. Please try again!\n");
                                    }
                                    break;
                                case 4:
                                    cardConnection.closeAccount(account.getCardNumber());
                                    System.out.println("\nThe account has been closed!\n");
                                case 5:
                                    System.out.println("\nYou have successfully logged out!\n");
                                    break;
                                case 0:
                                    menu = 0;
                                    System.out.println("\nBye!");
                                    cardConnection.closeConnection();
                                    break;
                                default:
                                    break;
                            }
                        } while (semiMenu != 0 && semiMenu != 5);
                    } else {
                        System.out.println("Wrong card number or PIN!");
                    }

                    break;
                case 0:
                    System.out.println("\nBye!");
                    cardConnection.closeConnection();
                    break;
                default:
                    break;
            }
        } while (menu != 0);
    }
}