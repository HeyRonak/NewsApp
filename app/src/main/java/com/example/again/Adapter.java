package com.example.again;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestFutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.again.Article;

import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.viewHolder> implements AdapterView.OnItemClickListener {

    private List<Article> articleList;
    private Context context;

    public Adapter(List<Article> articleList, Context context) {
        this.articleList = articleList;
        this.context = context;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) { }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item,viewGroup,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder viewHolder, int i) {

        Article model  =  articleList.get(i);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(Utils.getRandomDrawbleColor());
        requestOptions.error(Utils.getRandomDrawbleColor());
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.centerCrop();

        Glide.with(context)
                .load(model.getUrlToImage())
                .apply(requestOptions)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        viewHolder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        viewHolder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                }).transition(DrawableTransitionOptions.withCrossFade())
                   .into(viewHolder.imageView);


        viewHolder.title.setText(model.getTitle());
        viewHolder.desc.setText(model.getDescription());
        viewHolder.source.setText(model.getSource().getName());
        viewHolder.time.setText(" \u2022 " + Utils.DateToTimeFormat(model.getPublishedAt()));
        viewHolder.published_At.setText(Utils.DateFormat(model.getPublishedAt()));
        viewHolder.author.setText((model.getAuthor()));


        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,News_Detail_Activity.class);

//                Article article = articleList.get(i);
                intent.putExtra("url",model.getUrl());
                intent.putExtra("title",model.getTitle());
                intent.putExtra("img",model.getUrlToImage());
                intent.putExtra("date",model.getPublishedAt());
                intent.putExtra("source",model.getSource().getName());
                intent.putExtra("author",model.getAuthor());

                intent.putExtra("description",model.getDescription());

                context.startActivity(intent);

            }
        });







    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }


    public class viewHolder extends RecyclerView.ViewHolder{

        TextView title,desc,author,published_At,source,time;
        ImageView imageView;
        ProgressBar progressBar;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.desc);
            author = itemView.findViewById(R.id.author);
            published_At = itemView.findViewById(R.id.published_at);
            source = itemView.findViewById(R.id.source);
            time =itemView.findViewById(R.id.time);

            imageView = itemView.findViewById(R.id.imgView);
            progressBar = itemView.findViewById(R.id.progress_load_image);

        }
    }

}
