/*
 * RecordsDatabaseServer.java
 *
 * The server main class.
 * This server provides a service to access the Records database.
 *
 * author: <2456077>
 *
 */

import java.net.Socket;
import java.net.ServerSocket;
import java.net.InetAddress;



public class RecordsDatabaseServer {

    private int thePort = 0;
    private String theIPAddress = null;
    private ServerSocket serverSocket =  null;

    //Support for closing the server
    //private boolean keypressedFlag = false;


    //Class constructor
    public RecordsDatabaseServer(){
        thePort = Credentials.PORT;
        theIPAddress = Credentials.HOST;

        System.out.println("Server: Initializing server socket at " + theIPAddress + " with listening port " + thePort);
        System.out.println("Server: Exit server application by pressing Ctrl+C (Windows or Linux) or Opt-Cmd-Shift-Esc (Mac OSX)." );
        try {
            serverSocket = new ServerSocket(thePort);
            System.out.println("Server: Server at " + theIPAddress + " is listening on port : " + thePort);
            while (true){
                System.out.println("Server: Waiting for client connection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Server: Connection established with " + clientSocket.getInetAddress());

                Thread clientHandlerThread = new Thread(new RecordsDatabaseService(clientSocket));
                clientHandlerThread.start();
            }
        } catch (Exception e){
            System.out.println(e);
            System.exit(1);
        }
    }

    //Runs the service loop
    public void executeServiceLoop()
    {
        System.out.println("Server: Start service loop.");
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Server: Client connected from " + clientSocket.getInetAddress());
                Thread serviceThread = new Thread(new RecordsDatabaseService(clientSocket));
                serviceThread.start();
            }
        } catch (Exception e){
            System.out.println(e);
        }
        System.out.println("Server: Finished service loop.");
        System.exit(0);
    }


/*
	@Override
	protected void finalize() {
		//If this server has to be killed by the launcher with destroyForcibly
		//make sure we also kill the service threads.
		System.exit(0);
	}
*/

    public static void main(String[] args){
        RecordsDatabaseServer server=new RecordsDatabaseServer();
        server.executeServiceLoop();
        System.out.println("Server: Finished.");
        System.exit(0);
    }
}