package cl.playground.cv_receptor.controller;

import cl.playground.cv_receptor.dto.ProcessResponse;
import cl.playground.cv_receptor.service.ResumeProcessService;
import cl.playground.cv_receptor.service.AwsResponseProcessService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/resume")
public class CvReceptor {

    private final ResumeProcessService resumeProcessService;
    private final AwsResponseProcessService awsResponseProcessService;

    public CvReceptor(ResumeProcessService resumeProcessService,
                      AwsResponseProcessService awsResponseProcessService) {
        this.resumeProcessService = resumeProcessService;
        this.awsResponseProcessService = awsResponseProcessService;
    }

    @PostMapping(value = "/process", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProcessResponse> processResume(@RequestParam("file") MultipartFile file,
                                                         @RequestParam("language") String language,
                                                         @RequestParam("instructions") String instructions) {

        // El controlador solo coordina, delega la lógica al servicio
        ProcessResponse response = resumeProcessService.processResume(file, language, instructions);

        // El controlador maneja ResponseEntity según el resultado del servicio
        if ("ERROR".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping(value = "/aws-response", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProcessResponse> receiveAwsResponse(@RequestBody JsonNode awsData) {

        // El controlador delega el procesamiento de la respuesta de AWS al servicio
        ProcessResponse response = awsResponseProcessService.processAwsResponse(awsData);

        // El controlador maneja ResponseEntity según el resultado del servicio
        if ("ERROR".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }

        return ResponseEntity.ok(response);
    }

}
