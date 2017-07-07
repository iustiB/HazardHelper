package licenta.iusti.hazardhelper.signInClasses;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

import licenta.iusti.hazardhelper.MainActivity;
import licenta.iusti.hazardhelper.R;

/**
 * Created by Iusti on 6/10/2017.
 */

public class SignInActivity extends FragmentActivity implements View.OnClickListener {

    private GoogleSignInFragment.SignInListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this, MainActivity.class));
            SignInActivity.this.finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_login_method);
        findViewById(R.id.phone_sign_in_button).setOnClickListener(this);
        findViewById(R.id.google_sign_in_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.phone_sign_in_button:
                startActivity(new Intent(this, PhoneAuthActivity.class));
                break;
            case R.id.google_sign_in_button:
                startActivity(new Intent(this,GoogleSignInActivity.class));
                break;
        }
    }
}
