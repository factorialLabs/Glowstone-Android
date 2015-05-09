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
import com.manuelpeinado.fadingactionbar.extras.actionbarcompat.FadingActionBarHelper;

import java.util.List;

import factoriallabs.com.baconbeacon.estimote.BeaconDetectionManager;
import factoriallabs.com.baconbeacon.fragments.InformationFragment;
import factoriallabs.com.baconbeacon.fragments.SearchingFragment;


public class SearchingActivity extends AppCompatActivity implements BeaconDetectionManager.OnBeaconDetectListener{

    private BeaconDetectionManager mBeaconDetectionManager;

    boolean mShowBeaconInfo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        //FrameLayout frame = (FrameLayout) findViewById(R.id.container);

        if (savedInstanceState == null) {
            /* During initial setup, plug in the details fragment.
            TableFragment table = new TableFragment();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(CONTENT_VIEW_ID, table).commit();
            table.setArguments(getIntent().getExtras());
            getFragmentManager().beginTransaction().add(android.R.id.content, details).commit(); */
        }

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
    public void onBeaconFind(Region region, List<Beacon> beacons) {

    }

    @Override
    public void onClosestBeaconFind(Beacon beacon) {
        //show info fragment if it is not already open
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        if(beacon != null && !mShowBeaconInfo && beacon.getRssi() > -70){
            //good enough signal
            //go back to searching screen
            Fragment infofrag = manager.findFragmentByTag("InformationFragment");
            if(infofrag == null){
                infofrag = InformationFragment.newInstance(null, null);
            }
            Toast.makeText(this,"Signal: " + beacon.getRssi(),Toast.LENGTH_LONG).show();
            transaction.replace(R.id.container, infofrag, "InformationFragment"); // newInstance() is a static factory method.
            transaction.commit();

            mShowBeaconInfo = true;

        }else{
            if(beacon != null && mShowBeaconInfo && beacon.getRssi() < -70){
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
}
