import java.util.ArrayList;
import java.util.List;

public class Table {

    // This variable is only null until the class is instantiated for the first time
    private static Table singleInstance = null;

    // The twelve positions of the solitaire table. This variable is STATIC and can be accessed from all other classes
    public static List<List<String>> position;
    /*
        Positions 0-6:    Columns
        Positions 7-10:   Foundation piles
        Positions 11:     Draw pile
    */

    public static boolean debugText;                // Boolean to switch debug printouts on/off
    public static boolean gameOn = true;            // When true, game continues indefinitely.
    public static int cardsLeftInDrawPile = 23;     // When game starts, draw pile has 23 cards
    public static int cardsRemovedFromDrawPile = 0; // Track how many cards we permanently remove from draw pile
    public static int shuffles = 3;                 // Track how many shuffles we may still do
    public static int[] unseen = {0, 1, 2, 3, 4, 5, 6, 0, 0, 0, 0, 0};       // Track how many unseen cards remain in each column
    public static String justMoved = "XX";                                  // Track last move to avoid moving same card two times in a row
    public static int columnToColumn = 0;                               // Track number of column moves to avoid looping
    public static final int MAX_COLUMN_TO_COLUMN = 2;
    // Maximum allowed column-to-column moves before game is forced to try another type of move

    // Constructor. It is private, so the Table class can only be instantiated from inside this class.
    private Table() {
        position = new ArrayList<>();             // The 'outer' ArrayList is initialized

        for (int i = 0; i < 12; i++) {
            position.add(new ArrayList<>());      // The 12 'inner' ArrayLists are added
        }

        for (int i = 0; i < 7; i++) {
            position.get(i).add("XX");            // An "XX" is added to each column, denoting no cards
        }

        position.get(7).add("XC");                // Foundation pile 1 is marked with clubs. X means no card.
        position.get(8).add("XD");                // Foundation pile 2 is marked with diamonds. X means no card.
        position.get(9).add("XH");                // Foundation pile 3 is marked with hearts. X means no card.
        position.get(10).add("XS");               // Foundation pile 4 is marked with spades. X means no card.

        position.get(11).add("XX");               // Draw pile is also initialized with "XX", denoting no card
    }

    // The following method is the essence of a so called "singleton" class.
    // When this public method is called for the first time, the Table Class is instantiated in
    // the variable "singleInstance". If this method is called a second time, nothing happens.
    public static void createTable() {
        if (singleInstance == null) {           // This is only true the first time method is called
            singleInstance = new Table();       // The table structure is created and initialized as seen in the constructor
        }
    }

    public static void printTable() {           // Method to print the entire Table for debugging
        System.out.println();
        System.out.println("******* CURRENT TABLE *******");

        int drawpileSize = Table.position.get(11).size();
        System.out.print("Draw pile: ");
        for (int i = 0; i < drawpileSize; i++) {
            System.out.print(Table.position.get(11).get(i) + " ");      // Print drawpile.
        }
        System.out.println();
        System.out.println("Cards left in draw pile:" + Table.cardsLeftInDrawPile + "  Cards removed from draw pile:" + Table.cardsRemovedFromDrawPile);


        for (int i = 7; i < 11; i++) {                                   // Iterate foundation piles (positions 7-10)
            System.out.print("Foundation " + (i - 7) + ": ");
            for (int j = 0; j < Table.position.get(i).size(); j++) {
                System.out.print(Table.position.get(i).get(j) + " ");
            }
            System.out.println();
        }

        for (int i = 0; i < 7; i++) {                                   // Iterate columns (positions 0-6)
            System.out.print("Column " + i + ": ");
            System.out.print("Unseen cards: " + unseen[i] + "  Visible: ");
            for (int j = 0; j < Table.position.get(i).size(); j++) {
                System.out.print(Table.position.get(i).get(j) + " ");
            }
            System.out.println();
        }
        System.out.println("******* TABLE END *******\n");
    }

    public static String convertToText(int value, String card) {    // Receives raw card such as '12' and "H" and returns plaintext version, "ruder dame"

        String plainText = "";

        switch (card) {
            case "C":
                plainText = "klør ";
                break;
            case "D":
                plainText = "ruder ";
                break;
            case "H":
                plainText = "hjerter ";
                break;
            case "S":
                plainText = "spar ";
                break;
            default:
                System.out.println("Error in text conversion: " + card + " was received in stead of letter C, D, H or S");
        }

        switch (value) {
            case 1:
                plainText = plainText.concat("es");
                break;
            case 2:
                plainText = plainText.concat("to");
                break;
            case 3:
                plainText = plainText.concat("tre");
                break;
            case 4:
                plainText = plainText.concat("fire");
                break;
            case 5:
                plainText = plainText.concat("fem");
                break;
            case 6:
                plainText = plainText.concat("seks");
                break;
            case 7:
                plainText = plainText.concat("syv");
                break;
            case 8:
                plainText = plainText.concat("otte");
                break;
            case 9:
                plainText = plainText.concat("ni");
                break;
            case 10:
                plainText = plainText.concat("ti");
                break;
            case 11:
                plainText = plainText.concat("knægt");
                break;
            case 12:
                plainText = plainText.concat("dame");
                break;
            case 13:
                plainText = plainText.concat("konge");
                break;
            default:
                System.out.println("Error in text conversion: " + value + " was received instead of value 1-13");
        }

        return plainText;
    }
}
