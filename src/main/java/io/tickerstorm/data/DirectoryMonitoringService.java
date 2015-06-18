package io.tickerstorm.data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DirectoryMonitoringService {

  @Autowired
  private List<BaseFileConverter> listeners = new ArrayList<BaseFileConverter>();

  @PostConstruct
  public void init() throws Exception {

    final File directory = new File("./data");
    FileAlterationObserver fao = new FileAlterationObserver(directory);

    for (BaseFileConverter l : listeners) {
      fao.addListener(l);

      if (!StringUtils.isEmpty(l.provider()))
        FileUtils.forceMkdir(new File("./data/" + l.provider()));
    }

    final FileAlterationMonitor monitor = new FileAlterationMonitor(2000);
    monitor.addObserver(fao);

    System.out.println("Starting monitor. CTRL+C to stop.");
    monitor.start();

    Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

      @Override
      public void run() {
        try {
          System.out.println("Stopping monitor.");
          monitor.stop();
        } catch (Exception ignored) {
        }
      }
    }));

  }

}
