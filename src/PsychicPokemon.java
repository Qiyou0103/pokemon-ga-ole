import java.io.Serializable;

public class PsychicPokemon extends Pokemon implements Serializable {
    private static final long serialVersionUID = 1L;
    public PsychicPokemon() {
        // No-argument constructor for serialization
    }

    public PsychicPokemon(String name, int level, double baseCatchRate,int baseHp, int baseAttack, int baseDefense, int baseSpeed) {
        super(name, PokemonType.PSYCHIC, level, baseCatchRate, baseHp, baseAttack, baseDefense, baseSpeed);
    }
    @Override
    public void useSpecialAbility() {
        System.out.println(getName() + " uses Psychic!");
    }

    @Override
    public void useSpecialAbility(Pokemon opponent) {
        System.out.println(getName() + " uses Psychic on " + opponent.getName() + "!");
        // Add damage calculation logic here if needed
    }
}