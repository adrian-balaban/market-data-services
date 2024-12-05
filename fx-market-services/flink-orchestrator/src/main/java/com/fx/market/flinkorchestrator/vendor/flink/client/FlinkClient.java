package com.fx.market.flinkorchestrator.vendor.flink.client;

import com.fx.flink.model.*;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(name = "flinkClient", url="${flink.url}")
public interface  FlinkClient {

    @GetMapping("/jars")
    JarListInfo getJars();

    @PostMapping(value = "/jars/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Headers("Content-Type: multipart/form-data")
    JarUploadResponseBody uploadJar(
            @RequestPart(value = "jarfile") MultipartFile file);

    @DeleteMapping(value = "/jars/{jarId}")
    void deleteJar(@PathVariable("jarId") String jarId);

    @PostMapping(value = "/jars/{jarId}/run")
    JarRunResponseBody submitJobFromJar(
            @PathVariable("jarId") String jarId,
           JarRunRequestBody jarRunRequestBody
    );

    @GetMapping("/jobs")
    JobIdsWithStatusOverview getJobIdsWithStatusesOverview();

    @PatchMapping("/jobs/{jobId}")
    JobStatusInfo cancelJob(
            @PathVariable("jobId") String jobId
    );

    @GetMapping("/jobs/{jobId}/status")
    JobStatusInfo getJobStatusInfo(
            @PathVariable("jobId") String jobId
    );

}
