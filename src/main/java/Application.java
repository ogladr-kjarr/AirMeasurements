import model.CSVDownloader;
import model.CSVLoader;
import model.Measurement;
import model.PostgreSQLAccess;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class Application {

    private static final Logger logger = LogManager.getLogger();

    public static void main(String args[]) throws java.sql.SQLException{

        logger.atTrace().log("Starting application");

        if (args[0].equals("download-to-csv-database")){
            CSVDownloader downloader = new CSVDownloader("src/main/resources/");

            downloader.downloadData(
                    "https://data.stadt-zuerich.ch/dataset/ugz_luftschadstoffmessung_tageswerte/download/ugz_ogd_air_d1_",
                    "air",
                    1983,
                    2022);

            downloader.downloadData(
                    "https://data.stadt-zuerich.ch/dataset/ugz_meteodaten_stundenmittelwerte/download/ugz_ogd_meteo_h1_",
                    "meteo",
                    1992,
                    2022);

            List<Measurement> measurements = CSVLoader.loadMeasurements("air", "src/main/resources/");
        } else if (args[0].equals("read-from-db")) {
            List<Measurement> measurements = CSVLoader.loadMeasurements("air", "src/main/resources/");

            PostgreSQLAccess pgAccess = new PostgreSQLAccess(
                    "127.0.0.1", "5432", "measurements", "postgres", "testingjava");

            boolean insertOK = pgAccess.saveMeasurements(measurements);
        }


    }
}
