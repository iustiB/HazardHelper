package licenta.iusti.hazardhelper.utils;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import licenta.iusti.hazardhelper.domain.Message;


public class MessagesWatcher {

    public static final String MESSAGES_DB_KEY = "messages";

    public interface MessagesUpdateListener{
        void onMessagesUpdated(Message message);
    }
    private MessagesUpdateListener listener;
    private static DatabaseReference mDatabase;
    private static FirebaseUser user;


    ValueEventListener v = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for(DataSnapshot item : dataSnapshot.getChildren()){
                Message message = item.getValue(Message.class);
                message.setId(item.getKey());
                if(listener!=null) {
                    listener.onMessagesUpdated(message);
                }
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(MessagesWatcher.class.getSimpleName(), databaseError.getMessage());
        }
    };



    public MessagesWatcher(final MessagesUpdateListener listener){
        this.listener = listener;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public void destroyWatcher(){
        this.listener = null;
        mDatabase.removeEventListener(v);
    }

    public void watchCurrentTripModifications(String safepointID){
        mDatabase.child(MESSAGES_DB_KEY).child(safepointID).addValueEventListener(v);
    }
}
