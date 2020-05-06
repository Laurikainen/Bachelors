/* 
  Combining data for correlation table, so it could be displayed.
*/

package MainLogic;

public class Correlation {
    private Double grade;
    private Double logs;

    public Correlation() {
    }

    public Correlation(Double grade, Double logs) {
        this.grade = grade;
        this.logs = logs;
    }

    public Double getGrade() {
        return grade;
    }

    public Double getLogs() {
        return logs;
    }
}
