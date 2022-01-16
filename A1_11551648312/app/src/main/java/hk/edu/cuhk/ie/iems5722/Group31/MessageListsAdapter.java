package hk.edu.cuhk.ie.iems5722.Group31;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

public class MessageListsAdapter extends ArrayAdapter<MessageModel> {

    private Context mContext;
    int mResource;

    public MessageListsAdapter(@NonNull Context context, int resource, @NonNull List<MessageModel> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        String message = getItem(position).getMessage();
        String time_now = getItem(position).getTime_now();
        String name = getItem(position).getName();

        MessageModel messageModel = new MessageModel(message, time_now,name);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);
        TextView chat_time = (TextView) convertView.findViewById(R.id.chat_time);
        TextView chat_content = (TextView) convertView.findViewById(R.id.chat_content);

        chat_content.setText(messageModel.name + ": " + messageModel.message);
        chat_time.setText(messageModel.time_now);

        System.out.println("================="+messageModel.message+"===============");

        return convertView;
    }
}
