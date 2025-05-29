# =======================================
# 1) 빌드 스테이지: JAR 준비
# =======================================
FROM eclipse-temurin:17-jdk AS builder
ARG VERSION
WORKDIR /workspace

# JAR 복사
COPY build/libs//newsbot-${VERSION}.jar ./app.jar

# =======================================
# 2) 런타임 스테이지: Selenium + Chromium + Java
# =======================================
FROM --platform=linux/arm64 seleniarm/standalone-chromium

USER root
WORKDIR /app

# 1) Temurin JDK 복사
COPY --from=builder /opt/java/openjdk /opt/java/openjdk
ENV PATH=/opt/java/openjdk/bin:$PATH

# 2) Spring Boot JAR 복사
COPY --from=builder /workspace/app.jar ./app.jar

# (선택) 추가 의존성 설치 — seleniarm base에 없을 때만
RUN apt-get update && apt-get install -y --no-install-recommends \
        fonts-liberation \
        xdg-utils \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# 3) OpenTelemetry javaagent 파일 다운로드 (런타임 스테이지에서)
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /app/opentelemetry-javaagent.jar

# 3) 포트 노출 (Spring Boot 서버 포트)
EXPOSE 21000

# 4) 컨테이너 시작 시 실행 명령
#ENTRYPOINT ["java", "-javaagent:/app/opentelemetry-javaagent.jar", "-jar", "/app/app.jar"]
#ENTRYPOINT ["java", "-javaagent:/app/opentelemetry-javaagent.jar", "-Dotel.exporter=logging", "-jar", "/app/app.jar"]

