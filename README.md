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

## Future Enhancements

- Persistent storage (database integration)
- Export reflections to various formats (PDF, CSV)
- Search functionality
- Tags and categorization
