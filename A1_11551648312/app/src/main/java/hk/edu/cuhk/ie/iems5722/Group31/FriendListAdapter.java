package hk.edu.cuhk.ie.iems5722.Group31;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class FriendListAdapter extends ArrayAdapter<FriendListModel> {
    private Context mContext;
    private int mResource;
    TextView textView;

    public FriendListAdapter(@NonNull Context context, int resource, @NonNull List<FriendListModel> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        FriendListModel friendListModel = getItem(position);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);
        textView = (TextView) convertView.findViewById(R.id.frienditem);
        textView.setText(friendListModel.name);


        return convertView;
    }
}
