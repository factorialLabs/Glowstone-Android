package factoriallabs.com.baconbeacon.fragments;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.CycleInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;

import factoriallabs.com.baconbeacon.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchingFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView networkText;
    private ImageView foundDevice;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchingFragment newInstance(String param1, String param2) {
        SearchingFragment fragment = new SearchingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SearchingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public void setConnectedNetwork(boolean b){
        if(networkText != null){
            if(b){
                networkText.setVisibility(View.GONE);
            }else{
                networkText.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // -- inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_searching, container, false);
        //ImageView img = (ImageView) v.findViewById(R.id.imageView);
        final RippleBackground rippleBackground=(RippleBackground)v.findViewById(R.id.content);
        ImageView imageView=(ImageView)v.findViewById(R.id.centerImage);
        networkText=(TextView)v.findViewById(R.id.network_msg);
        foundDevice=(ImageView)v.findViewById(R.id.foundDevice);
        rippleBackground.startRippleAnimation();

        return v;
    }
    public void foundDevice(Animator.AnimatorListener l){
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(l);
        animatorSet.setDuration(400);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        ArrayList<Animator> animatorList=new ArrayList<Animator>();
        ObjectAnimator scaleXAnimator = ObjectAnimator.ofFloat(foundDevice, "ScaleX", 0f, 1.2f, 1f);
        animatorList.add(scaleXAnimator);
        ObjectAnimator scaleYAnimator = ObjectAnimator.ofFloat(foundDevice, "ScaleY", 0f, 1.2f, 1f);
        animatorList.add(scaleYAnimator);
        animatorSet.playTogether(animatorList);
        foundDevice.setVisibility(View.VISIBLE);
        animatorSet.start();
    }
}
