package model;

import java.util.ArrayList;
import java.util.List;

public interface MeasurementAccess {

    boolean saveMeasurements(List<Measurement> measurements);
    ArrayList<Measurement> retrieveMeasurements();
    ArrayList<Measurement> retrieveMeasurementSubset(int startYear, int endYear);
}
