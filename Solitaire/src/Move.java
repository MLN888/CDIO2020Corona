public class Move {

    private int fromPosition;   // Which column (or draw pile) can we move something from
    private int toPosition;     // Which column or foundation pile can we move something to
    private int cut;            // Where to cut the column when moving a group of cards. 1 = top card
    private int type;           // What type of move we are doing (see below)
    // Type 1 : move card from a column or draw pile to a foundation pile
    // Type 2 : move a king from a column or the draw pile to an empty column
    // Type 3 : move one or more cards from a column to a column
    // Type 4 : move the card from the draw pile to a column
    private String card;        // The 'raw' card directly from the file. (3D, 6S, etc)
    private String plainText;   // The 'raw' card converted to nice plaintext such as "spar seks".

    public Move(int fromPosition, int toPosition, int cut, int type, String card, String plainText) {
        this.fromPosition = fromPosition;
        this.toPosition = toPosition;
        this.cut = cut;
        this.type = type;
        this.card = card;
        this.plainText = plainText;
    }

    public int getFromPosition() {
        return fromPosition;
    }

    public int getToPosition() {
        return toPosition;
    }

    public int getCut() {
        return cut;
    }

    public int getType() {
        return type;
    }

    public String getCard() {
        return card;
    }

    public String getPlainText() {
        return plainText;
    }

    @Override
    public String toString() {      // OMG the toString-method is overrided. Amazing Java skills.
        return "Move{" +
                "fromPosition=" + fromPosition +
                ", toPosition=" + toPosition +
                ", cut=" + cut +
                ", type=" + type +
                ", card='" + card + '\'' +
                ", plainText='" + plainText + '\'' +
                '}';
    }
}
