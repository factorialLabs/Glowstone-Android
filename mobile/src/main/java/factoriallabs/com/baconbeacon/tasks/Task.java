package factoriallabs.com.baconbeacon.tasks;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by yuchen.hou on 15-05-09.
 */
public class Task {

    public interface OnResultListener{
        void onDone(ParseObject object);
    }

    private OnResultListener callback;
    ParseQuery<ParseObject> query;

    Task(String endpoint, OnResultListener l){
        query = ParseQuery.getQuery(endpoint);
        callback = l;
    }

    public void run(){
        query.getInBackground("", new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    //call back
                    callback.onDone(object);
                } else {
                    // something went wrong
                    callback.onDone(null);
                }
            }
        });
    }
}
