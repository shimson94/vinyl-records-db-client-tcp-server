# 🎵 Vinyl Record Query System

A **3-tier TCP-based multi-threaded client-server application** built in Java, featuring a **JavaFX client** and a **PostgreSQL-backed JDBC service** for querying vinyl record availability.

Originally developed as part of a university networking and database systems assignment, this project demonstrates:
- **Java Networking** with multi-threaded request handling
- **JDBC integration** for secure SQL queries (SQL injection-safe)
- **JavaFX** GUI for a user-friendly interface
- Robust exception handling and graceful shutdown
- Parameterised database queries for artist and shop lookups

---

## 📦 Features

- **Multi-threaded Server**: Handles multiple simultaneous client requests.
- **Service Provider Pattern**: Dedicated thread per request, connects to PostgreSQL, retrieves data, sends results.
- **JavaFX Client GUI**: Easy input for artist surname and shop city, table display of matching records.
- **Secure JDBC**: Parameterised queries prevent SQL injection.
- **Robust Error Handling**: Handles no-result queries gracefully.
- **PostgreSQL Backend**: Tested with `postgresql-42.6.0.jar` driver.

---

## 🏗 Architecture
JavaFX Client ↔ TCP Socket ↔ Multi-threaded Java Server ↔ JDBC ↔ PostgreSQL DB  


---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- PostgreSQL 14+
- `postgresql-42.6.0.jar` JDBC driver

### 1. Clone the Repository
```bash
git clone https://github.com/<your-username>/vinyl-db-client-server.git
cd vinyl-db-client-server
```

### 2. Set Up the Database

- Import the provided schema.sql into PostgreSQL.
- Update DB connection details in ServerServiceProvider.java.

### 3. Run the Server
```bash
javac -cp ".:postgresql-42.6.0.jar" server/*.java
java -cp ".:postgresql-42.6.0.jar" server.ServerMain
```

### 4. Run the Client
```bash
javac client/*.java
java client.ClientMain
```

---

## 📚 Future Improvements

- Docker Compose for server + DB
- More advanced search filters (genre, price range)
- REST API alongside TCP service

---

## 🪪 License

MIT License — see LICENSE file.

---

**Other Setup Recommendations:**
- **.gitignore:**
  - *.class
  - *.jar
  - /bin/
  - /out/
  - *.log
  - .DS_Store

- **Branch protection:** Enable `main` branch protection in GitHub settings.
- **Topics/Tags:** `java`, `javafx`, `tcp-server`, `jdbc`, `postgresql`, `multithreading`, `networking`, `university-project`

---
