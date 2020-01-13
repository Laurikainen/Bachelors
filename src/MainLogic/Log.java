package MainLogic;

public class Log {
    private String time;
    private String eventContext;
    private String eventName;
    private String studentGroup;
    private String studentName;

    public Log() {
    }

    public Log(String time, String eventContext, String eventName, String studentGroup, String studentName) {
        this.time = time;
        this.eventContext = eventContext;
        this.eventName = eventName;
        this.studentGroup = studentGroup;
        this.studentName = studentName;
    }

    public String getTime() {
        return time;
    }

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
