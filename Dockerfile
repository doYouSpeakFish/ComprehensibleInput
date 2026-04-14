FROM gradle:latest AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle :backend:shadowJar --no-daemon

FROM amazoncorretto:25-alpine AS runtime
EXPOSE 8080
RUN mkdir /app
COPY --from=build /home/gradle/src/backend/build/libs/*.jar /app/comprehensible-input-backend.jar
USER 1000
ENTRYPOINT ["java","-jar","/app/comprehensible-input-backend.jar"]
