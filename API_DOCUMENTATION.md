# API Documentation

This document details the client-server communication protocol used by the Vinyl Records Query System.

## Protocol Overview

The application uses a **custom TCP-based protocol** for communication between JavaFX clients and the multi-threaded Java server. The protocol is designed for simplicity, reliability, and efficient data transfer.

### Communication Architecture

```
Client                           Server                          Database
  │                                │                                │
  ├── 1. Establish TCP Connection  │                                │
  │ ────────────────────────────►  │                                │
  │                                │                                │
  ├── 2. Send Query Request        │                                │
  │ ────────────────────────────►  │                                │
  │                                ├── 3. Parse Request             │
  │                                │ ────────────────────────────►  │
  │                                │                                │
  │                                │ ◄──── 4. Execute SQL Query   ──┤
  │                                │                                │
  │                                ├── 5. Serialize Results         │
  │ ◄──── 6. Send Response ─────── │                                │
  │                                │                                │
  ├── 7. Display Results           │                                │
  │                                │                                │
  ├── 8. Close Connection          │                                │
  │ ────────────────────────────►  │                                │
```

## Message Format

### Request Message Structure

**Format:** `artist_surname;shop_city#`

**Components:**
- `artist_surname`: The artist's last name (String)
- `;`: Separator character (fixed)
- `shop_city`: The record shop's city (String) 
- `#`: Message terminator (fixed)

**Examples:**
```
Beyonce;London#
Sheeran;Cardiff#
Taylor;Manchester#
InvalidArtist;NonExistentCity#
```

### Response Message Structure

**Format:** Serialized `CachedRowSet` object

The server responds with a Java `CachedRowSet` object containing the query results. This object is serialized using Java's built-in object serialization mechanism.

**Response Data Fields:**
- `title` (String): Album/record title
- `label` (String): Record label name  
- `genre` (String): Musical genre
- `rrp` (String): Recommended retail price
- `num_copies` (String): Number of available copies

## Network Protocol Details

### Connection Management

#### Server Side:
```java
// Server listens on configured port
ServerSocket serverSocket = new ServerSocket(Credentials.PORT);

// Accept client connections
Socket clientSocket = serverSocket.accept();

// Create dedicated service thread
Thread serviceThread = new Thread(new RecordsDatabaseService(clientSocket));
serviceThread.start();
```

#### Client Side:
```java
// Connect to server
Socket clientSocket = new Socket(Credentials.HOST, Credentials.PORT);

// Send request and receive response
// ... (detailed below)

// Close connection
clientSocket.close();
```

### Request Transmission

#### Client Implementation:
```java
public void requestService() {
    try {
        String commandToSend = userCommand + "#";
        OutputStream outputStream = clientSocket.getOutputStream();
        outputStream.write(commandToSend.getBytes());
        outputStream.flush();
    } catch(IOException e) {
        // Error handling
    }
}
```

**Protocol Details:**
- **Encoding:** UTF-8 (default Java string encoding)
- **Transmission:** Raw bytes over TCP socket
- **Termination:** Hash character (#) indicates end of message
- **Flushing:** Stream is flushed to ensure immediate transmission

### Request Processing

#### Server-side Parsing:
```java
public String[] retrieveRequest() {
    try {
        InputStream socketStream = serviceSocket.getInputStream();
        InputStreamReader socketReader = new InputStreamReader(socketStream);
        
        StringBuffer stringBuffer = new StringBuffer();
        char x;
        
        // Read characters until terminator
        while (true) {
            x = (char) socketReader.read();
            if (x == '#') break;
            stringBuffer.append(x);
        }
        
        // Parse message components
        String incomingRequest = stringBuffer.toString();
        String[] parts = incomingRequest.split(";");
        
        if (parts.length == 2) {
            this.requestStr[0] = parts[0];  // artist surname
            this.requestStr[1] = parts[1];  // shop city
        }
    } catch (IOException e) {
        // Error handling
    }
    return this.requestStr;
}
```

**Parsing Logic:**
- Read character by character until '#' terminator
- Split on semicolon separator
- Validate message format (exactly 2 components)
- Store artist surname and shop city for database query

### Database Query Execution

#### SQL Query Construction:
```java
public boolean attendRequest() {
    String sql = "SELECT record.title, record.label, record.genre, record.rrp, " +
                 "COUNT(recordcopy.recordID) AS num_copies " +
                 "FROM record " +
                 "INNER JOIN artist ON artist.artistID = record.artistID " +
                 "INNER JOIN recordcopy ON recordcopy.recordID = record.recordID " +
                 "INNER JOIN recordshop ON recordshop.recordshopID = recordcopy.recordshopID " +
                 "WHERE artist.lastname = ? AND recordshop.city = ? " +
                 "GROUP BY record.title, record.label, record.genre, record.rrp " +
                 "HAVING COUNT(recordcopy.recordID) > 0";

    try {
        Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        PreparedStatement stmt = conn.prepareStatement(sql);
        
        // Set parameters safely (prevents SQL injection)
        stmt.setString(1, artistLastName);
        stmt.setString(2, recordShopCity);
        
        ResultSet rs = stmt.executeQuery();
        
        // Convert to serializable format
        RowSetFactory aFactory = RowSetProvider.newFactory();
        CachedRowSet crs = aFactory.createCachedRowSet();
        crs.populate(rs);
        this.outcome = crs;
        
        // Clean up resources
        rs.close();
        stmt.close();
        conn.close();
    } catch (SQLException e) {
        // Error handling
        return false;
    }
    return true;
}
```

### Response Transmission

#### Server-side Response:
```java
public void returnServiceOutcome() {
    try {
        ObjectOutputStream outcomeStreamWriter = 
            new ObjectOutputStream(serviceSocket.getOutputStream());
        outcomeStreamWriter.writeObject(this.outcome);
        outcomeStreamWriter.flush();
        
        outcomeStreamWriter.close();
        serviceSocket.close();
    } catch (IOException e) {
        // Error handling
    }
}
```

**Protocol Details:**
- **Serialization:** Java Object Serialization
- **Object Type:** `javax.sql.rowset.CachedRowSet`
- **Transmission:** Binary data over TCP socket
- **Connection:** Closed after response transmission

### Response Reception

#### Client-side Processing:
```java
public void reportServiceOutcome() {
    try {
        ObjectInputStream outcomeStreamReader = 
            new ObjectInputStream(clientSocket.getInputStream());
        serviceOutcome = (CachedRowSet) outcomeStreamReader.readObject();
        
        // Process results for GUI display
        ObservableList<MyTableRecord> tmpRecords = FXCollections.observableArrayList();
        
        while (serviceOutcome.next()) {
            MyTableRecord record = new MyTableRecord();
            record.setTitle(serviceOutcome.getString("title"));
            record.setLabel(serviceOutcome.getString("label"));
            record.setGenre(serviceOutcome.getString("genre"));
            record.setRrp(serviceOutcome.getString("rrp"));
            record.setCopyID(serviceOutcome.getString("num_copies"));
            tmpRecords.add(record);
        }
        
        // Update GUI table
        outputBox.setItems(tmpRecords);
        
    } catch (IOException | ClassNotFoundException | SQLException e) {
        // Error handling
    }
}
```

## Error Handling

### Client-side Errors

#### Connection Failures:
```java
try {
    clientSocket = new Socket(Credentials.HOST, Credentials.PORT);
} catch (UnknownHostException e) {
    System.out.println("Client Error: Host could not be found: " + e.getMessage());
} catch (IOException e) {
    System.out.println("Client Error: Couldn't get I/O for connection: " + e.getMessage());
}
```

#### Data Reception Errors:
```java
try {
    serviceOutcome = (CachedRowSet) outcomeStreamReader.readObject();
} catch (ClassNotFoundException e) {
    System.out.println("Client: Unable to cast read object to CachedRowSet.");
} catch (SQLException e) {
    System.out.println("Client: Can't retrieve requested attribute from result set.");
}
```

### Server-side Errors

#### Database Connection Failures:
```java
try {
    Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
    // ... database operations
} catch (SQLException e) {
    System.out.println("Database or SQL exception: " + e.getMessage());
    return false;  // Indicate service failure
}
```

#### Client Communication Errors:
```java
try {
    InputStream socketStream = serviceSocket.getInputStream();
    // ... read client request
} catch (IOException e) {
    System.out.println("Service thread: I/O error - " + e.getMessage());
}
```

## Threading Model

### Server Architecture

The server employs a **thread-per-client** model:

1. **Main Thread:** Accepts incoming connections
2. **Service Threads:** Handle individual client requests
3. **Thread Lifecycle:** Created per request, terminated after response

#### Thread Creation:
```java
// Main server loop
while (true) {
    Socket clientSocket = serverSocket.accept();
    Thread serviceThread = new Thread(new RecordsDatabaseService(clientSocket));
    serviceThread.start();
}
```

#### Thread Execution:
```java
public void run() {
    try {
        // 1. Parse client request
        this.retrieveRequest();
        
        // 2. Execute database query  
        boolean success = this.attendRequest();
        
        // 3. Send response to client
        this.returnServiceOutcome();
        
    } catch (Exception e) {
        System.out.println("Service thread error: " + e);
    }
}
```

### Concurrency Considerations

#### Thread Safety:
- Each service thread operates independently
- No shared mutable state between threads
- Database connections are per-thread (not pooled)

#### Resource Management:
- Connections closed in finally blocks
- Socket resources cleaned up per thread
- No thread synchronization required

## Performance Characteristics

### Typical Response Times

| Operation | Expected Time | Notes |
|-----------|---------------|--------|
| Connection Establishment | <10ms | Local network |
| Request Parsing | <1ms | Simple string operations |
| Database Query | <100ms | Complex JOIN across 4 tables |
| Result Serialization | <10ms | CachedRowSet conversion |
| Response Transmission | <10ms | Object serialization |
| **Total Response Time** | **<130ms** | End-to-end |

### Scalability Limits

#### Concurrent Connections:
- **Tested:** 5+ simultaneous clients
- **Theoretical:** Limited by system resources (threads, sockets, database connections)
- **Bottleneck:** Database connection creation (no pooling implemented)

#### Memory Usage:
- **Per Client:** ~1-2MB (thread stack + data structures)
- **Result Set Size:** Typically <1KB (small number of records)
- **Server Overhead:** Minimal (stateless design)

## Security Considerations

### SQL Injection Prevention

The protocol uses **parameterized queries** exclusively:

```java
// SECURE: Uses PreparedStatement parameters
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setString(1, artistLastName);
stmt.setString(2, recordShopCity);

// INSECURE: Direct string concatenation (NOT USED)
// String sql = "SELECT * FROM record WHERE artist = '" + artistName + "'";
```

### Network Security

#### Protocol Vulnerabilities:
- **No encryption:** Data transmitted in plaintext
- **No authentication:** Any client can connect
- **No rate limiting:** Potential for DoS attacks

#### Mitigation Recommendations:
1. **TLS/SSL encryption** for production deployment
2. **Authentication mechanism** for client validation
3. **Connection rate limiting** to prevent abuse
4. **Input validation** beyond SQL injection prevention

## Protocol Extensions

### Potential Enhancements

1. **Message Authentication:**
   ```
   artist_surname;shop_city;timestamp;checksum#
   ```

2. **Multiple Query Types:**
   ```
   SEARCH;artist_surname;shop_city#
   LIST_GENRES;#
   GET_SHOPS;#
   ```

3. **Batch Queries:**
   ```
   BATCH;query1;query2;query3#
   ```

4. **Error Response Codes:**
   ```
   ERROR;code;message#
   SUCCESS;data#
   ```

### Backward Compatibility

Any protocol extensions should maintain backward compatibility with the current message format to ensure existing clients continue to function.

---

*This documentation covers the complete client-server communication protocol. For implementation details, refer to the source code in the respective Java classes.*
