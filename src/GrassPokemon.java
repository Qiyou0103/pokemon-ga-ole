import java.io.Serializable;

public class GrassPokemon extends Pokemon implements Serializable {
    private static final long serialVersionUID = 1L;
    public GrassPokemon() {
        // No-argument constructor for serialization
    }

    public GrassPokemon(String name, int level, double baseCatchRate,int baseHp, int baseAttack, int baseDefense, int baseSpeed) {
        super(name, PokemonType.GRASS, level, baseCatchRate, baseHp, baseAttack, baseDefense, baseSpeed);
    }
    @Override
    public void useSpecialAbility() {
        System.out.println(getName() + " uses Solar Beam!");
    }

    @Override
    public void useSpecialAbility(Pokemon opponent) {
        System.out.println(getName() + " uses Solar Beam on " + opponent.getName() + "!");
        // Add damage calculation logic here if needed
    }
}