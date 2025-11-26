# CV Receptor - Docker Setup

Aplicación Spring Boot para recibir datos de CV procesados en formato JSON.

## Requisitos

- Docker
- Docker Compose
- Cuenta de ngrok (opcional, ya incluida)

## Estructura del Proyecto

```
cv-receptor/
├── Dockerfile                  # Multi-stage Dockerfile optimizado
├── docker-compose.yml          # Orquestación de servicios
├── get-ngrok-url.sh           # Script para obtener URL pública de ngrok
├── src/                       # Código fuente Spring Boot
└── pom.xml                    # Configuración Maven
```

## Servicios

### 1. API (cv-receptor-api)
- Puerto: 8080
- Endpoint: POST /cv-processed
- Healthcheck: /actuator/health

### 2. Ngrok (cv-receptor-ngrok)
- Puerto Web UI: 4040
- Expone la API al internet públicamente

## Inicio Rápido

### 1. Construir y levantar los servicios

```bash
docker-compose up --build -d
```

### 2. Verificar que los servicios estén corriendo

```bash
docker-compose ps
```

Deberías ver:
```
NAME                   STATUS              PORTS
cv-receptor-api        Up (healthy)        0.0.0.0:8080->8080/tcp
cv-receptor-ngrok      Up                  0.0.0.0:4040->4040/tcp
```

### 3. Obtener la URL pública de ngrok

```bash
./get-ngrok-url.sh
```

Esto mostrará:
- URL pública de ngrok
- Endpoint disponible
- Ejemplo de comando curl para probar

## Uso

### Probar el endpoint localmente

```bash
curl -X POST http://localhost:8080/cv-processed \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan Pérez",
    "email": "juan.perez@example.com",
    "phone": "+56912345678"
  }'
```

### Probar el endpoint públicamente (via ngrok)

Primero obtén la URL pública:
```bash
./get-ngrok-url.sh
```

Luego usa esa URL:
```bash
curl -X POST https://xxxx-xxx-xxx-xxx.ngrok-free.app/cv-processed \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan Pérez",
    "email": "juan.perez@example.com"
  }'
```

## Monitoreo

### Ver logs de la aplicación

```bash
# Todos los servicios
docker-compose logs -f

# Solo la API
docker-compose logs -f api

# Solo ngrok
docker-compose logs -f ngrok
```

### Verificar salud de la aplicación

```bash
# Localmente
curl http://localhost:8080/actuator/health

# Via ngrok (reemplaza con tu URL)
curl https://xxxx-xxx-xxx-xxx.ngrok-free.app/actuator/health
```

### Interfaz web de ngrok

Abre en tu navegador: http://localhost:4040

Aquí puedes ver:
- Todas las peticiones HTTP recibidas
- Respuestas de la aplicación
- Estadísticas de tráfico

## Comandos Útiles

### Detener los servicios

```bash
docker-compose down
```

### Detener y eliminar volúmenes

```bash
docker-compose down -v
```

### Reconstruir la imagen

```bash
docker-compose build --no-cache api
docker-compose up -d
```

### Ver el estado de los contenedores

```bash
docker-compose ps
```

### Ejecutar bash en el contenedor de la API

```bash
docker-compose exec api sh
```

## Configuración de Ngrok

La API key de ngrok está configurada en el `docker-compose.yml`:

```yaml
environment:
  - NGROK_AUTHTOKEN=${NGROK_AUTHTOKEN:-2xtf0E1SVfQSpiL9S6iUwnuLznI_uddo2VPakZ8ytW49Y6T7}
```

Para usar tu propia API key:

1. Obtén tu token en: https://dashboard.ngrok.com/get-started/your-authtoken
2. Crea un archivo `.env` en la raíz del proyecto:
   ```
   NGROK_AUTHTOKEN=tu_token_aqui
   ```
3. Reinicia los servicios:
   ```bash
   docker-compose down
   docker-compose up -d
   ```

## Arquitectura del Dockerfile

El Dockerfile usa multi-stage build para optimizar el tamaño de la imagen:

### Stage 1: Build
- Imagen base: `maven:3.9.11-eclipse-temurin-17`
- Descarga dependencias
- Compila el proyecto
- Genera el JAR

### Stage 2: Runtime
- Imagen base: `eclipse-temurin:17-jre-alpine` (más liviana)
- Solo copia el JAR compilado
- Usuario no-root para seguridad
- Configuraciones JVM optimizadas para contenedores

## Troubleshooting

### La aplicación no inicia

```bash
# Ver logs detallados
docker-compose logs api

# Verificar que el puerto 8080 no esté en uso
lsof -i :8080
```

### Ngrok no obtiene la URL

```bash
# Verificar que el contenedor de ngrok esté corriendo
docker-compose ps ngrok

# Ver logs de ngrok
docker-compose logs ngrok

# Esperar unos segundos más y volver a ejecutar
./get-ngrok-url.sh
```

### Error de healthcheck

```bash
# Verificar que actuator esté disponible
curl http://localhost:8080/actuator/health

# Si falla, verificar logs
docker-compose logs api
```

### Rebuild completo

```bash
# Detener todo
docker-compose down

# Limpiar imágenes antiguas
docker system prune -a

# Reconstruir desde cero
docker-compose build --no-cache
docker-compose up -d
```

## Endpoints Disponibles

### Aplicación

- `POST /cv-processed` - Endpoint principal para recibir datos de CV
- `GET /actuator/health` - Estado de salud de la aplicación
- `GET /actuator/info` - Información de la aplicación

### Ngrok

- `http://localhost:4040` - Web UI de ngrok
- `http://localhost:4040/api/tunnels` - API de ngrok para obtener túneles

## Notas de Seguridad

1. El token de ngrok está hardcodeado en el docker-compose.yml como fallback
2. Se recomienda usar variables de entorno o archivos .env para secretos
3. La aplicación no tiene autenticación implementada
4. Para producción, considera implementar:
   - Autenticación/Autorización
   - Rate limiting
   - HTTPS
   - Validación de entrada más robusta

## Desarrollo

Para desarrollo local sin Docker:

```bash
# Compilar
./mvnw clean package

# Ejecutar
./mvnw spring-boot:run

# O ejecutar el JAR directamente
java -jar target/cv-receptor-0.0.1-SNAPSHOT.jar
```