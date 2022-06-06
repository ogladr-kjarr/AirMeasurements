import model.Measurement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import static java.util.stream.Collectors.*;

public class AirMeasurementAnalyzer {

    private final ArrayList<Measurement> measurements;

    public AirMeasurementAnalyzer(ArrayList<Measurement> measurements){

        this.measurements = measurements;
    }

    public Map<String, Double> getAverageByLocationAndParameter(){
        Map<String, Double> averages = measurements.stream()
                .collect(groupingBy(measurement -> measurement.location() + ":" + measurement.parameter(),
                        averagingDouble(measurement -> measurement.value().orElse(0.0))));

        return averages;
    }

    public Map<String, Double> searchAverageByLocationAndParameter(String location, String parameter){
        return getSubsetByLocationAndParameter(location, parameter).stream()
                .collect(groupingBy(measurement -> measurement.location() + ":" + measurement.parameter(),
                        averagingDouble(measurement -> measurement.value().orElse(0.0))));
    }

    public List<Measurement> getSubsetByLocationAndParameter(String location, String parameter){
        return measurements.stream()
                .filter(measurement -> measurement.location().equals(location))
                .filter(measurement -> measurement.parameter().equals(parameter))
                .collect(toList());
    }


}
