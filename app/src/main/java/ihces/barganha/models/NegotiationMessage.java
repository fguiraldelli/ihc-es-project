package ihces.barganha.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

import utils.TimeFormatter;

public class NegotiationMessage {

    private int id;
    @SerializedName("id_usuario_remetente")
    private int senderId;
    private transient String senderName;
    @SerializedName("created_at")
    private Date messageTime;
    @SerializedName("mensagem")
    private String messageText;

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
        return messageTime;
    }

    public void setMessageTime(Date messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageTimeFormatted() {
        return TimeFormatter.getTimeFormatted(this.messageTime);
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
}
