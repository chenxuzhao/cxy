package cn.trainservice.trainservice.service.model;

import android.content.Context;
import android.util.Log;

import cn.trainservice.trainservice.TrainServiceApplication;
import cn.trainservice.trainservice.service.food.FoodService;
import cn.trainservice.trainservice.service.movie.MovieService;

/**
 * Created by BrainWang on 4/18/2016.
 */
public class ServiceFactory {
//
//    public enum ServiceTypes {
//        movie, food;
//
//
//        public String getName() {
//            switch (this) {
//                case movie:
//                    return "Movie";
//                //  break;
//                case food:
//                    return "Food";
//                // break;
//            }
//            return "";
//        }
//
//        public String getLocaleName() {
//            switch (this) {
//                case movie:
//                    return "Movie";
//                //  break;
//                case food:
//                    return "Food";
//                // break;
//            }
//            return "";
//        }
//
//        public String getListUrl() {
//            switch (this) {
//                case movie:
//                    return "Movie";
//                //  break;
//                case food:
//                    return "Food";
//                // break;
//            }
//            return "";
//        }
//
//        public String[] getTypes() {
//            switch (this) {
//                case movie:
//                    return new String[]{"国产","国外"};
//                //  break;
//                case food:
//                    return new String[]{""};
//                // break;
//            }
//            return null;
//        }
//
//    }

    public static SingleSimpleService create(Context context, String serviceStr) {

        switch (serviceStr) {
            case "movie":
                Log.d("data3","move");
                return new MovieService(context,"Movies", TrainServiceApplication.getMovieListUrl(),null);
            case "food":
                return new FoodService(context,"Foods", TrainServiceApplication.getFoodListUrl(),null);
            case "commodity":
                return new FoodService(context,"Foods", TrainServiceApplication.getComListListUrl(),null);
        }
        return null;
    }

}
