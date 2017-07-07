package licenta.iusti.hazardhelper.features;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import licenta.iusti.hazardhelper.R;
import licenta.iusti.hazardhelper.domain.Message;
import licenta.iusti.hazardhelper.domain.User;
import licenta.iusti.hazardhelper.signInClasses.ChooseUsernameFragment;
import licenta.iusti.hazardhelper.utils.MessagesListAdapter;
import licenta.iusti.hazardhelper.utils.MessagesWatcher;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment implements MessagesWatcher.MessagesUpdateListener, View.OnClickListener {


    MessagesWatcher watcher;
    private ListView mMessagesList;
    private MessagesListAdapter mAdapter;
    private EditText mContent;
    private String itemKey;

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.chat_layout,null);
        mMessagesList = (ListView) view.findViewById(R.id.messages_list);
        mAdapter = new MessagesListAdapter(new ArrayList<Message>(), getActivity());
        mMessagesList.setDividerHeight(0);
        mMessagesList.setAdapter(mAdapter);



        view.findViewById(R.id.send_message_button).setOnClickListener(this);

        mContent = (EditText)view.findViewById(R.id.message_content);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        watcher = new MessagesWatcher(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        watcher.watchCurrentTripModifications(itemKey);

    }

    @Override
    public void onStop() {
        super.onStop();
        watcher.destroyWatcher();

    }




    @Override
    public void onMessagesUpdated(Message message) {
        mAdapter.add(message);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_message_button:
                sendMessage();

        }
    }

    private void sendMessage() {
        final Message message = new Message();
        message.setContent(mContent.getText().toString());
        DateFormat dateFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
        Date date = new Date();
        message.setDate(dateFormat.format(date));

        FirebaseDatabase.getInstance().getReference().child(ChooseUsernameFragment.USERS_DB_KEY).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                message.setUsername(snapshot.getValue().toString());
                FirebaseDatabase.getInstance().getReference(MessagesWatcher.MESSAGES_DB_KEY).child(itemKey).push().setValue(message);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mContent.getText().clear();
    }
}
