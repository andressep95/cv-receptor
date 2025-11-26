package cl.playground.cv_receptor.service;

import cl.playground.cv_receptor.dto.ProcessResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AwsResponseProcessService {

    private static final Logger log = LoggerFactory.getLogger(AwsResponseProcessService.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProcessResponse processAwsResponse(Map<String, Object> awsData) {

        if (awsData == null || awsData.isEmpty()) {
            log.error("Datos de AWS vacíos o nulos");
            return new ProcessResponse("ERROR", "Los datos de AWS son requeridos");
        }

        try {
            // Convertir el Map a JSON pretty string
            String prettyJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(awsData);

            // Log de los datos recibidos
            log.info("========================================");
            log.info("LOG DATOS PROCESADOS RECIBIDOS DE AWS:");
            log.info("{}", prettyJson);
            log.info("========================================");

            // También imprimimos en consola para visibilidad
            System.out.println("========================================");
            System.out.println("PRINT DATOS PROCESADOS RECIBIDOS DE AWS:");
            System.out.println(prettyJson);
            System.out.println("========================================");

            // Aquí puedes procesar los datos según la estructura que venga de AWS
            // Ejemplos de campos que podrías extraer:
            // - awsData.get("status")
            // - awsData.get("data")
            // - awsData.get("extracted_text")
            // - awsData.get("metadata")
            // etc.

            // TODO: Implementar lógica de negocio con los datos procesados
            // Ejemplos:
            // - Guardar en base de datos
            // - Enviar notificación
            // - Procesar información extraída del CV
            // - etc.

            return new ProcessResponse("RECIBIDO", "Datos de AWS recibidos y procesados correctamente");

        } catch (Exception e) {
            log.error("Error al procesar respuesta de AWS", e);
            return new ProcessResponse("ERROR", "Error al procesar respuesta de AWS: " + e.getMessage());
        }
    }
}
