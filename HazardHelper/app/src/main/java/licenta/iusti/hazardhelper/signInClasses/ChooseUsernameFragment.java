package licenta.iusti.hazardhelper.signInClasses;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import licenta.iusti.hazardhelper.MainActivity;
import licenta.iusti.hazardhelper.R;
import licenta.iusti.hazardhelper.domain.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseUsernameFragment extends Fragment implements View.OnClickListener {


    public static final String USERS_DB_KEY = "users";
    EditText usernameTBox;

    public ChooseUsernameFragment() {
        // Required empty public constructor
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_choose_username,null);
        view.findViewById(R.id.username_choose_button).setOnClickListener(this);
        usernameTBox = (EditText)view.findViewById(R.id.username_choose_text);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.username_choose_button){
            Log.i("TAG","STARTED : Saving user with username in database");

            String username = usernameTBox.getText().toString();

            FirebaseDatabase.getInstance().getReference().child(USERS_DB_KEY).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                    setValue(new User(FirebaseAuth.getInstance().getCurrentUser().getUid(),username));
            Log.i("TAG","FINISHED : Saving user with username in database");
            getActivity().finish();
            startActivity(new Intent(getActivity(),MainActivity.class));

        }

    }
}
