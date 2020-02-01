
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

/*
Project members
Member 1: Mohammad Yahya Alghafli (1741679)
Member 2: Mishary Abdullah Alshahrani (1535870)
 */
/**
 * This class will make a database for students' account and books Students'
 * account will be saved in this format User username, password Books will be in
 * this format Book ID, bookName, owner, status
 * @author m7md2
 */
public class ServerDataBase {

    private File database;

    public ServerDataBase() {
        this.database = new File("database.txt");
    }

    public boolean addAccount(String username, String password) {
        if (isAccountExists(username)) {
            return false;
        }
        try (PrintWriter write = new PrintWriter(new FileOutputStream(database, true))) {
            // We used FileOutputStream class to open the database file in Open mode to append the current file
            write.println("User " + username + ", " + password);
        } catch (FileNotFoundException e) {
            System.out.println(" >> Error: the database file not found...");
            System.out.println(" >> method addAccount()");
            System.exit(0);
        }
        return true;
    }

    public boolean uploadBook(String username, String bookName) {
        if (!isAccountExists(username)) {
            return false;
        }
        try (PrintWriter write = new PrintWriter(new FileOutputStream(database, true))) {
            write.println("Book " + generateID() + ", " + bookName + ", " + username + ", " + true); // We will initialize the status with true which means available
        } catch (FileNotFoundException e) {
            System.out.println(" >> Error: the database file not found...");
            System.out.println(" >> method uploadBook()");
            System.exit(0);
        }
        return true;
    }

    public boolean isAccountExists(String username) {
        try (Scanner read = new Scanner(database)) {
            read.useDelimiter("[ ,\r\n]+"); // We changed the default delimiter to whitespace and comma
            String token = "";
            while (read.hasNext()) {
                token = read.next();
                if (token.equalsIgnoreCase("User")) {
                    if (username.equalsIgnoreCase(read.next())) {
                        return true;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(" >> Error: the database file not found...");
            System.out.println(" >> mothod isAccountExists()");
            System.exit(0);
        }
        return false;
    }

    public int generateID() {
        int ID = 0;
        try(Scanner read = new Scanner(database)) {
            read.useDelimiter("[ ,\r\n]+");
            while(read.hasNext()){
                if (read.next().equalsIgnoreCase("Book")) {
                    int newBookID = read.nextInt();
                    if (newBookID >= ID) {
                        ID = newBookID + 1; // We will update the current ID
                    }
                }
            }
            return ID;
        } catch (FileNotFoundException e) {
            System.out.println(" >> Error: the database file not found...");
            System.out.println(" >> method generateID()");
            System.exit(0);
        }
        return ID;
    }
    
    public String findBook(int ID) {
        String book = "";
        try (Scanner read = new Scanner(database)) {
            read.useDelimiter("[ ,\r\n]+");
            while(read.hasNext()) {
                if (read.next().equalsIgnoreCase("Book")) {
                    if (ID == read.nextInt()) {
                        book = read.nextLine();
                        return book;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(" >> Error: the database file not found...");
            System.out.println(" >> method findBook()");
            System.exit(0);
        }
        
        return book;
    }
    
    /**
     * This method will find an account and return its data in the following format username, password
     * @param username
     * @return 
     */
    public String findAccount (String username) {
        try (Scanner read = new Scanner(database)) {
            read.useDelimiter("[ ,\r\n]+");
            while (read.hasNext()){
                if (read.next().equalsIgnoreCase("User")) {
                    String tempAccount = read.next();
                    if (username.equalsIgnoreCase(tempAccount)) {
                        return tempAccount + read.nextLine();
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(" >> Error: the database file not found...");
            System.out.println(" >> method findAccount()");
            System.exit(0);
        }
        return "";
    }
    
    public String listBooks() {
        String booksList = "\r\nBooks List:\r\n";
        try (Scanner read = new Scanner(database)) {
            read.useDelimiter("[ ,\r\n]+");
            while(read.hasNext()){
                if (read.next().equalsIgnoreCase("Book")) {
                    String[] temp = read.nextLine().trim().split("[ ,\r\n]+");
                    booksList += temp[0] + " " + temp[1] + " " + temp[2];
                    if (!isReservedBook(Integer.valueOf(temp[0]))) {
                        booksList += " available\r\n";
                    } else {
                        booksList += " not available\r\n";
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(" >> Error: the database file not found...");
            System.out.println(" >> method listBooks");
            System.exit(0);
        }
        return booksList;
    }
    
    public boolean reserveBook(String username, int bookID){
        String bookData = findBook(bookID);
        if (bookData.equalsIgnoreCase("")) {
            return false;
        }
        
        int i = 0;
        while (!SBSS_Server.reservedBooks.isEmpty() && SBSS_Server.reservedBooks.get(i) != SBSS_Server.reservedBooks.getLast()) {
            if (SBSS_Server.reservedBooks.get(i).getBookId() == bookID) {
                return false;
            }
            i++;
        }
        
        if (!SBSS_Server.reservedBooks.isEmpty() && SBSS_Server.reservedBooks.getLast().getBookId() == bookID) {
            return false;
        } else {
            SBSS_Server.reservedBooks.add(new Reservation(username, bookID));
            SBSS_Server.saveReservationsUpdates();
            return true;
        }
        
//        String[] dataList = bookData.split("[ ,#]+");
//        if (dataList[dataList.length-1].equalsIgnoreCase("true")) {
//            return true;
//        } else {
//            return false;
//        }
    }
    
    public boolean isReservedBook(int bookId) {
        int i = 0;
        while(!SBSS_Server.reservedBooks.isEmpty() && SBSS_Server.reservedBooks.get(i) != SBSS_Server.reservedBooks.getLast()) {
            if (SBSS_Server.reservedBooks.get(i).getBookId() == bookId) {
                return true;
            }
            i++;
        }
        
        if (!SBSS_Server.reservedBooks.isEmpty() && SBSS_Server.reservedBooks.getLast().getBookId() == bookId) {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean deleteAccount(String username, String password) {
        boolean isDeleted = false;
        String newFileData = "";
        try (Scanner read = new Scanner(database)) {
            PrintWriter write = new PrintWriter(database);
            while (read.hasNext()) {
                String lineData = read.nextLine();
                String accountPattern = ".*" + username + ".*" + password + ".*";
                if (!lineData.matches(accountPattern)) {
                    newFileData += lineData + "\r\n";
                } else {
                    isDeleted = true;
                }
            }
            if (isDeleted) {
                write.print(newFileData);
                write.flush();
            }
            write.close();
        } catch (FileNotFoundException e) {
            System.out.println(" >> Error: the database file not found...");
            System.out.println(" >> method deleteAccount()");
            System.exit(0);
        }
        return isDeleted;
    }

}
