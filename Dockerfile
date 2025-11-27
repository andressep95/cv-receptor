# Stage 1: Build
FROM maven:3.9.11-eclipse-temurin-17 AS builder

WORKDIR /app

# Copiar archivos de Maven wrapper y pom.xml primero (para cache de dependencias)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Descargar dependencias (esta capa se cachea si pom.xml no cambia)
RUN ./mvnw dependency:go-offline -B

# Copiar el codigo fuente
COPY src ./src

# Compilar la aplicacion
RUN ./mvnw clean package -DskipTests -B

# Stage 2: Runtime
FROM amazoncorretto:17-alpine

WORKDIR /app

# Instalar wget para healthcheck
RUN apk add --no-cache wget

# Crear un usuario no-root para ejecutar la aplicacion
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar el JAR desde la etapa de build
COPY --from=builder /app/target/*.jar app.jar

# Exponer el puerto de la aplicacion
EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1

# Ejecutar la aplicacion con configuraciones optimizadas de JVM
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-XX:+ExitOnOutOfMemoryError", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", \
  "app.jar"]