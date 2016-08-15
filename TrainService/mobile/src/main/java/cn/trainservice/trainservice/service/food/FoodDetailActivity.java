package cn.trainservice.trainservice.service.food;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.litesuits.http.data.NameValuePair;
import com.litesuits.http.listener.HttpListener;
import com.litesuits.http.request.AbstractRequest;
import com.litesuits.http.request.StringRequest;
import com.litesuits.http.request.content.UrlEncodedFormBody;
import com.litesuits.http.request.param.HttpMethods;
import com.litesuits.http.response.Response;
import com.litesuits.http.utils.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import cn.trainservice.trainservice.LoadingActivity;
import cn.trainservice.trainservice.LoginActivity;
import cn.trainservice.trainservice.MainActivity;
import cn.trainservice.trainservice.R;
import cn.trainservice.trainservice.SettingsActivity;
import cn.trainservice.trainservice.TrainServiceApplication;
import cn.trainservice.trainservice.TrainTravel;
import cn.trainservice.trainservice.journey.view.TicketInfo;
import cn.trainservice.trainservice.service.Commoditys;
import cn.trainservice.trainservice.service.model.ServiceItem;
import cn.trainservice.trainservice.service.serviceItem.ServiceItemListActivity;
import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoader;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.image.impl.DefaultImageLoadHandler;

/**
 * An activity representing a single Food detail screen. This
 * activity is only used narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 */
public class FoodDetailActivity extends AppCompatActivity {

    private ImageLoader imageLoader;
    private TextView detail_commodity_price;
    private TextView detail_commodity_counts;
    private Button order_submit_bt;
    private FoodDetailAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        detail_commodity_price = (TextView) findViewById(R.id.detail_commodity_price);
        detail_commodity_counts = (TextView) findViewById(R.id.detail_commodity_counts);

        updateOrder();
        DefaultImageLoadHandler handler = new DefaultImageLoadHandler(this);
        imageLoader = ImageLoaderFactory.create(this, handler);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.serviceitem_list);
        adapter = new FoodDetailAdapter(Commoditys.commoditys);
        recyclerView.setAdapter(adapter);
        order_submit_bt = (Button) findViewById(R.id.order_submit_bt);
        order_submit_bt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                submit_order();
            }
        });
    }

    private void updateOrder() {
        DecimalFormat df = new DecimalFormat();
        df.applyPattern("##0.00");
        detail_commodity_price.setText(df.format(Commoditys.getCommoditySumPrice()));
        detail_commodity_counts.setText(String.format("%d", Commoditys.getCommodityCounts()));
    }

    private void submit_order() {
        if (TrainTravel.islogin) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    String uploadUrl = TrainServiceApplication.submit_order();
                    final StringRequest postRequest = new StringRequest(uploadUrl)
                            .setMethod(HttpMethods.Post)
                            .setHttpListener(new HttpListener<String>(true, false, true) {
                                @Override
                                public void onStart(AbstractRequest<String> request) {
                                    super.onStart(request);
                                }

                                @Override
                                public void onUploading(AbstractRequest<String> request, long total, long len) {

                                }

                                @Override
                                public void onEnd(Response<String> response) {

                                    if (response.isConnectSuccess()) {

                                        String jsonstr = response.getResult();
                                        try {
                                            Log.d("order", jsonstr);
                                            JSONArray js = new JSONArray(jsonstr);
                                            int code = -1;
                                            Map<Integer, List<String>> error = new HashMap<>();
                                            for (int i = 0; i < js.length(); i++) {
                                                JSONObject js1 = js.getJSONObject(i);
                                                if (js1.getBoolean("result")) {
                                                    if (code == -1) {
                                                        code = 0;
                                                    }
                                                    adapter.setCommoditieStatus(js1.getString("comm_id"));
                                                    Commoditys.addsubmitCommoditys(js1.getString("comm_id"));

                                                } else {
                                                    int err = js1.getInt("code");
                                                    if (code != 1)
                                                        code = err;
                                                    if (error.get(err) != null)
                                                        error.get(err).add(js1.getString("comm_id"));
                                                    else {
                                                        List<String> comm = new ArrayList<String>();
                                                        comm.add(js1.getString("comm_id"));
                                                        error.put(err, comm);
                                                    }
                                                    error_progress(code, error, js.length());

                                                }

                                            }
                                            adapter.notifyDataSetChanged();
                                            if (code == 0) {
                                                AlertDialog later = new AlertDialog.Builder(FoodDetailActivity.this).
                                                        setMessage("已提交").
                                                        create();
                                                later.show();
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        HttpUtil.showTips(FoodDetailActivity.this, "Upload Failure", response.getException() + "");
                                    }

                                }
                            });

                    LinkedList<NameValuePair> pList = new LinkedList<>();
                    String str = Commoditys.buildOrder();
                    Log.d("order", str);
                    DecimalFormat df = new DecimalFormat();
                    df.applyPattern("##0.00");
                    pList.add(new NameValuePair("user_id", TrainTravel.user_id));
                    pList.add(new NameValuePair("position", TrainTravel.Carrige + "_" + TrainTravel.seat));
                    pList.add(new NameValuePair("sum_price", df.format(Commoditys.getCommoditySumPrice())));
                    pList.add(new NameValuePair("sum_counts", Integer.toString(Commoditys.getCommodityCounts())));
                    pList.add(new NameValuePair("order", str));
                    try {
                        postRequest.setHttpBody(new UrlEncodedFormBody(pList));
                        TrainServiceApplication.getLiteHttp(FoodDetailActivity.this).executeAsync(postRequest);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {

            startActivity(new Intent(FoodDetailActivity.this, LoginActivity.class));

        }

    }

    private void error_progress(int code, Map<Integer, List<String>> error, int sum) {
        StringBuilder names = new StringBuilder();
        if (code == 1) {
            names.append("当前用户较多服务器繁忙");

        } else if (code == 2) {
            List<String> comms = error.get(2);
            for (String comm_id : comms) {
                names.append(Commoditys.getCommodityname(comm_id));
                names.append(",");
            }
            names.append("当前物品不足");
        }
        if (sum > error.size()) {
            names.append("部分提交完成");
        }
        AlertDialog later = new AlertDialog.Builder(FoodDetailActivity.this).
                setMessage(names).
                create();
        later.show();
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
        private List<Commoditys.Commodity> commodities = new ArrayList<>();

        public FoodDetailAdapter(List<Commoditys.Commodity> commodities) {
            copy(commodities);
        }

        private void copy(List<Commoditys.Commodity> commodities) {
            int i = 0;
            for (Commoditys.Commodity commodity : commodities) {
                this.commodities.add(i, commodity);
                i++;
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.submit_food_list, parent, false);
            return new FoodViewHodeler(view);
        }


        public void setCommoditieStatus(String index) {
            Iterator<Commoditys.Commodity> iter = commodities.iterator();
            while (iter.hasNext()) {
                Commoditys.Commodity commodity = iter.next();
                if (commodity.getCommodity_id().equals(index)) {
                    commodity.setSubmitted(true);
                }
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final String title = commodities.get(position).getName();
            final String imgUrl = commodities.get(position).getImg_url();
            final int number = commodities.get(position).getNumber();

            final int sum_number = commodities.get(position).getSum_number();
            final String comm_id = commodities.get(position).getCommodity_id();
            float price = commodities.get(position).getPrice();
            final int commodity_type = commodities.get(position).getCommodity_type();
            final FoodViewHodeler mvHolder = (FoodViewHodeler) holder;
            Log.d("data4", imgUrl);
            mvHolder.imge.loadImage(imageLoader, imgUrl);
            mvHolder.commodity_counts.setText(Integer.toString(number));
            if (number > 0)
                mvHolder.counts_sub.setEnabled(true);
            mvHolder.counts_sub.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int current_num = Integer.parseInt(mvHolder.commodity_counts.getText().toString());
                    if (current_num > 0) {
                        current_num--;
                        mvHolder.commodity_counts.setText(String.format("%d", current_num));
                        if (current_num <= 0) {
                            mvHolder.counts_sub.setEnabled(false);
                        }
                        Commoditys.subCommodity(comm_id, 1);
                    }

                    updateOrder();
                }
            });

            mvHolder.counts_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int current_num = Integer.parseInt(mvHolder.commodity_counts.getText().toString());
                    if (current_num < sum_number) {
                        current_num++;
                        mvHolder.counts_sub.setEnabled(true);
                        mvHolder.commodity_counts.setText(String.format("%d", current_num));
                        if (current_num >= sum_number) {
                            mvHolder.counts_add.setEnabled(false);
                        }
                        Commoditys.addCommodity(comm_id, 1);
                    }
                    updateOrder();
                }

            });
            mvHolder.tv_food_list_title.setText(title);
            mvHolder.tv_food_price.setText(String.format("￥%s", price));
            if (commodities.get(position).isSubmitted()) {
                mvHolder.tv_submit.setText("已提交");
            } else {
                mvHolder.tv_submit.setText("未提交");
            }
        }

        @Override
        public int getItemCount() {
            return commodities.size();
        }
    }

    private class FoodViewHodeler extends RecyclerView.ViewHolder {
        final View view;
        final CubeImageView imge;
        final TextView tv_food_list_title, tv_food_price, commodity_counts;
        final TextView tv_submit;
        final Button counts_sub, counts_add;

        FoodViewHodeler(View itemView) {
            super(itemView);
            view = itemView;
            imge = (CubeImageView) view.findViewById(R.id.thumb_food);
            tv_food_list_title = (TextView) view.findViewById(R.id.tv_food_list_title);
            tv_food_price = (TextView) view.findViewById(R.id.tv_food_price);
            commodity_counts = (TextView) view.findViewById(R.id.commodity_counts);
            counts_add = (Button) view.findViewById(R.id.counts_add);
            counts_sub = (Button) view.findViewById(R.id.counts_sub);
            tv_submit = (TextView) view.findViewById(R.id.tv_submitted);
        }
    }
}
