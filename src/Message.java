
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.io.Serializable;
/**
 *
 * @author Christos Sotirelis
 */
public class Message implements Serializable {
    private final String message;
    private final String timestamp;
    private final String username;

    public Message(String message, String username) {
        this.message = message;
        this.username = username;
        // TODO: Get time now as timestamp
        Date curDate = new Date();
        SimpleDateFormat sdfu = new SimpleDateFormat("MMMM dd kk:mm:ss", Locale.ENGLISH);        
        this.timestamp = sdfu.format(curDate);
    }

    
    public String getMessage() {
        return this.message;
    }
    
    public String getTimestamp() {
        return this.timestamp;
    }
}
