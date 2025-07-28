// Garrett Brunsch
// Lab #6 - Inheritance
// Due 7/27/25

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

    private static boolean[] usedNames = new boolean[CREATURE_NAMES.length];

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

class Creature
{
    protected String name;
    protected int health;
    protected int strength;

    public Creature()
    {
        this.name = Constants.DEFAULT_NAME;
        this.health = Constants.MIN_HEALTH;
        this.strength = Constants.MIN_STRENGTH;
    }

    public Creature(String name, int health, int strength)
    {
        this.name = name;
        this.health = health;
        this.strength = strength;
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
        health -= damage;
        if (health < 0) health = 0;
    }

    public boolean isAlive()
    {
        return health > 0;
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

class Army
{
    private String armyName = Constants.DEFAULT_NAME;
    private int armySize = Constants.DEFAULT_ARMY_SIZE;
    private Creature[] creatures = new Creature[Constants.MAX_ARMY_SIZE];

    public Army()
    {
        setArmy(Constants.ARMY_1_NAME, Constants.DEFAULT_ARMY_SIZE);
        initializeCreatures();
    }

    public Army(int size)
    {
        setArmy(Constants.ARMY_1_NAME, size);
        initializeCreatures();
    }

    public Army(String name)
    {
        setArmy(name, Constants.DEFAULT_ARMY_SIZE);
        initializeCreatures();
    }

    public Army(String name, int size)
    {
        setArmy(name, size);
        initializeCreatures();
    }

    public void setArmy(String name, int size)
    {
        armyName = name;
        armySize = (size >= Constants.MIN_ARMY_SIZE && size <= Constants.MAX_ARMY_SIZE) ?
                size : Constants.DEFAULT_ARMY_SIZE;
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
Choice: g
Incorrect format detected - Invalid choice. Please select a valid menu option


=== CREATURE BATTLE SYSTEM ===
1. Battle
2. Quit
Choice: 4
Invalid choice. Please select a valid menu option


=== CREATURE BATTLE SYSTEM ===
1. Battle
2. Quit
Choice: 1
Enter army size (1-15): 5


=== NEW BATTLE ===

Army Stats Before Battle:
Army #1 Stats
Creature        | Type            |   Strength |     Health
-----------------------------------------------------------------
Gimli           | cyberbahamut    |        132 |        120
Dwalin          | macara          |         95 |        178
Legolas         | bahamut         |        180 |         70
Merry           | macara          |         56 |        154
Boromir         | cyberbahamut    |         62 |        169
Total Health: 691

Army #2 Stats
Creature        | Type            |   Strength |     Health
-----------------------------------------------------------------
Kili            | bahamut         |         72 |        192
Arwen           | macara          |         95 |        189
Bombur          | bahamut         |        123 |         57
Eowyn           | macara          |         69 |        111
Oin             | cyberbahamut    |         85 |        191
Total Health: 740


--- BATTLE BEGINS ---

Battle 1: Gimli the cyberbahamut vs Kili the bahamut
Attacker                  | Damage | Army       | Defender                  | Defender Health | Army
------------------------------------------------------------------------------------------------------------------------
Kili the bahamut          |     17 | Army #2    | Gimli the cyberbahamut    |             103 | Army #1
Gimli the cyberbahamut    |     23 | Army #1    | Kili the bahamut          |             169 | Army #2
Kili the bahamut          |      9 | Army #2    | Gimli the cyberbahamut    |              94 | Army #1
Gimli the cyberbahamut    |    120 | Army #1    | Kili the bahamut          |              49 | Army #2
Kili the bahamut          |     44 | Army #2    | Gimli the cyberbahamut    |              50 | Army #1
Gimli the cyberbahamut    |      6 | Army #1    | Kili the bahamut          |              43 | Army #2
Kili the bahamut          |     50 | Army #2    | Gimli the cyberbahamut    |               0 | Army #1
Winner: Kili the bahamut

Battle 2: Dwalin the macara vs Arwen the macara
Attacker                  | Damage | Army       | Defender                  | Defender Health | Army
------------------------------------------------------------------------------------------------------------------------
Arwen the macara          |     25 | Army #2    | Dwalin the macara         |             153 | Army #1
Dwalin the macara         |     95 | Army #1    | Arwen the macara          |              94 | Army #2
Arwen the macara          |     65 | Army #2    | Dwalin the macara         |              88 | Army #1
Dwalin the macara         |     55 | Army #1    | Arwen the macara          |              39 | Army #2
Arwen the macara          |     49 | Army #2    | Dwalin the macara         |              39 | Army #1
Dwalin the macara         |     93 | Army #1    | Arwen the macara          |               0 | Army #2
Winner: Dwalin the macara

Battle 3: Legolas the bahamut vs Bombur the bahamut
Attacker                  | Damage | Army       | Defender                  | Defender Health | Army
------------------------------------------------------------------------------------------------------------------------
Legolas the bahamut       |    132 | Army #1    | Bombur the bahamut        |               0 | Army #2
Winner: Legolas the bahamut

Battle 4: Merry the macara vs Eowyn the macara
Attacker                  | Damage | Army       | Defender                  | Defender Health | Army
------------------------------------------------------------------------------------------------------------------------
Merry the macara          |     44 | Army #1    | Eowyn the macara          |              67 | Army #2
Eowyn the macara          |     13 | Army #2    | Merry the macara          |             141 | Army #1
Merry the macara          |     54 | Army #1    | Eowyn the macara          |              13 | Army #2
Eowyn the macara          |    100 | Army #2    | Merry the macara          |              41 | Army #1
Merry the macara          |      8 | Army #1    | Eowyn the macara          |               5 | Army #2
Eowyn the macara          |     60 | Army #2    | Merry the macara          |               0 | Army #1
Winner: Eowyn the macara

Battle 5: Boromir the cyberbahamut vs Oin the cyberbahamut
Attacker                  | Damage | Army       | Defender                  | Defender Health | Army
------------------------------------------------------------------------------------------------------------------------
Oin the cyberbahamut      |      5 | Army #2    | Boromir the cyberbahamut  |             164 | Army #1
Boromir the cyberbahamut  |     20 | Army #1    | Oin the cyberbahamut      |             171 | Army #2
Oin the cyberbahamut      |      9 | Army #2    | Boromir the cyberbahamut  |             155 | Army #1
Boromir the cyberbahamut  |     21 | Army #1    | Oin the cyberbahamut      |             150 | Army #2
Oin the cyberbahamut      |     83 | Army #2    | Boromir the cyberbahamut  |              72 | Army #1
Boromir the cyberbahamut  |     15 | Army #1    | Oin the cyberbahamut      |             135 | Army #2
Oin the cyberbahamut      |     26 | Army #2    | Boromir the cyberbahamut  |              46 | Army #1
Boromir the cyberbahamut  |     59 | Army #1    | Oin the cyberbahamut      |              76 | Army #2
Oin the cyberbahamut      |     44 | Army #2    | Boromir the cyberbahamut  |               2 | Army #1
Boromir the cyberbahamut  |     46 | Army #1    | Oin the cyberbahamut      |              30 | Army #2
Oin the cyberbahamut      |     38 | Army #2    | Boromir the cyberbahamut  |               0 | Army #1
Winner: Oin the cyberbahamut

Army Stats After Battle:
Army #1 Stats
Creature        | Type            |   Strength |     Health
-----------------------------------------------------------------
Gimli           | cyberbahamut    |        132 |          0
Dwalin          | macara          |         95 |         39
Legolas         | bahamut         |        180 |         70
Merry           | macara          |         56 |          0
Boromir         | cyberbahamut    |         62 |          0
Total Health: 109

Army #2 Stats
Creature        | Type            |   Strength |     Health
-----------------------------------------------------------------
Kili            | bahamut         |         72 |         43
Arwen           | macara          |         95 |          0
Bombur          | bahamut         |        123 |          0
Eowyn           | macara          |         69 |          5
Oin             | cyberbahamut    |         85 |         30
Total Health: 78



=== FINAL RESULTS ===
Army #1 total health: 109
Army #2 total health: 78
Army #1 wins the war!

Battle results written to file


=== CREATURE BATTLE SYSTEM ===
1. Battle
2. Quit
Choice: 1
Enter army size (1-15): 55
Army size must be between 1 and 15
Enter army size (1-15): -1
Army size must be between 1 and 15
Enter army size (1-15): 2


=== NEW BATTLE ===

Army Stats Before Battle:
Army #1 Stats
Creature        | Type            |   Strength |     Health
-----------------------------------------------------------------
Kili            | macara          |        130 |        170
Thorin          | superbahamut    |         78 |        103
Total Health: 273

Army #2 Stats
Creature        | Type            |   Strength |     Health
-----------------------------------------------------------------
Faramir         | superbahamut    |        104 |        189
Pippin          | cyberbahamut    |        123 |         88
Total Health: 277


--- BATTLE BEGINS ---

Battle 1: Kili the macara vs Faramir the superbahamut
Attacker                  | Damage | Army       | Defender                  | Defender Health | Army
------------------------------------------------------------------------------------------------------------------------
Faramir the superbahamut  |     91 | Army #2    | Kili the macara           |              79 | Army #1
Faramir the superbahamut (2nd) |     56 | Army #2    | Kili the macara           |              23 | Army #1
Kili the macara           |     57 | Army #1    | Faramir the superbahamut  |             132 | Army #2
Faramir the superbahamut  |    101 | Army #2    | Kili the macara           |               0 | Army #1
Winner: Faramir the superbahamut

Battle 2: Thorin the superbahamut vs Pippin the cyberbahamut
Attacker                  | Damage | Army       | Defender                  | Defender Health | Army
------------------------------------------------------------------------------------------------------------------------
Thorin the superbahamut   |     41 | Army #1    | Pippin the cyberbahamut   |              47 | Army #2
Thorin the superbahamut (2nd) |     25 | Army #1    | Pippin the cyberbahamut   |              22 | Army #2
Pippin the cyberbahamut   |     14 | Army #2    | Thorin the superbahamut   |              89 | Army #1
Thorin the superbahamut   |     59 | Army #1    | Pippin the cyberbahamut   |               0 | Army #2
Winner: Thorin the superbahamut

Army Stats After Battle:
Army #1 Stats
Creature        | Type            |   Strength |     Health
-----------------------------------------------------------------
Kili            | macara          |        130 |          0
Thorin          | superbahamut    |         78 |         89
Total Health: 89

Army #2 Stats
Creature        | Type            |   Strength |     Health
-----------------------------------------------------------------
Faramir         | superbahamut    |        104 |        132
Pippin          | cyberbahamut    |        123 |          0
Total Health: 132



=== FINAL RESULTS ===
Army #1 total health: 89
Army #2 total health: 132
Army #2 wins the war!

Battle results written to file


=== CREATURE BATTLE SYSTEM ===
1. Battle
2. Quit
Choice: 2
Now exiting program...

Process finished with exit code 0


 */
