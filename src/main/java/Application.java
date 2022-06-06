import model.CSVDownloader;
import model.CSVLoader;
import model.Measurement;

import java.util.ArrayList;
import java.util.List;

public class Application {

    public static void main(String args[]){

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

    }
}
