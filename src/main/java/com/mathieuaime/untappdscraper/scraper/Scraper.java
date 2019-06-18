package com.mathieuaime.untappdscraper.scraper;

import com.mathieuaime.untappdscraper.model.Beer;
import com.mathieuaime.untappdscraper.model.CheckIn;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public interface Scraper {

  static CheckIn extractCheckIn(String username, Element elt) {
    Beer beer = extractBeer(elt);
    DateTimeFormatter formatter = DateTimeFormatter
        .ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
    String dateText = elt.select(".details").select(".date").get(1).text().replace("Recent: ", "");
    LocalDateTime date = LocalDateTime.parse(dateText, formatter);

    return CheckIn.create(username, beer, date);
  }

  private static Beer extractBeer(Element elt) {
    Elements details = elt.select(".details");
    Elements beerDetails = elt.select(".beer-details");

    String name = beerDetails.select(".name").text();
    String brewery = beerDetails.select(".brewery").text();
    String style = beerDetails.select(".style").text();

    Elements you = beerDetails.select(".ratings").select(".you");

    Float myScore = getScore(you, "Their");

    Float globalScore = getScore(you, "Global");

    String abvText = details.select(".abv").text().trim().replace("% ABV", "");
    Float abv = abvText.equals("No ABV") ? 0 : Float.parseFloat(abvText);

    String ibuText = details.select(".ibu").text().trim().replace(" IBU", "");
    Float ibu = ibuText.equals("No") ? 0 : Float.parseFloat(ibuText);

    return Beer.create(name, brewery, style, myScore, globalScore, abv, ibu);
  }

  private static Float getScore(Elements you, String score) {
    return you.stream().map(Element::text).filter(e -> e.contains(score))
        .findFirst()
        .map(e -> e.replace(score + " Rating (", "").replace(")", ""))
        .map(Float::parseFloat)
        .orElse(0F);
  }
}
