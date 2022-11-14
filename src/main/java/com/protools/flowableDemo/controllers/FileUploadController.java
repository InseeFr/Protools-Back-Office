package com.protools.flowableDemo.controllers;

import com.protools.flowableDemo.services.utils.UploadFileToEngineService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileUploadController {
    private final UploadFileToEngineService uploadFileToEngineService;

    public FileUploadController(UploadFileToEngineService uploadFileToEngineService) {
        this.uploadFileToEngineService = uploadFileToEngineService;
    }

    @PostMapping("/upload-context")
    public ResponseEntity<Void> uploadFile(
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam("taskID") String taskID
    ) {
        uploadFileToEngineService.storeFile(file, taskID);
        return new ResponseEntity<>(HttpStatus.OK);

    }
}
