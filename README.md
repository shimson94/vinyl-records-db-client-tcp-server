# 🎵 Vinyl Record Query System

A comprehensive **3-tier TCP-based multi-threaded client-server application** built with Java, featuring a JavaFX desktop client and PostgreSQL backend for querying vinyl record availability across multiple record shops.

**Developer:** Hashim  
**Project Duration:** March - April 2024  
**University Assignment:** Full Stack Application Development  

---

## 🏗️ Project Overview

This application demonstrates enterprise-level software architecture by implementing a complete inventory management system for vinyl record shops. Users can search for records by artist surname and shop location, receiving real-time inventory data including availability counts and pricing information.

### Key Features

- **Multi-threaded TCP Server** - Handles concurrent client connections efficiently
- **JavaFX Desktop Client** - Professional GUI with dynamic table updates
- **PostgreSQL Integration** - Robust database with normalized schema design
- **Secure JDBC Implementation** - Parameterized queries preventing SQL injection
- **Real-time Inventory Queries** - Instant search across multiple record shops
- **Concurrent User Support** - Tested with multiple simultaneous client connections

---

## �️ Technical Architecture

### System Components

```
┌───────────────────┐    TCP/IP     ┌───────────────────┐    JDBC     ┌───────────────────┐
│   JavaFX Client   │◄─────────────►│ Multi-threaded    │◄───────────►│  PostgreSQL DB    │
│                   │   Socket      │   Java Server     │ Connection  │                   │
│ • Search GUI      │ Communication │ • Thread Pool     │    Pool     │ • Artist Table    │
│ • Results Table   │               │ • Request Handler │             │ • Record Table    │
│ • Input Forms     │               │ • JDBC Service    │             │ • Shop Table      │
│                   │               │                   │             │ • Copy Table      │
└───────────────────┘               └───────────────────┘             └───────────────────┘
```

### Database Schema

The application utilizes a normalized 4-table PostgreSQL schema:

- **artist** - Artist information (artistID, firstname, lastname)
- **record** - Album details (recordID, title, label, genre, rrp, artistID)
- **recordshop** - Shop locations (recordshopID, name, city)
- **recordcopy** - Inventory tracking (recordcopyID, recordID, recordshopID, onLoan)

---

## 🚀 Getting Started

### Prerequisites
- **Java 17+**
- **PostgreSQL 14+** 
- **JavaFX SDK 17+** (external download required)
- **PostgreSQL JDBC driver** (`postgresql-42.6.0.jar`)

### 1. Clone the Repository
```bash
git clone https://github.com/shimson94/vinyl-records-db-client-tcp-server.git
cd vinyl-records-db-client-tcp-server
```

### 2. Download Required Dependencies

**JavaFX SDK:**
1. Download from: https://openjfx.io/
2. Extract to a local directory (e.g., `C:\javafx-sdk-17\` or `/usr/local/javafx-sdk-17/`)

**PostgreSQL JDBC Driver:**
1. Download `postgresql-42.6.0.jar` from: https://jdbc.postgresql.org/download/
2. Place in your project's `lib/` directory or add to classpath

### 3. IDE Setup (Choose your IDE)

#### IntelliJ IDEA:
1. **Add JavaFX SDK:**
   - File → Project Structure → Libraries → + → Java
   - Select `javafx-sdk-17/lib` directory
   - Apply to modules

2. **Add PostgreSQL Driver:**
   - File → Project Structure → Libraries → + → Java  
   - Select `postgresql-42.6.0.jar`
   - Apply to modules

3. **Configure Run Configuration:**
   - Run → Edit Configurations
   - VM options: `--module-path "path/to/javafx-sdk-17/lib" --add-modules javafx.controls,javafx.fxml`

#### VS Code:
1. **Configure `.vscode/settings.json`:**
```json
{
    "java.project.referencedLibraries": [
        "lib/**/*.jar",
        "path/to/javafx-sdk-17/lib/**/*.jar",
        "path/to/postgresql-42.6.0.jar"
    ]
}
```

2. **Create `.vscode/launch.json`:**
```json
{
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "Launch RecordsDatabaseServer",
            "request": "launch",
            "mainClass": "RecordsDatabaseServer",
            "vmArgs": "--module-path \"path/to/javafx-sdk-17/lib\" --add-modules javafx.controls,javafx.fxml",
            "classpath": ["${workspaceFolder}/src", "path/to/postgresql-42.6.0.jar"]
        },
        {
            "type": "java", 
            "name": "Launch RecordsDatabaseClient",
            "request": "launch",
            "mainClass": "RecordsDatabaseClient",
            "vmArgs": "--module-path \"path/to/javafx-sdk-17/lib\" --add-modules javafx.controls,javafx.fxml",
            "classpath": ["${workspaceFolder}/src", "path/to/postgresql-42.6.0.jar"]
        }
    ]
}
```

### 4. Command Line Setup (Alternative)

**Create lib directory and add dependencies:**
```bash
mkdir lib
# Copy postgresql-42.6.0.jar to lib/
# Copy javafx-sdk-17/ to lib/ (or reference external path)
```

**Compile with dependencies:**
```bash
javac -cp "lib/postgresql-42.6.0.jar:lib/javafx-sdk-17/lib/*" --module-path lib/javafx-sdk-17/lib --add-modules javafx.controls,javafx.fxml src/*.java
```

**Run with dependencies:**
```bash
# Server
java -cp "src:lib/postgresql-42.6.0.jar:lib/javafx-sdk-17/lib/*" --module-path lib/javafx-sdk-17/lib --add-modules javafx.controls,javafx.fxml RecordsDatabaseServer

# Client  
java -cp "src:lib/postgresql-42.6.0.jar:lib/javafx-sdk-17/lib/*" --module-path lib/javafx-sdk-17/lib --add-modules javafx.controls,javafx.fxml RecordsDatabaseClient
```

### 5. Set Up the Database

**Import the database schema:**
```sql
-- In PostgreSQL shell
\i /path/to/records.sql

-- Verify tables were created
\dt

-- View table contents
SELECT * FROM tablename;
```

**Update database credentials** in `Credentials.java` (create this file):
```java
public class Credentials {
    public static String username = "your_username";
    public static String password = "your_password"; 
    public static String url = "jdbc:postgresql://localhost:5432/your_database";
    public static String hostAddress = "localhost";
    public static int portNumber = 8888;
}
```

### 6. Compile and Run

**Compile all classes:**
```bash
javac -cp ".:postgresql-42.6.0.jar" *.java
```

**Start the server:**
```bash
java -cp ".:postgresql-42.6.0.jar" RecordsDatabaseServer
```

**Run the client (in separate terminal):**
```bash
java -cp ".:postgresql-42.6.0.jar" RecordsDatabaseClient
```

---

## 🖥️ Usage

1. **Start the server** - it will listen indefinitely for client connections
2. **Launch the client** - JavaFX GUI will appear
3. **Enter search criteria:**
   - Artist surname (e.g., "Sheeran", "Beyonce")
   - Record shop city (e.g., "Cardiff", "London")
4. **Click "Request Service"** - results appear in the table below
5. **Multiple queries** - repeat with different search terms

**Example queries:**
- Artist: `Beyonce`, City: `London`
- Artist: `Sheeran`, City: `Cardiff`
- Artist: `Franklin`, City: `Berlin` (returns no results)

---

## 🔧 Technical Implementation

### Server Components
- **RecordsDatabaseServer**: Main server with infinite service loop
- **RecordsDatabaseService**: Thread-based service provider for each client request
- **Thread-per-request model**: Each client connection spawns a new service thread

### Client Components  
- **RecordsDatabaseClient**: JavaFX application with GUI and networking
- **MyTableRecord**: Data model for TableView display
- **Socket communication**: TCP connection with object serialization

### Security Features
- **Parameterized SQL queries** prevent injection attacks
- **Proper resource cleanup** (connections, statements, result sets)
- **Exception handling** with appropriate exit codes

---

## 📁 Project Structure

```
src/
├── RecordsDatabaseServer.java    # Main server application
├── RecordsDatabaseService.java   # Service thread handler  
├── RecordsDatabaseClient.java    # JavaFX client application
└── Credentials.java              # Database/server credentials (optional)
```
- See [PROJECT_STRUCTURE.md](https://github.com/shimson94/vinyl-records-db-client-tcp-server/blob/main/PROJECT_STRUCTURE.md) file for more details
---

## 📚 Future Improvements

- Docker Compose for server + PostgreSQL setup
- REST API alongside TCP service
- Advanced search filters (genre, price range, date)
- Connection pooling for better scalability
- Configuration file instead of hardcoded credentials

---

## 🪪 License

MIT License — see LICENSE file.

---

**Development Setup:**
- Tested with PostgreSQL 14+ and `postgresql-42.6.0.jar`
- Compatible with IntelliJ IDEA and VS Code
- Command-line compilation and execution supported
