package com.my.cookme;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class recycleAdapter extends RecyclerView.Adapter<recycleAdapter.MyViewHolder> {
    private List<String> ingredientArrayList;

    public recycleAdapter(List<String> ingredients) {
        this.ingredientArrayList = ingredients;
    }

    public class  MyViewHolder extends  RecyclerView.ViewHolder {
        private AutoCompleteTextView name;

        public MyViewHolder(final View view) {
            super(view);
            name = view.findViewById(R.id.editTextIngredientName);
        }
    }

    @NonNull
    @Override
    public recycleAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.ingredients_list_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull recycleAdapter.MyViewHolder holder, int position) {
        String name = ingredientArrayList.get(position);
        if (name == null)
            return;
        holder.name.setText(name);
    }

    @Override
    public int getItemCount() {
        return ingredientArrayList.size();
    }
}
