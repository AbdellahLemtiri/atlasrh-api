# Étape 1 : Build de l'application avec Maven et JDK 17
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Optimisation du cache Docker : copier d'abord le pom.xml
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copier le code source et compiler le Jar
COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2 : Image d'exécution légère 
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Création d'un utilisateur non-root pour la sécurité
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copie du jar généré depuis l'étape de build
COPY --from=build /app/target/*.jar app.jar

# Port exposé par Spring Boot
EXPOSE 8080

# Exécution de l'application
ENTRYPOINT ["java", "-jar", "app.jar"]