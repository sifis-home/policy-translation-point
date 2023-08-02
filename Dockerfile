FROM adoptopenjdk/openjdk11
WORKDIR /app
COPY README.md ./

COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
 
COPY src ./src
 
CMD ["./mvnw", "spring-boot:run"]
