package factoriallabs.com.baconbeacon.estimote;

import android.content.Context;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by yuchen.hou on 15-05-09.
 */
public class BeaconDetectionManager {
    private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
    private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId", ESTIMOTE_PROXIMITY_UUID, null, null);
    private static final String TAG = "BACONBEACON";
    //public double min = 0;

    private BeaconManager beaconManager;
    private OnBeaconDetectListener callback;

    public interface OnBeaconDetectListener {
        void onBeaconFind(Region region, List<Beacon> beacons, Beacon min);
        void onClosestBeaconFind(Beacon beacon);
        //void onConditionBeaconFind(List<Beacon> beacons);
    }

    public BeaconDetectionManager(Context c, OnBeaconDetectListener l){
        beaconManager = new BeaconManager(c);
        //min = minimum_signal;
        callback = l;
    }

    public void setMin(double new_min){
        //min = new_min;
    }

    public void init(){
        // Should be invoked in #onCreate.
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
                double min_rssi = -1000;
                Beacon min = null;
                List<Beacon> nearBeacons = new ArrayList<Beacon>();

                for(Beacon b : beacons){
                    if(b.getRssi() > -70) {
                        nearBeacons.add(b);
                        if (b.getRssi() > min_rssi) {
                            min = b;
                            min_rssi = b.getRssi();
                        }
                    }
                }

                callback.onBeaconFind(region, nearBeacons, min);
            }
        });
    }

    public void startListening(){
        // Should be invoked in #onStart.
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
                } catch (RemoteException e) {
                    Log.e(TAG, "Cannot start ranging", e);
                }
            }
        });
    }

    public void stop(){
        // Should be invoked in #onStop.
        try {
            beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS);
        } catch (RemoteException e) {
            Log.e(TAG, "Cannot stop but it does not matter now", e);
        }
    }
    public void destroy(){
        // When no longer needed. Should be invoked in #onDestroy.
        beaconManager.disconnect();
    }
}
