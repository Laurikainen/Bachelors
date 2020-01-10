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

    public void setTime(String time) {
        this.time = time;
    }

    public String getEventContext() {
        return eventContext;
    }

    public void setEventContext(String eventContext) {
        this.eventContext = eventContext;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getStudentGroup() {
        return studentGroup;
    }

    public void setStudentGroup(String studentGroup) {
        this.studentGroup = studentGroup;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}
