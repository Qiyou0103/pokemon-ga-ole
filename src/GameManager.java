import java.io.*;
import java.util.*;
public class GameManager {    
    private Player player;    
    private String currentLocation;    
    private int coins;    
    private final Scanner scanner;    
    private static final String SCORES_FILE = "data" + File.separator + "scores.txt";    
    public GameManager(Scanner scanner) {        
        this.scanner = scanner;    }    
        public void startGame() {        
            selectUser();        
            mainMenu();    }    
            private void selectUser() {        
                while (true) {            
                    System.out.println("\n====== SELECT USER ======");            
                    File dataDir = new File("data");            
                    File[] saveFiles = dataDir.listFiles((dir, name) -> name.endsWith(".data"));            
                    List<String> playerNames = new ArrayList<>();            
                    if (saveFiles != null) {                
                        for (File file : saveFiles) {                    
                            String fileName = file.getName();                    
                            playerNames.add(fileName.substring(0, fileName.lastIndexOf(".")));                
                        }            
                    }            
                    if (playerNames.isEmpty()) {                
                        System.out.println("No existing save files found. Let's create a new game!");                
                        initializeGame();                
                        return;            
                    }            
                    System.out.println("Existing Players:");           
                    for (int i = 0; i < playerNames.size(); i++) {                
                        System.out.println((i + 1) + ". " + playerNames.get(i));            
                    }            
                    System.out.println((playerNames.size() + 1) + ". Create New Player");            
                    System.out.print("Choose an option: ");            
                    int choice = PokemonGame.getIntInput(1, playerNames.size() + 1, scanner);            
                    if (choice <= playerNames.size()) {                
                        String selectedPlayerName = playerNames.get(choice - 1);                
                        if (loadGame(selectedPlayerName + ".data")) { // Check if loading was successful
                            return;            
                        } else {
                            System.out.println("Failed to load game. Please try again or create a new player.");
                            // Loop will continue, prompting user again
                        }
                    } else {                
                        initializeGame();                
                        return;            
                    }        
                }    
            }    
            private void initializeGame() {        
                System.out.print("Enter your trainer name: ");        
                String playerName = scanner.nextLine();        
                if (playerName.trim().isEmpty()) {            
                    playerName = "Trainer";        
                }        
                coins = 100;        
                currentLocation = ""; // Initialize currentLocation
                player = new Player(playerName);        
                System.out.println("Welcome, " + playerName + "! You have " + coins + " coins.");        
                saveGame(playerName + ".data");    }    
                private void mainMenu() {        
                    while (true) {            
                        System.out.println("\n====== MAIN MENU ======");            
                        System.out.println("1. Battle and Catch");            
                        System.out.println("2. View party");            
                        System.out.println("3. Manage Pokemon");            
                        System.out.println("4. View Top Scores");           
                        System.out.println("5. Save Game");            
                        System.out.println("6. Disc Machine");            
                        System.out.println("7. Select User");            
                        System.out.println("8. Exit");            
                        System.out.print("Choose an option: ");            
                        int choice = PokemonGame.getIntInput(1, 8, scanner);            
                        switch (choice) {                
                            case 1:                    
                                int updatedCoins = PokemonGame.battleAndCatchMode(player, scanner, coins, this);                    
                                this.coins = updatedCoins;                
                                break;                
                            case 2: 
                                PokemonGame.viewParty(player);
                                break;                
                            case 3: 
                                PokemonGame.managePokemon(player, scanner);
                                break;                
                            case 4: 
                                displayTopScores();
                                break;                
                            case 5: 
                                saveGame(player.getName() + ".data");
                                break;                
                            case 6: 
                                discMachine();
                                break;                
                            case 7: 
                                selectUser();
                                break;                
                            case 8:                    
                                System.out.println("Thanks for playing!");                    
                                return;                
                        }        
                    }    
                }    
                private void saveGame(String filename) {        
                    System.out.println("\n--- Saving Game Data ---");        
                    System.out.println("Inserting Trainer Card into the slot...");        
                    try {            
                        File dataDir = new File("data");
            
                        if (!dataDir.exists()) {                
                            dataDir.mkdirs();            
                        }            
                        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data" + File.separator + filename))) {                
                            GameData data = new GameData(player, coins, currentLocation);                
                            oos.writeObject(data);                
                            System.out.println("Trainer Card data saved successfully!");            
                        }        
                    } catch (IOException e) {            
                        System.out.println("Error saving game: " + e.getMessage());            
                        e.printStackTrace();        
                    }    
                }    
                private boolean loadGame(String filename) {        
                    System.out.println("\n--- Loading Game Data ---");
                    System.out.println("Reading data from Trainer Card...");        
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data" + File.separator + filename))) {            
                        GameData data = (GameData) ois.readObject();            
                        this.player = data.player;            
                        this.coins = data.coins;            
                        this.currentLocation = data.currentLocation;            
                        System.out.println("Trainer Card data loaded successfully!");           
                        System.out.println("Welcome back, " + player.getName() + "!");            
                        System.out.println("Location: " + currentLocation);            
                        System.out.println("Coins: " + coins);        
                        return true; // Success
                    }        
                    catch (IOException | ClassNotFoundException e) {            
                        System.out.println("Error loading game: " + e.getMessage());            
                        e.printStackTrace();        
                        this.player = null; // Ensure player is null on failure
                        return false; // Failure
                    }    
                }    
                private void displayTopScores() {        
                    try {            
                        if (!new File(SCORES_FILE).exists()) {                
                            System.out.println("No scores recorded yet!");                
                            return;            
                        }
                        System.out.println("\n===== TOP 5 SCORES =====");            
                        System.out.println("------------------------");            
                        try (BufferedReader reader = new BufferedReader(new FileReader(SCORES_FILE))) {                
                            String line;                
                            int rank = 1;                
                            while ((line = reader.readLine()) != null && rank <= 5) {                    
                                String[] parts = line.split(":");                    
                                String playerName;                    
                                String scoreValue;                    
                                if (parts.length == 2) {                        
                                    playerName = parts[0];                        
                                    scoreValue = parts[1];                    
                                } else {                        
                                    playerName = "Unknown Trainer";                        
                                    scoreValue = parts[0];                    
                                }                    
                                System.out.printf("%d. %-15s %s%n", rank, playerName, scoreValue);                    
                                rank++;                
                            }            
                        }            
                        System.out.println("------------------------");        
                    }        catch (IOException e) {            
                        System.out.println("Error reading scores: " + e.getMessage());        
                    }    
                }    
                public void saveScore(String playerName, int score) {
                    try {
                        // Read existing scores
                        List<String> scores = new ArrayList<>();
                        if (new File(SCORES_FILE).exists()) {
                            try (BufferedReader reader = new BufferedReader(new FileReader(SCORES_FILE))) {
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    scores.add(line);
                                }
                            }
                        }
                        scores.add(playerName + ":" + score);
                        
                        // Sort scores (descending) based on the numerical score part
                        scores.sort((s1, s2) -> {
                            int score1 = Integer.parseInt(s1.split(":")[1]);
                            int score2 = Integer.parseInt(s2.split(":")[1]);
                            return Integer.compare(score2, score1);
                        });
            
                        if (scores.size() > 5) {
                            scores = scores.subList(0, 5);
                        }
                        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SCORES_FILE))) {
                            for (String s : scores) {
                                writer.write(s);
                                writer.newLine();
                            }
                        }
                        System.out.println("Score saved!");
                    } 
                    catch (IOException e) {
                        System.out.println("Error saving score: " + e.getMessage());
                    }
                }
                private void discMachine() {       
                    int cost = 10;        
                    if (coins < cost) {            
                        System.out.println("You don't have enough coins! You need " + cost + " coins.");            
                        return;        
                    }        
                    coins -= cost;        
                    System.out.println("\n--- Disc Machine ---");        
                    System.out.println("You inserted " + cost + " coins. Remaining coins: " + coins);        
                    System.out.println("Spinning the disc machine...");        
                    List<String> pokemonNames = new ArrayList<>(PokemonGame.POKEMON_DB.keySet());        
                    String randomPokemonName = pokemonNames.get(PokemonGame.getRandom().nextInt(pokemonNames.size()));        
                    PokemonData data = PokemonGame.POKEMON_DB.get(randomPokemonName);        
                    Pokemon newPokemon = PokemonGame.createPokemon(randomPokemonName, data);        
                    player.addPokemon(newPokemon, scanner);        System.out.println("\n!!! A new disc appeared !!!");        
                    System.out.println("You got: " + newPokemon.getName() + " (Lvl " + newPokemon.getLevel() + ")");        
                    System.out.println("It has been added to your party!");    
                }
            }