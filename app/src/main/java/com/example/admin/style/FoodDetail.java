package com.example.admin.style;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.admin.style.Database.Database;
import com.example.admin.style.Model.Food;
import com.example.admin.style.Model.Order;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class FoodDetail extends AppCompatActivity {
    TextView product_name, product_price, product_description;
    ImageView product_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;
    ElegantNumberButton numberButton;
    String productId = "";
    FirebaseDatabase database;
    DatabaseReference products;
    Food current_product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        database = FirebaseDatabase.getInstance();
        products = database.getReference("Product");

        numberButton = findViewById(R.id.number_button);
        btnCart = findViewById(R.id.btnCart);
        product_description = findViewById(R.id.product_description);
        product_name = findViewById(R.id.product_name);
        product_price = findViewById(R.id.product_price);
        product_image = findViewById(R.id.img_product);

        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);


        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToCart(new Order(productId,
                        current_product.getName(),
                        numberButton.getNumber(),
                        current_product.getPrice(),
                        current_product.getDiscount()));
                        Toast.makeText(FoodDetail.this, "Added to cart", Toast.LENGTH_SHORT).show();
            }
        });

        if(getIntent() !=  null)
            productId = getIntent().getStringExtra("foodId");
        if(!productId.isEmpty()){
            getDetailProducts(productId);
        }

    }

    private void getDetailProducts(String productId) {
        products.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                current_product = dataSnapshot.getValue(Food.class);

                Picasso.with(getBaseContext()).load(current_product.getImage()).into(product_image);

                collapsingToolbarLayout.setTitle(current_product.getName());
                product_price.setText(current_product.getPrice());
                product_name.setText(current_product.getName());
                product_description.setText(current_product.getDescription());


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

