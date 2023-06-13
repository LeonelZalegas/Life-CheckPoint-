package com.example.influencer.UI.CheckpointThemeChoose;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.influencer.databinding.ItemCheckpointhemeBinding;

import java.util.List;

public class CheckpointThemeChooseAdapter extends RecyclerView.Adapter<CheckpointThemeChooseAdapter.CheckpointThemeChooseViewHolder> {

    private List<CheckpointThemeItem> rowItems; // es nuestra lista de themes o categorias de checkpoints como tal

    public CheckpointThemeChooseAdapter(List<CheckpointThemeItem> rowItems) {
        this.rowItems = rowItems;
    }


    @NonNull
    @Override
    public CheckpointThemeChooseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ItemCheckpointhemeBinding binding = ItemCheckpointhemeBinding.inflate(layoutInflater, parent, false);
        return new CheckpointThemeChooseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckpointThemeChooseViewHolder holder, int position) {
        CheckpointThemeItem currentItem = rowItems.get(position);
        holder.binding.imageCheckpointThemeChoose.setImageResource(currentItem.getImageResourceId());
        holder.binding.textCheckpointThemeChoose.setText(currentItem.getText());
        holder.binding.maincardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), currentItem.getColor()));

    }

    @Override
    public int getItemCount() {
        return rowItems.size();
    }


    static class CheckpointThemeChooseViewHolder extends RecyclerView.ViewHolder {
        ItemCheckpointhemeBinding binding;

        public CheckpointThemeChooseViewHolder(@NonNull ItemCheckpointhemeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
