FROM --platform=linux/arm64 eclipse-temurin:17-jdk

LABEL maintainer="akradev"
LABEL description="ARM64 기반 Spring 크롬북 서버 with Chromium"

# 패키지 설치
RUN apt-get update && apt-get install -y \
    wget curl unzip gnupg ca-certificates \
    chromium-browser \
    libnss3 libatk1.0-0 libatk-bridge2.0-0 libx11-xcb1 \
    libxcomposite1 libxdamage1 libxrandr2 libxss1 \
    libasound2t64 libgtk-3-0 xdg-utils fonts-liberation \
    --no-install-recommends && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# JAR 파일 복사
COPY build/libs/newsbot-0.0.1-SNAPSHOT.jar /app.jar

# 기본 포트 노출
EXPOSE 8080

# 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]
