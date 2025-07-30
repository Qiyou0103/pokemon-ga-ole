import java.util.Scanner;

public class CatchSystem {
    public static boolean tryCatch(Pokemon pokemon, PokeBall ball, Scanner scanner) {
        System.out.println("\n--- Catching Mini-Game! ---");
        System.out.println("Press 'C' repeatedly to fill the catch meter!");
        int pressesNeeded = 5; // Number of successful presses for a good catch attempt
        long timeLimit = 3000; // 3 seconds

        int successfulPresses = 0;
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < timeLimit && successfulPresses < pressesNeeded) {
            System.out.print("Press 'C'! ");
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("C")) {
                successfulPresses++;
                System.out.println("Got it!");
            } else {
                System.out.println("Miss!");
            }
        }

        double qteBonus = (double) successfulPresses / pressesNeeded; // 0.0 to 1.0
        System.out.println("Mini-game result: " + (int)(qteBonus * 100) + "% success!");

        double catchProbability = pokemon.getCatchRate() * ball.getCatchModifier() * (0.5 + 0.5 * qteBonus);
        System.out.printf("Base Catch Rate: %.2f, Ball Modifier: %.2f, QTE Bonus: %.2f%n", 
                          pokemon.getCatchRate(), ball.getCatchModifier(), (0.5 + 0.5 * qteBonus));
        System.out.printf("Final Catch Probability: %.2f%n", catchProbability);

        return Math.random() < catchProbability;
    }
}