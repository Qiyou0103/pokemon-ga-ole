import java.io.Serializable;

public class GhostPokemon extends Pokemon implements Serializable {
    private static final long serialVersionUID = 1L;
    public GhostPokemon() {
    }

    public GhostPokemon(String name, int level, double baseCatchRate,int baseHp, int baseAttack, int baseDefense, int baseSpeed) {
        super(name, PokemonType.GHOST, level, baseCatchRate, baseHp, baseAttack, baseDefense, baseSpeed);
    }
    @Override
    public void useSpecialAbility() {
        System.out.println(getName() + " uses Shadow Ball!");
    }

    @Override
    public void useSpecialAbility(Pokemon opponent) {
        System.out.println(getName() + " uses Shadow Ball on " + opponent.getName() + "!");
        
    }
}