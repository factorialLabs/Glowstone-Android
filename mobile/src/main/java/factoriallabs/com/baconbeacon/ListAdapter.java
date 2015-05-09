package factoriallabs.com.baconbeacon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import com.estimote.sdk.Beacon;

import java.util.*;

/**
 * Created by Steven on 2015-05-09.
 */
public class ListAdapter extends ArrayAdapter<Beacon> {

    public ListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public ListAdapter(Context context, int resource, List<Beacon> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.beacon_list_item, null);
        }

        Beacon p = getItem(position);

        if (p != null) {
            TextView tt1 = (TextView) v.findViewById(R.id.id);
            TextView tt2 = (TextView) v.findViewById(R.id.categoryId);
            TextView tt3 = (TextView) v.findViewById(R.id.description);

            if (tt1 != null) {
                tt1.setText(p.getId());
            }

            if (tt2 != null) {
                tt2.setText(p.getCategory().getId());
            }

            if (tt3 != null) {
                tt3.setText(p.getDescription());
            }
        }

        return v;
    }

}