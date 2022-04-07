package com.example.myjourneyjournal.adapter;

import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myjourneyjournal.R;
import com.example.myjourneyjournal.model.JourneyModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewHolder> {
    private final RVItemClickListener itemViewClickListener;
    private final RVItemClickListener delImgViewClickListener;
    private final RVItemClickListener editImgViewClickListener;
    private final ArrayList<JourneyModel> itemsList;

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView txtTitle, txtLocation;
        CircleImageView delImgView, editImgView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.j_image);
            txtTitle = itemView.findViewById(R.id.j_title);
            txtLocation = itemView.findViewById(R.id.j_location);
            delImgView = itemView.findViewById(R.id.j_delete);
            editImgView = itemView.findViewById(R.id.j_edit);

            itemView.setOnClickListener(this::itemViewOnClick);
            delImgView.setOnClickListener(this::delImgViewOnClick);
            editImgView.setOnClickListener(this::editImgViewOnClick);
        }

        private void editImgViewOnClick(View view) {
            editImgViewClickListener.onItemClick(view, getAdapterPosition());
        }

        private void delImgViewOnClick(View view) {
            delImgViewClickListener.onItemClick(view, getAdapterPosition());
        }

        public void itemViewOnClick(View view) {
            itemViewClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    public ItemsAdapter(
            ArrayList<JourneyModel> itemsList,
            RVItemClickListener itemViewClickListener,
            RVItemClickListener delImgViewClickListener,
            RVItemClickListener editImgViewClickListener
    ) {
        this.itemsList = itemsList;
        this.itemViewClickListener = itemViewClickListener;
        this.delImgViewClickListener = delImgViewClickListener;
        this.editImgViewClickListener = editImgViewClickListener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout._recyclerview_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        holder.txtTitle.setText(itemsList.get(position).getTitle());
        holder.txtLocation.setText(itemsList.get(position).getLocation());
        byte[] imageBlob = itemsList.get(position).getImage();
        if (imageBlob != null) {
            holder.imageView.setImageBitmap(
                    BitmapFactory.decodeByteArray(imageBlob, 0, imageBlob.length)
            );
        }
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }
}
