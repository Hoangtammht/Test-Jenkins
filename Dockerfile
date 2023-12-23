# Use the official Maven image with OpenJDK 11 as a base image
FROM maven:3.8.4-openjdk-11

# Set Timezone
ENV TZ=Asia/Ho_Chi_Minh

# Install Docker inside the container
USER root
RUN apt-get update && \
    apt-get install -y apt-transport-https ca-certificates curl software-properties-common && \
    curl -fsSL https://download.docker.com/linux/debian/gpg | apt-key add - && \
    add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/debian $(lsb_release -cs) stable" && \
    apt-get update && \
    apt-get install -y docker-ce-cli && \
    rm -rf /var/lib/apt/lists/* && \
    usermod -aG docker jenkins
USER jenkins

# Set the working directory inside the container
WORKDIR /usr/src/app

# Copy the Maven project files into the container
COPY pom.xml .
COPY src ./src

# Build the application
RUN mvn clean package

# Expose the port on which the application will run
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "target/Eparking-0.0.1-SNAPSHOT.jar"]
