import java.io.Serializable;

public class ElectricPokemon extends Pokemon implements Serializable {
    private static final long serialVersionUID = 1L;
    public ElectricPokemon() {
    }

    public ElectricPokemon(String name, int level, double baseCatchRate,int baseHp, int baseAttack, int baseDefense, int baseSpeed) {
        super(name, PokemonType.ELECTRIC, level, baseCatchRate, baseHp, baseAttack, baseDefense, baseSpeed);
    }
    @Override
    public void useSpecialAbility() {
        System.out.println(getName() + " uses Thunderbolt!");
    }

    @Override
    public void useSpecialAbility(Pokemon opponent) {
        System.out.println(getName() + " uses Thunderbolt on " + opponent.getName() + "!");
        
    }
}