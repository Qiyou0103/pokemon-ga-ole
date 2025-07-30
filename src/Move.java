public class Move implements java.io.Serializable {
    private String name;
    private int power;
    private PokemonType type;

    public Move(String name, int power, PokemonType type) {
        this.name = name;
        this.power = power;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public int getPower() {
        return power;
    }

    public PokemonType getType() {
        return type;
    }
}