import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class OpenCVCardFile {

  String cardsToOpenCV = "";

  OpenCVCardFile() {
  }

  public void skrivTilOpenCV() {
    createOpenCVstring(); // metode 1 (Genererer String med kortene)
    createOpenCVfile();   // metode 2 
    return;
  }

  private void createOpenCVstring() {

    int topCardOfDrawpileIndex = Table.position.get(11).size();
    String topCardofDrawpile = Table.position.get(11).get(topCardOfDrawpileIndex - 1);
    this.cardsToOpenCV.concat(topCardofDrawpile);
    this.cardsToOpenCV.concat(",");

    for (int i = 0; i < 7; i++) {
      int topCardOfColumnIndex = Table.position.get(i).size();
      String topCardofColumni = Table.position.get(i).get(topCardOfColumnIndex);
      this.cardsToOpenCV.concat(topCardofColumni);
      if (i != 6) {
        this.cardsToOpenCV.concat(",");
      }
    }

  }

  private void createOpenCVfile(){
    // Lækker kode fra Anders

    BufferedWriter bw = null;
    //PrintWriter pw = null;    Bruges til append 
    try {
        //Definerer string som skal skrives til filen
        String string = "Test";
        // filnavn og path (i dette tilfælde bliver den lagt i workspace)
        File file = new File("myfile.txt"); 
        
        // Laver fil hvis den ikke eksisterer
        if (!file.exists()) {
            file.createNewFile();
        }

        // Skaber file writer og bruger til at definere bw
        FileWriter fw = new FileWriter(file);
        bw = new BufferedWriter(fw);
        // pw = new PrintWriter(bw);   Bruges til append
        
        // Skriver til fil
        bw.write(string);
        // pw.println(string);   Append string til fil
        if(Table.debugText) System.out.println("Filen er blevet skrevet " + 
        string);


    } catch (IOException e) {
        e.printStackTrace();
    }

    // Bruger finally til at rydde op. (lukke filen i dette tilfælde)
    finally {
        try {
            if (bw != null)
                bw.close();
                //pw.close();   close append printer
        } catch (Exception ex) {
            System.out.println("Error in closing the BufferedWriter" + ex);
        }
    }



  }

}