package factoriallabs.com.baconbeacon.tasks;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by yuchen.hou on 15-05-09.
 */
public class Task {

    public interface OnResultListener{
        void onDone(List<ParseObject> object);
    }

    private OnResultListener callback;
    ParseQuery<ParseObject> query;

    public Task(String endpoint, OnResultListener l){
        query = ParseQuery.getQuery(endpoint);
        callback = l;
    }

    public void run(){
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if (e == null) {
                    //call back
                    callback.onDone(list);
                } else {
                    // something went wrong
                    callback.onDone(null);
                }
            }

        });
    }
}
