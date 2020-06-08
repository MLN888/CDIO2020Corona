import java.util.ArrayList;

public class FindLegalMoves {

    ArrayList<Move> allMoves;

    public FindLegalMoves() {
        this.allMoves = new ArrayList<>();
    }

    public ArrayList<Move> findMoves() {
        allMoves.clear();           // We re-use the same FindLegalMoves object again and again, so we need to clear the allMoves variable each time

        checkFoundations();         // Check if can move card from a column or from the draw pile to a foundation pile
        checkKingToFreeSpot();      // Check if we can move a king from a column or the draw pile to an empty column
        checkColumns();             // Check if we can move one card or a group of cards from a column to a column
        checkDrawPile();            // Check if we can move the card from the draw pile to a column

        if (Table.debugText) {      // For debugging: Print out all move-objects from the allMove ArrayList.
            System.out.println("******* POSSIBLE MOVES FOUND *******");
            for (int i = 0; i < allMoves.size(); i++) {
                System.out.println(allMoves.get(i).toString());
            }
        }
        return allMoves;
    }


    private void checkFoundations() {

        if (Table.debugText) System.out.println("******* CHECK FOUNDATIONS *******");
        for (int i = 0; i < 7; i++) {                                           // Iterate columns
            int topColumnIndex = Table.position.get(i).size() - 1;              // Calculate index of top card in ith column
            String topColumn = Table.position.get(i).get(topColumnIndex);       // Get the ith top column card from Table
            int valueColumnCard = extractValue(topColumn);                      // get value of top card in ith column, using the extractValue() method
            String suitColumnCard = topColumn.substring(1);                     // get suit of top card in ith column, using Java's substring method
            if (Table.debugText)
                System.out.println("(column " + i + ")   Value of card: " + valueColumnCard + "   Suit of card: " + suitColumnCard);

            for (int j = 7; j < 11; j++) {                                                  // Iterate foundation piles
                int topFoundationIndex = Table.position.get(j).size() - 1;                  // Calculate index of top card in jth foundation
                String topFoundation = Table.position.get(j).get(topFoundationIndex);       // Get the jth top foundation card from Table
                int valueFoundationCard = extractValue(topFoundation);                      // get value of top card in ith foundation
                String suitFoundationCard = topFoundation.substring(1);                     // get suit of top card in ith foundation
                if (Table.debugText)
                    System.out.println("(foundation " + (j - 6) + ")   Value of card: " + valueFoundationCard + "   Suit of card: " + suitFoundationCard);

                // Check if top card of a column can be placed on a foundation pile by comparing values and suits.
                if (valueColumnCard == valueFoundationCard + 1 && suitColumnCard.equals(suitFoundationCard)) {
                    if (Table.debugText) System.out.println("Possible move: column " + i + " to foundation " + (j - 7));
                    String text = Table.convertToText(valueColumnCard, suitColumnCard);     // convert raw card to plain text with amazing convertToText method
                    Move move = new Move(i, j, topColumnIndex, 1, topColumn, text);    // Create Move object
                    allMoves.add(move);     // Add the new legal move to ArrayList of legal moves
                }
            }
        }

        // Now we check if the draw pile holds a card that can be moved to a foundation pile
        int topDrawPileIndex = Table.position.get(11).size() - 1;               // Calculate index of top card in draw pile
        String topDrawPile = Table.position.get(11).get(topDrawPileIndex);      // Get the top card in draw pile
        int valueDrawPile = extractValue(topDrawPile);                          // get value of top card in draw pile
        String suitDrawPile = topDrawPile.substring(1);                         // get suit of top card in draw pile

        for (int i = 7; i < 11; i++) {                                          // Iterate foundation piles
            int topFoundationIndex = Table.position.get(i).size() - 1;                  // Calculate index of top card in foundation pile
            String topFoundation = Table.position.get(i).get(topFoundationIndex);       // get the ith top foundation card from Table
            int valueFoundationCard = extractValue(topFoundation);                      // get value of top card in foundation
            String suitFoundationCard = topFoundation.substring(1);                     // get suit of top card in foundation

            // Check if top card of the draw pile can be placed on a foundation pile by comparing values and suits.
            if (valueDrawPile == valueFoundationCard + 1 && suitDrawPile.equals(suitFoundationCard)) {
                if (Table.debugText) System.out.println("\nDraw pile: Match to foundation " + (i - 7));
                String text = Table.convertToText(valueDrawPile, suitDrawPile);                                                     // convert raw card to plain text
                Move move = new Move(11, i, topDrawPileIndex, 1, topDrawPile, text); // create legal move object
                allMoves.add(move);     // Add the new legal move to ArrayList of legal moves
            }
        }
        if (Table.debugText) System.out.println("******* FOUNDATIONS CHECKED ******\n");
    }


    private void checkKingToFreeSpot() {
        if (Table.debugText) System.out.println("******* CHECK FOR KINGS TO FREE SPOT *******");

        // Check if any column is empty. If so, iterate all cards in all columns to look for king.
        for (int i = 0; i < 7; i++) {                                       // Iterate columns to check for empty spot
            if (Table.position.get(i).size() == 1) {                        // If size of column is 1, it is empty.
                if (Table.debugText) System.out.println("Column " + i + " found empty");
                for (int j = 0; j < 7; j++) {                               // Iterate all column cards to check for king
                    int sizeOfColumn = Table.position.get(j).size();        // Get size of column j
                    for (int k = 0; k < sizeOfColumn; k++) {                // Iterate each column
                        if (Table.position.get(j).get(k).substring(0, 1).equals("K") && Table.unseen[j] != 0) {     // Is the card a king? And sits ontop unseen cards?
                            if (Table.debugText) System.out.println("King found in column " + j + " postion " + k);
                            String kingFound = Table.position.get(j).get(k);
                            int valueKing = extractValue(kingFound);                      // get value of king (Well, its always 13...)
                            String suitKing = kingFound.substring(1);                     // get suit of king
                            String text = Table.convertToText(valueKing, suitKing);       // convert raw card to plain text
                            Move move = new Move(j, i, k, 2, kingFound, text);  // Create new legal move object
                            allMoves.add(move);         // Add the new legal move to ArrayList of legal moves
                        }
                    }
                }
            }
        }

        // Check if top card of draw pile is a king
        int topDrawPileIndex = Table.position.get(11).size() - 1;               // Calculate index of top card in draw pile
        String topDrawPile = Table.position.get(11).get(topDrawPileIndex);      // Get the top card in draw pile

        if (topDrawPile.substring(0, 1).equals("K")) {                          // Top of drawpile is a king, if first letter is "K".
            for (int i = 0; i < 7; i++) {                                       // Iterate columns
                if (Table.position.get(i).size() == 1) {                        // If size of column is 1, it is empty.
                    if (Table.debugText) System.out.println("Possible move: King from drawpile to column " + i);
                    int valueDrawPile = extractValue(topDrawPile);                      // get value of top card in draw pile
                    String suitDrawPile = topDrawPile.substring(1);                     // get suit of top card in draw pile
                    String text = Table.convertToText(valueDrawPile, suitDrawPile);     // convert raw card to plain text
                    Move move = new Move(11, i, topDrawPileIndex, 2, topDrawPile, text); // create legal move object
                    allMoves.add(move);     // Add the new legal move to ArrayList of legal moves
                }
            }
        }

        if (Table.debugText) System.out.println("******* END OF CHECK FOR KINGS TO FREE SPOT *******\n");
    }

    private void checkColumns() {
        if(Table.columnToColumn == Table.MAX_COLUMN_TO_COLUMN){     // If limit on column-to-column moves is reached, skip this check
            if(Table.debugText) System.out.println("Max column-to-column reached, no type 3 moves allowed.");
            return;
        }

        if (Table.debugText) System.out.println("******* CHECK FOR COLUMN TO COLUMN *******");

        for (int i = 0; i < 7; i++) {                                       // Iterate all columns and look at top card
            int sizeOfColumn = Table.position.get(i).size();                // Get size of current column i
            String topCard = Table.position.get(i).get(sizeOfColumn - 1);   // Get the top card of column i
            int topValue = extractValue(topCard);                           // Get value of top card in column i
            String topColor = extractColor(topCard);                        // Get color of top card in column i
            if (Table.debugText) {
                System.out.println("Top card: Column " + i + " card " + topCard + " color " + topColor + " value " + topValue);
            }
            for (int j = 0; j < 7; j++) {                                   // Iterate all cards in all columns and compare with 'topCard'
                if (i == j) continue;                                       // No need to check own column for match
                int sizeOfColumnj = Table.position.get(j).size();           // Get size of current column j
                for (int k = 1; k < sizeOfColumnj; k++) {                   // Iterate cards in column j

                    String thisCard = Table.position.get(j).get(k);         // Get the card we are now looking at
                    int thisValue = extractValue(thisCard);                  // Get value of card we are now looking at
                    String thisColor = extractColor(thisCard);               // Get color of card we are now looking at
                    if (Table.debugText) {
                        System.out.println("Comparing with: column " + j + " card " + thisCard + " color " + thisColor + " value " + thisValue);
                    }
                    if (!thisColor.equals(topColor) && topValue == thisValue + 1) {
                        if (Table.debugText) {
                            System.out.println("Possible move from column " + j + " to column " + i + ": Card " + thisCard + " to " + topCard);
                        }
                        String suitCard = thisCard.substring(1);                     // get suit of card we are looking at
                        String text = Table.convertToText(thisValue, suitCard);     // convert raw card to plain text
                        // Create new legal move object
                        Move move = new Move(j, i, k, 3, thisCard, text);
                        if (!move.getCard().equals(Table.justMoved)) {          // Only accept move if card was not recently moved
                            allMoves.add(move);         // Add the new legal move to ArrayList of legal moves
                        }
                    }
                }
            }
        }
        if (Table.debugText) System.out.println("******* END OF CHECK FOR COLUMN TO COLUMN *******\n");
    }

    private void checkDrawPile() {
        if (Table.debugText) System.out.println("******* CHECK FOR DRAW PILE TO COLUMN *******");

        int topDrawPileIndex = Table.position.get(11).size();                   // Calculate index of top card in draw pile
        String topDrawPile = Table.position.get(11).get(topDrawPileIndex - 1);  // Get the top card in draw pile
        int valueDrawCard = extractValue(topDrawPile);                               // Get value of top card in column i
        String drawColor = extractColor(topDrawPile);                            // Get color of top card in column i
        if (Table.debugText)
            System.out.println("Top card in draw pile: " + topDrawPile + " color: " + drawColor + " value: " + valueDrawCard);

        for (int i = 0; i < 7; i++) {                                           // Iterate all columns and look at top card
            int sizeOfColumn = Table.position.get(i).size();                    // Get size of current column i
            String columnCard = Table.position.get(i).get(sizeOfColumn - 1);    // Get the top card of column i
            int columnValue = extractValue(columnCard);                         // Get value of top card in column i
            String columnColor = extractColor(columnCard);                      // Get color of top card in column i
            if (Table.debugText) {
                System.out.println("Comparing with column: " + i + " card: " + columnCard + " color: " + columnColor + " value: " + columnValue);
            }
            if (!drawColor.equals(columnColor) && columnValue == valueDrawCard + 1) {   // Compare value and color
                if (Table.debugText)
                    System.out.println("Possible move: Draw pile " + topDrawPile + " to column " + i + " onto card " + columnCard);

                String suitDrawCard = topDrawPile.substring(1);                     // get suit of card we are looking at
                String text = Table.convertToText(valueDrawCard, suitDrawCard);     // convert raw card to plain text
                Move move = new Move(11, i, topDrawPileIndex - 1, 4, topDrawPile, text);  // Create new legal move object
                allMoves.add(move);         // Add the new legal move to ArrayList of legal moves
            }
        }

        if (Table.debugText) System.out.println("******* END OF CHECK FOR DRAW PILE TO COLUMN *******\n");
    }


    private int extractValue(String card) {
        // Checks first character in a card-String and returns the value as an integer 1-13
        int value;
        String firstCharacter = card.substring(0, 1);
        switch (firstCharacter) {
            case "A":
                value = 1;
                break;
            case "0":
                value = 10;
                break;
            case "J":
                value = 11;
                break;
            case "Q":
                value = 12;
                break;
            case "K":
                value = 13;
                break;
            case "X":
                value = 0;
                break;
            default:
                try {
                    value = Integer.parseInt(firstCharacter);
                } catch (NumberFormatException e) {
                    System.out.println("Error extacting value from card");
                    return -1;
                }
                return value;
        }
        return value;
    }

    private String extractColor(String card) {
        if (card.substring(1).equals("C") || card.substring(1).equals("S")) {
            return "black";
        } else {
            return "red";
        }
    }

} // End of class
