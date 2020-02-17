## A tool for analyzing Moodle logs

### Running the program

For running the program Java 13.0.1 and JavaFX 13.0.1 are needed

- Java 13.0.1 can be downloaded from - 
- JavaFX 13.0.1 can be downloaded from - 

There are two different options for running the program

1. Cloning the repository and running Main.java
   * In order to run the Main class there are a few libraries that need to be added, the libraries can be found in the Bachelors/Librarys directory
     * GemBox Spreadsheet 4.5
     * Apache POI 4.1.1
     * Jsoup 1.12.1
     * JavaFX 13.0.1
   * When the libraries have been added just compile and run the program

2. Running the Programm.jar file in Bachelors/out/artifacts/Programm_jar directory
   * Open command prompt / terminal
   * Go to the directory where the Programm.jar file is located
   * Run the command  java --module-path %path_to_fx% --add-modules javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.web -jar Programm.jar