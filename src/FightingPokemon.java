import java.io.Serializable;

public class FightingPokemon extends Pokemon implements Serializable {
    private static final long serialVersionUID = 1L;
    public FightingPokemon() {
        // No-argument constructor for serialization
    }

    public FightingPokemon(String name, int level, double baseCatchRate,int baseHp, int baseAttack, int baseDefense, int baseSpeed) {
        super(name, PokemonType.FIGHTING, level, baseCatchRate, baseHp, baseAttack, baseDefense, baseSpeed);
    }
    @Override
    public void useSpecialAbility() {
        System.out.println(getName() + " uses Close Combat!");
    }

    @Override
    public void useSpecialAbility(Pokemon opponent) {
        System.out.println(getName() + " uses Close Combat on " + opponent.getName() + "!");
        // Add damage calculation logic here if needed
    }
}