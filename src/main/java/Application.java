import model.AirMeasurement;
import model.CSVParser;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Map;

public class Application {

    public static final String airQuality = "src/main/resources/Swiss_Air_Quality_2021.csv";
    public static final String airMeteo = "src/main/resources/Swiss_Meteo_2021.csv";

    public static void main(String args[]) throws IOException{

        //ArrayList<AirMeasurement> qualityMeasurements = CSVParser.parse(airQuality);
        ArrayList<AirMeasurement> meteoMeasurements = CSVParser.parse(airMeteo);

        //System.out.println("Length of quality: " + qualityMeasurements.size());
        System.out.println("Lenght of meteo: " + meteoMeasurements.size());

        //qualityMeasurements.addAll(meteoMeasurements);
        AirMeasurementAnalyzer ama = new AirMeasurementAnalyzer(meteoMeasurements);
//        Map<String, Double> locMeasure = ama.getAverageMeasurementsByLocation(AirMeasurement::hasValue,AirMeasurement::getValue);
//
//        for(String key: locMeasure.keySet()){
//            System.out.println(key + ": " +locMeasure.get(key));
//        }
//

        Map<String, Double> locMeasure = ama.getAverageMeasurementByLocationAndFeature (
                x -> x.getLocation().equals("Zch_Rosengartenstrasse"),
                x -> x.getParameter().equals("T")
        );
        for(String key: locMeasure.keySet()){
            System.out.println(key + ": " +locMeasure.get(key));
        }

    }
}
