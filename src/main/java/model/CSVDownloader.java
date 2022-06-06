package model;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;

public class CSVDownloader {

    public final String downloadLocation;
    private static final Logger logger = LogManager.getLogger(CSVDownloader.class.getName());

    public CSVDownloader(String downloadLocation){
        this.downloadLocation = downloadLocation;

        Configurator.setLevel(logger.getName(), Level.DEBUG);
    }

    public void downloadData(String url, String recordType, int startYear, int endYear) {

        int currentYear = startYear;

        while (currentYear <= endYear) {

            logger.atDebug().log("Current year: " + currentYear + ", End year: " + endYear);
            String downloadToFileName = downloadLocation + recordType + "_" + currentYear + ".csv";

            if (fileDoesntExist(downloadToFileName)) {
                try {
                    URL sourceURL = new URL(url + currentYear + ".csv");
                    downloadIndividualFile(sourceURL, recordType, downloadToFileName, currentYear);

               } catch (java.net.MalformedURLException e) {
                    logger.atError().log("Malformed URL Exception while creating URL for web-based data source");
                }
            }else{
                logger.atDebug().log("File " + downloadToFileName + " already exists.");
            }
            currentYear += 1;
        }
    }

    private boolean fileDoesntExist(String downloadToFileName){
        Path path = Paths.get(downloadToFileName);
        return !Files.exists(path);
    }

    private void downloadIndividualFile(URL sourceURL, String recordType, String downloadToFileName, int currentYear){

        try (ReadableByteChannel source = Channels.newChannel(sourceURL.openStream());
             FileOutputStream target = new FileOutputStream(new File(downloadToFileName))) {
            target.getChannel().transferFrom(source, 0, Long.MAX_VALUE);
            logger.atDebug().log("Finished downloading " + recordType + " measurements for year: " + currentYear + ".");
        } catch (java.io.IOException e) {
            logger.atError().log("IO Exception while opening URL Stream or File Output Stream");
        }
    }
}
