package factoriallabs.com.baconbeacon;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Region;
import com.parse.Parse;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import factoriallabs.com.baconbeacon.estimote.BeaconDetectionManager;
import factoriallabs.com.baconbeacon.fragments.InformationFragment;
import factoriallabs.com.baconbeacon.fragments.SearchingFragment;
import factoriallabs.com.baconbeacon.tasks.BeaconInfo;
import factoriallabs.com.baconbeacon.tasks.Task;

import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


public class SearchingActivity extends AppCompatActivity implements BeaconDetectionManager.OnBeaconDetectListener{

    private BeaconDetectionManager mBeaconDetectionManager;

    boolean mShowBeaconInfo = false;
    private HashMap<String, BeaconInfo> mBeaconList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //FrameLayout frame = (FrameLayout) findViewById(R.id.container);

        if (savedInstanceState == null) {

        }

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.container, SearchingFragment.newInstance(null, null), "searchfragment"); // newInstance() is a static factory method.
        transaction.commit();

        mBeaconList = new HashMap<>();

        new Task("Beacon", new Task.OnResultListener(){

            @Override
            public void onDone(List<ParseObject> object) {
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
    }
    @Override
    public void onStop(){
        super.onStop();
        if(mBeaconDetectionManager != null)
            mBeaconDetectionManager.stop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mBeaconDetectionManager != null)
            mBeaconDetectionManager.destroy();
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBeaconFind(Region region, List<Beacon> beacons) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        if(beacons.size() > 0){
            //good enough signal
            //go back to searching screen
            //TODO: Fix case when closest beacon changes

            InformationFragment infofrag = (InformationFragment) manager.findFragmentByTag("InformationFragment");
            StringBuffer beaconStatus = new StringBuffer();
            //Collections.sort(beacons, new BeaconComparator()); //sort from strongest to weakest
            Log.d("Beacons", "signal: " + beacons.get(0));

            BeaconInfo selectedBeacon = null;

            //find first beacon with data on the server
            for(Beacon b : beacons){
                if(mBeaconList.get(b.getMacAddress()) != null){
                    //found
                    selectedBeacon = mBeaconList.get(b.getMacAddress());
                    break;
                }
            }
            if (selectedBeacon != null && !mShowBeaconInfo) {
                if (infofrag == null) {
                    infofrag = InformationFragment.newInstance(selectedBeacon.description,selectedBeacon.name, selectedBeacon.imgUrl);
                }
                //Toast.makeText(this, "Signal: " + selectedBeacon.getRssi(), Toast.LENGTH_LONG).show();
                transaction.replace(R.id.container, infofrag, "InformationFragment"); // newInstance() is a static factory method.
                transaction.commit();
                infofrag.setText(selectedBeacon.description);
                mShowBeaconInfo = true;
            }

        }else{
            if(mShowBeaconInfo){
                //go back to searching screen
                Fragment frag = manager.findFragmentByTag("searchfragment");
                if(frag == null){
                    frag = SearchingFragment.newInstance(null, null);
                }

                transaction.replace(R.id.container, frag, "searchfragment"); // newInstance() is a static factory method.
                transaction.commit();

                mShowBeaconInfo = false;
            }
        }
    }
    class BeaconComparator implements Comparator<Beacon> {
        @Override
        public int compare(Beacon a, Beacon b) {
            return a.getRssi() < b.getRssi() ? 1 : a.getRssi() == b.getRssi() ? 0 : -1;
        }
    }
}
