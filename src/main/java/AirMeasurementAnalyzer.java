import model.Measurement;

import java.time.temporal.TemporalField;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import static java.util.stream.Collectors.*;

public class AirMeasurementAnalyzer {

    private final ArrayList<Measurement> measurements;

    public AirMeasurementAnalyzer(ArrayList<Measurement> measurements){

        this.measurements = measurements;
    }

    public void showSummary(){
        Map<String, Double> locationAndParameter = getAverageByLocationAndParameter();
        System.out.println("-----------------------------------------------------------------------------------------");
        for(String currentKey: locationAndParameter.keySet()){
            System.out.println(currentKey + ": " + locationAndParameter.get(currentKey));
        }
        System.out.println("-----------------------------------------------------------------------------------------");
        System.out.println("Average temperature for Zch_Stampfenbachstrasse by month");

        Map<String, Double> averageTemps = getAverageTemperatureByMonth();
        for(String currentKey: averageTemps.keySet()){
            System.out.println(currentKey + ": " + averageTemps.get(currentKey));
        }

    }

    public Map<String, Double> getAverageTemperatureByMonth(){
        return measurements.stream()
                .collect(groupingBy(measurement -> measurement.date().getYear() + ":" + measurement.date().getMonth(),
                        averagingDouble(measurement -> measurement.value().orElse(0.0))));
    }

    public Map<String, Double> getAverageByLocationAndParameter() {
        return measurements.stream()
                .collect(groupingBy(measurement -> measurement.location() + ":" + measurement.parameter(),
                        averagingDouble(measurement -> measurement.value().orElse(0.0))));

    }
}
