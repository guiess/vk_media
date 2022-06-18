FROM gradle:7.4-jdk18-alpine as builder

COPY . /home/gradle
WORKDIR /home/gradle
RUN cd /home/gradle
RUN gradle build

FROM openjdk:18.0.1.1-jdk-slim-buster
COPY --from=builder /home/gradle/build/libs/vk-media-0.0.1-SNAPSHOT.war vk_media.war

ENTRYPOINT ["java", "-jar", "/vk_media.war"]