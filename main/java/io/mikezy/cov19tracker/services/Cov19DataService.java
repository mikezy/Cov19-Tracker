package io.mikezy.cov19tracker.services;

import io.mikezy.cov19tracker.models.LocationStats;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class Cov19DataService {

    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Confirmed.csv";
    private static String RECOVERED_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Recovered.csv";
    private static String DEATH_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_19-covid-Deaths.csv";

    private List<LocationStats> allStats = new ArrayList<>();
    private int allRecovered = 0;
    private int allDeath = 0;

    public int getAllRecovered() {
        return allRecovered;
    }

    public int getAllDeath() {
        return allDeath;
    }

    public List<LocationStats> getAllStats() {
        return allStats;
    }

    @PostConstruct
    @Scheduled(cron = "0 0/30 * * * ?")
    public void fetchVirusData() throws IOException, InterruptedException {
        List<LocationStats> newStats = new ArrayList<>();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(VIRUS_DATA_URL))
                .build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        //parse the information from JHU git
        StringReader csvBodyReader = new StringReader(httpResponse.body());

        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withSkipHeaderRecord(false).parse(csvBodyReader);
        String curDate = "";
        for (CSVRecord record : records) {
            LocationStats locationStats = new LocationStats();

            if (record.getRecordNumber() == 1) {
                curDate = record.get(record.size() - 1);
            }
            else {
                String country = record.get(1);
                locationStats.setCountry(country);
                locationStats.setState(record.get(0));
                locationStats.setLat(Float.parseFloat(record.get(2)));
                locationStats.setLat(Float.parseFloat(record.get(3)));

                int latestCases = Integer.parseInt(record.get(record.size() - 1));
                int prevDayCases = Integer.parseInt(record.get(record.size() - 2));
                locationStats.setLatestTotalCases(latestCases);
                locationStats.setDiffFromPrevDay(latestCases - prevDayCases);
                locationStats.setCurDate(curDate);

                //filter out US only
                if (country.equals("US")) {
                    newStats.add(locationStats);
                }
            }
        }
        this.allStats = newStats;

        //get recovered and death data
        allRecovered = 0;
        allDeath = 0;
        allRecovered = fetchRecoveredData();
        allDeath = fetchDeathData();
    }

    private int fetchDeathData() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(DEATH_DATA_URL))
                .build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        //parse the information from JHU git
        StringReader csvBodyReader = new StringReader(httpResponse.body());

        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);

        for (CSVRecord record : records) {
            String country = record.get("Country/Region");
            String state = record.get("Province/State");
            int recovered = Integer.parseInt(record.get(record.size() - 1));

            if (country.equals("US")) {
                allDeath += Integer.parseInt(record.get(record.size() - 1));
            }
        }

        return allDeath;

    }

    private int fetchRecoveredData() throws IOException, InterruptedException{

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(RECOVERED_DATA_URL))
                .build();
        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        //parse the information from JHU git
        StringReader csvBodyReader = new StringReader(httpResponse.body());

        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);

        for (CSVRecord record : records) {
            String country = record.get(1);
            if (country.equals("US")) {
                allRecovered += Integer.parseInt(record.get(record.size() - 1));
            }
        }
        return allRecovered;
    }
}
