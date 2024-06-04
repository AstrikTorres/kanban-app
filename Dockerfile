FROM node:latest AS front
WORKDIR /app
COPY frontend .
RUN npm install
RUN npm run build

FROM maven:latest AS appserver1
WORKDIR /app
COPY pom.xml .
RUN mvn -B -f pom.xml -s /usr/share/maven/ref/settings-docker.xml dependency:resolve
COPY . .
COPY --from=front /app/build ./src/main/resources/static
RUN mvn -B -s /usr/share/maven/ref/settings-docker.xml package -DskipTests

FROM openjdk:11.0.16-oracle
WORKDIR /app
COPY --from=appserver1 /app/target/*.jar ./app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
