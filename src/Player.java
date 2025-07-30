import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String name;
    private final ArrayList<Pokemon> party;

    public Player() {
        this.name = ""; // Default or placeholder name
        this.party = new ArrayList<>();
    }

    public Player(String name) {
        this.name = name;
        this.party = new ArrayList<>();
    }

    public void addPokemon(Pokemon pokemon, java.util.Scanner scanner) {
        if (party.size() < 6) {
            party.add(pokemon);
        } else {
            System.out.println("Party is full! Would you like to send to PC or replace a Pokemon?");
            System.out.println("1. Send to PC");
            System.out.println("2. Replace a Pokemon");
            System.out.print("Choose: ");
            int choice = PokemonGame.getIntInput(1, 2, scanner);
            if (choice == 1) {
                System.out.println(pokemon.getName() + " sent to PC!");
            } else {
                System.out.println("Select Pokemon to replace:");
                for (int i = 0; i < party.size(); i++) {
                    System.out.println((i + 1) + ". " + party.get(i).getName());
                }
                int replaceChoice = PokemonGame.getIntInput(1, party.size(), scanner);
                Pokemon replaced = party.set(replaceChoice - 1, pokemon);
                System.out.println(replaced.getName() + " was replaced with " + pokemon.getName());
            }
        }
    }

    public boolean removePokemon(Pokemon pokemon) {
        return party.remove(pokemon);
    }

    public ArrayList<Pokemon> getParty() {
        return party;
    }

    public String getName() {
        return name;
    }
}