# 1. THE BASE: Start with a machine that already has Java installed
# "openjdk" is the official Java image. "17-jdk-slim" is a lightweight version.
FROM openjdk:17-jdk-slim

# 2. THE WORKSPACE: Create a folder inside the container called "/app"
WORKDIR /app

# 3. COPY INGREDIENTS: Copy everything from your computer (.) to the container (.)
# This moves your 'The_Arena' folder inside the container.
COPY . .

# 4. PREPARE: Compile the code inside the container
# This runs 'javac' just like you did manually.
RUN javac The_Arena/*.java

# 5. THE STARTUP: What command runs when the container starts?
# This runs 'java The_Arena.Main'
CMD ["java", "The_Arena.Main"]