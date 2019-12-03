package com.example.admin.style;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import com.example.admin.style.Interface.ItemClickListener;
import com.example.admin.style.Model.Food;
import com.example.admin.style.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class FoodList extends AppCompatActivity {


    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapterProduct;
    FirebaseDatabase database;
    DatabaseReference productList;
    String categoryId = "";

    //Search functionality
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);


        //Init fire base
        database = FirebaseDatabase.getInstance();
        productList = database.getReference("Product");
        recyclerView = findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Get intent here
        if(getIntent() != null)
            categoryId = getIntent().getStringExtra("CategoryId");

        if(!categoryId.isEmpty()  && categoryId != null){
            loadListFoods(categoryId);
        }

        //Seaarch
        materialSearchBar = findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter your product");
        //materialSearchBar.setSpeechMode(false);
        loadSuggest();
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<String> suggest = new ArrayList<>();
                for(String search: suggestList){
                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase())){
                        suggest.add(search);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                if(!enabled)
                    recyclerView.setAdapter(adapterProduct);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                    startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }

    private void startSearch(CharSequence text) {

        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class, R.layout.food_item, FoodViewHolder.class, productList.orderByChild("name").equalTo(text.toString())) {
            @Override
            protected void populateViewHolder(FoodViewHolder viewHolder, Food model, int position) {
                viewHolder.food_name.setText(model.getName());

                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.food_image);

                final Food local = model;

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(FoodList.this, "Its good", Toast.LENGTH_SHORT).show();
                        Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                        foodDetail.putExtra("foodId",searchAdapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });
            }
        };
        recyclerView.setAdapter(searchAdapter);
    }

    private void loadSuggest() {
        productList.orderByChild("menuId").equalTo(categoryId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    Food item = postSnapshot.getValue(Food.class);
                    suggestList.add(item.getName());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void loadListFoods(String categoryId) {

        adapterProduct = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class, R.layout.food_item, FoodViewHolder.class,
                productList.orderByChild("menuId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(FoodViewHolder foodViewHolder, Food food, int i) {
                foodViewHolder.food_name.setText(food.getName());

                Picasso.with(getBaseContext()).load(food.getImage()).into(foodViewHolder.food_image);

                final Food local = food;

                foodViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(FoodList.this, "Its good", Toast.LENGTH_SHORT).show();
                        Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                        foodDetail.putExtra("foodId",adapterProduct.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });
            }
        };
        recyclerView.setAdapter(adapterProduct);
    }
}
