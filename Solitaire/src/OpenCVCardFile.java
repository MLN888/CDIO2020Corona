import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class OpenCVCardFile {

  private String cardsToOpenCV = "";

  OpenCVCardFile() {
  }

  public void skrivTilOpenCV() {
    createOpenCVstring(); // Genererer String med kortene
    createOpenCVfile(); // Skriver den genererede string til fil
    this.cardsToOpenCV = "";
    return;
  }

  private void createOpenCVstring() {

    int topCardOfDrawpileIndex = Table.position.get(11).size();       // Get size of draw pile
    String topCardofDrawpile = Table.position.get(11).get(topCardOfDrawpileIndex - 1);    // Grab top card of drawpile

    if(Table.cardsLeftInDrawPile==0 && Table.position.get(11).size()==1){
      cardsToOpenCV = cardsToOpenCV.concat("FF");
    } else 

    if(Table.newDrawPileCard){                      // If a new card has been turned over in the drawpile...
      cardsToOpenCV = cardsToOpenCV.concat("UU");   // ... OpenCV wants a "UU" in the file 
      Table.newDrawPileCard = false;                // Resetting boolean, ready for next time a new card arrives
    } else {                                        // else we just put the actual card there.
    cardsToOpenCV = cardsToOpenCV.concat(topCardofDrawpile);    // Concatenate top card of drawpile to String
    }
    cardsToOpenCV = cardsToOpenCV.concat(",");                  // Add a comma


    for (int i = 0; i < 7; i++) {                                     // Iterate the 7 columns
      int topCardOfColumnIndex = Table.position.get(i).size();        // Get size of current column
      String topCardofColumni = Table.position.get(i).get(topCardOfColumnIndex - 1);    // Grab top card in column i 

      if (Table.unseenForOpenCV[i] > 0 && topCardofColumni.equals("XX")) {     // If top card is "XX" and there is unseen cards...
        cardsToOpenCV = cardsToOpenCV.concat("UU");                   // ... add "UU" instead
      } else {
        cardsToOpenCV = cardsToOpenCV.concat(topCardofColumni);       // Else: Just add whatever was the top card to the String
      }

      if (i != 6) {                                   // Only if column is not the last column....
        cardsToOpenCV = cardsToOpenCV.concat(",");    // ... add a comma
      }
    }
  }

  private void createOpenCVfile() {
    // Lækker kode fra Anders

    BufferedWriter bw = null;
    // PrintWriter pw = null; Bruges til append
    try {
      // Definerer string som skal skrives til filen
      String string = cardsToOpenCV;
      // filnavn og path (i dette tilfælde bliver den lagt i workspace)
      File file = new File("fileToOpenCV.txt");

      // Laver fil hvis den ikke eksisterer
      if (!file.exists()) {
        file.createNewFile();
      }

      // Skaber file writer og bruger til at definere bw
      FileWriter fw = new FileWriter(file,false); // false indicates that we want to overwrite, not append
      bw = new BufferedWriter(fw);
      // pw = new PrintWriter(bw); Bruges til append

      // Skriver til fil
      bw.write(string);
      // pw.println(string); Append string til fil
      
      if(Table.debugText) System.out.println("File to OpenCV written: "+this.cardsToOpenCV);

    } catch (IOException e) {
      e.printStackTrace();
    }

    // Bruger finally til at rydde op. (lukke filen i dette tilfælde)
    finally {
      try {
        if (bw != null)
          bw.close();
        // pw.close(); close append printer
      } catch (Exception ex) {
        System.out.println("Error in closing the BufferedWriter" + ex);
      }
    }
  }

  public String getCardsToOpenCV(){
    return this.cardsToOpenCV;
  }
}