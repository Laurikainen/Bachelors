package MainLogic;

public class Log {
    // Variables for all the Log parameters
    private String time;
    private String eventContext;
    private String eventName;
    private String studentGroup;
    private String studentName;
    // Constructors to create a Log instances
    public Log() {
    }
    public Log(String time, String eventContext, String eventName, String studentGroup, String studentName) {
        this.time = time;
        this.eventContext = eventContext;
        this.eventName = eventName;
        this.studentGroup = studentGroup;
        this.studentName = studentName;
    }
    // Getters to get all the log parameters
    public String getTime() { return time; }
    public String getEventContext() {
        return eventContext;
    }
    public String getEventName() {
        return eventName;
    }
    public String getStudentGroup() {
        return studentGroup;
    }
    public String getStudentName() {
        return studentName;
    }

}
