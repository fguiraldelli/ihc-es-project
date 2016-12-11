package ihces.barganha;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import ihces.barganha.models.NegotiationMessage;

public class MessagesAdapter extends BaseAdapter {

    private Context context;
    private List<NegotiationMessage> messages;
    private final LayoutInflater inflater;

    public MessagesAdapter(Context context, NegotiationMessage[] messages) {
        this.context = context;
        this.messages = Arrays.asList(messages);
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.messages.size();
    }

    @Override
    public Object getItem(int position) {
        return this.messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return this.messages.get(position).getId();
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

        NegotiationMessage current = messages.get(position);

        tvSender.setText(current.getSenderName());
        tvTime.setText(current.getMessageTimeFormatted());
        tvMessage.setText(current.getMessageText());

        return customView;
    }
}
