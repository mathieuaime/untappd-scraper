package com.mathieuaime.untappdscraper;

import com.mathieuaime.untappdscraper.model.CheckIn;
import com.mathieuaime.untappdscraper.scraper.Scraper;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

public class UntappdScraper {

  public static void main(String[] args) {
    scrapAccount(List.of());
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

  private static void storeCheckIn(CheckIn e) {
    System.out.println(e);
  }
}
