package MainLogic;

import FilesProcessing.GradesProcessing;
import FilesProcessing.MoodleLogsProcessing;
import FilesProcessing.StudentInfoProcessing;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class Main extends Application {

    private Scene sceneMainView, sceneLogContext, sceneLogName, sceneFilterByGroup, sceneFilterByName,  sceneTimeframe, sceneDisplaySelectedData, sceneAnalyseData;
    private File logFile, studentFile, gradeFile;
    private MoodleLogsProcessing moodleLogsProcessing;
    private StudentInfoProcessing studentInfoProcessing;
    private GradesProcessing gradesProcessing;
    private Thread threadDisplayEventContext, threadDisplayEventName, threadDisplayStudentGroup, threadDisplayStudentName, threadDisplatFilteredResults;
    private ListView<String> listViewLogContext = new ListView<>();
    private ListView<String> listViewLogName = new ListView<>();
    private ListView<String> listViewStudentName = new ListView<>();
    private ListView<String> listViewStudentGroup = new ListView<>();
    private TableView tableViewSelectedData = new TableView();
    private ObservableList<String> observableListLogContext, observableListLogEvent, observableListStudentName, observableListStudentGroup;
    private LocalDate localDateFrom, localDateTo;
    private LocalTime localTimeFrom, localTimeTo;
    private List<Log> filteredResults = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    public Main() { }

    @Override
    public void start(Stage stage) {

        // All the used TitledPanes
        TitledPane titledPaneMainView = new TitledPane("Vajalike failide sisestamine", new Label());
        TitledPane titledPaneEventContext = new TitledPane("Sündmuse konteksti elementide valimine", new Label());
        TitledPane titledPaneEventName = new TitledPane("Sündmuse nime elementide valimine", new Label());
        TitledPane titledPaneStudentGroups = new TitledPane("Õpilaste gtuppide valimine", new Label());
        TitledPane titledPaneStudentName = new TitledPane("Õpilaste nimede valimine", new Label());
        TitledPane titledPaneTimeframe = new TitledPane("Ajaraami valimine", new Label());
        TitledPane titledPaneDisplayTable = new TitledPane("Kõik valitud andmed", new Label());
        // Making all the TitledPanes not collapsible
        titledPaneMainView.setCollapsible(false);
        titledPaneEventContext.setCollapsible(false);
        titledPaneEventName.setCollapsible(false);
        titledPaneStudentGroups.setCollapsible(false);
        titledPaneStudentName.setCollapsible(false);
        titledPaneTimeframe.setCollapsible(false);
        titledPaneDisplayTable.setCollapsible(false);
        // All rhe used GridPanes
        GridPane gridPaneMainView = new GridPane();
        GridPane gridPaneEventContext = new GridPane();
        GridPane gridPaneEventName = new GridPane();
        GridPane gridPaneStudentGroups = new GridPane();
        GridPane gridPaneStudentName = new GridPane();
        GridPane gridPaneTimeframe = new GridPane();
        GridPane gridPaneDisplayTable = new GridPane();
        // All the used Labels
        Label labelStudent = new Label("Õpilaste andmed (.xls) ");
        Label labelGrade = new Label("Õpilaste hinded (.xlsx) ");
        Label labelLog = new Label("Moodle'i logid (.csv) ");
        Label labelLogFileName = new Label();
        Label labelStudentFileName =  new Label();
        Label labelGradeFileName = new Label();
        Label labelEventContext = new Label("Sündmuse kontekst");
        Label labelEventName = new Label("Sündmuse nimi");
        Label labelStudentGroups = new Label("Õpilaste grupid");
        Label labelStudentNames = new Label("Õpilaste nimed");
        Label labelDateFrom = new Label("Alguskuupäev ");
        Label labelDateTo = new Label("Lõpukuupäev ");
        Label labelTableName = new Label("Valitud andmed: ");
        // All the used Buttons
        Button buttonStudent = new Button("Ava õpilaste fail");
        Button buttonGrade = new Button("Ava hinnete fail");
        Button buttonLog = new Button("Ava logide fail");
        Button buttonSubmit = new Button("Edasi");
        Button buttonEventNameForward = new Button("Edasi");
        Button buttonMainViewBack = new Button("Tagasi");
        Button buttonFilterByStudentGroupForward = new Button("Edasi");
        Button buttonEventContextBack = new Button("Tagasi");
        Button buttonFilterByStudentForward = new Button("Edasi");
        Button buttonEventNameBack = new Button("Tagasi");
        Button buttonDisplayTimeFrame = new Button("Edasi");
        Button buttonFilterByStudentGroupsBack = new Button("Tagasi");
        Button buttonDisplayAllDataForward = new Button("Edasi");
        Button buttonFilterByNameBack = new Button("Tagasi");
        // Making ListViews and TableView selection models to multiple
        listViewLogContext.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listViewLogName.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listViewStudentGroup.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listViewStudentName.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableViewSelectedData.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // Making ListViews and TableView width to 900
        listViewLogContext.setMinWidth(900);
        listViewLogName.setMinWidth(900);
        listViewStudentGroup.setMinWidth(900);
        listViewStudentName.setMinWidth(900);
        tableViewSelectedData.setMinWidth(900);


        // Create the main view in order to input nessecary files
        FileChooser fileChooserLog = new FileChooser();
        FileChooser fileChooserStudent = new FileChooser();
        FileChooser fileChooserGrade = new FileChooser();
        FileChooser.ExtensionFilter extFilterLog = new FileChooser.ExtensionFilter(".csv files (*.csv)", "*.csv");
        FileChooser.ExtensionFilter extFilterStudent = new FileChooser.ExtensionFilter(".xls files (*.xls)", "*.xls");
        FileChooser.ExtensionFilter extFilterGrade = new FileChooser.ExtensionFilter(".xlsx files (*.xlsx)", "*.xlsx");
        fileChooserLog.setTitle("Ava logide fail ");
        fileChooserStudent.setTitle("Ava õpilaste fail ");
        fileChooserGrade.setTitle("Ava hinnete fail ");
        fileChooserLog.getExtensionFilters().add(extFilterLog);
        fileChooserStudent.getExtensionFilters().add(extFilterStudent);
        fileChooserGrade.getExtensionFilters().add(extFilterGrade);
        gridPaneMainView.add(labelStudent, 0, 0);
        gridPaneMainView.add(labelGrade, 0, 1 );
        gridPaneMainView.add(labelLog,0, 2);
        gridPaneMainView.add(labelStudentFileName, 1, 0);
        gridPaneMainView.add(labelGradeFileName, 1, 1 );
        gridPaneMainView.add(labelLogFileName,1, 2);
        gridPaneMainView.add(buttonStudent, 2, 0);
        gridPaneMainView.add(buttonGrade, 2, 1 );
        gridPaneMainView.add(buttonLog,2, 2);
        gridPaneMainView.add(buttonSubmit, 2, 3);
        gridPaneMainView.setVgap(15);
        gridPaneMainView.setHgap(200);
        titledPaneMainView.setContent(gridPaneMainView);
        sceneMainView = new Scene(titledPaneMainView, 900, 500);

        buttonStudent.setOnAction(e -> {
            studentFile = fileChooserStudent.showOpenDialog(stage);
            if (studentFile != null) {
                labelStudentFileName.setText(studentFile.getName() + " ");
            }
        });
        buttonGrade.setOnAction(e -> {
            gradeFile = fileChooserGrade.showOpenDialog(stage);
            if (gradeFile != null) {
                labelGradeFileName.setText(gradeFile.getName() + " ");
            }
        });
        buttonLog.setOnAction(e -> {
            logFile = fileChooserLog.showOpenDialog(stage);
            if (logFile != null) {
                labelLogFileName.setText(logFile.getName() + " ");
            }
        });
        buttonSubmit.setOnAction(e -> {
            Alert alertFileNotFound = null;
            try {
                moodleLogsProcessing = new MoodleLogsProcessing();
                moodleLogsProcessing.processLogs(logFile);
            }
            catch (NullPointerException np) {
                alertFileNotFound = new Alert(Alert.AlertType.ERROR);
                alertFileNotFound.setTitle("Viga!");
                alertFileNotFound.setHeaderText("Logide faili pole valitud!");
                alertFileNotFound.setContentText("Pead sisestama moodle'i logide faili.");
                alertFileNotFound.show();
            }
            try {
                studentInfoProcessing = new StudentInfoProcessing();
                studentInfoProcessing.processStudents(studentFile);
            }
            catch (NullPointerException np) {
                alertFileNotFound = new Alert(Alert.AlertType.ERROR);
                alertFileNotFound.setTitle("Viga!");
                alertFileNotFound.setHeaderText("Õpilaste faili pole valitud!");
                alertFileNotFound.setContentText("Pead sisestama õpilaste faili.");
                alertFileNotFound.show();
            }
            try {
                gradesProcessing = new GradesProcessing();
                gradesProcessing.processGrades(gradeFile);
            }
            catch (NullPointerException np) {
                alertFileNotFound = new Alert(Alert.AlertType.ERROR);
                alertFileNotFound.setTitle("Viga!");
                alertFileNotFound.setHeaderText("Hinnete faili pole valitud!");
                alertFileNotFound.setContentText("Pead sisestama hinnete faili.");
                alertFileNotFound.show();
            }
            if (alertFileNotFound == null) {
                threadDisplayEventContext = new Thread(displayEventContext);
                threadDisplayEventContext.start();
                stage.setScene(sceneLogContext);
            }
        });

        // Create the event context menu for choosing different contexts
        gridPaneEventContext.add(labelEventContext, 0, 0);
        gridPaneEventContext.add(listViewLogContext, 0, 1, 2, 1);
        gridPaneEventContext.add(buttonMainViewBack, 0, 2);
        gridPaneEventContext.add(buttonEventNameForward, 1, 2);
        gridPaneEventContext.setVgap(15);
        titledPaneEventContext.setContent(gridPaneEventContext);
        sceneLogContext = new Scene(titledPaneEventContext, 900, 500);

        buttonEventNameForward.setOnAction(e -> {
            observableListLogContext = listViewLogContext.getSelectionModel().getSelectedItems();
            if (observableListLogContext.isEmpty()) {
                observableListLogContext = listViewLogContext.getItems();
            }
            threadDisplayEventName = new Thread(displayEventName);
            threadDisplayEventName.start();
            stage.setScene(sceneLogName);

        });
        buttonMainViewBack.setOnAction(e -> {
            moodleLogsProcessing.clearAllLogData();
            gradesProcessing.clearAllGradeData();
            studentInfoProcessing.clearAllStudentData();
            labelLogFileName.setText("");
            labelGradeFileName.setText("");
            labelStudentFileName.setText("");
            logFile = null;
            gradeFile = null;
            studentFile = null;
            listViewLogContext = new ListView<>();
            observableListLogContext = FXCollections.observableArrayList();
            stage.setScene(sceneMainView);
        });

        // Create the event name menu for choosing different event names
        gridPaneEventName.add(labelEventName, 0, 0);
        gridPaneEventName.add(listViewLogName, 0, 1, 2, 1);
        gridPaneEventName.add(buttonEventContextBack, 0, 2);
        gridPaneEventName.add(buttonFilterByStudentGroupForward, 1, 2);
        gridPaneEventName.setVgap(15);
        titledPaneEventName.setContent(gridPaneEventName);
        sceneLogName = new Scene(titledPaneEventName, 900, 500);

        buttonFilterByStudentGroupForward.setOnAction(e -> {
            observableListLogEvent = listViewLogName.getSelectionModel().getSelectedItems();
            if (observableListLogEvent.isEmpty()) {
                observableListLogEvent = listViewLogName.getItems();
            }
            stage.setScene(sceneFilterByGroup);
            new Thread(displayStudentGroup).start();
        });
        buttonEventContextBack.setOnAction(e -> {
            listViewLogName = new ListView<>();
            observableListLogEvent = FXCollections.observableArrayList();
            stage.setScene(sceneLogContext);
        });

        // Create the student groups menu for choosing different groups
        gridPaneStudentGroups.add(labelStudentGroups, 0, 0);
        gridPaneStudentGroups.add(listViewStudentGroup, 0, 1, 2, 1);
        gridPaneStudentGroups.add(buttonEventNameBack, 0, 2);
        gridPaneStudentGroups.add(buttonFilterByStudentForward, 1, 2);
        gridPaneStudentGroups.setVgap(15);
        titledPaneStudentGroups.setContent(gridPaneStudentGroups);
        sceneFilterByGroup = new Scene(titledPaneStudentGroups, 900, 500);

        buttonFilterByStudentForward.setOnAction(e -> {
            observableListStudentGroup = listViewStudentGroup.getSelectionModel().getSelectedItems();
            if (observableListStudentGroup == null) {
                observableListStudentGroup = listViewStudentGroup.getItems();
            }
            stage.setScene(sceneFilterByName);
            new Thread(displayStudentName).start();
        });
        buttonEventNameBack.setOnAction(e -> stage.setScene(sceneLogName));

        // Create the student names menu for choosing different names
        gridPaneStudentName.add(labelStudentNames, 0, 0);
        gridPaneStudentName.add(listViewStudentName, 0, 1, 2, 1);
        gridPaneStudentName.add(buttonFilterByStudentGroupsBack, 0, 2);
        gridPaneStudentName.add(buttonDisplayTimeFrame, 1, 2);
        gridPaneStudentName.setVgap(15);
        titledPaneStudentName.setContent(gridPaneStudentName);
        sceneFilterByName = new Scene(titledPaneStudentName, 900, 500);

        buttonDisplayTimeFrame.setOnAction(e -> {
            observableListStudentName = listViewStudentName.getSelectionModel().getSelectedItems();
            if (observableListStudentName == null) {
                observableListStudentName = listViewStudentName.getItems();
            }
            stage.setScene(sceneTimeframe);
        });
        buttonFilterByStudentGroupsBack.setOnAction(e -> stage.setScene(sceneFilterByGroup));

        //Create the date view in order to choose a timeframe
        DatePicker datePickerFrom = new DatePicker();
        DatePicker datePickerTo = new DatePicker();
        TextField timePickerFrom = new TextField();
        TextField timePickerTo = new TextField();
        gridPaneTimeframe.add(labelDateFrom, 0, 0);
        gridPaneTimeframe.add(labelDateTo, 1, 0);
        gridPaneTimeframe.add(datePickerFrom, 0, 1);
        gridPaneTimeframe.add(datePickerTo, 1, 1);
        gridPaneTimeframe.add(buttonFilterByNameBack, 0, 2);
        gridPaneTimeframe.add(buttonDisplayAllDataForward, 1, 2);
        gridPaneTimeframe.add(timePickerFrom, 0, 3);
        gridPaneTimeframe.add(timePickerTo, 1, 3);
        gridPaneTimeframe.setVgap(15);
        titledPaneTimeframe.setContent(gridPaneTimeframe);
        sceneTimeframe = new Scene(titledPaneTimeframe, 900, 500);
        buttonDisplayAllDataForward.setOnAction(e -> {
            localDateFrom = datePickerFrom.getValue();
            localDateTo = datePickerTo.getValue();
            localTimeFrom = null;
            localDateTo = null;
            stage.setScene(sceneDisplaySelectedData);
            new Thread(displaySelectedData).start();
        });
        buttonFilterByNameBack.setOnAction(e -> stage.setScene(sceneFilterByName));

        //Create the table with all the information
        TableColumn<String, Log> columnLogTime = new TableColumn<>("Aeg");
        TableColumn<String, Log> columnStudentName = new TableColumn<>("Nimi");
        TableColumn<String, Log> columnEventContext = new TableColumn<>("Sündmuse kontekst");
        TableColumn<String, Log> columnEventName = new TableColumn<>("Sündmuse nimi");
        TableColumn<String, Log> columnStudentGroup = new TableColumn<>("Rühm");
        columnLogTime.setMinWidth(100);
        columnStudentName.setMinWidth(115);
        columnEventContext.setMinWidth(275);
        columnEventName.setMinWidth(275);
        columnStudentGroup.setMinWidth(100);
        columnLogTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        columnStudentName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        columnEventContext.setCellValueFactory(new PropertyValueFactory<>("eventContext"));
        columnEventName.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        columnStudentGroup.setCellValueFactory(new PropertyValueFactory<>("studentGroup"));
        tableViewSelectedData.getColumns().add(columnLogTime);
        tableViewSelectedData.getColumns().add(columnStudentName);
        tableViewSelectedData.getColumns().add(columnEventContext);
        tableViewSelectedData.getColumns().add(columnEventName);
        tableViewSelectedData.getColumns().add(columnStudentGroup);
        gridPaneDisplayTable.add(labelTableName, 0, 0);
        gridPaneDisplayTable.add(tableViewSelectedData, 0, 1, 2, 1);
        gridPaneDisplayTable.setVgap(15);
        titledPaneDisplayTable.setContent(gridPaneDisplayTable);
        sceneDisplaySelectedData = new Scene(titledPaneDisplayTable, 900, 500);

        stage.setScene(sceneMainView);
        stage.setTitle("Moodle'i logide analüüsimine");
        stage.setResizable(false);
        stage.show();
    }

    private Task<Void> displayEventContext = new Task<>() {
        @Override
        protected Void call() {
            Platform.runLater(() -> {
                List<String> contextList = new ArrayList<>(moodleLogsProcessing.getEventContext());
                Collections.sort(contextList);
                listViewLogContext.getItems().addAll(contextList);
            });
            return null;
        }
    };

    private Task<Void> displayEventName = new Task<>() {
        @Override
        protected Void call() {
            Platform.runLater(() -> {
                Set<String> setEventName = new HashSet<>();
                for (Map<String, String> log : moodleLogsProcessing.getLogs()) {
                    if (observableListLogContext.contains(log.get("Event context"))) {
                        setEventName.add(log.get("Event name"));
                    }
                }
                List<String> listEventName = new ArrayList<>(setEventName);
                Collections.sort(listEventName);
                listViewLogName.getItems().addAll(listEventName);
            });
            return null;
        }
    };

    private Task<Void> displayStudentGroup = new Task<>() {
        @Override
        protected Void call() {
            Platform.runLater(() -> {
                List<String> groupList = new ArrayList<>(studentInfoProcessing.getStudentGroup());
                Collections.sort(groupList);
                listViewStudentGroup.getItems().addAll(groupList);
            });
            return null;
        }
    };

    private Task<Void> displayStudentName = new Task<>() {
        @Override
        protected Void call() {
            Platform.runLater(() -> {
                Set<String> setStudentName = new HashSet<>();
                for (Map<String, String> log : studentInfoProcessing.getStudents()) {
                    if (observableListStudentGroup.contains(log.get("Group"))) {
                        setStudentName.add(log.get("Name"));
                    }
                }
                List<String> listStudentName = new ArrayList<>(setStudentName);
                Collections.sort(listStudentName);
                listViewStudentName.getItems().addAll(listStudentName);
            });
            return null;
        }
    };

    private Task<Void> displaySelectedData = new Task<>() {
        @Override
        protected Void call() {
            Platform.runLater(() -> {
                for (Map<String, String> log : moodleLogsProcessing.getLogs()) {
                    if (observableListStudentName.contains(log.get("Name")) && observableListLogContext.contains(log.get("Event context")) && observableListLogEvent.contains(log.get("Event name"))) {
                        Log newLog = new Log(log.get("Time"), log.get("Event context"), log.get("Event name"), studentInfoProcessing.getStudentAndGroup().get(log.get("Name")), log.get("Name"));
                        tableViewSelectedData.getItems().add(newLog);
                        filteredResults.add(newLog);
                    }
                }
            });
            return null;
        }
    };
}
