/* Μουστάκας Γεώργιος 321 / 2011 102
   Χατζηαναστασιάδης Μιχαήλ Μάριος 321 / 2011 176
   Σωτηρέλης Χρήστος 321 / 2012 182
*/
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.io.Serializable;

// Κλάση κράτησης μηνυμάτων
public class Message implements Serializable {
    private final String message;
    private final String timestamp;
    private final String username;

    // Constructor
    public Message(String message, String username) {
        this.message = message;
        this.username = username;

        
        // Για κάθε νέο μήνυμα, ο server αναλαμβάνει να τοποθετήσει
        // και τη χρονική στιγμή που ελήφθη το συγκεκριμένο μήνυμα
        // ώστε να εμφανιστεί στο Chat.
        // Αυτό γίνεται στον σερβερ και όχι στον client για να αποφύγουμε
        // τυχόν λάθη στο ρολόι του κάθε χρήστη
        
        Date curDate = new Date();
        SimpleDateFormat sdfu = new SimpleDateFormat("MMMM dd kk:mm:ss", Locale.ENGLISH);        
        this.timestamp = sdfu.format(curDate);
    }

    // Getters
    public String getMessage() {
        return this.message;
    }
    
    public String getTimestamp() {
        return this.timestamp;
    }
    
    public String getUsername() {
        return this.username;
    }
}
