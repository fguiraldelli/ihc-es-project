package ihces.barganha.models;

import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import utils.TimeFormatter;

public class NegotiationMessage {

    private int id;
    @SerializedName("id_usuario_remetente")
    private int senderId;
    private transient String senderName;
    @SerializedName("created_at")
    private String createdAt;
    private transient Date messageTime;
    @SerializedName("mensagem")
    private String messageText;

    private static DateFormat m_ISO8601Local = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

    public NegotiationMessage(int id, int senderId, String messageText) {
        this.id = id;
        this.senderId = senderId;
        this.messageText = messageText;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public Date getMessageTime() {
        if (messageTime == null) {
            try {
                messageTime = m_ISO8601Local.parse(createdAt);
            } catch (ParseException e) {
                messageTime = Calendar.getInstance().getTime();
                Log.e("NegotiationMessage", "Error parsing date:", e);
            }
        }

        return messageTime;
    }

    public void setMessageTime(Date messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageTimeFormatted() {
        return TimeFormatter.getTimeFormatted(this.getMessageTime());
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
}
