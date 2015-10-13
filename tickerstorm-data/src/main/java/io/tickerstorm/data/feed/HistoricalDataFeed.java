package io.tickerstorm.data.feed;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Repository;

import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;

import io.tickerstorm.data.dao.MarketDataDto;
import io.tickerstorm.entity.Candle;
import io.tickerstorm.entity.Markers;
import io.tickerstorm.entity.MarketData;
import io.tickerstorm.entity.MarketDataMarker;
import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

@Repository
public class HistoricalDataFeed {

  private static final java.time.format.DateTimeFormatter dateFormat =
      java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd");

  private static final Logger logger = LoggerFactory.getLogger(HistoricalDataFeed.class);

  @Qualifier("realtime")
  @Autowired
  private MBassador<MarketData> realtimeBus;

  @Qualifier("query")
  @Autowired
  private MBassador<HistoricalFeedQuery> queryBus;

  @Autowired
  private CassandraOperations cassandra;

  @Value("${cassandra.keyspace}")
  private String keyspace;

  @PostConstruct
  public void setup() {
    queryBus.subscribe(this);
  }

  @PreDestroy
  public void destroy() {
    queryBus.unsubscribe(this);
  }

  @Handler
  public void onQuery(HistoricalFeedQuery query) {

    logger.debug("Historical feed query received");
    LocalDateTime start = query.from;
    LocalDateTime end = query.until;
    LocalDateTime date = start;

    Set<String> dates = new java.util.HashSet<>();
    dates.add(dateFormat.format(date));

    for (String s : query.symbols) {

      while (!date.equals(end)) {

        if (date.isBefore(end))
          date = date.plusDays(1);

        dates.add(dateFormat.format(date));
      }

      Select select = QueryBuilder.select().from("marketdata");
      select.where(QueryBuilder.eq("symbol", s.toLowerCase()))
          .and(QueryBuilder.in("date", dates.toArray(new String[] {})))
          .and(QueryBuilder.eq("type", Candle.TYPE.toLowerCase()))
          .and(QueryBuilder.eq("source", query.source.toLowerCase()))
          .and(QueryBuilder.eq("interval", query.periods.iterator().next()));

      logger.debug("Cassandra query: " + select.toString());
      long startTimer = System.currentTimeMillis();
      List<MarketDataDto> dtos = cassandra.select(select, MarketDataDto.class);
      logger.info("Query took " + (System.currentTimeMillis() - startTimer) + "ms to fetch "
          + dtos.size() + " results.");

      startTimer = System.currentTimeMillis();
      MarketData first = null;
      MarketData last = null;
      int count = 0;
      for (MarketDataDto dto : dtos) {

        try {
          MarketData m = dto.toMarketData();

          if (null == first) {
            first = m;
            MarketDataMarker marker =
                new MarketDataMarker(m.getSymbol(), m.getSource(), m.getTimestamp(), query.id);
            marker.addMarker(Markers.QUERY_START.toString());
            marker.expect = dtos.size();
            realtimeBus.publish(marker);
          }

          realtimeBus.publish(m);
          count++;

          if (count == dtos.size() && null == last) {
            last = m;
            MarketDataMarker marker =
                new MarketDataMarker(m.getSymbol(), m.getSource(), m.getTimestamp(), query.id);
            marker.addMarker(Markers.QUERY_END.toString());
            marker.expect = 0;
            realtimeBus.publish(marker);
          }
        } catch (Exception e) {
          logger.error(e.getMessage(), e);
          // continue
        }
      }

      logger.info(
          "Dispatch historical data feed took " + (System.currentTimeMillis() - startTimer) + "ms");

    }
  }
}
