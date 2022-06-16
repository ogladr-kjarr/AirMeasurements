import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

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
                    sendToQueue();
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
                logger.atDebug().log("Data has been saved to the database successfully");
            }else{
                logger.atDebug().log("Data has failed to save to the database");
            }
        } catch (DownloadException e){
            logger.atError().log("Error when trying to download files from main application");
            logger.atError().log(e);
        }

    }

    private static ArrayList<Measurement> loadDataFromDB(){
        logger.atDebug().log("Starting to load measurments from database");
        MeasurementAccess databaseAccess = new PostgreSQLAccess("127.0.0.1", "5432", "measurements", "postgres", "testingjava");
        ArrayList<Measurement> measurements = databaseAccess.retrieveMeasurements();
        logger.atDebug().log("Finished loading measurements from database");
        return measurements;
    }

    private static void showStatistics(){
        ArrayList<Measurement> measurements = loadDataFromDB();
        AirMeasurementAnalyzer analyzer = new AirMeasurementAnalyzer(measurements);
        logger.atDebug().log("Finished loading and initialized the analyzer");
        analyzer.showSummary();

    }

    private static Properties getKafkaProperties(){
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("linger.ms", 1);
        props.put("request.timeout.ms", 500);
        props.put("delivery.timeout.ms", 1000);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return props;
    }
    private static void sendToQueue()  {

        ArrayList<Measurement> measurements = loadDataFromDB();
        Properties props = getKafkaProperties();

        logger.atDebug().log("Creating Kafka producer");
        ObjectMapper jsonMapper = new ObjectMapper().registerModule(new JavaTimeModule());

        try(Producer<String, String> producer = new KafkaProducer<>(props)){
            logger.atDebug().log("Starting to send to Kafka");
            for (Measurement m: measurements){
                producer.send(new ProducerRecord<String, String>("measurements", m.location(), jsonMapper.writeValueAsString(m)));
            }
            logger.atDebug().log("Finished sending to Kafka");
        }catch(JsonProcessingException e){
            logger.atError().log("Problem serializing Measurement reference");
            logger.atError().log(e);
        }
    }
}
