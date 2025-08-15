# Project Structure

This document outlines the complete structure of the Vinyl Records Database Application project.

## Directory Layout

```
vinyl-records-db-client-tcp-server/
├── 📁 .git/                          # Git repository metadata
├── 📁 .vscode/                       # VS Code workspace settings
│   ├── launch.json                   # Run/debug configurations
│   └── settings.json                 # Java project settings
├── 📁 bin/                          # Compiled class files (auto-generated)
│   ├── Credentials.class
│   ├── RecordsDatabaseClient.class
│   ├── RecordsDatabaseClient$1.class
│   ├── RecordsDatabaseClient$MyTableRecord.class
│   ├── RecordsDatabaseServer.class
│   └── RecordsDatabaseService.class
├── 📁 lib/                          # External dependencies
│   ├── postgresql-42.6.0.jar        # PostgreSQL JDBC driver
│   └── 📁 javafx-sdk-24.0.2/        # JavaFX SDK (platform-specific)
│       ├── 📁 bin/                   # JavaFX executables
│       ├── 📁 lib/                   # JavaFX libraries
│       └── 📁 legal/                 # License information
├── 📁 src/                          # Java source code
│   ├── Credentials.java              # Database & server configuration
│   ├── RecordsDatabaseClient.java    # JavaFX GUI application
│   ├── RecordsDatabaseServer.java    # Main server application
│   └── RecordsDatabaseService.java   # Multi-threaded service handler
├── 📄 .classpath                    # Eclipse classpath configuration
├── 📄 .gitignore                    # Git ignore rules
├── 📄 .project                      # Eclipse project descriptor
├── 📄 API_DOCUMENTATION.md          # Client-server protocol documentation
├── 📄 DATABASE_SETUP.md             # PostgreSQL installation guide
├── 📄 LICENSE                       # MIT License
├── 📄 README.md                     # Main project documentation
├── 📄 records.sql                   # Database schema and sample data
├── 📄 run-client.bat                # Windows batch file for client
└── 📄 run-server.bat                # Windows batch file for server
```

## Core Application Files

### Source Code (`src/`)

#### `RecordsDatabaseServer.java`
- **Purpose:** Main server application entry point
- **Responsibilities:**
  - Initialize server socket on configured port
  - Accept incoming client connections
  - Create service threads for each client
  - Manage server lifecycle

#### `RecordsDatabaseService.java`
- **Purpose:** Multi-threaded client request handler
- **Responsibilities:**
  - Parse client requests from TCP socket
  - Execute database queries via JDBC
  - Serialize and return query results
  - Manage database connections per thread

#### `RecordsDatabaseClient.java`
- **Purpose:** JavaFX desktop client application
- **Responsibilities:**
  - Provide graphical user interface
  - Handle user input (artist, location)
  - Establish TCP connections to server
  - Display query results in table format

#### `Credentials.java`
- **Purpose:** Configuration constants
- **Content:**
  - Database connection parameters (URL, username, password)
  - Server connection settings (host, port)
- **Note:** Excluded from version control for security

### Database Files

#### `records.sql`
- **Purpose:** Database schema and initial data
- **Content:**
  - Table creation statements (artist, record, recordshop, recordcopy)
  - Sample data inserts for testing
  - Foreign key relationships
- **Usage:** Import into PostgreSQL to set up database

### Configuration Files

#### VS Code Configuration (`.vscode/`)

##### `launch.json`
```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "name": "Run Server",
            "type": "java",
            "mainClass": "RecordsDatabaseServer"
        },
        {
            "name": "Run Client", 
            "type": "java",
            "mainClass": "RecordsDatabaseClient"
        }
    ]
}
```

##### `settings.json`
```json
{
    "java.project.sourcePaths": ["src"],
    "java.project.referencedLibraries": ["lib/**/*.jar"],
    "java.configuration.updateBuildConfiguration": "automatic"
}
```

#### Eclipse Configuration

##### `.project`
- Eclipse project descriptor
- Defines project name and build specifications
- Enables Eclipse Java development tools

##### `.classpath`
- Java classpath configuration
- References source folders and external JARs
- Compatible with Eclipse IDE

### Execution Scripts

#### `run-server.bat`
```batch
@echo off
cd /d "%~dp0"
echo Starting RecordsDatabaseServer...
java -cp "src;lib\postgresql-42.6.0.jar" RecordsDatabaseServer
pause
```

#### `run-client.bat`
```batch
@echo off
cd /d "%~dp0"
echo Starting RecordsDatabaseClient...
java --module-path "lib\javafx-sdk-24.0.2\lib" --add-modules javafx.controls,javafx.fxml -cp "src;lib\postgresql-42.6.0.jar" RecordsDatabaseClient
pause
```

## Dependencies

### External Libraries

#### PostgreSQL JDBC Driver (`postgresql-42.6.0.jar`)
- **Version:** 42.6.0
- **Purpose:** Database connectivity
- **Location:** `lib/postgresql-42.6.0.jar`
- **License:** BSD 2-Clause

#### JavaFX SDK (`javafx-sdk-24.0.2/`)
- **Version:** 24.0.2
- **Purpose:** GUI framework
- **Location:** `lib/javafx-sdk-24.0.2/`
- **Required Modules:** `javafx.controls`, `javafx.fxml`
- **Platform:** Windows x64 (platform-specific)

### Development Dependencies

#### Java Development Kit (JDK)
- **Minimum Version:** Java 17
- **Tested Version:** Java 24
- **Required For:** Compilation and runtime

#### PostgreSQL Database
- **Minimum Version:** PostgreSQL 14
- **Purpose:** Data storage and querying
- **Connection:** JDBC over TCP/IP

## Build Artifacts

### Compiled Classes (`bin/`)
- Auto-generated by Java compiler
- Contains `.class` files for all source files
- Excluded from version control
- Recreated during compilation

### File Relationships

```
RecordsDatabaseServer.java
├── Uses: Credentials.java (configuration)
└── Creates: RecordsDatabaseService.java threads

RecordsDatabaseService.java
├── Uses: Credentials.java (database connection)
├── Connects to: PostgreSQL database
└── Processes: Client requests via TCP

RecordsDatabaseClient.java
├── Uses: Credentials.java (server connection)
├── Connects to: RecordsDatabaseServer via TCP
└── Depends on: JavaFX SDK for GUI
```

## Version Control

### Git Configuration

#### Tracked Files:
- All source code (`.java` files)
- Documentation (`.md` files)
- Configuration files (VS Code, Eclipse)
- Database schema (`records.sql`)
- Execution scripts (`.bat` files)
- License and legal files

#### Ignored Files (`.gitignore`):
- Compiled classes (`*.class`)
- IDE-specific files (beyond configuration)
- External dependencies (`lib/` contents)
- Database credentials (`Credentials.java`)
- Assignment-specific documents
- System-generated files

## Development Workflow

### Compilation Process:
1. **Automatic (VS Code):** F5 triggers auto-compilation
2. **Manual (Command Line):** `javac` with classpath
3. **Output:** Compiled classes in `bin/` directory

### Execution Methods:
1. **VS Code Integration:** F5 launch configurations
2. **Command Line:** Java with module path and classpath
3. **Batch Files:** Double-click execution scripts

### Testing Approach:
1. **Single Client:** Basic functionality testing
2. **Multiple Clients:** Concurrent connection testing
3. **Database Queries:** Various search scenarios
4. **Error Handling:** Invalid input testing

## Architecture Patterns

### Design Patterns Used:

#### **3-Tier Architecture**
- **Presentation Tier:** JavaFX Client
- **Logic Tier:** Java Server with multi-threading
- **Data Tier:** PostgreSQL Database

#### **Thread-per-Request**
- Each client connection spawns dedicated service thread
- Enables concurrent request processing
- Isolates client sessions

#### **Data Transfer Object (DTO)**
- `MyTableRecord` class for GUI data binding
- `CachedRowSet` for serializable database results
- Clean separation of data and presentation

#### **Factory Pattern**
- `RowSetFactory` for creating `CachedRowSet` instances
- Standardized object creation approach

This structure demonstrates enterprise-level organization with clear separation of concerns, proper dependency management, and professional development practices.

---

*This documentation reflects the current project structure. Update as needed when adding new components or refactoring the codebase.*
