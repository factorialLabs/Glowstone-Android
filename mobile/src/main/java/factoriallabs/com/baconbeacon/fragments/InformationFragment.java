package factoriallabs.com.baconbeacon.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.manuelpeinado.fadingactionbar.extras.actionbarcompat.FadingActionBarHelper;
import com.manuelpeinado.fadingactionbar.view.ObservableScrollView;
import com.manuelpeinado.fadingactionbar.view.OnScrollChangedCallback;
import com.melnykov.fab.FloatingActionButton;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.Locale;

import factoriallabs.com.baconbeacon.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InformationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InformationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InformationFragment extends Fragment implements TextToSpeech.OnInitListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ARG_PARAM3 = "param3";
    private FadingActionBarHelper mFadingHelper;
    private Bundle mArguments;

    private OnFragmentInteractionListener mListener;
    private TextToSpeech tts;
    private String mDescription;
    private TextView textView;
    private String mName;
    private String mURL;
    private TextView titleView;
    private int mLastScrollY;
    private int mScrollThreshold = 4;
    private ObservableScrollView scrollView;
    private View v;
    private Activity mActivity;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment InformationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InformationFragment newInstance(String description, String name, String url) {
        InformationFragment fragment = new InformationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, description);
        args.putString(ARG_PARAM2, name);
        args.putString(ARG_PARAM3, url);
        fragment.setArguments(args);
        return fragment;
    }

    public InformationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDescription = getArguments().getString(ARG_PARAM1);
            mName = getArguments().getString(ARG_PARAM2);
            mURL = getArguments().getString(ARG_PARAM3);
        }
        //mDescription = getActivity().getResources().getString(R.string.loren_ipsum);
        tts = new TextToSpeech(getActivity(), this);
    }

    public void setText(String text){
        if (text != null) {
            mDescription = text;
            if(textView != null)
                textView.setText(mDescription);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mArguments = getArguments();
        int actionBarBg = R.color.primary;

        mFadingHelper = new FadingActionBarHelper()
                .actionBarBackground(actionBarBg)
                .parallax(true)
                .headerLayout(R.layout.header)
                .contentLayout(R.layout.fragment_information)
                .lightActionBar(true);
        mFadingHelper.initActionBar(mActivity);

        // Inflate the layout for this fragment
        v = mFadingHelper.createView(inflater);
        ImageView img = (ImageView) v.findViewById(R.id.image_header);

        textView = (TextView) v.findViewById(R.id.description);
        titleView = (TextView) v.findViewById(R.id.titleView);
        if(mDescription != null)
            textView.setText(mDescription);

        if(mName != null)
            titleView.setText(mName);

        if(mURL != null){
            ImageLoader.getInstance().displayImage(mURL, img);
        }
        else
            img.setImageResource(R.drawable.mc);

        final FloatingActionButton btn = (FloatingActionButton) v.findViewById(R.id.imageButton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //read aloud
                if (tts!=null) {
                    if (mDescription!=null) {
                        if (!tts.isSpeaking()) {
                            tts.speak(mDescription, TextToSpeech.QUEUE_FLUSH, null);
                        }else{
                            tts.stop();
                        }
                    }
                }
            }
        });
        scrollView = (ObservableScrollView) v.findViewById(R.id.fab__scroll_view);
        scrollView.setOnScrollChangedCallback(new OnScrollChangedCallback() {
            @Override
            public void onScroll(int i, int t) {
                boolean isSignificantDelta = Math.abs(t - mLastScrollY) > mScrollThreshold;
                if (isSignificantDelta) {
                    if (t > mLastScrollY) {
                        btn.show(true);
                    } else {
                        btn.hide(true);
                    }
                }
                mLastScrollY = t;
            }
        });
        //ListView listView = (ListView) v.findViewById(android.R.id.list);

        //btn.attachToScrollView(scrollView);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    @Override
         public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onDestroy() {
        if (tts!=null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int code) {
        if (code==TextToSpeech.SUCCESS) {
            tts.setLanguage(Locale.getDefault());
            tts.setSpeechRate(0.75f);


        } else {
            tts = null;
            Toast.makeText(getActivity(), "Failed to initialize TTS engine.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        /*
        mArguments = getArguments();
        int actionBarBg = R.color.primary;

        mFadingHelper = new FadingActionBarHelper()
                .actionBarBackground(actionBarBg)
                .parallax(true)
                .headerLayout(R.layout.header)
                .contentLayout(R.layout.fragment_information)
                .lightActionBar(true);
        mFadingHelper.initActionBar(activity);
        */
        mActivity = activity;
    }
}
