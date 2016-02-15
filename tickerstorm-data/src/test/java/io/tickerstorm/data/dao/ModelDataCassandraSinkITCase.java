package io.tickerstorm.data.dao;

import static org.testng.Assert.assertEquals;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import io.tickerstorm.common.entity.BaseField;
import io.tickerstorm.common.entity.BaseMarker;
import io.tickerstorm.common.entity.Candle;
import io.tickerstorm.common.entity.Field;
import io.tickerstorm.common.entity.Markers;
import io.tickerstorm.common.entity.MarketData;
import io.tickerstorm.data.TestMarketDataServiceConfig;
import net.engio.mbassy.bus.MBassador;

@DirtiesContext
@ContextConfiguration(classes = {TestMarketDataServiceConfig.class})
public class ModelDataCassandraSinkITCase extends AbstractTestNGSpringContextTests {

  @Autowired
  private CassandraOperations session;

  @Qualifier("modelData")
  @Autowired
  private MBassador<Map<String, Object>> modelDataBus;

  @Autowired
  private ModelDataDao dao;

  @AfterMethod
  public void cleanup() throws Exception {
    session.getSession().execute("TRUNCATE modeldata");
    Thread.sleep(2000);
  }

  @Test
  public void testStoreModelData() throws Exception {

    Map<String, Object> tuple = new HashMap<String, Object>();
    Candle c = new Candle("goog", "google", Instant.now(), BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ONE, BigDecimal.ONE, "1m", 1000);
    c.setStream("Test model");
    BaseField<Integer> df = new BaseField<Integer>(c.getEventId(), "ave", 100);
    BaseField<BigDecimal> cf = new BaseField<BigDecimal>(c.getEventId(), "sma", BigDecimal.TEN);
    BaseField<String> cat = new BaseField<String>(c.getEventId(), "some field name", "category");

    tuple.put(Field.Name.MARKETDATA.field(), c);
    tuple.put(Field.Name.AVE.field(), Lists.newArrayList(df));
    tuple.put(Field.Name.SMA.field(), Lists.newArrayList(cf));
    modelDataBus.publish(tuple);

    Thread.sleep(5000);

    long count = dao.count();

    assertEquals(count, 1);

    Iterable<ModelDataDto> result = dao.findAll();

    ModelDataDto dto = result.iterator().next();

    Assert.assertNotNull(dto.primarykey);
    Assert.assertNotNull(dto.primarykey.date);
    Assert.assertNotNull(dto.primarykey.timestamp);
    Assert.assertNotNull(dto.primarykey.stream);
    Assert.assertNotNull(dto.fields);
    Assert.assertEquals(dto.fields.size(), 12);

    Map<String, Object> map = dto.fromRow();
    MarketData data = (MarketData) map.get(Field.Name.MARKETDATA.field());

    Assert.assertNotNull(data);
    Assert.assertNotNull(data.getStream());
    Assert.assertNotNull(map.get(Field.Name.AVE.field()));
    Assert.assertNotNull(map.get(Field.Name.SMA.field()));
    Assert.assertEquals(c, data);
    Assert.assertEquals(((Collection) map.get(Field.Name.SMA.field())).iterator().next(), cf);
    Assert.assertEquals(((Collection) map.get(Field.Name.AVE.field())).iterator().next(), df);

  }

  @Test
  public void testStoreMarker() throws Exception {

    Map<String, Object> tuple = new HashMap<String, Object>();

    BaseMarker marker = new BaseMarker(UUID.randomUUID().toString(), "Default");
    marker.addMarker(Markers.QUERY_START.toString());
    marker.expect = 100;
    tuple.put(Field.Name.MARKETDATA.field(), marker);
    tuple.put(Field.Name.STREAM.field(), "test model");

    modelDataBus.publish(tuple);

    Thread.sleep(5000);

    long count = dao.count();

    assertEquals(count, 0);
  }
}
