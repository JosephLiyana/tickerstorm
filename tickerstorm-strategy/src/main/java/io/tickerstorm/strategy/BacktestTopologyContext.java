package io.tickerstorm.strategy;

import javax.jms.ConnectionFactory;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.annotation.EnableJms;

import backtype.storm.contrib.jms.spout.JmsSpout;
import io.tickerstorm.data.eventbus.Destinations;
import io.tickerstorm.strategy.spout.RealtimeDestinationProvider;
import io.tickerstorm.strategy.spout.StormJmsTupleProducer;
import io.tickerstorm.strategy.util.BacktestClock;
import io.tickerstorm.strategy.util.Clock;

@EnableJms
@Configuration
@ComponentScan(basePackages = {"io.tickerstorm.strategy"})
@PropertySource({"classpath:default.properties"})
public class BacktestTopologyContext {

  public static final Logger logger =
      org.slf4j.LoggerFactory.getLogger(BacktestTopologyContext.class);

  @Value("${jms.transport}")
  private String transport;

  @Bean
  public ConnectionFactory buildActiveMQConnectionFactory() throws Exception {
    logger.info("Creating Connection Factory");
    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(transport);
    return connectionFactory;
  }

  @Qualifier("realtime")
  @Bean
  public JmsSpout buildJmsSpout(ConnectionFactory factory) throws Exception {

    JmsSpout spout = new JmsSpout();
    spout.setJmsProvider(new RealtimeDestinationProvider(factory,
        factory.createConnection().createSession(false, Session.CLIENT_ACKNOWLEDGE)
            .createTopic(Destinations.TOPIC_REALTIME_MARKETDATA)));
    spout.setJmsTupleProducer(new StormJmsTupleProducer());
    spout.setJmsAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
    spout.setDistributed(true);
    spout.setRecoveryPeriod(1000);

    return spout;

  }

  @Bean
  public Clock backtestClock() {
    return new BacktestClock();
  }
}
