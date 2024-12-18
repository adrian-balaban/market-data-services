package com.fx.market.flinkorchestrator.service;

import com.fx.market.flinkorchestrator.vendor.flink.FlinkJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.fx.utils.SafeMapper.nullSafe;

@Slf4j
@Service
public class FlinkJobMonitor {

    @Autowired
    private FlinkJobService flinkJobService;

    private static final List<String> JOB_IDS = new ArrayList<>();

    public void addJobId(String jobId) {
        JOB_IDS.add(jobId);
    }

    @Scheduled(fixedRate=1000)
    public void monitorFlinkJobs() {
        JOB_IDS.forEach(
                jobId ->
                        log.info(
                                "JobId: {}, status: {}", jobId,
                                nullSafe(()-> flinkJobService.getJobStatus(jobId).getStatus().getValue()))
        );
    }
}
