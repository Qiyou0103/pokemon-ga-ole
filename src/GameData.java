import java.io.Serializable;

public class GameData implements Serializable {
    private static final long serialVersionUID = 1L;
    final Player player;
    final int coins;
    final String currentLocation;

    public GameData(Player player, int coins, String currentLocation) {
        this.player = player;
        this.coins = coins;
        this.currentLocation = currentLocation;
    }
}