import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        Table.debugText = true;      // Sets if you want various debug print-outs to be displayed

        Table.createTable();         // Instantiates the Table. This is the primary data structure of the game board. Only one instance can exist.
        ArrayList<Move> legalMoves;  // This ArrayList contains all legal moves found in the form of Move-objects
        FindLegalMoves findLegalMoves = new FindLegalMoves();   // New findLegalMoves object.
        UserInterface UI = new UserInterface(true);
        AI ai = new AI(UI);                                       // New AI object
        FileHandler fileHandler = new FileHandler();            // New FileHandler object

        while (Table.gameOn) {                          // Game loops forever while 'gameOn' is true
            fileHandler.updateTable();                  // This method reads file created by OpenCV and updates Table accordingly
            UI.checkNewData();
            legalMoves = findLegalMoves.findMoves();    // Call method findMoves(). Returns an ArrayList of legal Moves
            ai.thinkHard(legalMoves);                   // Call method thinkHard. Argument is the ArrayList of legal Moves
        }
    }
}
