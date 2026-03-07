# =========================
# Stage 1: Build with Maven
# =========================
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom ก่อน เพื่อ cache dependency
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build jar
RUN mvn clean package -DskipTests

# =========================
# Stage 2: Run application
# =========================
FROM eclipse-temurin:17-jre

WORKDIR /app

# timezone optional
ENV TZ=Asia/Bangkok

# Copy jar from builder
COPY --from=builder /app/target/ticket-oa-api.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]