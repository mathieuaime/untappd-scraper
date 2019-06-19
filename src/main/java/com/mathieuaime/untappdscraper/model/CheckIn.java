package com.mathieuaime.untappdscraper.model;

import com.google.auto.value.AutoValue;
import java.time.LocalDateTime;

@AutoValue
public abstract class CheckIn {

  public abstract String username();

  public abstract Beer beer();

  public abstract LocalDateTime date();

  public abstract String venue();

  public static CheckIn create(String username, Beer beer, LocalDateTime date, String venue) {
    return new AutoValue_CheckIn(username, beer, date, venue);
  }
}