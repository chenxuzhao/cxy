package cn.trainservice.trainservice.service.serviceItem;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import cn.trainservice.trainservice.R;
import cn.trainservice.trainservice.service.CommodityRecoderActivity;
import cn.trainservice.trainservice.service.Commoditys;
import cn.trainservice.trainservice.service.food.FoodDetailActivity;
import cn.trainservice.trainservice.service.model.ServiceFactory;
import cn.trainservice.trainservice.service.model.SingleSimpleService;


/**
 * An activity representing a list of ServiceItems. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ServiceItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ServiceItemListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private SingleSimpleService service;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String title = "";
        String serviceName = intent.getStringExtra("serviceName");
        if (serviceName.equals("movie")) {

            title = "电影";
            setContentView(R.layout.activity_serviceitem_list);
        }
        else if(serviceName.equals("food")||serviceName.equals("commodity")){
            if(serviceName.equals("food"))
                title = "食物";
            else{
                title = "商店";
            }
            setContentView(R.layout.activity_food_item_list);
            Button anlance_bt = (Button) findViewById(R.id.banlance_bt);
            Button order_submited_bt = (Button) findViewById(R.id.order_submited_bt);
            order_submited_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Log.d("data4","order_submit");
                        Intent intent = new Intent(ServiceItemListActivity.this, CommodityRecoderActivity.class);
                        startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            anlance_bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Commoditys.getCommodityCounts() > 0) {
                        try {
                            Intent intent = new Intent(ServiceItemListActivity.this, FoodDetailActivity.class);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        AlertDialog later = new AlertDialog.Builder(ServiceItemListActivity.this).
                                setMessage("你还没有添加任何订单").
                                create();
                        later.show();
                    }

                }
            });
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        Intent intent=getIntent();
        //  String serviceName=intent.getStringExtra("serviceName");
        service = ServiceFactory.create(this, serviceName);
        //String

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        actionBar.setTitle(title);
        actionBar.setBackgroundDrawable(service.getThemeColor());
        recyclerView = (RecyclerView) findViewById(R.id.serviceitem_list);
        //  assert recyclerView != null;


        if (findViewById(R.id.serviceitem_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }
    }

    @Override
    protected void onResume() {

        recyclerView.setAdapter(service.getViewAdapter());
        super.onResume();
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
            //NavUtils.navigateUpFromSameTask(this);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setAdapter(service.getViewAdapter());
    }


}
