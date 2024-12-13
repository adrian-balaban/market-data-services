package com.fx.market.flinkorchestrator.service;

import com.fx.flink.model.JarRunResponseBody;
import com.fx.market.flinkorchestrator.helpers.JarLoader;
import com.fx.market.flinkorchestrator.vendor.flink.FlinkJarService;
import com.fx.market.flinkorchestrator.vendor.flink.FlinkJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.fx.utils.SafeMapper.nullSafe;

@Slf4j
@Service
public class FlinkOrchestratorService {

    @Autowired
    private JarLoader jarLoader;

    @Autowired
    private FlinkJarService flinkJarService;

    @Autowired
    private FlinkJobService flinkJobService;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        List<String> jobIds = new ArrayList<>();

        log.info("FlinkOrchestratorService - ApplicationReadyEvent Start");
        List<File> flinkJars = jarLoader.loadFlinkProcessorWithResourceLoader();

        flinkJarService.removeAllExistingJars();
        flinkJobService.cancelAllJobs();

        flinkJars.forEach(
                jarFile -> {
                    flinkJarService.uploadNewJar(jarFile);
                    JarRunResponseBody jarRunResponseBody = flinkJarService.runNewJarByName(jarFile.getName());
                    jobIds.add(jarRunResponseBody.getJobid());
                }
        );

        log.info("FlinkOrchestratorService - ApplicationReadyEvent End of Setup. Monitoring jobs...");

        while (true) {
            try {
                jobIds.forEach(
                        jobId ->
                                log.info(
                                        "JobId: {}, status: {}", jobId,
                                        nullSafe(()-> flinkJobService.getJobStatus(jobId).getStatus().getValue()))
                );
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
