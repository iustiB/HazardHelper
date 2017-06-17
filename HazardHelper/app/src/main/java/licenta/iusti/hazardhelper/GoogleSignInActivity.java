package licenta.iusti.hazardhelper;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import licenta.iusti.hazardhelper.domain.User;

public class GoogleSignInActivity extends AppCompatActivity implements GoogleSignInFragment.SignInListener {


    private GoogleSignInFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);
        fragment = new GoogleSignInFragment();
        fragment.setSignInListener(this);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,fragment).commit();

    }

    // [START on_start_add_listener]

    // [START onactivityresult]
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == GoogleSignInFragment.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                fragment.firebaseAuthWithGoogle(account);

                } else {

            }
        }
    }

    private void checkIfAlreadyExist() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean userExists = false;
                for (DataSnapshot item :dataSnapshot.getChildren()){
                    User user = item.getValue(User.class);
                    if(user.getUID()==FirebaseAuth.getInstance().getCurrentUser().getUid());
                    {
                        Log.e("E", "users table : already exists");
                        userExists=true;

                    }
                }
                if(userExists == false){  ///TODO: change to false when ready

                    startRegisterForNewUser();

                }else{
                    GoogleSignInActivity.this.finish();
                    startActivity(new Intent(GoogleSignInActivity.this,MainActivity.class));
                }
//                if(dataSnapshot.hasChild())
//                if(dataSnapshot.hasChild(UID)){
//                    // use "username" already exists
//                    Log.e("E","users table : already exists");
//                    // Let the user know he needs to pick another username.
//                } else {
//                    Log.e("E","users table : not exists");
//
//                    // User does not exist. NOW call createUserWithEmailAndPassword
//                    // Your previous code here.
//
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void startRegisterForNewUser() {
        getSupportFragmentManager().beginTransaction().replace(R.id.container,new ChooseUsernameFragment()).commit();

    }
// [END onactivityresult]

    // [START auth_with_google]


    private void showProgressDialog() {
    }
// [END auth_with_google]

    // [START signin]


    private void hideProgressDialog() {
    }


    @Override
    public void signInComplete() {
        checkIfAlreadyExist();
    }
}
