import model.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class Application {

    private static final Logger logger = LogManager.getLogger();
    private static final String downloadLocation = "src/main/resources/";

    public static void main(String args[]) throws java.io.IOException {

        boolean exit = false;

        char input;

        while (!exit) {
            System.out.println("To download the data and save to database press 'd'");
            System.out.println("To show stats from the measurements press 's'");
            System.out.println("To send to message queue, press 'm'");
            System.out.println("To exit press 'e'");

            input = (char) System.in.read();

            switch (input) {
                case 'd':
                    downloadAndSaveData();
                    break;
                case 's':
                    showStatistics();
                    break;
                case 'm':
                    break;
                case 'e':
                    exit = true;
                    break;
                default:
                    break;
            }
        }
    }

    private static void downloadAndSaveData(){

        CSVDownloader downloader = new CSVDownloader(downloadLocation);

        try {
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

            System.out.println("Download has been successful");

            List<Measurement> measurements = CSVLoader.loadMeasurements(downloadLocation);
            MeasurementAccess databaseAccess = new PostgreSQLAccess("127.0.0.1", "5432", "measurements", "postgres", "testingjava");
            boolean saveResult = databaseAccess.saveMeasurements(measurements);
            if(saveResult){
                System.out.println("Data has been saved to the database successfully");
            }else{
                System.out.println("Data has failed to save to the database");
            }
        } catch (DownloadException e){
            logger.atError().log("Error when trying to download files from main application");
            logger.atError().log(e);
            System.out.println("Download was not successful");
        }

    }

    private static void showStatistics(){
        logger.atDebug().log("Starting to load measurments from database");
        MeasurementAccess databaseAccess = new PostgreSQLAccess("127.0.0.1", "5432", "measurements", "postgres", "testingjava");
        ArrayList<Measurement> measurements = databaseAccess.retrieveMeasurements();
        AirMeasurementAnalyzer analyzer = new AirMeasurementAnalyzer(measurements);
        logger.atDebug().log("Finished loading and initialized the analyzer");
        analyzer.showSummary();

    }
}
