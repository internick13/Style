package com.example.admin.style;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.admin.style.Common.Common;
import com.example.admin.style.Model.Request;
import com.example.admin.style.ViewHolder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OrderStatus extends AppCompatActivity {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Request");
        
        recyclerView = findViewById(R.id.list_order);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        if(getIntent() != null)
           loadOrders(Common.currentUser.getPhone());
        else
            loadOrders(getIntent().getStringExtra("userPhone"));
    }

    private void loadOrders(String phone) {

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(Request.class,
                                                                        R.layout.order_layout,
                                                                        OrderViewHolder.class,
                                                                        requests.orderByChild("phone").equalTo(phone)) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.txtOrderAddress.setText(model.getAddress());
            }
        };
        recyclerView.setAdapter(adapter);
    }


}
