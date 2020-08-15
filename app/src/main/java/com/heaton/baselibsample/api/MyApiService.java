package com.heaton.baselibsample.api;

import com.heaton.baselib.api.BaseResponse;
import com.heaton.baselib.bean.UpdateVO;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * author: jerry
 * date: 20-5-23
 * email: superliu0911@gmail.com
 * des:
 */
public interface MyApiService {

    @FormUrlEncoded
    @POST("app/lastUpdate")
    Observable<BaseResponse<UpdateVO>> update(@FieldMap Map<String, String> map);
}
