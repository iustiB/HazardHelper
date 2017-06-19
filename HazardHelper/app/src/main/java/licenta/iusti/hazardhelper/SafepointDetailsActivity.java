package licenta.iusti.hazardhelper;

import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import licenta.iusti.hazardhelper.domain.CustomPlace;
import licenta.iusti.hazardhelper.domain.Message;
import licenta.iusti.hazardhelper.utils.MessagesListAdapter;
import licenta.iusti.hazardhelper.utils.MessagesWatcher;
import licenta.iusti.hazardhelper.utils.PagerAdapter;

/**
 * Created by Iusti on 6/19/2017.
 */
public class SafepointDetailsActivity extends AppCompatActivity {
    public static final String SAFEPOINT_UID = "safepoint_uid";
    private String safepointId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.safepoint_details_layout);


        this.safepointId = getIntent().getStringExtra(SAFEPOINT_UID);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager());
        SafepointDetailsFragment safepointDetailsFragment = new SafepointDetailsFragment();
        safepointDetailsFragment.setSafepointID(safepointId);
        adapter.addFragment(safepointDetailsFragment,"Details");
        ChatFragment chatFragment = new ChatFragment();
        chatFragment.setItemKey(safepointId);
        adapter.addFragment(chatFragment,"Chat");
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);
    }





}
