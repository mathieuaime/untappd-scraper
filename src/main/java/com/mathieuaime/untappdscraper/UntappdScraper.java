package com.mathieuaime.untappdscraper;

import com.mathieuaime.untappdscraper.model.CheckIn;
import com.mathieuaime.untappdscraper.scraper.Scraper;
import java.io.IOException;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collection;
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
    scrapAccount(Arrays.asList());
  }

  private static void scrapAccount(Collection<String> usernames) {
    for (String username : usernames) {
      getItems(username).map(element -> Scraper.extractCheckIn(username, element))
          .forEach(UntappdScraper::storeCheckIn);
    }
  }

  private static Stream<Element> getItems(String username) {
    try {
      return getConnection(username).get().select(".beer-item").stream();
    } catch (IOException ignored) {
      return Stream.empty();
    }
  }

  private static Connection getConnection(String username) {
    Connection connection = Jsoup.connect("https://untappd.com/user/" + username + "/beers");
    connection.userAgent("Mozilla");
    connection.timeout(5000);
    return connection;
  }

  private static void storeCheckIn(CheckIn checkIn) {
    System.out.println("Store checkin " + checkIn);
    try (InfluxDB influxDB = InfluxDBFactory.connect(" http://influxdb:8086")) {
      String dbName = "untappd";
      influxDB.setDatabase(dbName);
      influxDB.enableBatch(BatchOptions.DEFAULTS);

      influxDB.write(Point.measurement("checkin")
          .time(checkIn.date().toInstant(ZoneOffset.ofHours(2)).toEpochMilli(),
              TimeUnit.MILLISECONDS)
          .addField("username", checkIn.username())
          .addField("brewery", checkIn.beer().brewery())
          .addField("beer", checkIn.beer().name())
          .addField("score", checkIn.beer().myScore())
          .addField("globalScore", checkIn.beer().globalScore())
          .addField("abv", checkIn.beer().abv())
          .addField("ibu", checkIn.beer().ibu())
          .build());
    }
  }
}
