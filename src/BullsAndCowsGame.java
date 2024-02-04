import java.util.Scanner;
import java.util.Random;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

public class BullsAndCowsGame {
    private static final Logger LOGGER = Logger.getLogger(BullsAndCowsGame.class.getName());
    private static final String LOG_FILE_NAME = "BullsAndCowsGame.log";

    private static final int CODE_LENGTH = 5;
    private static final char[] ALPHABET_EN = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final char[] ALPHABET_RU = "абвгдежзиклмнопрстуфхцчшщьыъэюя".toCharArray();
    private static final char[] DIGITS = "0123456789".toCharArray();

    private char[] secretCode;
    private int maxAttempts;
    private int attempts;
    private ArrayList<String> history;

    public BullsAndCowsGame(int maxAttempts) {
        this.maxAttempts = maxAttempts;
        initializeGame();
    }

    private void initializeGame() {
        secretCode = generateSecretCode();
        attempts = 0;
        history = new ArrayList<>();

        // Configure logging
        try {
            FileHandler fileHandler = new FileHandler(LOG_FILE_NAME);
            LOGGER.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
        } catch (Exception e) {
            LOGGER.warning("Failed to configure logging");
        }
    }

    private char[] generateSecretCode() {
        char[] code = new char[CODE_LENGTH];
        Random random = new Random();

        for (int i = 0; i < CODE_LENGTH; i++) {
            int choice = random.nextInt(3); // 0 - буква (EN), 1 - буква (RU), 2 - цифра
            switch (choice) {
                case 0:
                    code[i] = ALPHABET_EN[random.nextInt(ALPHABET_EN.length)];
                    break;
                case 1:
                    code[i] = ALPHABET_RU[random.nextInt(ALPHABET_RU.length)];
                    break;
                case 2:
                    code[i] = DIGITS[random.nextInt(DIGITS.length)];
                    break;
            }
        }

        return code;
    }

    private String getUserInput() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите ваш вариант кода: ");
        return scanner.nextLine().toLowerCase(); // Приводим к нижнему регистру для удобства сравнения
    }

    private boolean isValidInput(String input) {
        return input.matches("[a-zа-я0-9]{" + CODE_LENGTH + "}");
    }

    private int countBulls(char[] guess) {
        int bulls = 0;
        for (int i = 0; i < CODE_LENGTH; i++) {
            if (guess[i] == secretCode[i]) {
                bulls++;
            }
        }
        return bulls;
    }

    private int countCows(char[] guess) {
        int cows = 0;
        for (int i = 0; i < CODE_LENGTH; i++) {
            for (int j = 0; j < CODE_LENGTH; j++) {
                if (i != j && guess[i] == secretCode[j]) {
                    cows++;
                }
            }
        }
        return cows;
    }

    private void logGuess(String guess, int bulls, int cows) {
        history.add(String.format("Попытка %d: %s - Быки: %d, Коровы: %d", attempts, guess, bulls, cows));
        LOGGER.info(String.format("Попытка %d: %s - Быки: %d, Коровы: %d", attempts, guess, bulls, cows));
    }

    public void playGame() {
        System.out.println("Добро пожаловать в игру 'Быки и Коровы'!");
        System.out.println("Правила: введите пять букв или цифр, чтобы угадать код.");

        while (attempts < maxAttempts) {
            String guess = getUserInput().toLowerCase();

            if (!isValidInput(guess)) {
                System.out.println("Некорректный ввод. Пожалуйста, введите пять букв или цифр.");
                continue;
            }

            attempts++;
            int bulls = countBulls(guess.toCharArray());
            int cows = countCows(guess.toCharArray());

            logGuess(guess, bulls, cows);

            System.out.println("Быки: " + bulls + ", Коровы: " + cows);

            if (bulls == CODE_LENGTH) {
                System.out.println("Поздравляем! Вы угадали код " + new String(secretCode) + " с " + attempts + " попыток.");
                break;
            }
        }

        if (attempts == maxAttempts) {
            System.out.println("Игра окончена. Вы использовали все попытки. Код: " + new String(secretCode));
        }

        printHistory();
    }

    private void printHistory() {
        System.out.println("История игры:");
        for (String entry : history) {
            System.out.println(entry);
        }
    }

    public static void main(String[] args) {
        System.out.print("Введите максимальное количество попыток: ");
        Scanner scanner = new Scanner(System.in);
        int userMaxAttempts = scanner.nextInt();

        BullsAndCowsGame game = new BullsAndCowsGame(userMaxAttempts);
        game.playGame();
    }
}
