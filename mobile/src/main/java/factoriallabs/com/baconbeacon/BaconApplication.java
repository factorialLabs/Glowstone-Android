package factoriallabs.com.baconbeacon;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by yuchen.hou on 15-05-08.
 */
public class BaconApplication extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "aMCN1DWIofhjx7Q2S4ezuOljE3A5FyJtRgAZm0sC", "7mAMLVFDkqHlkjmDM82M7bJFcg3l120eyQXLmYnp");

    }
}
