package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CSVParser{

    private static final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'kk:mmZ");

    public static ArrayList<AirMeasurement> parse(String filepath) throws IOException {

        final Path dataPath = Paths.get(filepath);
        return parseFileContents(dataPath);
    }

    private static ArrayList<AirMeasurement> parseFileContents(Path dataPath) throws IOException{

        final List<String> lines = Files.readAllLines(dataPath);
        ArrayList<AirMeasurement> airMeasurements = new ArrayList<>();

        for(String line: lines){
            airMeasurements.add(CSVParser.parseMeasurement(line));
        }

        return airMeasurements;
    }

    private static AirMeasurement parseMeasurement(String line){

        final String[] columns = line.split(",");

        //The date field has a prefix unicode value in front of the opening quotation
        final LocalDate recordDate = LocalDate.parse(columns[0].substring(1).replace("\"",""), DATE_PATTERN);
        final String location = columns[1].replace("\"","");
        final String parameter = columns[2].replace("\"","");
        final String interval = columns[3].replace("\"","");
        final String unit = columns[4].replace("\"","");
        Optional<Double> value;
        try{
            value = Optional.of(Double.parseDouble(columns[5]));
        }catch(NumberFormatException e){
            value = Optional.empty();
        }

        final String status = columns[6].replace("\"","");

        return new AirMeasurement(recordDate, location, parameter, interval, unit, value, status);
    }
}
