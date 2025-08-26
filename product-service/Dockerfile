# Etapa 1: Construcción del JAR con Maven
# Usamos una imagen de Maven estable con OpenJDK 17
FROM maven:3.9.6-eclipse-temurin-17 AS build
# Establece el directorio de trabajo dentro del contenedor
WORKDIR /usr/src/app
# Copia los archivos de configuración y el código fuente
COPY . .
# Ejecuta la compilación y empaquetado del proyecto
RUN mvn -f pom.xml clean package -DskipTests

# Etapa 2: Creación de la imagen final
# Usamos una imagen ligera de OpenJDK 17 para el runtime
FROM openjdk:17-jdk-slim
# Establece el directorio de trabajo para la app final
WORKDIR /app
# Copia el archivo JAR generado desde la etapa de build
COPY --from=build /usr/src/app/target/*.jar app.jar
# Expone el puerto de la aplicación
EXPOSE 8080
# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]