package com.fx.market.flinkorchestrator.vendor.flink;

import com.fx.flink.model.JarListInfo;
import com.fx.flink.model.JarRunRequestBody;
import com.fx.flink.model.JarRunResponseBody;
import com.fx.flink.model.JarUploadResponseBody;
import com.fx.market.flinkorchestrator.helpers.JavaFileToMultipartFile;
import com.fx.market.flinkorchestrator.vendor.flink.client.FlinkClient;
import com.fx.market.flinkorchestrator.vendor.flink.client.FlinkProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static com.fx.utils.SafeMapper.nullSafe;
import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
@Service
public class FlinkJarService {

    @Autowired
    private FlinkClient flinkClient;

    @Autowired
    private FlinkProperties flinkProperties;

    public void uploadNewJar(File flinkJar) {
        log.info("Uploading started: {}",
                flinkJar.getName());
        JarUploadResponseBody jarUploadResponseBody = flinkClient.uploadJar(new JavaFileToMultipartFile(flinkJar));
        log.info("Uploaded finished: {} with status: {}",
                jarUploadResponseBody.getFilename(),
                jarUploadResponseBody.getStatus());

    }

    public JarRunResponseBody runNewJarByName(String jarName) {
        AtomicBoolean isFound = new AtomicBoolean(false);
        AtomicReference<JarRunResponseBody> jarRunResponseBody = new AtomicReference<>();
        JarListInfo responseAfter = flinkClient.getJars();
        responseAfter.getFiles().forEach(
                jarFileInfo -> {
                    if (jarFileInfo.getName().contains(jarName)) {
                        isFound.set(true);
                        jarRunResponseBody.set(runNewJarById(jarFileInfo.getId()));
                    }
                }
        );

        if (isFound.get()) {
            log.info("Submitted jar with jobId: {}", jarRunResponseBody.get().getJobid());
            return jarRunResponseBody.get();
        } else {
            throw new NoSuchElementException("Jar with given name not found");
        }
    }

    public JarRunResponseBody runNewJarById(String jarId) {
        JarRunRequestBody jarRunRequestBody = new JarRunRequestBody();
        flinkProperties.getJobArguments(jarId).forEach( (key, value) -> {
                    jarRunRequestBody.addProgramArgsListItem("--" + key);
                    jarRunRequestBody.addProgramArgsListItem(value);
                }
        );
        var response = flinkClient.submitJobFromJar(
                jarId,
                jarRunRequestBody
        );
        log.info("Submitted jobId {} based on jarId: {}.", response.getJobid(), jarId);
        return response;
    }

    public void removeAllExistingJars() {
        JarListInfo responseBefore = flinkClient.getJars();
        if (isEmpty(responseBefore.getFiles())) {
            log.info("No Jars to remove");
            return;
        }
        log.info("Found: {} jars. Removing...", nullSafe(() -> responseBefore.getFiles().size()));
        responseBefore.getFiles().forEach(
                jarFileInfo -> {
                    log.info("Removing jarId: {}", jarFileInfo.getId());
                    flinkClient.deleteJar(jarFileInfo.getId());
                    log.info("Removing jarId: {} - removed", jarFileInfo.getId());
                });
    }

}
