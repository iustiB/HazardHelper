package licenta.iusti.hazardhelper.features;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import licenta.iusti.hazardhelper.R;
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
