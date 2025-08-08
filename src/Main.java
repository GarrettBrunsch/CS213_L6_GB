// Garrett Brunsch
// Lab #6 - Inheritance
// Original Due 7/27/25 - Modifications due 8/10

import java.util.Scanner;
import java.util.Random;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main
{
    enum MenuOptions
    {
        INVALID, BATTLE, QUIT
    }

    private static final String[] CREATURE_NAMES = {
            "Aragorn", "Legolas", "Gimli", "Gandalf", "Frodo", "Sam", "Merry", "Pippin",
            "Boromir", "Faramir", "Eowyn", "Theoden", "Arwen", "Elrond", "Galadriel",
            "Thorin", "Balin", "Dwalin", "Fili", "Kili", "Bofur", "Bifur", "Bombur",
            "Ori", "Nori", "Dori", "Oin", "Gloin", "Radagast", "Saruman"
    };

    static boolean[] usedNames = new boolean[CREATURE_NAMES.length];

    public static void main(String[] args) throws IOException
    {
        Scanner scanner = new Scanner(System.in);
        Game game = new Game();

        int choice = 0;
        MenuOptions menuChoice = MenuOptions.INVALID;

        while (menuChoice != MenuOptions.QUIT)
        {
            displayMenu();
            choice = getUserChoice(scanner);

            choice = (choice >= MenuOptions.BATTLE.ordinal() && choice <= MenuOptions.QUIT.ordinal()) ? choice : 0;
            menuChoice = MenuOptions.values()[choice];

            switch (menuChoice)
            {
                case BATTLE:
                    game.playGame(scanner);
                    break;
                case QUIT:
                    System.out.println("Now exiting program...");
                    break;
                default:
                    System.out.println("Invalid choice. Please select a valid menu option");
                    break;
            }
        }
        scanner.close();
    }

    public static void displayMenu()
    {
        System.out.print("\n\n=== CREATURE BATTLE SYSTEM ===\n" +
                "1. Battle\n" +
                "2. Quit\n" +
                "Choice: ");
    }

    public static int getUserChoice(Scanner scanner)
    {
        String input = scanner.nextLine().trim();
        int choice = 0;

        try
        {
            if (input != null && !input.isEmpty())
            {
                choice = Integer.parseInt(input);
            }
        }
        catch (NumberFormatException e)
        {
            System.out.print("Incorrect format detected - ");
            choice = 0;
        }
        return choice;
    }

    public static void resetUsedNames()
    {
        for (int i = 0; i < usedNames.length; i++)
        {
            usedNames[i] = false;
        }
    }

    public static String getUniqueName()
    {
        int nameIndex;
        do
        {
            nameIndex = Constants.rand.nextInt(CREATURE_NAMES.length);
        } while (usedNames[nameIndex]);

        usedNames[nameIndex] = true;
        return CREATURE_NAMES[nameIndex];
    }
}

final class Constants
{
    // General Defaults
    public static final Random rand = new Random();
    public static final int MAX_TEST_CASES = 10;

    public static final String OUTPUT_FILE = "src/out_battle_results.txt";

    // Creature Defaults
    public static final int MIN_STRENGTH = 50;
    public static final int MAX_STRENGTH = 197;

    public static final int MIN_HEALTH = 50;
    public static final int MAX_HEALTH = 197;

    public static final int MIN_NAME_LENGTH = 3;
    public static final String DEFAULT_NAME = "Unknown";

    // Special attack defaults
    public static final int BAHAMUT_BONUS_DAMAGE = 25;
    public static final int BAHAMUT_CHANCE = 10;
    public static final int MACARA_CHANCE = 20;

    // Army Defaults
    public static final int MIN_ARMY_SIZE = 1;
    public static final int MAX_ARMY_SIZE = 15;
    public static final int DEFAULT_ARMY_SIZE = MIN_ARMY_SIZE;
    public static final String ARMY_1_NAME = "Army #1";
    public static final String ARMY_2_NAME = "Army #2";
}

class Game
{
    private StringBuilder battleLog;

    public Game()
    {
        battleLog = new StringBuilder();
    }

    private void printAndAppend(String message)
    {
        System.out.println(message);
        battleLog.append(message + "\n");
    }

    public void playGame(Scanner mainScanner) throws IOException
    {
        battleLog.setLength(0);
        int armySize = getArmySize(mainScanner);

        Main.resetUsedNames();

        Army army1 = new Army(Constants.ARMY_1_NAME, armySize);
        Army army2 = new Army(Constants.ARMY_2_NAME, armySize);

        printAndAppend("\n=== NEW BATTLE ===");
        printAndAppend("\nArmy Stats Before Battle:");
        printAndAppend(army1.toString());
        printAndAppend(army2.toString());

        conductBattle(army1, army2);

        printAndAppend("\nArmy Stats After Battle:");
        printAndAppend(army1.toString());
        printAndAppend(army2.toString());

        announceWinner(army1, army2);

        writeAllToFile();

        System.out.println("\nBattle results written to file");
    }

    private int getArmySize(Scanner mainScanner)
    {
        int size = 0;
        boolean validInput = false;

        while (!validInput)
        {
            System.out.print("Enter army size (1-15): ");
            String input = mainScanner.nextLine().trim();

            try
            {
                if (input != null && !input.isEmpty())
                {
                    size = Integer.parseInt(input);
                    if (size >= Constants.MIN_ARMY_SIZE && size <= Constants.MAX_ARMY_SIZE)
                    {
                        validInput = true;
                        System.out.println();
                    }
                    else
                    {
                        System.out.println("Army size must be between " + Constants.MIN_ARMY_SIZE + " and " + Constants.MAX_ARMY_SIZE);
                    }
                }
                else
                {
                    System.out.println("Please enter a valid number");
                }
            }
            catch (NumberFormatException e)
            {
                System.out.println("Please enter a valid number");
            }
        }

        return size;
    }

    private void conductBattle(Army army1, Army army2)
    {
        System.out.println("\n--- BATTLE BEGINS ---");
        battleLog.append("\n--- BATTLE LOG ---\n");

        int armySize = army1.getArmySize();

        for (int position = 0; position < armySize; position++)
        {
            Creature creature1 = army1.getCreature(position);
            Creature creature2 = army2.getCreature(position);

            if (creature1 != null && creature2 != null && creature1.getHealth() > 0 && creature2.getHealth() > 0)
            {
                printAndAppend("\nBattle " + (position + 1) + ": " + creature1.getName() +
                        " vs " + creature2.getName());

                String battleHeader = String.format("%-25s | %-6s | %-10s | %-25s | %-15s | %-10s",
                        "Attacker", "Damage", "Army", "Defender", "Defender Health", "Army");
                String separator = "\n" + "-".repeat(120);

                printAndAppend(battleHeader + separator);

                battleCreatures(creature1, creature2, army1.getArmyName(), army2.getArmyName());
            }
        }
    }

    private void battleCreatures(Creature creature1, Creature creature2, String army1Name, String army2Name)
    {
        int currentTurn = Constants.rand.nextInt(2);

        while (creature1.getHealth() > 0 && creature2.getHealth() > 0)
        {
            Creature attacker = (currentTurn == 0) ? creature1 : creature2;
            Creature defender = (currentTurn == 0) ? creature2 : creature1;
            String attackerArmy = (currentTurn == 0) ? army1Name : army2Name;
            String defenderArmy = (currentTurn == 0) ? army2Name : army1Name;

            if (attacker instanceof Superbahamut)
            {
                int damage1 = attacker.getDamage();
                defender.takeDamage(damage1);

                String battleLine1 = String.format("%-25s | %6d | %-10s | %-25s | %15d | %-10s",
                        attacker.getName(), damage1, attackerArmy,
                        defender.getName(), defender.getHealth(), defenderArmy);
                printAndAppend(battleLine1);

                if (defender.getHealth() > 0)
                {
                    int damage2 = attacker.getDamage();
                    defender.takeDamage(damage2);

                    String battleLine2 = String.format("%-25s | %6d | %-10s | %-25s | %15d | %-10s",
                            attacker.getName() + " (2nd)", damage2, attackerArmy,
                            defender.getName(), defender.getHealth(), defenderArmy);
                    printAndAppend(battleLine2);
                }
            }
            else
            {
                int damage = attacker.getDamage();
                defender.takeDamage(damage);

                String battleLine = String.format("%-25s | %6d | %-10s | %-25s | %15d | %-10s",
                        attacker.getName(), damage, attackerArmy,
                        defender.getName(), defender.getHealth(), defenderArmy);
                printAndAppend(battleLine);
            }

            currentTurn = (currentTurn + 1) % 2;
        }

        Creature winner = (creature1.getHealth() > 0) ? creature1 : creature2;
        printAndAppend("Winner: " + winner.getName());
    }

    private void announceWinner(Army army1, Army army2)
    {
        int army1Health = army1.getTotalHealth();
        int army2Health = army2.getTotalHealth();

        String winnerMessage = (army1Health > army2Health) ? army1.getArmyName() + " wins the war!" :
                (army2Health > army1Health) ? army2.getArmyName() + " wins the war!" :
                        "The war ends in a tie!";

        String finalResults = "\n\n=== FINAL RESULTS ===\n" +
                army1.getArmyName() + " total health: " + army1Health + "\n" +
                army2.getArmyName() + " total health: " + army2Health + "\n" +
                winnerMessage;

        printAndAppend(finalResults);
    }

    private void writeAllToFile() throws IOException
    {
        FileWriter writer = new FileWriter(Constants.OUTPUT_FILE, true);
        writer.write(battleLog.toString());
        writer.close();
    }
}

class InvalidCreatureParamException extends RuntimeException
{
    public InvalidCreatureParamException(String message)
    {
        super(message);
    }
}
class Creature
{
    protected String name;
    protected int health;
    protected int strength;

    public Creature()
    {
        try
        {
            setCreature(Constants.DEFAULT_NAME, Constants.MIN_HEALTH, Constants.MIN_STRENGTH);
        }
        catch (InvalidCreatureParamException e)
        {
            restoreDefaults();
            System.err.println("Error during default creature creation: " + e.getMessage());
        }
    }

    public Creature(String n_name, int n_health, int n_strength)
    {
        try
        {
            setCreature(n_name, n_health, n_strength);
        }
        catch (InvalidCreatureParamException e)
        {
            restoreDefaults();
            System.err.println("Invalid creature parameters: " + e.getMessage());
        }
    }

    // Thrown errors are caught and handled within constructors
    public void setCreature(String n_name, int n_health, int n_strength)
    {
        if (n_name == null || n_name.trim().length() < Constants.MIN_NAME_LENGTH)
        {
            throw new InvalidCreatureParamException("Creature name must be at least " + Constants.MIN_NAME_LENGTH +
                    " characters long and not null");
        }
        if (n_health < 0)
        {
            throw new InvalidCreatureParamException("Creature health must be > 0");
        }

        if (n_strength < Constants.MIN_STRENGTH || n_strength > Constants.MAX_STRENGTH)
        {
            throw new InvalidCreatureParamException("Creature strength must be between "+Constants.MIN_STRENGTH+
                    " and "+Constants.MAX_STRENGTH);
        }

        name = n_name.trim();
        health = n_health;
        strength = n_strength;
    }

    private void restoreDefaults()
    {
        name = Constants.DEFAULT_NAME;
        health = Constants.MIN_HEALTH;
        strength = Constants.MIN_STRENGTH;
    }

    public String getName()
    {
        return name + " the creature";
    }

    public int getHealth()
    {
        return health;
    }

    public int getStrength()
    {
        return strength;
    }

    public void takeDamage(int damage)
    {
        if ((health - damage) < 0)
        {
            health = 0;
        }
        else
        {
            health -= damage;
        }
    }

    public int getDamage()
    {
        return (Constants.rand.nextInt(strength) + 1);
    }

    public String toString()
    {
        return String.format("%-15s | %-15s | %10d | %10d", name, getCreatureType(), strength, health);
    }

    protected String getCreatureType()
    {
        return "creature";
    }
}

class Bahamut extends Creature
{
    public Bahamut()
    {
        super();
    }

    public Bahamut(String name, int health, int strength)
    {
        super(name, health, strength);
    }

    @Override
    public String getName()
    {
        return name + " the bahamut";
    }

    @Override
    public int getDamage()
    {
        int damage = super.getDamage();

        if ((Constants.rand.nextInt(100)) < Constants.BAHAMUT_CHANCE)
        {
            damage = damage + Constants.BAHAMUT_BONUS_DAMAGE;
        }
        return damage;
    }

    @Override
    protected String getCreatureType()
    {
        return "bahamut";
    }
}

class Cyberbahamut extends Bahamut
{
    public Cyberbahamut()
    {
        super();
    }

    public Cyberbahamut(String name, int health, int strength)
    {
        super(name, health, strength);
    }

    @Override
    public String getName()
    {
        return name + " the cyberbahamut";
    }

    @Override
    protected String getCreatureType()
    {
        return "cyberbahamut";
    }

}

class Superbahamut extends Bahamut
{
    public Superbahamut()
    {
        super();
    }

    public Superbahamut(String name, int health, int strength)
    {
        super(name, health, strength);
    }

    @Override
    public String getName()
    {
        return name + " the superbahamut";
    }

    @Override
    protected String getCreatureType()
    {
        return "superbahamut";
    }
}

class Macara extends Creature
{
    public Macara()
    {
        super();
    }

    public Macara(String name, int health, int strength)
    {
        super(name, health, strength);
    }

    @Override
    public String getName()
    {
        return name + " the macara";
    }

    @Override
    public int getDamage()
    {
        int damage = super.getDamage();

        if ((Constants.rand.nextInt(Constants.MACARA_CHANCE)) == 0)
        {
            damage = damage * 2;
        }
        return damage;
    }

    @Override
    protected String getCreatureType()
    {
        return "macara";
    }
}

class InvalidArmyParamException extends RuntimeException
{
    public InvalidArmyParamException(String message)
    {
        super(message);
    }
}

class Army
{
    private String armyName = Constants.DEFAULT_NAME;
    private int armySize = Constants.DEFAULT_ARMY_SIZE;
    private Creature[] creatures = new Creature[Constants.MAX_ARMY_SIZE];

    public Army()
    {
        try
        {
            setArmy(Constants.DEFAULT_NAME, Constants.DEFAULT_ARMY_SIZE);
            initializeCreatures();
        }
        catch (InvalidArmyParamException e)
        {
            restoreDefaults();
            System.err.println("Failed to create army with default parameters: " + e.getMessage());
        }
    }

    public Army(int size)
    {
        try
        {
            setArmy(Constants.DEFAULT_NAME, size);
            initializeCreatures();
        }
        catch (InvalidArmyParamException e)
        {
            restoreDefaults();
            System.err.println("Failed to create an army with size " + size + ": " + e.getMessage());
        }
    }

    public Army(String name)
    {
        try
        {
            setArmy(name, Constants.DEFAULT_ARMY_SIZE);
            initializeCreatures();
        }
        catch (InvalidArmyParamException e)
        {
            restoreDefaults();
            System.err.println("Failed to create an army named " + name + ": " + e.getMessage());
        }
    }

    public Army(String name, int size)
    {
        try
        {
            setArmy(name, size);
            initializeCreatures();
        }
        catch (InvalidArmyParamException e)
        {
            restoreDefaults();
            System.err.println("Failed to create an army named " + name + " of size " + size+ ": " + e.getMessage());
        }
    }

    private void restoreDefaults()
    {
        armyName = Constants.DEFAULT_NAME;
        armySize = Constants.DEFAULT_ARMY_SIZE;
        creatures = new Creature[armySize];
    }

    // Throws propagate to/ are caught and handled within constructors
    public void setArmy(String name, int size)
    {
        if (name == null || name.trim().isEmpty())
        {
            throw new IllegalArgumentException("Army name cannot be null or empty");
        }

        if (size < Constants.MIN_ARMY_SIZE || size > Constants.MAX_ARMY_SIZE)
        {
            throw new IllegalArgumentException("Army size must be between " + Constants.MIN_ARMY_SIZE + " and " +  Constants.MAX_ARMY_SIZE);
        }

        armyName = name.trim();
        armySize = size;
    }

    public int getArmySize()
    {
        return armySize;
    }

    public String getArmyName()
    {
        return armyName;
    }

    public Creature getCreature(int index)
    {
        Creature result = null;

        if (index >= 0 && index < armySize)
        {
            result = creatures[index];
        }
        return result;
    }

    private void initializeCreatures()
    {
        // Prevents names from being "consumed" when creation fails
        boolean[] originalUsedNames = new boolean[Main.usedNames.length];
        System.arraycopy(Main.usedNames, 0, originalUsedNames, 0, originalUsedNames.length);

        try
        {
            for (int i = 0; i < armySize; i++)
            {
                String creatureName = Main.getUniqueName();
                int creatureType = Constants.rand.nextInt(4);
                int strength = Constants.rand.nextInt(Constants.MAX_STRENGTH - Constants.MIN_STRENGTH + 1) + Constants.MIN_STRENGTH;
                int health = Constants.rand.nextInt(Constants.MAX_HEALTH - Constants.MIN_HEALTH + 1) + Constants.MIN_HEALTH;

                switch (creatureType)
                {
                    case 0:
                        creatures[i] = new Bahamut(creatureName, health, strength);
                        break;
                    case 1:
                        creatures[i] = new Cyberbahamut(creatureName, health, strength);
                        break;
                    case 2:
                        creatures[i] = new Superbahamut(creatureName, health, strength);
                        break;
                    case 3:
                        creatures[i] = new Macara(creatureName, health, strength);
                        break;
                    default:
                        creatures[i] = new Creature(creatureName, health, strength);
                        break;
                }
            }
        }
        catch (InvalidArmyParamException e)
        {
            System.arraycopy(originalUsedNames, 0, Main.usedNames, 0, originalUsedNames.length);
            throw new InvalidArmyParamException("Failed to initialize creatures: " + e.getMessage()); // Caught and handled within constructor
        }
    }

    public int getTotalHealth()
    {
        int totalHealth = 0;

        for (int i = 0; i < armySize; i++)
        {
            if (creatures[i] != null)
            {
                totalHealth += creatures[i].getHealth();
            }
        }
        return totalHealth;
    }

    public String toString()
    {
        String headerFormat = "%-15s | %-15s | %10s | %10s";
        String result = armyName + " Stats\n";
        result += String.format(headerFormat, "Creature", "Type", "Strength", "Health") + "\n";
        result += "-".repeat(65) + "\n";

        for (int i = 0; i < armySize; i++)
        {
            if (creatures[i] != null)
            {
                result += creatures[i].toString() + "\n";
            }
        }

        result += "Total Health: " + getTotalHealth() + "\n";

        return result;
    }
}

/*

=== CREATURE BATTLE SYSTEM ===
1. Battle
2. Quit
Choice: h
Incorrect format detected - Invalid choice. Please select a valid menu option


=== CREATURE BATTLE SYSTEM ===
1. Battle
2. Quit
Choice: 3
Invalid choice. Please select a valid menu option


=== CREATURE BATTLE SYSTEM ===
1. Battle
2. Quit
Choice: 1
Enter army size (1-15): -1
Army size must be between 1 and 15
Enter army size (1-15): 16
Army size must be between 1 and 15
Enter army size (1-15): 5


=== NEW BATTLE ===

Army Stats Before Battle:
Army #1 Stats
Creature        | Type            |   Strength |     Health
-----------------------------------------------------------------
Thorin          | bahamut         |        142 |         79
Legolas         | macara          |         77 |         91
Ori             | macara          |        105 |         95
Bombur          | superbahamut    |         92 |        144
Oin             | macara          |        176 |        115
Total Health: 524

Army #2 Stats
Creature        | Type            |   Strength |     Health
-----------------------------------------------------------------
Radagast        | cyberbahamut    |        116 |        127
Sam             | cyberbahamut    |        145 |         73
Pippin          | superbahamut    |        155 |        158
Frodo           | macara          |         83 |        119
Faramir         | cyberbahamut    |        156 |        124
Total Health: 601


--- BATTLE BEGINS ---

Battle 1: Thorin the bahamut vs Radagast the cyberbahamut
Attacker                  | Damage | Army       | Defender                  | Defender Health | Army
------------------------------------------------------------------------------------------------------------------------
Radagast the cyberbahamut |     95 | Army #2    | Thorin the bahamut        |               0 | Army #1
Winner: Radagast the cyberbahamut

Battle 2: Legolas the macara vs Sam the cyberbahamut
Attacker                  | Damage | Army       | Defender                  | Defender Health | Army
------------------------------------------------------------------------------------------------------------------------
Legolas the macara        |      3 | Army #1    | Sam the cyberbahamut      |              70 | Army #2
Sam the cyberbahamut      |     41 | Army #2    | Legolas the macara        |              50 | Army #1
Legolas the macara        |     45 | Army #1    | Sam the cyberbahamut      |              25 | Army #2
Sam the cyberbahamut      |     96 | Army #2    | Legolas the macara        |               0 | Army #1
Winner: Sam the cyberbahamut

Battle 3: Ori the macara vs Pippin the superbahamut
Attacker                  | Damage | Army       | Defender                  | Defender Health | Army
------------------------------------------------------------------------------------------------------------------------
Ori the macara            |     86 | Army #1    | Pippin the superbahamut   |              72 | Army #2
Pippin the superbahamut   |    109 | Army #2    | Ori the macara            |               0 | Army #1
Winner: Pippin the superbahamut

Battle 4: Bombur the superbahamut vs Frodo the macara
Attacker                  | Damage | Army       | Defender                  | Defender Health | Army
------------------------------------------------------------------------------------------------------------------------
Frodo the macara          |      4 | Army #2    | Bombur the superbahamut   |             140 | Army #1
Bombur the superbahamut   |     18 | Army #1    | Frodo the macara          |             101 | Army #2
Bombur the superbahamut (2nd) |     54 | Army #1    | Frodo the macara          |              47 | Army #2
Frodo the macara          |     82 | Army #2    | Bombur the superbahamut   |              58 | Army #1
Bombur the superbahamut   |     76 | Army #1    | Frodo the macara          |               0 | Army #2
Winner: Bombur the superbahamut

Battle 5: Oin the macara vs Faramir the cyberbahamut
Attacker                  | Damage | Army       | Defender                  | Defender Health | Army
------------------------------------------------------------------------------------------------------------------------
Faramir the cyberbahamut  |     44 | Army #2    | Oin the macara            |              71 | Army #1
Oin the macara            |    113 | Army #1    | Faramir the cyberbahamut  |              11 | Army #2
Faramir the cyberbahamut  |     21 | Army #2    | Oin the macara            |              50 | Army #1
Oin the macara            |     61 | Army #1    | Faramir the cyberbahamut  |               0 | Army #2
Winner: Oin the macara

Army Stats After Battle:
Army #1 Stats
Creature        | Type            |   Strength |     Health
-----------------------------------------------------------------
Thorin          | bahamut         |        142 |          0
Legolas         | macara          |         77 |          0
Ori             | macara          |        105 |          0
Bombur          | superbahamut    |         92 |         58
Oin             | macara          |        176 |         50
Total Health: 108

Army #2 Stats
Creature        | Type            |   Strength |     Health
-----------------------------------------------------------------
Radagast        | cyberbahamut    |        116 |        127
Sam             | cyberbahamut    |        145 |         25
Pippin          | superbahamut    |        155 |         72
Frodo           | macara          |         83 |          0
Faramir         | cyberbahamut    |        156 |          0
Total Health: 224



=== FINAL RESULTS ===
Army #1 total health: 108
Army #2 total health: 224
Army #2 wins the war!

Battle results written to file


=== CREATURE BATTLE SYSTEM ===
1. Battle
2. Quit
Choice: 2
Now exiting program...

Process finished with exit code 0

 */
