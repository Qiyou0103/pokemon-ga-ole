public class PokeBall implements java.io.Serializable {
    private final String name;
    private final double catchModifier;

    public PokeBall(String name, double catchModifier) {
        this.name = name;
        this.catchModifier = catchModifier;
    }

    public String getName() {
        return name;
    }

    public double getCatchModifier() {
        return catchModifier;
    }
}