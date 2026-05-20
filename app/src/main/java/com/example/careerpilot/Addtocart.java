package com.example.careerpilot;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Addtocart extends AppCompatActivity {

    private RecyclerView recyclerViewCart;
    private Button btnClearCart;
    private CartAdapter cartAdapter;
    private List<String> cartItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtocart);

        // Initialize views
        recyclerViewCart = findViewById(R.id.recycler_view_cart);
        btnClearCart = findViewById(R.id.btn_clear_cart);

        // Initialize the cart items list and adapter
        cartItems = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItems);

        // Set up RecyclerView
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCart.setAdapter(cartAdapter);

        // Load ACTUAL data from SharedPreferences
        loadCartItems();

        // Set up the Clear Cart button click listener
        btnClearCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. Clear from Permanent Storage
                SharedPreferences sp = getSharedPreferences("CareerCart", Context.MODE_PRIVATE);
                sp.edit().remove("items").apply();

                // 2. Clear from UI List
                cartItems.clear();
                cartAdapter.notifyDataSetChanged();

                Toast.makeText(Addtocart.this, "Cart cleared successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadCartItems() {
        // Retrieve the saved courses from SharedPreferences
        SharedPreferences sp = getSharedPreferences("CareerCart", Context.MODE_PRIVATE);
        Set<String> savedItems = sp.getStringSet("items", new HashSet<>());

        cartItems.clear();
        if (savedItems.isEmpty()) {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
        } else {
            cartItems.addAll(savedItems);
        }
        cartAdapter.notifyDataSetChanged();
    }

    // CartAdapter nested class
    private class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

        private List<String> cartItems;

        public CartAdapter(List<String> cartItems) {
            this.cartItems = cartItems;
        }

        @NonNull
        @Override
        public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Uses standard Android list item layout
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new CartViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
            String item = cartItems.get(position);
            holder.textView.setText(item);
        }

        @Override
        public int getItemCount() {
            return cartItems.size();
        }

        class CartViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            CartViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}