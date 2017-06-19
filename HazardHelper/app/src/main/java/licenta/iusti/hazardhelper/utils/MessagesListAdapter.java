package licenta.iusti.hazardhelper.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import licenta.iusti.hazardhelper.R;
import licenta.iusti.hazardhelper.domain.Message;

/**
 * Created by Iusti on 6/17/2017.
 */

public class MessagesListAdapter extends BaseAdapter {

    private final ArrayList<Message> messages;
    private final Context context;

    public MessagesListAdapter(ArrayList<Message> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Message getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;
        LayoutInflater inflater = LayoutInflater.from(context);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_chat_item, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }
        final Message currentListData = getItem(position);
        mViewHolder.tv_username.setText(currentListData.getUsername());
        mViewHolder.tv_message.setText(currentListData.getContent());
        mViewHolder.tv_date.setText(currentListData.getDate());

        return convertView;

    }
    public void add(Message message){
        if(!messages.contains(message)){
            messages.add(message);
            notifyDataSetChanged();
        }
    }

    private class MyViewHolder {
        TextView tv_username;
        TextView tv_message;
        TextView tv_date;

        public MyViewHolder(View view) {
            this.tv_username = (TextView) view.findViewById(R.id.text_view_username);
            this.tv_message = (TextView) view.findViewById(R.id.text_view_message);
            this.tv_date = (TextView) view.findViewById(R.id.text_view_date);
        }
    }
}
