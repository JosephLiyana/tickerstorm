package io.tickerstorm.strategy.backtest;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Service;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import io.tickerstorm.strategy.BacktestTopologyContext;

@Service
public class BacktestTopology {

  private Config stormConfig = new Config();

  @Autowired
  private ForwardFlowTopologyFactory forwardTopology;

  @Autowired
  private RetroFlowTopologyFactory retroTopology;

  private LocalCluster cluster;
  private final String FORWARD = "forward-flow-topology";
  private final String RETRO = "retro-flow-topology";

  public static void main(String[] args) throws Exception {
    SpringApplication.run(BacktestTopologyContext.class, args);
  }

  @PostConstruct
  public void init() throws Exception {

    stormConfig.setDebug(false);
    stormConfig.setNumWorkers(1);

    cluster = new LocalCluster();
    cluster.submitTopology(FORWARD, stormConfig, forwardTopology.withAveBolt().build());
    cluster.submitTopology(RETRO, stormConfig, retroTopology.build());

  }

  @PreDestroy
  private void destroy() {

    if (cluster != null) {
      cluster.killTopology(FORWARD);
      cluster.killTopology(RETRO);
    }

  }

}
