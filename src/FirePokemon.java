import java.io.Serializable;

public class FirePokemon extends Pokemon implements Serializable {
    private static final long serialVersionUID = 1L;
    public FirePokemon() {
        // No-argument constructor for serialization
    }

    public FirePokemon(String name, int level, double baseCatchRate,int baseHp, int baseAttack, int baseDefense, int baseSpeed) {
        super(name, PokemonType.FIRE, level, baseCatchRate, baseHp, baseAttack, baseDefense, baseSpeed);
    }
    @Override
    public void useSpecialAbility() {
        System.out.println(getName() + " uses Fire Blast!");
    }

    @Override
    public void useSpecialAbility(Pokemon opponent) {
        System.out.println(getName() + " uses Fire Blast on " + opponent.getName() + "!");
        // Add damage calculation logic here if needed
    }
}