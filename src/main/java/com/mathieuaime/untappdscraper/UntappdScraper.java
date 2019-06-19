package com.mathieuaime.untappdscraper;

import com.mathieuaime.untappdscraper.model.CheckIn;
import com.mathieuaime.untappdscraper.scraper.Scraper;
import java.io.IOException;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public class UntappdScraper {

  public static void main(String[] args) {
    scrapVenues(List.of());
    scrapAccount(List.of());
  }

  private static void scrapAccount(Collection<String> usernames) {
    for (String username : usernames) {
      getCheckIns("https://untappd.com/user/" + username + "/beers", ".beer-item")
          .map(element -> Scraper.extractCheckInFromUser(username, element))
          .forEach(UntappdScraper::storeCheckIn);
    }
  }

  private static void scrapVenues(Collection<String> venues) {
    for (String venue : venues) {
      getCheckIns("https://untappd.com/v/" + venue, ".item")
          .map(element -> Scraper.extractCheckInFromVenue(venue.split("/")[0], element))
          .forEach(UntappdScraper::storeCheckIn);
    }
  }

  private static Stream<Element> getCheckIns(String url, String classItem) {
    try {
      return getConnection(url).get().select(classItem).stream();
    } catch (IOException ignored) {
      return Stream.empty();
    }
  }

  private static Connection getConnection(String url) {
    Connection connection = Jsoup.connect(url);
    connection.userAgent("Mozilla");
    connection.timeout(5000);
    return connection;
  }

  private static void storeCheckIn(CheckIn checkIn) {
    System.out.println("Store checkin " + checkIn);
    try (InfluxDB influxDB = InfluxDBFactory.connect("http://influxdb:8086")) {
      String dbName = "untappd";
      influxDB.setDatabase(dbName);
      influxDB.enableBatch(BatchOptions.DEFAULTS);

      influxDB.write(Point.measurement("checkin")
          .time(checkIn.date().toInstant(ZoneOffset.UTC).toEpochMilli(),
              TimeUnit.MILLISECONDS)
          .addField("username", checkIn.username())
          .addField("brewery", checkIn.beer().brewery())
          .addField("beer", checkIn.beer().name())
          .addField("score", checkIn.beer().myScore())
          .addField("globalScore", checkIn.beer().globalScore())
          .addField("abv", checkIn.beer().abv())
          .addField("ibu", checkIn.beer().ibu())
          .addField("venue", checkIn.venue())
          .build());
    }
  }
}
