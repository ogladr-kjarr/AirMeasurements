package model;

import java.time.OffsetDateTime;
import java.util.Optional;

    public record Measurement(OffsetDateTime date,
                              String location,
                              String parameter,
                              String interval,
                              String unit,
                              Optional<Double> value,
                              String status){}


