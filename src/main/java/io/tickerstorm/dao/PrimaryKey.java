package io.tickerstorm.dao;

import java.io.Serializable;
import java.util.Date;

import org.springframework.cassandra.core.Ordering;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
@SuppressWarnings("serial")
public class PrimaryKey implements Serializable {

  @PrimaryKeyColumn(name = "symbol", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
  public String symbol;

  @PrimaryKeyColumn(name = "date", ordinal = 1, type = PrimaryKeyType.PARTITIONED, ordering = Ordering.DESCENDING)
  public int date;

  @PrimaryKeyColumn(name = "type", ordinal = 2, type = PrimaryKeyType.CLUSTERED)
  public String type;

  @PrimaryKeyColumn(name = "source", ordinal = 3, type = PrimaryKeyType.CLUSTERED)
  public String source;

  @PrimaryKeyColumn(name = "interval", ordinal = 4, type = PrimaryKeyType.CLUSTERED)
  public String interval;

  @PrimaryKeyColumn(name = "timestamp", ordinal = 5, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
  public Date timestamp;

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PrimaryKey other = (PrimaryKey) obj;
    if (date != other.date)
      return false;
    if (interval == null) {
      if (other.interval != null)
        return false;
    } else if (!interval.equals(other.interval))
      return false;
    if (source == null) {
      if (other.source != null)
        return false;
    } else if (!source.equals(other.source))
      return false;
    if (symbol == null) {
      if (other.symbol != null)
        return false;
    } else if (!symbol.equals(other.symbol))
      return false;
    if (timestamp == null) {
      if (other.timestamp != null)
        return false;
    } else if (!timestamp.equals(other.timestamp))
      return false;
    if (type == null) {
      if (other.type != null)
        return false;
    } else if (!type.equals(other.type))
      return false;
    return true;
  }

  public String getInterval() {
    return interval;
  }

  public Date getTimestamp() {
    return timestamp;
  }

  public int getDate() {
    return date;
  }

  public void setDate(int date) {
    this.date = date;
  }

  public void setTimestamp(Date timestamp) {
    this.timestamp = timestamp;
  }

  public String getSource() {
    return source;
  }

  public String getSymbol() {
    return symbol;
  }

  public String getType() {
    return type;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + date;
    result = prime * result + ((interval == null) ? 0 : interval.hashCode());
    result = prime * result + ((source == null) ? 0 : source.hashCode());
    result = prime * result + ((symbol == null) ? 0 : symbol.hashCode());
    result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  public void setInterval(String interval) {
    this.interval = interval;
  }

  public void setSource(String source) {
    this.source = source;
  }

  public void setSymbol(String symbol) {
    this.symbol = symbol;
  }

  public void setType(String type) {
    this.type = type;
  }
}