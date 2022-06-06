package model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;

public class CSVLoader{

    private static final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'kk:mmZ");
    private static final Logger logger = LogManager.getLogger(CSVLoader.class.getName());

    public static List<Measurement> loadMeasurements(String recordType, String downloadLocation){
        Configurator.setLevel(logger.getName(), Level.DEBUG);

        //Get list of files i.e. have air or meteo in name
        return Stream.of(new File(downloadLocation).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getAbsolutePath)
                .filter(filename -> filename.contains(recordType))
                .flatMap(CSVLoader::loadMeasurementsFromFile)
                .collect(Collectors.toList());
    }

    public static Stream<Measurement> loadMeasurementsFromFile(String filepath) {

        try{
            logger.atDebug().log("At file: " + filepath);
            final Path dataPath = Paths.get(filepath);
            final List<String> lines = Files.readAllLines(dataPath);
            return parseFileContents(lines).stream();
        }catch(IOException e){
            logger.atError().log("IO Exception while parsing file at: " + filepath);
        }
        return new ArrayList<Measurement>().stream();
    }

    private static ArrayList<Measurement> parseFileContents(List<String> lines) throws IOException{

        ArrayList<Measurement> airMeasurements = new ArrayList<>();

        // Start at 1, as line 0 is the header text
        for(int i = 1; i < lines.size(); i++){
            airMeasurements.add(CSVLoader.parseMeasurement(lines.get(i)));
        }
        return airMeasurements;
    }

    private static Measurement parseMeasurement(String line){

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

        return new Measurement(recordDate, location, parameter, interval, unit, value, status);
    }
}
