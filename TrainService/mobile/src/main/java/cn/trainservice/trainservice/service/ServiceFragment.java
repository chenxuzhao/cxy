package cn.trainservice.trainservice.service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import cn.trainservice.trainservice.R;
import cn.trainservice.trainservice.service.live.LiveActivity;
import cn.trainservice.trainservice.service.radio.RadioActivity;
import cn.trainservice.trainservice.service.serviceItem.ServiceItemListActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ServiceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ServiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiceFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ServiceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ServiceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ServiceFragment newInstance(String param1, String param2) {
        ServiceFragment fragment = new ServiceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_service, container, false);
        LinearLayout service_movies = (LinearLayout) view.findViewById(R.id.service_movies);
        LinearLayout service_diningHall = (LinearLayout) view.findViewById(R.id.service_diningHall);
        LinearLayout service_store = (LinearLayout) view.findViewById(R.id.service_store);
        LinearLayout service_BookingBerth = (LinearLayout) view.findViewById(R.id.service_BookingBerth);
        LinearLayout service_liveVideo = (LinearLayout) view.findViewById(R.id.service_liveVideo);
        LinearLayout service_radio = (LinearLayout) view.findViewById(R.id.service_radio);
        LinearLayout service_gameHall = (LinearLayout) view.findViewById(R.id.service_gameHall);
        LinearLayout service_more = (LinearLayout) view.findViewById(R.id.service_more);

        Bundle movie = new Bundle();
        bindService(service_movies, ServiceItemListActivity.class, "movie");
        bindService(service_radio, RadioActivity.class, null);
        bindService(service_diningHall, ServiceItemListActivity.class, "food");
        bindService(service_store, ServiceItemListActivity.class, "commodity");
//        bindService(service_BookingBerth, ServiceItemListActivity.class);
        bindService(service_liveVideo, LiveActivity.class, null);
//        bindService(service_radio, RadioActivity.class);
//        bindService(service_gameHall, ServiceItemListActivity.class);
//        bindService(service_more, ServiceItemListActivity.class);

        return view;
    }


    public void bindService(LinearLayout layout, final Class activity, String serviceName) {
        final Intent intent = new Intent(getContext(), activity);
        if (serviceName != null) {
            intent.putExtra("serviceName", serviceName);
        }
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent);
            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        void onFragmentInteraction(Uri uri);
    }
}
