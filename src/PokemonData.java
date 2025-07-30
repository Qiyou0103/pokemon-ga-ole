public class PokemonData implements java.io.Serializable {
    PokemonType type;
    int level;
    double catchRate;
    int baseHp;
    int baseAttack;
    int baseDefense;
    int baseSpeed;

    public PokemonData(PokemonType type, int level, double catchRate,int baseHp, int baseAttack, int baseDefense, int baseSpeed) {
        this.type = type;
        this.level = level;
        this.catchRate = catchRate;
        this.baseHp = baseHp;
        this.baseAttack = baseAttack;
        this.baseDefense = baseDefense;
        this.baseSpeed = baseSpeed;
    }
}