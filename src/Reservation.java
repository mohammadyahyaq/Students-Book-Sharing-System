
/*
Project members
Member 1: Mohammad Yahya Alghafli (1741679)
Member 2: Mishary Abdullah Alshahrani (1535870)
*/

public class Reservation {
    private String owner;
    private int bookId;
    
    public Reservation (String username, int bookId) {
        this.owner = username;
        this.bookId = bookId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }    
}
