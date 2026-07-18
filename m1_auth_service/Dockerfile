# 1. Fase de compilacion: Usamos Maven para construir el archivo .jar
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos el archivo de configuracion de dependencias y el codigo fuente
COPY pom.xml .
COPY src ./src

# Compilamos saltandonos los tests para que sea más rápido en local
RUN mvn clean package -DskipTests

# 2. Fase de ejecución: Una imagen más ligera que solo corre Java 17
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# Nos traemos el .jar compilado en la fase anterior
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto en el contenedor (asegúrate de que coincida con el properties de este servicio)
EXPOSE 8081

# Comando para arrancar el microservicio
ENTRYPOINT ["java", "-jar", "app.jar"]