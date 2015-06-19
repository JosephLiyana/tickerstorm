package io.tickerstorm.entity;

import java.math.BigDecimal;

import com.google.common.base.Objects;

@SuppressWarnings("serial")
public class Candle extends BaseMarketData {

  public static final String TYPE = "candle";
  public static final String MIN_5_INTERVAL = "5m";
  public static final String MIN_1_INTERVAL = "1m";
  public static final String MIN_10_INTERVAL = "10m";
  public static final String WEEK_INTERVAL = "7d";
  public static final String HOURLY_INTERVAL = "1h";
  public static final String MONTH_INTERVAL = "1mon";
  public static final String EOD = "EOD";

  public BigDecimal low;

  public BigDecimal high;

  public BigDecimal open;

  public BigDecimal close;

  public BigDecimal volume;

  public String interval;

  public BigDecimal getClose() {
    return close;
  }

  public BigDecimal getHigh() {
    return high;
  }

  public String getInterval() {
    return interval;
  }

  public BigDecimal getLow() {
    return low;
  }

  public BigDecimal getOpen() {
    return open;
  }

  @Override
  public String getType() {
    return TYPE;
  }

  public void setType(String type) {
    // nothing
  }

  public BigDecimal getVolume() {
    return volume;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((close == null) ? 0 : close.hashCode());
    result = prime * result + ((high == null) ? 0 : high.hashCode());
    result = prime * result + ((interval == null) ? 0 : interval.hashCode());
    result = prime * result + ((low == null) ? 0 : low.hashCode());
    result = prime * result + ((open == null) ? 0 : open.hashCode());
    result = prime * result + ((volume == null) ? 0 : volume.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    Candle other = (Candle) obj;
    if (close == null) {
      if (other.close != null)
        return false;
    } else if (!close.equals(other.close))
      return false;
    if (high == null) {
      if (other.high != null)
        return false;
    } else if (!high.equals(other.high))
      return false;
    if (interval == null) {
      if (other.interval != null)
        return false;
    } else if (!interval.equals(other.interval))
      return false;
    if (low == null) {
      if (other.low != null)
        return false;
    } else if (!low.equals(other.low))
      return false;
    if (open == null) {
      if (other.open != null)
        return false;
    } else if (!open.equals(other.open))
      return false;
    if (volume == null) {
      if (other.volume != null)
        return false;
    } else if (!volume.equals(other.volume))
      return false;
    return true;
  }

  public void setClose(BigDecimal close) {
    this.close = close;
  }

  public void setHigh(BigDecimal high) {
    this.high = high;
  }

  public void setInterval(String interval) {
    this.interval = interval;
  }

  public void setLow(BigDecimal low) {
    this.low = low;
  }

  public void setOpen(BigDecimal open) {
    this.open = open;
  }

  public void setVolume(BigDecimal volume) {
    this.volume = volume;
  }
}
