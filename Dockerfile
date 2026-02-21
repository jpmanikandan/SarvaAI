# Build Stage
FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

# Run Stage
FROM eclipse-temurin:17-jre-focal
WORKDIR /app
COPY --from=build /app/target/sarva-bhasha-ai-with-core-0.0.1-SNAPSHOT.jar app.jar

# Set environment variables for Render
ENV PORT=8080
ENV DATABASE_PATH=/app/data/sarva_db

# Create data directory for H2 persistence
RUN mkdir -p /app/data

# Optimize JVM for Render's 512MB free tier
ENV JAVA_OPTS="-Xmx384m -Xss512k -XX:MaxMetaspaceSize=128m"

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
