package cn.trainservice.trainservice.service.Chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.trainservice.trainservice.R;
import cn.trainservice.trainservice.TrainServiceApplication;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChatFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class ChatFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private  RecyclerView viewPagger;
    private  SwipeRefreshLayout swipeRefreshLayout;
    private  List<FriendAdapter.Friend>   list = new ArrayList<>();

    public  static Map<String ,FriendAdapter.Friend>  ip_list = new HashMap<>();
    private  FriendAdapter friendAdapter ;

    private OnFragmentInteractionListener mListener;
    private View view=null;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
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
        registerReceiver();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_chat, container, false);
            swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.chat_user_container);
            viewPagger = (RecyclerView) view.findViewById(R.id.friend_list);
            viewPagger.setItemAnimator(new DefaultItemAnimator());
            friendAdapter = new FriendAdapter(this.getContext(), list);
            setHasOptionsMenu(true);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                @Override
                public void onRefresh() {
                    swipeRefreshLayout.post(new Runnable() {

                        @Override
                        public void run() {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });

                }
            });
//            addFriend("CHANXU", "CHENXU", "127.0.0.1");
//            addFriend("CHANXUcc", "CHENXUccc", "127.0.0.1");
            viewPagger.setAdapter(friendAdapter);
        }

        return view;
    }
    @Override
 public void   onResume(){
        friendAdapter.notifyDataSetChanged();
        super.onResume();
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



    public void addFriend(String user_id ,String user_name ,String ip) {

        Log.d("data1", "add Friend");
        if(!ip_list.containsKey(user_id)){
            FriendAdapter.Friend friend = new FriendAdapter.Friend(user_id ,user_name,ip,false);
            ip_list.put(user_id, friend);
            list.add(friend);

            if(friendAdapter!=null)
            friendAdapter.loadList(list);

            Log.d("data1", "add");
        }
        friendAdapter.notifyDataSetChanged();

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

    private void registerReceiver() {
        ChatBroadcastReceiver receiver = new ChatBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(TrainServiceApplication.ChatDiscoverBroadcastAction);
        getActivity().registerReceiver(receiver, filter);
    }


    //private
    class ChatBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String user_id = intent.getStringExtra("user_id");
            String user_name = intent.getStringExtra("user_name");
            String ip = intent.getStringExtra("ip");
            addFriend(user_id,user_name,ip);
        }
    }
}
