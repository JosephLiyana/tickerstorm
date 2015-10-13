package io.tickerstorm.data.query;

import java.util.HashSet;
import java.util.Set;

import io.tickerstorm.entity.Candle;


public class StooqHistoricalForexQuery implements QueryBuilder, DataQuery {

  public String getInterval() {
    return Candle.MIN_5_INTERVAL;
  }

  @Override
  public String namespace() {
    return HOST;
  }

  @Override
  public String getSymbol() {
    throw new IllegalAccessError("Not supported");
  }

  private static final String HOST = "http://s.stooq.pl/db/h/";

  private String fileName = "_world_txt.zip";
  private Set<String> securityTypes = new HashSet<String>();
  private String interval = "5";

  public StooqHistoricalForexQuery currencies() {
    this.fileName = "_world_txt.zip";
    this.securityTypes.add("currencies");
    return this;
  }

  public StooqHistoricalForexQuery bonds() {
    this.securityTypes.add("bonds");
    return this;
  }

  public StooqHistoricalForexQuery commodities() {
    this.securityTypes.add("commodities");
    return this;
  }

  public StooqHistoricalForexQuery indicies() {
    this.securityTypes.add("indicies");
    return this;
  }

  public StooqHistoricalForexQuery etfs() {
    this.securityTypes.add("nasdaq etfs");
    this.securityTypes.add("nyse etfs");
    this.securityTypes.add("nysqmkt etfs");
    return this;
  }

  public StooqHistoricalForexQuery stocks() {
    this.securityTypes.add("nasdaq stocks");
    this.securityTypes.add("nyse stocks");
    this.securityTypes.add("nysqmkt stocks");
    return this;
  }

  public StooqHistoricalForexQuery forUS() {
    this.fileName = "_us_txt.zip";
    return this;
  }

  public StooqHistoricalForexQuery forWorld() {
    this.fileName = "_world_txt.zip";
    return this;
  }

  public StooqHistoricalForexQuery min5() {
    this.interval = "5";
    return this;
  }

  public StooqHistoricalForexQuery hourly() {
    this.interval = "h";
    return this;
  }

  public StooqHistoricalForexQuery daily() {
    this.interval = "d";
    return this;
  }

  @Override
  public String build() {
    return HOST + interval + fileName;
  }

  @Override
  public String provider() {
    return "Stooq";
  }

}
