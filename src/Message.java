
import java.util.Date;

/**
 *
 * @author Christos Sotirelis
 */
public class Message {
    private final String message;
    private final Date timestamp;
    
    public Message(String message, Date timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }
    
    public Message(String message) {
        this.message = message;
        // TODO: Get time now as timestamp
        this.timestamp = null;
    }
    
    public Message() {
        this.message = null;
        this.timestamp = null;
    }
    
    public String getMessage() {
        return this.message;
    }
    
    public Date getTimestamp() {
        return this.timestamp;
    }
}
