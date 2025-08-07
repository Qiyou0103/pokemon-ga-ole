import java.io.Serializable;

public class FairyPokemon extends Pokemon implements Serializable {
    private static final long serialVersionUID = 1L;
    public FairyPokemon() {
    }

    public FairyPokemon(String name, int level, double baseCatchRate,int baseHp, int baseAttack, int baseDefense, int baseSpeed) {
        super(name, PokemonType.FAIRY, level, baseCatchRate, baseHp, baseAttack, baseDefense, baseSpeed);
    }
    @Override
    public void useSpecialAbility() {
        System.out.println(getName() + " uses Moonblast!");
    }

    @Override
    public void useSpecialAbility(Pokemon opponent) {
        System.out.println(getName() + " uses Moonblast on " + opponent.getName() + "!");
        
    }
}