
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.util.LinkedList;
import java.util.Scanner;

/*
Project members
Member 1: Mohammad Yahya Alghafli (1741679)
Member 2: Mishary Abdullah Alshahrani (1535870)
*/

public class SBSS_Server {

    public static ServerDataBase database = new ServerDataBase();
    public static LinkedList<String> loginUsers = new LinkedList<String>(); // this liked list will contain all the loged in users (if the server shut down all users will loged off immediately)
    public static LinkedList<Reservation> reservedBooks = new LinkedList<Reservation>(); // this liked list will save the reserved books
    
    public static void main(String[] args) {
        try (ServerSocket mySocket = new ServerSocket(1500)) {
            mySocket.setReuseAddress(true); //this statement to reuse the same socket after rerunning the program
            constructLinkedList();
            System.out.println("Waiting for client on port " + mySocket.getLocalPort());
            while (true) {
                ClientThread clientThread = new ClientThread(mySocket.accept()); // Everytime we have a new client we will create a thread for him
                clientThread.start();
            }
        } catch (IOException e) {
            System.out.println(" >> Error: port number is already used...");
            System.exit(0);
        }
    }
    
    /**
     * This method for will construct the liked list from reservation file
     */
    public static void constructLinkedList (){
        try (Scanner read = new Scanner("reservations.txt")) {
            String word;
            while(read.hasNext()){
                word = read.next();
                if (word.equalsIgnoreCase("Reserve")) {
                    reservedBooks.add(new Reservation(read.next(), read.nextInt()));
                }
            }
        }
    }
    
    /**
     * when we made any change in the reservations we will update the reservation file using this method
     */
    public static void saveReservationsUpdates () {
        try (PrintWriter write = new PrintWriter("reservations.txt")) {
            int i = 0;
            while (!reservedBooks.isEmpty() && reservedBooks.get(i) != reservedBooks.getLast()) {
                write.println("Reserve " + reservedBooks.get(i).getOwner() + " " + reservedBooks.get(i).getBookId());
                i++;
            }
            
            write.println("Reserve " + reservedBooks.getLast().getOwner() + " " + reservedBooks.getLast().getBookId());
            
        } catch (FileNotFoundException e) {
            System.out.println(" Error: reservation file not found...");
            System.exit(0);
        }
    }

}
