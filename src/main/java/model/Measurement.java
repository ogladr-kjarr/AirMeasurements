package model;

import java.time.LocalDate;
import java.util.Optional;

    public record Measurement(LocalDate date,
                              String location,
                              String parameter,
                              String interval,
                              String unit,
                              Optional<Double> value,
                              String status){}


