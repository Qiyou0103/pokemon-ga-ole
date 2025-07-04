import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.Arrays;
import java.util.stream.Collectors;

// ================= CORE ENUMS =================
enum PokemonType {
    FIRE, WATER, GRASS, ELECTRIC, NORMAL, FIGHTING, POISON, GROUND, FLYING, PSYCHIC, BUG, ROCK, ICE, DRAGON, DARK, STEEL
}

enum MoveType {
    FIRE, WATER, GRASS, ELECTRIC, NORMAL, FIGHTING, POISON, GROUND, FLYING, PSYCHIC, BUG, ROCK, ICE, DRAGON, DARK, STEEL
}

enum StatusEffect {
    NONE, POISON, PARALYSIS, SLEEP, BURN, FREEZE, CONFUSION, FLINCH
}

enum ItemType {
    POTION, SUPER_POTION, HYPER_POTION, MAX_POTION, FULL_RESTORE,
    X_ATTACK, X_DEFENSE, X_SPEED, X_SPECIAL, DIRE_HIT,
    ANTIDOTE, PARALYZE_HEAL, AWAKENING, BURN_HEAL, ICE_HEAL, FULL_HEAL,
    RARE_CANDY, REVIVE, MAX_REVIVE, ELIXER, MAX_ELIXER,
    POKE_BALL, GREAT_BALL, ULTRA_BALL, MASTER_BALL
}

enum BattleActionType {
    ATTACK, USE_ITEM, SWITCH_POKEMON, RUN_AWAY, USE_ABILITY
}

// ================= INTERFACES =================
interface BattleObserver {
    void onBattleStart(BattleContext context);
    void onTurnStart(Pokemon activePokemon, int turnNumber);
    void onMoveUsed(Pokemon attacker, Move move, Pokemon target);
    void onItemUsed(ItemType item, Pokemon target);
    void onStatusEffectApplied(Pokemon pokemon, StatusEffect effect);
    void onPokemonFainted(Pokemon pokemon);
    void onBattleEnd(BattleResult result);
}

interface Targetable {
    boolean canBeTargeted();
    String getTargetName();
}

interface ItemEffect {
    boolean apply(Pokemon target, BattleContext context);
    String getEffectDescription();
    boolean canUseOn(Pokemon target);
}

interface StatusCondition {
    void apply(Pokemon pokemon);
    void process(Pokemon pokemon);
    boolean preventsAction();
    String getDescription();
}

// ================= EVENTS & CONTEXT =================
class BattleEvent {
    private final String type;
    private final Object data;
    private final long timestamp;
    
    public BattleEvent(String type, Object data) {
        this.type = type;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getType() { return type; }
    public Object getData() { return data; }
    public long getTimestamp() { return timestamp; }
}

class BattleContext {
    private final Pokemon playerPokemon;
    private final Pokemon opponentPokemon;
    private final String battleType;
    private final Map<String, Object> battleData;
    private final List<BattleEvent> eventHistory;
    private int turnCount;
    private boolean playerTurn;
    
    public BattleContext(Pokemon playerPokemon, Pokemon opponentPokemon, String battleType) {
        this.playerPokemon = playerPokemon;
        this.opponentPokemon = opponentPokemon;
        this.battleType = battleType;
        this.battleData = new ConcurrentHashMap<>();
        this.eventHistory = new ArrayList<>();
        this.turnCount = 0;
        this.playerTurn = true;
    }
    
    public void addEvent(BattleEvent event) {
        eventHistory.add(event);
    }
    
    public void setBattleData(String key, Object value) {
        battleData.put(key, value);
    }
    
    public Object getBattleData(String key) {
        return battleData.get(key);
    }
    
    public void incrementTurn() { 
        turnCount++; 
        playerTurn = !playerTurn;
    }
    
    public Pokemon getCurrentActivePokemon() {
        return playerTurn ? playerPokemon : opponentPokemon;
    }
    
    public Pokemon getOpposingPokemon() {
        return playerTurn ? opponentPokemon : playerPokemon;
    }
    
    // Getters
    public Pokemon getPlayerPokemon() { return playerPokemon; }
    public Pokemon getOpponentPokemon() { return opponentPokemon; }
    public String getBattleType() { return battleType; }
    public int getTurnCount() { return turnCount; }
    public boolean isPlayerTurn() { return playerTurn; }
    public List<BattleEvent> getEventHistory() { return new ArrayList<>(eventHistory); }
}

class BattleResult {
    private final Pokemon winner;
    private final Pokemon loser;
    private final String battleType;
    private final int totalTurns;
    private final boolean playerVictory;
    
    public BattleResult(Pokemon winner, Pokemon loser, String battleType, int totalTurns, boolean playerVictory) {
        this.winner = winner;
        this.loser = loser;
        this.battleType = battleType;
        this.totalTurns = totalTurns;
        this.playerVictory = playerVictory;
    }
    
    public Pokemon getWinner() { return winner; }
    public Pokemon getLoser() { return loser; }
    public String getBattleType() { return battleType; }
    public int getTotalTurns() { return totalTurns; }
    public boolean isPlayerVictory() { return playerVictory; }
}

// ================= STATUS CONDITIONS =================
class PoisonCondition implements StatusCondition {
    @Override
    public void apply(Pokemon pokemon) {
        System.out.println(pokemon.getName() + " has been poisoned!");
    }
    
    @Override
    public void process(Pokemon pokemon) {
        int damage = Math.max(1, pokemon.getMaxHp() / 8);
        pokemon.takeDamage(damage);
        System.out.println(pokemon.getName() + " is hurt by poison! (-" + damage + " HP)");
    }
    
    @Override
    public boolean preventsAction() { return false; }
    
    @Override
    public String getDescription() { return "Loses HP each turn"; }
}

class ParalysisCondition implements StatusCondition {
    private Random random = new Random();
    
    @Override
    public void apply(Pokemon pokemon) {
        System.out.println(pokemon.getName() + " is paralyzed!");
        pokemon.multiplySpeed(0.5);
    }
    
    @Override
    public void process(Pokemon pokemon) {
        // Paralysis is checked in preventsAction
    }
    
    @Override
    public boolean preventsAction() {
        return random.nextInt(100) < 25; // 25% chance to be unable to move
    }
    
    @Override
    public String getDescription() { return "May be unable to move, speed reduced"; }
}

class SleepCondition implements StatusCondition {
    @Override
    public void apply(Pokemon pokemon) {
        System.out.println(pokemon.getName() + " fell asleep!");
    }
    
    @Override
    public void process(Pokemon pokemon) {
        System.out.println(pokemon.getName() + " is fast asleep...");
    }
    
    @Override
    public boolean preventsAction() { return true; }
    
    @Override
    public String getDescription() { return "Cannot move while asleep"; }
}

class BurnCondition implements StatusCondition {
    @Override
    public void apply(Pokemon pokemon) {
        System.out.println(pokemon.getName() + " was burned!");
        pokemon.multiplyAttack(0.5);
    }
    
    @Override
    public void process(Pokemon pokemon) {
        int damage = Math.max(1, pokemon.getMaxHp() / 16);
        pokemon.takeDamage(damage);
        System.out.println(pokemon.getName() + " is hurt by its burn! (-" + damage + " HP)");
    }
    
    @Override
    public boolean preventsAction() { return false; }
    
    @Override
    public String getDescription() { return "Loses HP each turn, attack reduced"; }
}

// ================= STATUS MANAGER =================
class StatusManager {
    private static final Map<StatusEffect, StatusCondition> CONDITIONS = new HashMap<>();
    
    static {
        CONDITIONS.put(StatusEffect.POISON, new PoisonCondition());
        CONDITIONS.put(StatusEffect.PARALYSIS, new ParalysisCondition());
        CONDITIONS.put(StatusEffect.SLEEP, new SleepCondition());
        CONDITIONS.put(StatusEffect.BURN, new BurnCondition());
    }
    
    public static void applyStatusEffect(Pokemon pokemon, StatusEffect effect) {
        if (effect == StatusEffect.NONE || pokemon.getStatusEffect() != StatusEffect.NONE) {
            return; // Can't apply status to already statused Pokemon
        }
        
        StatusCondition condition = CONDITIONS.get(effect);
        if (condition != null) {
            pokemon.setStatusEffect(effect, 3); // Default 3 turns
            condition.apply(pokemon);
        }
    }
    
    public static void processStatusEffect(Pokemon pokemon) {
        StatusEffect effect = pokemon.getStatusEffect();
        if (effect == StatusEffect.NONE) return;
        
        StatusCondition condition = CONDITIONS.get(effect);
        if (condition != null) {
            condition.process(pokemon);
        }
        
        pokemon.decrementStatusTurns();
        if (pokemon.getStatusTurns() <= 0) {
            cureStatus(pokemon);
        }
    }
    
    public static boolean canAct(Pokemon pokemon) {
        StatusEffect effect = pokemon.getStatusEffect();
        if (effect == StatusEffect.NONE) return true;
        
        StatusCondition condition = CONDITIONS.get(effect);
        return condition == null || !condition.preventsAction();
    }
    
    public static void cureStatus(Pokemon pokemon) {
        StatusEffect oldEffect = pokemon.getStatusEffect();
        if (oldEffect != StatusEffect.NONE) {
            pokemon.cureStatus();
            System.out.println(pokemon.getName() + " recovered from " + oldEffect.toString().toLowerCase() + "!");
        }
    }
}

// ================= ITEM EFFECTS =================
class HealingEffect implements ItemEffect {
    private final int healAmount;
    
    public HealingEffect(int healAmount) {
        this.healAmount = healAmount;
    }
    
    @Override
    public boolean apply(Pokemon target, BattleContext context) {
        int oldHp = target.getCurrentHp();
        target.heal(healAmount);
        int actualHealing = target.getCurrentHp() - oldHp;
        
        if (actualHealing > 0) {
            System.out.println(target.getName() + " restored " + actualHealing + " HP!");
            return true;
        }
        return false;
    }
    
    @Override
    public String getEffectDescription() {
        return "Restores " + healAmount + " HP";
    }
    
    @Override
    public boolean canUseOn(Pokemon target) {
        return target.getCurrentHp() < target.getMaxHp();
    }
}

class FullHealingEffect implements ItemEffect {
    @Override
    public boolean apply(Pokemon target, BattleContext context) {
        int oldHp = target.getCurrentHp();
        target.heal(target.getMaxHp());
        int actualHealing = target.getCurrentHp() - oldHp;
        
        if (actualHealing > 0) {
            System.out.println(target.getName() + " was fully healed!");
            return true;
        }
        return false;
    }
    
    @Override
    public String getEffectDescription() {
        return "Fully restores HP";
    }
    
    @Override
    public boolean canUseOn(Pokemon target) {
        return target.getCurrentHp() < target.getMaxHp();
    }
}

class StatBoostEffect implements ItemEffect {
    private final String statName;
    private final int stages;
    
    public StatBoostEffect(String statName, int stages) {
        this.statName = statName;
        this.stages = stages;
    }
    
    @Override
    public boolean apply(Pokemon target, BattleContext context) {
        switch (statName.toLowerCase()) {
            case "attack":
                target.boostAttack(stages);
                break;
            case "defense":
                target.boostDefense(stages);
                break;
            case "speed":
                target.boostSpeed(stages);
                break;
            case "critical":
                target.boostCriticalHitRatio(stages);
                break;
            default:
                return false;
        }
        return true;
    }
    
    @Override
    public String getEffectDescription() {
        return "Raises " + statName + " by " + stages + " stage(s)";
    }
    
    @Override
    public boolean canUseOn(Pokemon target) {
        return !target.isDefeated();
    }
}

class StatusCureEffect implements ItemEffect {
    private final StatusEffect targetStatus;
    private final boolean cureAll;
    
    public StatusCureEffect(StatusEffect targetStatus) {
        this.targetStatus = targetStatus;
        this.cureAll = false;
    }
    
    public StatusCureEffect() {
        this.targetStatus = StatusEffect.NONE;
        this.cureAll = true;
    }
    
    @Override
    public boolean apply(Pokemon target, BattleContext context) {
        if (cureAll) {
            if (target.getStatusEffect() != StatusEffect.NONE) {
                StatusManager.cureStatus(target);
                return true;
            }
        } else {
            if (target.getStatusEffect() == targetStatus) {
                StatusManager.cureStatus(target);
                return true;
            }
        }
        
        System.out.println("It had no effect on " + target.getName() + "!");
        return false;
    }
    
    @Override
    public String getEffectDescription() {
        return cureAll ? "Cures all status conditions" : "Cures " + targetStatus.toString().toLowerCase();
    }
    
    @Override
    public boolean canUseOn(Pokemon target) {
        return target.getStatusEffect() != StatusEffect.NONE;
    }
}

class ReviveEffect implements ItemEffect {
    private final boolean fullRestore;
    
    public ReviveEffect(boolean fullRestore) {
        this.fullRestore = fullRestore;
    }
    
    @Override
    public boolean apply(Pokemon target, BattleContext context) {
        if (target.isDefeated()) {
            if (fullRestore) {
                target.revive(target.getMaxHp());
                System.out.println(target.getName() + " was revived and fully healed!");
            } else {
                target.revive(target.getMaxHp() / 2);
                System.out.println(target.getName() + " was revived with half HP!");
            }
            return true;
        }
        
        System.out.println("It had no effect on " + target.getName() + "!");
        return false;
    }
    
    @Override
    public String getEffectDescription() {
        return fullRestore ? "Revives with full HP" : "Revives with half HP";
    }
    
    @Override
    public boolean canUseOn(Pokemon target) {
        return target.isDefeated();
    }
}

// ================= ENHANCED ITEM SYSTEM =================
abstract class Item {
    protected String name;
    protected ItemType type;
    protected String description;
    protected ItemEffect effect;
    protected int value;
    
    public Item(String name, ItemType type, String description, ItemEffect effect, int value) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.effect = effect;
        this.value = value;
    }
    
    public boolean use(Pokemon target, BattleContext context) {
        if (effect.canUseOn(target)) {
            return effect.apply(target, context);
        }
        return false;
    }
    
    public void displayInfo() {
        System.out.println("=== " + name + " ===");
        System.out.println(description);
        System.out.println("Effect: " + effect.getEffectDescription());
        System.out.println("Value: " + value + " coins");
    }
    
    // Getters
    public String getName() { return name; }
    public ItemType getType() { return type; }
    public String getDescription() { return description; }
    public ItemEffect getEffect() { return effect; }
    public int getValue() { return value; }
}

class ConsumableItem extends Item {
    public ConsumableItem(String name, ItemType type, String description, ItemEffect effect, int value) {
        super(name, type, description, effect, value);
    }
}

class KeyItem extends Item {
    public KeyItem(String name, ItemType type, String description, ItemEffect effect) {
        super(name, type, description, effect, 0); // Key items have no value
    }
    
    @Override
    public boolean use(Pokemon target, BattleContext context) {
        System.out.println("Key items cannot be used in battle!");
        return false;
    }
}

// ================= ITEM FACTORY =================
class ItemFactory {
    private static final Map<ItemType, Item> ITEM_TEMPLATES = new HashMap<>();
    
    static {
        // Healing items
        ITEM_TEMPLATES.put(ItemType.POTION, new ConsumableItem("Potion", ItemType.POTION, 
            "A basic healing potion", new HealingEffect(20), 200));
        ITEM_TEMPLATES.put(ItemType.SUPER_POTION, new ConsumableItem("Super Potion", ItemType.SUPER_POTION,
            "A strong healing potion", new HealingEffect(50), 700));
        ITEM_TEMPLATES.put(ItemType.HYPER_POTION, new ConsumableItem("Hyper Potion", ItemType.HYPER_POTION,
            "A very strong healing potion", new HealingEffect(120), 1500));
        ITEM_TEMPLATES.put(ItemType.MAX_POTION, new ConsumableItem("Max Potion", ItemType.MAX_POTION,
            "Fully restores HP", new FullHealingEffect(), 2500));
        
        // Status cure items
        ITEM_TEMPLATES.put(ItemType.ANTIDOTE, new ConsumableItem("Antidote", ItemType.ANTIDOTE,
            "Cures poison", new StatusCureEffect(StatusEffect.POISON), 200));
        ITEM_TEMPLATES.put(ItemType.PARALYZE_HEAL, new ConsumableItem("Paralyze Heal", ItemType.PARALYZE_HEAL,
            "Cures paralysis", new StatusCureEffect(StatusEffect.PARALYSIS), 300));
        ITEM_TEMPLATES.put(ItemType.AWAKENING, new ConsumableItem("Awakening", ItemType.AWAKENING,
            "Wakes up sleeping Pokemon", new StatusCureEffect(StatusEffect.SLEEP), 200));
        ITEM_TEMPLATES.put(ItemType.BURN_HEAL, new ConsumableItem("Burn Heal", ItemType.BURN_HEAL,
            "Cures burns", new StatusCureEffect(StatusEffect.BURN), 300));
        ITEM_TEMPLATES.put(ItemType.FULL_HEAL, new ConsumableItem("Full Heal", ItemType.FULL_HEAL,
            "Cures all status conditions", new StatusCureEffect(), 800));
        
        // Stat boost items
        ITEM_TEMPLATES.put(ItemType.X_ATTACK, new ConsumableItem("X Attack", ItemType.X_ATTACK,
            "Raises Attack stat", new StatBoostEffect("attack", 1), 500));
        ITEM_TEMPLATES.put(ItemType.X_DEFENSE, new ConsumableItem("X Defense", ItemType.X_DEFENSE,
            "Raises Defense stat", new StatBoostEffect("defense", 1), 500));
        ITEM_TEMPLATES.put(ItemType.X_SPEED, new ConsumableItem("X Speed", ItemType.X_SPEED,
            "Raises Speed stat", new StatBoostEffect("speed", 1), 500));
        ITEM_TEMPLATES.put(ItemType.DIRE_HIT, new ConsumableItem("Dire Hit", ItemType.DIRE_HIT,
            "Raises critical hit ratio", new StatBoostEffect("critical", 1), 700));
        
        // Revival items
        ITEM_TEMPLATES.put(ItemType.REVIVE, new ConsumableItem("Revive", ItemType.REVIVE,
            "Revives fainted Pokemon with half HP", new ReviveEffect(false), 2000));
        ITEM_TEMPLATES.put(ItemType.MAX_REVIVE, new ConsumableItem("Max Revive", ItemType.MAX_REVIVE,
            "Revives fainted Pokemon with full HP", new ReviveEffect(true), 4000));
    }
    
    public static Item createItem(ItemType type) {
        Item template = ITEM_TEMPLATES.get(type);
        if (template == null) {
            throw new IllegalArgumentException("Unknown item type: " + type);
        }
        
        // Create new instance based on template
        return new ConsumableItem(template.getName(), template.getType(), 
            template.getDescription(), template.getEffect(), template.getValue());
    }
    
    public static Item getTemplate(ItemType type) {
        return ITEM_TEMPLATES.get(type);
    }
}

// ================= INVENTORY MANAGER =================
class InventoryManager {
    private final Map<ItemType, Integer> itemCounts;
    private final Map<ItemType, Item> itemTemplates;
    private final List<BattleObserver> observers;
    
    public InventoryManager() {
        this.itemCounts = new ConcurrentHashMap<>();
        this.itemTemplates = new HashMap<>();
        this.observers = new ArrayList<>();
        initializeInventory();
    }
    
    private void initializeInventory() {
        // Starting inventory
        addItem(ItemType.POTION, 5);
        addItem(ItemType.SUPER_POTION, 2);
        addItem(ItemType.HYPER_POTION, 1);
        addItem(ItemType.X_ATTACK, 2);
        addItem(ItemType.X_DEFENSE, 2);
        addItem(ItemType.X_SPEED, 1);
        addItem(ItemType.ANTIDOTE, 2);
        addItem(ItemType.PARALYZE_HEAL, 1);
        addItem(ItemType.AWAKENING, 1);
        addItem(ItemType.BURN_HEAL, 1);
        addItem(ItemType.FULL_HEAL, 1);
        addItem(ItemType.REVIVE, 1);
        
        // Cache templates
        for (ItemType type : ItemType.values()) {
            try {
                itemTemplates.put(type, ItemFactory.getTemplate(type));
            } catch (IllegalArgumentException e) {
                // Item type not implemented yet
            }
        }
    }
    
    public void addObserver(BattleObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(BattleObserver observer) {
        observers.remove(observer);
    }
    
    public void addItem(ItemType type, int quantity) {
        if (quantity <= 0) return;
        itemCounts.put(type, itemCounts.getOrDefault(type, 0) + quantity);
    }
    
    public boolean removeItem(ItemType type, int quantity) {
        int current = itemCounts.getOrDefault(type, 0);
        if (current >= quantity) {
            itemCounts.put(type, current - quantity);
            return true;
        }
        return false;
    }
    
    public boolean hasItem(ItemType type) {
        return itemCounts.getOrDefault(type, 0) > 0;
    }
    
    public int getItemCount(ItemType type) {
        return itemCounts.getOrDefault(type, 0);
    }
    
    public boolean useItem(ItemType type, Pokemon target, BattleContext context) {
        if (!hasItem(type)) {
            System.out.println("You don't have any " + type.name() + "!");
            return false;
        }
        
        Item item = ItemFactory.createItem(type);
        boolean success = item.use(target, context);
        
        if (success) {
            removeItem(type, 1);
            notifyItemUsed(type, target);
        }
        
        return success;
    }
    
    private void notifyItemUsed(ItemType type, Pokemon target) {
        for (BattleObserver observer : observers) {
            observer.onItemUsed(type, target);
        }
    }
    
    public void displayInventory() {
        System.out.println("\n=== INVENTORY ===");
        boolean hasItems = false;
        
        for (ItemType type : ItemType.values()) {
            int count = itemCounts.getOrDefault(type, 0);
            if (count > 0) {
                Item template = itemTemplates.get(type);
                if (template != null) {
                    System.out.println(count + "x " + template.getName() + " - " + template.getDescription());
                    hasItems = true;
                }
            }
        }
        
        if (!hasItems) {
            System.out.println("Your inventory is empty!");
        }
        System.out.println("=================\n");
    }
    
    public List<ItemType> getAvailableItems() {
        return itemCounts.entrySet().stream()
            .filter(entry -> entry.getValue() > 0)
            .map(Map.Entry::getKey)
            .filter(type -> itemTemplates.containsKey(type))
            .sorted()
            .toList();
    }
    
    public List<ItemType> getUsableItems(Pokemon target) {
        return getAvailableItems().stream()
            .filter(type -> {
                Item template = itemTemplates.get(type);
                return template != null && template.getEffect().canUseOn(target);
            })
            .toList();
    }
}

// ================= ENHANCED MOVE SYSTEM =================
class Move {
    private final String name;
    private final MoveType type;
    private final int power;
    private final int accuracy;
    private final int pp;
    private final StatusEffect statusEffect;
    private final int statusChance;
    private final String description;
    private final Consumer<BattleContext> specialEffect;
    private int currentPP;
    
    public Move(String name, MoveType type, int power, int accuracy, int pp, String description) {
        this(name, type, power, accuracy, pp, description, StatusEffect.NONE, 0, null);
    }
    
    public Move(String name, MoveType type, int power, int accuracy, int pp, String description,
                StatusEffect statusEffect, int statusChance, Consumer<BattleContext> specialEffect) {
        this.name = name;
        this.type = type;
        this.power = power;
        this.accuracy = accuracy;
        this.pp = pp;
        this.currentPP = pp;
        this.description = description;
        this.statusEffect = statusEffect;
        this.statusChance = statusChance;
        this.specialEffect = specialEffect;
    }
    
    public boolean canUse() {
        return currentPP > 0;
    }
    
    public void use() {
        if (currentPP > 0) {
            currentPP--;
        }
    }
    
    public void restorePP(int amount) {
        currentPP = Math.min(pp, currentPP + amount);
    }
    
    public void executeSpecialEffect(BattleContext context) {
        if (specialEffect != null) {
            specialEffect.accept(context);
        }
    }
    
    // Getters
    public String getName() { return name; }
    public PokemonType getType() { return type; }
    public int getMaxHp() { return maxHp; }
    public int getCurrentHp() { return currentHp; }
    public int getBaseAttack() { return baseAttack; }
    public int getCurrentAttack() { return currentAttack; }
    public int getBaseDefense() { return baseDefense; }
    public int getCurrentDefense() { return currentDefense; }
    public int getBaseSpeed() { return baseSpeed; }
    public int getCurrentSpeed() { return currentSpeed; }
    public int getBaseSpecialAttack() { return baseSpecialAttack; }
    public int getCurrentSpecialAttack() { return currentSpecialAttack; }
    public int getBaseSpecialDefense() { return baseSpecialDefense; }
    public int getCurrentSpecialDefense() { return currentSpecialDefense; }
    public int getLevel() { return level; }
    public boolean isDefeated() { return isDefeated; }
}

// ================= ENHANCED POKEMON =================
abstract class Pokemon implements Targetable {
    protected String name;
    protected PokemonType type;
    protected int maxHp;
    protected int currentHp;
    protected int baseAttack;
    protected int baseDefense;
    protected int baseSpecialAttack;
    protected int baseSpecialDefense;
    protected int baseSpeed;
    protected int currentAttack;
    protected int currentDefense;
    protected int currentSpecialAttack;
    protected int currentSpecialDefense;
    protected int currentSpeed;
    protected int level;
    protected Move[] moves;
    protected StatusEffect statusEffect;
    protected int statusTurns;
    protected int criticalHitStage;
    protected boolean isDefeated;

    // Stat stage counters (-6 to +6)
    protected int attackStages;
    protected int defenseStages;
    protected int specialAttackStages;
    protected int specialDefenseStages;
    protected int speedStages;
    protected int accuracyStages;
    protected int evasionStages;
    
    public Pokemon(String name, PokemonType type, int maxHp, int attack, int defense, int speed, int level) {
        this.name = name;
        this.type = type;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.baseAttack = attack;
        this.baseDefense = defense;
        this.baseSpecialAttack = specialAttack;
        this.baseSpecialDefense = specialDefense;
        this.baseSpeed = speed;
        this.currentAttack = attack;
        this.currentDefense = defense;
        this.currentSpecialAttack = specialAttack;
        this.currentSpecialDefense = specialDefense;
        this.currentSpeed = speed;
        this.level = level;
        this.moves = new Move[4];
        this.statusEffect = StatusEffect.NONE;
        this.statusTurns = 0;
        this.criticalHitStage = 0;
        this.isDefeated = false;
    }
    
    public abstract void useSpecialAbility(BattleContext context);
    
    @Override
    public boolean canBeTargeted() {
        return !isDefeated;
    }
    
    @Override
    public String getTargetName() {
        return name;
    }
    
    // Status management
    public void setStatusEffect(StatusEffect effect, int turns) {
        this.statusEffect = effect;
        this.statusTurns = turns;
    }
    
    public void decrementStatusTurns() {
        if (statusTurns > 0) {
            statusTurns--;
        }
    }
    
    public void cureStatus() {
        this.statusEffect = StatusEffect.NONE;
        this.statusTurns = 0;
        // Restore original stats
        this.currentAttack = baseAttack;
        this.currentDefense = baseDefense;
        this.currentSpeed = baseSpeed;
    }
    
    // Stat modifications
public void boostAttack(int stages) {
    double multiplier = Math.pow(1.5, Math.max(-6, Math.min(6, stages)));
    this.currentAttack = (int) (this.baseAttack * multiplier);
    this.attackStages = Math.max(-6, Math.min(6, this.attackStages + stages));
}

public void boostDefense(int stages) {
    double multiplier = Math.pow(1.5, Math.max(-6, Math.min(6, stages)));
    this.currentDefense = (int) (this.baseDefense * multiplier);
    this.defenseStages = Math.max(-6, Math.min(6, this.defenseStages + stages));
}

public void boostSpeed(int stages) {
    double multiplier = Math.pow(1.5, Math.max(-6, Math.min(6, stages)));
    this.currentSpeed = (int) (this.baseSpeed * multiplier);
    this.speedStages = Math.max(-6, Math.min(6, this.speedStages + stages));
}

public void boostSpecialAttack(int stages) {
    double multiplier = Math.pow(1.5, Math.max(-6, Math.min(6, stages)));
    this.currentSpecialAttack = (int) (this.baseSpecialAttack * multiplier);
    this.specialAttackStages = Math.max(-6, Math.min(6, this.specialAttackStages + stages));
}

public void boostSpecialDefense(int stages) {
    double multiplier = Math.pow(1.5, Math.max(-6, Math.min(6, stages)));
    this.currentSpecialDefense = (int) (this.baseSpecialDefense * multiplier);
    this.specialDefenseStages = Math.max(-6, Math.min(6, this.specialDefenseStages + stages));
}

public void boostAccuracy(int stages) {
    this.accuracyStages = Math.max(-6, Math.min(6, this.accuracyStages + stages));
}

public void boostEvasion(int stages) {
    this.evasionStages = Math.max(-6, Math.min(6, this.evasionStages + stages));
}

// Reset all stat modifications
public void resetStatModifications() {
    this.currentAttack = this.baseAttack;
    this.currentDefense = this.baseDefense;
    this.currentSpeed = this.baseSpeed;
    this.currentSpecialAttack = this.baseSpecialAttack;
    this.currentSpecialDefense = this.baseSpecialDefense;
    
    this.attackStages = 0;
    this.defenseStages = 0;
    this.speedStages = 0;
    this.specialAttackStages = 0;
    this.specialDefenseStages = 0;
    this.accuracyStages = 0;
    this.evasionStages = 0;
}

// Get current stat multiplier for a given stage
public double getStatMultiplier(int stages) {
    return Math.pow(1.5, Math.max(-6, Math.min(6, stages)));
}

// Get accuracy multiplier (different formula than stats)
public double getAccuracyMultiplier() {
    if (accuracyStages >= 0) {
        return (3.0 + accuracyStages) / 3.0;
    } else {
        return 3.0 / (3.0 + Math.abs(accuracyStages));
    }
}

// Get evasion multiplier (different formula than stats)
public double getEvasionMultiplier() {
    if (evasionStages >= 0) {
        return (3.0 + evasionStages) / 3.0;
    } else {
        return 3.0 / (3.0 + Math.abs(evasionStages));
    }
}

// Apply all current stat modifications
public void applyStatModifications() {
    this.currentAttack = (int) (this.baseAttack * getStatMultiplier(this.attackStages));
    this.currentDefense = (int) (this.baseDefense * getStatMultiplier(this.defenseStages));
    this.currentSpeed = (int) (this.baseSpeed * getStatMultiplier(this.speedStages));
    this.currentSpecialAttack = (int) (this.baseSpecialAttack * getStatMultiplier(this.specialAttackStages));
    this.currentSpecialDefense = (int) (this.baseSpecialDefense * getStatMultiplier(this.specialDefenseStages));
}
