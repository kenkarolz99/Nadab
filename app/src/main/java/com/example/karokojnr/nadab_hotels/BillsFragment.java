package com.example.karokojnr.nadab_hotels;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.karokojnr.nadab_hotels.adapter.BillsAdapter;
import com.example.karokojnr.nadab_hotels.api.HotelService;
import com.example.karokojnr.nadab_hotels.api.RetrofitInstance;
import com.example.karokojnr.nadab_hotels.model.Order;
import com.example.karokojnr.nadab_hotels.model.OrderItem;
import com.example.karokojnr.nadab_hotels.model.Orders;
import com.example.karokojnr.nadab_hotels.utils.SharedPrefManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BillsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BillsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BillsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private BillsAdapter adapter;
    private BillsFragment context;
    RecyclerView recyclerView;

    private List<Order> orderLists = new ArrayList<>();


    public BillsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BillsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BillsFragment newInstance(String param1, String param2) {
        BillsFragment fragment = new BillsFragment ();
        Bundle args = new Bundle ();
        args.putString ( ARG_PARAM1, param1 );
        args.putString ( ARG_PARAM2, param2 );
        fragment.setArguments ( args );
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate ( savedInstanceState );
        if (getArguments () != null) {
            mParam1 = getArguments ().getString ( ARG_PARAM1 );
            mParam2 = getArguments ().getString ( ARG_PARAM2 );
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate ( R.layout.fragment_bills, container, false);

        context = this;


        HotelService service = RetrofitInstance.getRetrofitInstance ().create ( HotelService.class );
        Call<Orders> call = service.getOrders( SharedPrefManager.getInstance(getActivity ()).getToken() );
        call.enqueue ( new Callback<Orders>() {
            @Override
            public void onResponse(Call<Orders> call, Response<Orders> response) {
                orderLists.clear();
                for (int i = 0; i < response.body ().getOrdersList ().size (); i++) {
                    Order order = response.body ().getOrdersList().get(i);
                    if(order.getOrderStatus().equals("BILLS")) orderLists.add ( order );
                }
                generateOrdersList ((ArrayList<Order>) orderLists);
            }

            @Override
            public void onFailure(Call<Orders> call, Throwable t) {
                Toast.makeText ( getActivity (), "Something went wrong...Please try later!"+t.getMessage(), Toast.LENGTH_SHORT ).show ();
            }
        } );


        recyclerView = (RecyclerView) view.findViewById ( R.id.bills_recycler_view );
        recyclerView.addOnItemTouchListener ( new RecyclerTouchListener( getActivity (), recyclerView, new RecyclerTouchListener.ClickListener () {
            @Override
            public void onClick(View view, final int position) {
                final Order order = orderLists.get ( position );
                OrderItem[] orderItems = order.getOrderItems();
                Intent intent = new Intent(getContext(), com.example.karokojnr.nadab_hotels.Order.class);
                intent.putExtra("orderId", order.getOrderId());
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        } ) );


        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction ( uri );
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void generateOrdersList(ArrayList<Order> empDataList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getActivity () );

        adapter = new BillsAdapter( empDataList, getActivity ());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter (adapter);
    }
}