package factoriallabs.com.baconbeacon;

import android.animation.Animator;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Region;
import com.parse.Parse;
import com.parse.ParseObject;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import factoriallabs.com.baconbeacon.estimote.BeaconDetectionManager;
import factoriallabs.com.baconbeacon.fragments.BeaconListFragment;
import factoriallabs.com.baconbeacon.fragments.InformationFragment;
import factoriallabs.com.baconbeacon.fragments.SearchingFragment;
import factoriallabs.com.baconbeacon.tasks.BeaconInfo;
import factoriallabs.com.baconbeacon.tasks.Task;

import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


public class SearchingActivity extends AppCompatActivity implements BeaconListFragment.OnFragmentInteractionListener,BeaconDetectionManager.OnBeaconDetectListener, SlidingUpPanelLayout.PanelSlideListener {

    boolean mShowBeaconInfo = false;
    private BeaconDetectionManager mBeaconDetectionManager;
    private HashMap<String, BeaconInfo> mBeaconList;
    private BeaconListFragment listfrag;
    private SlidingUpPanelLayout mFrame;
    private boolean mUserSelectedItem = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mFrame = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mFrame.setPanelSlideListener(this);
        if (savedInstanceState == null) {

        }

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        final SearchingFragment searchFrag = SearchingFragment.newInstance(false, null);
        transaction.replace(R.id.container, searchFrag, "searchFrag"); // newInstance() is a static factory method.
        transaction.commit();

        mBeaconList = new HashMap<>();

        new Task("Beacon", new Task.OnResultListener(){

            @Override
            public void onDone(List<ParseObject> object) {
                if(object.size()>0)
                    searchFrag.setConnectedNetwork(true);
                for(ParseObject obj : object){
                    JSONObject jobj = obj.getJSONObject("beacon");
                    try {
                        mBeaconList.put(jobj.getString("id"),new BeaconInfo(jobj.getString("description"),jobj.getString("name"), jobj.getString("image"),jobj.getString("id")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                mBeaconDetectionManager = new BeaconDetectionManager(SearchingActivity.this, SearchingActivity.this);
                mBeaconDetectionManager.init();
                mBeaconDetectionManager.startListening();

            }
        }).run();
        FragmentTransaction transaction2 = manager.beginTransaction();
        transaction2.setTransition(R.anim.abc_fade_in);

        if (listfrag == null) {
            listfrag = BeaconListFragment.newInstance(null, null);
        }
        //Toast.makeText(this, "Signal: " + selectedBeacon.getRssi(), Toast.LENGTH_LONG).show();
        transaction2.replace(R.id.panel_container, listfrag, "beaconlistfragment"); // newInstance() is a static factory method.
        //transaction2.addToBackStack("list");
        transaction2.commit();

        searchFrag.setConnectedNetwork(false);
    }

    @Override
    public void onBackPressed() {
        if(mFrame.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED){
            closePanel();
        }else{
            if(mUserSelectedItem){
                mUserSelectedItem = false;
                mShowBeaconInfo = false;
                FragmentManager manager = getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                final SearchingFragment searchFrag = SearchingFragment.newInstance(true, null);
                transaction.replace(R.id.container, searchFrag, "searchFrag"); // newInstance() is a static factory method.
                transaction.commit();
            }else {
                super.onBackPressed();
            }
        }


        //moveTaskToBack(true);
    }
    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mBeaconDetectionManager != null){
            mBeaconDetectionManager.stop();
            mBeaconDetectionManager.destroy();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_list) {
            openPanel();
            return true;
        }
        if(id == R.id.action_speak){
            FragmentManager manager = getSupportFragmentManager();
            InformationFragment infofrag = (InformationFragment) manager.findFragmentByTag("InformationFragment");
            if(infofrag != null)
                infofrag.speak();
        }

        return super.onOptionsItemSelected(item);
    }

    public String signalFormatter(int signalLvl){
        if(signalLvl > -50){
            return "Very Close";
        }
        if(signalLvl > -70){
            return "Close";
        }
        if(signalLvl > -80){
            return "Fair distance";
        }
        if(signalLvl > -200){
            return "Far away";
        }
        return "Not in range";
    }

    @Override
    public void onBeaconFind(Region region, List<Beacon> beacons) {
        FragmentManager manager = getSupportFragmentManager();
        final FragmentTransaction transaction = manager.beginTransaction();

        if(beacons.size() > 0){
            //good enough signal
            //go back to searching screen
            //TODO: Fix case when closest beacon changes

            //InformationFragment infofrag = (InformationFragment) manager.findFragmentByTag("InformationFragment");
            //StringBuffer beaconStatus = new StringBuffer();
            //Collections.sort(beacons, new BeaconComparator()); //sort from strongest to weakest
            //Log.d("Beacons", "signal: " + beacons.get(0));

            BeaconInfo selectedBeacon = null;

            //find first beacon with data on the server
            for(Beacon b : beacons) {
                if (mBeaconList.get(b.getMacAddress()) != null) {
                    mBeaconList.get(b.getMacAddress()).extra = signalFormatter(b.getRssi()) + " ("+b.getRssi()+ " " + b.getMacAddress()+")";
                }
            }

            if(listfrag != null)
                listfrag.onBeaconDetect(mBeaconList.values(), null);

            //find first beacon with data on the server
            for(Beacon b : beacons){
                if(mBeaconList.get(b.getMacAddress()) != null){
                    //found
                    selectedBeacon = mBeaconList.get(b.getMacAddress());
                    break;
                }
            }
            if (selectedBeacon != null && !mShowBeaconInfo) {
                SearchingFragment searchFrag = (SearchingFragment) manager.findFragmentByTag("searchFrag");
                //final InformationFragment finalInfofrag = infofrag;
                final BeaconInfo finalSelectedBeacon = selectedBeacon;
                if(searchFrag == null){
                    searchFrag = SearchingFragment.newInstance(true, null);
                }
                if(searchFrag != null)
                searchFrag.foundDevice(new Animator.AnimatorListener(){

                    @Override
                    public void onAnimationStart(Animator animation) {
                        mShowBeaconInfo = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                InformationFragment finalInfofrag = InformationFragment.newInstance(finalSelectedBeacon.description,finalSelectedBeacon.name, finalSelectedBeacon.imgUrl);
                                //Toast.makeText(this, "Signal: " + selectedBeacon.getRssi(), Toast.LENGTH_LONG).show();
                                transaction.replace(R.id.container, finalInfofrag, "InformationFragment"); // newInstance() is a static factory method.
                                transaction.setTransition(R.anim.abc_fade_in);
                                //transaction.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out);
                                transaction.commit();
                                finalInfofrag.setText(finalSelectedBeacon.description);
                                mShowBeaconInfo = true;
                            }
                        }, 1000);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });

                showNotif(selectedBeacon);
            }

        }else{
            if(mShowBeaconInfo){
                //go back to searching screen
                SearchingFragment frag = (SearchingFragment) manager.findFragmentByTag("searchFrag");
                if(frag == null){
                    frag = SearchingFragment.newInstance(true, null);
                }



                transaction.replace(R.id.container, frag, "searchfragment"); // newInstance() is a static factory method.
                //transaction.setCustomAnimations(R.anim.abc_fade_out, R.anim.abc_fade_in);
                transaction.setTransition(R.anim.abc_fade_out);
                transaction.commit();

                frag.setConnectedNetwork(true);

                mShowBeaconInfo = false;

            }
        }
    }

    public void openPanel(){
        mFrame.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        getSupportActionBar().hide();
    }

    public void closePanel(){
        mFrame.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        getSupportActionBar().show();
    }

    public void showNotif(BeaconInfo selectedBeacon){
        //show notificaiton
        // Create an intent for the reply action
        Intent actionIntent = new Intent(this, SearchingActivity.class);
        PendingIntent actionPendingIntent =
                PendingIntent.getActivity(this, 0, actionIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

// Create the action
        NotificationCompat.Action action =
                new NotificationCompat.Action.Builder(R.drawable.ic_action_device_wifi_tethering,
                        "Glowstone: " + selectedBeacon.name, actionPendingIntent)
                        .build();
        NotificationCompat.BigTextStyle bigStyle = new NotificationCompat.BigTextStyle();
        bigStyle.bigText(selectedBeacon.description);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_action_device_wifi_tethering)
                        .setLargeIcon(BitmapFactory.decodeResource(
                                getResources(), R.mipmap.ic_launcher))
                        .setContentTitle("Glowstone: " + selectedBeacon.name)
                        .setContentText(selectedBeacon.description)
                        .setContentIntent(actionPendingIntent)
                        .setStyle(bigStyle);
        notificationBuilder.setPriority(Notification.PRIORITY_HIGH);
        // Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

// Issue the notification with notification manager.
        notificationManager.notify(1, notificationBuilder.build());
    }

    @Override
    public void onPanelSlide(View view, float v) {

    }

    @Override
    public void onPanelCollapsed(View view) {
        getSupportActionBar().show();
    }

    @Override
    public void onPanelExpanded(View view) {
        getSupportActionBar().hide();
    }

    @Override
    public void onPanelAnchored(View view) {

    }

    @Override
    public void onPanelHidden(View view) {

    }

    @Override
    public void onFragmentInteraction(BeaconInfo selectedBeacon) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        //  if (infofrag == null) {
        if (!selectedBeacon.extra.equals("Not in range")) {
            InformationFragment infofrag = InformationFragment.newInstance(selectedBeacon.description, selectedBeacon.name, selectedBeacon.imgUrl);
            //  }
            //Toast.makeText(this, "Signal: " + selectedBeacon.getRssi(), Toast.LENGTH_LONG).show();
            transaction.replace(R.id.container, infofrag, "InformationFragment"); // newInstance() is a static factory method.
            transaction.setTransition(R.anim.abc_fade_in);
            //transaction.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out);
            transaction.commit();
            infofrag.setText(selectedBeacon.description);
            mShowBeaconInfo = true;
            mUserSelectedItem = true;
            showNotif(selectedBeacon);
            closePanel();
        }
        else{
            Toast.makeText(this, "Beacon is too far!", Toast.LENGTH_LONG).show();
        }
    }
}
