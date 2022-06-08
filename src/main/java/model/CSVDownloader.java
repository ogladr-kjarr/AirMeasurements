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

    private final String downloadLocation;
    private static final Logger logger = LogManager.getLogger();

    public CSVDownloader(String downloadLocation){
        this.downloadLocation = downloadLocation;
        logger.atTrace().log("Initialized CSVDownloader");
    }

    public void downloadData(String url, String recordType, int startYear, int endYear) {

        int currentYear = startYear;
        logger.atDebug().log("Starting downloads for record type: " + recordType + ", starting from: " + startYear + ", to end year: " + endYear);

        while (currentYear <= endYear) {

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
        logger.atDebug().log("Checking file " + downloadToFileName + " exists");
        Path path = Paths.get(downloadToFileName);
        return !Files.exists(path);
    }

    private void downloadIndividualFile(URL sourceURL, String recordType, String downloadToFileName, int currentYear){

        logger.atDebug().log("Attempting to download file " + downloadToFileName);
        try (ReadableByteChannel source = Channels.newChannel(sourceURL.openStream());
             FileOutputStream target = new FileOutputStream(new File(downloadToFileName))) {
            target.getChannel().transferFrom(source, 0, Long.MAX_VALUE);
            logger.atDebug().log("Finished downloading " + recordType + " measurements for year: " + currentYear + ".");
        } catch (java.io.IOException e) {
            logger.atError().log("IO Exception while opening URL Stream or File Output Stream");
        }
    }
}
