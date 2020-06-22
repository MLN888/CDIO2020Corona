import java.util.ArrayList;


public class AI {

    private boolean fundetValg = false;                         // Flag used in selecting move
    OpenCVCardFile openCVCardFile = new OpenCVCardFile();       // Feature to enable communication with OpenCV
    UserInterface UI;
    private boolean needReset;
    private boolean resetNow;

    public AI(UserInterface ui) {
        this.UI = ui;
    }
    /*  The AI receives the ArrayList with legal move objects via the "thinkHard" method
     *   The AI selects the best move.
     *   The AI prints to console what the best move is
     *   The AI updates Table to reflect the move (always assuming that the player performs move correctly)
     *   When player wins or loses, game is stopped by setting Table.gameOn = false.
     * */

    public void thinkHard(ArrayList<Move> legalMoves) {
        System.out.println("\nTænker dybt...\n");

        if (checkIfWon()) {                         // Check if win-condition is true (All unseen cards exposed)
            System.out.println("Der er ikke flere uvendte kort. Spillet er vundet, men du kan fortsætte med byggebunkerne.");
            System.out.println("------------------------------------------------------------------------------------------");
        }

        
           for(int i=0; i<Table.unseen.length;i++){             // Copies unseen cards into unseenForOpenCV
                Table.unseenForOpenCV[i] = Table.unseen[i];     // OpenCV needs unseen card information BEFORE AI flips unseen cards
           }
        
        if (legalMoves.isEmpty()) {                     // Check if there are no moves left. IF not, a number of things may happen:

            if (checkIfWon()) {                         // Check if win-condition is true (All unseen cards exposed)
                System.out.println("Du har vundet! KABALEN ER GÅET OP!");
                Table.gameOn = false;
                return;
            }

            if (Table.cardsLeftInDrawPile == 0 && Table.shuffles == 0) {        // If draw pile is empty and no more shuffles allowed, the game must end
                System.out.println("Der er ikke flere mulige træk og du må ikke blande bunken igen.\nSpillet er slut.\nTak for denne gang!");
                Table.gameOn = false;
                return;
            }
            
            // If none of the above statements were true, then the player must simply draw a new card from the drawpile:
            System.out.println("Du skal vende et nyt kort fra trækbunken!");
            checkNeedToShuffle();
            resetNow = true;
            Table.newDrawPileCard = true;           // We need to inform OpenCV, that a new card arrives in draw pile
            Table.cardsLeftInDrawPile--;            // Deduct one from remaining cards in draw pile
            promptUser(0,UI.getStackSizeAtIndex(0) - 1, 12);
            UI.needFlip = true;
            UI.flipIndex = 11;
            return;
        }

        Move selectedMove = pickBestMove(legalMoves);   // This method is where AI-team implempents stuff to select the optimum move from the legalMoves-arrayList.


        if (selectedMove.getType() == 1) {              // If move is type 1: Move top card from column or drawpile to a foundation
            int unseen = Table.unseen[selectedMove.getFromPosition()];
            Table.columnToColumn = 0;      // Reset counter
            System.out.println("Du kan flytte et kort til en byggebunke!");
            if (selectedMove.getFromPosition() == 11) {         // If card comes from draw pile
                System.out.println("Flyt " + selectedMove.getPlainText() + " fra trækbunken til en byggebunke.");
                Table.cardsRemovedFromDrawPile++;               // A card is now permanently removed from draw pile
                if(Table.position.get(11).size() == 1)          //if drawpile is empty
                {
                    UI.needFlip = true;
                    UI.flipIndex = 11;
                }
                if (Table.position.get(11).size() == 1) {       // If no visible cards in draw pile, one is turned over by player
                    checkNeedToShuffle();
                    Table.cardsLeftInDrawPile--;
                    Table.newDrawPileCard = true;           // We need to inform OpenCV, that a new card arrives in draw pile
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
            promptUser(selectedMove.getFromPosition() + 1, UI.getStackSizeAtIndex(selectedMove.getFromPosition() + 1) - 1, selectedMove.getToPosition() + 1);
            if(UI.getStackSizeAtIndex(selectedMove.getFromPosition()+1) == unseen && unseen != 0)
            {
                UI.needFlip = true;
                UI.flipIndex = selectedMove.getFromPosition();
            }
            return;
        }

        if (selectedMove.getType() == 2) {              // If selected move is type 2: Move king from draw pile or column to empty spot
            int unseen = Table.unseen[selectedMove.getFromPosition()];
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
                    checkNeedToShuffle();
                    Table.cardsLeftInDrawPile--;                // .. so unturned cards in draw pile is reduced by one
                    Table.newDrawPileCard = true;           // We need to inform OpenCV, that a new card arrives in draw pile
                }
                if (Table.debugText) {
                    System.out.println("Cards left in draw pile: " + Table.cardsLeftInDrawPile + " Cards removed from draw pile:" + Table.cardsRemovedFromDrawPile);
                }
                promptUser(12, UI.getStackSizeAtIndex(12) - 1, selectedMove.getToPosition() + 1);
                if(Table.position.get(11).size() == 1)
                {
                    UI.needFlip = true;
                    UI.flipIndex = 11;
                }
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
            promptUser(selectedMove.getFromPosition() + 1, selectedMove.getCut() + unseen - 1, selectedMove.getToPosition() + 1);
            if(UI.getStackSizeAtIndex(selectedMove.getFromPosition()+1) == unseen)
            {
                UI.needFlip = true;
                UI.flipIndex = selectedMove.getFromPosition();
            }
            return;
        }

        if (selectedMove.getType() == 3) {          // If selected move type 3: Move card(s) from column to column
            Table.columnToColumn++;                 // Increment counter to keep track of not looping forever with column-to-column
            System.out.println("Du kan flytte kort fra søjle til søjle!");
            System.out.println("Flyt " + selectedMove.getPlainText() + " fra søjle " + (selectedMove.getFromPosition() + 1) + " til søjle " + (selectedMove.getToPosition() + 1) + ".");
            int sizeColumnFrom = Table.position.get(selectedMove.getFromPosition()).size();         // Get size of column that the card(s) is moved from
            int unseen = Table.unseen[selectedMove.getFromPosition()];

            // Add the card group to target column
            for (int i = selectedMove.getCut(); i < sizeColumnFrom; i++) {                          // Iterate subgroup of cards to be moved
                Table.position.get(selectedMove.getToPosition()).add(Table.position.get(selectedMove.getFromPosition()).get(i)); // Copy cards into new spot
                if (Table.debugText) {
                    System.out.println("Adding " + Table.position.get(selectedMove.getFromPosition()).get(i) + " to column " + selectedMove.getToPosition());
                }
            }

            // Delete card group from source column
            for (int i = selectedMove.getCut(); i < sizeColumnFrom; i++) {                          // Iterate subgroup of cards and remove them from Table
                if (Table.debugText) {
                    System.out.println("Deleting " + Table.position.get(selectedMove.getFromPosition()).get(selectedMove.getCut()) + " from column " + selectedMove.getFromPosition());
                }
                Table.position.get(selectedMove.getFromPosition()).remove(selectedMove.getCut());
                if (Table.position.get(selectedMove.getFromPosition()).size() == 1 && Table.unseen[selectedMove.getFromPosition()] > 0) { // If column now empty
                    Table.unseen[selectedMove.getFromPosition()]--; // Remove an unseen card from column, if any unseen cards remain.
                    if (Table.debugText)
                        System.out.println("Unseen card removed from column " + selectedMove.getFromPosition());
                }
            }
            Table.justMoved = selectedMove.getCard();          // Remember last moved card
            if (Table.debugText) System.out.println("Max column-to-column counter: " + Table.columnToColumn);
            promptUser(selectedMove.getFromPosition() + 1, selectedMove.getCut() + unseen - 1, selectedMove.getToPosition() + 1);
            if(UI.getStackSizeAtIndex(selectedMove.getFromPosition()+1) == unseen && unseen != 0)
            {
                UI.needFlip = true;
                UI.flipIndex = selectedMove.getFromPosition();
            }
            return;
        }

        if (selectedMove.getType() == 4) {          // If selected move is type 4: Move card from draw pile to a column
            Table.columnToColumn = 0;      // Reset counter
            System.out.println("Du kan flytte et kort fra trækbunken til en søjle!");
            System.out.println("Flyt " + selectedMove.getPlainText() + " til søjle " + (selectedMove.getToPosition() + 1) + ".");
            Table.cardsRemovedFromDrawPile++;       // A card is now permanently removed from draw pile
            int sizeDrawPile = Table.position.get(selectedMove.getFromPosition()).size();              // Get size of column or drawpile, from which the card is moved
            Table.position.get(selectedMove.getFromPosition()).remove(sizeDrawPile - 1);         // Remove card from Table
            Table.position.get(selectedMove.getToPosition()).add(selectedMove.getCard());       // Add card to target column
            
            if (Table.position.get(11).size() == 1) {       // If no visible cards in draw pile, one is turned over by player
                checkNeedToShuffle();
                Table.cardsLeftInDrawPile--;
                Table.newDrawPileCard = true;           // We need to inform OpenCV, that a new card arrives in draw pile
                UI.needFlip = true;
                UI.flipIndex = 11;
            }
           
            Table.justMoved = selectedMove.getCard();                                              // Remember last moved card
            if (Table.debugText) {
                System.out.println("Cards left in draw pile: " + Table.cardsLeftInDrawPile + "  Cards removed from draw pile: " + Table.cardsRemovedFromDrawPile);
            }
            Table.justMoved = selectedMove.getCard();    // Remember last moved card
            promptUser(12, UI.getStackSizeAtIndex(12) - 1, selectedMove.getToPosition() + 1);
            return;
        }

        if (selectedMove.getType() == 5) {      // Type 5: When AI force game engine to draw a new card even if other moves exist
            System.out.println("Du skal vende et nyt kort fra trækbunken!");
            if(Table.debugText) System.out.println("Type 5 move!");
            checkNeedToShuffle();
            resetNow = true;
            Table.cardsLeftInDrawPile--;            // Deduct one from remaining cards in draw pile
            Table.newDrawPileCard = true;           // We need to inform OpenCV, that a new card arrives in draw pile
            if (Table.debugText) {
                System.out.println("Cards left in draw pile: " + Table.cardsLeftInDrawPile + " Cards removed from draw pile:" + Table.cardsRemovedFromDrawPile);
            }
            promptUser(0,UI.getStackSizeAtIndex(0) - 1, 12);
            UI.needFlip = true;
            UI.flipIndex = 11;
            return;
        }


    }

    private void promptUser(int si, int sr, int di) {             // After AI has selected a move, the game pauses until the player has made the move.
        openCVCardFile.skrivTilOpenCV();

        int startIndex = si;
        int startReach = sr;
        int destIndex = di;
        if(needReset)
        {
            UI.resetDeck(resetNow);
            if(resetNow)startReach = UI.getStackSizeAtIndex(0) - 1;
        }
        while(!UI.inputMade){
            UI.moveSug(startIndex, startReach, destIndex);
        }
        UI.makeMove(startIndex, startReach, destIndex);
        UI.inputMade = false;

        if(needReset)
        {
            UI.resetSugRemove();
            needReset = false;
            resetNow = false;
        }

        
    }

    private boolean checkIfWon() {
        for (int i = 0; i < 7; i++) {       // Loop through the columns
            if (Table.unseen[i] > 0) {      // Check if there are any columns, that still have unseen cards.
                return false;               // If so, the game is not yet finished.
            }
        }
        return true;                        // But if all unseen cards have been exposed, the game is solved.
    }

    private void checkNeedToShuffle() {     // We check if draw pile has no unturned cards left

        if (Table.cardsLeftInDrawPile == 0 && Table.position.get(11).size()==1){    // If no cards to shuffle, return.
            return;
        }

        if (Table.cardsLeftInDrawPile <= 0) {        // If draw pile is empty.
            System.out.println("Ikke flere kort i trækbunken! Bland trækbunken og vend et nyt kort.");
            Table.shuffles--;                       // Draw pile is shuffled. Deduct one from allowed shuffles
            Table.position.get(11).clear();         // Empty draw pile
            Table.position.get(11).add("XX");       // Add an XX to indicate empty
            Table.newDrawPileCard = true;           // We need to inform OpenCV, that a new card arrives in draw pile
            Table.cardsLeftInDrawPile = 24 - Table.cardsRemovedFromDrawPile;       // Reinitialize drawpile to original amount (24) minus cards removed
            UI.reshuffleSug();
            needReset = true;
        }
        if (Table.debugText) {
            System.out.println("Cards left in draw pile: " + Table.cardsLeftInDrawPile + " Cards removed from draw pile:" + Table.cardsRemovedFromDrawPile);
        }
    }

    private Move pickBestMove(ArrayList<Move> legalMoves) {


        if(Table.debugText) System.out.println("Size of legalMoves before selecting anything: "+legalMoves.size());
        Move currentChosen = legalMoves.get(0);
        for (int i = 0; i < legalMoves.size(); i++) {           // Vi itererer igennem alle lovlige moves
            if(Table.debugText) System.out.println("Checking legalmove:" + legalMoves.get(i)+ "  Unseens:" + Table.unseen[legalMoves.get(i).getFromPosition()]);

            currentChosen = checkForEs(legalMoves, i, currentChosen);           //Metode der finder et es eller en 2'er der kan placeres i byggebunken
            if (fundetValg) {
                fundetValg = false;
                if(Table.debugText) System.out.println("Priority move: Ace or two moved to foundation pile");
                return currentChosen;
            }

            currentChosen = checkForColumnKings(legalMoves, i, currentChosen);        //Metode der finder konger fra søjler med unseen kort.
            if (fundetValg) {
                fundetValg = false;
                if(Table.debugText) System.out.println("Priority move: Moving king to expose unseen card");
                return currentChosen;
            }

            currentChosen = checkForFriKortTraek(legalMoves, i, currentChosen); //Metode der finder træk der vender usete kort
            if(fundetValg){
                fundetValg = false;
                if(Table.debugText) System.out.println("Priority move: Moving card(s) to expose unseen card");
                return currentChosen;
            }

            currentChosen = checkForTraekKings(legalMoves, i, currentChosen);   //Metode der tager konge fra trækbunke og ligger på en fri plads
            if (fundetValg) {
                fundetValg = false;
                if(Table.debugText) System.out.println("Priority move: Moving king to empty column");
                return currentChosen;
            }

            currentChosen = checkForExposeEmptyColumn(legalMoves, i, currentChosen);   //Metode der tager undersøger om vi skal lave en tom plads
            if (fundetValg) {
                fundetValg = false;
                if(Table.debugText) System.out.println("Priority move: Exposing empty spot to make room for king");
                return currentChosen;
            }
        }

        // Hvis Vi kan stadig kan blande bunken & trække kort, og der kun er type 1 træk til rådighed, vil vi hellere trække nyt kort fra trækbunken
        if(Table.shuffles != 0 && Table.cardsLeftInDrawPile != 0 && legalMoves.get(legalMoves.size() - 1).getType() == 1){
            if(Table.debugText) System.out.println("Priority move: Only type 1 moves available. Drawing card instead.");
            currentChosen.setType(5);   // Type 5 = træk nyt kort
            return currentChosen;
        }

        int counter = 0;
        for (int i = 0; i < legalMoves.size(); i++) {   // Vi itererer lovlige træk
            if(legalMoves.get(i).getType() == 1){       // Har vi fat i en type 1?
                counter = counter + 1;                  // Så tæller vi counteren 1 op
            }
            if(counter == legalMoves.size()){           // Hvis alle træk har været type 1
                if(Table.debugText) System.out.println("Priority: No shuffles, no cards in drawpile, so we accept type 1");
                return legalMoves.get(0);               // Så vælger vi gerne type 1
            }
        }
        
        for (int i = 0; i < legalMoves.size(); i++) {   // Vi itererer lovlige træk
            if(legalMoves.get(i).getType() != 1  && legalMoves.get(i).getType() != 3 ){       // Det første træk som IKKE er type 1 eller 3...
                currentChosen = legalMoves.get(i);      // ... vælger vi at bruge.
                if(Table.debugText) System.out.println("Picking first move that is NOT type 1 or type 3");
                return currentChosen;
            }
        }

        if (legalMoves.get(0).getType()==3){            // Fix problem where AI moves a card from column to column repeatedly
            legalMoves.get(0).setType(5);
            if(Table.debugText) System.out.println("Type 3 afvist for at undgå dumme søjle-til-søjle. Træk kort i stedet");
            return legalMoves.get(0);
        }

        if (Table.debugText){
        System.out.println("Ingen specielle træk er foretrukket. Vi vælger default legalMoves.get(0)");
        System.out.println(legalMoves.get(0).toString());
        }

        return legalMoves.get(0);   
    }// AI uncritically selects first move from legalMoves



    //Metode der finder et es eller en 2'er der kan ligges i en byggebunke
    private Move checkForEs(ArrayList<Move> legalMoves, int i, Move currentChosen) {
        if (legalMoves.get(i).getType() == 1 && (legalMoves.get(i).getCard().startsWith("A") || legalMoves.get(i).getCard().startsWith("2"))) {
            currentChosen = legalMoves.get(i);
            fundetValg = true;
            return currentChosen;
        }
        return legalMoves.get(0);
    }

    // Metode der finder konger fra søjler med unseen kort.
    private Move checkForColumnKings(ArrayList<Move> legalMoves, int i, Move currentChosen){
        if (legalMoves.get(i).getType() == 2) {
            if (Table.unseen[legalMoves.get(i).getFromPosition()] > 0 && legalMoves.get(i).getCut() == 1) {
                currentChosen = legalMoves.get(i);
                fundetValg = true;
                return currentChosen;
            }
        }
        return legalMoves.get(0);
    }

    //Metode der tjekker konge fra traekbunke og ligger på en fri plads
    private Move checkForTraekKings(ArrayList<Move> legalMoves, int i, Move currentChosen) {
        if (legalMoves.get(i).getFromPosition() == 11 && legalMoves.get(i).getCard().startsWith("K")) {
            fundetValg = true;
            return currentChosen;
        }
        return legalMoves.get(0);
    }

    // Metode der tjekker for om vi kan frigive en tom søjle OG der er en konge til rådighed et sted
    private Move checkForFriKortTraek(ArrayList<Move> legalMoves, int i, Move currentChosen){
        if(Table.unseen[legalMoves.get(i).getFromPosition()] > 0 && legalMoves.get(i).getCut() == 1){
            currentChosen = legalMoves.get(i);
            fundetValg = true;
            return currentChosen;
        }
        return legalMoves.get(0);
    }

    // Hjælpemetode der tjekker om der er en konge til rådighed et sted
    private Move checkForExposeEmptyColumn(ArrayList<Move> legalMoves, int i, Move currentChosen){
        //If we have type 3 move && we are looking to move cut 1 && no unseen cards && kings available elsewhere:
        if(legalMoves.get(i).getCut()==1 && legalMoves.get(i).getType()==3 && Table.unseen[legalMoves.get(i).getFromPosition()]<1 && kingsAvailable()){
            currentChosen = legalMoves.get(i);
            fundetValg = true;
            return currentChosen;
        }
        return legalMoves.get(0);
    }

    // Method that checks if a King is available somewhere
    private boolean kingsAvailable(){
        for (int i = 0; i < 7; i++) {
            if(Table.position.get(i).size()>1){
                if(Table.position.get(i).get(1).startsWith("K") && Table.unseen[i]>0){ //
                return true;
            }
        }
    }

        int sizeOfDrawpile = Table.position.get(11).size();
        System.out.println("King in top of drawpile: "+Table.position.get(11).get(sizeOfDrawpile-1).startsWith("K"));
        if (Table.position.get(11).get(sizeOfDrawpile-1).startsWith("K")){
            return true;
        }

        return false;
    }
}
