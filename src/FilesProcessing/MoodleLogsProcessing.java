package FilesProcessing;

import java.io.*;
import java.util.*;

public class MoodleLogsProcessing {

    private List<Map<String, String>> logs = new ArrayList<>();
    private Set<String> user = new HashSet<>();
    private Set<String> eventContext = new HashSet<>();
    private Set<String> component = new HashSet<>();
    private Set<String> eventName = new HashSet<>();

    public void processLogs(File file) {
        String line;
        String[] log = new String[0];
        int skipLine = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                if (skipLine != 0) {
                    skipLine--;
                }
                else {
                    try {
                        log = makeQuotedSentencesIntoOne(line);
                        if (log.length == 9) {
                            addData(log);
                        } else if (log.length == 8 && line.substring(line.length() - 4).equals("cli,")) {
                            addData(log);
                        } else {
                            throw new ArrayIndexOutOfBoundsException();
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        while (log.length != 9) {
                            line = line.replace("\n", "");
                            line = line + br.readLine();
                            line = line.replace("            ", " ");
                            skipLine++;
                            log = makeQuotedSentencesIntoOne(line);
                        }
                        addData(log);
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addData(String[] log) {
        Map<String, String> oneLog = new HashMap<>();
        List<String> data = new ArrayList<>();
        data.add(log[0]);
        data.add(log[1]);
        data.add(log[3]);
        data.add(log[4]);
        data.add(log[5]);

        oneLog.put("Time", log[0]);
        oneLog.put("Name", log[1]);
        oneLog.put("Event context", log[3]);
        oneLog.put("Component", log[4]);
        oneLog.put("Event name", log[5]);
        logs.add(oneLog);
        user.add(data.get(1));
        eventContext.add(data.get(2));
        component.add(data.get(3));
        eventName.add(data.get(4));
    }


    private String[] makeQuotedSentencesIntoOne(String line) {
        String separator = ",";
        String[] log = line.split(separator);
        List<String> listLog = new ArrayList<>(Arrays.asList(log));
        for (int i = 0; i < log.length-1; i++) {
            int beginning = i;
            if (log[i].contains("\"")) {
                if (log[i].indexOf("\"") == log[i].lastIndexOf("\"")) {
                    i++;
                    while (!log[i].contains("\"")) {
                        i++;
                    }
                    i++;
                }
            }
            StringBuilder data = new StringBuilder();
            for (int j = 0; j < i - beginning; j++) {
                data.append(log[beginning + j]);
                listLog.remove(beginning);
            }
            if (data.length() > 0) {
                listLog.add(beginning, String.valueOf(data));
                log = listLog.toArray(new String[0]);
            }
        }
        return log;
    }

    public void clearAllLogData() {
        logs = new ArrayList<>();
        user = new HashSet<>();
        eventContext = new HashSet<>();
        component = new HashSet<>();
        eventName = new HashSet<>();
    }

    public List<Map<String, String>> getLogs() {
        return logs;
    }

    public Set<String> getUser() {
        return user;
    }

    public Set<String> getEventContext() {
        return eventContext;
    }

    public Set<String> getComponent() {
        return component;
    }

    public Set<String> getEventName() {
        return eventName;
    }
}
