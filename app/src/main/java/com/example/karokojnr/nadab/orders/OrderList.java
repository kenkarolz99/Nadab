package com.example.karokojnr.nadab.orders;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.karokojnr.nadab.Items;
import com.example.karokojnr.nadab.R;
import com.example.karokojnr.nadab.RecyclerTouchListener;
import com.example.karokojnr.nadab.adapter.ItemsAdapter;
import com.example.karokojnr.nadab.adapter.OrdersAdapter;
import com.example.karokojnr.nadab.api.HotelService;
import com.example.karokojnr.nadab.api.RetrofitInstance;
import com.example.karokojnr.nadab.model.Order;
import com.example.karokojnr.nadab.model.OrderItem;
import com.example.karokojnr.nadab.model.Orders;
import com.example.karokojnr.nadab.model.Product;
import com.example.karokojnr.nadab.model.Products;
import com.example.karokojnr.nadab.utils.SharedPrefManager;
import com.example.karokojnr.nadab.utils.utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderList extends AppCompatActivity {
    private OrdersAdapter adapter;
    private Context context;
    RecyclerView recyclerView;

    private List<Order> orderLists = new ArrayList<>();

    public static final String TAG = OrderList.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        utils.sendRegistrationToServer(OrderList.this, token);
                        // Log and toast
                        Log.d(TAG, token);
                        Toast.makeText(OrderList.this, token, Toast.LENGTH_SHORT).show();
                    }
                });

        /*Create handle for the RetrofitInstance interface*/
        HotelService service = RetrofitInstance.getRetrofitInstance ().create ( HotelService.class );
        Call<Orders> call = service.getOrders( SharedPrefManager.getInstance(OrderList.this).getToken() );
        call.enqueue ( new Callback<Orders>() {
            @Override
            public void onResponse(Call<Orders> call, Response<Orders> response) {
                for (int i = 0; i < response.body ().getOrdersList ().size (); i++) {
                    orderLists.add ( response.body ().getOrdersList().get(i) );
                }
                generateOrdersList ( response.body ().getOrdersList () );
            }

            @Override
            public void onFailure(Call<Orders> call, Throwable t) {
                Log.wtf("LOG", "onFailure: "+t.getMessage() );
                Toast.makeText ( OrderList.this, "Something went wrong...Please try later!"+t.getMessage(), Toast.LENGTH_SHORT ).show ();
            }
        } );


        recyclerView = (RecyclerView) findViewById ( R.id.orderslist_recycler_view );
        recyclerView.addOnItemTouchListener ( new RecyclerTouchListener( OrderList.this, recyclerView, new RecyclerTouchListener.ClickListener () {
            @Override
            public void onClick(View view, final int position) {
                final Order order = orderLists.get ( position );
                OrderItem[] orderItems = order.getOrderItems();
                String items[] = new String[orderItems.length];
                for (int i = 0; i < orderItems.length; i++) {
                    OrderItem item = orderItems[i];
                    items[i] = item.getQty() + " " + item.getName() + " @ Kshs. " + (item.getQty() * item.getPrice());
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(OrderList.this);
                builder.setTitle("Order items");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        
                    }
                });
                builder.setPositiveButton("ACCEPT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(context, "Accepting order No:: "+ order.getOrderId(), Toast.LENGTH_SHORT).show();
                        HotelService service = RetrofitInstance.getRetrofitInstance ().create ( HotelService.class );
                        Call<Order> call = service.acceptOrder(order.getOrderId(), "ACCEPTED");
                        call.enqueue ( new Callback<Order>() {
                            @Override
                            public void onResponse(Call<Order> call, Response<Order> response) {
//                                orderLists.set(position, response.body());
                                adapter.notifyItemChanged(position, response.body());
                            }

                            @Override
                            public void onFailure(Call<Order> call, Throwable t) {
                                Log.wtf("LOG", "onFailure: "+t.getMessage() );
                                Toast.makeText ( OrderList.this, "Something went wrong...Please try later!"+t.getMessage(), Toast.LENGTH_SHORT ).show ();
                            }
                        } );
                    }
                });
                builder.setNeutralButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.setNegativeButton("REJECT", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(context, "Rejecting order No:: "+ order.getOrderId(), Toast.LENGTH_SHORT).show();
                        HotelService service = RetrofitInstance.getRetrofitInstance ().create ( HotelService.class );
                        Call<Order> call = service.acceptOrder(order.getOrderId(), "REJECTED");
                        call.enqueue ( new Callback<Order>() {
                            @Override
                            public void onResponse(Call<Order> call, Response<Order> response) {
                                orderLists.add(response.body());
                                adapter.notifyDataSetChanged();
//                                adapter.notifyItemChanged(position, response.body());
                            }

                            @Override
                            public void onFailure(Call<Order> call, Throwable t) {
                                Toast.makeText ( OrderList.this, "Something went wrong...Please try later!"+t.getMessage(), Toast.LENGTH_SHORT ).show ();
                            }
                        } );
                    }
                });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        } ) );
    }

    private void generateOrdersList(ArrayList<Order> empDataList) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( context );

        adapter = new OrdersAdapter( empDataList, OrderList.this );
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter (adapter);
    }

}
