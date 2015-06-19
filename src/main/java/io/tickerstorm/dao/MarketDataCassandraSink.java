package io.tickerstorm.dao;

import io.tickerstorm.entity.MarketData;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cassandra.core.Ordering;
import org.springframework.cassandra.core.keyspace.CreateKeyspaceSpecification;
import org.springframework.cassandra.core.keyspace.CreateTableSpecification;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Repository;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.ResultSet;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

@Repository
public class MarketDataCassandraSink {

  @Value("${cassandra.keyspace}")
  private String keyspace;
  
  @PostConstruct
  public void init() {
    
    CreateKeyspaceSpecification spec = CreateKeyspaceSpecification.createKeyspace(keyspace).ifNotExists().withSimpleReplication(3);
    ResultSet set = session.execute(spec);
    
    session.execute("USE " + keyspace);
    
    CreateTableSpecification tableSpec = CreateTableSpecification.
        createTable("marketdata").
        partitionKeyColumn("symbol", DataType.text()).
        partitionKeyColumn("date", DataType.cint()).
        clusteredKeyColumn("type", DataType.text(), Ordering.ASCENDING).
        clusteredKeyColumn("source", DataType.text(), Ordering.ASCENDING).
        clusteredKeyColumn("interval", DataType.text(), Ordering.ASCENDING).
        clusteredKeyColumn("timestamp", DataType.timestamp(), Ordering.DESCENDING).        
        column("ask", DataType.decimal()).
        column("askSize", DataType.decimal()).
        column("bid", DataType.decimal()).
        column("bidSize", DataType.decimal()).
        column("close", DataType.decimal()).
        column("high", DataType.decimal()).
        column("low", DataType.decimal()).
        column("open", DataType.decimal()).
        column("price", DataType.decimal()).
        column("properties", DataType.map(DataType.text(), DataType.text())).
        column("volume", DataType.decimal()).
        column("quantity", DataType.decimal()).
        ifNotExists();
    
    set = session.execute(tableSpec);   
    bus.register(this);
  }

  @PreDestroy
  public void destroy() {
    bus.unregister(this);
  }

  @Autowired
  private MarketDataDao dao;
  
  @Autowired
  private CassandraOperations session;

  @Qualifier("historical")
  @Autowired
  private EventBus bus;

  @Subscribe
  public void onMarketData(MarketData data) {

    MarketDataDto dto = MarketDataDto.convert(data);
    dao.save(dto);

  }

}
