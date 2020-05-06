/*
  Log file processing to get values about name, time, event context and event name in order
  to produce different tables and graphs and to combine the data with grades and by student groups.
*/

package FilesProcessing;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class MoodleLogsProcessing {

    private List<Map<String, String>> logs = new ArrayList<>();
    private Set<String> user = new HashSet<>();
    private Set<String> eventContext = new HashSet<>();
    // Process all the logs when given the file name using BufferReader
    public void processLogs(File file) {

        String line;
        String[] log = new String[0];
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
            while ((line = br.readLine()) != null) {
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
                    while (!(log.length == 9) && !(log.length == 8 && line.substring(line.length() - 4).equals("cli,"))) {
                        line = line.replace("\n", "");
                        line = line + br.readLine();
                        line = line.replace("            ", " ");
                        log = makeQuotedSentencesIntoOne(line);
                    }
                    addData(log);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    // Add all the data from log files into variables
    private void addData(String[] log) {
        Map<String, String> oneLog = new HashMap<>();
        if (log[3].startsWith("\"")) {
            log[3] = log[3].replaceFirst("\"", "");
            log[3] = log[3].substring(0, log[3].length()-1);
        }
        oneLog.put("Time", log[0].replaceAll("\"", ""));
        oneLog.put("Name", log[1].replaceAll("\"", ""));
        oneLog.put("Event context", log[3]);
        oneLog.put("Event name", log[5].replaceAll("\"", ""));
        logs.add(oneLog);
        user.add(log[1].replaceAll("\"", ""));
        eventContext.add(log[3]);
    }
    // Make logs that have extra commas into the right shape for an array
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
    // Clear all logs data, so new logs could be processed
    public void clearAllLogData() {
        logs = new ArrayList<>();
        user = new HashSet<>();
        eventContext = new HashSet<>();
    }
    // Getters to get the log parameters in other classes
    public List<Map<String, String>> getLogs() { return logs; }
    public Set<String> getUser() { return user; }
    public Set<String> getEventContext() { return eventContext; }
}
