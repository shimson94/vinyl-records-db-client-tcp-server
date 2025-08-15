# Database Setup Guide

This guide walks through setting up the PostgreSQL database required for the Vinyl Records Query System.

## Prerequisites

- PostgreSQL 14 or higher installed
- Administrator access to create databases
- Basic familiarity with SQL commands

## Installation Steps

### 1. Install PostgreSQL

#### Windows:
1. Download PostgreSQL from [postgresql.org](https://www.postgresql.org/download/windows/)
2. Run the installer and follow the setup wizard
3. Remember the password you set for the `postgres` user
4. Default port is usually 5432

#### macOS:
```bash
# Using Homebrew
brew install postgresql
brew services start postgresql

# Or download from postgresql.org
```

#### Linux (Ubuntu/Debian):
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

### 2. Create Database User

Connect to PostgreSQL as administrator:

```bash
# Windows
psql -U postgres

# Linux/macOS
sudo -u postgres psql
```

Create a new user for the application:
```sql
-- Create user (replace 'your_username' and 'your_password')
CREATE USER your_username WITH PASSWORD 'your_password';

-- Grant necessary privileges
ALTER USER your_username CREATEDB;
GRANT ALL PRIVILEGES ON DATABASE postgres TO your_username;
```

### 3. Create Application Database

```sql
-- Create the database
CREATE DATABASE "FSAD2024_Records";

-- Grant permissions to your user
GRANT ALL PRIVILEGES ON DATABASE "FSAD2024_Records" TO your_username;

-- Connect to the new database
\c "FSAD2024_Records"
```

### 4. Import Database Schema

Exit PostgreSQL shell and import the provided schema:

```bash
# Navigate to your project directory
cd /path/to/vinyl-records-db-client-tcp-server

# Import the database schema
psql -U your_username -d FSAD2024_Records -f records.sql
```

### 5. Verify Installation

Connect to the database and verify tables were created:

```bash
psql -U your_username -d FSAD2024_Records
```

```sql
-- List all tables
\dt

-- Check table contents
SELECT * FROM artist LIMIT 5;
SELECT * FROM record LIMIT 5;
SELECT * FROM recordshop LIMIT 5;
SELECT * FROM recordcopy LIMIT 10;

-- Verify data relationships
SELECT COUNT(*) as total_artists FROM artist;
SELECT COUNT(*) as total_records FROM record;
SELECT COUNT(*) as total_shops FROM recordshop;
SELECT COUNT(*) as total_copies FROM recordcopy;
```

Expected output should show:
- **artist** table with various musicians
- **record** table with album information
- **recordshop** table with shop locations
- **recordcopy** table with inventory data

### 6. Test Sample Query

Verify the application's core functionality with a test query:

```sql
-- Test the main application query
SELECT record.title, record.label, record.genre, record.rrp, 
       COUNT(recordcopy.recordID) AS num_copies
FROM record 
INNER JOIN artist ON artist.artistID = record.artistID
INNER JOIN recordcopy ON recordcopy.recordID = record.recordID
INNER JOIN recordshop ON recordshop.recordshopID = recordcopy.recordshopID
WHERE artist.lastname = 'Beyonce' AND recordshop.city = 'London'
GROUP BY record.title, record.label, record.genre, record.rrp
HAVING COUNT(recordcopy.recordID) > 0;
```

This should return approximately 8 records for Beyonce in London shops.

## Configuration

### Update Application Credentials

Edit `src/Credentials.java` with your database settings:

```java
public class Credentials {
    //JDBC connection
    public static final String USERNAME = "your_username";        // Your PostgreSQL username
    public static final String PASSWORD = "your_password";        // Your PostgreSQL password  
    public static final String URL = "jdbc:postgresql://localhost:5432/FSAD2024_Records";
    
    //Client-server connection
    public static final String HOST = "127.0.0.1";               // Server host
    public static final int PORT = 9994;                         // Server port (not PostgreSQL port)
}
```

### Connection String Format

The JDBC URL format is:
```
jdbc:postgresql://[host]:[port]/[database_name]
```

For local development:
```
jdbc:postgresql://localhost:5432/FSAD2024_Records
```

## Troubleshooting

### Common Issues

#### 1. Connection Refused
```
Error: Connection refused
```
**Solution:** Ensure PostgreSQL service is running
```bash
# Windows
net start postgresql-x64-14

# Linux/macOS
sudo systemctl start postgresql
```

#### 2. Authentication Failed
```
Error: password authentication failed for user
```
**Solution:** 
- Verify username/password in `Credentials.java`
- Reset user password if necessary:
```sql
ALTER USER your_username PASSWORD 'new_password';
```

#### 3. Database Does Not Exist
```
Error: database "FSAD2024_Records" does not exist
```
**Solution:** Create the database as shown in Step 3

#### 4. Tables Not Found
```
Error: relation "artist" does not exist
```
**Solution:** Import the schema file as shown in Step 4

#### 5. Permission Denied
```
Error: permission denied for table
```
**Solution:** Grant proper permissions:
```sql
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO your_username;
GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO your_username;
```

### Performance Optimization

For better performance with larger datasets:

```sql
-- Create indexes on frequently queried columns
CREATE INDEX idx_artist_lastname ON artist(lastname);
CREATE INDEX idx_recordshop_city ON recordshop(city);
CREATE INDEX idx_recordcopy_recordid ON recordcopy(recordID);
CREATE INDEX idx_recordcopy_shopid ON recordcopy(recordshopID);
```

### Security Considerations

1. **Create dedicated user** - Don't use the `postgres` superuser
2. **Limit permissions** - Grant only necessary database privileges
3. **Use strong passwords** - Especially for production environments
4. **Network security** - Consider firewall rules for remote connections

## Database Schema Overview

### Table Relationships

```
artist (1) ──── (N) record (1) ──── (N) recordcopy (N) ──── (1) recordshop
  │                   │                      │                      │
artistID            recordID              recordcopyID          recordshopID
firstname           title                 recordID              name
lastname            label                 onLoan                city
                    genre                                       
                    rrp                                         
                    artistID                                    
```

### Sample Data Structure

- **Artists:** Beyonce, Ed Sheeran, Taylor Swift, etc.
- **Records:** Various albums with titles, genres, prices
- **Shops:** Located in London, Cardiff, Manchester, etc.
- **Copies:** Inventory tracking with loan status

This setup provides a realistic dataset for testing the application's functionality with various query scenarios.

---

*For additional support or questions about the database setup, refer to the main README.md or check the PostgreSQL official documentation.*
