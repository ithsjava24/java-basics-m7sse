package org.example;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.Scanner;

public class App {

    public static int[] elpriser = new int[24];

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu();

            if (scanner.hasNextLine()) {
                String val = scanner.nextLine();

                switch (val.toLowerCase()) {
                    case "1":
                        inputPrices(scanner);
                        break;
                    case "2":
                        calculateMinMaxAvg();
                        break;
                    case "3":
                        sortPrices();
                        break;
                    case "4":
                        bestChargingTime();
                        break;
                    case "e":
                        System.out.print("Avslutar programmet...\n");
                        scanner.close();
                        return;
                    default:
                        System.out.print("Ogiltigt val, försök igen.\n");
                }
            } else {
                break;
            }
        }
    }

    private static void printMenu() {
        System.out.println("""
                Elpriser
                ========
                1. Inmatning
                2. Min, Max och Medel
                3. Sortera
                4. Bästa Laddningstid (4h)
                e. Avsluta
                """);
    }

    private static void inputPrices(Scanner scanner) {
        System.out.print("Ange elpriser för varje timme på dygnet (i öre per kWh):\n");
        for (int i = 0; i < 24; i++) {
            System.out.print("Ange elpris för timme " + i + "-" + (i + 1) + ": ");
            if (scanner.hasNextInt()) {
                elpriser[i] = scanner.nextInt();
            } else {
                System.out.print("Felaktig inmatning, försök igen.\n");
                scanner.next();
                i--;
            }
        }
        scanner.nextLine();
    }

    private static void calculateMinMaxAvg() {
        Locale.setDefault(Locale.forLanguageTag("sv-SE"));

        if (elpriser.length == 0) {
            System.out.print("Inga elpriser inmatade.\n");
            return;
        }

        int min = Arrays.stream(elpriser).min().orElse(Integer.MAX_VALUE);
        int max = Arrays.stream(elpriser).max().orElse(Integer.MIN_VALUE);
        double avg = Arrays.stream(elpriser).average().orElse(Double.NaN);

        String minHour = "";
        String maxHour = "";
        for (int i = 0; i < elpriser.length; i++) {
            if (elpriser[i] == min && minHour.isEmpty()) {
                minHour = formatTime(i);
            }
            if (elpriser[i] == max && maxHour.isEmpty()) {
                maxHour = formatTime(i);
            }
            if (!minHour.isEmpty() && !maxHour.isEmpty()) {
                break;
            }
        }

        System.out.printf("Lägsta pris: %s, %d öre/kWh\n", minHour, min);
        System.out.printf("Högsta pris: %s, %d öre/kWh\n", maxHour, max);
        System.out.printf("Medelpris: %.2f öre/kWh\n", avg);
    }

    private static void sortPrices() {
        if (elpriser.length == 0) {
            System.out.print("Inga elpriser inmatade.\n");
            return;
        }

        Integer[][] timeAndPrices = new Integer[24][2];
        for (int i = 0; i < elpriser.length; i++) {
            timeAndPrices[i][0] = i;
            timeAndPrices[i][1] = elpriser[i];
        }

        Arrays.sort(timeAndPrices, Comparator.comparingInt(a -> -a[1]));

        System.out.print("Elpriser sorterade från dyrast till billigast:\n");
        for (Integer[] timePrice : timeAndPrices) {
            System.out.printf("%02d-%02d %d öre\n", timePrice[0], timePrice[0] + 1, timePrice[1]);
        }
    }

    private static void bestChargingTime() {
        Locale.setDefault(Locale.forLanguageTag("sv-SE"));

        if (elpriser.length < 4) {
            System.out.print("Inte tillräckligt med data för att hitta bästa laddningstid.\n");
            return;
        }

        int minSum = Integer.MAX_VALUE;
        int bestStartIndex = 0;

        for (int i = 0; i <= elpriser.length - 4; i++) {
            int sum = elpriser[i] + elpriser[i + 1] + elpriser[i + 2] + elpriser[i + 3];
            if (sum < minSum) {
                minSum = sum;
                bestStartIndex = i;
            }
        }

        double avg = minSum / 4.0;
        System.out.printf("Påbörja laddning klockan %02d\n", bestStartIndex);
        System.out.printf("Medelpris 4h: %.1f öre/kWh\n", avg);
    }

    private static String formatTime(int hour) {
        return String.format("%02d-%02d", hour, hour + 1);
    }
}
