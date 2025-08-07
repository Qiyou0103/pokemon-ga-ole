import java.io.Serializable;

public class WaterPokemon extends Pokemon implements Serializable {
    private static final long serialVersionUID = 1L;
    public WaterPokemon() {
        // No-argument constructor for serialization
    }

    public WaterPokemon(String name, int level, double baseCatchRate,int baseHp, int baseAttack, int baseDefense, int baseSpeed) {
        super(name, PokemonType.WATER, level, baseCatchRate, baseHp, baseAttack, baseDefense, baseSpeed);
    }
    @Override
    public void useSpecialAbility() {
        System.out.println(getName() + " uses Hydro Pump!");
    }

    @Override
    public void useSpecialAbility(Pokemon opponent) {
        System.out.println(getName() + " uses Hydro Pump on " + opponent.getName() + "!");
        
    }
}