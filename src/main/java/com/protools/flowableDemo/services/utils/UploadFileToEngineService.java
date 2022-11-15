package com.protools.flowableDemo.services.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;

import com.protools.flowableDemo.services.engineService.WorkflowService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class UploadFileToEngineService {
	private Logger logger = LogManager.getLogger(UploadFileToEngineService.class);

	private final Path fileStorageLocation;

	@Autowired
	private WorkflowService workflowService;

	@Autowired
	public UploadFileToEngineService(@Value("${app.file.upload-dir:./uploads/files}") String path) {
		this.fileStorageLocation = Paths.get(path).toAbsolutePath().normalize();

		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			throw new RuntimeException("Could not create the directory where files will be stored.", ex);
		}
	}

	private String getFileExtension(String fileName) {
		if (fileName == null) {
			return null;
		}
		String[] fileNameParts = fileName.split("\\.");

		return fileNameParts[fileNameParts.length - 1];
	}

	public String storeFile(MultipartFile file, String taskID) {
		// Normalize file name
		String fileName = new Date().getTime() + "-file." + getFileExtension(file.getOriginalFilename());

		try {
			// Check if the filename contains invalid characters
			if (fileName.contains("..")) {
				throw new RuntimeException("File name contains invalid path sequence " + fileName);
			}
			// Récupérer le contenu du fichier
			String content = new String(file.getBytes(), StandardCharsets.UTF_8).replaceAll("[\\n\\r\\t]+", "");

			logger.info("\t >> Context File content : " + content);
			var values = new HashMap<String, Object>();
			values.put("contextRawFile", content);
			// Complete task & upload file content into the engine

			workflowService.completeTask(taskID, values, "user");
			logger.info("\t >> Context File uploaded to process engine");
			return fileName;
		} catch (IOException ex) {
			throw new RuntimeException("Could not store file " + fileName, ex);
		}
	}
}
