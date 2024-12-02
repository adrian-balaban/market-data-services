package com.fx.market.flinkorchestrator.vendor.flink;

import com.fx.flink.model.JobIdsWithStatusOverview;
import com.fx.flink.model.JobStatus;
import com.fx.flink.model.JobStatusInfo;
import com.fx.market.flinkorchestrator.vendor.flink.client.FlinkClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FlinkJobService {

    @Autowired
    private FlinkClient flinkClient;

    public JobStatusInfo getJobStatus(String jobId) {
        return flinkClient.getJobStatusInfo(jobId);
    }

    public void cancelAllJobs() {
        log.info("Canceling all jobs");
        getJobIdsWithStatusesOverview().getJobs()
                .forEach(
                        jobIdWithStatus -> {
                            if(jobIdWithStatus.getStatus().equals(JobStatus.RUNNING)){
                                cancelJob(jobIdWithStatus.getId());
                            }
                        }
                );
    }

    public void cancelJob(String jobId) {
        log.info("Canceling job:{}", jobId);
        flinkClient.cancelJob(jobId);
    }

    public JobIdsWithStatusOverview getJobIdsWithStatusesOverview() {
        return flinkClient.getJobIdsWithStatusesOverview();
    }

}
