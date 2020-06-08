import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileHandler {

    private String fileName = "C:/Users/janmu/IdeaProjects/Solitaire/src/tableFile";
    private String splitter = ",";
    private String[] cards;

    public FileHandler() {
    }

    public void updateTable() { // This is the only public method in this class. Amazing encapsulation.
        readFile();             // Method to read the file created by OpenCV.
        populateTable();        // Method that fills in the Table with the cards from the file
        if (Table.debugText) Table.printTable();    // Print the Table after it is updated with new cards from file
    }

    private void readFile() {
        // Standard csv file reader
        if (Table.debugText) System.out.println("\n******* NOW READING FILE *******");
        String line;
        try {
            BufferedReader myBuffReader = new BufferedReader(new FileReader(fileName));
            while ((line = myBuffReader.readLine()) != null) {
                this.cards = line.split(splitter);         // File contents is stored in String Array 'cards'
            }
            myBuffReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (Table.debugText) System.out.println("******* FILE READ COMPLETE *******\n");

        if (Table.debugText) {  // Call method to print file contents if 'debugText' is TRUE
            printCards(this.cards);
        }
    }

    private void populateTable() {
        // All cards from the file is compared to the current top card on the Table.
        // If they are different, it can only be because a card has been flipped. If so, that
        // card is then inserted into the column or draw pile in Table

        if (Table.debugText) System.out.println("\n******* NOW UPDATING TABLE WITH CARDS FROM FILE *******");

        int sizeDrawPile = Table.position.get(11).size();                           // Get size of draw pile
        if (!cards[0].equals(Table.position.get(11).get(sizeDrawPile - 1))) {       // Compare top card of draw pile with card from file
            Table.position.get(11).add(cards[0]);                                   // If new, add it
            if (Table.debugText) System.out.println("Card added to draw pile: " + cards[0]);
        }

        for (int i = 0; i < 7; i++) {
            // only add card from file to column if it is DIFFERENT (newly flipped)
            if (!Table.position.get(i).get(Table.position.get(i).size() - 1).equals(cards[5 + i])) {
                Table.position.get(i).add(cards[5 + i]);
                if (Table.debugText) System.out.println("Card added to column " + i + ": " + cards[5 + i]);
            }
        }
        // Note: Foundation piles are not updated using the file. They are updated by AI.
        if (Table.debugText) System.out.println("******* TABLE UPDATE COMPLETE *******");
    }


    private void printCards(String[] cards) {       // Print current file contents for debugging
        System.out.println("******* FILE CHECK *******");
        System.out.println("Number cards found in file: " + cards.length);
        System.out.println("Draw pile: " + cards[0]);
        System.out.println("Foundation pile 0: " + cards[1]);
        System.out.println("Foundation pile 1: " + cards[2]);
        System.out.println("Foundation pile 2: " + cards[3]);
        System.out.println("Foundation pile 3: " + cards[4]);
        System.out.println("Column 0: " + cards[5]);
        System.out.println("Column 1: " + cards[6]);
        System.out.println("Column 2: " + cards[7]);
        System.out.println("Column 3: " + cards[8]);
        System.out.println("Column 4: " + cards[9]);
        System.out.println("Column 5: " + cards[10]);
        System.out.println("Column 6: " + cards[11]);
        System.out.println("******* FILE CHECK END *******");
    }
}
