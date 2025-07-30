import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Pokemon implements Serializable {
   private static final long serialVersionUID = 1L;
   protected String name;
   protected int level;
   protected int maxHp;
   protected int currentHp;
   protected int attack;
   protected int defense;
   protected int speed;
   protected PokemonType type;
   protected String status; // e.g., "Normal", "Poisoned", "Paralyzed"
   protected double baseCatchRate;
   protected int currentXp;
   protected int xpToNextLevel;
    private List<Move> moves;

    // No-argument constructor for serialization
    protected Pokemon() {
        this.moves = new ArrayList<>();
        this.status = "Normal";
        this.type = PokemonType.NORMAL; // Initialize type to a default value
    }

    public Pokemon(String name, PokemonType type, int level, double baseCatchRate,
                 int baseHp, int baseAttack, int baseDefense, int baseSpeed) {
        this.name = name;
        this.type = type;
        this.level = level;
        this.baseCatchRate = baseCatchRate;
        this.maxHp = baseHp + (level * 5);
        this.currentHp = this.maxHp;
        this.attack = baseAttack + (level * 2);
        this.defense = baseDefense + (level * 2);
        this.speed = baseSpeed + (level * 2);
        this.status = "Normal";
        this.moves = new ArrayList<>();
        this.currentXp = 0;
        this.xpToNextLevel = calculateXpToNextLevel(level);
    }

    public abstract void useSpecialAbility();
    public abstract void useSpecialAbility(Pokemon opponent);
    public void displayInfo() {
        System.out.println("Name: " + name);
        System.out.println("Type: " + type);
        System.out.println("Level: " + level);
    }

    public void displayDetailedInfo() {
        displayInfo();
        System.out.println("HP: " + currentHp + "/" + maxHp);
        System.out.println("Attack: " + attack);
        System.out.println("Defense: " + defense);
        System.out.println("Speed: " + speed);
    }

    public String getName() { return name; }
    public PokemonType getType() { return type; }
    public int getCurrentHp() { return currentHp; }
    public int getMaxHp() { return maxHp; }
    public int getLevel() { return level; }
    public int getAttack() { return attack; }
    public int getDefense() { return defense; }
    public int getSpeed() { return speed; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public void takeDamage(int damage) {
        currentHp = Math.max(0, currentHp - damage);
    }

    public void restoreHealth(int amount) {
        currentHp = Math.min(maxHp, currentHp + amount);
    }

    public void addMove(Move move) {
        this.moves.add(move);
    }

    public List<Move> getMoves() {
        return this.moves;
    }

    public boolean isDefeated() {
        return currentHp <= 0;
    }

    public double getCatchRate() {
        double hpFactor = 1.0 - (double) currentHp / maxHp;
        return Math.min(1.0, baseCatchRate * (0.5 + hpFactor));
    }

    public void gainXp(int xp) {
        this.currentXp += xp;
        System.out.println(this.name + " gained " + xp + " XP!");
        while (this.currentXp >= this.xpToNextLevel) {
            levelUp();
        }
    }

    private void levelUp() {
        this.level++;
        this.currentXp -= this.xpToNextLevel;
        this.xpToNextLevel = calculateXpToNextLevel(this.level);
        this.maxHp += 5; // Increase stats on level up
        this.currentHp = this.maxHp; // Fully heal on level up
        this.attack += 2;
        this.defense += 2;
        this.speed += 2;
        System.out.println("\u001b[32m" + this.name + " leveled up to Level " + this.level + "!\u001b[0m");
    }

    private int calculateXpToNextLevel(int currentLevel) {
        return 100 + (currentLevel * 20); // Simple XP curve
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (type == null) {
            type = PokemonType.NORMAL; // Default to NORMAL if type is null (for old save files)
        }
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }
}