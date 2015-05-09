package factoriallabs.com.baconbeacon;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.Region;
import com.parse.Parse;

import java.util.List;

import factoriallabs.com.baconbeacon.estimote.BeaconDetectionManager;
import factoriallabs.com.baconbeacon.fragments.InformationFragment;
import factoriallabs.com.baconbeacon.fragments.SearchingFragment;
import android.widget.TextView;



public class SearchingActivity extends AppCompatActivity implements BeaconDetectionManager.OnBeaconDetectListener{

    private BeaconDetectionManager mBeaconDetectionManager;

    boolean mShowBeaconInfo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //FrameLayout frame = (FrameLayout) findViewById(R.id.container);

        if (savedInstanceState == null) {

        }

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "aMCN1DWIofhjx7Q2S4ezuOljE3A5FyJtRgAZm0sC", "7mAMLVFDkqHlkjmDM82M7bJFcg3l120eyQXLmYnp");


        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        transaction.replace(R.id.container, SearchingFragment.newInstance(null, null), "searchfragment"); // newInstance() is a static factory method.
        transaction.commit();

        mBeaconDetectionManager = new BeaconDetectionManager(this, this);
        mBeaconDetectionManager.init();
        mBeaconDetectionManager.startListening();
    }
    @Override
    public void onStop(){
        super.onStop();
        mBeaconDetectionManager.stop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
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
    public void onBeaconFind(Region region, List<Beacon> beacons, Beacon closestBeacon) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        if(closestBeacon != null){
            //good enough signal
            //go back to searching screen

            //TODO: Fix case when closest beacon changes

            InformationFragment infofrag = (InformationFragment) manager.findFragmentByTag("InformationFragment");
            StringBuffer beaconStatus = new StringBuffer();
            for (Beacon b : beacons) {
                beaconStatus.append("Beacon name: " + b.toString() + "\nBeacon signal strength: " + b.getRssi() + "\n\n");
            }
            if (!mShowBeaconInfo) {
                if (infofrag == null) {
                    infofrag = InformationFragment.newInstance(null, null);
                }
                Toast.makeText(this, "Signal: " + closestBeacon.getRssi(), Toast.LENGTH_LONG).show();
                transaction.replace(R.id.container, infofrag, "InformationFragment"); // newInstance() is a static factory method.
                transaction.commit();
            }
            infofrag.setText(beaconStatus.toString());
            mShowBeaconInfo = true;
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

    @Override
    public void onClosestBeaconFind(Beacon beacon) {

    }
}
