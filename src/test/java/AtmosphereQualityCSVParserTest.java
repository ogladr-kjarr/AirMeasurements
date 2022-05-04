import model.AirMeasurement;
import model.CSVParser;
import org.junit.Assert;
import org.junit.Test;
import org.junit.Before;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

public class AtmosphereQualityCSVParserTest {

    private static final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'kk:mmZ");


    @Test
    public void parseCSVLinesMeteo() throws Exception {

        ArrayList<AirMeasurement> measurements = CSVParser.parse("src/test/resources/Swiss_Meteo_2021.csv");

        LocalDate firstDate = LocalDate.parse("2021-01-01T00:00+0100", DATE_PATTERN);
        LocalDate lastDate = LocalDate.parse("2021-12-31T21:00+0100", DATE_PATTERN);
        Optional<Double> firstValue = Optional.empty();

        AirMeasurement firstMeasurement = new AirMeasurement(
                firstDate,"Zch_Stampfenbachstrasse","T","h1","°C", firstValue,"provisorisch");

        AirMeasurement lastMeasurement = new AirMeasurement(
                lastDate,"Zch_Rosengartenstrasse","WVs","h1","m/s",Optional.of(0.19),"provisorisch");

        Assert.assertEquals(firstMeasurement, measurements.get(0));
        Assert.assertEquals(lastMeasurement, measurements.get(1));
    }

    @Test
    public void parseCSVLinesAirQuality() throws Exception {

        ArrayList<AirMeasurement> measurements = CSVParser.parse("src/test/resources/Swiss_Air_Quality_2021.csv");

        LocalDate firstDate = LocalDate.parse("2021-01-01T00:00+0100", DATE_PATTERN);
        LocalDate lastDate = LocalDate.parse("2021-12-29T00:00+0100", DATE_PATTERN);

        AirMeasurement firstMeasurement = new AirMeasurement(
                firstDate,"Zch_Stampfenbachstrasse","SO2","d1","µg/m3",Optional.of(1.59),"provisorisch");

        AirMeasurement lastMeasurement = new AirMeasurement(
                lastDate,"Zch_Heubeeribüel","O3_nb_h1>120","d1","1",Optional.of(0.0),"provisorisch");

        Assert.assertEquals(firstMeasurement, measurements.get(0));
        Assert.assertEquals(lastMeasurement, measurements.get(1));
    }
}


