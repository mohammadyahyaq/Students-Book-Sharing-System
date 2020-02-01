
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/*
Project members
Member 1: Mohammad Yahya Alghafli (1741679)
Member 2: Mishary Abdullah Alshahrani (1535870)
 */
public class ClientThread extends Thread {
    
    private Socket myClient;
    
    public ClientThread(Socket myClient) {
        this.myClient = myClient;
    }
    
    @Override
    public void run() {
        try (DataInputStream receive = new DataInputStream(myClient.getInputStream()); DataOutputStream send = new DataOutputStream(myClient.getOutputStream())) {
            System.out.println("Just connected to " + myClient.getRemoteSocketAddress());
            boolean isLogedIn = false;
            String command = "";
            while (!command.equalsIgnoreCase("exit")) {
                command = receive.readUTF(); //This will read the command from the client
                // I will use reguler expressions to check the type of the command
                if (command.matches("01;.*;.*#")) {
                    // In this case the command is create account
                    String username = command.split("[ ;#\r\n]+")[1];
                    String password = command.split("[ ;#]+")[2];
                    if (SBSS_Server.database.addAccount(username, password)) {
                        send.writeUTF("20;OK#");
                    } else {
                        send.writeUTF("21;Invalid user (or bad password)#");
                    }
                } else if (command.matches("02;.*;.*#")) {
                    // In this case the command is log in
                    String username = command.split("[ ;#\r\n]+")[1];
                    String password = command.split("[ ;#\r\n]+")[2];
                    if (!isLogedIn && !SBSS_Server.loginUsers.contains(username) && SBSS_Server.database.isAccountExists(username)) {
                        isLogedIn = true;
                        send.writeUTF("20;OK#");
                    } else {
                        send.writeUTF("21;Invalid user (or bad password)#");
                    }
                } else if (command.matches("03;.*,.*#")) {
                    // In this case the command is upload book information
                    String username = command.split("[ ;#,\r\n]+")[1];
                    String bookName = command.split("[ ;,#\r\n]+")[2];
                    if (isLogedIn && SBSS_Server.database.uploadBook(username, bookName)) {
                        send.writeUTF("22;Information is uploaded successfully on the system#");
                    } else {
                        send.writeUTF("23; Invalid format#");
                    }
                } else if (command.matches("04;LISTBOOKS#")) {
                    // In this case the command is view list of books
                    String list = SBSS_Server.database.listBooks();
                    send.writeUTF(list);
                } else if (command.matches("05;.*;\\d*#")) {
                    // In this case the command is generate booking request
                    String username = command.split("[ ;#\r\n]+")[1];
                    int bookID = Integer.valueOf(command.split("[ ;#\r\n]+")[2]);
                    if (!isLogedIn) {
                        send.writeUTF("23; Invalid request#");
                    } else if (SBSS_Server.database.reserveBook(username, bookID)) {
                        // In this case the book is already reserved!!
                        send.writeUTF("25; This book is not available for reservation#");
                    } else {
                        SBSS_Server.reservedBooks.add(new Reservation(username, bookID)); // mark the book as reserved
                        send.writeUTF("25;Book has been reserved successfully #"); // send a feedback message to the client
                    }
                } else if (command.matches("06;.*;.*#")) {
                    // In this case the command is log off
                    String username = command.split("[ ;#\r\n]+")[1];
                    String password = command.split("[ ;#\r\n]+")[2];
                    if (!isLogedIn) {
                        send.writeUTF("23; Invalid request#");
                    } else if (SBSS_Server.loginUsers.contains(username)) {
                        String accountData = SBSS_Server.database.findAccount(username);
                        String realPassword = accountData.split("[ ,\r\n]+")[2];
                        if (password.equals(realPassword)) {
                            send.writeUTF("26;username is logged off successfully#");
                        } else {
                            send.writeUTF("21;Invalid user (or bad password)#");
                        }
                        
                    } else {
                        send.writeUTF("21;Invalid user (or bad password)#");
                    }
                } else if (command.matches("07;.*;.*#")) {
                    // In this case the command is delete account
                    String username = command.split("[ ;#\r\n]+")[1];
                    String password = command.split("[ ;#\r\n]+")[2];
                    if (SBSS_Server.database.deleteAccount(username, password)) {
                        send.writeUTF("27;" + username + "#");
                    } else {
                        send.writeUTF("21;Invalid user (or bad password)#");
                    }
                } else if (!command.equalsIgnoreCase("exit")) {
                    // In this case the command is not in correct form!
                    send.writeUTF("29;Invalid message#");
                } else {
                    send.writeUTF("Connection closed...");
                    System.out.println("Connection with " + myClient.getRemoteSocketAddress() + " closed");
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println(" >> Error: Incorrect IP or port number...");
            System.out.println(e.getMessage());
            System.exit(0);
        }
    }
}
