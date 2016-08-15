package cn.trainservice.trainservice.service.movie;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import cn.trainservice.trainservice.R;
import cn.trainservice.trainservice.TrainServiceApplication;
import cn.trainservice.trainservice.service.model.SimpleServiceRecyclerViewAdapter;
import cn.trainservice.trainservice.service.model.SingleSimpleService;
import cn.trainservice.trainservice.service.movie.MovieDetailActivity;
import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoader;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

/**
 * Created by BrainWang on 4/18/2016.
 */
public class MovieService extends SingleSimpleService {

    private final ImageLoader imageLoader;
    private Context context;

    public MovieService(Context context, String name, String listUrl, String[] types) {
        super(name, listUrl, types);
        this.context = context;
        DefaultImageLoadHandler handler = new DefaultImageLoadHandler(this.context);
        handler.setLoadingResources(R.mipmap.loading);
        imageLoader = ImageLoaderFactory.create(this.context, handler);
    }


    @Override
    public SimpleServiceRecyclerViewAdapter getViewAdapter() {
        return new movieListAdapter(context, this.mListUrl);
    }

    @Override
    public Drawable getThemeColor() {
        return context.getResources().getDrawable(R.color.service_movie_main);
    }

    @Override
    public void jumpToDetail() {

    }


    private class movieListAdapter extends SimpleServiceRecyclerViewAdapter {
        movieListAdapter(Context context, String url) {
            super(context, url);

        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.movieitem_list_content, parent, false);
            return new MovieViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final String introduce =(String) mServiceItems.get(position).getExtraByKey("f_introduction");
            final String title =(String) mServiceItems.get(position).getExtraByKey("f_name");
            final String imgUrl=TrainServiceApplication.getMovieImagePath((String) mServiceItems.get(position).getExtraByKey("f_image_url"));
            final String filmType=(String) mServiceItems.get(position).getExtraByKey("f_type");
            final String filmYear=(String) mServiceItems.get(position).getExtraByKey("f_year");
            final String filmRate=(String) mServiceItems.get(position).getExtraByKey("f_point");
            final String tv_free= (String) mServiceItems.get(position).getExtraByKey("f_wprice");
            //final String genres=(String) mServiceItems.get(position).getExtraByKey("FRate");
            final String sourceUrl=TrainServiceApplication.getMoviePath((String) mServiceItems.get(position).getExtraByKey("f_source_url"));
            MovieViewHolder mvHolder = (MovieViewHolder) holder;
            mvHolder.thumb.loadImage(imageLoader, imgUrl );
            mvHolder.tv_rate.setText(filmRate);
            mvHolder.tv_year.setText(filmYear);
            mvHolder.tv_introduce.setText(introduce);
            mvHolder.tv_movie_list_title.setText(title);
            mvHolder.tv_free.setText(tv_free);
            mvHolder.tv_genres.setText(filmType);
            mvHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent =new Intent(context, MovieDetailActivity.class);

                    intent.putExtra("filmType",filmType);
                    intent.putExtra("introduce",introduce);
                    intent.putExtra("imgUrl",imgUrl);
                    Log.e("imgUrl","imgimg"+imgUrl);
                    intent.putExtra("title",title);
                    intent.putExtra("sourceUrl",sourceUrl);
                    intent.putExtra("filmYear",filmYear);
                    intent.putExtra("filmRate",filmRate);
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mServiceItems.size();
        }

        public class MovieViewHolder extends ViewHolder {
            final View view;
            final CubeImageView thumb;
            final TextView tv_introduce;
            final TextView tv_movie_list_title,tv_year,tv_rate,tv_genres,tv_free;

            public MovieViewHolder(View itemView) {
                super(itemView);
                view=itemView;
                thumb = (CubeImageView) itemView.findViewById(R.id.thumb_movie);
                tv_introduce = (TextView) itemView.findViewById(R.id.tv_movie_introduce);
                tv_movie_list_title= (TextView) itemView.findViewById(R.id.tv_movie_list_title);
                tv_genres= (TextView) itemView.findViewById(R.id.tv_genres);
                tv_year= (TextView) itemView.findViewById(R.id.tv_year);
                        tv_rate= (TextView) itemView.findViewById(R.id.tv_rate);
                tv_free = (TextView) itemView.findViewById(R.id.tv_free);
            }
        }

    }

}
