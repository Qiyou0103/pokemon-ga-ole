import java.util.*;

// Enums for Pokemon and Move types - Extended with more types
enum PokemonType {
    FIRE, WATER, GRASS, ELECTRIC, NORMAL, FIGHTING, POISON, GROUND, FLYING, PSYCHIC, BUG, ROCK
}

enum MoveType {
    FIRE, WATER, GRASS, ELECTRIC, NORMAL, FIGHTING, POISON, GROUND, FLYING, PSYCHIC, BUG, ROCK
}

// Status effects enum
enum StatusEffect {
    NONE, POISON, PARALYSIS, SLEEP, BURN, FREEZE
}

// Item types
enum ItemType {
    POTION, SUPER_POTION, HYPER_POTION, X_ATTACK, X_DEFENSE, AWAKENING, ANTIDOTE, PARALYZE_HEAL
}

// Abstract base Item class - Polymorphism foundation
abstract class Item {
    protected String name;
    protected ItemType type;
    protected String description;
    
    public Item(String name, ItemType type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
    }
    
    // Abstract method - Each item type must implement its own effect
    public abstract boolean useItem(Pokemon target, BattleContext context);
    
    // Template method - Common behavior for all items
    public final void displayItemInfo() {
        System.out.println("=== " + name + " ===");
        System.out.println(description);
        System.out.println("Type: " + type);
    }
    
    // Getters
    public String getName() { return name; }
    public ItemType getType() { return type; }
    public String getDescription() { return description; }
}

// Concrete Item implementations - Demonstrating Polymorphism
class HealingItem extends Item {
    private int healAmount;
    
    public HealingItem(String name, ItemType type, String description, int healAmount) {
        super(name, type, description);
        this.healAmount = healAmount;
    }
    
    @Override
    public boolean useItem(Pokemon target, BattleContext context) {
        int oldHp = target.getCurrentHp();
        target.heal(healAmount);
        int actualHealing = target.getCurrentHp() - oldHp;
        
        if (actualHealing > 0) {
            System.out.println(target.getName() + " restored " + actualHealing + " HP!");
            return true;
        } else {
            System.out.println(target.getName() + " is already at full health!");
            return false;
        }
    }
}

class StatBoostItem extends Item {
    private String statType;
    private int boostAmount;
    
    public StatBoostItem(String name, ItemType type, String description, String statType, int boostAmount) {
        super(name, type, description);
        this.statType = statType;
        this.boostAmount = boostAmount;
    }
    
    @Override
    public boolean useItem(Pokemon target, BattleContext context) {
        switch (statType.toLowerCase()) {
            case "attack":
                target.boostAttack(boostAmount);
                break;
            case "defense":
                target.boostDefense(boostAmount);
                break;
            default:
                System.out.println("Unknown stat type: " + statType);
                return false;
        }
        return true;
    }
}

class StatusCureItem extends Item {
    private StatusEffect targetStatus;
    
    public StatusCureItem(String name, ItemType type, String description, StatusEffect targetStatus) {
        super(name, type, description);
        this.targetStatus = targetStatus;
    }
    
    @Override
    public boolean useItem(Pokemon target, BattleContext context) {
        if (target.getStatusEffect() == targetStatus) {
            target.cureStatus();
            return true;
        } else {
            System.out.println("It had no effect on " + target.getName() + "!");
            return false;
        }
    }
}

// Context class for battle information
class BattleContext {
    private Pokemon playerPokemon;
    private Pokemon opponentPokemon;
    private int turnCount;
    private String battleType;
    
    public BattleContext(Pokemon playerPokemon, Pokemon opponentPokemon, String battleType) {
        this.playerPokemon = playerPokemon;
        this.opponentPokemon = opponentPokemon;
        this.battleType = battleType;
        this.turnCount = 0;
    }
    
    public void incrementTurn() { turnCount++; }
    
    // Getters
    public Pokemon getPlayerPokemon() { return playerPokemon; }
    public Pokemon getOpponentPokemon() { return opponentPokemon; }
    public int getTurnCount() { return turnCount; }
    public String getBattleType() { return battleType; }
}

// Move class representing Pokemon attacks - Enhanced with status effects
class Move {
    private String name;
    private MoveType type;
    private int power;
    private int accuracy;
    private StatusEffect statusEffect;
    private int statusChance;
    
    public Move(String name, MoveType type, int power, int accuracy) {
        this(name, type, power, accuracy, StatusEffect.NONE, 0);
    }
    
    public Move(String name, MoveType type, int power, int accuracy, StatusEffect statusEffect, int statusChance) {
        this.name = name;
        this.type = type;
        this.power = power;
        this.accuracy = accuracy;
        this.statusEffect = statusEffect;
        this.statusChance = statusChance;
    }
    
    // Getters
    public String getName() { return name; }
    public MoveType getType() { return type; }
    public int getPower() { return power; }
    public int getAccuracy() { return accuracy; }
    public StatusEffect getStatusEffect() { return statusEffect; }
    public int getStatusChance() { return statusChance; }
}

// Abstract base Pokemon class - Enhanced with status effects
abstract class Pokemon {
    protected String name;
    protected PokemonType type;
    protected int maxHp;
    protected int currentHp;
    protected int baseAttack;
    protected int baseDefense;
    protected int currentAttack;
    protected int currentDefense;
    protected int level;
    protected Move[] moves;
    protected StatusEffect statusEffect;
    protected int statusTurns;
    
    public Pokemon(String name, PokemonType type, int maxHp, int attack, int defense, int level) {
        this.name = name;
        this.type = type;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.baseAttack = attack;
        this.baseDefense = defense;
        this.currentAttack = attack;
        this.currentDefense = defense;
        this.level = level;
        this.moves = new Move[4];
        this.statusEffect = StatusEffect.NONE;
        this.statusTurns = 0;
    }
    
    // Abstract method for special abilities
    public abstract void useSpecialAbility();
    
    // Status effect methods
    public void applyStatusEffect(StatusEffect effect) {
        this.statusEffect = effect;
        this.statusTurns = 3; // Most status effects last 3 turns
        System.out.println(name + " is now " + effect.toString().toLowerCase() + "!");
    }
    
    public void processStatusEffect() {
        if (statusEffect == StatusEffect.NONE) return;
        
        switch (statusEffect) {
            case POISON:
                int poisonDamage = maxHp / 8;
                takeDamage(poisonDamage);
                System.out.println(name + " is hurt by poison! (-" + poisonDamage + " HP)");
                break;
            case BURN:
                int burnDamage = maxHp / 16;
                takeDamage(burnDamage);
                currentAttack = (int)(baseAttack * 0.5); // Burn reduces attack
                System.out.println(name + " is hurt by burn! (-" + burnDamage + " HP)");
                break;
            case SLEEP:
                System.out.println(name + " is fast asleep...");
                break;
            case PARALYSIS:
                System.out.println(name + " is paralyzed!");
                break;
            case FREEZE:
                System.out.println(name + " is frozen solid!");
                break;
        }
        
        statusTurns--;
        if (statusTurns <= 0) {
            cureStatus();
        }
    }
    
    public void cureStatus() {
        if (statusEffect != StatusEffect.NONE) {
            System.out.println(name + " recovered from " + statusEffect.toString().toLowerCase() + "!");
            statusEffect = StatusEffect.NONE;
            statusTurns = 0;
            currentAttack = baseAttack;
            currentDefense = baseDefense;
        }
    }
    
    public boolean canAct() {
        return statusEffect != StatusEffect.SLEEP && statusEffect != StatusEffect.FREEZE &&
               !(statusEffect == StatusEffect.PARALYSIS && new Random().nextInt(100) < 25);
    }
    
    // Stat modification methods
    public void boostAttack(int stages) {
        double multiplier = Math.pow(1.5, stages);
        currentAttack = (int)(baseAttack * multiplier);
        System.out.println(name + "'s attack " + (stages > 0 ? "rose!" : "fell!"));
    }
    
    public void boostDefense(int stages) {
        double multiplier = Math.pow(1.5, stages);
        currentDefense = (int)(baseDefense * multiplier);
        System.out.println(name + "'s defense " + (stages > 0 ? "rose!" : "fell!"));
    }
    
    // Common methods
    public void takeDamage(int damage) {
        currentHp = Math.max(0, currentHp - damage);
    }
    
    public void heal(int amount) {
        currentHp = Math.min(maxHp, currentHp + amount);
    }
    
    public boolean isDefeated() {
        return currentHp <= 0;
    }
    
    public void addMove(Move move, int slot) {
        if (slot >= 0 && slot < 4) {
            moves[slot] = move;
        }
    }
    
    public Move getMove(int index) {
        return (index >= 0 && index < 4) ? moves[index] : null;
    }
    
    public Move[] getAvailableMoves() {
        return moves;
    }
    
    // Display Pokemon status
    public void displayStatus() {
        String statusStr = (statusEffect != StatusEffect.NONE) ? " [" + statusEffect + "]" : "";
        System.out.println(name + " (Lv." + level + ") - HP: " + currentHp + "/" + maxHp + 
                          " - Type: " + type + statusStr);
    }
    
    public void displayDetailedStatus() {
        displayStatus();
        System.out.println("  Attack: " + currentAttack + " | Defense: " + currentDefense);
        System.out.println("  Moves:");
        for (int i = 0; i < moves.length; i++) {
            if (moves[i] != null) {
                System.out.println("    " + (i + 1) + ". " + moves[i].getName() + 
                                 " (Power: " + moves[i].getPower() + ", Acc: " + moves[i].getAccuracy() + "%)");
            }
        }
    }
    
    // Getters
    public String getName() { return name; }
    public PokemonType getType() { return type; }
    public int getCurrentHp() { return currentHp; }
    public int getMaxHp() { return maxHp; }
    public int getCurrentAttack() { return currentAttack; }
    public int getCurrentDefense() { return currentDefense; }
    public int getLevel() { return level; }
    public StatusEffect getStatusEffect() { return statusEffect; }
}

// Concrete Pokemon classes
class FirePokemon extends Pokemon {
    public FirePokemon(String name, int maxHp, int attack, int defense, int level) {
        super(name, PokemonType.FIRE, maxHp, attack, defense, level);
    }
    
    @Override
    public void useSpecialAbility() {
        System.out.println(name + " uses Blaze! Attack increased when HP is low!");
        if (currentHp < maxHp / 3) {
            boostAttack(1);
        }
    }
}

class WaterPokemon extends Pokemon {
    public WaterPokemon(String name, int maxHp, int attack, int defense, int level) {
        super(name, PokemonType.WATER, maxHp, attack, defense, level);
    }
    
    @Override
    public void useSpecialAbility() {
        System.out.println(name + " uses Torrent! Water moves powered up when HP is low!");
        if (currentHp < maxHp / 3) {
            boostAttack(1);
        }
    }
}

class GrassPokemon extends Pokemon {
    public GrassPokemon(String name, int maxHp, int attack, int defense, int level) {
        super(name, PokemonType.GRASS, maxHp, attack, defense, level);
    }
    
    @Override
    public void useSpecialAbility() {
        System.out.println(name + " uses Overgrow! Grass moves powered up when HP is low!");
        if (currentHp < maxHp / 3) {
            boostAttack(1);
        }
    }
}

// Enhanced Type effectiveness calculator
class TypeEffectiveness {
    private static final double[][] EFFECTIVENESS_CHART = {
        //        FIR WAT GRA ELE NOR FIG POI GRO FLY PSY BUG ROC
        /* FIR */ {0.5,0.5,2.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,2.0,0.5},
        /* WAT */ {2.0,0.5,0.5,1.0,1.0,1.0,1.0,2.0,1.0,1.0,1.0,2.0},
        /* GRA */ {0.5,2.0,0.5,1.0,1.0,1.0,0.5,2.0,0.5,1.0,0.5,2.0},
        /* ELE */ {1.0,2.0,0.5,0.5,1.0,1.0,1.0,0.0,2.0,1.0,1.0,1.0},
        /* NOR */ {1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,0.5},
        /* FIG */ {1.0,1.0,1.0,1.0,2.0,1.0,0.5,1.0,0.5,0.5,0.5,2.0},
        /* POI */ {1.0,1.0,2.0,1.0,1.0,1.0,0.5,0.5,1.0,1.0,1.0,0.5},
        /* GRO */ {2.0,1.0,0.5,2.0,1.0,1.0,2.0,1.0,0.0,1.0,0.5,2.0},
        /* FLY */ {1.0,1.0,2.0,0.5,1.0,2.0,1.0,1.0,1.0,1.0,2.0,0.5},
        /* PSY */ {1.0,1.0,1.0,1.0,1.0,2.0,2.0,1.0,1.0,0.5,1.0,1.0},
        /* BUG */ {0.5,1.0,2.0,1.0,1.0,0.5,0.5,1.0,0.5,2.0,1.0,1.0},
        /* ROC */ {2.0,1.0,1.0,1.0,1.0,0.5,1.0,0.5,2.0,1.0,2.0,1.0}
    };
    
    public static double getEffectiveness(MoveType attackType, PokemonType defenderType) {
        int attackIndex = attackType.ordinal();
        int defenseIndex = defenderType.ordinal();
        return EFFECTIVENESS_CHART[attackIndex][defenseIndex];
    }
    
    public static String getEffectivenessMessage(double effectiveness) {
        if (effectiveness > 1.0) {
            return "It's super effective!";
        } else if (effectiveness < 1.0 && effectiveness > 0.0) {
            return "It's not very effective...";
        } else if (effectiveness == 0.0) {
            return "It has no effect!";
        } else {
            return "";
        }
    }
}

// Item Factory - Factory Design Pattern for creating items
class ItemFactory {
    public static Item createItem(ItemType type) {
        switch (type) {
            case POTION:
                return new HealingItem("Potion", ItemType.POTION, 
                    "Restores 20 HP to a Pokemon", 20);
            case SUPER_POTION:
                return new HealingItem("Super Potion", ItemType.SUPER_POTION, 
                    "Restores 50 HP to a Pokemon", 50);
            case HYPER_POTION:
                return new HealingItem("Hyper Potion", ItemType.HYPER_POTION, 
                    "Restores 120 HP to a Pokemon", 120);
            case X_ATTACK:
                return new StatBoostItem("X Attack", ItemType.X_ATTACK, 
                    "Raises Attack stat of a Pokemon", "attack", 1);
            case X_DEFENSE:
                return new StatBoostItem("X Defense", ItemType.X_DEFENSE, 
                    "Raises Defense stat of a Pokemon", "defense", 1);
            case ANTIDOTE:
                return new StatusCureItem("Antidote", ItemType.ANTIDOTE, 
                    "Cures a Pokemon of poison", StatusEffect.POISON);
            case PARALYZE_HEAL:
                return new StatusCureItem("Paralyze Heal", ItemType.PARALYZE_HEAL, 
                    "Cures a Pokemon of paralysis", StatusEffect.PARALYSIS);
            case AWAKENING:
                return new StatusCureItem("Awakening", ItemType.AWAKENING, 
                    "Wakes up a sleeping Pokemon", StatusEffect.SLEEP);
            default:
                throw new IllegalArgumentException("Unknown item type: " + type);
        }
    }
}

// Inventory Manager - Encapsulates all inventory-related operations
class InventoryManager {
    private Map<ItemType, Integer> itemCounts;
    private Map<ItemType, Item> itemTemplates;
    
    public InventoryManager() {
        itemCounts = new HashMap<>();
        itemTemplates = new HashMap<>();
        initializeInventory();
    }
    
    private void initializeInventory() {
        // Initialize starting inventory
        addItem(ItemType.POTION, 3);
        addItem(ItemType.SUPER_POTION, 1);
        addItem(ItemType.X_ATTACK, 2);
        addItem(ItemType.X_DEFENSE, 1);
        addItem(ItemType.ANTIDOTE, 1);
        addItem(ItemType.PARALYZE_HEAL, 1);
        addItem(ItemType.AWAKENING, 1);
        
        // Create item templates for display purposes
        for (ItemType type : ItemType.values()) {
            itemTemplates.put(type, ItemFactory.createItem(type));
        }
    }
    
    public void addItem(ItemType type, int quantity) {
        itemCounts.put(type, itemCounts.getOrDefault(type, 0) + quantity);
    }
    
    public boolean hasItem(ItemType type) {
        return itemCounts.getOrDefault(type, 0) > 0;
    }
    
    public boolean useItem(ItemType type, Pokemon target, BattleContext context) {
        if (!hasItem(type)) {
            System.out.println("You don't have any " + type.name() + "!");
            return false;
        }
        
        Item item = ItemFactory.createItem(type);
        boolean itemUsed = item.useItem(target, context);
        
        if (itemUsed) {
            itemCounts.put(type, itemCounts.get(type) - 1);
            System.out.println("Used " + item.getName() + " on " + target.getName() + "!");
        }
        
        return itemUsed;
    }
    
    public void displayInventory() {
        System.out.println("\n=== INVENTORY ===");
        boolean hasItems = false;
        
        for (ItemType type : ItemType.values()) {
            int count = itemCounts.getOrDefault(type, 0);
            if (count > 0) {
                Item template = itemTemplates.get(type);
                System.out.println(count + "x " + template.getName() + " - " + template.getDescription());
                hasItems = true;
            }
        }
        
        if (!hasItems) {
            System.out.println("Your inventory is empty!");
        }
        System.out.println("=================\n");
    }
    
    public List<ItemType> getAvailableItems() {
        List<ItemType> available = new ArrayList<>();
        for (ItemType type : ItemType.values()) {
            if (hasItem(type)) {
                available.add(type);
            }
        }
        return available;
    }
    
    public int getItemCount(ItemType type) {
        return itemCounts.getOrDefault(type, 0);
    }
    
    public Item getItemTemplate(ItemType type) {
        return itemTemplates.get(type);
    }
}

// Enhanced Battle class with user input and all new features
class Battle {
    private Pokemon playerPokemon;
    private Pokemon opponentPokemon;
    private boolean playerTurn;
    private Random random;
    private Scanner scanner;
    private PlayerInventory inventory;
    
    public Battle(Pokemon playerPokemon, Pokemon opponentPokemon, PlayerInventory inventory) {
        this.playerPokemon = playerPokemon;
        this.opponentPokemon = opponentPokemon;
        this.playerTurn = true;
        this.random = new Random();
        this.scanner = new Scanner(System.in);
        this.inventory = inventory;
    }
    
    public void startBattle() {
        System.out.println("=== BATTLE START ===");
        System.out.println("Player's " + playerPokemon.getName() + " VS Wild " + opponentPokemon.getName());
        System.out.println();
        
        // Show initial status
        playerPokemon.displayStatus();
        opponentPokemon.displayStatus();
        System.out.println();
        
        // Battle loop
        while (!playerPokemon.isDefeated() && !opponentPokemon.isDefeated()) {
            if (playerTurn) {
                playerTurn();
            } else {
                opponentTurn();
            }
            
            // Process status effects
            playerPokemon.processStatusEffect();
            opponentPokemon.processStatusEffect();
            
            // Switch turns
            playerTurn = !playerTurn;
            
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
        }
        
        // Battle result
        System.out.println("\n=== BATTLE END ===");
        if (playerPokemon.isDefeated()) {
            System.out.println(playerPokemon.getName() + " has been defeated!");
            System.out.println("Wild " + opponentPokemon.getName() + " wins!");
        } else {
            System.out.println("Wild " + opponentPokemon.getName() + " has been defeated!");
            System.out.println(playerPokemon.getName() + " wins!");
        }
    }
    
    private void playerTurn() {
        System.out.println("--- " + playerPokemon.getName() + "'s Turn ---");
        playerPokemon.displayDetailedStatus();
        
        if (!playerPokemon.canAct()) {
            System.out.println(playerPokemon.getName() + " cannot act due to status effect!");
            return;
        }
        
        System.out.println("\nChoose your action:");
        System.out.println("1. Attack");
        System.out.println("2. Use Item");
        System.out.println("3. Use Special Ability");
        
        int choice = getValidInput(1, 3);
        
        switch (choice) {
            case 1:
                chooseAndExecuteMove();
                break;
            case 2:
                useItem();
                break;
            case 3:
                playerPokemon.useSpecialAbility();
                break;
        }
        
        System.out.println();
    }
    
    private void chooseAndExecuteMove() {
        System.out.println("Choose a move:");
        Move[] moves = playerPokemon.getAvailableMoves();
        List<Move> availableMoves = new ArrayList<>();
        
        for (int i = 0; i < moves.length; i++) {
            if (moves[i] != null) {
                availableMoves.add(moves[i]);
                System.out.println((i + 1) + ". " + moves[i].getName() + 
                                 " (Power: " + moves[i].getPower() + ", Acc: " + moves[i].getAccuracy() + "%)");
            }
        }
        
        if (availableMoves.isEmpty()) {
            System.out.println("No moves available!");
            return;
        }
        
        int moveChoice = getValidInput(1, availableMoves.size());
        Move selectedMove = availableMoves.get(moveChoice - 1);
        
        executeAttack(playerPokemon, opponentPokemon, selectedMove);
    }
    
    private void useItem() {
        List<ItemType> availableItems = inventory.getAvailableItems();
        
        if (availableItems.isEmpty()) {
            System.out.println("No items available!");
            return;
        }
        
        System.out.println("Choose an item:");
        for (int i = 0; i < availableItems.size(); i++) {
            System.out.println((i + 1) + ". " + availableItems.get(i).name());
        }
        
        int itemChoice = getValidInput(1, availableItems.size());
        ItemType selectedItem = availableItems.get(itemChoice - 1);
        
        if (inventory.useItem(selectedItem)) {
            applyItemEffect(selectedItem, playerPokemon);
        }
    }
    
    private void applyItemEffect(ItemType item, Pokemon target) {
        switch (item) {
            case POTION:
                target.heal(20);
                System.out.println(target.getName() + " restored 20 HP!");
                break;
            case SUPER_POTION:
                target.heal(50);
                System.out.println(target.getName() + " restored 50 HP!");
                break;
            case HYPER_POTION:
                target.heal(120);
                System.out.println(target.getName() + " restored 120 HP!");
                break;
            case X_ATTACK:
                target.boostAttack(1);
                break;
            case X_DEFENSE:
                target.boostDefense(1);
                break;
            case ANTIDOTE:
                if (target.getStatusEffect() == StatusEffect.POISON) {
                    target.cureStatus();
                } else {
                    System.out.println("It had no effect!");
                }
                break;
            case PARALYZE_HEAL:
                if (target.getStatusEffect() == StatusEffect.PARALYSIS) {
                    target.cureStatus();
                } else {
                    System.out.println("It had no effect!");
                }
                break;
            case AWAKENING:
                if (target.getStatusEffect() == StatusEffect.SLEEP) {
                    target.cureStatus();
                } else {
                    System.out.println("It had no effect!");
                }
                break;
        }
    }
    
    private void opponentTurn() {
        System.out.println("--- " + opponentPokemon.getName() + "'s Turn ---");
        
        if (!opponentPokemon.canAct()) {
            System.out.println(opponentPokemon.getName() + " cannot act due to status effect!");
            return;
        }
        
        // Simple AI - randomly choose between attack and special ability
        if (random.nextInt(100) < 80) { // 80% chance to attack
            Move selectedMove = getRandomAvailableMove(opponentPokemon);
            if (selectedMove != null) {
                executeAttack(opponentPokemon, playerPokemon, selectedMove);
            }
        } else {
            opponentPokemon.useSpecialAbility();
        }
        
        System.out.println();
    }
    
    private Move getRandomAvailableMove(Pokemon pokemon) {
        Move[] moves = pokemon.getAvailableMoves();
        List<Move> availableMoves = new ArrayList<>();
        
        for (Move move : moves) {
            if (move != null) {
                availableMoves.add(move);
            }
        }
        
        if (availableMoves.isEmpty()) {
            return null;
        }
        
        return availableMoves.get(random.nextInt(availableMoves.size()));
    }
    
    private void executeAttack(Pokemon attacker, Pokemon defender, Move move) {
        System.out.println(attacker.getName() + " uses " + move.getName() + "!");
        
        // Check if move hits based on accuracy
        if (random.nextInt(100) < move.getAccuracy()) {
            // Check for critical hit (6.25% chance)
            boolean criticalHit = random.nextInt(16) == 0;
            
            // Calculate damage
            int damage = calculateDamage(attacker, defender, move, criticalHit);
            
            // Apply damage
            defender.takeDamage(damage);
            
            // Get type effectiveness
            double effectiveness = TypeEffectiveness.getEffectiveness(move.getType(), defender.getType());
            String effectivenessMsg = TypeEffectiveness.getEffectivenessMessage(effectiveness);
            
            System.out.println(defender.getName() + " takes " + damage + " damage!");
            if (criticalHit) {
                System.out.println("Critical hit!");
            }
            if (!effectivenessMsg.isEmpty()) {
                System.out.println(effectivenessMsg);
            }
            
            // Apply status effect if move has one
            if (move.getStatusEffect() != StatusEffect.NONE && random.nextInt(100) < move.getStatusChance()) {
                defender.applyStatusEffect(move.getStatusEffect());
            }
            
            // Show defender's current HP
            System.out.println(defender.getName() + " HP: " + defender.getCurrentHp() + "/" + defender.getMaxHp());
            
        } else {
            System.out.println(attacker.getName() + "'s attack missed!");
        }
    }
    
    private int calculateDamage(Pokemon attacker, Pokemon defender, Move move, boolean criticalHit) {
        // Enhanced damage calculation
        double baseDamage = ((2.0 * attacker.getLevel() / 5.0 + 2.0) * move.getPower() * 
                            attacker.getCurrentAttack() / defender.getCurrentDefense()) / 50.0 + 2.0;
        
        // Apply critical hit multiplier
        if (criticalHit) {
            baseDamage *= 2.0;
        }
        
        // Apply type effectiveness
        double effectiveness = TypeEffectiveness.getEffectiveness(move.getType(), defender.getType());
        baseDamage *= effectiveness;
        
        // Same-type attack bonus (STAB)
        if (move.getType().name().equals(attacker.getType().name())) {
            baseDamage *= 1.5;
        }
        
        // Add randomness (85-100% of calculated damage)
        double randomFactor = 0.85 + (random.nextDouble() * 0.15);
        baseDamage *= randomFactor;
        
        return Math.max(1, (int)baseDamage);
    }
    
    private int getValidInput(int min, int max) {
        int input;
        while (true) {
            try {
                System.out.print("Enter choice (" + min + "-" + max + "): ");
                input = Integer.parseInt(scanner.nextLine());
                if (input >= min && input <= max) {
                    return input;
                } else {
                    System.out.println("Please enter a number between " + min + " and " + max);
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number!");
            }
        }
    }
    
    public Pokemon getWinner() {
        if (playerPokemon.isDefeated()) {
            return opponentPokemon;
        } else if (opponentPokemon.isDefeated()) {
            return playerPokemon;
        }
        return null;
    }
    
    public boolean isPlayerVictorious() {
        return opponentPokemon.isDefeated() && !playerPokemon.isDefeated();
    }
}

// Enhanced demo class
class EnhancedBattleDemo {
    public static void main(String[] args) {
        // Create sample Pokemon with more diverse movesets
        FirePokemon playerPokemon = new FirePokemon("Charizard", 100, 80, 65, 25);
        GrassPokemon wildPokemon = new GrassPokemon("Venusaur", 95, 75, 70, 24);
        
        // Add moves with status effects
        playerPokemon.addMove(new Move("Flamethrower", MoveType.FIRE, 90, 85, StatusEffect.BURN, 10), 0);
        playerPokemon.addMove(new Move("Dragon Claw", MoveType.NORMAL, 80, 95), 1);
        playerPokemon.addMove(new Move("Thunder Punch", MoveType.ELECTRIC, 75, 90, StatusEffect.PARALYSIS, 20), 2);
        playerPokemon.addMove(new Move("Fire Blast", MoveType.FIRE, 110, 75), 3);
        
        wildPokemon.addMove(new Move("Solar Beam", MoveType.GRASS, 120, 95), 0);
        wildPokemon.addMove(new Move("Sludge Bomb", MoveType.POISON, 90, 90, StatusEffect.POISON, 30), 1);
        wildPokemon.addMove(new Move("Sleep Powder", MoveType.GRASS, 0, 75, StatusEffect.SLEEP, 100), 2);
        wildPokemon.addMove(new Move("Earthquake", MoveType.GROUND, 100, 90), 3);
        
        // Create inventory
        PlayerInventory inventory = new PlayerInventory();
        
        // Start enhanced battle
        Battle battle = new Battle(playerPokemon, wildPokemon, inventory);
        battle.startBattle();
        
        // Show battle result
        if (battle.isPlayerVictorious()) {
            System.out.println("\nCongratulations! You can now attempt to catch the wild Pokemon!");
        } else {
            System.out.println("\nBetter luck next time! Train your Pokemon and try again!");
        }
    }
}
