FROM eclipse-temurin:21-jdk-jammy

# Устанавливаем только минимально необходимые компоненты
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    pandoc \
    texlive-base \
    texlive-latex-recommended \
    texlive-fonts-recommended \
    lmodern \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY target/report-generator-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]