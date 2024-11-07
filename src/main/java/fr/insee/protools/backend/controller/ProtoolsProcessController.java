package fr.insee.protools.backend.controller;

import fr.insee.protools.backend.service.context.IContextService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/protools-process")
@Controller
@RequiredArgsConstructor
public class ProtoolsProcessController {

    private final IContextService contextService;

    @PostMapping(value = "/upload-context", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload of the context file",
            description = "Upload the context of the process containing all the metadata used during the process")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "The received file is not a valid JSON"),
            @ApiResponse(responseCode = "404", description = "The taskId does not exist"),
            @ApiResponse(responseCode = "415", description = "The received file is not an JSON file"),
            @ApiResponse(responseCode = "422", description = "The received file is a valid JSON bad has incorrect content")
    })
    public ResponseEntity<Void> uploadFile(
            @Parameter(name = "file", description = "JSON file with the context", required = true)
            @RequestPart(name = "file") MultipartFile file,
            @RequestParam("taskID") String taskID
    ) {
        contextService.processContextFileAndCompleteTask(file, taskID);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    @PostMapping(value = "/create_process_instance_with_context", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Start a process instance providing and provide de protools context",
            description = "Start a process instance with provided *processDefinitionId* and *businessKey* "
                    + "and the protools JSON context similary with what is done by /runtime/process-instances")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "The received file is not a valid JSON"),
            @ApiResponse(responseCode = "404", description = "The processDefinitionId does not exist"),
            @ApiResponse(responseCode = "415", description = "The received file is not an JSON file"),
            @ApiResponse(responseCode = "422", description = "The received file is a valid JSON bad has incorrect content")
    })
    public ResponseEntity<Void> createProcessInstanceWithContext(
            @Parameter(name = "file", description = "JSON file with the context", required = true)
            @RequestPart(name = "file") MultipartFile file,
            @RequestParam("processDefinitionId") String processDefinitionId,
            @RequestParam(name = "businessKey") String businessKey) {
        contextService.processContextFileAndCreateProcessInstance(file, processDefinitionId, businessKey);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
