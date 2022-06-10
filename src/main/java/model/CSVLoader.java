package model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class CSVLoader{

    private static final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'kk:mmZ");
    private static final Logger logger = LogManager.getLogger();

    public static List<Measurement> loadMeasurements(String downloadLocation){

        logger.atDebug().log("Beginning to load measurements from location: " + downloadLocation);
        return Stream.of(new File(downloadLocation).listFiles())
                .filter(file -> !file.isDirectory())
                .map(File::getAbsolutePath)
                .filter(name -> name.contains("csv"))
                .flatMap(CSVLoader::loadMeasurementsFromFile)
                .filter(measurement -> measurement.value().isPresent())
                .collect(Collectors.toList());
    }

    public static Stream<Measurement> loadMeasurementsFromFile(String filepath) {

        try{
            logger.atDebug().log("Attempting to load measurements from file: " + filepath);
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

        logger.atDebug().log("Iterating over file contents. " + (lines.size() - 1) + " measurements to load");
        // Start at 1, as line 0 is the header text
        for(int i = 1; i < lines.size(); i++){
            airMeasurements.add(CSVLoader.parseMeasurement(lines.get(i)));
        }
        return airMeasurements;
    }

    private static Measurement parseMeasurement(String line){

        final String[] columns = line.split(",");

        //The date field has a prefix unicode value in front of the opening quotation
        final OffsetDateTime date = OffsetDateTime.parse(columns[0].substring(1).replace("\"",""), DATE_PATTERN);
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

        return new Measurement(date, location, parameter, interval, unit, value, status);
    }
}
