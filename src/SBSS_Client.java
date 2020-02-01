
import java.io.*;
import java.net.*;
import java.util.Scanner;

/*
Project members
Member 1: Mohammad Yahya Alghafli (1741679)
Member 2: Mishary Abdullah Alshahrani (1535870)
*/

public class SBSS_Client {

    public static void main(String[] args) {
        try (Socket serverSocket = new Socket("127.0.0.1", 1500)){
            DataOutputStream send = new DataOutputStream(serverSocket.getOutputStream()); //This object will be used to send the command to the server
            DataInputStream receive = new DataInputStream(serverSocket.getInputStream()); //This object will be used to read the feedback from the server
            Scanner readConsole = new Scanner(System.in);
            String command = "";
            while (!command.equalsIgnoreCase("exit")) {
                System.out.print("Write a command: ");
                command = readConsole.nextLine();
                send.writeUTF(command);
                System.out.println("Server response: " + receive.readUTF());
            }
            
            //the end of the program
            send.close();
            receive.close();
        } catch (IOException e) {
            System.out.println(" >> Error: The IP address or the port number are incorrect!!!");
            System.exit(0);
        }
    }
    
}
