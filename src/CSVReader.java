import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CSVReader {
    private BufferedReader reader;
    private String delimiter;
    private boolean hasHeader;
    private List<String> columnLabels = new ArrayList<>();
    private Map<String, Integer> columnLabelsToInt = new HashMap<>();
    private String[]current;

    /**
     * @param filename  - nazwa pliku
     * @param delimiter - separator pól
     * @param hasHeader - czy plik ma wiersz nagłówkowy
     */
    public CSVReader(String filename, String delimiter, boolean hasHeader) throws IOException {
        this(new BufferedReader(new FileReader(filename)),delimiter,hasHeader);
    }

    public CSVReader(String filename,String delimiter) throws IOException {
        this(filename,delimiter,false);
    }

    public CSVReader(String filename) throws IOException {
        this(filename,",");
    }

    public CSVReader(Reader reader, String delimiter, boolean hasHeader) {
        try {
            this.reader = new BufferedReader(reader);
            this.delimiter = delimiter;
            this.hasHeader = hasHeader;
            this.columnLabels = new ArrayList<>();
            this.columnLabelsToInt = new HashMap<>();

            if (hasHeader) parseHeader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void parseHeader() throws IOException {
        String line = reader.readLine();
        if(line == null) return;
        String[] header = line.split(delimiter);

        for(int i = 0; i < header.length; i++) {
            columnLabels.add(header[i]);
            columnLabelsToInt.put(header[i],i);
        }
    }

    boolean next() throws IOException {
        String line = reader.readLine();
        if(line == null) {
            return false;
        }
        current = line.split(delimiter, -1);
        return true;
    }

    List<String> getColumnLabels() {
        return columnLabels;
    }

    int getRecordLength() {
        return current.length;
    }

    boolean isMissing(int columnIndex) {
        return current == null || columnIndex >= current.length || columnIndex < 0 || current[columnIndex].isEmpty();
    }

    boolean isMissing(String columnLabel) {
        Integer columnIndex = columnLabelsToInt.get(columnLabel);
        return columnIndex == null || isMissing(columnIndex);
    }

    String get(int columnIndex) {
        if(isMissing(columnIndex)){
            return "";
        }
        return current[columnIndex];
    }

    String get(String columnLabel){
        if(isMissing(columnLabel)){
            return "";
        }
        return current[columnLabelsToInt.get(columnLabel)];
    }

    int getInt(int columnIndex) throws Exception {
        if(get(columnIndex).isEmpty()) {
            throw new Exception("empty int index");
        }
        return Integer.parseInt(get(columnIndex));
    }

    int getInt(String columnLabel) throws Exception {
        if(get(columnLabel).isEmpty()) {
            throw new Exception("empty int label");
        }
        return Integer.parseInt(get(columnLabel));
    }

    long getLong(int columnIndex) throws Exception {
        if(this.get(columnIndex).isEmpty()) {
            throw new Exception("Empty long index");
        }
        return Long.parseLong(get(columnIndex));
    }

    long getLong(String columnLabel) throws Exception {
        if(this.get(columnLabel).isEmpty()) {
            throw new Exception("Empty long label");
        }
        return Long.parseLong(get(columnLabel));
    }

    double getDouble(int columnIndex) throws Exception {
        if(get(columnIndex).isEmpty()) {
            throw new Exception("Empty double index");
        }
        return Double.parseDouble(get(columnIndex));
    }

    double getDouble(String columnLabel) throws Exception {
        if(this.get(columnLabel).isEmpty()) {
            throw new Exception("Empty double label");
        }
        return Double.parseDouble(get(columnLabel));
    }

    LocalTime getTime(int columnIndex, String format) {
        if (isMissing(columnIndex)) return null;
        String timeString = current[columnIndex];
        return LocalTime.parse(timeString, DateTimeFormatter.ofPattern(format));
    }

    LocalTime getTime(String columnLabel, String format) {
        Integer columnIndex = columnLabelsToInt.get(columnLabel);
        if (columnIndex == null) return null;
        return getTime(columnIndex, format);
    }

    LocalDate getDate(int columnIndex, String format) {
        if (isMissing(columnIndex)) return null;
        String dateString = current[columnIndex];
        return LocalDate.parse(dateString, DateTimeFormatter.ofPattern(format));
    }

    LocalDate getDate(String columnLabel, String format) {
        Integer columnIndex = columnLabelsToInt.get(columnLabel);
        if (columnIndex == null) return null;
        return getDate(columnIndex, format);
    }
}