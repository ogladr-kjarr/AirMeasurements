import model.AirMeasurement;

import java.util.ArrayList;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;


import static java.util.stream.Collectors.*;

public class AirMeasurementAnalyzer {

    private final ArrayList<AirMeasurement> measurements;

    public AirMeasurementAnalyzer(ArrayList<AirMeasurement> measurements){

        this.measurements = measurements;
    }

    public Map<String, Double> getAverageMeasurementsByLocation(Predicate<AirMeasurement> p, Function<AirMeasurement, Double> f){
        Map<String, Double> a = measurements.stream()
                .filter(AirMeasurement::hasValue)
                .filter(p)
                .collect(groupingBy(AirMeasurement::getLocationAndParameter, averagingDouble(AirMeasurement::getValue)));

        return a;
    }

    public Map<String, Double> getAverageMeasurementByLocationAndFeature(Predicate<AirMeasurement> locationFilter, Predicate<AirMeasurement> featureFilter){
        Map<String, Double> averages = measurements.stream()
                .filter(AirMeasurement::hasValue)
                .filter(locationFilter)
                .filter(featureFilter)
                .collect(groupingBy(AirMeasurement::getLocation, averagingDouble(AirMeasurement::getValue)));

        return averages;
    }
}
