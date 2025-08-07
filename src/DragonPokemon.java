import java.io.Serializable;

public class DragonPokemon extends Pokemon implements Serializable {
    private static final long serialVersionUID = 1L;
    public DragonPokemon() {
    }

    public DragonPokemon(String name, int level, double baseCatchRate,int baseHp, int baseAttack, int baseDefense, int baseSpeed) {
        super(name, PokemonType.DRAGON, level, baseCatchRate, baseHp, baseAttack, baseDefense, baseSpeed);
    }
    @Override
    public void useSpecialAbility() {
        System.out.println(getName() + " uses Draco Meteor!");
    }

    @Override
    public void useSpecialAbility(Pokemon opponent) {
        System.out.println(getName() + " uses Draco Meteor on " + opponent.getName() + "!");
        
    }
}