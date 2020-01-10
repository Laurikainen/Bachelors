package FilesProcessing;

import org.jsoup.Jsoup;
import java.io.*;
import java.util.*;

public class StudentInfoProcessing {
    private List<Map<String, String>> students = new ArrayList<>();
    private Map<String, String> studentAndGroup = new HashMap<>();
    private List<String> studentName = new ArrayList<>();
    private Set<String> studentGroup = new HashSet<>();
    private Set<String> studentCurriculum = new HashSet<>();

    public void processStudents(File file) {
        try {
            File tmpStudentFile = new File("newStudents.html");
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(tmpStudentFile));

            String currentLine;
            int counter = 0;
            while ((currentLine = bufferedReader.readLine()) != null) {
                if (counter < 2 || counter > 4) {
                    bufferedWriter.write(currentLine);
                }
                counter++;
            }
            bufferedReader.close();
            bufferedWriter.close();

            org.jsoup.nodes.Document doc = Jsoup.parse(tmpStudentFile, "UTF-8");
            org.jsoup.select.Elements rows = doc.select("tr");
            for (org.jsoup.nodes.Element row : rows) {
                int columnNumber = 0;
                Map<String, String> student = new HashMap<>();
                StringBuilder name = new StringBuilder();
                org.jsoup.select.Elements columns = row.select("td");
                for (org.jsoup.nodes.Element column : columns) {
                    if (columnNumber == 2) {
                        name.append(column.text());
                        name.append(" ");
                    } else if (columnNumber == 3) {
                        name.append(column.text());
                        studentName.add(String.valueOf(name));
                        student.put("Name", String.valueOf(name));
                    } else if (columnNumber == 5) {
                        studentCurriculum.add(column.text());
                        student.put("Curriculum", column.text());
                    } else if (columnNumber == 6) {
                        if (column.text().equals("")) {
                            studentGroup.add("-");
                            student.put("Group", "-");
                            studentAndGroup.put(studentName.get(studentName.size()-1), "-");
                        }
                        else {
                            studentGroup.add(column.text());
                            student.put("Group", column.text());
                            studentAndGroup.put(studentName.get(studentName.size()-1), column.text());
                        }

                    }
                    students.add(student);
                    columnNumber++;
                }
            }
            boolean value = tmpStudentFile.delete();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearAllStudentData() {
        students = new ArrayList<>();
        studentName = new ArrayList<>();
        studentGroup = new HashSet<>();
        studentCurriculum = new HashSet<>();
        studentAndGroup = new HashMap<>();
    }

    public List<String> getStudentName() {
        return studentName;
    }

    public Set<String> getStudentGroup() {
        return studentGroup;
    }

    public Set<String> getStudentCurriculum() {
        return studentCurriculum;
    }

    public Map<String, String> getStudentAndGroup() {
        return studentAndGroup;
    }

    public List<Map<String, String>> getStudents() {
        return students;
    }
}
