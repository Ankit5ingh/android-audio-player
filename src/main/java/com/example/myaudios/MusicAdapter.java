package com.example.myaudios;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> implements Filterable {

    private Context mContext;
    private ArrayList<MusicFiles> mFiles;
    private ArrayList<MusicFiles> mFilesSecondary;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView album_art;
        TextView file_name;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            file_name = itemView.findViewById(R.id.music_file_name);
            album_art = itemView.findViewById(R.id.music_img);
        }

        public TextView getFile_name() {
            return file_name;
        }
        public ImageView getAlbum_art(){
            return album_art;
        }
    }

    MusicAdapter (Context mContext, ArrayList<MusicFiles> mFiles){
        this.mContext = mContext;
        this.mFiles = mFiles;
        this.mFilesSecondary = new ArrayList<>(mFiles);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_items, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.file_name.setText(mFiles.get(position).getTitle());
        byte[] image = getAlbumArt(mFiles.get(position).getPath());
        if (image != null){
            Glide.with(mContext).asBitmap()
                    .load(image)
                    .into(holder.album_art);
        }else {
            Glide.with(mContext)
                    .load(R.drawable.default_image)
                    .into(holder.album_art);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PlayerActivity.class);
                intent.putExtra("Position", position);
                intent.putExtra("title", mFiles.get(position).getTitle());
                intent.putExtra("duration", mFiles.get(position).getDuration());
                intent.putExtra("path", mFiles.get(position).getPath());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFiles.size();
    }

    private byte[] getAlbumArt(String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }

    @Override
    public Filter getFilter() {
        return musicFilter;
    }

    public Filter musicFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<MusicFiles> filterList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0){
                filterList.addAll(mFilesSecondary);
            }else{
                String filterPattern = constraint.toString().toLowerCase(Locale.ROOT).trim();
                for (MusicFiles item :
                        mFilesSecondary) {
                    if (item.getTitle().toLowerCase(Locale.ROOT).contains(filterPattern)) {
                        filterList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filterList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mFiles.clear();
            mFiles.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
    
}
