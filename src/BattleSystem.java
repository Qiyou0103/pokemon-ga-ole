
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class BattleSystem {
   private final List<Pokemon> playerTeam;
   private final List<Pokemon> opponentTeam;
   private int turnCount;
   private boolean battleEnded;
   private boolean playerVictorious;
   private boolean doubleRushActive;
   private int doubleRushTurns;
   private boolean rushComboActive;
   private int rushComboCount;
   private final Random random = new Random();
   private final Scanner scanner;
   private Pokemon playerActivePokemon;
   private Player player;

   public BattleSystem(List<Pokemon> var1, List<Pokemon> var2, Scanner scanner, Player player) {
      this.scanner = scanner;
      this.player = player;
      this.playerTeam = new ArrayList(var1);
      this.opponentTeam = new ArrayList(var2);
      this.turnCount = 0;
      this.battleEnded = false;
      this.playerVictorious = false;
      this.doubleRushActive = false;
      this.doubleRushTurns = 0;
      this.rushComboActive = false;
      this.rushComboCount = 0;
   }

   public void startBattle() {
      System.out.println("\n" + "=".repeat(40));
      System.out.println("\u001b[31m\u001b[1mBATTLE STARTED! \u001b[0m");
      System.out.println("=".repeat(40));
      System.out.println("\n--- Initializing Battle ---");
      System.out.println("Your Team:");
      this.playerTeam.forEach(pokemon -> {
         System.out.println("- " + pokemon.getName() + " (Lvl " + pokemon.getLevel() + ") HP: " + pokemon.getCurrentHp() + "/" + pokemon.getMaxHp() + " Type: " + pokemon.getType());
      });
      System.out.println("\nOpponent Team:");
      this.opponentTeam.forEach(pokemon -> {
         System.out.println("- " + pokemon.getName() + " (Lvl " + pokemon.getLevel() + ") HP: " + pokemon.getCurrentHp() + "/" + pokemon.getMaxHp() + " Type: " + pokemon.getType());
      });
      System.out.println("---------------------------");

      for(; !this.battleEnded && !this.isTeamDefeated(this.playerTeam) && !this.isTeamDefeated(this.opponentTeam); this.updateBattleEffects()) {
         ++this.turnCount;
         int var10001 = this.turnCount;
         System.out.println("\n--- Turn " + var10001 + " ---");
         this.checkBattleEvents();
         System.out.println("\nSpecial Abilities:");
         this.playerTeam.forEach((var0) -> {
            System.out.print("Your " + var0.getName() + ": ");
            var0.useSpecialAbility();
         });
         this.opponentTeam.forEach((var0) -> {
            System.out.print("Wild " + var0.getName() + ": ");
            var0.useSpecialAbility();
         });
         this.playerActivePokemon = this.playerTeam.get(0);
         Pokemon opponentActivePokemon = this.opponentTeam.get(0);

         System.out.println("\n--- Current HP ---");
         System.out.println(playerActivePokemon.getName() + ": " + this.getHpBar(playerActivePokemon) + " (Status: " + playerActivePokemon.getStatus() + ")");
         System.out.println(opponentActivePokemon.getName() + ": " + this.getHpBar(opponentActivePokemon) + " (Status: " + opponentActivePokemon.getStatus() + ")");
         System.out.println("------------------");

         boolean playerGoesFirst = playerActivePokemon.getSpeed() >= opponentActivePokemon.getSpeed();
         if (playerGoesFirst) {
            if (this.applyStatusEffects(playerActivePokemon)) {
                this.playerTurn(playerActivePokemon, opponentActivePokemon);
            }
            if (!this.battleEnded && !opponentActivePokemon.isDefeated()) {
                if (this.applyStatusEffects(opponentActivePokemon)) {
                    this.opponentTurn(opponentActivePokemon, playerActivePokemon);
                }
            }
         } else {
            if (this.applyStatusEffects(opponentActivePokemon)) {
                this.opponentTurn(opponentActivePokemon, playerActivePokemon);
            }
            if (!this.battleEnded && !playerActivePokemon.isDefeated()) {
                if (this.applyStatusEffects(playerActivePokemon)) {
                    this.playerTurn(playerActivePokemon, opponentActivePokemon);
                }
            }
         }
      }

      this.concludeBattle();
   }

   private void checkBattleEvents() {
      if (!this.doubleRushActive && this.random.nextDouble() < 0.2) {
         this.doubleRushActive = true;
         this.doubleRushTurns = 3;
         System.out.println("\n\u001b[33mDOUBLE RUSH ACTIVATED! Attacks do double damage for 3 turns!\u001b[0m");
         if (this.performQTE("Double Rush QTE! Press 'A' 5 times quickly!", "A", 5, 3000)) {
            System.out.println("Double Rush QTE successful! Increased bonus!");
            this.doubleRushTurns = 5; // Extend duration for successful QTE
         } else {
            System.out.println("Double Rush QTE failed. Standard bonus.");
         }
      }

      if (this.doubleRushActive && !this.rushComboActive && this.random.nextDouble() < 0.3) {
         this.rushComboActive = true;
         this.rushComboCount = 0;
         System.out.println("\n\u001b[36mRUSH COMBO ACTIVATED! Consecutive attacks increase damage!\u001b[0m");
         if (this.performQTE("Rush Combo QTE! Press 'B' 3 times quickly!", "B", 3, 2000)) {
            System.out.println("Rush Combo QTE successful! Increased starting combo!");
            this.rushComboCount = 1; // Start with 1 combo for successful QTE
         } else {
            System.out.println("Rush Combo QTE failed. Standard combo.");
         }
      }

   }

   private boolean performQTE(String message, String key, int repetitions, long timeLimit) {
      if (System.console() == null) {
         // If running in a non-interactive environment, auto-succeed QTE
         return true;
      }

      System.out.println("\n" + message);
      System.out.println("Prepare...");
      try {
         Thread.sleep(1000);
      } catch (InterruptedException var10) {
         Thread.currentThread().interrupt();
      }

      long startTime = System.currentTimeMillis();
      int successfulPresses = 0;

      while(System.currentTimeMillis() - startTime < timeLimit && successfulPresses < repetitions) {
         System.out.print("Press " + key + "! ");
         long inputStartTime = System.currentTimeMillis();
         String input = this.scanner.nextLine().trim();
         if (input.equalsIgnoreCase(key)) {
            ++successfulPresses;
            System.out.println("Good!");
         } else {
            System.out.println("Miss!");
         }
      }

      return successfulPresses >= repetitions;
   }

   private void updateBattleEffects() {
      if (this.doubleRushActive) {
         --this.doubleRushTurns;
         if (this.doubleRushTurns <= 0) {
            this.doubleRushActive = false;
            System.out.println("\nDouble Rush has ended.");
            if (this.rushComboActive) {
               this.rushComboActive = false;
               this.rushComboCount = 0;
               System.out.println("Rush Combo has ended.");
            }
         }
      }
   }

   private boolean applyStatusEffects(Pokemon pokemon) {
      if (pokemon.getStatus().equals("Poisoned")) {
         int poisonDamage = pokemon.getMaxHp() / 10;
         pokemon.takeDamage(poisonDamage);
         System.out.println(pokemon.getName() + " is hurt by poison! (" + poisonDamage + " damage)");
         if (pokemon.isDefeated()) {
            System.out.println(pokemon.getName() + " fainted from poison!");
            return false;
         }
      } else if (pokemon.getStatus().equals("Paralyzed")) {
         if (this.random.nextDouble() < 0.25) { // 25% chance to be fully paralyzed
            System.out.println(pokemon.getName() + " is fully paralyzed and can't move!");
            return false;
         }
      }
      return true;
   }

   private void playerTurn(Pokemon playerPokemon, Pokemon opponentPokemon) {
      System.out.println("\n--- Your Turn: " + playerPokemon.getName() + " ---");
      System.out.println("1. Attack");
      System.out.println("2. Use Pokeball");
      System.out.println("3. Use Trainer Skill");
      System.out.println("4. Flee");
      System.out.print("Choose action: ");
      int choice = PokemonGame.getIntInput(1, 4, this.scanner);
      if (choice == 1) {
         System.out.println("Choose a move:");
         for (int i = 0; i < playerPokemon.getMoves().size(); i++) {
             System.out.println((i + 1) + ". " + playerPokemon.getMoves().get(i).getName());
         }
         int moveChoice = PokemonGame.getIntInput(1, playerPokemon.getMoves().size(), this.scanner);
         Move chosenMove = playerPokemon.getMoves().get(moveChoice - 1);
         System.out.println(playerPokemon.getName() + " prepares to use " + chosenMove.getName() + "!");
         this.attackSequence(playerPokemon, opponentPokemon, chosenMove, true);
         if (this.rushComboActive) {
            ++this.rushComboCount;
         }
      } else if (choice == 2) {
         System.out.println("Attempting to catch " + opponentPokemon.getName() + "...");
         this.attemptCatch(opponentPokemon);
         this.rushComboCount = 0;
      } else if (choice == 3) {
         this.useTrainerSkill(playerPokemon, opponentPokemon);
         this.rushComboCount = 0;
      } else {
         System.out.println("You attempt to flee from battle!");
         this.battleEnded = true;
         this.rushComboCount = 0;
      }
   }

   private void opponentTurn(Pokemon opponentPokemon, Pokemon playerPokemon) {
      System.out.println("\n--- Wild Pokemon's Turn: " + opponentPokemon.getName() + " ---");
      List<Move> superEffectiveMoves = new ArrayList<>();
      for (Move move : opponentPokemon.getMoves()) {
          if (this.getTypeMultiplier(move.getType(), playerPokemon.getType()) > 1.0) {
              superEffectiveMoves.add(move);
          }
      }

      Move chosenMove;
      if (!superEffectiveMoves.isEmpty()) {
          chosenMove = superEffectiveMoves.get(this.random.nextInt(superEffectiveMoves.size()));
      } else {
          chosenMove = opponentPokemon.getMoves().get(this.random.nextInt(opponentPokemon.getMoves().size()));
      }
      System.out.println(opponentPokemon.getName() + " prepares to use " + chosenMove.getName() + "!");
      this.attackSequence(opponentPokemon, playerPokemon, chosenMove, false);
   }

   private void attackSequence(Pokemon attacker, Pokemon defender, Move move, boolean isPlayer) {
      int baseDamage = move.getPower() + attacker.getLevel() * 2;
      int damage = this.random.nextInt(baseDamage / 2) + baseDamage / 2;
      if (this.doubleRushActive) {
         damage *= 2;
         System.out.println("Double Rush boosts the attack!");
      }

      if (this.rushComboActive && isPlayer) {
         double comboMultiplier = 1.0 + 0.1 * (double)this.rushComboCount;
         damage = (int)((double)damage * comboMultiplier);
         System.out.println("Rush Combo x" + this.rushComboCount + " boosts damage by " + (int)((comboMultiplier - 1.0) * 100.0) + "%!");
      }

      double typeMultiplier = this.getTypeMultiplier(move.getType(), defender.getType());
      damage = (int)((double)damage * typeMultiplier);
      if (typeMultiplier > 1.0) {
         System.out.println("It's super effective!");
      } else if (typeMultiplier < 1.0) {
         System.out.println("It's not very effective...");
      }

      System.out.println(attacker.getName() + " used " + move.getName() + "!");
      System.out.println("\u001b[31m" + attacker.getName() + " attacks!\u001b[0m");
      System.out.println(defender.getName() + " took " + damage + " damage.");
      defender.takeDamage(damage);
      if (defender.isDefeated()) {
         System.out.println("\u001b[31m" + defender.getName() + " fainted!\u001b[0m");
      } else {
         System.out.println(defender.getName() + " HP: " + this.getHpBar(defender));
      }
   }

   private static final Map<PokemonType, Map<PokemonType, Double>> TYPE_EFFECTIVENESS;

   static {
      TYPE_EFFECTIVENESS = new java.util.EnumMap<>(PokemonType.class);

      // Initialize all types with a default effectiveness map
      for (PokemonType attackerType : PokemonType.values()) {
         TYPE_EFFECTIVENESS.put(attackerType, new java.util.EnumMap<>(PokemonType.class));
         for (PokemonType defenderType : PokemonType.values()) {
            TYPE_EFFECTIVENESS.get(attackerType).put(defenderType, 1.0); // Default to normal effectiveness
         }
      }

      // Super Effective (1.5x)
      setEffectiveness(PokemonType.FIRE, PokemonType.GRASS, 1.5);
      setEffectiveness(PokemonType.FIRE, PokemonType.BUG, 1.5);
      setEffectiveness(PokemonType.FIRE, PokemonType.ICE, 1.5);
      setEffectiveness(PokemonType.FIRE, PokemonType.STEEL, 1.5);

      setEffectiveness(PokemonType.WATER, PokemonType.FIRE, 1.5);
      setEffectiveness(PokemonType.WATER, PokemonType.ROCK, 1.5);
      setEffectiveness(PokemonType.WATER, PokemonType.GROUND, 1.5);

      setEffectiveness(PokemonType.GRASS, PokemonType.WATER, 1.5);
      setEffectiveness(PokemonType.GRASS, PokemonType.ROCK, 1.5);
      setEffectiveness(PokemonType.GRASS, PokemonType.GROUND, 1.5);

      setEffectiveness(PokemonType.ELECTRIC, PokemonType.WATER, 1.5);
      setEffectiveness(PokemonType.ELECTRIC, PokemonType.FLYING, 1.5);

      setEffectiveness(PokemonType.GHOST, PokemonType.GHOST, 1.5);
      setEffectiveness(PokemonType.GHOST, PokemonType.PSYCHIC, 1.5);

      setEffectiveness(PokemonType.FIGHTING, PokemonType.NORMAL, 1.5);
      setEffectiveness(PokemonType.FIGHTING, PokemonType.ROCK, 1.5);
      setEffectiveness(PokemonType.FIGHTING, PokemonType.ICE, 1.5);
      setEffectiveness(PokemonType.FIGHTING, PokemonType.DARK, 1.5);

      setEffectiveness(PokemonType.PSYCHIC, PokemonType.FIGHTING, 1.5);
      setEffectiveness(PokemonType.PSYCHIC, PokemonType.POISON, 1.5);

      setEffectiveness(PokemonType.DRAGON, PokemonType.DRAGON, 1.5);

      setEffectiveness(PokemonType.FAIRY, PokemonType.FIGHTING, 1.5);
      setEffectiveness(PokemonType.FAIRY, PokemonType.DRAGON, 1.5);
      setEffectiveness(PokemonType.FAIRY, PokemonType.DARK, 1.5);

      // Not Very Effective (0.5x)
      setEffectiveness(PokemonType.FIRE, PokemonType.WATER, 0.5);
      setEffectiveness(PokemonType.FIRE, PokemonType.ROCK, 0.5);
      setEffectiveness(PokemonType.FIRE, PokemonType.GROUND, 0.5);

      setEffectiveness(PokemonType.WATER, PokemonType.GRASS, 0.5);
      setEffectiveness(PokemonType.WATER, PokemonType.ELECTRIC, 0.5);

      setEffectiveness(PokemonType.GRASS, PokemonType.FIRE, 0.5);
      setEffectiveness(PokemonType.GRASS, PokemonType.FLYING, 0.5);
      setEffectiveness(PokemonType.GRASS, PokemonType.POISON, 0.5);
      setEffectiveness(PokemonType.GRASS, PokemonType.BUG, 0.5);
      setEffectiveness(PokemonType.GRASS, PokemonType.ICE, 0.5);

      setEffectiveness(PokemonType.ELECTRIC, PokemonType.GROUND, 0.5);

      setEffectiveness(PokemonType.GHOST, PokemonType.GHOST, 0.5); // Ghost is weak to Ghost
      setEffectiveness(PokemonType.GHOST, PokemonType.DARK, 0.5);

      setEffectiveness(PokemonType.FIGHTING, PokemonType.FLYING, 0.5);
      setEffectiveness(PokemonType.FIGHTING, PokemonType.PSYCHIC, 0.5);
      setEffectiveness(PokemonType.FIGHTING, PokemonType.FAIRY, 0.5);

      setEffectiveness(PokemonType.PSYCHIC, PokemonType.BUG, 0.5);
      setEffectiveness(PokemonType.PSYCHIC, PokemonType.GHOST, 0.5);
      setEffectiveness(PokemonType.PSYCHIC, PokemonType.DARK, 0.5);

      setEffectiveness(PokemonType.DRAGON, PokemonType.ICE, 0.5);
      setEffectiveness(PokemonType.DRAGON, PokemonType.DRAGON, 0.5); // Dragon is weak to Dragon
      setEffectiveness(PokemonType.DRAGON, PokemonType.FAIRY, 0.5);

      setEffectiveness(PokemonType.FAIRY, PokemonType.POISON, 0.5);
      setEffectiveness(PokemonType.FAIRY, PokemonType.STEEL, 0.5);
   }

   private static void setEffectiveness(PokemonType attacker, PokemonType defender, double multiplier) {
      TYPE_EFFECTIVENESS.get(attacker).put(defender, multiplier);
   }

   private double getTypeMultiplier(PokemonType attackerType, PokemonType defenderType) {
      return TYPE_EFFECTIVENESS.getOrDefault(attackerType, java.util.Collections.emptyMap())
                               .getOrDefault(defenderType, 1.0);
   }

   private void attemptCatch(Pokemon var1) {
      PokeBall[] var2 = new PokeBall[]{new PokeBall("Poke Ball", 1.0), new PokeBall("Great Ball", 1.5), new PokeBall("Ultra Ball", 2.0), new PokeBall("Master Ball", 2.5)};
      System.out.println("\nChoose a Poke Ball:");

      int var3;
      for(var3 = 0; var3 < var2.length; ++var3) {
         System.out.println(var3 + 1 + ". " + var2[var3].getName());
      }

      var3 = PokemonGame.getIntInput(1, var2.length, this.scanner);
      PokeBall var4 = var2[var3 - 1];
      if (CatchSystem.tryCatch(var1, var4, this.scanner)) {
         this.player.addPokemon(var1, this.scanner);
         this.battleEnded = true;
         this.playerVictorious = true;
      }

   }

   private boolean isTeamDefeated(List<Pokemon> var1) {
      return var1.stream().allMatch(Pokemon::isDefeated);
   }

   private void concludeBattle() {
      System.out.println("\n" + "=".repeat(40));
      System.out.println("\u001b[31m\u001b[1mBATTLE ENDED \u001b[0m");
      if (this.isTeamDefeated(this.playerTeam)) {
         System.out.println("You lost the battle!");
      } else if (this.isTeamDefeated(this.opponentTeam)) {
         System.out.println("You won the battle!");
         this.playerVictorious = true;
         // Award XP to the player's active Pokemon
         int xpEarned = 50 + this.opponentTeam.get(0).getLevel() * 5; // Example XP calculation
         this.playerActivePokemon.gainXp(xpEarned);
      }

      System.out.println("Battle lasted " + this.turnCount + " turns");
      System.out.println("=".repeat(40));
   }

   public boolean isPlayerVictorious() {
      return this.playerVictorious;
   }

   private String getHpBar(Pokemon pokemon) {
      int barLength = 20; // Length of the HP bar
      double hpPercentage = (double) pokemon.getCurrentHp() / pokemon.getMaxHp();
      int filledLength = (int) (barLength * hpPercentage);
      String bar = "[" + "#".repeat(filledLength) + "-".repeat(barLength - filledLength) + "]";
      return bar + " " + pokemon.getCurrentHp() + "/" + pokemon.getMaxHp();
   }

   public int getBattleScore() {
      return this.playerVictorious ? 100 + 10 * this.playerTeam.size() : 0;
   }

   private void useTrainerSkill(Pokemon playerPokemon, Pokemon opponentPokemon) {
      System.out.println("\n--- Choose a Trainer Skill ---");
      System.out.println("1. Heal (Restore 30 HP to your active Pokemon)");
      System.out.println("2. Boost Attack (Increase your active Pokemon's attack for this turn)");
      System.out.print("Choose skill: ");

      int skillChoice = PokemonGame.getIntInput(1, 2, this.scanner);
      switch (skillChoice) {
         case 1:
            playerPokemon.restoreHealth(30);
            System.out.println(playerPokemon.getName() + " restored 30 HP!");
            break;
         case 2:
            System.out.println(playerPokemon.getName() + "'s attack is boosted for this turn!");
            // This boost would ideally be applied before damage calculation in attackSequence
            // For simplicity, we'll just print a message for now.
            break;
      }
   }
}
