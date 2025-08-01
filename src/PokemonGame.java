import java.io.*;
import java.util.*;

public class PokemonGame {

    public static final Map<String, PokemonData> POKEMON_DB = new HashMap<>();
    public static final Map<String, Integer> POKEMON_RARITY = new HashMap<>();
    private static final Random random = new Random();

    static {
        initializePokemonDatabase();
    }

    private static void initializePokemonDatabase() {
        // Electric
        POKEMON_DB.put("Pikachu", new PokemonData(PokemonType.ELECTRIC, 10, 0.3, 35, 55, 40, 90));
        POKEMON_RARITY.put("Pikachu", 3);

        // Fire
        POKEMON_DB.put("Charmander", new PokemonData(PokemonType.FIRE, 12, 0.2, 39, 52, 43, 65));
        POKEMON_RARITY.put("Charmander", 2);
        POKEMON_DB.put("Ponyta", new PokemonData(PokemonType.FIRE, 12, 0.25, 50, 85, 55, 90));
        POKEMON_RARITY.put("Ponyta", 3);
        POKEMON_DB.put("Growlithe", new PokemonData(PokemonType.FIRE, 11, 0.3, 55, 70, 45, 60));
        POKEMON_RARITY.put("Growlithe", 2);

        // Water
        POKEMON_DB.put("Squirtle", new PokemonData(PokemonType.WATER, 11, 0.2, 44, 48, 65, 43));
        POKEMON_RARITY.put("Squirtle", 2);
        POKEMON_DB.put("Psyduck", new PokemonData(PokemonType.WATER, 11, 0.3, 50, 52, 48, 55));
        POKEMON_RARITY.put("Psyduck", 3);
        POKEMON_DB.put("Poliwag", new PokemonData(PokemonType.WATER, 10, 0.4, 40, 50, 40, 90));
        POKEMON_RARITY.put("Poliwag", 2);
        POKEMON_DB.put("Magikarp", new PokemonData(PokemonType.WATER, 5, 0.8, 20, 10, 55, 80));
        POKEMON_RARITY.put("Magikarp", 1);

        // Grass
        POKEMON_DB.put("Bulbasaur", new PokemonData(PokemonType.GRASS, 10, 0.2, 45, 49, 49, 45));
        POKEMON_RARITY.put("Bulbasaur", 2);
        POKEMON_DB.put("Oddish", new PokemonData(PokemonType.GRASS, 9, 0.35, 45, 50, 55, 30));
        POKEMON_RARITY.put("Oddish", 2);
        POKEMON_DB.put("Bellsprout", new PokemonData(PokemonType.GRASS, 10, 0.3, 50, 75, 35, 40));
        POKEMON_RARITY.put("Bellsprout", 2);

        // Normal
        POKEMON_DB.put("Eevee", new PokemonData(PokemonType.NORMAL, 9, 0.3, 55, 55, 50, 55));
        POKEMON_RARITY.put("Eevee", 3);
        POKEMON_DB.put("Jigglypuff", new PokemonData(PokemonType.FAIRY, 8, 0.4, 115, 45, 20, 20));
        POKEMON_RARITY.put("Jigglypuff", 1);
        POKEMON_DB.put("Meowth", new PokemonData(PokemonType.NORMAL, 10, 0.4, 40, 45, 35, 90));
        POKEMON_RARITY.put("Meowth", 1);
        POKEMON_DB.put("Snorlax", new PokemonData(PokemonType.NORMAL, 20, 0.1, 160, 110, 65, 30));
        POKEMON_RARITY.put("Snorlax", 3);

        // Fighting
        POKEMON_DB.put("Machop", new PokemonData(PokemonType.FIGHTING, 13, 0.25, 70, 80, 50, 35));
        POKEMON_RARITY.put("Machop", 4);

        // Ghost
        POKEMON_DB.put("Gastly", new PokemonData(PokemonType.GHOST, 15, 0.2, 30, 35, 30, 80));
        POKEMON_RARITY.put("Gastly", 3);
        POKEMON_DB.put("Haunter", new PokemonData(PokemonType.GHOST, 20, 0.15, 45, 50, 45, 95));
        POKEMON_RARITY.put("Haunter", 3);
        POKEMON_DB.put("Gengar", new PokemonData(PokemonType.GHOST, 25, 0.1, 60, 65, 60, 110));
        POKEMON_RARITY.put("Gengar", 4);
        POKEMON_DB.put("Misdreavus", new PokemonData(PokemonType.GHOST, 18, 0.2, 60, 60, 60, 85));
        POKEMON_RARITY.put("Misdreavus", 3);

        // Dragon/Psychic (rare)
        POKEMON_DB.put("Dragonite", new PokemonData(PokemonType.DRAGON, 30, 0.05, 91, 134, 95, 80));
        POKEMON_RARITY.put("Dragonite", 5);
        POKEMON_DB.put("Mewtwo", new PokemonData(PokemonType.PSYCHIC, 40, 0.01, 106, 110, 90, 130));
        POKEMON_RARITY.put("Mewtwo", 5);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        GameManager gameManager = new GameManager(scanner);
        gameManager.startGame();
        scanner.close();
    }

    public static Random getRandom() {
        return random;
    }

    // Removed methods: selectUser, initializeGame, mainMenu, saveGame, loadGame, displayTopScores, discMachine
    // These are now in GameManager.java

    public static int getIntInput(int min, int max, Scanner scanner) {
        if (System.console() == null) {
            return 1; // Default to 1 in non-interactive mode
        }
        int choice;
        while (true) {
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); 
                if (choice >= min && choice <= max) break;
                System.out.print("Invalid input. Enter between " + min + "-" + max + ": ");
            }
            catch (InputMismatchException e) {
                System.out.print("Invalid input. Enter a number: ");
                scanner.next();
            }
        }
        return choice;
    }

    public static List<Pokemon> generateRandomEncounters(int count, String boostedPokemon) {
        List<Pokemon> encounters = new ArrayList<>();
        List<String> pokemonNames = new ArrayList<>(POKEMON_DB.keySet());

        while (encounters.size() < count) {
            // Boosted chance for the location's featured Pokemon
            if (random.nextInt(100) < 30 && !containsPokemonByName(encounters, boostedPokemon)) {
                PokemonData data = POKEMON_DB.get(boostedPokemon);
                if (data == null) {
                    System.err.println("Warning: Boosted Pokemon '" + boostedPokemon + "' not found in POKEMON_DB!");
                    continue;
                }
                Pokemon pokemon = createPokemon(boostedPokemon, data);
                if (pokemon != null) encounters.add(pokemon);
                continue;
            }

            // Regular random selection with rarity consideration
            int rarity = getRandomRarity(random.nextInt(100) + 1);
            List<String> candidates = new ArrayList<>();
            for (String name : pokemonNames) {
                if (POKEMON_RARITY.get(name) == rarity && !containsPokemonByName(encounters, name)) {
                    candidates.add(name);
                }
            }

            if (!candidates.isEmpty()) {
                String name = candidates.get(random.nextInt(candidates.size()));
                PokemonData data = POKEMON_DB.get(name);
                if (data == null) {
                    System.err.println("Warning: Pokemon '" + name + "' not found in POKEMON_DB!");
                    continue;
                }
                Pokemon pokemon = createPokemon(name, data);
                if (pokemon != null) encounters.add(pokemon);
            }
        }
        return encounters;
    }

    public static Pokemon createPokemon(String name, PokemonData data) {
        if (data == null) {
            System.err.println("Error: Tried to create Pokemon '" + name + "' but data is null!");
            return null;
        }
        Pokemon newPokemon = null;
        switch (data.type) {
            case FIRE:
                newPokemon = new FirePokemon(name, data.level, data.catchRate, data.baseHp, data.baseAttack, data.baseDefense, data.baseSpeed);
                break;
            case WATER:
                newPokemon = new WaterPokemon(name, data.level, data.catchRate, data.baseHp, data.baseAttack, data.baseDefense, data.baseSpeed);
                break;
            case GRASS:
                newPokemon = new GrassPokemon(name, data.level, data.catchRate, data.baseHp, data.baseAttack, data.baseDefense, data.baseSpeed);
                break;
            case ELECTRIC:
                newPokemon = new ElectricPokemon(name, data.level, data.catchRate, data.baseHp, data.baseAttack, data.baseDefense, data.baseSpeed);
                break;
            case GHOST:
                newPokemon = new GhostPokemon(name, data.level, data.catchRate, data.baseHp, data.baseAttack, data.baseDefense, data.baseSpeed);
                break;
            case FIGHTING:
                newPokemon = new FightingPokemon(name, data.level, data.catchRate, data.baseHp, data.baseAttack, data.baseDefense, data.baseSpeed);
                break;
            case PSYCHIC:
                newPokemon = new PsychicPokemon(name, data.level, data.catchRate, data.baseHp, data.baseAttack, data.baseDefense, data.baseSpeed);
                break;
            case DRAGON:
                newPokemon = new DragonPokemon(name, data.level, data.catchRate, data.baseHp, data.baseAttack, data.baseDefense, data.baseSpeed);
                break;
            case FAIRY:
                newPokemon = new FairyPokemon(name, data.level, data.catchRate, data.baseHp, data.baseAttack, data.baseDefense, data.baseSpeed);
                break;
            default:
                newPokemon = new NormalPokemon(name, data.level, data.catchRate, data.baseHp, data.baseAttack, data.baseDefense, data.baseSpeed);
                break;
        }
        if (newPokemon != null) {
            assignMoves(newPokemon);
        }
        return newPokemon;
    }

    public static void assignMoves(Pokemon pokemon) {
        switch (pokemon.getType()) {
            case FIRE:
                pokemon.addMove(new Move("Ember", 40, PokemonType.FIRE));
                pokemon.addMove(new Move("Fire Spin", 35, PokemonType.FIRE));
                break;
            case WATER:
                pokemon.addMove(new Move("Water Gun", 40, PokemonType.WATER));
                pokemon.addMove(new Move("Bubble Beam", 65, PokemonType.WATER));
                break;
            case GRASS:
                pokemon.addMove(new Move("Vine Whip", 45, PokemonType.GRASS));
                pokemon.addMove(new Move("Razor Leaf", 55, PokemonType.GRASS));
                break;
            case ELECTRIC:
                pokemon.addMove(new Move("Thunder Shock", 40, PokemonType.ELECTRIC));
                pokemon.addMove(new Move("Thunderbolt", 90, PokemonType.ELECTRIC));
                break;
            case GHOST:
                pokemon.addMove(new Move("Lick", 30, PokemonType.GHOST));
                pokemon.addMove(new Move("Shadow Ball", 80, PokemonType.GHOST));
                break;
            case FIGHTING:
                pokemon.addMove(new Move("Low Kick", 60, PokemonType.FIGHTING));
                pokemon.addMove(new Move("Karate Chop", 50, PokemonType.FIGHTING));
                break;
            case PSYCHIC:
                pokemon.addMove(new Move("Confusion", 50, PokemonType.PSYCHIC));
                pokemon.addMove(new Move("Psychic", 90, PokemonType.PSYCHIC));
                break;
            case DRAGON:
                pokemon.addMove(new Move("Dragon Breath", 60, PokemonType.DRAGON));
                pokemon.addMove(new Move("Dragon Claw", 80, PokemonType.DRAGON));
                break;
            case FAIRY:
                pokemon.addMove(new Move("Fairy Wind", 40, PokemonType.FAIRY));
                pokemon.addMove(new Move("Dazzling Gleam", 80, PokemonType.FAIRY));
                break;
            case NORMAL:
                pokemon.addMove(new Move("Tackle", 40, PokemonType.NORMAL));
                pokemon.addMove(new Move("Quick Attack", 40, PokemonType.NORMAL));
                break;
            default:
                pokemon.addMove(new Move("Struggle", 50, PokemonType.NORMAL)); // Fallback move
                break;
        }
    }

    public static boolean containsPokemonByName(List<Pokemon> list, String name) {
        for (Pokemon p : list) {
            if (p.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static int getRandomRarity(int roll) {
        if (roll <= 5) return 5;      
        else if (roll <= 20) return 4; 
        else if (roll <= 40) return 3; 
        else if (roll <= 70) return 2; 
        else return 1;                 
    }

    public static int battleAndCatchMode(Player player, Scanner scanner, int coins, GameManager gameManager) {
        // Deduct 1 coin to play
        if (coins < 1) {
            System.out.println("You need at least 1 coin to play!");
            return coins;
        }

        coins--;
        System.out.println("\n1 coin has been used to enter the stage.");
        System.out.println("Remaining coins: " + coins);

        // Choose location with boosted Pokemon
        System.out.println("\n===== BATTLE STAGE SELECTION =====");
        System.out.println("1. Verdant Glade (Boosted: Charmander)");
        System.out.println("2. Whispering Woods (Boosted: Bulbasaur)");
        System.out.println("3. Crystal Lakefront (Boosted: Squirtle)");
        System.out.println("4. Haunted Mansion (Boosted: Gastly)");
        System.out.print("Choose a battle stage: ");

        int choice = getIntInput(1, 4, scanner);
        String[] locations = {"Verdant Glade", "Whispering Woods", "Crystal Lakefront", "Haunted Mansion"};
        String[] boostedPokemon = {"Charmander", "Bulbasaur", "Squirtle", "Gastly"};
        String currentLocation = locations[choice - 1]; // Make local
        String boostedName = boostedPokemon[choice - 1];

        System.out.println("\nYou've arrived at: " + currentLocation);
        System.out.println("This location has a higher chance to encounter " + boostedName + "!");

        // Catch Time - select from 3 random Pokemon
        System.out.println("\n=== CATCH TIME ===");
        List<Pokemon> encounterPool = generateRandomEncounters(3, boostedName);
        System.out.println("Choose a Pokemon to catch:");

        for (int i = 0; i < encounterPool.size(); i++) {
            System.out.println("\n[" + (i + 1) + "]");
            encounterPool.get(i).displayInfo();
            System.out.print("Rarity: ");
            for (int j = 0; j < POKEMON_RARITY.get(encounterPool.get(i).getName()); j++) {
                System.out.print("*");
            }
            System.out.println();
        }

        System.out.print("\nEnter your choice (1-3): ");
        int captureChoice = getIntInput(1, 3, scanner);
        Pokemon caught = encounterPool.get(captureChoice - 1);
        addPokemonToParty(player, caught.getName(), scanner);

        // Battle sequence
        return startBattleSequence(player, scanner, coins, gameManager);
    }

    public static int startBattleSequence(Player player, Scanner scanner, int coins, GameManager gameManager) {
        if (player.getParty().isEmpty()) {
            System.out.println("You need at least 1 Pokemon in your party to battle!");
            return coins;
        }

        System.out.println("\n" + player.getName() + ", prepare for battle!");
        System.out.println("Choose your Pokemon:");
        List<Pokemon> party = player.getParty();

        for (int i = 0; i < party.size(); i++) {
            Pokemon p = party.get(i);
            System.out.println((i + 1) + ". " + p.getName() + 
                             " (Lvl " + p.getLevel() + " HP: " + 
                             p.getCurrentHp() + "/" + p.getMaxHp() + ")");
        }

        System.out.print("Enter your choice: ");
        int choice = getIntInput(1, party.size(), scanner);
        Pokemon selected = party.get(choice - 1);

        // Check if Pokemon is defeated BEFORE proceeding
        if (selected.isDefeated()) {
            System.out.println(selected.getName() + " is too weak to battle! Please heal it first.");
            return coins;
        }

        System.out.println("\n" + selected.getName() + " is ready for battle!");

        // Generate opponent (with safety check)
        List<Pokemon> wildPokemon = generateRandomEncounters(1, "");
        if (wildPokemon.isEmpty()) {
            System.out.println("No wild Pokemon appeared! Try again.");
            return coins;
        }

        Pokemon opponent = wildPokemon.get(0);
        System.out.println("\nA wild " + opponent.getName() + " appeared!");

        // Start battle
        BattleSystem battle = new BattleSystem(
            Collections.singletonList(selected),
            Collections.singletonList(opponent),
            scanner,
            player
        );
        battle.startBattle();

        if (battle.hasFled()) {
            System.out.println("Returning to the main menu...");
            return coins;
        }

        // Post-battle logic
        if (battle.isPlayerVictorious()) {
            int battleScore = battle.getBattleScore();
            gameManager.saveScore(player.getName(), battleScore);
            System.out.println("Battle Score: " + battleScore);
            int coinsEarned = battleScore / 10;
            coins += coinsEarned;
            System.out.println("Earned " + coinsEarned + " coins! Total coins: " + coins);
            if (!opponent.isDefeated()) {
                attemptCatch(player, wildPokemon.get(0), scanner);
            }
        } else {
            System.out.println("You lost the battle! No coins earned.");
        }
        return coins;
    }

    public static void attemptCatch(Player player, Pokemon wildPokemon, Scanner scanner) {
        System.out.println("\nAttempt to catch " + wildPokemon.getName() + "?");
        System.out.println("1. Yes");
        System.out.println("2. No");
        System.out.print("Choose: ");

        int choice = getIntInput(1, 2, scanner);
        if (choice == 2) return;

        PokeBall[] balls = {
            new PokeBall("Poke Ball", 1.0),
            new PokeBall("Great Ball", 1.5),
            new PokeBall("Ultra Ball", 2.0),
            new PokeBall("Master Ball", 2.5)
        };

        System.out.println("\nChoose a Poke Ball:");
        for (int i = 0; i < balls.length; i++) {
            System.out.println((i + 1) + ". " + balls[i].getName() + 
                             " (Catch Rate Bonus: " + balls[i].getCatchModifier() + "x)");
        }

        int ballChoice = getIntInput(1, balls.length, scanner);
        PokeBall chosenBall = balls[ballChoice - 1];

        if (CatchSystem.tryCatch(wildPokemon, chosenBall, scanner)) {
            player.addPokemon(wildPokemon, scanner);
            System.out.println("Gotcha! " + wildPokemon.getName() + " was caught!");
        } 
        else {
            System.out.println("Oh no! " + wildPokemon.getName() + " broke free!");
        }
    }

    public static void addPokemonToParty(Player player, String name, Scanner scanner) {
        if (!POKEMON_DB.containsKey(name)) {
            System.out.println("Error: Unknown Pokemon " + name);
            return;
        }

        PokemonData data = POKEMON_DB.get(name);
        Pokemon newPokemon = createPokemon(name, data);
        player.addPokemon(newPokemon, scanner);
        System.out.println("\nYou captured: " + name + "!");
    }

    public static void viewParty(Player player) {
        System.out.println("\n===== YOUR POKEMON PARTY =====");
        List<Pokemon> party = player.getParty();

        if (party.isEmpty()) {
            System.out.println("You have no Pokemon in your party!");
            return;
        }

        for (int i = 0; i < party.size(); i++) {
            System.out.println("\nPokemon #" + (i + 1));
            party.get(i).displayDetailedInfo();
            System.out.println("Type Effectiveness:");
            System.out.println("Strong against: " + getStrongAgainst(party.get(i).getType()));
            System.out.println("Weak against: " + getWeakAgainst(party.get(i).getType()));
        }
    }

    public static String getStrongAgainst(PokemonType type) {
        switch (type) {
            case FIRE: 
            	return "Grass, Bug, Ice, Steel";
            case WATER: 
            	return "Fire, Rock, Ground";
            case GRASS: 
            	return "Water, Rock, Ground";
            case ELECTRIC: 
            	return "Water, Flying";
            case GHOST: 
            	return "Ghost, Psychic";
            case FAIRY: 
            	return "Fighting, Dragon, Dark";
            case FIGHTING: 
            	return "Normal, Rock, Ice, Dark";
            case PSYCHIC: 
            	return "Fighting, Poison";
            case DRAGON: 
            	return "Dragon";
            default: 
            	return "None";
        }
    }

    public static String getWeakAgainst(PokemonType type) {
        switch (type) {
            case FIRE: 
            	return "Water, Rock, Ground";
            case WATER: 
            	return "Grass, Electric";
            case GRASS: 
            	return "Fire, Flying, Poison, Bug, Ice";
            case ELECTRIC: 
            	return "Ground";
            case GHOST: 
            	return "Ghost, Dark";
            case FAIRY: 
            	return "Poison, Steel";
            case FIGHTING: 
            	return "Flying, Psychic, Fairy";
            case PSYCHIC: 
            	return "Bug, Ghost, Dark";
            case DRAGON: 
            	return "Ice, Dragon, Fairy";
            default: 
            	return "None";
        }
    }

    public static void managePokemon(Player player, Scanner scanner) {
        if (player.getParty().isEmpty()) {
            System.out.println("You have no Pokemon to manage!");
            return;
        }

        System.out.println("\n===== MANAGE POKEMON =====");
        List<Pokemon> party = player.getParty();

        for (int i = 0; i < party.size(); i++) {
            Pokemon p = party.get(i);
            System.out.printf("%d. %s (Lvl %d) HP: %d/%d\n",i+1, p.getName(), p.getLevel(), p.getCurrentHp(), p.getMaxHp());
        }

        System.out.print("Select Pokemon to manage (0 to cancel): ");
        int choice = getIntInput(0, party.size(), scanner); // Use passed scanner
        if (choice == 0) return;

        Pokemon selected = party.get(choice - 1);
        System.out.println("\nSelected: " + selected.getName());
        System.out.println("1. Use Potion (restore 20 HP)");
        System.out.println("2. Send to PC");
        System.out.print("Choose action: ");

        int action = getIntInput(1, 2, scanner); // Use passed scanner
        if (action == 1) {
            selected.restoreHealth(20);
            System.out.println(selected.getName() + " restored 20 HP!");
        } 
        else {
            player.removePokemon(selected);
            System.out.println(selected.getName() + " sent to PC!");
        }
    }

    }