package io.tickerstorm.strategy;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.jms.core.JmsTemplate;

import com.google.common.eventbus.EventBus;

import io.tickerstorm.common.EventBusContext;
import io.tickerstorm.common.JmsEventBusContext;
import io.tickerstorm.common.eventbus.Destinations;
import io.tickerstorm.common.eventbus.EventBusToJMSBridge;
import io.tickerstorm.common.eventbus.JMSToEventBusBridge;
import io.tickerstorm.service.HeartBeatGenerator;

@SpringBootApplication(
    scanBasePackages = {"io.tickerstorm.strategy.backtest", "io.tickerstorm.strategy.processor", "io.tickerstorm.strategy.util"})
@Import({EventBusContext.class, JmsEventBusContext.class})
public class StrategyServiceApplication {

  @Value("${service.name:strategy-service}")
  private String SERVICE;

  public static final Logger logger = org.slf4j.LoggerFactory.getLogger(StrategyServiceApplication.class);

  public static void main(String[] args) throws Exception {
    SpringApplication.run(StrategyServiceApplication.class, args);
  }

  @Bean
  public static PropertySourcesPlaceholderConfigurer buildConfig() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  @Bean
  public HeartBeatGenerator generateStrategyServiceHeartBeat() {
    return new HeartBeatGenerator(SERVICE, 5000);
  }

  // SENDERS
  @Qualifier(Destinations.MODEL_DATA_BUS)
  @Bean
  public EventBusToJMSBridge buildModelDataJmsBridge(@Qualifier(Destinations.MODEL_DATA_BUS) EventBus eventbus, JmsTemplate template) {
    return new EventBusToJMSBridge(eventbus, Destinations.QUEUE_MODEL_DATA, template, SERVICE);
  }

  @Qualifier(Destinations.NOTIFICATIONS_BUS)
  @Bean
  public EventBusToJMSBridge buildNotificationsJmsBridge2(@Qualifier(Destinations.NOTIFICATIONS_BUS) EventBus eventbus,
      JmsTemplate template) {
    return new EventBusToJMSBridge(eventbus, Destinations.TOPIC_NOTIFICATIONS, template, SERVICE, 5000);
  }

  // RECEIVERS
  @Bean
  public JMSToEventBusBridge buildStrategyListener(@Qualifier(Destinations.REALTIME_MARKETDATA_BUS) EventBus realtimeBus,
      @Qualifier(Destinations.COMMANDS_BUS) EventBus commandsBus,
      @Qualifier(Destinations.RETRO_MODEL_DATA_BUS) EventBus retroModelDataBus) {
    JMSToEventBusBridge bridge = new JMSToEventBusBridge(SERVICE);
    bridge.realtimeBus = realtimeBus;
    bridge.commandsBus = commandsBus;
    bridge.retroModelDataBus = retroModelDataBus;
    return bridge;
  }

  @Qualifier("processorEventBus")
  @Bean
  public EventBus buildEventProcessorBus() {
    return new EventBus("processorEventBus");
  }


}
