package ihces.barganha;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import ihces.barganha.models.NegotiationMessage;
import ihces.barganha.models.User;
import ihces.barganha.rest.UserService;

public class MessagesAdapter extends BaseAdapter {

    private Context context;
    private NegotiationMessage[] messages;
    private final LayoutInflater inflater;

    public MessagesAdapter(Context context) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setData(NegotiationMessage[] messages) {
        this.messages = messages;
    }

    public void addData(NegotiationMessage message) {
        int currentSize = this.messages.length;
        NegotiationMessage[] newArray = new NegotiationMessage[currentSize + 1];
        System.arraycopy(this.messages, 0, newArray, 0, currentSize);
        newArray[currentSize] = message;
        this.messages = newArray;
    }

    @Override
    public int getCount() {
        return this.messages.length;
    }

    @Override
    public Object getItem(int position) {
        return this.messages[position];
    }

    @Override
    public long getItemId(int position) {
        return this.messages[position].getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View customView;

        if (convertView == null) {
            customView = inflater.inflate(R.layout.message_row, parent, false);
        } else {
            customView = convertView;
        }

        TextView tvSender = (TextView) customView.findViewById(R.id.tv_sender_name);
        TextView tvTime = (TextView) customView.findViewById(R.id.tv_message_time);
        TextView tvMessage = (TextView) customView.findViewById(R.id.tv_message_text);

        NegotiationMessage current = messages[position];

        tvSender.setText(current.getSenderName());
        tvTime.setText(current.getMessageTimeFormatted());
        tvMessage.setText(current.getMessageText());

        return customView;
    }
}
