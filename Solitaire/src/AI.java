import java.util.ArrayList;
import java.util.Scanner;

public class AI {

    /*  The AI receives the ArrayList with legal move objects via the "thinkHard" method
     *   The AI selects the best move.
     *   The AI prints to console what the best move is
     *   The AI updates Table to reflect the move (always assuming that the player performs move correctly)
     *   When player wins or loses, game is stopped by setting Table.gameOn = false.
     * */

    public void thinkHard(ArrayList<Move> legalMoves) {
        System.out.println("\nTænker dybt...\n");


        if (legalMoves.isEmpty()) {                     // Check if there are no moves left. IF not, a number of things may happen:

            if (checkIfWon()) {                         // Check if win-condition is true (All unseen cards exposed)
                System.out.println("Du har vundet! KABALEN GÅR OP!");
                Table.gameOn = false;
                return;
            }

            if (Table.cardsLeftInDrawPile == 0 && Table.shuffles == 0) {        // If draw pile is empty and no more shuffles allowed, the game must end
                System.out.println("Der er ikke flere mulige træk og du må ikke blande bunken igen.\nSpillet er slut.\nTak for denne gang!");
                Table.gameOn = false;
                return;
            }

            if (Table.cardsLeftInDrawPile == 0) {        // If draw pile is empty, but player may still shuffle
                System.out.println("Ikke flere træk! Bland trækbunken og vend et nyt kort");
                Table.shuffles--;                       // Draw pile is shuffled. Deduct one from allowed shuffles
                Table.position.get(11).clear();         // Empty draw pile
                Table.position.get(11).add("XX");       // Add an XX to indicate empty
                Table.cardsLeftInDrawPile = 24 - Table.cardsRemovedFromDrawPile;       // Reinitialize drawpile to original amount (24) minus cards removed
                if (Table.debugText) {
                    System.out.println("Cards left in draw pile: " + Table.cardsLeftInDrawPile + " Cards removed from draw pile:" + Table.cardsRemovedFromDrawPile);
                }
                promptUser();
                return;
            }

            // If none of the above statements were true, then the player must simply draw a new card from the drawpile:
            System.out.println("Du skal vende et nyt kort fra trækbunken!");
            Table.cardsLeftInDrawPile--;            // Deduct one from remaining cards in draw pile
            if (Table.debugText) {
                System.out.println("Cards left in draw pile: " + Table.cardsLeftInDrawPile + " Cards removed from draw pile:" + Table.cardsRemovedFromDrawPile);
            }
            promptUser();
            return;
        }

        Move selectedMove = pickBestMove(legalMoves);   // This method is where AI-team implempents stuff to select the optimum move from the legalMoves-arrayList.


        if (selectedMove.getType() == 1) {              // If move is type 1: Move top card from column or drawpile to a foundation
            Table.columnToColumn = 0;      // Reset counter
            System.out.println("Du kan flytte et kort til en byggebunke!");
            if (selectedMove.getFromPosition() == 11) {         // If card comes from draw pile
                System.out.println("Flyt " + selectedMove.getPlainText() + " fra trækbunken til en byggebunke.");
                Table.cardsRemovedFromDrawPile++;               // A card is now permanently removed from draw pile
                if (Table.position.get(11).size() == 1) {       // If no visible cards in draw pile, one is turned over by player
                    Table.cardsLeftInDrawPile--;
                }
                if (Table.debugText) {
                    System.out.println("Cards left in draw pile: " + Table.cardsLeftInDrawPile + " Cards removed from draw pile:" + Table.cardsRemovedFromDrawPile);
                }
            } else {
                System.out.println("Flyt " + selectedMove.getPlainText() + " fra søjle " + (selectedMove.getFromPosition() + 1) + " til en byggebunke.");
            }
            int sizeColumn = Table.position.get(selectedMove.getFromPosition()).size();             // Get size of column that the card is moved from
            Table.position.get(selectedMove.getFromPosition()).remove(sizeColumn - 1);       // Remove card from Table

            if (Table.position.get(selectedMove.getFromPosition()).size() == 1 && Table.unseen[selectedMove.getFromPosition()] > 0) { // If column now empty
                Table.unseen[selectedMove.getFromPosition()]--; // Remove an unseen card from column, if any unseen cards remain.
            }

            Table.position.get(selectedMove.getToPosition()).add(selectedMove.getCard());          // Add card to foundation
            Table.justMoved = selectedMove.getCard();                                              // Remember last moved card
            promptUser();
            return;
        }

        if (selectedMove.getType() == 2) {              // If selected move is type 2: Move king from draw pile or column to empty spot
            Table.columnToColumn = 0;      // Reset counter
            System.out.println("Du kan flytte en konge til en ledig søjle!");
            if (selectedMove.getFromPosition() == 11) {         // If king comes from draw pile
                System.out.println("Flyt " + selectedMove.getPlainText() + " fra trækbunken til søjle nummer " + (selectedMove.getToPosition() + 1) + ".");
                int sizeStack = Table.position.get(selectedMove.getFromPosition()).size();              // Get size of drawpile
                Table.position.get(selectedMove.getFromPosition()).remove(sizeStack - 1);         // Remove king from draw pile
                Table.position.get(selectedMove.getToPosition()).add(selectedMove.getCard());           // Add king to the empty spot
                Table.justMoved = selectedMove.getCard();                                               // Remember last moved card
                Table.cardsRemovedFromDrawPile++;               // A card is now permanently removed from draw pile
                if (Table.position.get(11).size() == 1) {       // If no visible cards in draw pile, one is turned over by player..
                    Table.cardsLeftInDrawPile--;                // .. so unturned cards in draw pile is reduced by one
                }
                if (Table.debugText) {
                    System.out.println("Cards left in draw pile: " + Table.cardsLeftInDrawPile + " Cards removed from draw pile:" + Table.cardsRemovedFromDrawPile);
                }
                promptUser();
                return;
            }

            // The following is when King is moved from a column to another column
            System.out.println("Flyt " + selectedMove.getPlainText() + " fra søjle " + (selectedMove.getFromPosition() + 1) + " til søjle " + (selectedMove.getToPosition() + 1) + ".");
            int sizeColumnFrom = Table.position.get(selectedMove.getFromPosition()).size();         // Get size of column that the card(s) is moved from

            // Add the card group to target column
            for (int i = selectedMove.getCut(); i < sizeColumnFrom; i++) {                          // Iterate subgroup of cards to be moved
                Table.position.get(selectedMove.getToPosition()).add(Table.position.get(selectedMove.getFromPosition()).get(i)); // Copy cards into new spot
                System.out.println("Adding " + Table.position.get(selectedMove.getFromPosition()).get(i) + " to column " + selectedMove.getToPosition());
            }

            // Delete card group from source column
            for (int i = selectedMove.getCut(); i < sizeColumnFrom; i++) {                          // Iterate subgroup of cards and remove them from Table
                System.out.println("Deleting " + Table.position.get(selectedMove.getFromPosition()).get(selectedMove.getCut()) + " from column " + selectedMove.getFromPosition());
                Table.position.get(selectedMove.getFromPosition()).remove(selectedMove.getCut());

                if (Table.position.get(selectedMove.getFromPosition()).size() == 1 && Table.unseen[selectedMove.getFromPosition()] > 0) { // If column now empty
                    Table.unseen[selectedMove.getFromPosition()]--; // Remove an unseen card from column, if any unseen cards remain.
                    if (Table.debugText)
                        System.out.println("Unseen card removed from column " + selectedMove.getFromPosition());
                }
            }
            Table.justMoved = selectedMove.getCard();          // Remember last moved card
            promptUser();
            return;
        }

        if (selectedMove.getType() == 3) {          // If selected move type 3: Move card(s) from column to column
            Table.columnToColumn++;                 // Increment counter to keep track of not looping forever with column-to-column
            System.out.println("Du kan flytte kort fra søjle til søjle!");
            System.out.println("Flyt " + selectedMove.getPlainText() + " fra søjle " + (selectedMove.getFromPosition() + 1) + " til søjle " + (selectedMove.getToPosition() + 1) + ".");
            int sizeColumnFrom = Table.position.get(selectedMove.getFromPosition()).size();         // Get size of column that the card(s) is moved from

            // Add the card group to target column
            for (int i = selectedMove.getCut(); i < sizeColumnFrom; i++) {                          // Iterate subgroup of cards to be moved
                Table.position.get(selectedMove.getToPosition()).add(Table.position.get(selectedMove.getFromPosition()).get(i)); // Copy cards into new spot
                if (Table.debugText) {
                    System.out.println("Adding " + Table.position.get(selectedMove.getFromPosition()).get(i) + " to column " + selectedMove.getToPosition());
                }
            }

            // Delete card group from source column
            for (int i = selectedMove.getCut(); i < sizeColumnFrom; i++) {                          // Iterate subgroup of cards and remove them from Table
                Table.position.get(selectedMove.getFromPosition()).remove(selectedMove.getCut());
                if (Table.debugText) {
                    System.out.println("Deleting " + Table.position.get(selectedMove.getFromPosition()).get(selectedMove.getCut()) + " from column " + selectedMove.getFromPosition());
                }
                if (Table.position.get(selectedMove.getFromPosition()).size() == 1 && Table.unseen[selectedMove.getFromPosition()] > 0) { // If column now empty
                    Table.unseen[selectedMove.getFromPosition()]--; // Remove an unseen card from column, if any unseen cards remain.
                    if (Table.debugText)
                        System.out.println("Unseen card removed from column " + selectedMove.getFromPosition());
                }
            }
            Table.justMoved = selectedMove.getCard();          // Remember last moved card
            if (Table.debugText) System.out.println("Max column-to-column counter: " + Table.columnToColumn);
            promptUser();
            return;
        }

        if (selectedMove.getType() == 4) {          // If selected move is type 4: Move card from draw pile to a column
            Table.columnToColumn = 0;      // Reset counter
            System.out.println("Du kan flytte et kort fra trækbunken til en søjle!");
            System.out.println("Flyt " + selectedMove.getPlainText() + " til søjle " + (selectedMove.getToPosition() + 1) + ".");
            Table.cardsRemovedFromDrawPile++;       // A card is now permanently removed from draw pile
            int sizeDrawPile = Table.position.get(selectedMove.getFromPosition()).size();              // Get size of column or drawpile, from which the card is moved
            Table.position.get(selectedMove.getFromPosition()).remove(sizeDrawPile - 1);         // Remove card from Table
            if (Table.position.get(11).size() == 1) {       // If no visible cards in draw pile, one is turned over by player
                Table.cardsLeftInDrawPile--;
            }
            if (Table.debugText) {
                System.out.println("Cards left in draw pile: " + Table.cardsLeftInDrawPile + "  Cards removed from draw pile: " + Table.cardsRemovedFromDrawPile);
            }
            Table.justMoved = selectedMove.getCard();                                              // Remember last moved card
            promptUser();
            return;
        }
    }

    private void promptUser() {             // After AI has selected a move, the game pauses until the player has made the move.
        System.out.println("\nTast 'f' og tryk enter, når du har foretaget trækket.\nTast 'o' for at opgive spillet.");
        Scanner myScanner = new Scanner(System.in);
        String input = myScanner.nextLine();
        if (input.equals("o")) {
            Table.gameOn = false;
        }
    }

    private boolean checkIfWon() {
        for (int i = 0; i < 6; i++) {       // Loop through the columns
            if (Table.unseen[i] > 0) {      // Check if there are any columns, that still have unseen cards.
                return false;               // If so, the game is not yet finished.
            }
        }
        return true;                        // But if all unseen cards have been exposed, the game is solved.
    }

    private Move pickBestMove(ArrayList<Move> legalMoves) {
        return legalMoves.get(0);            // AI uncritically selects first move from legalMoves
    }
}
