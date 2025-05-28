# Work Reflection Service

A simple command-line interface for recording and retrieving daily work reflections.

## Features

- Add daily work reflections
- Retrieve past reflections by date
- List all recorded reflection dates
- Command history and tab completion

## Prerequisites

- Java 24 or higher
- Maven

## Getting Started

### Building the Application

```bash
./mvnw clean package
```

### Running the Application

To run the CLI application:

```bash
./run-cli.sh
```

Or manually:

```bash
# Build the application
./mvnw clean package -DskipTests

# Run the CLI application
java -Dspring.main.web-application-type=none \
     -Dspring.ai.vectorstore.chroma.enabled=false \
     -Dspring.ai.model.ollama.enabled=false \
     -Dspring.ai.advisor.enabled=false \
     -jar target/work-reflection-service-0.0.1-SNAPSHOT.jar \
     --spring.main.sources=de.sipgate.konschack.work_reflection_service.cli.CliApplication
```

## Usage

Once the application is running, you'll see a shell prompt. Here are the available commands:

### Adding a Reflection

```
add --reflection "Today I completed the user authentication feature and started working on the reporting module."
```

To add a reflection for a specific date:

```
add --reflection "Completed sprint planning and refined backlog items." --date 2023-06-15
```

### Getting a Reflection

To get today's reflection:

```
get
```

To get a reflection for a specific date:

```
get --date 2023-06-15
```

### Listing All Reflections

```
list
```

### Other Commands

- `help` - Display help information
- `version` - Display the application version
- `clear` - Clear the console
- `exit` - Exit the application

## Tab Completion

The shell supports tab completion. Start typing a command and press Tab to complete it or see available options.

## Command History

Use the up and down arrow keys to navigate through previously executed commands.

## Docker Configuration

### Running Docker Containers

This project uses Docker Compose to run the required containers (Chroma vector database and Ollama).

#### Running All Containers

To start both the Chroma vector database and Ollama with the llama3.2 model:

```bash
# Start all containers in detached mode
docker compose up -d

# View container logs
docker compose logs -f
```

This will:
1. Start the Chroma vector database container
2. Start the Ollama container
3. Automatically pull the required models for Ollama (llama3.2 and mxbai-embed-large)

#### Running Individual Containers

You can also start each container individually:

```bash
# Start only the Chroma container
docker compose up -d chroma

# Start only the Ollama container
docker compose up -d ollama
```

#### Stopping Containers

To stop all containers:

```bash
docker compose down
```

To stop a specific container:

```bash
docker compose stop chroma
docker compose stop ollama
```

### Connecting to Docker Containers

#### Connecting to Chroma Vector Database

When running the Chroma vector database in Docker Desktop, you need to configure the application to connect to it properly:

1. Edit the `application.properties` file and set the Chroma base URL:
   ```properties
   # Use host.docker.internal instead of localhost when running in Docker Desktop
   spring.ai.vectorstore.chroma.base-url=http://host.docker.internal:8000
   ```

2. If you're running the application from IntelliJ terminal, you can use localhost:
   ```properties
   spring.ai.vectorstore.chroma.base-url=http://localhost:8000
   ```

#### Connecting to Ollama

Similarly, when running the Ollama container in Docker Desktop, you need to configure the application to connect to it properly:

1. Edit the `application.properties` file and set the Ollama base URL:
   ```properties
   # Use host.docker.internal instead of localhost when running in Docker Desktop
   spring.ai.ollama.base-url=http://host.docker.internal:11434
   ```

2. If you're running the application from IntelliJ terminal, you can use localhost:
   ```properties
   spring.ai.ollama.base-url=http://localhost:11434
   ```

The difference is due to how Docker Desktop networking works. When running a container in Docker Desktop, "localhost" refers to the container itself, not your host machine. Using "host.docker.internal" allows the container to access services running on your host machine.

## Future Enhancements

- Persistent storage (database integration)
- Export reflections to various formats (PDF, CSV)
- Search functionality
- Tags and categorization
