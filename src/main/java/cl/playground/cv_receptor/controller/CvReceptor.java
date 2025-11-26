package cl.playground.cv_receptor.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.JsonNode;

@RestController
@RequestMapping("/cv-processed")
public class CvReceptor {

    @PostMapping
    public ResponseEntity<Void> getData(@RequestBody JsonNode json) {
        System.out.println("📩 JSON recibido:");
        System.out.println(json.toPrettyString());
        return ResponseEntity.ok().build();
    }
}
