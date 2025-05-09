# Use a base image with Java 17
FROM openjdk:17-jdk-alpine

# Add the executable jar file to the container
ADD target/escalayt-0.0.1-SNAPSHOT.jar escalayt.jar

# Command to run the application
ENTRYPOINT ["java", "-jar", "escalayt.jar"]