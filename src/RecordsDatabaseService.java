/*
 * RecordsDatabaseService.java
 *
 * The service threads for the records database server.
 * This class implements the database access service, i.e. opens a JDBC connection
 * to the database, makes and retrieves the query, and sends back the result.
 *
 * author: <2456077>
 *
 */

import java.io.InputStream;
import java.io.InputStreamReader;
// import java.io.OutputStream;
// import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;

import java.net.Socket;

// import java.util.StringTokenizer;

import java.sql.*;
import javax.sql.rowset.*;

//Direct import of the classes CachedRowSet and CachedRowSetImpl will fail because
//these classes are not exported by the module. Instead, one needs to import
//javax.sql.rowset.* as above.


public class RecordsDatabaseService extends Thread {

    private Socket serviceSocket = null;
    private String[] requestStr = new String[2]; //One slot for artist's name and one for recordshop's name.
    private ResultSet outcome = null;

    //JDBC connection
    private String USERNAME = Credentials.USERNAME;
    private String PASSWORD = Credentials.PASSWORD;
    private String URL = Credentials.URL;


    //Class constructor
    public RecordsDatabaseService(Socket aSocket) {
        serviceSocket = aSocket;
    }


    //Retrieve the request from the socket
    public String[] retrieveRequest() {
        this.requestStr[0] = "";
        this.requestStr[1] = "";
        try {
            InputStream socketStream = serviceSocket.getInputStream();
            InputStreamReader socketReader = new InputStreamReader(socketStream);
            StringBuffer stringBuffer = new StringBuffer();
            char x;
            while (true) {
                System.out.println("Service thread: reading characters ");
                x = (char) socketReader.read();
                System.out.println("Service thread: " + x);
                if (x == '#') break;
                stringBuffer.append(x);
            }
            String incomingRequest = stringBuffer.toString();
            String[] parts = incomingRequest.split(";");
            if (parts.length == 2) {
                this.requestStr[0] = parts[0];
                this.requestStr[1] = parts[1];
            } else {
                System.out.println("Service thread " + this.threadId() + ": Incorrect message format.");
            }
        } catch (IOException e) {
            System.out.println("Service thread " + this.threadId() + ": " + e);
        }
        return this.requestStr;
    }


    //Parse the request command and execute the query
    public boolean attendRequest() {
        boolean flagRequestAttended = true;

        this.outcome = null;
        String artistLastName = requestStr[0];
        String recordShopCity = requestStr[1];


        String sql = "SELECT record.title, record.label, record.genre, record.rrp, COUNT(recordcopy.recordID) AS num_copies " +
                "FROM record " +
                "INNER JOIN artist ON artist.artistID = record.artistID " +
                "INNER JOIN recordcopy ON recordcopy.recordID = record.recordID " +
                "INNER JOIN recordshop ON recordshop.recordshopID = recordcopy.recordshopID " +
                "WHERE artist.lastname = '" + artistLastName + "' AND recordshop.city = '" + recordShopCity + "' " +
                "GROUP BY record.title, record.label, record.genre, record.rrp;";



        try {
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            RowSetFactory aFactory = RowSetProvider.newFactory();
            CachedRowSet crs = aFactory.createCachedRowSet();
            crs.populate(rs);
            this.outcome = crs;

            rs.close();

        } catch (SQLException e) {
            System.out.println("Database or SQL exception: " + e.getMessage());
            flagRequestAttended = false;
        }
        return flagRequestAttended;
    }

    //Wrap and return service outcome
    public void returnServiceOutcome() {
        try {

            ObjectOutputStream outcomeStreamWriter = new ObjectOutputStream(serviceSocket.getOutputStream());
            outcomeStreamWriter.writeObject(this.outcome);
            outcomeStreamWriter.flush();

            System.out.println("Service thread " + this.threadId() + ": Service outcome returned; " + this.outcome);

            outcomeStreamWriter.close();
            serviceSocket.close();
            System.out.println("Service thread " + this.threadId() + ": Connection closed.");
        } catch (IOException e) {
            System.out.println("Service thread " + this.threadId() + ": " + e);
        }
    }


    //The service thread run() method
    public void run() {
        try {
            System.out.println("\n============================================\n");
            this.retrieveRequest();
            System.out.println("Service thread " + this.threadId() + ": Request retrieved: " + "artist->" + this.requestStr[0] + "; recordshop->" + this.requestStr[1]);

            boolean tmp = this.attendRequest();

            if (!tmp) System.out.println("Service thread " + this.threadId() + ": Unable to provide service.");
            this.returnServiceOutcome();

        } catch (Exception e) {
            System.out.println("Service thread " + this.threadId() + ": " + e);
        }
        System.out.println("Service thread " + this.threadId() + ": Finished service.");
    }
}