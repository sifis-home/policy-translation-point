FROM adoptopenjdk/openjdk11

WORKDIR /app

RUN ./mvn -N wrapper:wrapper 
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
 
COPY src ./src
 
CMD ["./mvnw", "spring-boot:run"]
