package cn.trainservice.trainservice.service;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import cn.trainservice.trainservice.TrainTravel;

/**
 * Created by chenxu on 2016/8/1.
 */

public class Commoditys {
    public static boolean order_submited = false;
    public static List<Commodity> commoditys = new ArrayList<>();
    public static List<Commodity> commodities_submited = new ArrayList<>();

    public static void reset() {
        commoditys.clear();
        commodities_submited.clear();
    }

    public static void addCommodity(String index, Commodity commodity) {
        boolean exit_flag = false;
        for (Commodity commodity1 : commoditys) {
            if (commodity1.getCommodity_id().equals(index)) {
                commodity1.number += commodity.getNumber();
                exit_flag = true;
                break;
            }
        }

        if (!exit_flag) {
            commoditys.add(commodity);
        }
    }

    public static boolean addCommodity(String index, int number) {
        for (Commodity commodity1 : commoditys) {
            if (commodity1.getCommodity_id().equals(index)) {
                commodity1.number += number;
                return true;
            }
        }
        return false;
    }

    public static boolean subCommodity(String index, int number) {
        for (Commodity commodity1 : commoditys) {
            if (commodity1.getCommodity_id().equals(index)) {
                commodity1.number -= number;
                return true;
            }
        }
        return false;
    }

    public static int getCommodetys() {
        return commoditys.size();
    }

    public static void removeCommodity(String index) {
        Iterator<Commodity> iter = commoditys.iterator();
        while (iter.hasNext()) {
            Commodity commodity = iter.next();
            if (commodity.commodity_id.equals(index)) {

                iter.remove();
            }
        }
    }

    public static void addsubmitCommoditys(String index) {
        Iterator<Commodity> iter = commoditys.iterator();
        while (iter.hasNext()) {
            Commodity commodity = iter.next();
            if (commodity.commodity_id.equals(index)) {
                commodity.submitted = true;
                commodities_submited.add(commodity);
                Log.d("order", "已移除");
                iter.remove();
            }
        }
    }

    public static String getCommodityname(String index) {
        Iterator<Commodity> iter = commoditys.iterator();
        while (iter.hasNext()) {
            Commodity commodity = iter.next();
            if (commodity.commodity_id.equals(index)) {
                return commodity.name;
            }
        }
        return null;
    }

    public static int getCommodityCounts() {
        int sum = 0;
        for (Commodity commodity : commoditys) {
            sum += commodity.number;
        }

        return sum;
    }

    public static float getCommoditySumPrice() {
        float sum = 0;
        for (Commodity commodity : commoditys) {
            sum += commodity.number * commodity.price;
        }
        BigDecimal b = new BigDecimal(sum);
        float f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        //   b.setScale(2,  BigDecimal.ROUND_HALF_UP)  表明四舍五入，保留两位小数
        return f1;
    }

    public static String buildOrder() {

        StringBuilder str = new StringBuilder();
        for (Commodity commodity : commoditys) {
            if (commodity.getNumber() > 0 && !commodity.isSubmitted()) {

                str.append(commodity.commodity_id + ",");
                str.append(commodity.commodity_type + ",");
                str.append(commodity.number + ",");
                str.append(commodity.price);
                str.append("_");
            }

        }


        return str.toString();
    }

    public class Commodity {

        private String name;
        private String commodity_id;
        private float price;
        private String img_url;
        private int number;
        private int sum_number;
        private int commodity_type;


        private boolean submitted;

        public Commodity(String commodity_id, String name, float price, String img_url, int number, int sum, int commodity_type) {
            this.commodity_id = commodity_id;
            this.name = name;
            this.price = price;
            this.img_url = img_url;
            this.number = number;
            this.sum_number = sum;
            this.commodity_type = commodity_type;
            this.submitted = false;
        }

        public String getName() {
            return name;
        }


        public float getPrice() {
            return price;
        }

        public String getImg_url() {
            return img_url;
        }

        public int getNumber() {
            return number;
        }

        public int getCommodity_type() {
            return commodity_type;
        }

        public int getSum_number() {
            return sum_number;
        }

        public String getCommodity_id() {
            return commodity_id;
        }

        public boolean isSubmitted() {
            return submitted;
        }

        public void setSubmitted(boolean submitted) {
            this.submitted = submitted;
        }

    }

}
