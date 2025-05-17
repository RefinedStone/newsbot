FROM eclipse-temurin:17-jdk

LABEL maintainer="akradev"
LABEL description="경량 headless Chrome 크롤링 서버"

# 크롬 설치 + 클린업까지 하나의 RUN 블록
RUN apt-get update && apt-get install -y \
    wget curl unzip gnupg ca-certificates \
    && wget -q -O /tmp/chrome.deb https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb \
    && apt-get install -y /tmp/chrome.deb \
    && rm /tmp/chrome.deb \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# 앱 복사
COPY build/libs/newsbot-0.0.1-SNAPSHOT.jar /app.jar

# 기본 실행 명령
ENTRYPOINT ["java", "-jar", "/app.jar"]
