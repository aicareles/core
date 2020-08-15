package com.heaton.baselib.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * author: jerry
 * date: 20-5-22
 * email: superliu0911@gmail.com
 * des:
 */
public class Api {
    public static OkHttpClient mOkHttpClient;
    private static final String TAG = "Api";
    private static Object apiService;

    public static void init(ApiConfig apiConfig) {
        mOkHttpClient = OkHttpManager.getOkHttpClient();
        //设置使用okhttp网络请求
        /*Gson gson = new GsonBuilder()
                .registerTypeAdapter(BaseResponse.class, new BaseResponse.JsonAdapter(apiConfig.getWrapper()))
                .create();*/
        Retrofit retrofit = new Retrofit.Builder()
                //设置使用okhttp网络请求
                .client(mOkHttpClient)
                .baseUrl(apiConfig.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create())
//                .addConverterFactory(ResponseConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        apiService = retrofit.create(apiConfig.getApiServiceCls());
    }

    public static <T>T apiService(Class<T> tClass){
        return (T) apiService;
    }

    //请求的原始函数
    public static <T> void request(Observable<T> observable, Observer<T> observer) {
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

}
