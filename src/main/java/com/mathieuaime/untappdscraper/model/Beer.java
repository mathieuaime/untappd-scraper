package com.mathieuaime.untappdscraper.model;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class Beer {

  public abstract String name();

  public abstract String brewery();

  public abstract String style();

  public abstract Float myScore();

  public abstract Float globalScore();

  public abstract Float abv();

  public abstract Float ibu();

  public static Beer create(String name, String brewery, String style, Float myScore) {
    return create(name, brewery, style, myScore, -1F, -1F, -1F);
  }

  public static Beer create(String name, String brewery, String style, Float myScore,
      Float globalScore, Float abv, Float ibu) {
    return new AutoValue_Beer(name, brewery, style, myScore, globalScore, abv, ibu);
  }
}