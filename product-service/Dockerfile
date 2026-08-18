
# Build
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Runtime
From eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/*.jar product.jar
ENTRYPOINT ["java", "-jar", "product.jar"]
