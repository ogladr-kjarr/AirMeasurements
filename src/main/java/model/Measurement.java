package model;

import java.time.OffsetDateTime;

    public record Measurement(OffsetDateTime date,
                              String location,
                              String parameter,
                              String interval,
                              String unit,
                              Double value,
                              String status){}


