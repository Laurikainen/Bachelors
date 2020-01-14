package MainLogic;

import FilesProcessing.GradesProcessing;
import FilesProcessing.MoodleLogsProcessing;
import FilesProcessing.StudentInfoProcessing;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;

public class Main extends Application {

    private Scene sceneMainView, sceneLogContext, sceneLogName, sceneFilterByGroup, sceneFilterByName, sceneTimeFrame, sceneDisplayAllSelectedData;
    private File logFile, studentFile, gradeFile;
    private MoodleLogsProcessing moodleLogsProcessing;
    private StudentInfoProcessing studentInfoProcessing;
    private GradesProcessing gradesProcessing;
    private ListView<String> listViewLogContext = new ListView<>();
    private ListView<String> listViewLogEventFinal = new ListView<>();
    private ListView<String> listViewLogEvent = new ListView<>();
    private ListView<String> listViewStudentNameFinal = new ListView<>();
    private ListView<String> listViewStudentName = new ListView<>();
    private ListView<String> listViewStudentGroup = new ListView<>();
    private TableView tableViewSelectedData = new TableView();
    private TableView tableViewSelectedStudentsGrades = new TableView();
    private ObservableList<String> observableListLogContext = FXCollections.observableArrayList();
    private ObservableList<String> observableListLogEvent = FXCollections.observableArrayList();
    private ObservableList<String> observableListStudentName = FXCollections.observableArrayList();
    private ObservableList<String> observableListStudentGroup = FXCollections.observableArrayList();
    private LocalDate localDateFrom, localDateTo;
    private String localTimeFrom, localTimeTo;
    private List<Log> filteredResults = new ArrayList<>();
    private CategoryAxis xAxisEventName = new CategoryAxis();
    private CategoryAxis xAxisEventContext = new CategoryAxis();
    private CategoryAxis xAxisStudentGroup = new CategoryAxis();
    private CategoryAxis xAxisStudentName = new CategoryAxis();
    private CategoryAxis xAxisVisitsPerWeek = new CategoryAxis();
    private CategoryAxis xAxisVisitsPerDay = new CategoryAxis();
    private CategoryAxis xAxisVisitsPerHour = new CategoryAxis();

    private NumberAxis yAxisEventName = new NumberAxis();
    private NumberAxis yAxisEventContext = new NumberAxis();
    private NumberAxis yAxisStudentGroup = new NumberAxis();
    private NumberAxis yAxisStudentName = new NumberAxis();
    private NumberAxis yAxisVisitsPerWeek = new NumberAxis();
    private NumberAxis yAxisVisitsPerDay = new NumberAxis();
    private NumberAxis yAxisVisitsPerHour = new NumberAxis();

    private BarChart barChartEventName = new BarChart(yAxisEventName, xAxisEventName);
    private BarChart barChartEventContext = new BarChart(yAxisEventContext, xAxisEventContext);
    private BarChart barChartStudentGroup = new BarChart(xAxisStudentGroup, yAxisStudentGroup);
    private BarChart barChartStudentName = new BarChart(xAxisStudentName, yAxisStudentName);
    private BarChart barChartVisitsPerWeek = new BarChart(xAxisVisitsPerWeek, yAxisVisitsPerWeek);
    private BarChart barChartVisitsPerDay = new BarChart(xAxisVisitsPerDay, yAxisVisitsPerDay);
    private BarChart barChartVisitsPerHour = new BarChart(xAxisVisitsPerHour, yAxisVisitsPerHour);

    public static void main(String[] args) {
        launch(args);
    }

    public Main() { }

    @Override
    public void start(Stage stage) {

        // All the used TitledPanes and TabPanes
        TitledPane titledPaneMainView = new TitledPane("Vajalike failide sisestamine", new Label());
        TitledPane titledPaneEventContext = new TitledPane("Sündmuse konteksti elementide valimine", new Label());
        TitledPane titledPaneEventName = new TitledPane("Sündmuse nime elementide valimine", new Label());
        TitledPane titledPaneStudentGroups = new TitledPane("Õpilaste gtuppide valimine", new Label());
        TitledPane titledPaneStudentName = new TitledPane("Õpilaste nimede valimine", new Label());
        TitledPane titledPaneTimeFrame = new TitledPane("Ajaraami valimine", new Label());
        TabPane tabPaneAnalysedData = new TabPane();
        // Making all the TitledPanes not collapsible
        titledPaneMainView.setCollapsible(false);
        titledPaneEventContext.setCollapsible(false);
        titledPaneEventName.setCollapsible(false);
        titledPaneStudentGroups.setCollapsible(false);
        titledPaneStudentName.setCollapsible(false);
        titledPaneTimeFrame.setCollapsible(false);
        // All the used Tabs
        Tab tabAllSelectedData = new Tab("Valitud andmed ");
        Tab tabEventContext = new Tab("Sündmuse kontekst ");
        Tab tabEventName = new Tab("Sündmuse nimi ");
        Tab tabStudentName = new Tab("Õpilase nimi ");
        Tab tabStudentGroup = new Tab("Õpilaste grupid ");
        Tab tabStudentGrades = new Tab("Õpilaste hinded ");
        Tab tabVisitsPerWeek = new Tab("Külastused nädalati ");
        Tab tabVisitsPerDay = new Tab("Külastused päeviti ");
        Tab tabVisitsPerHour = new Tab("Külastused tunniti ");
        // Make all tabs not closable
        tabAllSelectedData.setClosable(false);
        tabEventContext.setClosable(false);
        tabEventName.setClosable(false);
        tabStudentName.setClosable(false);
        tabStudentGroup.setClosable(false);
        tabStudentGrades.setClosable(false);
        tabVisitsPerWeek.setClosable(false);
        tabVisitsPerDay.setClosable(false);
        tabVisitsPerHour.setClosable(false);
        // All rhe used GridPanes
        GridPane gridPaneMainView = new GridPane();
        GridPane gridPaneEventContext = new GridPane();
        GridPane gridPaneEventName = new GridPane();
        GridPane gridPaneStudentGroups = new GridPane();
        GridPane gridPaneStudentName = new GridPane();
        GridPane gridPaneTimeFrame = new GridPane();
        GridPane gridPaneTabAllSelectedData = new GridPane();
        GridPane gridPaneTabEventContext = new GridPane();
        GridPane gridPaneTabEventName = new GridPane();
        GridPane gridPaneTabStudentName = new GridPane();
        GridPane gridPaneTabStudentGroup = new GridPane();
        GridPane gridPaneTabStudentGrades = new GridPane();
        GridPane gridPaneTabVisitsPerWeek = new GridPane();
        GridPane gridPaneTabVisitsPerDay = new GridPane();
        GridPane gridPaneTabVisitsPerHour = new GridPane();
        GridPane gridPaneTabPane = new GridPane();
        // All the used Labels
        Label labelStudent = new Label("Õpilaste andmed (.xls) ");
        Label labelGrade = new Label("Õpilaste hinded (.xlsx) ");
        Label labelLog = new Label("Moodle'i logid (.csv) ");
        Label labelLogFileName = new Label();
        Label labelStudentFileName =  new Label();
        Label labelGradeFileName = new Label();
        Label labelDateFrom = new Label("Alguskuupäev ");
        Label labelDateTo = new Label("Lõpukuupäev ");
        Label labelTimeFrom = new Label("Alguskellaaeg ");
        Label labelTimeTo = new Label("Lõpukellaaeg ");
        // All the used Buttons
        Button buttonChooseStudentFile = new Button("Ava õpilaste fail");
        Button buttonChooseGradeFile = new Button("Ava hinnete fail");
        Button buttonChooseLogFile = new Button("Ava logide fail");
        Button buttonSubmitAllTheChosenFiles = new Button("Edasi");
        Button buttonGoToChooseEventName = new Button("Edasi");
        Button buttonGoBackToMainView = new Button("Tagasi");
        Button buttonGoToChooseStudentGroup = new Button("Edasi");
        Button buttonGoBackToChooseEventContext = new Button("Tagasi");
        Button buttonGoToChooseStudentName = new Button("Edasi");
        Button buttonGoBackToChooseEventName = new Button("Tagasi");
        Button buttonGoToChooseTimeFrame = new Button("Edasi");
        Button buttonGoBackToChoosetudentGroup = new Button("Tagasi");
        Button buttonGoToDisplayAllChosenData = new Button("Edasi");
        Button buttonGoBackToChooseStudentName = new Button("Tagasi");
        Button buttonRemoveDateAndTime = new Button("Tühjenda väljad");
        Button buttonGoBackToTimeFrame = new Button("Tagasi");
        // SearchView
        // Making ListViews and TableView selection models to multiple
        listViewLogContext.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listViewLogEvent.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listViewStudentGroup.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listViewStudentName.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableViewSelectedData.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // Making ListViews and TableView width to 900
        listViewLogContext.setMinWidth(800);
        listViewLogEvent.setMinWidth(800);
        listViewStudentGroup.setMinWidth(800);
        listViewStudentName.setMinWidth(800);
        tableViewSelectedData.setMinWidth(900);
        tableViewSelectedStudentsGrades.setMinWidth(900);
        // Making TextFields to filter ListViews
        TextField textFieldFilteredLogContext = new TextField();
        TextField textFieldFilteredLogEvent = new TextField();
        TextField textFieldFilteredStudentGroup = new TextField();
        TextField textFieldFilteredStudentName = new TextField();
        // Setting TextFields size
        textFieldFilteredLogContext.setMinWidth(800);
        textFieldFilteredLogEvent.setMinWidth(800);
        textFieldFilteredStudentGroup.setMinWidth(800);
        textFieldFilteredStudentName.setMinWidth(800);
        // Creating filters for the ListViews
        textFieldFilteredLogContext.textProperty().addListener(obs -> {
            String filterLogContext = textFieldFilteredLogContext.getText();
            if(filterLogContext == null || filterLogContext.length() == 0) {
                List<String> listLogContext = new ArrayList<>(moodleLogsProcessing.getEventContext());
                Collections.sort(listLogContext);
                listViewLogContext.setItems(FXCollections.observableArrayList(listLogContext));
            }
            else {
                List<String> listLogContext = new ArrayList<>();
                for (String eventContext : moodleLogsProcessing.getEventContext()) {
                    if (eventContext.contains(filterLogContext)) {
                        listLogContext.add(eventContext);
                    }
                }
                Collections.sort(listLogContext);
                listViewLogContext.setItems(FXCollections.observableArrayList(listLogContext));
            }
        });
        textFieldFilteredLogEvent.textProperty().addListener(obs -> {
            String filterLogName = textFieldFilteredLogEvent.getText();
            if(filterLogName == null || filterLogName.length() == 0) {
                listViewLogEvent.setItems(listViewLogEventFinal.getItems());
            }
            else {
                List<String> listLogEvent = new ArrayList<>();
                for (String eventName : listViewLogEventFinal.getItems()) {
                    if (eventName.contains(filterLogName)) {
                        listLogEvent.add(eventName);
                    }
                }
                Collections.sort(listLogEvent);
                listViewLogEvent.setItems(FXCollections.observableArrayList(listLogEvent));
            }
        });
        textFieldFilteredStudentGroup.textProperty().addListener(obs -> {
            String filterStudentGroup = textFieldFilteredStudentGroup.getText();
            if(filterStudentGroup == null || filterStudentGroup.length() == 0) {
                List<String> listStudentGroup = new ArrayList<>(studentInfoProcessing.getStudentGroup());
                Collections.sort(listStudentGroup);
                listViewStudentGroup.setItems(FXCollections.observableArrayList(listStudentGroup));
            }
            else {
                List<String> listStudentGroup = new ArrayList<>();
                for (String studentGroup : studentInfoProcessing.getStudentGroup()) {
                    if (studentGroup.contains(filterStudentGroup)) {
                        listStudentGroup.add(studentGroup);
                    }
                }
                Collections.sort(listStudentGroup);
                listViewStudentGroup.setItems(FXCollections.observableArrayList(listStudentGroup));
            }
        });
        textFieldFilteredStudentName.textProperty().addListener(obs -> {
            String filterStudentName = textFieldFilteredStudentName.getText();
            if(filterStudentName == null || filterStudentName.length() == 0) {
                listViewStudentName.setItems(listViewStudentNameFinal.getItems());
            }
            else {
                List<String> listStudentName = new ArrayList<>();
                for (String studentName : listViewStudentNameFinal.getItems()) {
                    if (studentName.contains(filterStudentName)) {
                        listStudentName.add(studentName);
                    }
                }
                Collections.sort(listStudentName);
                listViewStudentName.setItems(FXCollections.observableArrayList(listStudentName));
            }
        });
        // Creating FileChoosers
        FileChooser fileChooserLog = new FileChooser();
        FileChooser fileChooserStudent = new FileChooser();
        FileChooser fileChooserGrade = new FileChooser();
        // Specifying FileChoosers extensions
        FileChooser.ExtensionFilter extFilterLog = new FileChooser.ExtensionFilter(".csv files (*.csv)", "*.csv");
        FileChooser.ExtensionFilter extFilterStudent = new FileChooser.ExtensionFilter(".xls files (*.xls)", "*.xls");
        FileChooser.ExtensionFilter extFilterGrade = new FileChooser.ExtensionFilter(".xlsx files (*.xlsx)", "*.xlsx");
        // FileChoosers
        fileChooserLog.setTitle("Ava logide fail ");
        fileChooserStudent.setTitle("Ava õpilaste fail ");
        fileChooserGrade.setTitle("Ava hinnete fail ");
        fileChooserLog.getExtensionFilters().add(extFilterLog);
        fileChooserStudent.getExtensionFilters().add(extFilterStudent);
        fileChooserGrade.getExtensionFilters().add(extFilterGrade);
        // Creating DatePicker and ChoiceBoxes
        DatePicker datePickerFrom = new DatePicker();
        DatePicker datePickerTo = new DatePicker();
        ChoiceBox<String> choiceBoxTimeFromHours = new ChoiceBox<>();
        ChoiceBox<String> choiceBoxTimeFromMinutes = new ChoiceBox<>();
        ChoiceBox<String> choiceBoxTimeToHours = new ChoiceBox<>();
        ChoiceBox<String> choiceBoxTimeToMinutes = new ChoiceBox<>();
        // Initializing ChoiceBoxes
        for (int i = 0; i<25; i++) {
            choiceBoxTimeFromHours.getItems().add(Integer.toString(i));
            choiceBoxTimeToHours.getItems().add(Integer.toString(i));
        }
        for (int i = 0; i<61; i++) {
            choiceBoxTimeFromMinutes.getItems().add(Integer.toString(i));
            choiceBoxTimeToMinutes.getItems().add(Integer.toString(i));
        }
        // Creating the table for all the selected information
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
        // Adding elements to GridPanes
        gridPaneMainView.add(labelStudent, 0, 0);
        gridPaneMainView.add(labelGrade, 0, 1 );
        gridPaneMainView.add(labelLog,0, 2);
        gridPaneMainView.add(labelStudentFileName, 1, 0);
        gridPaneMainView.add(labelGradeFileName, 1, 1 );
        gridPaneMainView.add(labelLogFileName,1, 2);
        gridPaneMainView.add(buttonChooseStudentFile, 2, 0);
        gridPaneMainView.add(buttonChooseGradeFile, 2, 1 );
        gridPaneMainView.add(buttonChooseLogFile,2, 2);
        gridPaneMainView.add(buttonSubmitAllTheChosenFiles, 2, 3);
        gridPaneMainView.setVgap(15);
        gridPaneMainView.setHgap(200);
        gridPaneEventContext.add(textFieldFilteredLogContext, 0, 0, 2, 1);
        gridPaneEventName.add(textFieldFilteredLogEvent, 0, 0,2 ,1);
        gridPaneStudentGroups.add(textFieldFilteredStudentGroup, 0, 0, 2, 1);
        gridPaneStudentName.add(textFieldFilteredStudentName, 0, 0, 2, 1);
        gridPaneEventContext.add(listViewLogContext, 0,  1, 2, 1);
        gridPaneEventName.add(listViewLogEvent, 0, 1, 2, 1);
        gridPaneStudentGroups.add(listViewStudentGroup, 0, 1, 2, 1);
        gridPaneStudentName.add(listViewStudentName, 0, 1, 2, 1);
        gridPaneEventContext.add(buttonGoBackToMainView, 0, 2);
        gridPaneEventContext.add(buttonGoToChooseEventName, 1, 2);
        gridPaneEventName.add(buttonGoBackToChooseEventContext, 0, 2);
        gridPaneEventName.add(buttonGoToChooseStudentGroup, 1, 2);
        gridPaneStudentGroups.add(buttonGoBackToChooseEventName, 0, 2);
        gridPaneStudentGroups.add(buttonGoToChooseStudentName, 1, 2);
        gridPaneStudentName.add(buttonGoBackToChoosetudentGroup, 0, 2);
        gridPaneStudentName.add(buttonGoToChooseTimeFrame, 1, 2);
        gridPaneEventContext.setVgap(15);
        gridPaneEventName.setVgap(15);
        gridPaneStudentGroups.setVgap(15);
        gridPaneStudentName.setVgap(15);
        gridPaneEventContext.setHgap(15);
        gridPaneEventName.setHgap(15);
        gridPaneStudentGroups.setHgap(15);
        gridPaneStudentName.setHgap(15);
        gridPaneTimeFrame.add(labelDateFrom, 0, 0);
        gridPaneTimeFrame.add(labelDateTo, 2, 0);
        gridPaneTimeFrame.add(datePickerFrom, 0, 1, 2, 1);
        gridPaneTimeFrame.add(datePickerTo, 2, 1, 2, 1);
        gridPaneTimeFrame.add(labelTimeFrom, 0, 2);
        gridPaneTimeFrame.add(labelTimeTo, 2, 2);
        gridPaneTimeFrame.add(choiceBoxTimeFromHours, 0, 3);
        gridPaneTimeFrame.add(choiceBoxTimeFromMinutes, 1, 3);
        gridPaneTimeFrame.add(choiceBoxTimeToHours, 2, 3);
        gridPaneTimeFrame.add(choiceBoxTimeToMinutes, 3, 3);
        gridPaneTimeFrame.add(buttonGoBackToChooseStudentName, 0, 5);
        gridPaneTimeFrame.add(buttonGoToDisplayAllChosenData, 2, 5);
        gridPaneTimeFrame.add(buttonRemoveDateAndTime, 0, 4);
        gridPaneTimeFrame.setVgap(15);
        gridPaneTimeFrame.setHgap(15);
        gridPaneTabAllSelectedData.add(tableViewSelectedData, 0, 0, 2, 1);
        gridPaneTabEventName.add(barChartEventName, 0, 0);
        gridPaneTabEventContext.add(barChartEventContext, 0, 0);
        gridPaneTabStudentGroup.add(barChartStudentGroup, 0, 0);
        gridPaneTabStudentName.add(barChartStudentName, 0, 0);
        gridPaneTabStudentGrades.add(tableViewSelectedStudentsGrades, 0, 0 ,2, 1);
        gridPaneTabPane.add(buttonGoBackToTimeFrame, 0, 1);
        gridPaneTabVisitsPerWeek.add(barChartVisitsPerWeek, 0, 0);
        gridPaneTabVisitsPerDay.add(barChartVisitsPerDay, 0, 0);
        gridPaneTabVisitsPerHour.add(barChartVisitsPerHour, 0, 0);
        // Add elements to Tabs
        tabAllSelectedData.setContent(gridPaneTabAllSelectedData);
        tabEventContext.setContent(gridPaneTabEventContext);
        tabEventName.setContent(gridPaneTabEventName);
        tabStudentName.setContent(gridPaneTabStudentName);
        tabStudentGroup.setContent(gridPaneTabStudentGroup);
        tabStudentGrades.setContent(gridPaneTabStudentGrades);
        tabVisitsPerWeek.setContent(gridPaneTabVisitsPerWeek);
        tabVisitsPerDay.setContent(gridPaneTabVisitsPerDay);
        tabVisitsPerHour.setContent(gridPaneTabVisitsPerHour);
        // Adding elements to TitlePanes and TabPane
        titledPaneMainView.setContent(gridPaneMainView);
        titledPaneEventContext.setContent(gridPaneEventContext);
        titledPaneEventName.setContent(gridPaneEventName);
        titledPaneStudentGroups.setContent(gridPaneStudentGroups);
        titledPaneStudentName.setContent(gridPaneStudentName);
        titledPaneTimeFrame.setContent(gridPaneTimeFrame);
        tabPaneAnalysedData.getTabs().addAll(tabAllSelectedData, tabEventContext, tabEventName, tabStudentName, tabStudentGroup, tabStudentGrades, tabVisitsPerWeek, tabVisitsPerDay, tabVisitsPerHour);
        // GridPane connected with TabPane
        gridPaneTabPane.add(tabPaneAnalysedData, 0, 0);;
        // Creating the new scenes
        sceneMainView = new Scene(titledPaneMainView, 900, 500);
        sceneLogContext = new Scene(titledPaneEventContext, 900, 500);
        sceneLogName = new Scene(titledPaneEventName, 900, 500);
        sceneFilterByGroup = new Scene(titledPaneStudentGroups, 900, 500);
        sceneFilterByName = new Scene(titledPaneStudentName, 900, 500);
        sceneTimeFrame = new Scene(titledPaneTimeFrame, 900, 500);
        sceneDisplayAllSelectedData = new Scene(gridPaneTabPane, 900, 500);
        // Button setOnAction()'s
        buttonChooseStudentFile.setOnAction(e -> {
            studentFile = fileChooserStudent.showOpenDialog(stage);
            if (studentFile != null) { labelStudentFileName.setText(studentFile.getName() + " "); }
        });
        buttonChooseGradeFile.setOnAction(e -> {
            gradeFile = fileChooserGrade.showOpenDialog(stage);
            if (gradeFile != null) { labelGradeFileName.setText(gradeFile.getName() + " "); }
        });
        buttonChooseLogFile.setOnAction(e -> {
            logFile = fileChooserLog.showOpenDialog(stage);
            if (logFile != null) { labelLogFileName.setText(logFile.getName() + "  "); }
        });
        buttonSubmitAllTheChosenFiles.setOnAction(e -> {
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
                displayEventContext();
                stage.setScene(sceneLogContext);
            }
        });

        // Forward
        buttonGoToChooseEventName.setOnAction(e -> {
            observableListLogContext = listViewLogContext.getSelectionModel().getSelectedItems();
            if (observableListLogContext.isEmpty()) { observableListLogContext = listViewLogContext.getItems(); }
            displayEventName();
            stage.setScene(sceneLogName);
        });
        buttonGoToChooseStudentGroup.setOnAction(e -> {
            observableListLogEvent = listViewLogEvent.getSelectionModel().getSelectedItems();
            if (observableListLogEvent.isEmpty()) { observableListLogEvent = listViewLogEvent.getItems(); }
            displayStudentGroup();
            stage.setScene(sceneFilterByGroup);
        });
        buttonGoToChooseStudentName.setOnAction(e -> {
            observableListStudentGroup = listViewStudentGroup.getSelectionModel().getSelectedItems();
            if (observableListStudentGroup.isEmpty()) { observableListStudentGroup = listViewStudentGroup.getItems(); }
            displayStudentName();
            stage.setScene(sceneFilterByName);
        });
        buttonGoToChooseTimeFrame.setOnAction(e -> {
            observableListStudentName = listViewStudentName.getSelectionModel().getSelectedItems();
            if (observableListStudentName.isEmpty()) { observableListStudentName = listViewStudentName.getItems(); }
            stage.setScene(sceneTimeFrame);
        });
        buttonGoToDisplayAllChosenData.setOnAction(e -> {
            localDateFrom = datePickerFrom.getValue();
            localDateTo = datePickerTo.getValue();
            if (localDateFrom == null && localDateTo != null) {
                Alert alertTimeHoursFromAfterTimeHoursTo = new Alert(Alert.AlertType.ERROR);
                alertTimeHoursFromAfterTimeHoursTo.setTitle("Viga!");
                alertTimeHoursFromAfterTimeHoursTo.setHeaderText("Alguskuupäeva pole valitud!");
                alertTimeHoursFromAfterTimeHoursTo.setContentText("Palun vali alguskuupäev.");
                alertTimeHoursFromAfterTimeHoursTo.show();
            }
            else if (localDateFrom != null && localDateTo == null) {
                Alert alertTimeHoursFromAfterTimeHoursTo = new Alert(Alert.AlertType.ERROR);
                alertTimeHoursFromAfterTimeHoursTo.setTitle("Viga!");
                alertTimeHoursFromAfterTimeHoursTo.setHeaderText("Lõpukuupäev pole valitud!");
                alertTimeHoursFromAfterTimeHoursTo.setContentText("Palun vali lõpukuupäev.");
                alertTimeHoursFromAfterTimeHoursTo.show();
            }
            else if (localDateFrom != null) {
                if (choiceBoxTimeFromHours.getValue() != null && choiceBoxTimeFromMinutes.getValue() != null && choiceBoxTimeToHours.getValue() != null && choiceBoxTimeToMinutes.getValue() != null) {
                    StringBuilder timeFrom = new StringBuilder();
                    StringBuilder timeTo = new StringBuilder();
                    if (Integer.parseInt(String.valueOf(choiceBoxTimeFromHours.getValue())) < 10) {
                        timeFrom.append("0");
                    }
                    if (Integer.parseInt(String.valueOf(choiceBoxTimeToHours.getValue())) < 10) {
                        timeTo.append("0");
                    }
                    timeFrom.append(choiceBoxTimeFromHours.getValue());
                    timeFrom.append(".");
                    timeTo.append(choiceBoxTimeToHours.getValue());
                    timeTo.append(".");
                    if (Integer.parseInt(String.valueOf(choiceBoxTimeFromMinutes.getValue())) < 10) {
                        timeFrom.append("0");
                    }
                    if (Integer.parseInt(String.valueOf(choiceBoxTimeToMinutes.getValue())) < 10) {
                        timeTo.append("0");
                    }
                    timeFrom.append(choiceBoxTimeFromMinutes.getValue());
                    timeTo.append(choiceBoxTimeToMinutes.getValue());
                    localTimeFrom = timeFrom.toString();
                    localTimeTo = timeTo.toString();

                    if (localDateFrom.isEqual(localDateTo)) {
                        String[] from = localTimeFrom.split("\\.");
                        String[] to = localTimeTo.split("\\.");
                        if (Integer.parseInt(from[0]) > Integer.parseInt(to[0])) {
                            Alert alertTimeHoursFromAfterTimeHoursTo = new Alert(Alert.AlertType.ERROR);
                            alertTimeHoursFromAfterTimeHoursTo.setTitle("Viga!");
                            alertTimeHoursFromAfterTimeHoursTo.setHeaderText("Algustund on enne lõputundi!");
                            alertTimeHoursFromAfterTimeHoursTo.setContentText("Palun vali algustund, mis on enne lõputundi.");
                            alertTimeHoursFromAfterTimeHoursTo.show();
                        }
                        else if (from[0].equals(to[0])) {
                            if (Integer.parseInt(from[1]) > Integer.parseInt(to[1])) {
                                Alert alertTimeMinutesFromAfterTimeMinutesTo = new Alert(Alert.AlertType.ERROR);
                                alertTimeMinutesFromAfterTimeMinutesTo.setTitle("Viga!");
                                alertTimeMinutesFromAfterTimeMinutesTo.setHeaderText("Algusminutid on enne lõpuminuteid!");
                                alertTimeMinutesFromAfterTimeMinutesTo.setContentText("Palun vali algusminutid, mis on enne lõpuminuteid.");
                                alertTimeMinutesFromAfterTimeMinutesTo.show();
                            }
                            else {
                                runMethodsForTabView();
                                stage.setScene(sceneDisplayAllSelectedData);
                            }
                        }
                        else {
                            runMethodsForTabView();
                            stage.setScene(sceneDisplayAllSelectedData);
                        }
                    }
                    else if (localDateFrom.isAfter(localDateTo)) {
                        Alert alertDateFromAfterDateTo = new Alert(Alert.AlertType.ERROR);
                        alertDateFromAfterDateTo.setTitle("Viga!");
                        alertDateFromAfterDateTo.setHeaderText("Alguskuupäev on enne lõpukuupäeva!");
                        alertDateFromAfterDateTo.setContentText("Palun vali alguskuupäev, mis on enne lõpukuupäeva.");
                        alertDateFromAfterDateTo.show();
                    }
                    else {
                        runMethodsForTabView();
                        stage.setScene(sceneDisplayAllSelectedData);
                    }
                }
                else if ((choiceBoxTimeFromHours.getValue() != null && choiceBoxTimeFromMinutes.getValue() == null) || (choiceBoxTimeFromHours.getValue() == null && choiceBoxTimeFromMinutes.getValue() != null)) {
                    Alert alertTimeHoursFromAfterTimeHoursTo = new Alert(Alert.AlertType.ERROR);
                    alertTimeHoursFromAfterTimeHoursTo.setTitle("Viga!");
                    alertTimeHoursFromAfterTimeHoursTo.setHeaderText("Algusaeg on valimata!");
                    alertTimeHoursFromAfterTimeHoursTo.setContentText("Palun vali alguaeg.");
                    alertTimeHoursFromAfterTimeHoursTo.show();
                }
                else if ((choiceBoxTimeToHours.getValue() != null && choiceBoxTimeToMinutes.getValue() == null) || (choiceBoxTimeToHours.getValue() == null && choiceBoxTimeToMinutes.getValue() != null)) {
                    Alert alertTimeHoursFromAfterTimeHoursTo = new Alert(Alert.AlertType.ERROR);
                    alertTimeHoursFromAfterTimeHoursTo.setTitle("Viga!");
                    alertTimeHoursFromAfterTimeHoursTo.setHeaderText("Lõpuaeg on valimata!");
                    alertTimeHoursFromAfterTimeHoursTo.setContentText("Palun vali lõpuaeg.");
                    alertTimeHoursFromAfterTimeHoursTo.show();
                }
                else if ((choiceBoxTimeFromHours.getValue() == null && choiceBoxTimeFromMinutes.getValue() == null) && (choiceBoxTimeToHours.getValue() != null && choiceBoxTimeToMinutes.getValue() != null)) {
                    Alert alertTimeHoursFromAfterTimeHoursTo = new Alert(Alert.AlertType.ERROR);
                    alertTimeHoursFromAfterTimeHoursTo.setTitle("Viga!");
                    alertTimeHoursFromAfterTimeHoursTo.setHeaderText("Algusaeg on valimata!");
                    alertTimeHoursFromAfterTimeHoursTo.setContentText("Palun vali alguaeg.");
                    alertTimeHoursFromAfterTimeHoursTo.show();
                }
                else if ((choiceBoxTimeFromHours.getValue() != null && choiceBoxTimeFromMinutes.getValue() != null) && (choiceBoxTimeToHours.getValue() == null && choiceBoxTimeToMinutes.getValue() == null)) {
                    Alert alertTimeHoursFromAfterTimeHoursTo = new Alert(Alert.AlertType.ERROR);
                    alertTimeHoursFromAfterTimeHoursTo.setTitle("Viga!");
                    alertTimeHoursFromAfterTimeHoursTo.setHeaderText("Lõpuaeg on valimata!");
                    alertTimeHoursFromAfterTimeHoursTo.setContentText("Palun vali lõpuaeg.");
                    alertTimeHoursFromAfterTimeHoursTo.show();
                }
                else {
                    localTimeFrom = null;
                    localTimeTo = null;
                }
            }
            else {
                runMethodsForTabView();
                stage.setScene(sceneDisplayAllSelectedData);
            }

        });
        // Backward
        buttonGoBackToMainView.setOnAction(e -> {
            moodleLogsProcessing.clearAllLogData();
            gradesProcessing.clearAllGradeData();
            studentInfoProcessing.clearAllStudentData();
            labelLogFileName.setText("");
            labelGradeFileName.setText("");
            labelStudentFileName.setText("");
            logFile = null;
            gradeFile = null;
            studentFile = null;
            listViewLogContext.setItems(FXCollections.observableArrayList());
            observableListLogContext = FXCollections.observableArrayList();
            stage.setScene(sceneMainView);
        });
        buttonGoBackToChooseEventContext.setOnAction(e -> {
            observableListLogContext = FXCollections.observableArrayList();
            listViewLogEvent.setItems(FXCollections.observableArrayList());
            observableListLogEvent = FXCollections.observableArrayList();
            stage.setScene(sceneLogContext);
        });
        buttonGoBackToChooseEventName.setOnAction(e -> {
            listViewStudentGroup.setItems(FXCollections.observableArrayList());
            observableListStudentGroup = FXCollections.observableArrayList();
            observableListLogEvent = FXCollections.observableArrayList();
            stage.setScene(sceneLogName);
        });
        buttonGoBackToChoosetudentGroup.setOnAction(e -> {
            listViewStudentName.setItems(FXCollections.observableArrayList());
            observableListStudentName = FXCollections.observableArrayList();
            observableListStudentGroup = FXCollections.observableArrayList();
            stage.setScene(sceneFilterByGroup);
        });
        buttonGoBackToChooseStudentName.setOnAction(e -> {
            localDateFrom = null;
            localDateTo = null;
            localTimeFrom = null;
            localTimeTo = null;
            datePickerFrom.setValue(null);
            datePickerTo.setValue(null);
            choiceBoxTimeFromHours.setValue(null);
            choiceBoxTimeFromMinutes.setValue(null);
            choiceBoxTimeToHours.setValue(null);
            choiceBoxTimeToMinutes.setValue(null);
            observableListStudentName = FXCollections.observableArrayList();
            stage.setScene(sceneFilterByName);
        });
        buttonGoBackToTimeFrame.setOnAction(e -> {
            choiceBoxTimeFromHours.setValue(null);
            choiceBoxTimeFromMinutes.setValue(null);
            choiceBoxTimeToHours.setValue(null);
            choiceBoxTimeToMinutes.setValue(null);
            localDateFrom = null;
            localDateTo = null;
            localTimeFrom = null;
            localTimeTo = null;
            datePickerFrom.setValue(null);
            datePickerTo.setValue(null);
            filteredResults = new ArrayList<>();
            barChartEventName.getData().clear();
            barChartEventContext.getData().clear();
            barChartStudentGroup.getData().clear();
            barChartStudentName.getData().clear();
            tableViewSelectedData.setItems(FXCollections.observableArrayList());
            tableViewSelectedStudentsGrades.setItems(FXCollections.observableArrayList());
            stage.setScene(sceneTimeFrame);
        });

        // Extra Button actions
        buttonRemoveDateAndTime.setOnAction(e -> {
            datePickerFrom.setValue(null);
            datePickerTo.setValue(null);
            localDateFrom = null;
            localDateTo = null;
            localTimeFrom = null;
            localTimeTo = null;
            choiceBoxTimeFromHours.setValue(null);
            choiceBoxTimeFromMinutes.setValue(null);
            choiceBoxTimeToHours.setValue(null);
            choiceBoxTimeToMinutes.setValue(null);
        });
        // Finalizing the Stage
        stage.setScene(sceneMainView);
        stage.setTitle("Moodle'i logide analüüsimine");
        stage.setResizable(false);
        stage.show();
    }

    private void runMethodsForTabView() {
        displaySelectedData();
        displayBarChartEventName();
        displayBarChartEventContext();
        displayBarChartStudentGroup();
        displayBarChartStudentName();
        displayStudentGrades();
        displayBarChartVisitsPerWeek();
        displayBarChartVisitsPerDay();
        displayBarChartVisitsPerHour();
    }

    private void displayEventContext() {
        Platform.runLater(() -> {
            List<String> contextList = new ArrayList<>(moodleLogsProcessing.getEventContext());
            Collections.sort(contextList);
            listViewLogContext.getItems().addAll(contextList);
        });
    }

    private void displayEventName() {
        Platform.runLater(() -> {
            Set<String> setEventName = new HashSet<>();
            for (Map<String, String> log : moodleLogsProcessing.getLogs()) {
                if (observableListLogContext.contains(log.get("Event context"))) {
                    setEventName.add(log.get("Event name"));
                }
            }
            List<String> listEventName = new ArrayList<>(setEventName);
            Collections.sort(listEventName);
            listViewLogEvent.getItems().addAll(listEventName);
            listViewLogEventFinal.setItems(listViewLogEvent.getItems());
        });
    }

    private void displayStudentGroup() {
        Platform.runLater(() -> {
            List<String> groupList = new ArrayList<>(studentInfoProcessing.getStudentGroup());
            Collections.sort(groupList);
            listViewStudentGroup.getItems().addAll(groupList);
        });

    }

    private void displayStudentName() {
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
            listViewStudentNameFinal.setItems(listViewStudentName.getItems());
        });

    }

    private void displaySelectedData() {
        Platform.runLater(() -> {
            if (localDateFrom != null) {
                if (localTimeFrom == null) {
                    for (Map<String, String> log : moodleLogsProcessing.getLogs()) {
                        if (observableListStudentName.contains(log.get("Name")) && observableListLogContext.contains(log.get("Event context")) && observableListLogEvent.contains(log.get("Event name"))) {
                            DateTimeFormatter dateTimeFormatter;
                            String[] logArray = log.get("Time").split(" ");
                            if (logArray[0].length() == 9) {
                                dateTimeFormatter = DateTimeFormatter.ofPattern("d.MM.yyyy");
                            }
                            else {
                                dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                            }
                            LocalDate localDate = LocalDate.parse(logArray[0], dateTimeFormatter);
                            if ((localDate.isAfter(localDateFrom) || localDate.isEqual(localDateFrom)) && (localDate.isBefore(localDateTo) || localDate.isEqual(localDateTo))) {
                                Log newLog = new Log(log.get("Time"), log.get("Event context"), log.get("Event name"), studentInfoProcessing.getStudentAndGroup().get(log.get("Name")), log.get("Name"));
                                tableViewSelectedData.getItems().add(newLog);
                                filteredResults.add(newLog);
                            }
                        }

                    }
                }
                else {
                    for (Map<String, String> log : moodleLogsProcessing.getLogs()) {
                        if (observableListStudentName.contains(log.get("Name")) && observableListLogContext.contains(log.get("Event context")) && observableListLogEvent.contains(log.get("Event name"))) {
                            String[] logArray = log.get("Time").split(" ");
                            DateTimeFormatter dateTimeFormatter;
                            if (logArray[0].length() == 9) {
                                dateTimeFormatter = DateTimeFormatter.ofPattern("d.MM.yyyy");
                            }
                            else {
                                dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                            }
                            LocalDate localDate = LocalDate.parse(logArray[0], dateTimeFormatter);
                            if ((localDate.isAfter(localDateFrom) || localDate.isEqual(localDateFrom)) && (localDate.isBefore(localDateTo) || localDate.isEqual(localDateTo))) {
                                DateTimeFormatter dateTimeFormatterTime = DateTimeFormatter.ofPattern("HH.mm");
                                LocalTime logTime = LocalTime.parse(logArray[1], dateTimeFormatterTime);
                                LocalTime inputTimeFrom = LocalTime.parse(localTimeFrom, dateTimeFormatterTime);
                                LocalTime inputTimeTo = LocalTime.parse(localTimeTo, dateTimeFormatterTime);
                                if (localDateFrom.isEqual(localDateTo) && localDate.isEqual(localDateFrom)) {
                                    if ((logTime.isAfter(inputTimeFrom) || logTime == inputTimeFrom) && logTime.isBefore(inputTimeTo) || logTime == inputTimeTo) {
                                        Log newLog = new Log(log.get("Time"), log.get("Event context"), log.get("Event name"), studentInfoProcessing.getStudentAndGroup().get(log.get("Name")), log.get("Name"));
                                        tableViewSelectedData.getItems().add(newLog);
                                        filteredResults.add(newLog);
                                    }
                                }
                                else if (localDate.isEqual(localDateFrom)) {
                                    if (logTime.isAfter(inputTimeFrom) || logTime == inputTimeFrom) {
                                        Log newLog = new Log(log.get("Time"), log.get("Event context"), log.get("Event name"), studentInfoProcessing.getStudentAndGroup().get(log.get("Name")), log.get("Name"));
                                        tableViewSelectedData.getItems().add(newLog);
                                        filteredResults.add(newLog);
                                    }
                                }
                                else if (localDate.isEqual(localDateTo)) {
                                    if (logTime.isBefore(inputTimeTo) || logTime == inputTimeTo) {
                                        Log newLog = new Log(log.get("Time"), log.get("Event context"), log.get("Event name"), studentInfoProcessing.getStudentAndGroup().get(log.get("Name")), log.get("Name"));
                                        tableViewSelectedData.getItems().add(newLog);
                                        filteredResults.add(newLog);
                                    }
                                }
                                else {
                                    Log newLog = new Log(log.get("Time"), log.get("Event context"), log.get("Event name"), studentInfoProcessing.getStudentAndGroup().get(log.get("Name")), log.get("Name"));
                                    tableViewSelectedData.getItems().add(newLog);
                                    filteredResults.add(newLog);
                                }

                            }
                        }
                    }
                }
            }
            else {
                for (Map<String, String> log : moodleLogsProcessing.getLogs()) {
                    if (observableListStudentName.contains(log.get("Name")) && observableListLogContext.contains(log.get("Event context")) && observableListLogEvent.contains(log.get("Event name"))) {
                        Log newLog = new Log(log.get("Time"), log.get("Event context"), log.get("Event name"), studentInfoProcessing.getStudentAndGroup().get(log.get("Name")), log.get("Name"));
                        tableViewSelectedData.getItems().add(newLog);
                        filteredResults.add(newLog);
                    }
                }
            }
        });
    }

    private void displayBarChartEventName() {
        Platform.runLater(() -> {
            XYChart.Series dataSeriesEventName = new XYChart.Series();
            dataSeriesEventName.setName("Kui palju sündmuse nime kohta käivaid kirjeid esineb logides.");
            Map<String, Integer> dataMapEventName = new HashMap<>();
            for (Log log : filteredResults) {
                if (dataMapEventName.containsKey(log.getEventName())) {
                    dataMapEventName.put(log.getEventName(), dataMapEventName.get(log.getEventName()) + 1);
                } else {
                    dataMapEventName.put(log.getEventName(), 1);
                }
            }
            if (dataMapEventName.keySet().size() > 15) {
                List<Integer> mapValueEventName = new ArrayList<>();
                List<String> mapKeyEventName = new ArrayList<>();
                for (String key : dataMapEventName.keySet()) {
                    mapValueEventName.add(dataMapEventName.get(key));
                    mapKeyEventName.add(key);
                }
                List<Integer> sortedMapValueEventContext = new ArrayList<>(mapValueEventName);
                Collections.sort(sortedMapValueEventContext);
                while (mapKeyEventName.size() > 15) {
                    int remove = sortedMapValueEventContext.get(0);
                    int index = mapValueEventName.indexOf(remove);
                    sortedMapValueEventContext.remove(0);
                    mapValueEventName.remove(index);
                    mapKeyEventName.remove(index);
                }
                for (int i = 0; i < 15; i++) {
                    dataSeriesEventName.getData().add(new XYChart.Data(mapValueEventName.get(i), mapKeyEventName.get(i)));
                }
            }
            else {
                for (String key : dataMapEventName.keySet()) {
                    dataSeriesEventName.getData().add(new XYChart.Data(dataMapEventName.get(key), key));
                }
            }
            barChartEventName.setMinWidth(900);
            barChartEventName.getData().add(dataSeriesEventName);
        });
    }

    private void displayBarChartEventContext() {
        Platform.runLater(() -> {
            XYChart.Series dataSeriesEventContext = new XYChart.Series();
            dataSeriesEventContext.setName("Kui palju sündmuse konteksti kohta käivaid kirjeid esineb logides.");
            Map<String, Integer> dataMapEventContext = new HashMap<>();
            for (Log log : filteredResults) {
                if (dataMapEventContext.containsKey(log.getEventContext())) {
                    dataMapEventContext.put(log.getEventContext(), dataMapEventContext.get(log.getEventContext()) + 1);
                } else {
                    dataMapEventContext.put(log.getEventContext(), 1);
                }
            }
            if (dataMapEventContext.keySet().size() > 15) {
                List<String> mapKeyEventContext = new ArrayList<>();
                List<Integer> mapValueEventContext = new ArrayList<>();
                for (String key : dataMapEventContext.keySet()) {
                    mapKeyEventContext.add(key);
                    mapValueEventContext.add(dataMapEventContext.get(key));
                }
                List<Integer> sortedMapValueEventContext = new ArrayList<>(mapValueEventContext);
                Collections.sort(sortedMapValueEventContext);
                while (mapKeyEventContext.size() > 15) {
                    int remove = sortedMapValueEventContext.get(0);
                    int index = mapValueEventContext.indexOf(remove);
                    mapKeyEventContext.remove(index);
                    mapValueEventContext.remove(index);
                    sortedMapValueEventContext.remove(0);
                }
                for (int i = 0; i < 15; i++) {
                    dataSeriesEventContext.getData().add(new XYChart.Data(mapValueEventContext.get(i), mapKeyEventContext.get(i)));
                }
            }
            else {
                for (String key : dataMapEventContext.keySet()) {
                    dataSeriesEventContext.getData().add(new XYChart.Data(dataMapEventContext.get(key), key));
                }
            }
            barChartEventContext.setMinWidth(900);
            barChartEventContext.getData().add(dataSeriesEventContext);
        });
    }

    private void displayBarChartStudentGroup() {
        Platform.runLater(() -> {
            XYChart.Series dataSeriesStudentGroup = new XYChart.Series();
            dataSeriesStudentGroup.setName("Kui palju iga rühma kohta käivaid kirjeid esineb logides.");
            Map<String, Integer> dataMapStudentGroup = new HashMap<>();
            for (Log log : filteredResults) {
                if (dataMapStudentGroup.containsKey(log.getStudentGroup())) {
                    dataMapStudentGroup.put(log.getStudentGroup(), dataMapStudentGroup.get(log.getStudentGroup()) + 1);
                } else {
                    dataMapStudentGroup.put(log.getStudentGroup(), 1);
                }
            }
            for (String key : dataMapStudentGroup.keySet()) {
                dataSeriesStudentGroup.getData().add(new XYChart.Data(key, dataMapStudentGroup.get(key)));
            }
            barChartStudentGroup.setMinWidth(900);
            barChartStudentGroup.getData().add(dataSeriesStudentGroup);
        });
    }

    private void displayBarChartStudentName() {
        Platform.runLater(() -> {
            XYChart.Series dataSeriesStudentName = new XYChart.Series();
            dataSeriesStudentName.setName("Kui palju iga õpilase kohta käivaid kirjeid esineb logides.");
            Map<String, Integer> dataMapStudentName = new HashMap<>();
            for (Log log : filteredResults) {
                if (dataMapStudentName.containsKey(log.getStudentName())) {
                    dataMapStudentName.put(log.getStudentName(), dataMapStudentName.get(log.getStudentName()) + 1);
                } else {
                    dataMapStudentName.put(log.getStudentName(), 1);
                }
            }
            for (String key : dataMapStudentName.keySet()) {
                dataSeriesStudentName.getData().add(new XYChart.Data(key, dataMapStudentName.get(key)));
            }
            barChartStudentName.setMinWidth(900);
            barChartStudentName.getData().add(dataSeriesStudentName);
        });
    }


    private void displayStudentGrades() {
        Platform.runLater(() -> {
            Integer[] integers = new Integer[100];
            for (int i = 0; i< 100; i++) {
                integers[i] = i;
            }
            for (Integer integer : integers) {
                TableColumn<List<StringProperty>, String> columnStudentGrade = new TableColumn<>(gradesProcessing.getColumnNames().get(integer).replace(" (Punktid)", ""));
                columnStudentGrade.setCellValueFactory(data -> data.getValue().get(integer));
                tableViewSelectedStudentsGrades.getColumns().add(columnStudentGrade);
            }
            ObservableList<List<StringProperty>> data = FXCollections.observableArrayList();
            for (List<String> grade : gradesProcessing.getStudentGrades()) {
                List<StringProperty> firstRow = new ArrayList<>();
                for (int i = 0; i < grade.size(); i++) {
                    firstRow.add(i, new SimpleStringProperty(grade.get(i)));
                }
                data.add(firstRow);
            }
            tableViewSelectedStudentsGrades.setItems(data);

//                for (String studentGradeColumn : gradesProcessing.getColumnNames()) {
//                    TableColumn<String, List<String>> columnStudentGrade = new TableColumn<>(studentGradeColumn.replace(" (Punktid)", ""));
//                    columnStudentGrade.setCellValueFactory(new PropertyValueFactory<>(studentGradeColumn.replace(" (Punktid)", "")));
//                    tableViewSelectedStudentsGrades.getColumns().add(columnStudentGrade);
//                }
//                for (List<String> grade : gradesProcessing.getStudentGrades()) {
//                    tableViewSelectedStudentsGrades.getItems().add(grade);
//
//                }
        });
    }

    private void displayBarChartVisitsPerHour() {
        Platform.runLater(() -> {
            XYChart.Series dataSeriesVisitsPerHour = new XYChart.Series();
            dataSeriesVisitsPerHour.setName("Kui palju külastatakse Moodle't erinetave ajavahemike jooksul.");
            List<String> time = new ArrayList<>();
            List<Integer> timeOccurrence = new ArrayList<>();
            time.add("0-1");
            timeOccurrence.add(0);
            time.add("2-3");
            timeOccurrence.add(0);
            time.add("4-5");
            timeOccurrence.add(0);
            time.add("6-7");
            timeOccurrence.add(0);
            time.add("8-9");
            timeOccurrence.add(0);
            time.add("10-11");
            timeOccurrence.add(0);
            time.add("12-13");
            timeOccurrence.add(0);
            time.add("14-15");
            timeOccurrence.add(0);
            time.add("16-17");
            timeOccurrence.add(0);
            time.add("18-19");
            timeOccurrence.add(0);
            time.add("20-21");
            timeOccurrence.add(0);
            time.add("22-23");
            timeOccurrence.add(0);
            for (Log log : filteredResults) {
                String[] logArray = log.getTime().split(" ");
                DateTimeFormatter dateTimeFormatterTime = DateTimeFormatter.ofPattern("HH.mm");
                LocalTime logTime = LocalTime.parse(logArray[1], dateTimeFormatterTime);
                if (logTime.getHour() == 0 || logTime.getHour() == 1) {
                    timeOccurrence.add(0, timeOccurrence.get(0) + 1);
                    timeOccurrence.remove(1);
                }
                else if (logTime.getHour() == 2 || logTime.getHour() == 3) {
                    timeOccurrence.add(1, timeOccurrence.get(1) + 1);
                    timeOccurrence.remove(2);
                }
                else if (logTime.getHour() == 4 || logTime.getHour() == 5) {
                    timeOccurrence.add(2, timeOccurrence.get(2) + 1);
                    timeOccurrence.remove(3);
                }
                else if (logTime.getHour() == 6 || logTime.getHour() == 7) {
                    timeOccurrence.add(3, timeOccurrence.get(3) + 1);
                    timeOccurrence.remove(4);
                }
                else if (logTime.getHour() == 8 || logTime.getHour() == 9) {
                    timeOccurrence.add(4, timeOccurrence.get(4) + 1);
                    timeOccurrence.remove(5);
                }
                else if (logTime.getHour() == 10 || logTime.getHour() == 11) {
                    timeOccurrence.add(5, timeOccurrence.get(5) + 1);
                    timeOccurrence.remove(6);
                }
                else if (logTime.getHour() == 12 || logTime.getHour() == 13) {
                    timeOccurrence.add(6, timeOccurrence.get(6) + 1);
                    timeOccurrence.remove(7);
                }
                else if (logTime.getHour() == 14 || logTime.getHour() == 15) {
                    timeOccurrence.add(7, timeOccurrence.get(7) + 1);
                    timeOccurrence.remove(8);
                }
                else if (logTime.getHour() == 16 || logTime.getHour() == 17) {
                    timeOccurrence.add( 8, timeOccurrence.get(8) + 1);
                    timeOccurrence.remove(9);
                }
                else if (logTime.getHour() == 18 || logTime.getHour() == 19) {
                    timeOccurrence.add(9, timeOccurrence.get(9) + 1);
                    timeOccurrence.remove(10);
                }
                else if (logTime.getHour() == 20 || logTime.getHour() == 21) {
                    timeOccurrence.add(10, timeOccurrence.get(10) + 1);
                    timeOccurrence.remove(11);
                }
                else {
                    timeOccurrence.add( 11, timeOccurrence.get(11) + 1);
                    timeOccurrence.remove(12);
                }
            }
            for (int i = 0; i < time.size(); i++) {
                dataSeriesVisitsPerHour.getData().add(new XYChart.Data(time.get(i), timeOccurrence.get(i)));
            }
            barChartVisitsPerHour.setMinWidth(900);
            barChartVisitsPerHour.getData().add(dataSeriesVisitsPerHour);
        });
    }

    private void displayBarChartVisitsPerWeek() {
        Platform.runLater(() -> {
            XYChart.Series dataSeriesVisitsPerWeek = new XYChart.Series();
            dataSeriesVisitsPerWeek.setName("Kui palju külastatakse Moodle't erinevatel õppenädalatel.");
            List<Integer> weekOccurrences = new ArrayList<>();
            for (int i = 0; i < 52; i++) {
                weekOccurrences.add(0);
            }
            for (Log log : filteredResults) {
                DateTimeFormatter dateTimeFormatter;
                String[] logArray = log.getTime().split(" ");
                if (logArray[0].length() == 9) { dateTimeFormatter = DateTimeFormatter.ofPattern("d.MM.yyyy"); }
                else { dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy"); }
                LocalDate localDate = LocalDate.parse(logArray[0], dateTimeFormatter);
                WeekFields weekFields = WeekFields.of(Locale.getDefault());
                int weekNumber = localDate.get(weekFields.weekOfWeekBasedYear());
                weekOccurrences.add(weekNumber-1, weekOccurrences.get(weekNumber-1) + 1);
                weekOccurrences.remove(weekNumber);
            }
            for (int i = 0; i < weekOccurrences.size(); i++) {
                dataSeriesVisitsPerWeek.getData().add(new XYChart.Data(String.valueOf(i+1), weekOccurrences.get(i)));
            }
            barChartVisitsPerWeek.setMinWidth(900);
            barChartVisitsPerWeek.getData().add(dataSeriesVisitsPerWeek);
        });
    }

    private void displayBarChartVisitsPerDay() {
        Platform.runLater(() -> {
            XYChart.Series dataSeriesVisitsPerDay = new XYChart.Series();
            dataSeriesVisitsPerDay.setName("Kui palju külastatakse Moodle't erinevatel päevadel.");
            List<String> day = new ArrayList<>();
            List<Integer> dayOccurrences = new ArrayList<>();
            day.add("E");
            dayOccurrences.add(0);
            day.add("T");
            dayOccurrences.add(0);
            day.add("K");
            dayOccurrences.add(0);
            day.add("N");
            dayOccurrences.add(0);
            day.add("R");
            dayOccurrences.add(0);
            day.add("L");
            dayOccurrences.add(0);
            day.add("P");
            dayOccurrences.add(0);
            for (Log log : filteredResults) {
                DateTimeFormatter dateTimeFormatter;
                String[] logArray = log.getTime().split(" ");
                if (logArray[0].length() == 9) {
                    dateTimeFormatter = DateTimeFormatter.ofPattern("d.MM.yyyy");
                }
                else {
                    dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                }
                LocalDate localDate = LocalDate.parse(logArray[0], dateTimeFormatter);
                if (localDate.getDayOfWeek() == DayOfWeek.MONDAY) {
                    dayOccurrences.add(0, dayOccurrences.get(0) + 1);
                    dayOccurrences.remove(1);
                }
                else if (localDate.getDayOfWeek() == DayOfWeek.TUESDAY) {
                    dayOccurrences.add(1, dayOccurrences.get(1) + 1);
                    dayOccurrences.remove(2);
                }
                else if (localDate.getDayOfWeek() == DayOfWeek.WEDNESDAY) {
                    dayOccurrences.add(2, dayOccurrences.get(2) + 1);
                    dayOccurrences.remove(3);
                }
                else if (localDate.getDayOfWeek() == DayOfWeek.THURSDAY) {
                    dayOccurrences.add(3, dayOccurrences.get(3) + 1);
                    dayOccurrences.remove(4);
                }
                else if (localDate.getDayOfWeek() == DayOfWeek.FRIDAY) {
                    dayOccurrences.add(4, dayOccurrences.get(4) + 1);
                    dayOccurrences.remove(5);
                }
                else if (localDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
                    dayOccurrences.add(5, dayOccurrences.get(5) + 1);
                    dayOccurrences.remove(6);
                }
                else {
                    dayOccurrences.add(6, dayOccurrences.get(6) + 1);
                    dayOccurrences.remove(7);
                }
            }
            for (int i = 0; i < day.size(); i++) {
                dataSeriesVisitsPerDay.getData().add(new XYChart.Data(day.get(i), dayOccurrences.get(i)));
            }
            barChartVisitsPerDay.setMinWidth(900);
            barChartVisitsPerDay.getData().add(dataSeriesVisitsPerDay);
        });
    }
}
