package io.tickerstorm.data.query;

public interface DataQuery {

  public String namespace();

  public String build();

  public String provider();

  public String getSymbol();

  public String getInterval();

}
