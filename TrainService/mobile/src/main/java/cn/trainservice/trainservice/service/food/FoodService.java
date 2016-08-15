package cn.trainservice.trainservice.service.food;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapLabel;
import com.beardedhen.androidbootstrap.api.attributes.BootstrapBrand;

import java.security.PrivateKey;

import cn.trainservice.trainservice.R;
import cn.trainservice.trainservice.TrainServiceApplication;
import cn.trainservice.trainservice.service.Commoditys;
import cn.trainservice.trainservice.service.model.SimpleServiceRecyclerViewAdapter;
import cn.trainservice.trainservice.service.model.SingleSimpleService;
import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoader;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

/**
 * Created by BrainWang on 4/18/2016.
 */
public class FoodService extends SingleSimpleService {

    private final ImageLoader imageLoader;
    private Context context;

    public FoodService(Context context, String name, String listUrl, String[] types) {
        super(name, listUrl, types);
        this.context = context;
        DefaultImageLoadHandler handler = new DefaultImageLoadHandler(this.context);
        handler.setLoadingResources(R.mipmap.loading);
        imageLoader = ImageLoaderFactory.create(this.context, handler);
    }


    @Override
    public SimpleServiceRecyclerViewAdapter getViewAdapter() {
        return new foodListAdapter(context, this.mListUrl);
    }

    @Override
    public Drawable getThemeColor() {
        return context.getResources().getDrawable(R.color.service_dining_main);
    }

    @Override
    public void jumpToDetail() {

    }


    private class foodListAdapter extends SimpleServiceRecyclerViewAdapter {
        // private ArrayList<ServiceItem> mMovies =new ArrayList<>();

        foodListAdapter(Context context, String url) {
            super(context, url);

        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fooditem_list_content, parent, false);
            return new FoodViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final String title = (String) mServiceItems.get(position).getExtraByKey("comm_name");

            final String imgUrl = TrainServiceApplication.getaFoodImagePath((String) mServiceItems.get(position).getExtraByKey("comm_picture_url"));
            Log.d("data4","copy"+imgUrl);
            final String number = (String) mServiceItems.get(position).getExtraByKey("comm_counts");
            String price = (String) mServiceItems.get(position).getExtraByKey("comm_price");
            final int discont = Integer.parseInt((String) mServiceItems.get(position).getExtraByKey("comm_discounted"));
            final String comm_id = (String) mServiceItems.get(position).getExtraByKey("comm_id");
            final int commodity_number = Integer.parseInt(number);
            final int commodity_type = Integer.parseInt((String) mServiceItems.get(position).getExtraByKey("comm_type"));
            final FoodViewHolder mvHolder = (FoodViewHolder) holder;
            mvHolder.thumb.loadImage(imageLoader, imgUrl);
            mvHolder.counts_sub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int current_num = Integer.parseInt(mvHolder.tv_counts.getText().toString());
                    if (current_num > 0) {
                        current_num--;
                        mvHolder.tv_counts.setText(String.format("%d", current_num));
                        if (current_num <= 0) {
                            mvHolder.counts_sub.setEnabled(false);
                            mvHolder.choose_box.setChecked(false);
                            Commoditys.removeCommodity(comm_id );
                        }
                        Commoditys.subCommodity(comm_id,1);
                    }
                }
            });

            mvHolder.counts_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int current_num = Integer.parseInt(mvHolder.tv_counts.getText().toString());
                    if (current_num < commodity_number) {
                        current_num++;
                        mvHolder.counts_sub.setEnabled(true);
                        mvHolder.tv_counts.setText(String.format("%d", current_num));
                        if (current_num >= commodity_number) {
                            mvHolder.counts_add.setEnabled(false);
                        }
                        Commoditys.addCommodity(comm_id,1);
                    }
                }
            });


            mvHolder.tv_food_list_title.setText(title);
            mvHolder.tv_food_price.setText(String.format("￥%s", price));
            if (discont > 0) {
                mvHolder.tv_food_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                price = Double.toString(((Double.parseDouble(price) * (100-discont)) / 100));
                mvHolder.tv_discount_price.setText(String.format("￥%s", price));
            } else {
                mvHolder.tv_discount_price.setVisibility(View.INVISIBLE);
            }
            final String Price = price;
            mvHolder.tv_food_counts.setText(number);
            mvHolder.choose_box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        int num = Integer.parseInt(mvHolder.tv_counts.getText().toString());
                        if (num == 0)
                            num = 1;
                        mvHolder.counts_sub.setEnabled(true);
                        Commoditys.addCommodity(comm_id, new Commoditys().new Commodity(comm_id,title, Float.parseFloat(Price), imgUrl, num, commodity_number,commodity_type));
                    } else {
                        Commoditys.removeCommodity(comm_id);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return mServiceItems.size();
        }

        class FoodViewHolder extends ViewHolder {
            final View view;
            final CubeImageView thumb;
            final TextView tv_food_list_title, tv_food_counts, tv_discount_price;
            final TextView tv_food_price;
            final CheckBox choose_box;
            final TextView tv_counts;
            final Button counts_add;
            final Button counts_sub;

            FoodViewHolder(View itemView) {
                super(itemView);
                view = itemView;
                thumb = (CubeImageView) itemView.findViewById(R.id.thumb_food);
                tv_food_price = (TextView) itemView.findViewById(R.id.tv_food_price);
                tv_food_counts = (TextView) itemView.findViewById(R.id.tv_food_counts);
                tv_food_list_title = (TextView) itemView.findViewById(R.id.tv_food_list_title);
                tv_discount_price = (TextView) itemView.findViewById(R.id.tv_discount_price);
                choose_box = (CheckBox) itemView.findViewById(R.id.choose_box);
                tv_counts = (TextView) itemView.findViewById(R.id.commodity_counts);
                counts_add = (Button) itemView.findViewById(R.id.counts_add);
                counts_sub = (Button) itemView.findViewById(R.id.counts_sub);
            }
        }

    }

}
