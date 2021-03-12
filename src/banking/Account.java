package banking;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Account extends Object{
    private String cardNumber = "400000";
    private String PIN = "";
    private int balance = 0;

    public int digitsSum(int[] digits) {
        int sum = 0;
        for (int i = 0; i < digits.length; i ++) {
            sum += digits[i];
        }
        return sum;
    }

    public int[] numberLuhn(String cardNumber) {
        int digits[] = new int[15];
        for (int i = 0; i < 15; i++) {
            digits[i] = Integer.parseInt(String.valueOf(cardNumber.charAt(i)));

            if ((i + 1) % 2 == 1) {
                digits[i] *= 2;
            }

            if (digits[i] > 9) {
                digits[i] -= 9;
            }
        }
        return digits;
    }

    public boolean algLuhn(String cardNumber) {
        if(cardNumber.length() == 16) {
            int sum = digitsSum(numberLuhn(cardNumber));
            if ((sum + Integer.parseInt(String.valueOf(cardNumber.charAt(15)))) % 10 == 0) {
                return true;
            }
        }
        return false;
    }

    private void generateCardNumber() {
        Random random = new Random();

        for (int i = 0; i < 9; i ++) {
            cardNumber += String.valueOf(random.nextInt(10));
        }

        int sum = digitsSum(numberLuhn(cardNumber));
        int lastDigit = 0;
        while ((sum + lastDigit) % 10 != 0) {
            lastDigit++;
        }
        cardNumber += String.valueOf(lastDigit);
    }

    private void generatePIN() {
        Random random = new Random();
        for (int i = 0; i < 4; i ++) {
            PIN += String.valueOf(random.nextInt(10));
        }
    }

    private void saveInFile() {
        try (FileWriter fileWriter= new FileWriter("./Accounts.txt", true)) {
            fileWriter.write(cardNumber + " " + PIN + "\n");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public Account() {
        generateCardNumber();
        generatePIN();
        saveInFile();
    }


    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setPIN(String PIN) {
        this.PIN = PIN;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getPIN() {
        return PIN;
    }

    @Override
    public String toString() {
        return "Your card number:\n" + cardNumber + "\nYour card PIN:\n" + PIN;
    }
}
