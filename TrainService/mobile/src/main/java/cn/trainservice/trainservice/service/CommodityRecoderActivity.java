package cn.trainservice.trainservice.service;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.litesuits.http.exception.HttpException;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.request.StringRequest;
import com.litesuits.http.response.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.trainservice.trainservice.LoginActivity;
import cn.trainservice.trainservice.R;
import cn.trainservice.trainservice.TrainServiceApplication;
import cn.trainservice.trainservice.TrainTravel;
import cn.trainservice.trainservice.service.food.FoodDetailActivity;
import cn.trainservice.trainservice.service.model.ServiceItem;
import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoader;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

public class CommodityRecoderActivity extends AppCompatActivity {
    private ImageLoader imageLoader;
    private TextView order_commodity_price;
    private TextView ordre_commodity_counts;
    private BootstrapButton order_confirm_bt;
    private List<ServiceItem> orders = new ArrayList<>();
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commodity_recoder);

        order_commodity_price = (TextView) findViewById(R.id.order_commodity_price);
        ordre_commodity_counts = (TextView) findViewById(R.id.order_commodity_counts);


        DefaultImageLoadHandler handler = new DefaultImageLoadHandler(this);
        imageLoader = ImageLoaderFactory.create(this, handler);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        adapter = new FoodDetailAdapter();
        recyclerView = (RecyclerView) findViewById(R.id.serviceitem_list);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshSections();
            }
        });

        refreshSections();
    }

    private void refreshSections() {
        getSubmitedOrder();
    }

    private void getSubmitedOrder() {
        swipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String uploadUrl = TrainServiceApplication.getSubmitOrderInfo();
                    TrainServiceApplication.getLiteHttp(CommodityRecoderActivity.this).execute(new StringRequest(uploadUrl).setHttpListener(
                            new HttpListener<String>() {
                                @Override
                                public void onSuccess(String data, Response<String> response) {
                                    try {
                                        JSONObject Json = new JSONObject(data);
                                        Log.d("data5", "order3" + data);
                                        boolean result = Json.getBoolean("result");
                                        if (result) {
                                            orders.clear();
                                            if (Json.has("orders")) {
                                                JSONArray items = Json.getJSONArray("orders");
                                                for (int i = 0; i < items.length(); i++) {
                                                    JSONObject js = (JSONObject) items.get(i);
                                                    Iterator it = js.keys();
                                                    ServiceItem item = new ServiceItem();
                                                    while (it.hasNext()) {
                                                        String key = String.valueOf(it.next());
                                                        String value = (String) js.get(key);
                                                        item.addExtraInfo(key, value);
                                                    }
                                                    orders.add(item);
                                                }
                                            }
                                            adapter.notifyDataSetChanged();

                                        } else {

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }


                                }

                                @Override
                                public void onFailure(HttpException e, Response<String> response) {
                                    Log.i("httpp", e.toString());
                                }
                            }
                    ));
                }
            }).start();

        swipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class FoodDetailAdapter extends RecyclerView.Adapter {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.confirm_order_detail, parent, false);
            return new FoodViewHodeler(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final int pstion = position;
            final String title = (String) orders.get(position).getExtraByKey("comm_name");
            final String imgUrl = TrainServiceApplication.getaFoodImagePath((String) orders.get(position).getExtraByKey("comm_picture_url"));
            final int number = Integer.parseInt((String) orders.get(position).getExtraByKey("commodity_num"));
            final String comm_id = (String) orders.get(position).getExtraByKey("commodity_id");
            final String price = (String) orders.get(position).getExtraByKey("comm_price");
            final String discount = (String) orders.get(position).getExtraByKey("comm_discounted");
            final String time = (String) orders.get(position).getExtraByKey("ordertime");
            final String order_id = (String) orders.get(position).getExtraByKey("order_id");
            final FoodViewHodeler mvHolder = (FoodViewHodeler) holder;
            Log.d("data4", imgUrl);
            mvHolder.imge.loadImage(imageLoader, imgUrl);
            mvHolder.tv_food_list_title.setText(title);
            float Price = Float.parseFloat(price) * (100 - Float.parseFloat(discount)) / 100;
            DecimalFormat df = new DecimalFormat();
            df.applyPattern("##0.00");
            mvHolder.tv_food_price.setText(String.format("￥%s",df.format(Price)));
            mvHolder.tv_food_counts.setText(String.format("%d 份", number));
            mvHolder.oeder_submit_time.setText(time);
            mvHolder.oeder_submit_id.setText(order_id);
            mvHolder.order_confirm_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    confirmOrder((String) orders.get(pstion).getExtraByKey("order_id"), (String) orders.get(pstion).getExtraByKey("order_detail_id"));
                }
            });
        }

        @Override
        public int getItemCount() {
            return orders.size();
        }
    }

    private void confirmOrder(String order_id, String order_detail_id) {
        new Thread(new ConfirmOrder(order_id, order_detail_id)).start();
    }

    private class ConfirmOrder implements Runnable {
        private String order_id;
        private String order_detail_id;

        ConfirmOrder(String order, String order_detail_id) {
            this.order_id = order;
            this.order_detail_id = order_detail_id;

        }

        @Override
        public void run() {
            String uploadUrl = TrainServiceApplication.getConfirmOrderInfo(order_id, order_detail_id);
            Log.d("data5", uploadUrl);
            TrainServiceApplication.getLiteHttp(CommodityRecoderActivity.this).execute(new StringRequest(uploadUrl).setHttpListener(
                    new HttpListener<String>() {
                        @Override
                        public void onSuccess(String data, Response<String> response) {
                            try {
                                JSONObject Json = new JSONObject(data);
                                Log.d("data5", "order" + data);
                                boolean result = Json.getBoolean("result");
                                if (result) {
                                    Log.d("data5", "order refresh");
                                    refreshSections();
                                } else {

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }

                        @Override
                        public void onFailure(HttpException e, Response<String> response) {
                            Log.i("httpp", e.toString());
                        }
                    }
            ));
        }
    }

    private class FoodViewHodeler extends RecyclerView.ViewHolder {
        final View view;
        final CubeImageView imge;
        final TextView tv_food_list_title, tv_food_price, tv_food_counts, oeder_submit_time, oeder_submit_id;
        final Button order_confirm_bt;

        FoodViewHodeler(View itemView) {
            super(itemView);
            view = itemView;
            imge = (CubeImageView) view.findViewById(R.id.thumb_food);
            tv_food_list_title = (TextView) view.findViewById(R.id.tv_food_list_title);
            tv_food_price = (TextView) view.findViewById(R.id.tv_food_price);
            tv_food_counts = (TextView) view.findViewById(R.id.tv_food_counts);
            oeder_submit_time = (TextView) view.findViewById(R.id.oeder_submit_time);
            oeder_submit_id = (TextView) view.findViewById(R.id.order_submit_id);
            order_confirm_bt = (Button) view.findViewById(R.id.order_confirm_bt);
        }
    }
}


