FROM maven:3.9.12-eclipse-temurin-25 as build

COPY . /build

WORKDIR /build

RUN mvn clean package -DskipTests=true -B

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/build/application/target/application-1.0.0.jar"]


