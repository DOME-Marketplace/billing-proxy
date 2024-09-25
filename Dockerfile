FROM openjdk:17-jdk-alpine
RUN addgroup -S dome && adduser -S bproxy -G dome
USER bproxy:dome
ARG JAR_FILE=target/billing-proxy.war
COPY ${JAR_FILE} billing-proxy.war
EXPOSE 8080/tcp 
ENTRYPOINT ["java","-jar","/billing-proxy.war"]