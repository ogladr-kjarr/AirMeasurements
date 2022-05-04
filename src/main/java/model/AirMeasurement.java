package model;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

// models air measurements taken from the files found here:
// https://data.europa.eu/data/datasets/6db44316-9717-4a98-8a83-577d4cb25afc-stadt-zurich?locale=en
// https://data.europa.eu/data/datasets/3d0c33d6-ec57-426a-918c-ac8a60573789-stadt-zurich?locale=en
public class AirMeasurement {

    private final LocalDate date;
    private final String location;
    private final String parameter;
    private final String interval;
    private final String unit;
    private final Optional<Double> value;
    private final String status;

    public AirMeasurement(final LocalDate date,
                          final String location,
                          final String parameter,
                          final String interval,
                          final String unit,
                          final Optional<Double> value,
                          final String status){

        this.date = date;
        this.location = location;
        this.parameter = parameter;
        this.interval = interval;
        this.unit = unit;
        this.value = value;
        this.status = status;
    }

    public boolean hasValue(){
        return value.isPresent();
    }

    public String getLocation(){
        return location;
    }

    public String getParameter(){
        return parameter;
    }

    public String getLocationAndParameter(){
        return getLocation() + ": " + getParameter();
    }

    public double getValue(){
        return value.get();
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AirMeasurement that = (AirMeasurement) o;

        return this.date.equals(that.date) &&
                this.location.equals(that.location) &&
                this.parameter.equals(that.parameter) &&
                this.interval.equals(that.interval) &&
                this.unit.equals(that.unit) &&
                this.value.equals(that.value) &&
                this.status.equals(that.status);
    }

    @Override
    public String toString(){
        return "Date: " + this.date.toString() +
                ", Location: " + this.location +
                ", Parameter: " + this.parameter +
                ", Interval: " + this.interval +
                ", Unit: " + this.unit +
                ", Value: " + this.value +
                ", Status: " + this.status;
    }

    @Override
    public int hashCode(){
        return Objects.hash(date, location, parameter, interval, unit, value, status);
    }
}
