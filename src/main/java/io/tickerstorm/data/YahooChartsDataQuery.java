package io.tickerstorm.data;

import io.tickerstorm.entity.Candle;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YahooChartsDataQuery implements QueryBuilder, DataConverter {

  public static final Logger logger = LoggerFactory.getLogger(YahooChartsDataQuery.class);

  public static final String HOST = "http://chartapi.finance.yahoo.com/instrument/1.0/";
  private String symbol;
  private String range = "9d";

  private String exchange;
  private TimeZone timezone;

  @Override
  public DataConverter converter() {
    return this;
  }

  public YahooChartsDataQuery(String symbol) {
    this.symbol = symbol;

  }

  public YahooChartsDataQuery withRange(String range) {
    this.range = range;
    return this;
  }

  public YahooChartsDataQuery withSymbol(String symbol) {
    this.symbol = symbol;
    return this;
  }

  public String build() {
    String url = HOST + symbol + "/chartdata;type=quote;range=" + range + "/csv/";
    logger.info(url);
    return url;
  }

  public Candle[] convert(String line) {

    if (line.contains("Exchange-Name:")) {
      this.exchange = line.split(":")[1];
      return null;
    }

    if (line.contains("timezone:")) {
      this.timezone = TimeZone.getTimeZone(line.split(":")[1]);
      return null;
    }

    if (line.contains(":")) {
      return null;
    }

    String[] args = line.split(",");

    Candle c = new Candle();
    c.timestamp = new DateTime(Integer.valueOf(args[0]) * 1000).withZone(DateTimeZone.forTimeZone(timezone));
    c.low = new BigDecimal(args[3]);
    c.high = new BigDecimal(args[2]);
    c.close = new BigDecimal(args[1]);
    c.open = new BigDecimal(args[4]);
    c.volume = new BigDecimal(args[5]);
    c.symbol = symbol;
    c.interval = Candle.MIN_5_INTERVAL;
    c.source = "yahoo";
    c.exchange = exchange;

    return new Candle[] { c };
  }

  public String type() {
    return Candle.TYPE;
  }

  public String symbol() {
    return symbol;
  }

  @Override
  public String provider() {
    return "Yahoo";
  }

}
