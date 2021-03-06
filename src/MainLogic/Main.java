/*
  Main class
  - all the three different files are combined
  - all of the screens are made
  - coefficent for correlation is calculated
  - all the tables and graphs are made
*/

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
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import com.gembox.spreadsheet.*;
import com.gembox.spreadsheet.charts.*;
import org.shaded.apache.commons.collections4.list.TreeList;


public class Main extends Application {

    private Scene sceneMainView, sceneLogContext, sceneLogName, sceneFilterByGroup, sceneFilterByName, sceneTimeFrame, sceneDisplayAllSelectedData;
    private File logFile, studentFile, gradeFile;
    private MoodleLogsProcessing moodleLogsProcessing;
    private StudentInfoProcessing studentInfoProcessing;
    private GradesProcessing gradesProcessing;
    private List<Integer> mapValueEventName, mapValueEventContext, timeOccurrences;
    private List<String> mapKeyEventName, mapKeyEventContext, time;
    private Map<String, Integer> dataMapStudentGroup, dataMapStudentName;
    private Map<String, Map<String, Integer>> mapHourOccurrencesByEachDay;
    private Map<Date, Map<String, Integer>> mapDayOccurrencesByEachWeek;
    private List<Map<String, Double>> listOfCorrelationData;
    private ListView<String> listViewLogContext = new ListView<>();
    private ListView<String> listViewLogEventFinal = new ListView<>();
    private ListView<String> listViewLogEvent = new ListView<>();
    private ListView<String> listViewStudentNameFinal = new ListView<>();
    private ListView<String> listViewStudentName = new ListView<>();
    private ListView<String> listViewStudentGroup = new ListView<>();
    private TableView<Log> tableViewSelectedData = new TableView<>();
    private TableView<List<StringProperty>> tableViewSelectedStudentsGrades = new TableView<>();
    private TableView<List<StringProperty>> tableViewWeekAndDay = new TableView<>();
    private TableView<List<StringProperty>> tableViewDayAndHour = new TableView<>();
    private TableView<Correlation> tableViewCorrelation = new TableView<>();
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
    private CategoryAxis xAxisVisitsPerHour = new CategoryAxis();
    private NumberAxis xAxisCorrelationBetweenLogsAndGrades = new NumberAxis();
    private CategoryAxis xAxisVisitsPerWeekAndDay = new CategoryAxis();
    private CategoryAxis xAxisVisitsPerDayAndHour = new CategoryAxis();
    private NumberAxis yAxisEventName = new NumberAxis();
    private NumberAxis yAxisEventContext = new NumberAxis();
    private NumberAxis yAxisStudentGroup = new NumberAxis();
    private NumberAxis yAxisStudentName = new NumberAxis();
    private NumberAxis yAxisVisitsPerHour = new NumberAxis();
    private NumberAxis yAxisCorrelationBetweenLogsAndGrades = new NumberAxis();
    private NumberAxis yAxisVisitsPerWeekAndDay = new NumberAxis();
    private NumberAxis yAxisVisitsPerDayAndHour = new NumberAxis();
    private BarChart<Number, String> barChartEventName = new BarChart<>(yAxisEventName, xAxisEventName);
    private BarChart<Number, String> barChartEventContext = new BarChart<>(yAxisEventContext, xAxisEventContext);
    private BarChart<String, Number> barChartStudentGroup = new BarChart<>(xAxisStudentGroup, yAxisStudentGroup);
    private BarChart<String, Number> barChartStudentName = new BarChart<>(xAxisStudentName, yAxisStudentName);
    private BarChart<String, Number> barChartVisitsPerHour = new BarChart<>(xAxisVisitsPerHour, yAxisVisitsPerHour);
    private ScatterChart<Number, Number> scatterChartCorrelationBetweenLogsAndGrades = new ScatterChart<>(xAxisCorrelationBetweenLogsAndGrades, yAxisCorrelationBetweenLogsAndGrades);
    private StackedBarChart<String, Number> stackedBarChartVisitsPerWeekAndDay = new StackedBarChart<>(xAxisVisitsPerWeekAndDay, yAxisVisitsPerWeekAndDay);
    private StackedBarChart<String, Number> stackedBarChartVisitsPerDayAndHour = new StackedBarChart<>(xAxisVisitsPerDayAndHour, yAxisVisitsPerDayAndHour);
    private Label displayCoefficient = new Label();
    private String pattern = "dd.MM.YY";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    private String patternShort = "dd.MM";
    private SimpleDateFormat simpleDateFormatShort = new SimpleDateFormat(patternShort);

    public static void main(String[] args) {
        launch(args);
    }

    public Main() { }

    @Override
    public void start(Stage stage) {
        stage.setMinWidth(750);
        stage.setMinHeight(465);
        // All the used TitledPanes and TabPanes
        TitledPane titledPaneMainView = new TitledPane("Vajalike failide sisestamine", new Label());
        TitledPane titledPaneEventContext = new TitledPane("Vali sündmuse konteksti elemendid", new Label());
        TitledPane titledPaneEventName = new TitledPane("Vali sündmuse nime elemendid", new Label());
        TitledPane titledPaneStudentGroups = new TitledPane("Vali õpilaste gtupid", new Label());
        TitledPane titledPaneStudentName = new TitledPane("Vali õpilaste nimed", new Label());
        TitledPane titledPaneTimeFrame = new TitledPane("Vali ajaraam", new Label());
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
        Tab tabEventContextAndName = new Tab("Sündmuse kontekst ja nimi");
        Tab tabStudentGroupAndName = new Tab("Õpilase grupp ja nimi");
        Tab tabVisitsPerWeekAndDay = new Tab("Kirjete arv nädalate kaupa");
        Tab tabVisitsPerDayAndHour = new Tab("Kirjete arv päevade kaupa");
        Tab tabVisitsPerHour = new Tab("Kirjete arv tundide kaupa");
        Tab tabStudentGrades = new Tab("Õpilaste hinded");
        Tab tabCorrelationBetweenLogsAndGrades = new Tab("Hinnete ja logide korrelatsoon");
        // TextField for time
        TextField textFieldStart = new TextField(null);
        TextField textFieldEnd = new TextField(null);
        // TextField set text
        textFieldStart.setPromptText("tt.mm");
        textFieldEnd.setPromptText("tt.mm");
        // Make all tabs not closable
        tabPaneAnalysedData.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        // All the used GridPanes
        GridPane gridPaneMainView = new GridPane();
        GridPane gridPaneEventContext = new GridPane();
        GridPane gridPaneEventName = new GridPane();
        GridPane gridPaneStudentGroups = new GridPane();
        GridPane gridPaneStudentName = new GridPane();
        GridPane gridPaneTimeFrame = new GridPane();
        GridPane gridPaneTabAllSelectedData = new GridPane();
        GridPane gridPaneTabEventContextAndName = new GridPane();
        GridPane gridPaneTabStudentGroupAndName = new GridPane();
        GridPane gridPaneTabStudentGrades = new GridPane();
        GridPane gridPaneTabVisitsPerHour = new GridPane();
        GridPane gridPaneTabCorrelationBetweenLogsAndGrades = new GridPane();
        GridPane gridPaneTabVisitsPerWeekAndDay = new GridPane();
        GridPane gridPaneTabVisitsPerDayAndHour = new GridPane();
        GridPane gridPaneForTabPane = new GridPane();
        // Add insets for all Tabs
        gridPaneTabAllSelectedData.setPadding(new Insets(10));
        gridPaneTabEventContextAndName.setPadding(new Insets(10));
        gridPaneTabStudentGroupAndName.setPadding(new Insets(10));
        gridPaneTabStudentGrades.setPadding(new Insets(10));
        gridPaneTabVisitsPerHour.setPadding(new Insets(10));
        gridPaneTabCorrelationBetweenLogsAndGrades.setPadding(new Insets(10));
        gridPaneTabVisitsPerWeekAndDay.setPadding(new Insets(10));
        gridPaneTabVisitsPerDayAndHour.setPadding(new Insets(10));
        // All the used Labels
        Label labelLog = new Label("Moodle'i logid (.csv) ");
        Label labelStudent = new Label("Õpilaste andmed (.xls) ");
        Label labelGrade = new Label("Õpilaste hinded (.xlsx) ");
        Label labelLogFileName = new Label();
        Label labelStudentFileName =  new Label();
        Label labelGradeFileName = new Label();
        Label labelDateFrom = new Label("Alguskuupäev ");
        Label labelDateTo = new Label("Lõpukuupäev ");
        Label labelTimeFrom = new Label("Alguskellaaeg ");
        Label labelTimeTo = new Label("Lõpukellaaeg ");
        // All the used Buttons
        Button buttonChooseLogFile = new Button("Ava logide fail");
        Button buttonChooseStudentFile = new Button("Ava õpilaste fail");
        Button buttonChooseGradeFile = new Button("Ava hinnete fail");
        Button buttonGoToChooseEventContext = new Button("Edasi");
        Button buttonGoToChooseEventName = new Button("Edasi");
        Button buttonGoBackToMainView = new Button("Tagasi");
        Button buttonGoToChooseStudentGroup = new Button("Edasi");
        Button buttonGoBackToChooseEventContext = new Button("Tagasi");
        Button buttonGoToChooseStudentName = new Button("Edasi");
        Button buttonGoBackToChooseEventName = new Button("Tagasi");
        Button buttonGoToChooseTimeFrame = new Button("Edasi");
        Button buttonGoBackToChooseStudentGroup = new Button("Tagasi");
        Button buttonGoToDisplayAllChosenData = new Button("Edasi");
        Button buttonGoBackToChooseStudentName = new Button("Tagasi");
        Button buttonRemoveDateAndTime = new Button("Tühjenda väljad");
        Button buttonGoBackToTimeFrame = new Button("Tagasi");
        Button saveBarChartEventContext = new Button("Salvesta sündmuse konteksti graafik");
        Button saveBarChartEventName = new Button("Salvesta sündmuse nime graafik");
        Button saveBarChartStudentGroup = new Button("Salvesta õpilaste rühmade graafik");
        Button saveBarChartStudentName = new Button("Salvesta õpilaste nimede graafik");
        Button saveBarChartVisitsPerHour = new Button("Salvesta graafik");
        Button saveTableViewGradeData = new Button("Salvesta tabel");
        Button saveTableViewLogData = new Button("Salvesta tabel");
        Button saveTableViewCorrelationBetweenLogsAndGrades = new Button("Salvesta tabel");
        Button saveStackedBarChartVisitsPerWeekAndDay = new Button("Salvesta graafik");
        Button saveStackedBarChartVisitsPerDayAndHour = new Button("Salvesta graafik");
        Button saveTableViewStackedBarChartVisitsPerWeekAndDay = new Button("Salvesta tabel");
        Button saveTableViewStackedBarChartVisitsPerDayAndHour = new Button("Salvesta tabel");
        // Creating HBoxes
        HBox hBoxBarChartEvent = new HBox(10);
        hBoxBarChartEvent.getChildren().addAll(saveBarChartEventContext, saveBarChartEventName);
        HBox hBoxBarChartStudent = new HBox(10);
        hBoxBarChartStudent.getChildren().addAll(saveBarChartStudentGroup, saveBarChartStudentName);
        HBox hBoxDayAndHour = new HBox(10);
        hBoxDayAndHour.getChildren().addAll(saveStackedBarChartVisitsPerDayAndHour, saveTableViewStackedBarChartVisitsPerDayAndHour);
        HBox hBoxWeekAndDay = new HBox(10);
        hBoxWeekAndDay.getChildren().addAll(saveStackedBarChartVisitsPerWeekAndDay, saveTableViewStackedBarChartVisitsPerWeekAndDay);
        HBox hBoxGoBackToTimeFrame = new HBox();
        hBoxGoBackToTimeFrame.getChildren().add(buttonGoBackToTimeFrame);
        hBoxGoBackToTimeFrame.setPadding(new Insets(5, 5, 10, 10));
        // Making ListViews and TableView selection models to multiple
        listViewLogContext.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listViewLogEvent.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listViewStudentGroup.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        listViewStudentName.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableViewSelectedData.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableViewSelectedStudentsGrades.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableViewWeekAndDay.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        tableViewDayAndHour.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // Making TableView width to 200
        tableViewDayAndHour.setMaxHeight(200);
        // Making TextFields to filter ListViews
        TextField textFieldFilteredLogContext = new TextField();
        TextField textFieldFilteredLogEvent = new TextField();
        TextField textFieldFilteredStudentGroup = new TextField();
        TextField textFieldFilteredStudentName = new TextField();
        // Setting TextField text
        textFieldFilteredLogContext.setPromptText("Otsi sündmuse konteksti järgi");
        textFieldFilteredLogEvent.setPromptText("Otsi sündmuse nime järgi");
        textFieldFilteredStudentGroup.setPromptText("Otsi õpilaste rühma järgi");
        textFieldFilteredStudentName.setPromptText("Otsi õpilase nime järgi");
        // Creating filters/searches for the ListViews
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
        // Creating DatePicker
        DatePicker datePickerFrom = new DatePicker();
        DatePicker datePickerTo = new DatePicker();
        // Set DatePicker format to dd.MM.yyyy
        StringConverter<LocalDate> stringConverter = new StringConverter<>()
        {
            private DateTimeFormatter dateTimeFormatter=DateTimeFormatter.ofPattern("dd.MM.yyyy");

            @Override
            public String toString(LocalDate localDate)
            {
                if(localDate==null)
                    return "";
                return dateTimeFormatter.format(localDate);
            }

            @Override
            public LocalDate fromString(String dateString)
            {
                if(dateString==null || dateString.trim().isEmpty())
                {
                    return null;
                }
                return LocalDate.parse(dateString,dateTimeFormatter);
            }
        };
        datePickerFrom.setConverter(stringConverter);
        datePickerTo.setConverter(stringConverter);
        datePickerFrom.setPromptText("pp.kk.aaaa");
        datePickerTo.setPromptText("pp.kk.aaaa");
        // Creating the table for all the selected information from logs
        TableColumn<Log, String> columnLogTime = new TableColumn<>("Aeg");
        TableColumn<Log, String> columnStudentName = new TableColumn<>("Nimi");
        TableColumn<Log, String> columnEventContext = new TableColumn<>("Sündmuse kontekst");
        TableColumn<Log, String> columnEventName = new TableColumn<>("Sündmuse nimi");
        TableColumn<Log, String> columnStudentGroup = new TableColumn<>("Rühm");
        TableColumn<Correlation, Double> columnCorrelationGrade = new TableColumn<>("Hinne");
        TableColumn<Correlation, Double> columnCorrelationLogs = new TableColumn<>("Logid");
        columnLogTime.setCellValueFactory(new PropertyValueFactory<>("time"));
        columnStudentName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        columnEventContext.setCellValueFactory(new PropertyValueFactory<>("eventContext"));
        columnEventName.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        columnStudentGroup.setCellValueFactory(new PropertyValueFactory<>("studentGroup"));
        columnCorrelationGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        columnCorrelationLogs.setCellValueFactory(new PropertyValueFactory<>("logs"));
        tableViewSelectedData.getColumns().add(columnLogTime);
        tableViewSelectedData.getColumns().add(columnStudentName);
        tableViewSelectedData.getColumns().add(columnEventContext);
        tableViewSelectedData.getColumns().add(columnEventName);
        tableViewSelectedData.getColumns().add(columnStudentGroup);
        tableViewCorrelation.getColumns().add(columnCorrelationGrade);
        tableViewCorrelation.getColumns().add(columnCorrelationLogs);
        // Adding elements to GridPanes
        gridPaneMainView.add(labelStudent, 0, 1);
        gridPaneMainView.add(labelGrade, 0, 2 );
        gridPaneMainView.add(labelLog,0, 0);
        gridPaneMainView.add(labelStudentFileName, 1, 1);
        gridPaneMainView.add(labelGradeFileName, 1, 2);
        gridPaneMainView.add(labelLogFileName,1, 0);
        gridPaneMainView.add(buttonChooseLogFile,2, 0);
        gridPaneMainView.add(buttonChooseStudentFile, 2, 1);
        gridPaneMainView.add(buttonChooseGradeFile, 2, 2 );
        gridPaneMainView.add(buttonGoToChooseEventContext, 2, 3);
        gridPaneMainView.setVgap(15);
        gridPaneEventContext.add(listViewLogContext, 0,  1, 2, 1);
        gridPaneEventName.add(listViewLogEvent, 0, 1, 2, 1);
        gridPaneStudentGroups.add(listViewStudentGroup, 0, 1, 2, 1);
        gridPaneStudentName.add(listViewStudentName, 0, 1, 2, 1);
        gridPaneEventContext.add(textFieldFilteredLogContext, 0, 0, 2, 1);
        gridPaneEventName.add(textFieldFilteredLogEvent, 0, 0,2 ,1);
        gridPaneStudentGroups.add(textFieldFilteredStudentGroup, 0, 0, 2, 1);
        gridPaneStudentName.add(textFieldFilteredStudentName, 0, 0, 2, 1);
        gridPaneEventContext.add(buttonGoBackToMainView, 0, 2);
        gridPaneEventContext.add(buttonGoToChooseEventName, 1, 2);
        gridPaneEventName.add(buttonGoBackToChooseEventContext, 0, 2);
        gridPaneEventName.add(buttonGoToChooseStudentGroup, 1, 2);
        gridPaneStudentGroups.add(buttonGoBackToChooseEventName, 0, 2);
        gridPaneStudentGroups.add(buttonGoToChooseStudentName, 1, 2);
        gridPaneStudentName.add(buttonGoBackToChooseStudentGroup, 0, 2);
        gridPaneStudentName.add(buttonGoToChooseTimeFrame, 1, 2);
        gridPaneTimeFrame.add(buttonRemoveDateAndTime, 0, 4);
        gridPaneTimeFrame.add(labelDateFrom, 0, 0);
        gridPaneTimeFrame.add(labelDateTo, 2, 0);
        gridPaneTimeFrame.add(datePickerFrom, 0, 1, 2, 1);
        gridPaneTimeFrame.add(datePickerTo, 2, 1, 2, 1);
        gridPaneTimeFrame.add(labelTimeFrom, 0, 2);
        gridPaneTimeFrame.add(labelTimeTo, 2, 2);
        gridPaneTimeFrame.add(buttonGoBackToChooseStudentName, 0, 5);
        gridPaneTimeFrame.add(buttonGoToDisplayAllChosenData, 2, 5);
        gridPaneTimeFrame.add(textFieldStart, 0, 3, 2, 1);
        gridPaneTimeFrame.add(textFieldEnd, 2, 3, 2, 1);
        gridPaneTabAllSelectedData.add(tableViewSelectedData, 0, 0, 2, 1);
        gridPaneTabEventContextAndName.add(barChartEventName, 0, 1);
        gridPaneTabEventContextAndName.add(barChartEventContext, 0, 0);
        gridPaneTabStudentGroupAndName.add(barChartStudentGroup, 0, 0);
        gridPaneTabStudentGroupAndName.add(barChartStudentName, 0, 1);
        gridPaneTabStudentGrades.add(tableViewSelectedStudentsGrades, 0, 0 ,2, 1);
        gridPaneTabVisitsPerHour.add(barChartVisitsPerHour, 0, 0);
        gridPaneTabCorrelationBetweenLogsAndGrades.add(tableViewCorrelation, 0, 1);
        gridPaneTabCorrelationBetweenLogsAndGrades.add(scatterChartCorrelationBetweenLogsAndGrades, 0, 0);
        gridPaneTabCorrelationBetweenLogsAndGrades.add(displayCoefficient, 0, 2);
        gridPaneTabVisitsPerWeekAndDay.add(stackedBarChartVisitsPerWeekAndDay, 0, 0);
        gridPaneTabVisitsPerDayAndHour.add(stackedBarChartVisitsPerDayAndHour, 0, 0);
        gridPaneTabVisitsPerWeekAndDay.add(tableViewWeekAndDay, 0, 1);
        gridPaneTabVisitsPerDayAndHour.add(tableViewDayAndHour, 0, 1);
        gridPaneTabAllSelectedData.add(saveTableViewLogData, 0, 1);
        gridPaneTabEventContextAndName.add(hBoxBarChartEvent, 0, 2);
        gridPaneTabStudentGroupAndName.add(hBoxBarChartStudent, 0, 2);
        gridPaneTabStudentGrades.add(saveTableViewGradeData, 0, 1);
        gridPaneTabVisitsPerHour.add(saveBarChartVisitsPerHour, 0, 1);
        gridPaneTabCorrelationBetweenLogsAndGrades.add(saveTableViewCorrelationBetweenLogsAndGrades, 0, 3);
        gridPaneTabVisitsPerWeekAndDay.add(hBoxWeekAndDay, 0, 2);
        gridPaneTabVisitsPerDayAndHour.add(hBoxDayAndHour, 0, 2);
        gridPaneForTabPane.add(hBoxGoBackToTimeFrame, 0, 1);
        gridPaneEventContext.setVgap(15);
        gridPaneEventName.setVgap(15);
        gridPaneStudentGroups.setVgap(15);
        gridPaneStudentName.setVgap(15);
        gridPaneTimeFrame.setVgap(15);
        gridPaneTimeFrame.setHgap(15);
        gridPaneTabAllSelectedData.setVgap(15);
        gridPaneTabStudentGrades.setVgap(15);
        gridPaneTabAllSelectedData.setVgap(15);
        gridPaneTabEventContextAndName.setVgap(15);
        gridPaneTabStudentGroupAndName.setVgap(15);
        gridPaneTabStudentGrades.setVgap(15);
        gridPaneTabVisitsPerHour.setVgap(15);
        gridPaneTabCorrelationBetweenLogsAndGrades.setVgap(15);
        gridPaneTabVisitsPerWeekAndDay.setVgap(15);
        gridPaneTabVisitsPerDayAndHour.setVgap(15);
        gridPaneEventContext.setHgap(15);
        gridPaneEventName.setHgap(15);
        gridPaneStudentGroups.setHgap(15);
        gridPaneStudentName.setHgap(15);
        // Add elements to Tabs
        tabAllSelectedData.setContent(gridPaneTabAllSelectedData);
        ScrollPane scrollPaneWeekAndDay = new ScrollPane();
        scrollPaneWeekAndDay.setContent(gridPaneTabVisitsPerWeekAndDay);
        ScrollPane scrollPaneDayAndHour = new ScrollPane();
        scrollPaneDayAndHour.setContent(gridPaneTabVisitsPerDayAndHour);
        ScrollPane scrollPaneEventContextAndName = new ScrollPane();
        scrollPaneEventContextAndName.setContent(gridPaneTabEventContextAndName);
        ScrollPane scrollPaneStudentGroupAndName = new ScrollPane();
        scrollPaneStudentGroupAndName.setContent(gridPaneTabStudentGroupAndName);
        ScrollPane scrollPaneCorrelation = new ScrollPane();
        scrollPaneCorrelation.setContent(gridPaneTabCorrelationBetweenLogsAndGrades);
        tabEventContextAndName.setContent(scrollPaneEventContextAndName);
        tabStudentGroupAndName.setContent(scrollPaneStudentGroupAndName);
        tabStudentGrades.setContent(gridPaneTabStudentGrades);
        tabVisitsPerWeekAndDay.setContent(scrollPaneWeekAndDay);
        tabVisitsPerDayAndHour.setContent(scrollPaneDayAndHour);
        tabVisitsPerHour.setContent(gridPaneTabVisitsPerHour);
        tabCorrelationBetweenLogsAndGrades.setContent(scrollPaneCorrelation);
        // Adding elements to TitlePanes and TabPane
        titledPaneMainView.setContent(gridPaneMainView);
        titledPaneEventContext.setContent(gridPaneEventContext);
        titledPaneEventName.setContent(gridPaneEventName);
        titledPaneStudentGroups.setContent(gridPaneStudentGroups);
        titledPaneStudentName.setContent(gridPaneStudentName);
        titledPaneTimeFrame.setContent(gridPaneTimeFrame);
        tabPaneAnalysedData.getTabs().addAll(tabAllSelectedData, tabEventContextAndName, tabStudentGroupAndName, tabVisitsPerWeekAndDay, tabVisitsPerDayAndHour, tabVisitsPerHour, tabStudentGrades, tabCorrelationBetweenLogsAndGrades);
        // GridPane connected with TabPane
        gridPaneForTabPane.add(tabPaneAnalysedData, 0, 0);
        // Creating the new scenes
        sceneMainView = new Scene(titledPaneMainView);
        sceneLogContext = new Scene(titledPaneEventContext);
        sceneLogName = new Scene(titledPaneEventName);
        sceneFilterByGroup = new Scene(titledPaneStudentGroups);
        sceneFilterByName = new Scene(titledPaneStudentName);
        sceneTimeFrame = new Scene(titledPaneTimeFrame);
        sceneDisplayAllSelectedData = new Scene(gridPaneForTabPane);
        // Button setOnAction()'s
        buttonChooseLogFile.setOnAction(e -> {
            logFile = fileChooserLog.showOpenDialog(stage);
            if (logFile != null) {
                fileChooserStudent.setInitialDirectory(logFile.getParentFile());
                fileChooserGrade.setInitialDirectory(logFile.getParentFile());
                fileChooserLog.setInitialDirectory(logFile.getParentFile());
                labelLogFileName.setText(logFile.getName() + "  ");
            }
        });
        buttonChooseStudentFile.setOnAction(e -> {
            studentFile = fileChooserStudent.showOpenDialog(stage);
            if (studentFile != null) {
                fileChooserStudent.setInitialDirectory(studentFile.getParentFile());
                fileChooserGrade.setInitialDirectory(studentFile.getParentFile());
                fileChooserLog.setInitialDirectory(studentFile.getParentFile());
                labelStudentFileName.setText(studentFile.getName() + " ");
            }
        });
        buttonChooseGradeFile.setOnAction(e -> {
            gradeFile = fileChooserGrade.showOpenDialog(stage);
            if (gradeFile != null) {
                fileChooserStudent.setInitialDirectory(gradeFile.getParentFile());
                fileChooserGrade.setInitialDirectory(gradeFile.getParentFile());
                fileChooserLog.setInitialDirectory(gradeFile.getParentFile());
                labelGradeFileName.setText(gradeFile.getName() + " ");
            }
        });

        buttonGoToChooseEventContext.setOnAction(e -> {
            Alert alertPleaseWait = new Alert(Alert.AlertType.INFORMATION);
            alertPleaseWait.setTitle("Andmete töötlemine.");
            alertPleaseWait.setHeaderText("Palun oota! Andmeid loetakse sisse. \nAken sulgub automaatselt.");
            alertPleaseWait.setContentText("Kui uut vaadet ei kuvata 3 minuti jooksul, siis taaskäivitage programm.");
            alertPleaseWait.show();
            if (gradeFile == null) {
                displayErrorAlert("Hinnete faili pole valitud!", "Pead sisestama hinnete faili.");
            }
            else {
                gradesProcessing = new GradesProcessing();
                gradesProcessing.processGrades(gradeFile);
            }
            if (studentFile == null) {
                displayErrorAlert("Õpilaste faili pole valitud!", "Pead sisestama õpilaste faili.");
            }
            else {
                studentInfoProcessing = new StudentInfoProcessing();
                studentInfoProcessing.processStudents(studentFile);
            }
            if (logFile == null) {
                displayErrorAlert("Logide faili pole valitud!", "Pead sisestama moodle'i logide faili.");
            }
            else {
                moodleLogsProcessing = new MoodleLogsProcessing();
                moodleLogsProcessing.processLogs(logFile);
            }
            alertPleaseWait.close();
            if (logFile != null && studentFile != null && gradeFile != null) {
                if (!gradesProcessing.getColumnNames().contains("Hinne (Punktid)")) {
                    displayInformationAlert("Vigane hinnete fail!", "Hinnete failis puudub veerg - \"Hinne (Punktid)\"!", "Korrelatsiooni pole võimalik arvutada.");
                }
                else {
                    displayEventContext(stage);
                    stage.setScene(sceneLogContext);
                }
            }
        });
        // Forward
        buttonGoToChooseEventName.setOnAction(e -> {
            observableListLogContext = listViewLogContext.getSelectionModel().getSelectedItems();
            if (observableListLogContext.isEmpty()) { observableListLogContext = listViewLogContext.getItems(); }
            displayEventName(stage);
            stage.setScene(sceneLogName);
        });
        buttonGoToChooseStudentGroup.setOnAction(e -> {
            observableListLogEvent = listViewLogEvent.getSelectionModel().getSelectedItems();
            if (observableListLogEvent.isEmpty()) { observableListLogEvent = listViewLogEvent.getItems(); }
            displayStudentGroup(stage);
            stage.setScene(sceneFilterByGroup);
        });
        buttonGoToChooseStudentName.setOnAction(e -> {
            observableListStudentGroup = listViewStudentGroup.getSelectionModel().getSelectedItems();
            if (observableListStudentGroup.isEmpty()) { observableListStudentGroup = listViewStudentGroup.getItems(); }
            displayStudentName(stage);
            stage.setScene(sceneFilterByName);
        });
        buttonGoToChooseTimeFrame.setOnAction(e -> {
            observableListStudentName = listViewStudentName.getSelectionModel().getSelectedItems();
            if (observableListStudentName.isEmpty()) { observableListStudentName = listViewStudentName.getItems(); }
            stage.setScene(sceneTimeFrame);
        });
        buttonGoToDisplayAllChosenData.setOnAction(e -> {
            Alert alertPleaseWait = new Alert(Alert.AlertType.INFORMATION);
            alertPleaseWait.setTitle("Andmete töötlemine.");
            alertPleaseWait.setHeaderText("Palun oota! Andmeid töödeldakse. \nAken sulgub automaatselt.");
            alertPleaseWait.setContentText("Kui uut vaadet ei kuvata 5 minuti jooksul, siis taaskäivitage programm.");
            alertPleaseWait.show();
            localDateFrom = datePickerFrom.getValue();
            localDateTo = datePickerTo.getValue();
            if (localDateFrom == null && localDateTo != null) {
                alertPleaseWait.close();
                displayErrorAlert("Alguskuupäeva pole valitud!", "Palun vali alguskuupäev.");
            }
            else if (localDateFrom != null && localDateTo == null) {
                alertPleaseWait.close();
                displayErrorAlert("Lõpukuupäev pole valitud!", "Palun vali lõpukuupäev.");
            }
            else if (localDateFrom != null) {
                if (textFieldEnd.getText() == null && textFieldStart.getText() != null) {
                    alertPleaseWait.close();
                    displayErrorAlert("Lõpuaeg on valimata!", "Palun vali lõpuaeg.");
                }
                else if (textFieldEnd.getText() != null && textFieldStart.getText() == null) {
                    alertPleaseWait.close();
                    displayErrorAlert("Algusaeg on valimata!", "Palun vali alguaeg.");
                }
                else if (textFieldEnd.getText() != null) {
                    DateTimeFormatter dateTimeFormatterTime = DateTimeFormatter.ofPattern("HH.mm");
                    try {
                        localTimeFrom = textFieldStart.getText();
                        localTimeTo = textFieldEnd.getText();
                        LocalTime.parse(localTimeFrom, dateTimeFormatterTime);
                        LocalTime.parse(localTimeTo, dateTimeFormatterTime);
                        alertPleaseWait.close();
                        runMethodsForTabView(stage);
                        stage.setScene(sceneDisplayAllSelectedData);
                    }
                    catch (Exception ex) {
                        localTimeFrom = null;
                        localTimeTo = null;
                        alertPleaseWait.close();
                        displayErrorAlert("Aeg on vales formaadis!", "Palun kirjuta aeg korrektselt.");
                    }

                }
                else {
                    localTimeFrom = null;
                    localTimeTo = null;
                    alertPleaseWait.close();
                    runMethodsForTabView(stage);
                    stage.setScene(sceneDisplayAllSelectedData);
                }
            }
            else if (textFieldEnd.getText() != null || textFieldStart.getText() != null) {
                alertPleaseWait.close();
                displayErrorAlert("Koos ajaga peab ka kuupäeva valima!", "Palun vali kuupäevad.");
            }
            else {
                alertPleaseWait.close();
                runMethodsForTabView(stage);
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
        buttonGoBackToChooseStudentGroup.setOnAction(e -> {
            listViewStudentName.setItems(FXCollections.observableArrayList());
            observableListStudentName = FXCollections.observableArrayList();
            observableListStudentGroup = FXCollections.observableArrayList();
            stage.setScene(sceneFilterByGroup);
        });
        buttonGoBackToChooseStudentName.setOnAction(e -> {
            makeLocalDateAndTimeNull();
            datePickerFrom.setValue(null);
            datePickerTo.setValue(null);
            textFieldStart.setText(null);
            textFieldEnd.setText(null);
            observableListStudentName = FXCollections.observableArrayList();
            stage.setScene(sceneFilterByName);
        });
        buttonGoBackToTimeFrame.setOnAction(e -> {
            makeLocalDateAndTimeNull();
            textFieldStart.setText(null);
            textFieldEnd.setText(null);
            datePickerFrom.setValue(null);
            datePickerTo.setValue(null);
            filteredResults = new ArrayList<>();
            barChartEventName.getData().clear();
            barChartEventContext.getData().clear();
            barChartStudentGroup.getData().clear();
            barChartStudentName.getData().clear();
            barChartVisitsPerHour.getData().clear();
            scatterChartCorrelationBetweenLogsAndGrades.getData().clear();
            stackedBarChartVisitsPerDayAndHour.getData().clear();
            stackedBarChartVisitsPerWeekAndDay.getData().clear();
            tableViewSelectedData.setItems(FXCollections.observableArrayList());
            tableViewSelectedStudentsGrades.getItems().clear();
            tableViewWeekAndDay.getItems().clear();
            tableViewDayAndHour.getItems().clear();
            tableViewCorrelation.getItems().clear();
            tableViewSelectedStudentsGrades.getColumns().clear();
            tableViewWeekAndDay.getColumns().clear();
            tableViewDayAndHour.getColumns().clear();
            tableViewCorrelation.getColumns().clear();
            displayCoefficient.setText("");
            stage.setScene(sceneTimeFrame);
        });
        // Extra Button actions
        buttonRemoveDateAndTime.setOnAction(e -> {
            makeLocalDateAndTimeNull();
            datePickerFrom.setValue(null);
            datePickerTo.setValue(null);
            textFieldStart.setText(null);
            textFieldEnd.setText(null);
        });
        // Exporting TableViews
        saveTableViewLogData.setOnAction(e -> {
            Workbook workbook = new HSSFWorkbook();
            Sheet spreadsheet = workbook.createSheet("TableViewLogs");
            Row row = spreadsheet.createRow(0);
            for (int j = 0; j < tableViewSelectedData.getColumns().size(); j++) {
                row.createCell(j).setCellValue(tableViewSelectedData.getColumns().get(j).getText());
            }
            try {
                for (int i = 0; i < tableViewSelectedData.getItems().size(); i++) {
                    row = spreadsheet.createRow(i + 1);
                    for (int j = 0; j < tableViewSelectedData.getColumns().size(); j++) {
                        if (tableViewSelectedData.getColumns().get(j).getCellData(i) != null) {
                            row.createCell(j).setCellValue(tableViewSelectedData.getColumns().get(j).getCellData(i).toString());
                        } else {
                            row.createCell(j).setCellValue("");
                        }
                    }
                }
                try {
                    FileOutputStream fileOut = new FileOutputStream("TableViewLogs.xls");
                    workbook.write(fileOut);
                    fileOut.close();
                    displayInformationAlert("Fail salvestati!", "Fail on salvestatud!", "Fail salvestati programmiga samasse kausta.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    displayErrorAlert("Faili ei salvestatud!", "Sulge vana fail ja proovi uuesti.");
                }
            }
            catch (IllegalArgumentException ex) {
                displayErrorAlert("Faili ei salvestatud", "Failis on liiga palju ridu lubatud on ainult 65535 rida.");
            }
        });
        saveTableViewGradeData.setOnAction(e -> {
            Workbook workbook = new HSSFWorkbook();
            Sheet spreadsheet = workbook.createSheet("TableViewGrades");
            Row row = spreadsheet.createRow(0);
            for (int j = 0; j < tableViewSelectedStudentsGrades.getColumns().size(); j++) {
                row.createCell(j).setCellValue(tableViewSelectedStudentsGrades.getColumns().get(j).getText());
            }
            for (int i = 0; i < tableViewSelectedStudentsGrades.getItems().size(); i++) {
                row = spreadsheet.createRow(i + 1);
                for (int j = 0; j < tableViewSelectedStudentsGrades.getColumns().size(); j++) {
                    if(tableViewSelectedStudentsGrades.getColumns().get(j).getCellData(i) != null) {
                        try {
                            row.createCell(j).setCellValue(Double.valueOf(tableViewSelectedStudentsGrades.getColumns().get(j).getCellData(i).toString()));
                        }
                        catch (NumberFormatException nr) {
                            row.createCell(j).setCellValue(tableViewSelectedStudentsGrades.getColumns().get(j).getCellData(i).toString());
                        }
                    }
                    else {
                        row.createCell(j).setCellValue("");
                    }
                }
            }
            try {
                FileOutputStream fileOut = new FileOutputStream("TableViewGrades.xls");
                workbook.write(fileOut);
                fileOut.close();
                displayInformationAlert("Fail salvestati!", "Fail on salvestatud!", "Fail salvestati programmiga samasse kausta.");
            } catch (Exception ex) {
                ex.printStackTrace();
                displayErrorAlert("Faili ei salvestatud!", "Sulge vana fail ja proovi uuesti.");
            }
        });
        saveTableViewStackedBarChartVisitsPerWeekAndDay.setOnAction(e -> {
            Workbook workbook = new HSSFWorkbook();
            Sheet spreadsheet = workbook.createSheet("TableViewWeekAndDay");
            Row row = spreadsheet.createRow(0);
            for (int j = 0; j < tableViewWeekAndDay.getColumns().size(); j++) {
                row.createCell(j).setCellValue(tableViewWeekAndDay.getColumns().get(j).getText());
            }
            for (int i = 0; i < tableViewWeekAndDay.getItems().size(); i++) {
                row = spreadsheet.createRow(i + 1);
                for (int j = 0; j < tableViewWeekAndDay.getColumns().size(); j++) {
                    if(tableViewWeekAndDay.getColumns().get(j).getCellData(i) != null) {
                        if (j != 0) {
                            row.createCell(j).setCellValue(Integer.valueOf(tableViewWeekAndDay.getColumns().get(j).getCellData(i).toString()));
                        }
                        else {
                            row.createCell(j).setCellValue(tableViewWeekAndDay.getColumns().get(j).getCellData(i).toString());
                        }
                    }
                    else {
                        row.createCell(j).setCellValue("");
                    }
                }
            }
            try {
                FileOutputStream fileOut = new FileOutputStream("TableViewWeekAndDay.xls");
                workbook.write(fileOut);
                fileOut.close();
                displayInformationAlert("Fail salvestati!", "Fail on salvestatud!", "Fail salvestati programmiga samasse kausta.");
            } catch (Exception ex) {
                ex.printStackTrace();
                displayErrorAlert("Faili ei salvestatud!", "Sulge vana fail ja proovi uuesti.");
            }
        });
        saveTableViewStackedBarChartVisitsPerDayAndHour.setOnAction(e -> {
            Workbook workbook = new HSSFWorkbook();
            Sheet spreadsheet = workbook.createSheet("TableViewDayAndHour");
            Row row = spreadsheet.createRow(0);
            for (int j = 0; j < tableViewDayAndHour.getColumns().size(); j++) {
                row.createCell(j).setCellValue(tableViewDayAndHour.getColumns().get(j).getText());
            }
            for (int i = 0; i < tableViewDayAndHour.getItems().size(); i++) {
                row = spreadsheet.createRow(i + 1);
                for (int j = 0; j < tableViewDayAndHour.getColumns().size(); j++) {
                    if(tableViewDayAndHour.getColumns().get(j).getCellData(i) != null) {
                        if (j != 0) {
                            row.createCell(j).setCellValue(Integer.valueOf(tableViewDayAndHour.getColumns().get(j).getCellData(i).toString()));
                        }
                        else {
                            row.createCell(j).setCellValue(tableViewDayAndHour.getColumns().get(j).getCellData(i).toString());
                        }
                    }
                    else {
                        row.createCell(j).setCellValue("");
                    }
                }
            }
            try {
                FileOutputStream fileOut = new FileOutputStream("TableViewDayAndHour.xls");
                workbook.write(fileOut);
                fileOut.close();
                displayInformationAlert("Fail salvestati!", "Fail on salvestatud!", "Fail salvestati programmiga samasse kausta.");
            } catch (Exception ex) {
                ex.printStackTrace();
                displayErrorAlert("Faili ei salvestatud!", "Sulge vana fail ja proovi uuesti.");
            }
        });
        saveTableViewCorrelationBetweenLogsAndGrades.setOnAction(e -> {
            Workbook workbook = new HSSFWorkbook();
            Sheet spreadsheet = workbook.createSheet("TableViewCorrelation");
            Row row = spreadsheet.createRow(0);
            for (int j = 0; j < tableViewCorrelation.getColumns().size(); j++) {
                row.createCell(j).setCellValue(tableViewCorrelation.getColumns().get(j).getText());
            }
            for (int i = 0; i < tableViewCorrelation.getItems().size(); i++) {
                row = spreadsheet.createRow(i + 1);
                for (int j = 0; j < tableViewCorrelation.getColumns().size(); j++) {
                    if(tableViewCorrelation.getColumns().get(j).getCellData(i) != null) {
                        row.createCell(j).setCellValue(Double.valueOf(tableViewCorrelation.getColumns().get(j).getCellData(i).toString()));

                    }
                    else {
                        row.createCell(j).setCellValue("");
                    }
                }
            }
            try {
                FileOutputStream fileOut = new FileOutputStream("TableViewCorrelation.xls");
                workbook.write(fileOut);
                fileOut.close();
                displayInformationAlert("Fail salvestati!", "Fail on salvestatud!", "Fail salvestati programmiga samasse kausta.");
            } catch (Exception ex) {
                ex.printStackTrace();
                displayErrorAlert("Faili ei salvestatud!", "Sulge vana fail ja proovi uuesti.");
            }
        });
        // Exporting BarCharts
        saveBarChartEventContext.setOnAction(e -> {
            SpreadsheetInfo.setLicense("FREE-LIMITED-KEY");
            ExcelFile workbook = new ExcelFile();
            ExcelWorksheet worksheet = workbook.addWorksheet("BarChartEventContext");
            int numberOfEventContext = mapKeyEventContext.size();
            // Create Excel chart and select data for it.
            ExcelChart chart = worksheet.getCharts().add(ChartType.BAR, "D2", "M25");
            chart.selectData(worksheet.getCells().getSubrangeAbsolute(0, 0, numberOfEventContext, 1), true);
            // Add data which is used by the Excel chart.
            String[] names = mapKeyEventContext.toArray(new String[0]);
            for (int i = 0; i < numberOfEventContext; i++) {
                worksheet.getCell(i + 1, 0).setValue(names[i % names.length] + (i < names.length ? "" : " " + (i / names.length + 1)));
                worksheet.getCell(i + 1, 1).setValue(mapValueEventContext.get(i));
            }
            // Set header row and formatting.
            worksheet.getCell(0, 0).setValue("Sündmuse kontekst");
            worksheet.getCell(0, 1).setValue("Esinemine logides");
            worksheet.getCell(0, 1).getStyle().getFont().setWeight(ExcelFont.BOLD_WEIGHT);
            worksheet.getCell(0, 0).getStyle().getFont().setWeight(ExcelFont.BOLD_WEIGHT);
            worksheet.getColumn(0).setWidth((int) LengthUnitConverter.convert(3, LengthUnit.CENTIMETER, LengthUnit.ZERO_CHARACTER_WIDTH_256_TH_PART));
            try {
                workbook.save("BarChartEventContext.xlsx");
                displayInformationAlert("Fail salvestati!", "Fail on salvestatud!", "Fail salvestati programmiga samasse kausta.");
            } catch (IOException ex) {
                ex.printStackTrace();
                displayErrorAlert("Faili ei salvestatud!", "Sulge vana fail ja proovi uuesti.");
            } catch (Exception ex) {
                displayErrorAlert("Faili ei salvestatud!", "Fail on salvestamise jaoks liiga suur.");
            }
        });
        saveBarChartEventName.setOnAction(e -> {
            SpreadsheetInfo.setLicense("FREE-LIMITED-KEY");
            ExcelFile workbook = new ExcelFile();
            ExcelWorksheet worksheet = workbook.addWorksheet("BarChartEventName");
            int numberOfEventContext = mapKeyEventName.size();
            // Create Excel chart and select data for it.
            ExcelChart chart = worksheet.getCharts().add(ChartType.BAR, "D2", "M25");
            chart.selectData(worksheet.getCells().getSubrangeAbsolute(0, 0, numberOfEventContext, 1), true);
            // Add data which is used by the Excel chart.
            String[] names = mapKeyEventName.toArray(new String[0]);
            for (int i = 0; i < numberOfEventContext; i++) {
                worksheet.getCell(i + 1, 0).setValue(names[i % names.length] + (i < names.length ? "" : " " + (i / names.length + 1)));
                worksheet.getCell(i + 1, 1).setValue(mapValueEventName.get(i));
            }
            // Set header row and formatting.
            worksheet.getCell(0, 0).setValue("Sündmuse nimi");
            worksheet.getCell(0, 1).setValue("Esinemine logides");
            worksheet.getCell(0, 0).getStyle().getFont().setWeight(ExcelFont.BOLD_WEIGHT);
            worksheet.getCell(0, 1).getStyle().getFont().setWeight(ExcelFont.BOLD_WEIGHT);
            worksheet.getColumn(0).setWidth((int) LengthUnitConverter.convert(3, LengthUnit.CENTIMETER, LengthUnit.ZERO_CHARACTER_WIDTH_256_TH_PART));
            try {
                workbook.save("BarChartEventName.xlsx");
                displayInformationAlert("Fail salvestati!", "Fail on salvestatud!", "Fail salvestati programmiga samasse kausta.");
            } catch (IOException ex) {
                ex.printStackTrace();
                displayErrorAlert("Faili ei salvestatud!", "Sulge vana fail ja proovi uuesti.");
            } catch (Exception ex) {
                displayErrorAlert("Faili ei salvestatud!", "Fail on salvestamise jaoks liiga suur.");
            }
        });
        saveBarChartStudentGroup.setOnAction(e -> {
            List<Integer> mapValueStudentGroup = new ArrayList<>();
            List<String> mapKeyStudentGroup = new ArrayList<>();
            for (String key : dataMapStudentGroup.keySet()) {
                mapKeyStudentGroup.add(key);
                mapValueStudentGroup.add(dataMapStudentGroup.get(key));
            }
            SpreadsheetInfo.setLicense("FREE-LIMITED-KEY");
            ExcelFile workbook = new ExcelFile();
            ExcelWorksheet worksheet = workbook.addWorksheet("BarChartStudentGroup");
            int numberOfEventContext = mapKeyStudentGroup.size();
            // Create Excel chart and select data for it.
            ExcelChart chart = worksheet.getCharts().add(ChartType.BAR, "D2", "M25");
            chart.selectData(worksheet.getCells().getSubrangeAbsolute(0, 0, numberOfEventContext, 1), true);
            // Add data which is used by the Excel chart.
            String[] names = mapKeyStudentGroup.toArray(new String[0]);
            for (int i = 0; i < numberOfEventContext; i++) {
                worksheet.getCell(i + 1, 0).setValue(names[i % names.length] + (i < names.length ? "" : " " + (i / names.length + 1)));
                worksheet.getCell(i + 1, 1).setValue(mapValueStudentGroup.get(i));
            }
            // Set header row and formatting.
            worksheet.getCell(0, 0).setValue("Grupi nimi");
            worksheet.getCell(0, 1).setValue("Esinemine logides");
            worksheet.getCell(0, 0).getStyle().getFont().setWeight(ExcelFont.BOLD_WEIGHT);
            worksheet.getCell(0, 1).getStyle().getFont().setWeight(ExcelFont.BOLD_WEIGHT);
            worksheet.getColumn(0).setWidth((int) LengthUnitConverter.convert(3, LengthUnit.CENTIMETER, LengthUnit.ZERO_CHARACTER_WIDTH_256_TH_PART));
            try {
                workbook.save("BarChartStudentGroup.xlsx");
                displayInformationAlert("Fail salvestati!", "Fail on salvestatud!", "Fail salvestati programmiga samasse kausta.");
            } catch (IOException ex) {
                ex.printStackTrace();
                displayErrorAlert("Faili ei salvestatud!", "Sulge vana fail ja proovi uuesti.");
            } catch (Exception ex) {
                displayErrorAlert("Faili ei salvestatud!", "Fail on salvestamise jaoks liiga suur.");
            }
        });
        saveBarChartStudentName.setOnAction(e -> {
            List<Integer> mapValueStudentName = new ArrayList<>();
            List<String> mapKeyStudentName = new ArrayList<>();
            for (String key : dataMapStudentName.keySet()) {
                mapKeyStudentName.add(key);
                mapValueStudentName.add(dataMapStudentName.get(key));
            }
            SpreadsheetInfo.setLicense("FREE-LIMITED-KEY");
            ExcelFile workbook = new ExcelFile();
            ExcelWorksheet worksheet = workbook.addWorksheet("BarChartStudentName");
            int numberOfEventContext = mapKeyStudentName.size();
            // Create Excel chart and select data for it.
            ExcelChart chart = worksheet.getCharts().add(ChartType.BAR, "D2", "M25");
            chart.selectData(worksheet.getCells().getSubrangeAbsolute(0, 0, numberOfEventContext, 1), true);
            // Add data which is used by the Excel chart.
            String[] names = mapKeyStudentName.toArray(new String[0]);
            for (int i = 0; i < numberOfEventContext; i++) {
                worksheet.getCell(i + 1, 0).setValue(names[i % names.length] + (i < names.length ? "" : " " + (i / names.length + 1)));
                worksheet.getCell(i + 1, 1).setValue(mapValueStudentName.get(i));
            }
            // Set header row and formatting.
            worksheet.getCell(0, 0).setValue("Õpilase nimi");
            worksheet.getCell(0, 1).setValue("Esinemine logides");
            worksheet.getCell(0, 0).getStyle().getFont().setWeight(ExcelFont.BOLD_WEIGHT);
            worksheet.getCell(0, 1).getStyle().getFont().setWeight(ExcelFont.BOLD_WEIGHT);
            worksheet.getColumn(0).setWidth((int) LengthUnitConverter.convert(3, LengthUnit.CENTIMETER, LengthUnit.ZERO_CHARACTER_WIDTH_256_TH_PART));
            try {
                workbook.save("BarChartStudentName.xlsx");
                displayInformationAlert("Fail salvestati!", "Fail on salvestatud!", "Fail salvestati programmiga samasse kausta.");
            } catch (IOException ex) {
                ex.printStackTrace();
                displayErrorAlert("Faili ei salvestatud!", "Sulge vana fail ja proovi uuesti.");
            } catch (Exception ex) {
                displayErrorAlert("Faili ei salvestatud!", "Fail on salvestamise jaoks liiga suur.");
            }
        });
        saveBarChartVisitsPerHour.setOnAction(e -> {
            SpreadsheetInfo.setLicense("FREE-LIMITED-KEY");
            ExcelFile workbook = new ExcelFile();
            ExcelWorksheet worksheet = workbook.addWorksheet("BarChartVisitsPerHour");
            int numberOfEventContext = time.size();
            // Create Excel chart and select data for it.
            ExcelChart chart = worksheet.getCharts().add(ChartType.BAR, "D2", "M25");
            chart.selectData(worksheet.getCells().getSubrangeAbsolute(0, 0, numberOfEventContext, 1), true);
            // Add data which is used by the Excel chart.
            String[] names = time.toArray(new String[0]);
            for (int i = 0; i < numberOfEventContext; i++) {
                worksheet.getCell(i + 1, 0).setValue(names[i % names.length] + (i < names.length ? "" : " " + (i / names.length + 1)));
                worksheet.getCell(i + 1, 1).setValue(timeOccurrences.get(i));
            }
            // Set header row and formatting.
            worksheet.getCell(0, 0).setValue("Ajavahemik");
            worksheet.getCell(0, 1).setValue("Esinemine logides");
            worksheet.getCell(0, 0).getStyle().getFont().setWeight(ExcelFont.BOLD_WEIGHT);
            worksheet.getCell(0, 1).getStyle().getFont().setWeight(ExcelFont.BOLD_WEIGHT);
            worksheet.getColumn(0).setWidth((int) LengthUnitConverter.convert(3, LengthUnit.CENTIMETER, LengthUnit.ZERO_CHARACTER_WIDTH_256_TH_PART));
            try {
                workbook.save("BarChartVisitsPerHour.xlsx");
                displayInformationAlert("Fail salvestati!", "Fail on salvestatud!", "Fail salvestati programmiga samasse kausta.");
            } catch (IOException ex) {
                ex.printStackTrace();
                displayErrorAlert("Faili ei salvestatud!", "Sulge vana fail ja proovi uuesti.");
            } catch (Exception ex) {
                displayErrorAlert("Faili ei salvestatud!", "Fail on salvestamise jaoks liiga suur.");
            }
        });
        // Exporting StackedBarCharts
        saveStackedBarChartVisitsPerWeekAndDay.setOnAction(e -> {
            SpreadsheetInfo.setLicense("FREE-LIMITED-KEY");
            ExcelFile workbook = new ExcelFile();
            ExcelWorksheet worksheet = workbook.addWorksheet("BarChartVisitsPerWeek");
            // Create Excel chart and select data for it.
            ExcelChart chart = worksheet.getCharts().add(ChartType.BAR, "D2", "M25");
            chart.selectData(worksheet.getCells().getSubrangeAbsolute(0, 0, mapDayOccurrencesByEachWeek.size(), 1), true);
            Set<Date> set = mapDayOccurrencesByEachWeek.keySet();
            List<Date> list = new ArrayList<>(set);
            for (int i = 0; i < list.size(); i++) {
                int sum = 0;
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                calendar.setTime(list.get(i));
                calendar.add(Calendar.DATE, 6);
                for (Integer integer : mapDayOccurrencesByEachWeek.get(list.get(i)).values()) { sum += integer; }
                worksheet.getCell(i + 1, 0).setValue(simpleDateFormatShort.format(list.get(i)) + " - " + simpleDateFormat.format(calendar.getTime()));
                worksheet.getCell(i + 1, 1).setValue(sum);

            }
            // Set header row and formatting.
            worksheet.getCell(0, 0).setValue("Nädal");
            worksheet.getCell(0, 1).setValue("Esinemine logides");
            worksheet.getCell(0, 0).getStyle().getFont().setWeight(ExcelFont.BOLD_WEIGHT);
            worksheet.getCell(0, 1).getStyle().getFont().setWeight(ExcelFont.BOLD_WEIGHT);
            worksheet.getColumn(0).setWidth((int) LengthUnitConverter.convert(3, LengthUnit.CENTIMETER, LengthUnit.ZERO_CHARACTER_WIDTH_256_TH_PART));
            try {
                workbook.save("BarChartVisitsPerWeek.xlsx");
                displayInformationAlert("Fail salvestati!", "Fail on salvestatud!", "Fail salvestati programmiga samasse kausta.");
            } catch (IOException ex) {
                ex.printStackTrace();
                displayErrorAlert("Faili ei salvestatud!", "Sulge vana fail ja proovi uuesti.");
            } catch (Exception ex) {
                displayErrorAlert("Faili ei salvestatud!", "Fail on salvestamise jaoks liiga suur.");
            }
        });
        saveStackedBarChartVisitsPerDayAndHour.setOnAction(e -> {
            SpreadsheetInfo.setLicense("FREE-LIMITED-KEY");
            ExcelFile workbook = new ExcelFile();
            ExcelWorksheet worksheet = workbook.addWorksheet("BarChartVisitsPerDay");
            // Create Excel chart and select data for it.
            ExcelChart chart = worksheet.getCharts().add(ChartType.BAR, "D2", "M25");
            chart.selectData(worksheet.getCells().getSubrangeAbsolute(0, 0, mapHourOccurrencesByEachDay.size(), 1), true);
            for (int i = 0; i < mapHourOccurrencesByEachDay.size(); i++) {
                List<String> days = new ArrayList<>();
                days.add("E");
                days.add("T");
                days.add("K");
                days.add("N");
                days.add("R");
                days.add("L");
                days.add("P");
                int sum = 0;
                for (Integer integer : mapHourOccurrencesByEachDay.get(days.get(i)).values()) { sum += integer; }
                worksheet.getCell(i + 1, 0).setValue(days.get(i));
                worksheet.getCell(i + 1, 1).setValue(sum);
            }
            // Set header row and formatting.
            worksheet.getCell(0, 0).setValue("Päev");
            worksheet.getCell(0, 1).setValue("Esinemine logides");
            worksheet.getCell(0, 0).getStyle().getFont().setWeight(ExcelFont.BOLD_WEIGHT);
            worksheet.getCell(0, 1).getStyle().getFont().setWeight(ExcelFont.BOLD_WEIGHT);
            worksheet.getColumn(0).setWidth((int) LengthUnitConverter.convert(3, LengthUnit.CENTIMETER, LengthUnit.ZERO_CHARACTER_WIDTH_256_TH_PART));
            try {
                workbook.save("BarChartVisitsPerDay.xlsx");
                displayInformationAlert("Fail salvestati!", "Fail on salvestatud!", "Fail salvestati programmiga samasse kausta.");
            } catch (IOException ex) {
                ex.printStackTrace();
                displayErrorAlert("Faili ei salvestatud!", "Sulge vana fail ja proovi uuesti.");
            } catch (Exception ex) {
                displayErrorAlert("Faili ei salvestatud!", "Fail on salvestamise jaoks liiga suur.");
            }
        });
        // Scene resize events
        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            gridPaneMainView.setVgap(15);
            gridPaneMainView.setHgap(stage.getWidth()/5);
            listViewLogContext.setMinWidth(stage.getWidth()-50);
            listViewLogEvent.setMinWidth(stage.getWidth()-50);
            listViewLogEventFinal.setMinWidth(stage.getWidth()-50);
            listViewStudentGroup.setMinWidth(stage.getWidth()-50);
            listViewStudentName.setMinWidth(stage.getWidth()-50);
            listViewStudentNameFinal.setMinWidth(stage.getWidth()-50);
            tableViewSelectedData.setMinWidth(stage.getWidth()-55);
            tableViewSelectedStudentsGrades.setMinWidth(stage.getWidth()-55);
            tableViewDayAndHour.setMinWidth(stage.getWidth()-55);
            tableViewWeekAndDay.setMinWidth(stage.getWidth()-55);
            tableViewCorrelation.setMinWidth(stage.getWidth()-55);
            barChartEventName.setMinWidth(stage.getWidth()-75);
            barChartEventContext.setMinWidth(stage.getWidth()-75);
            barChartStudentGroup.setMinWidth(stage.getWidth()-75);
            barChartStudentName.setMinWidth(stage.getWidth()-75);
            stackedBarChartVisitsPerWeekAndDay.setMinWidth(stage.getWidth()-75);
            stackedBarChartVisitsPerDayAndHour.setMinWidth(stage.getWidth()-75);
            barChartVisitsPerHour.setMinWidth(stage.getWidth()-75);
            scatterChartCorrelationBetweenLogsAndGrades.setMinWidth(stage.getWidth()-75);
            tabPaneAnalysedData.setMinWidth(stage.getWidth()-15);
            if (tableViewSelectedData.getColumns().size() == 5) {
                tableViewSelectedData.getColumns().get(0).setMinWidth((stage.getWidth()-75)/5);
                tableViewSelectedData.getColumns().get(1).setMinWidth((stage.getWidth()-75)/5);
                tableViewSelectedData.getColumns().get(2).setMinWidth((stage.getWidth()-75)/5);
                tableViewSelectedData.getColumns().get(3).setMinWidth((stage.getWidth()-75)/5);
                tableViewSelectedData.getColumns().get(4).setMinWidth((stage.getWidth()-75)/5);

                tableViewSelectedData.getColumns().get(0).setMaxWidth((stage.getWidth()-75)/5);
                tableViewSelectedData.getColumns().get(1).setMaxWidth((stage.getWidth()-75)/5);
                tableViewSelectedData.getColumns().get(2).setMaxWidth((stage.getWidth()-75)/5);
                tableViewSelectedData.getColumns().get(3).setMaxWidth((stage.getWidth()-75)/5);
                tableViewSelectedData.getColumns().get(4).setMaxWidth((stage.getWidth()-75)/5);
            }
            if (tableViewCorrelation.getColumns().size() == 2) {
                tableViewCorrelation.getColumns().get(0).setMinWidth((stage.getWidth()-75)/2);
                tableViewCorrelation.getColumns().get(1).setMinWidth((stage.getWidth()-75)/2);

                tableViewCorrelation.getColumns().get(0).setMaxWidth((stage.getWidth()-75)/2);
                tableViewCorrelation.getColumns().get(1).setMaxWidth((stage.getWidth()-75)/2);
            }
            if (tableViewWeekAndDay.getColumns().size() == 8) {
                tableViewWeekAndDay.getColumns().get(0).setMinWidth((stage.getWidth()-75)/8);
                tableViewWeekAndDay.getColumns().get(1).setMinWidth((stage.getWidth()-75)/8);
                tableViewWeekAndDay.getColumns().get(2).setMinWidth((stage.getWidth()-75)/8);
                tableViewWeekAndDay.getColumns().get(3).setMinWidth((stage.getWidth()-75)/8);
                tableViewWeekAndDay.getColumns().get(4).setMinWidth((stage.getWidth()-75)/8);
                tableViewWeekAndDay.getColumns().get(5).setMinWidth((stage.getWidth()-75)/8);
                tableViewWeekAndDay.getColumns().get(6).setMinWidth((stage.getWidth()-75)/8);
                tableViewWeekAndDay.getColumns().get(7).setMinWidth((stage.getWidth()-75)/8);

                tableViewWeekAndDay.getColumns().get(0).setMaxWidth((stage.getWidth()-75)/8);
                tableViewWeekAndDay.getColumns().get(1).setMaxWidth((stage.getWidth()-75)/8);
                tableViewWeekAndDay.getColumns().get(2).setMaxWidth((stage.getWidth()-75)/8);
                tableViewWeekAndDay.getColumns().get(3).setMaxWidth((stage.getWidth()-75)/8);
                tableViewWeekAndDay.getColumns().get(4).setMaxWidth((stage.getWidth()-75)/8);
                tableViewWeekAndDay.getColumns().get(5).setMaxWidth((stage.getWidth()-75)/8);
                tableViewWeekAndDay.getColumns().get(6).setMaxWidth((stage.getWidth()-75)/8);
                tableViewWeekAndDay.getColumns().get(7).setMaxWidth((stage.getWidth()-75)/8);
            }
            if (tableViewDayAndHour.getColumns().size() == 13) {
                tableViewDayAndHour.getColumns().get(0).setMinWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(1).setMinWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(2).setMinWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(3).setMinWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(4).setMinWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(5).setMinWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(6).setMinWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(7).setMinWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(8).setMinWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(9).setMinWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(10).setMinWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(11).setMinWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(12).setMinWidth((stage.getWidth()-75)/13);

                tableViewDayAndHour.getColumns().get(0).setMaxWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(1).setMaxWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(2).setMaxWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(3).setMaxWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(4).setMaxWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(5).setMaxWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(6).setMaxWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(7).setMaxWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(8).setMaxWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(9).setMaxWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(10).setMaxWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(11).setMaxWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(12).setMaxWidth((stage.getWidth()-75)/13);
            }
            listViewLogContext.setMaxWidth(stage.getWidth()-50);
            listViewLogEvent.setMaxWidth(stage.getWidth()-50);
            listViewLogEventFinal.setMaxWidth(stage.getWidth()-50);
            listViewStudentGroup.setMaxWidth(stage.getWidth()-50);
            listViewStudentName.setMaxWidth(stage.getWidth()-50);
            listViewStudentNameFinal.setMaxWidth(stage.getWidth()-50);
            tableViewSelectedData.setMaxWidth(stage.getWidth()-55);
            tableViewSelectedStudentsGrades.setMaxWidth(stage.getWidth()-55);
            tableViewDayAndHour.setMaxWidth(stage.getWidth()-55);
            tableViewWeekAndDay.setMaxWidth(stage.getWidth()-55);
            tableViewCorrelation.setMaxWidth(stage.getWidth()-55);
            barChartEventName.setMaxWidth(stage.getWidth()-75);
            barChartEventContext.setMaxWidth(stage.getWidth()-75);
            barChartStudentGroup.setMaxWidth(stage.getWidth()-75);
            barChartStudentName.setMaxWidth(stage.getWidth()-75);
            stackedBarChartVisitsPerWeekAndDay.setMaxWidth(stage.getWidth()-75);
            stackedBarChartVisitsPerDayAndHour.setMaxWidth(stage.getWidth()-75);
            barChartVisitsPerHour.setMaxWidth(stage.getWidth()-75);
            scatterChartCorrelationBetweenLogsAndGrades.setMaxWidth(stage.getWidth()-75);
            tabPaneAnalysedData.setMaxWidth(stage.getWidth()-15);
        });

        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            listViewLogContext.setMinHeight(stage.getHeight()-175);
            listViewLogEvent.setMinHeight(stage.getHeight()-175);
            listViewLogEventFinal.setMinHeight(stage.getHeight()-175);
            listViewStudentGroup.setMinHeight(stage.getHeight()-175);
            listViewStudentName.setMinHeight(stage.getHeight()-175);
            listViewStudentNameFinal.setMinHeight(stage.getHeight()-175);
            tableViewSelectedData.setMinHeight(stage.getHeight()-175);
            tableViewSelectedStudentsGrades.setMinHeight(stage.getHeight()-175);
            barChartEventName.setMinHeight(stage.getHeight()-175);
            barChartEventContext.setMinHeight(stage.getHeight()-175);
            barChartStudentGroup.setMinHeight(stage.getHeight()-175);
            barChartStudentName.setMinHeight(stage.getHeight()-175);
            stackedBarChartVisitsPerDayAndHour.setMinHeight(stage.getHeight()-175);
            stackedBarChartVisitsPerWeekAndDay.setMinHeight(stage.getHeight()-175);
            barChartVisitsPerHour.setMinHeight(stage.getHeight()-175);
            scatterChartCorrelationBetweenLogsAndGrades.setMinHeight(stage.getHeight()-175);
            listViewLogContext.setMaxHeight(stage.getHeight()-175);
            listViewLogEvent.setMaxHeight(stage.getHeight()-175);
            listViewLogEventFinal.setMaxHeight(stage.getHeight()-175);
            listViewStudentGroup.setMaxHeight(stage.getHeight()-175);
            listViewStudentName.setMaxHeight(stage.getHeight()-175);
            listViewStudentNameFinal.setMaxHeight(stage.getHeight()-175);
            tableViewSelectedData.setMaxHeight(stage.getHeight()-175);
            tableViewSelectedStudentsGrades.setMaxHeight(stage.getHeight()-175);
            barChartEventName.setMaxHeight(stage.getHeight()-175);
            barChartEventContext.setMaxHeight(stage.getHeight()-175);
            barChartStudentGroup.setMaxHeight(stage.getHeight()-175);
            barChartStudentName.setMaxHeight(stage.getHeight()-175);
            stackedBarChartVisitsPerDayAndHour.setMaxHeight(stage.getHeight()-175);
            stackedBarChartVisitsPerWeekAndDay.setMaxHeight(stage.getHeight()-175);
            barChartVisitsPerHour.setMaxHeight(stage.getHeight()-175);
            scatterChartCorrelationBetweenLogsAndGrades.setMaxHeight(stage.getHeight()-175);
            tabPaneAnalysedData.setMinHeight(stage.getHeight()-80);
            tabPaneAnalysedData.setMaxHeight(stage.getHeight()-80);
        });
        // Finalizing the Stage
        stage.setScene(sceneMainView);
        stage.setTitle("Moodle'i logide analüüsimine");
        stage.show();
        // Make stage resizable
        stage.setWidth(stage.getWidth());
        stage.setHeight(stage.getHeight());
    }

    // Display ListViews
    private void displayEventContext(Stage stage) {
        Platform.runLater(() -> {
            List<String> contextList = new ArrayList<>(moodleLogsProcessing.getEventContext());
            Collections.sort(contextList);
            listViewLogContext.getItems().addAll(contextList);
            listViewLogContext.setMaxHeight(stage.getHeight()-175);
        });
    }
    private void displayEventName(Stage stage) {
        Platform.runLater(() -> {
            Set<String> setEventName = new TreeSet<>();
            for (Map<String, String> log : moodleLogsProcessing.getLogs()) {
                if (observableListLogContext.contains(log.get("Event context"))) {
                    setEventName.add(log.get("Event name"));
                }
            }
            List<String> listEventName = new TreeList<>(setEventName);
            Collections.sort(listEventName);
            listViewLogEvent.getItems().addAll(listEventName);
            listViewLogEvent.setMaxHeight(stage.getHeight()-175);
            listViewLogEventFinal.setItems(listViewLogEvent.getItems());
            listViewLogEventFinal.setMaxHeight(stage.getHeight()-175);
        });
    }
    private void displayStudentGroup(Stage stage) {
        Platform.runLater(() -> {
            List<String> groupList = new ArrayList<>(studentInfoProcessing.getStudentGroup());
            Collections.sort(groupList);
            listViewStudentGroup.getItems().addAll(groupList);
            listViewStudentGroup.setMaxHeight(stage.getHeight()-175);
        });

    }
    private void displayStudentName(Stage stage) {
        Platform.runLater(() -> {
            Set<String> setStudentName = new TreeSet<>();
            for (Map<String, String> log : studentInfoProcessing.getStudents()) {
                if (observableListStudentGroup.contains(log.get("Group"))) {
                    setStudentName.add(log.get("Name"));
                }
            }
            List<String> listStudentName = new ArrayList<>(setStudentName);
            Collections.sort(listStudentName);
            listViewStudentName.getItems().addAll(listStudentName);
            listViewStudentName.setMaxHeight(stage.getHeight()-175);
            listViewStudentNameFinal.setItems(listViewStudentName.getItems());
            listViewStudentNameFinal.setMaxHeight(stage.getHeight()-175);
        });

    }
    // Display TableViews
    private void displayTableViewLogs(Stage stage) {
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
            if (tableViewSelectedData.getColumns().size() == 5) {
                tableViewSelectedData.getColumns().get(0).setMinWidth((stage.getWidth()-75)/5);
                tableViewSelectedData.getColumns().get(1).setMinWidth((stage.getWidth()-75)/5);
                tableViewSelectedData.getColumns().get(2).setMinWidth((stage.getWidth()-75)/5);
                tableViewSelectedData.getColumns().get(3).setMinWidth((stage.getWidth()-75)/5);
                tableViewSelectedData.getColumns().get(4).setMinWidth((stage.getWidth()-75)/5);

                tableViewSelectedData.getColumns().get(0).setMaxWidth((stage.getWidth()-75)/5);
                tableViewSelectedData.getColumns().get(1).setMaxWidth((stage.getWidth()-75)/5);
                tableViewSelectedData.getColumns().get(2).setMaxWidth((stage.getWidth()-75)/5);
                tableViewSelectedData.getColumns().get(3).setMaxWidth((stage.getWidth()-75)/5);
                tableViewSelectedData.getColumns().get(4).setMaxWidth((stage.getWidth()-75)/5);
            }
        });
    }
    private void displayTableViewStudentGrades() {
        Platform.runLater(() -> {
            Integer[] integers = new Integer[100];
            for (int i = 0; i < 100; i++) {
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
                if (observableListStudentName.contains(grade.get(0))) {
                    for (int i = 0; i < grade.size(); i++) {
                        firstRow.add(i, new SimpleStringProperty(grade.get(i)));
                    }
                    data.add(firstRow);
                }
            }
            tableViewSelectedStudentsGrades.setItems(data);
        });
    }
    private void displayTableViewWeekAndDay(Stage stage) {
        Platform.runLater(() -> {
            Integer[] integers = new Integer[8];
            List<String> days = new ArrayList<>();
            days.add("Nädal/Päev");
            days.add("E");
            days.add("T");
            days.add("K");
            days.add("N");
            days.add("R");
            days.add("L");
            days.add("P");
            for (int i = 0; i < 8; i++) {
                integers[i] = i;
            }
            for (Integer integer : integers) {
                TableColumn<List<StringProperty>, String> columnWeekAndDay = new TableColumn<>(days.get(integer));
                columnWeekAndDay.setCellValueFactory(data  -> data.getValue().get(integer));
                tableViewWeekAndDay.getColumns().add(columnWeekAndDay);
            }
            ObservableList<List<StringProperty>> data = FXCollections.observableArrayList();
            for (Date var : mapDayOccurrencesByEachWeek.keySet()) {
                List<StringProperty> firstRow = new ArrayList<>();
                for (Integer integer : integers) {
                    if (integer == 0) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                        calendar.setTime(var);
                        calendar.add(Calendar.DATE, 6);
                        firstRow.add(integer, new SimpleStringProperty(simpleDateFormatShort.format(var) + " - " + simpleDateFormat.format(calendar.getTime())));
                    }
                    else {
                        firstRow.add(integer, new SimpleStringProperty(Integer.toString(mapDayOccurrencesByEachWeek.get(var).get(days.get(integer)))));
                    }
                }
                data.add(firstRow);
            }
            tableViewWeekAndDay.setItems(data);
            if (tableViewWeekAndDay.getColumns().size() == 8) {
                tableViewWeekAndDay.getColumns().get(0).setMinWidth((stage.getWidth()-75)/8);
                tableViewWeekAndDay.getColumns().get(1).setMinWidth((stage.getWidth()-75)/8);
                tableViewWeekAndDay.getColumns().get(2).setMinWidth((stage.getWidth()-75)/8);
                tableViewWeekAndDay.getColumns().get(3).setMinWidth((stage.getWidth()-75)/8);
                tableViewWeekAndDay.getColumns().get(4).setMinWidth((stage.getWidth()-75)/8);
                tableViewWeekAndDay.getColumns().get(5).setMinWidth((stage.getWidth()-75)/8);
                tableViewWeekAndDay.getColumns().get(6).setMinWidth((stage.getWidth()-75)/8);
                tableViewWeekAndDay.getColumns().get(7).setMinWidth((stage.getWidth()-75)/8);

                tableViewWeekAndDay.getColumns().get(0).setMaxWidth((stage.getWidth()-75)/8);
                tableViewWeekAndDay.getColumns().get(1).setMaxWidth((stage.getWidth()-75)/8);
                tableViewWeekAndDay.getColumns().get(2).setMaxWidth((stage.getWidth()-75)/8);
                tableViewWeekAndDay.getColumns().get(3).setMaxWidth((stage.getWidth()-75)/8);
                tableViewWeekAndDay.getColumns().get(4).setMaxWidth((stage.getWidth()-75)/8);
                tableViewWeekAndDay.getColumns().get(5).setMaxWidth((stage.getWidth()-75)/8);
                tableViewWeekAndDay.getColumns().get(6).setMaxWidth((stage.getWidth()-75)/8);
                tableViewWeekAndDay.getColumns().get(7).setMaxWidth((stage.getWidth()-75)/8);
            }
        });
    }
    private void displayTableViewDayAndHour(Stage stage) {
        Platform.runLater(() -> {
            Integer[] integers = new Integer[13];
            List<String> hours = new ArrayList<>();
            hours.add("Päev/Tunnid");
            hours.add("0-1");
            hours.add("2-3");
            hours.add("4-5");
            hours.add("6-7");
            hours.add("8-9");
            hours.add("10-11");
            hours.add("12-13");
            hours.add("14-15");
            hours.add("16-17");
            hours.add("18-19");
            hours.add("20-21");
            hours.add("22-23");
            List<String> days = new ArrayList<>();
            days.add("E");
            days.add("T");
            days.add("K");
            days.add("N");
            days.add("R");
            days.add("L");
            days.add("P");
            for (int i = 0; i < 13; i++) {
                integers[i] = i;
            }
            for (Integer integer : integers) {
                TableColumn<List<StringProperty>, String> columnDayAndHour = new TableColumn<>(hours.get(integer));
                columnDayAndHour.setCellValueFactory(data  -> data.getValue().get(integer));
                tableViewDayAndHour.getColumns().add(columnDayAndHour);
            }
            ObservableList<List<StringProperty>> data = FXCollections.observableArrayList();
            for (String string : days) {
                List<StringProperty> firstRow = new ArrayList<>();
                for (Integer integer : integers) {
                    if (integer == 0) {
                        firstRow.add(integer, new SimpleStringProperty(string));
                    }
                    else {
                        firstRow.add(integer, new SimpleStringProperty(Integer.toString(mapHourOccurrencesByEachDay.get(string).get(hours.get(integer)))));
                    }
                }
                data.add(firstRow);
            }
            tableViewDayAndHour.setItems(data);
            if (tableViewDayAndHour.getColumns().size() == 13) {
                tableViewDayAndHour.getColumns().get(0).setMinWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(1).setMinWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(2).setMinWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(3).setMinWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(4).setMinWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(5).setMinWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(6).setMinWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(7).setMinWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(8).setMinWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(9).setMinWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(10).setMinWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(11).setMinWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(12).setMinWidth((stage.getWidth()-75)/13);

                tableViewDayAndHour.getColumns().get(0).setMaxWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(1).setMaxWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(2).setMaxWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(3).setMaxWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(4).setMaxWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(5).setMaxWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(6).setMaxWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(7).setMaxWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(8).setMaxWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(9).setMaxWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(10).setMaxWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(11).setMaxWidth((stage.getWidth()-75)/13);
                tableViewDayAndHour.getColumns().get(12).setMaxWidth((stage.getWidth()-75)/13);
            }
        });
    }
    private void displayTableViewCorrelation(Stage stage) {
        Platform.runLater(() -> {
            for (Map<String, Double> map : listOfCorrelationData) {
                Correlation correlation = new Correlation(map.get("Points"), map.get("Logs"));
                tableViewCorrelation.getItems().add(correlation);
            }
            if (tableViewCorrelation.getColumns().size() == 2) {
                tableViewCorrelation.getColumns().get(0).setMinWidth((stage.getWidth()-75)/2);
                tableViewCorrelation.getColumns().get(1).setMinWidth((stage.getWidth()-75)/2);

                tableViewCorrelation.getColumns().get(0).setMaxWidth((stage.getWidth()-75)/2);
                tableViewCorrelation.getColumns().get(1).setMaxWidth((stage.getWidth()-75)/2);
            }
        });
    }
    // Display BarCharts
    private void displayBarChartEventName() {
        Platform.runLater(() -> {
            XYChart.Series<Number, String> dataSeriesEventName = new XYChart.Series<>();
            Map<String, Integer> dataMapEventName = new TreeMap<>();
            for (Log log : filteredResults) {
                if (dataMapEventName.containsKey(log.getEventName())) {
                    dataMapEventName.put(log.getEventName(), dataMapEventName.get(log.getEventName()) + 1);
                } else {
                    dataMapEventName.put(log.getEventName(), 1);
                }
            }
            if (dataMapEventName.keySet().size() > 15) {
                mapValueEventName = new ArrayList<>();
                mapKeyEventName = new ArrayList<>();
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
                    dataSeriesEventName.getData().add(new XYChart.Data<>(mapValueEventName.get(i), mapKeyEventName.get(i)));
                }
            }
            else {
                mapValueEventName = new ArrayList<>();
                mapKeyEventName = new ArrayList<>();
                for (String key : dataMapEventName.keySet()) {
                    mapValueEventName.add(dataMapEventName.get(key));
                    mapKeyEventName.add(key);
                }
                for (int i = 0; i < dataMapEventName.keySet().size(); i++) {
                    dataSeriesEventName.getData().add(new XYChart.Data<>(mapValueEventName.get(i), mapKeyEventName.get(i)));
                }
            }
            barChartEventName.setTitle("Kui palju sündmuse nime kohta käivaid kirjeid esineb logides");
            barChartEventName.setLegendVisible(false);
            barChartEventName.getData().add(dataSeriesEventName);
        });
    }
    private void displayBarChartEventContext() {
        Platform.runLater(() -> {
            XYChart.Series<Number, String> dataSeriesEventContext = new XYChart.Series<>();
            Map<String, Integer> dataMapEventContext = new TreeMap<>();
            for (Log log : filteredResults) {
                if (dataMapEventContext.containsKey(log.getEventContext())) {
                    dataMapEventContext.put(log.getEventContext(), dataMapEventContext.get(log.getEventContext()) + 1);
                } else {
                    dataMapEventContext.put(log.getEventContext(), 1);
                }
            }
            if (dataMapEventContext.keySet().size() > 15) {
                mapKeyEventContext = new ArrayList<>();
                mapValueEventContext = new ArrayList<>();
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
                    dataSeriesEventContext.getData().add(new XYChart.Data<>(mapValueEventContext.get(i), mapKeyEventContext.get(i)));
                }
            }
            else {
                mapKeyEventContext = new ArrayList<>();
                mapValueEventContext = new ArrayList<>();
                for (String key : dataMapEventContext.keySet()) {
                    mapKeyEventContext.add(key);
                    mapValueEventContext.add(dataMapEventContext.get(key));
                }
                for (int i = 0; i < dataMapEventContext.keySet().size(); i++) {
                    dataSeriesEventContext.getData().add(new XYChart.Data<>(mapValueEventContext.get(i), mapKeyEventContext.get(i)));
                }
            }
            barChartEventContext.setTitle("Kui palju sündmuse konteksti kohta käivaid kirjeid esineb logides");
            barChartEventContext.setLegendVisible(false);
            barChartEventContext.getData().add(dataSeriesEventContext);
        });
    }
    private void displayBarChartStudentGroup() {
        Platform.runLater(() -> {
            XYChart.Series<String, Number> dataSeriesStudentGroup = new XYChart.Series<>();
            dataMapStudentGroup = new TreeMap<>();
            for (Log log : filteredResults) {
                if (dataMapStudentGroup.containsKey(log.getStudentGroup())) {
                    dataMapStudentGroup.put(log.getStudentGroup(), dataMapStudentGroup.get(log.getStudentGroup()) + 1);
                } else {
                    dataMapStudentGroup.put(log.getStudentGroup(), 1);
                }
            }
            for (String key : dataMapStudentGroup.keySet()) {
                dataSeriesStudentGroup.getData().add(new XYChart.Data<>(key, dataMapStudentGroup.get(key)));
            }
            barChartStudentGroup.setTitle("Kui palju iga rühm kohta käivaid kirjeid esineb logides");
            barChartStudentGroup.setLegendVisible(false);
            barChartStudentGroup.getData().add(dataSeriesStudentGroup);
        });
    }
    private void displayBarChartStudentName() {
        Platform.runLater(() -> {
            XYChart.Series<String, Number> dataSeriesStudentName = new XYChart.Series<>();
            dataMapStudentName = new TreeMap<>();
            for (Log log : filteredResults) {
                if (dataMapStudentName.containsKey(log.getStudentName())) {
                    dataMapStudentName.put(log.getStudentName(), dataMapStudentName.get(log.getStudentName()) + 1);
                } else {
                    dataMapStudentName.put(log.getStudentName(), 1);
                }
            }
            for (String key : dataMapStudentName.keySet()) {
                dataSeriesStudentName.getData().add(new XYChart.Data<>(key, dataMapStudentName.get(key)));
            }
            barChartStudentName.setTitle("Kui palju iga õpilase kohta käivaid kirjeid esineb logides");
            barChartStudentName.setLegendVisible(false);
            barChartStudentName.getData().add(dataSeriesStudentName);
        });
    }
    private void displayStackedBarChartVisitsPerWeekAndDay() {
        Platform.runLater(() -> {
            XYChart.Series<String, Number> monday = new XYChart.Series<>();
            monday.setName("Esmaspäev");
            XYChart.Series<String, Number> tuesday = new XYChart.Series<>();
            tuesday.setName("Teisipäev");
            XYChart.Series<String, Number> wednesday = new XYChart.Series<>();
            wednesday.setName("Kolmapäev");
            XYChart.Series<String, Number> thursday = new XYChart.Series<>();
            thursday.setName("Neljapäev");
            XYChart.Series<String, Number> friday = new XYChart.Series<>();
            friday.setName("Reede");
            XYChart.Series<String, Number> saturday = new XYChart.Series<>();
            saturday.setName("Laupäev");
            XYChart.Series<String, Number> sunday = new XYChart.Series<>();
            sunday.setName("Pühapäev");
            mapDayOccurrencesByEachWeek = new TreeMap<>();
            for (Log log : filteredResults) {
                DateTimeFormatter dateTimeFormatter;
                String[] logArray = log.getTime().split(" ");
                if (logArray[0].length() == 9) { dateTimeFormatter = DateTimeFormatter.ofPattern("d.MM.yyyy"); }
                else { dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy"); }
                LocalDate localDate = LocalDate.parse(logArray[0], dateTimeFormatter);
                ZoneId defaultZoneId = ZoneId.of("Europe/Tallinn");
                Date date = Date.from(localDate.atStartOfDay(defaultZoneId).toInstant());
                Calendar calendar = Calendar.getInstance(Locale.GERMANY);
                calendar.setTime(date);
                int i = calendar.get(Calendar.DAY_OF_WEEK) - calendar.getFirstDayOfWeek();
                calendar.add(Calendar.DATE, -i);
                Date start = calendar.getTime();
                calendar.add(Calendar.DATE, 6);
                if (!mapDayOccurrencesByEachWeek.keySet().contains(start)) {
                    Map<String, Integer> mapOfDays = new HashMap<>();
                    mapOfDays.put("E", 0);
                    mapOfDays.put("T", 0);
                    mapOfDays.put("K", 0);
                    mapOfDays.put("N", 0);
                    mapOfDays.put("R", 0);
                    mapOfDays.put("L", 0);
                    mapOfDays.put("P", 0);
                    mapDayOccurrencesByEachWeek.put(start, mapOfDays);
                }
                if (localDate.getDayOfWeek() == DayOfWeek.MONDAY) {
                    mapDayOccurrencesByEachWeek.get(start).put("E", mapDayOccurrencesByEachWeek.get(start).get("E") + 1);
                }
                else if (localDate.getDayOfWeek() == DayOfWeek.TUESDAY) {
                    mapDayOccurrencesByEachWeek.get(start).put("T", mapDayOccurrencesByEachWeek.get(start).get("T") + 1);
                }
                else if (localDate.getDayOfWeek() == DayOfWeek.WEDNESDAY) {
                    mapDayOccurrencesByEachWeek.get(start).put("K", mapDayOccurrencesByEachWeek.get(start).get("K") + 1);
                }
                else if (localDate.getDayOfWeek() == DayOfWeek.THURSDAY) {
                    mapDayOccurrencesByEachWeek.get(start).put("N", mapDayOccurrencesByEachWeek.get(start).get("N") + 1);
                }
                else if (localDate.getDayOfWeek() == DayOfWeek.FRIDAY) {
                    mapDayOccurrencesByEachWeek.get(start).put("R", mapDayOccurrencesByEachWeek.get(start).get("R") + 1);
                }
                else if (localDate.getDayOfWeek() == DayOfWeek.SATURDAY) {
                    mapDayOccurrencesByEachWeek.get(start).put("L", mapDayOccurrencesByEachWeek.get(start).get("L") + 1);
                }
                else {
                    mapDayOccurrencesByEachWeek.get(start).put("P", mapDayOccurrencesByEachWeek.get(start).get("P") + 1);
                }
            }
            for (Date week : mapDayOccurrencesByEachWeek.keySet()) {
                Collection<Integer> collection = mapDayOccurrencesByEachWeek.get(week).values();
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                calendar.setTime(week);
                calendar.add(Calendar.DATE, 6);
                int sum = 0;
                for (Integer integer : collection) {
                    sum += integer;
                }
                if (sum != 0) {
                    for (int j = 0; j < 7; j++) {
                        if (j == 0) {
                            monday.getData().add((new XYChart.Data<>(simpleDateFormatShort.format(week) + " - " + simpleDateFormat.format(calendar.getTime()), mapDayOccurrencesByEachWeek.get(week).get("E"))));
                        }
                        else if (j == 1) {
                            tuesday.getData().add((new XYChart.Data<>(simpleDateFormatShort.format(week) + " - " + simpleDateFormat.format(calendar.getTime()), mapDayOccurrencesByEachWeek.get(week).get("T"))));
                        }
                        else if (j == 2) {
                            wednesday.getData().add((new XYChart.Data<>(simpleDateFormatShort.format(week) + " - " + simpleDateFormat.format(calendar.getTime()), mapDayOccurrencesByEachWeek.get(week).get("K"))));
                        }
                        else if (j == 3) {
                            thursday.getData().add((new XYChart.Data<>(simpleDateFormatShort.format(week) + " - " + simpleDateFormat.format(calendar.getTime()), mapDayOccurrencesByEachWeek.get(week).get("N"))));
                        }
                        else if (j == 4) {
                            friday.getData().add((new XYChart.Data<>(simpleDateFormatShort.format(week) + " - " + simpleDateFormat.format(calendar.getTime()), mapDayOccurrencesByEachWeek.get(week).get("R"))));
                        }
                        else if (j == 5) {
                            saturday.getData().add((new XYChart.Data<>(simpleDateFormatShort.format(week) + " - " + simpleDateFormat.format(calendar.getTime()), mapDayOccurrencesByEachWeek.get(week).get("L"))));
                        }
                        else {
                            sunday.getData().add((new XYChart.Data<>(simpleDateFormatShort.format(week) + " - " + simpleDateFormat.format(calendar.getTime()), mapDayOccurrencesByEachWeek.get(week).get("P"))));
                        }
                    }
                }
                else {
                    mapDayOccurrencesByEachWeek.remove(week);
                }
            }
            stackedBarChartVisitsPerWeekAndDay.setTitle("Kui palju iga nädala ja päeva kohta käivaid kirjeid esineb logides");
            stackedBarChartVisitsPerWeekAndDay.getData().add(monday);
            stackedBarChartVisitsPerWeekAndDay.getData().add(tuesday);
            stackedBarChartVisitsPerWeekAndDay.getData().add(wednesday);
            stackedBarChartVisitsPerWeekAndDay.getData().add(thursday);
            stackedBarChartVisitsPerWeekAndDay.getData().add(friday);
            stackedBarChartVisitsPerWeekAndDay.getData().add(saturday);
            stackedBarChartVisitsPerWeekAndDay.getData().add(sunday);

        });
    }
    private void displayStackedBarChartVisitsPerDayAndHour() {
        Platform.runLater(() -> {
            XYChart.Series<String, Number> zero = new XYChart.Series<>();
            zero.setName("0-1");
            XYChart.Series<String, Number> two = new XYChart.Series<>();
            two.setName("2-3");
            XYChart.Series<String, Number> four = new XYChart.Series<>();
            four.setName("4-5");
            XYChart.Series<String, Number> six = new XYChart.Series<>();
            six.setName("6-7");
            XYChart.Series<String, Number> eight = new XYChart.Series<>();
            eight.setName("8-9");
            XYChart.Series<String, Number> ten = new XYChart.Series<>();
            ten.setName("10-11");
            XYChart.Series<String, Number> twelve = new XYChart.Series<>();
            twelve.setName("12-13");
            XYChart.Series<String, Number> fourteen = new XYChart.Series<>();
            fourteen.setName("14-15");
            XYChart.Series<String, Number> sixteen = new XYChart.Series<>();
            sixteen.setName("16-17");
            XYChart.Series<String, Number> eighteen = new XYChart.Series<>();
            eighteen.setName("18-19");
            XYChart.Series<String, Number> twenty = new XYChart.Series<>();
            twenty.setName("20-21");
            XYChart.Series<String, Number> twenty_two = new XYChart.Series<>();
            twenty_two.setName("22-23");
            List<String> days = new ArrayList<>();
            days.add("E");
            days.add("T");
            days.add("K");
            days.add("N");
            days.add("R");
            days.add("L");
            days.add("P");
            mapHourOccurrencesByEachDay = new HashMap<>();
            for (String day : days) {
                Map<String, Integer> mapOfTimes = new HashMap<>();
                mapOfTimes.put("0-1", 0);
                mapOfTimes.put("2-3", 0);
                mapOfTimes.put("4-5", 0);
                mapOfTimes.put("6-7", 0);
                mapOfTimes.put("8-9", 0);
                mapOfTimes.put("10-11", 0);
                mapOfTimes.put("12-13", 0);
                mapOfTimes.put("14-15", 0);
                mapOfTimes.put("16-17", 0);
                mapOfTimes.put("18-19", 0);
                mapOfTimes.put("20-21", 0);
                mapOfTimes.put("22-23", 0);
                mapHourOccurrencesByEachDay.put(day, mapOfTimes);
            }
            for (Log log : filteredResults) {
                DateTimeFormatter dateTimeFormatter;
                String[] logArray = log.getTime().split(" ");
                if (logArray[0].length() == 9) { dateTimeFormatter = DateTimeFormatter.ofPattern("d.MM.yyyy"); }
                else { dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy"); }
                LocalDate localDate = LocalDate.parse(logArray[0], dateTimeFormatter);
                DateTimeFormatter dateTimeFormatterTime = DateTimeFormatter.ofPattern("HH.mm");
                LocalTime logTime = LocalTime.parse(logArray[1], dateTimeFormatterTime);
                if (logTime.getHour() == 0 || logTime.getHour() == 1) {
                    if (localDate.getDayOfWeek() == DayOfWeek.MONDAY) { mapHourOccurrencesByEachDay.get("E").put("0-1", mapHourOccurrencesByEachDay.get("E").get("0-1") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.TUESDAY) { mapHourOccurrencesByEachDay.get("T").put("0-1", mapHourOccurrencesByEachDay.get("T").get("0-1") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.WEDNESDAY) { mapHourOccurrencesByEachDay.get("K").put("0-1", mapHourOccurrencesByEachDay.get("K").get("0-1") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.THURSDAY) { mapHourOccurrencesByEachDay.get("N").put("0-1", mapHourOccurrencesByEachDay.get("N").get("0-1") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.FRIDAY) { mapHourOccurrencesByEachDay.get("R").put("0-1", mapHourOccurrencesByEachDay.get("R").get("0-1") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.SATURDAY) { mapHourOccurrencesByEachDay.get("L").put("0-1", mapHourOccurrencesByEachDay.get("L").get("0-1") + 1); }
                    else { mapHourOccurrencesByEachDay.get("P").put("0-1", mapHourOccurrencesByEachDay.get("P").get("0-1") + 1); }
                }
                else if (logTime.getHour() == 2 || logTime.getHour() == 3) {
                    if (localDate.getDayOfWeek() == DayOfWeek.MONDAY) { mapHourOccurrencesByEachDay.get("E").put("2-3", mapHourOccurrencesByEachDay.get("E").get("2-3") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.TUESDAY) { mapHourOccurrencesByEachDay.get("T").put("2-3", mapHourOccurrencesByEachDay.get("T").get("2-3") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.WEDNESDAY) { mapHourOccurrencesByEachDay.get("K").put("2-3", mapHourOccurrencesByEachDay.get("K").get("2-3") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.THURSDAY) { mapHourOccurrencesByEachDay.get("N").put("2-3", mapHourOccurrencesByEachDay.get("N").get("2-3") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.FRIDAY) { mapHourOccurrencesByEachDay.get("R").put("2-3", mapHourOccurrencesByEachDay.get("R").get("2-3") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.SATURDAY) { mapHourOccurrencesByEachDay.get("L").put("2-3", mapHourOccurrencesByEachDay.get("L").get("2-3") + 1); }
                    else { mapHourOccurrencesByEachDay.get("P").put("2-3", mapHourOccurrencesByEachDay.get("P").get("2-3") + 1); }
                }
                else if (logTime.getHour() == 4 || logTime.getHour() == 5) {
                    if (localDate.getDayOfWeek() == DayOfWeek.MONDAY) { mapHourOccurrencesByEachDay.get("E").put("4-5", mapHourOccurrencesByEachDay.get("E").get("4-5") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.TUESDAY) { mapHourOccurrencesByEachDay.get("T").put("4-5", mapHourOccurrencesByEachDay.get("T").get("4-5") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.WEDNESDAY) { mapHourOccurrencesByEachDay.get("K").put("4-5", mapHourOccurrencesByEachDay.get("K").get("4-5") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.THURSDAY) { mapHourOccurrencesByEachDay.get("N").put("4-5", mapHourOccurrencesByEachDay.get("N").get("4-5") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.FRIDAY) { mapHourOccurrencesByEachDay.get("R").put("4-5", mapHourOccurrencesByEachDay.get("R").get("4-5") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.SATURDAY) { mapHourOccurrencesByEachDay.get("L").put("4-5", mapHourOccurrencesByEachDay.get("L").get("4-5") + 1); }
                    else { mapHourOccurrencesByEachDay.get("P").put("4-5", mapHourOccurrencesByEachDay.get("P").get("4-5") + 1); }
                }
                else if (logTime.getHour() == 6 || logTime.getHour() == 7) {
                    if (localDate.getDayOfWeek() == DayOfWeek.MONDAY) { mapHourOccurrencesByEachDay.get("E").put("6-7", mapHourOccurrencesByEachDay.get("E").get("6-7") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.TUESDAY) { mapHourOccurrencesByEachDay.get("T").put("6-7", mapHourOccurrencesByEachDay.get("T").get("6-7") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.WEDNESDAY) { mapHourOccurrencesByEachDay.get("K").put("6-7", mapHourOccurrencesByEachDay.get("K").get("6-7") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.THURSDAY) { mapHourOccurrencesByEachDay.get("N").put("6-7", mapHourOccurrencesByEachDay.get("N").get("6-7") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.FRIDAY) { mapHourOccurrencesByEachDay.get("R").put("6-7", mapHourOccurrencesByEachDay.get("R").get("6-7") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.SATURDAY) { mapHourOccurrencesByEachDay.get("L").put("6-7", mapHourOccurrencesByEachDay.get("L").get("6-7") + 1); }
                    else { mapHourOccurrencesByEachDay.get("P").put("6-7", mapHourOccurrencesByEachDay.get("P").get("6-7") + 1); }
                }
                else if (logTime.getHour() == 8 || logTime.getHour() == 9) {
                    if (localDate.getDayOfWeek() == DayOfWeek.MONDAY) { mapHourOccurrencesByEachDay.get("E").put("8-9", mapHourOccurrencesByEachDay.get("E").get("8-9") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.TUESDAY) { mapHourOccurrencesByEachDay.get("T").put("8-9", mapHourOccurrencesByEachDay.get("T").get("8-9") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.WEDNESDAY) { mapHourOccurrencesByEachDay.get("K").put("8-9", mapHourOccurrencesByEachDay.get("K").get("8-9") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.THURSDAY) { mapHourOccurrencesByEachDay.get("N").put("8-9", mapHourOccurrencesByEachDay.get("N").get("8-9") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.FRIDAY) { mapHourOccurrencesByEachDay.get("R").put("8-9", mapHourOccurrencesByEachDay.get("R").get("8-9") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.SATURDAY) { mapHourOccurrencesByEachDay.get("L").put("8-9", mapHourOccurrencesByEachDay.get("L").get("8-9") + 1); }
                    else { mapHourOccurrencesByEachDay.get("P").put("8-9", mapHourOccurrencesByEachDay.get("P").get("8-9") + 1); }
                }
                else if (logTime.getHour() == 10 || logTime.getHour() == 11) {
                    if (localDate.getDayOfWeek() == DayOfWeek.MONDAY) { mapHourOccurrencesByEachDay.get("E").put("10-11", mapHourOccurrencesByEachDay.get("E").get("10-11") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.TUESDAY) { mapHourOccurrencesByEachDay.get("T").put("10-11", mapHourOccurrencesByEachDay.get("T").get("10-11") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.WEDNESDAY) { mapHourOccurrencesByEachDay.get("K").put("10-11", mapHourOccurrencesByEachDay.get("K").get("10-11") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.THURSDAY) { mapHourOccurrencesByEachDay.get("N").put("10-11", mapHourOccurrencesByEachDay.get("N").get("10-11") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.FRIDAY) { mapHourOccurrencesByEachDay.get("R").put("10-11", mapHourOccurrencesByEachDay.get("R").get("10-11") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.SATURDAY) { mapHourOccurrencesByEachDay.get("L").put("10-11", mapHourOccurrencesByEachDay.get("L").get("10-11") + 1); }
                    else { mapHourOccurrencesByEachDay.get("P").put("10-11", mapHourOccurrencesByEachDay.get("P").get("10-11") + 1); }
                }
                else if (logTime.getHour() == 12 || logTime.getHour() == 13) {
                    if (localDate.getDayOfWeek() == DayOfWeek.MONDAY) { mapHourOccurrencesByEachDay.get("E").put("12-13", mapHourOccurrencesByEachDay.get("E").get("12-13") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.TUESDAY) { mapHourOccurrencesByEachDay.get("T").put("12-13", mapHourOccurrencesByEachDay.get("T").get("12-13") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.WEDNESDAY) { mapHourOccurrencesByEachDay.get("K").put("12-13", mapHourOccurrencesByEachDay.get("K").get("12-13") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.THURSDAY) { mapHourOccurrencesByEachDay.get("N").put("12-13", mapHourOccurrencesByEachDay.get("N").get("12-13") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.FRIDAY) { mapHourOccurrencesByEachDay.get("R").put("12-13", mapHourOccurrencesByEachDay.get("R").get("12-13") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.SATURDAY) { mapHourOccurrencesByEachDay.get("L").put("12-13", mapHourOccurrencesByEachDay.get("L").get("12-13") + 1); }
                    else { mapHourOccurrencesByEachDay.get("P").put("12-13", mapHourOccurrencesByEachDay.get("P").get("12-13") + 1); }
                }
                else if (logTime.getHour() == 14 || logTime.getHour() == 15) {
                    if (localDate.getDayOfWeek() == DayOfWeek.MONDAY) { mapHourOccurrencesByEachDay.get("E").put("14-15", mapHourOccurrencesByEachDay.get("E").get("14-15") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.TUESDAY) { mapHourOccurrencesByEachDay.get("T").put("14-15", mapHourOccurrencesByEachDay.get("T").get("14-15") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.WEDNESDAY) { mapHourOccurrencesByEachDay.get("K").put("14-15", mapHourOccurrencesByEachDay.get("K").get("14-15") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.THURSDAY) { mapHourOccurrencesByEachDay.get("N").put("14-15", mapHourOccurrencesByEachDay.get("N").get("14-15") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.FRIDAY) { mapHourOccurrencesByEachDay.get("R").put("14-15", mapHourOccurrencesByEachDay.get("R").get("14-15") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.SATURDAY) { mapHourOccurrencesByEachDay.get("L").put("14-15", mapHourOccurrencesByEachDay.get("L").get("14-15") + 1); }
                    else { mapHourOccurrencesByEachDay.get("P").put("14-15", mapHourOccurrencesByEachDay.get("P").get("14-15") + 1); }
                }
                else if (logTime.getHour() == 16 || logTime.getHour() == 17) {
                    if (localDate.getDayOfWeek() == DayOfWeek.MONDAY) { mapHourOccurrencesByEachDay.get("E").put("16-17", mapHourOccurrencesByEachDay.get("E").get("16-17") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.TUESDAY) { mapHourOccurrencesByEachDay.get("T").put("16-17", mapHourOccurrencesByEachDay.get("T").get("16-17") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.WEDNESDAY) { mapHourOccurrencesByEachDay.get("K").put("16-17", mapHourOccurrencesByEachDay.get("K").get("16-17") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.THURSDAY) { mapHourOccurrencesByEachDay.get("N").put("16-17", mapHourOccurrencesByEachDay.get("N").get("16-17") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.FRIDAY) { mapHourOccurrencesByEachDay.get("R").put("16-17", mapHourOccurrencesByEachDay.get("R").get("16-17") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.SATURDAY) { mapHourOccurrencesByEachDay.get("L").put("16-17", mapHourOccurrencesByEachDay.get("L").get("16-17") + 1); }
                    else { mapHourOccurrencesByEachDay.get("P").put("16-17", mapHourOccurrencesByEachDay.get("P").get("16-17") + 1); }
                }
                else if (logTime.getHour() == 18 || logTime.getHour() == 19) {
                    if (localDate.getDayOfWeek() == DayOfWeek.MONDAY) { mapHourOccurrencesByEachDay.get("E").put("18-19", mapHourOccurrencesByEachDay.get("E").get("18-19") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.TUESDAY) { mapHourOccurrencesByEachDay.get("T").put("18-19", mapHourOccurrencesByEachDay.get("T").get("18-19") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.WEDNESDAY) { mapHourOccurrencesByEachDay.get("K").put("18-19", mapHourOccurrencesByEachDay.get("K").get("18-19") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.THURSDAY) { mapHourOccurrencesByEachDay.get("N").put("18-19", mapHourOccurrencesByEachDay.get("N").get("18-19") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.FRIDAY) { mapHourOccurrencesByEachDay.get("R").put("18-19", mapHourOccurrencesByEachDay.get("R").get("18-19") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.SATURDAY) { mapHourOccurrencesByEachDay.get("L").put("18-19", mapHourOccurrencesByEachDay.get("L").get("18-19") + 1); }
                    else { mapHourOccurrencesByEachDay.get("P").put("18-19", mapHourOccurrencesByEachDay.get("P").get("18-19") + 1); }
                }
                else if (logTime.getHour() == 20 || logTime.getHour() == 21) {
                    if (localDate.getDayOfWeek() == DayOfWeek.MONDAY) { mapHourOccurrencesByEachDay.get("E").put("20-21", mapHourOccurrencesByEachDay.get("E").get("20-21") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.TUESDAY) { mapHourOccurrencesByEachDay.get("T").put("20-21", mapHourOccurrencesByEachDay.get("T").get("20-21") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.WEDNESDAY) { mapHourOccurrencesByEachDay.get("K").put("20-21", mapHourOccurrencesByEachDay.get("K").get("20-21") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.THURSDAY) { mapHourOccurrencesByEachDay.get("N").put("20-21", mapHourOccurrencesByEachDay.get("N").get("20-21") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.FRIDAY) { mapHourOccurrencesByEachDay.get("R").put("20-21", mapHourOccurrencesByEachDay.get("R").get("20-21") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.SATURDAY) { mapHourOccurrencesByEachDay.get("L").put("20-21", mapHourOccurrencesByEachDay.get("L").get("20-21") + 1); }
                    else { mapHourOccurrencesByEachDay.get("P").put("22-23", mapHourOccurrencesByEachDay.get("P").get("20-21") + 1); }
                }
                else {
                    if (localDate.getDayOfWeek() == DayOfWeek.MONDAY) { mapHourOccurrencesByEachDay.get("E").put("22-23", mapHourOccurrencesByEachDay.get("E").get("22-23") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.TUESDAY) { mapHourOccurrencesByEachDay.get("T").put("22-23", mapHourOccurrencesByEachDay.get("T").get("22-23") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.WEDNESDAY) { mapHourOccurrencesByEachDay.get("K").put("22-23", mapHourOccurrencesByEachDay.get("K").get("22-23") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.THURSDAY) { mapHourOccurrencesByEachDay.get("N").put("22-23", mapHourOccurrencesByEachDay.get("N").get("22-23") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.FRIDAY) { mapHourOccurrencesByEachDay.get("R").put("22-23", mapHourOccurrencesByEachDay.get("R").get("22-23") + 1); }
                    else if (localDate.getDayOfWeek() == DayOfWeek.SATURDAY) { mapHourOccurrencesByEachDay.get("L").put("22-23", mapHourOccurrencesByEachDay.get("L").get("22-23") + 1); }
                    else { mapHourOccurrencesByEachDay.get("P").put("22-23", mapHourOccurrencesByEachDay.get("P").get("22-23") + 1); }
                }
            }
            for (int j = 0; j < 12; j++) {
                if (j == 0) {
                    zero.getData().add((new XYChart.Data<>("E", mapHourOccurrencesByEachDay.get("E").get("0-1"))));
                    zero.getData().add((new XYChart.Data<>("T", mapHourOccurrencesByEachDay.get("T").get("0-1"))));
                    zero.getData().add((new XYChart.Data<>("K", mapHourOccurrencesByEachDay.get("K").get("0-1"))));
                    zero.getData().add((new XYChart.Data<>("N", mapHourOccurrencesByEachDay.get("N").get("0-1"))));
                    zero.getData().add((new XYChart.Data<>("R", mapHourOccurrencesByEachDay.get("R").get("0-1"))));
                    zero.getData().add((new XYChart.Data<>("L", mapHourOccurrencesByEachDay.get("L").get("0-1"))));
                    zero.getData().add((new XYChart.Data<>("P", mapHourOccurrencesByEachDay.get("P").get("0-1"))));
                }
                else if (j == 1) {
                    two.getData().add((new XYChart.Data<>("E", mapHourOccurrencesByEachDay.get("E").get("2-3"))));
                    two.getData().add((new XYChart.Data<>("T", mapHourOccurrencesByEachDay.get("T").get("2-3"))));
                    two.getData().add((new XYChart.Data<>("K", mapHourOccurrencesByEachDay.get("K").get("2-3"))));
                    two.getData().add((new XYChart.Data<>("N", mapHourOccurrencesByEachDay.get("N").get("2-3"))));
                    two.getData().add((new XYChart.Data<>("R", mapHourOccurrencesByEachDay.get("R").get("2-3"))));
                    two.getData().add((new XYChart.Data<>("L", mapHourOccurrencesByEachDay.get("L").get("2-3"))));
                    two.getData().add((new XYChart.Data<>("P", mapHourOccurrencesByEachDay.get("P").get("2-3"))));
                }
                else if (j == 2) {
                    four.getData().add((new XYChart.Data<>("E", mapHourOccurrencesByEachDay.get("E").get("4-5"))));
                    four.getData().add((new XYChart.Data<>("T", mapHourOccurrencesByEachDay.get("T").get("4-5"))));
                    four.getData().add((new XYChart.Data<>("K", mapHourOccurrencesByEachDay.get("K").get("4-5"))));
                    four.getData().add((new XYChart.Data<>("N", mapHourOccurrencesByEachDay.get("N").get("4-5"))));
                    four.getData().add((new XYChart.Data<>("R", mapHourOccurrencesByEachDay.get("R").get("4-5"))));
                    four.getData().add((new XYChart.Data<>("L", mapHourOccurrencesByEachDay.get("L").get("4-5"))));
                    four.getData().add((new XYChart.Data<>("P", mapHourOccurrencesByEachDay.get("P").get("4-5"))));
                }
                else if (j == 3) {
                    six.getData().add((new XYChart.Data<>("E", mapHourOccurrencesByEachDay.get("E").get("6-7"))));
                    six.getData().add((new XYChart.Data<>("T", mapHourOccurrencesByEachDay.get("T").get("6-7"))));
                    six.getData().add((new XYChart.Data<>("K", mapHourOccurrencesByEachDay.get("K").get("6-7"))));
                    six.getData().add((new XYChart.Data<>("N", mapHourOccurrencesByEachDay.get("N").get("6-7"))));
                    six.getData().add((new XYChart.Data<>("R", mapHourOccurrencesByEachDay.get("R").get("6-7"))));
                    six.getData().add((new XYChart.Data<>("L", mapHourOccurrencesByEachDay.get("L").get("6-7"))));
                    six.getData().add((new XYChart.Data<>("P", mapHourOccurrencesByEachDay.get("P").get("6-7"))));
                }
                else if (j == 4) {
                    eight.getData().add((new XYChart.Data<>("E", mapHourOccurrencesByEachDay.get("E").get("8-9"))));
                    eight.getData().add((new XYChart.Data<>("T", mapHourOccurrencesByEachDay.get("T").get("8-9"))));
                    eight.getData().add((new XYChart.Data<>("K", mapHourOccurrencesByEachDay.get("K").get("8-9"))));
                    eight.getData().add((new XYChart.Data<>("N", mapHourOccurrencesByEachDay.get("N").get("8-9"))));
                    eight.getData().add((new XYChart.Data<>("R", mapHourOccurrencesByEachDay.get("R").get("8-9"))));
                    eight.getData().add((new XYChart.Data<>("L", mapHourOccurrencesByEachDay.get("L").get("8-9"))));
                    eight.getData().add((new XYChart.Data<>("P", mapHourOccurrencesByEachDay.get("P").get("8-9"))));
                }
                else if (j == 5) {
                    ten.getData().add((new XYChart.Data<>("E", mapHourOccurrencesByEachDay.get("E").get("10-11"))));
                    ten.getData().add((new XYChart.Data<>("T", mapHourOccurrencesByEachDay.get("T").get("10-11"))));
                    ten.getData().add((new XYChart.Data<>("K", mapHourOccurrencesByEachDay.get("K").get("10-11"))));
                    ten.getData().add((new XYChart.Data<>("N", mapHourOccurrencesByEachDay.get("N").get("10-11"))));
                    ten.getData().add((new XYChart.Data<>("R", mapHourOccurrencesByEachDay.get("R").get("10-11"))));
                    ten.getData().add((new XYChart.Data<>("L", mapHourOccurrencesByEachDay.get("L").get("10-11"))));
                    ten.getData().add((new XYChart.Data<>("P", mapHourOccurrencesByEachDay.get("P").get("10-11"))));
                }
                else if (j == 6) {
                    twelve.getData().add((new XYChart.Data<>("E", mapHourOccurrencesByEachDay.get("E").get("12-13"))));
                    twelve.getData().add((new XYChart.Data<>("T", mapHourOccurrencesByEachDay.get("T").get("12-13"))));
                    twelve.getData().add((new XYChart.Data<>("K", mapHourOccurrencesByEachDay.get("K").get("12-13"))));
                    twelve.getData().add((new XYChart.Data<>("N", mapHourOccurrencesByEachDay.get("N").get("12-13"))));
                    twelve.getData().add((new XYChart.Data<>("R", mapHourOccurrencesByEachDay.get("R").get("12-13"))));
                    twelve.getData().add((new XYChart.Data<>("L", mapHourOccurrencesByEachDay.get("L").get("12-13"))));
                    twelve.getData().add((new XYChart.Data<>("P", mapHourOccurrencesByEachDay.get("P").get("12-13"))));
                }
                else if (j == 7) {
                    fourteen.getData().add((new XYChart.Data<>("E", mapHourOccurrencesByEachDay.get("E").get("14-15"))));
                    fourteen.getData().add((new XYChart.Data<>("T", mapHourOccurrencesByEachDay.get("T").get("14-15"))));
                    fourteen.getData().add((new XYChart.Data<>("K", mapHourOccurrencesByEachDay.get("K").get("14-15"))));
                    fourteen.getData().add((new XYChart.Data<>("N", mapHourOccurrencesByEachDay.get("N").get("14-15"))));
                    fourteen.getData().add((new XYChart.Data<>("R", mapHourOccurrencesByEachDay.get("R").get("14-15"))));
                    fourteen.getData().add((new XYChart.Data<>("L", mapHourOccurrencesByEachDay.get("L").get("14-15"))));
                    fourteen.getData().add((new XYChart.Data<>("P", mapHourOccurrencesByEachDay.get("P").get("14-15"))));
                }
                else if (j == 8) {
                    sixteen.getData().add((new XYChart.Data<>("E", mapHourOccurrencesByEachDay.get("E").get("16-17"))));
                    sixteen.getData().add((new XYChart.Data<>("T", mapHourOccurrencesByEachDay.get("T").get("16-17"))));
                    sixteen.getData().add((new XYChart.Data<>("K", mapHourOccurrencesByEachDay.get("K").get("16-17"))));
                    sixteen.getData().add((new XYChart.Data<>("N", mapHourOccurrencesByEachDay.get("N").get("16-17"))));
                    sixteen.getData().add((new XYChart.Data<>("R", mapHourOccurrencesByEachDay.get("R").get("16-17"))));
                    sixteen.getData().add((new XYChart.Data<>("L", mapHourOccurrencesByEachDay.get("L").get("16-17"))));
                    sixteen.getData().add((new XYChart.Data<>("P", mapHourOccurrencesByEachDay.get("P").get("16-17"))));
                }
                else if (j == 9) {
                    eighteen.getData().add((new XYChart.Data<>("E", mapHourOccurrencesByEachDay.get("E").get("18-19"))));
                    eighteen.getData().add((new XYChart.Data<>("T", mapHourOccurrencesByEachDay.get("T").get("18-19"))));
                    eighteen.getData().add((new XYChart.Data<>("K", mapHourOccurrencesByEachDay.get("K").get("18-19"))));
                    eighteen.getData().add((new XYChart.Data<>("N", mapHourOccurrencesByEachDay.get("N").get("18-19"))));
                    eighteen.getData().add((new XYChart.Data<>("R", mapHourOccurrencesByEachDay.get("R").get("18-19"))));
                    eighteen.getData().add((new XYChart.Data<>("L", mapHourOccurrencesByEachDay.get("L").get("18-19"))));
                    eighteen.getData().add((new XYChart.Data<>("P", mapHourOccurrencesByEachDay.get("P").get("18-19"))));
                }
                else if (j == 10) {
                    twenty.getData().add((new XYChart.Data<>("E", mapHourOccurrencesByEachDay.get("E").get("20-21"))));
                    twenty.getData().add((new XYChart.Data<>("T", mapHourOccurrencesByEachDay.get("T").get("20-21"))));
                    twenty.getData().add((new XYChart.Data<>("K", mapHourOccurrencesByEachDay.get("K").get("20-21"))));
                    twenty.getData().add((new XYChart.Data<>("N", mapHourOccurrencesByEachDay.get("N").get("20-21"))));
                    twenty.getData().add((new XYChart.Data<>("R", mapHourOccurrencesByEachDay.get("R").get("20-21"))));
                    twenty.getData().add((new XYChart.Data<>("L", mapHourOccurrencesByEachDay.get("L").get("20-21"))));
                    twenty.getData().add((new XYChart.Data<>("P", mapHourOccurrencesByEachDay.get("P").get("20-21"))));
                }
                else {
                    twenty_two.getData().add((new XYChart.Data<>("E", mapHourOccurrencesByEachDay.get("E").get("22-23"))));
                    twenty_two.getData().add((new XYChart.Data<>("T", mapHourOccurrencesByEachDay.get("T").get("22-23"))));
                    twenty_two.getData().add((new XYChart.Data<>("K", mapHourOccurrencesByEachDay.get("K").get("22-23"))));
                    twenty_two.getData().add((new XYChart.Data<>("N", mapHourOccurrencesByEachDay.get("N").get("22-23"))));
                    twenty_two.getData().add((new XYChart.Data<>("R", mapHourOccurrencesByEachDay.get("R").get("22-23"))));
                    twenty_two.getData().add((new XYChart.Data<>("L", mapHourOccurrencesByEachDay.get("L").get("22-23"))));
                    twenty_two.getData().add((new XYChart.Data<>("P", mapHourOccurrencesByEachDay.get("P").get("22-23"))));
                }
            }
            stackedBarChartVisitsPerDayAndHour.setTitle("Kui palju iga päeva ja tunni kohta käivaid kirjeid esineb logides");
            stackedBarChartVisitsPerDayAndHour.getData().add(zero);
            stackedBarChartVisitsPerDayAndHour.getData().add(two);
            stackedBarChartVisitsPerDayAndHour.getData().add(four);
            stackedBarChartVisitsPerDayAndHour.getData().add(six);
            stackedBarChartVisitsPerDayAndHour.getData().add(eight);
            stackedBarChartVisitsPerDayAndHour.getData().add(ten);
            stackedBarChartVisitsPerDayAndHour.getData().add(twelve);
            stackedBarChartVisitsPerDayAndHour.getData().add(fourteen);
            stackedBarChartVisitsPerDayAndHour.getData().add(sixteen);
            stackedBarChartVisitsPerDayAndHour.getData().add(eighteen);
            stackedBarChartVisitsPerDayAndHour.getData().add(twenty);
            stackedBarChartVisitsPerDayAndHour.getData().add(twenty_two);
        });
    }
    private void displayBarChartVisitsPerHour() {
        Platform.runLater(() -> {
            XYChart.Series<String, Number> dataSeriesVisitsPerHour = new XYChart.Series<>();
            time = new ArrayList<>();
            timeOccurrences = new ArrayList<>();
            time.add("0-1");
            timeOccurrences.add(0);
            time.add("2-3");
            timeOccurrences.add(0);
            time.add("4-5");
            timeOccurrences.add(0);
            time.add("6-7");
            timeOccurrences.add(0);
            time.add("8-9");
            timeOccurrences.add(0);
            time.add("10-11");
            timeOccurrences.add(0);
            time.add("12-13");
            timeOccurrences.add(0);
            time.add("14-15");
            timeOccurrences.add(0);
            time.add("16-17");
            timeOccurrences.add(0);
            time.add("18-19");
            timeOccurrences.add(0);
            time.add("20-21");
            timeOccurrences.add(0);
            time.add("22-23");
            timeOccurrences.add(0);
            for (Log log : filteredResults) {
                String[] logArray = log.getTime().split(" ");
                DateTimeFormatter dateTimeFormatterTime = DateTimeFormatter.ofPattern("HH.mm");
                LocalTime logTime = LocalTime.parse(logArray[1], dateTimeFormatterTime);
                if (logTime.getHour() == 0 || logTime.getHour() == 1) {
                    timeOccurrences.add(0, timeOccurrences.get(0) + 1);
                    timeOccurrences.remove(1);
                }
                else if (logTime.getHour() == 2 || logTime.getHour() == 3) {
                    timeOccurrences.add(1, timeOccurrences.get(1) + 1);
                    timeOccurrences.remove(2);
                }
                else if (logTime.getHour() == 4 || logTime.getHour() == 5) {
                    timeOccurrences.add(2, timeOccurrences.get(2) + 1);
                    timeOccurrences.remove(3);
                }
                else if (logTime.getHour() == 6 || logTime.getHour() == 7) {
                    timeOccurrences.add(3, timeOccurrences.get(3) + 1);
                    timeOccurrences.remove(4);
                }
                else if (logTime.getHour() == 8 || logTime.getHour() == 9) {
                    timeOccurrences.add(4, timeOccurrences.get(4) + 1);
                    timeOccurrences.remove(5);
                }
                else if (logTime.getHour() == 10 || logTime.getHour() == 11) {
                    timeOccurrences.add(5, timeOccurrences.get(5) + 1);
                    timeOccurrences.remove(6);
                }
                else if (logTime.getHour() == 12 || logTime.getHour() == 13) {
                    timeOccurrences.add(6, timeOccurrences.get(6) + 1);
                    timeOccurrences.remove(7);
                }
                else if (logTime.getHour() == 14 || logTime.getHour() == 15) {
                    timeOccurrences.add(7, timeOccurrences.get(7) + 1);
                    timeOccurrences.remove(8);
                }
                else if (logTime.getHour() == 16 || logTime.getHour() == 17) {
                    timeOccurrences.add( 8, timeOccurrences.get(8) + 1);
                    timeOccurrences.remove(9);
                }
                else if (logTime.getHour() == 18 || logTime.getHour() == 19) {
                    timeOccurrences.add(9, timeOccurrences.get(9) + 1);
                    timeOccurrences.remove(10);
                }
                else if (logTime.getHour() == 20 || logTime.getHour() == 21) {
                    timeOccurrences.add(10, timeOccurrences.get(10) + 1);
                    timeOccurrences.remove(11);
                }
                else {
                    timeOccurrences.add( 11, timeOccurrences.get(11) + 1);
                    timeOccurrences.remove(12);
                }
            }
            for (int i = 0; i < time.size(); i++) {
                dataSeriesVisitsPerHour.getData().add(new XYChart.Data<>(time.get(i), timeOccurrences.get(i)));
            }
            barChartVisitsPerHour.setTitle("Kui palju erinevate ajavahemike kohta käivaid kirjeid esineb logides");
            barChartVisitsPerHour.setLegendVisible(false);
            barChartVisitsPerHour.getData().add(dataSeriesVisitsPerHour);
        });
    }
    private void displayScatterChartCorrelationBetweenLogsAndGrades() {
        Platform.runLater(() -> {
            List<Double> correlationX = new ArrayList<>();
            List<Double> correlationY = new ArrayList<>();
            XYChart.Series<Number, Number> dataSeriesCorrelation = new XYChart.Series<>();
            List<Map<String, String>> listOfGradesData = gradesProcessing.getGrades();
            listOfCorrelationData = new ArrayList<>();
            listOfGradesData.remove(0);
            for (String student : dataMapStudentName.keySet()) {
                Map<String, Double> hashMap = new TreeMap<>();
                for (Map<String, String> map : listOfGradesData) {
                    if (map.get("Name").equals(student)) {
                        if (!map.get("Hinne (Punktid)").equals("-")) {
                            dataSeriesCorrelation.getData().add(new XYChart.Data<>(dataMapStudentName.get(student), Double.valueOf(map.get("Hinne (Punktid)"))));
                            hashMap.put("Logs", Double.valueOf(dataMapStudentName.get(student)));
                            hashMap.put("Points", Double.valueOf(map.get("Hinne (Punktid)")));
                            listOfCorrelationData.add(hashMap);
                            correlationX.add(Double.valueOf(dataMapStudentName.get(student)));
                            correlationY.add(Double.valueOf(map.get("Hinne (Punktid)")));
                        }
                        break;
                    }
                }
            }
            scatterChartCorrelationBetweenLogsAndGrades.setTitle("Hinnete ja logide vaheline korrelatsioon");
            scatterChartCorrelationBetweenLogsAndGrades.setLegendVisible(false);
            scatterChartCorrelationBetweenLogsAndGrades.getData().add(dataSeriesCorrelation);
            displayCoefficient.setText("Korrelatsioonikordaja on " + calculateCorrelationCoefficient(correlationX, correlationY));
        });
    }
    // Display Alerts
    private void displayErrorAlert(String header, String content) {
        Alert alertDisplayError = new Alert(Alert.AlertType.ERROR);
        alertDisplayError.setTitle("Viga!");
        alertDisplayError.setHeaderText(header);
        alertDisplayError.setContentText(content);
        alertDisplayError.show();
    }
    private void displayInformationAlert(String title, String header, String content) {
        Alert alertTimeHoursFromAfterTimeHoursTo = new Alert(Alert.AlertType.INFORMATION);
        alertTimeHoursFromAfterTimeHoursTo.setTitle(title);
        alertTimeHoursFromAfterTimeHoursTo.setHeaderText(header);
        alertTimeHoursFromAfterTimeHoursTo.setContentText(content);
        alertTimeHoursFromAfterTimeHoursTo.show();
    }
    // Other methods
    private void makeLocalDateAndTimeNull() {
        localDateFrom = null;
        localDateTo = null;
        localTimeFrom = null;
        localTimeTo = null;
    }
    private void runMethodsForTabView(Stage stage) {
        displayTableViewLogs(stage);
        displayBarChartEventName();
        displayBarChartEventContext();
        displayBarChartStudentGroup();
        displayBarChartStudentName();
        displayTableViewStudentGrades();
        displayBarChartVisitsPerHour();
        displayStackedBarChartVisitsPerWeekAndDay();
        displayStackedBarChartVisitsPerDayAndHour();
        displayTableViewWeekAndDay(stage);
        displayTableViewDayAndHour(stage);
        displayScatterChartCorrelationBetweenLogsAndGrades();
        displayTableViewCorrelation(stage);

    }
    private Double calculateCorrelationCoefficient(List<Double> correlationX, List<Double> correlationY) {
        int length = correlationX.size();
        double sum_X = 0, sum_Y = 0, sum_XY = 0;
        double squareSum_X = 0, squareSum_Y = 0;
        Double[] arrayX = correlationX.toArray(new Double[0]);
        Double[] arrayY = correlationY.toArray(new Double[0]);
        // Calculate the variables needed to calculate coefficient
        for (int i = 0; i < length; i++) {
            sum_X = sum_X + arrayX[i];
            sum_Y = sum_Y + arrayY[i];
            sum_XY = sum_XY + arrayX[i] * arrayY[i];
            squareSum_X = squareSum_X + arrayX[i] * arrayX[i];
            squareSum_Y = squareSum_Y + arrayY[i] * arrayY[i];
        }
        // Calculate the coefficient
        return (length * sum_XY - sum_X * sum_Y) / (Math.sqrt((length * squareSum_X - sum_X * sum_X) * (length * squareSum_Y - sum_Y * sum_Y)));

    }
}
