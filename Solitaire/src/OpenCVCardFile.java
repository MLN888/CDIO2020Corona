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
  }

}