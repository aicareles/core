package com.heaton.baselib.api;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * 获取json数据基类
 */

public class BaseResponse<T> implements ErrorStatus{

    @SerializedName("code")
    public int code;
    @SerializedName("msg")
    public String msg;
    @SerializedName("data")
    public T data;

    public boolean isSuccess() {
        return code == OK;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }

    public static class Wrapper {
        public String codeKey;
        public String msgKey;
        public String dataKey;

        public Wrapper(String codeKey, String msgKey, String dataKey) {
            this.codeKey = codeKey;
            this.msgKey = msgKey;
            this.dataKey = dataKey;
        }
    }

    public static class JsonAdapter implements JsonDeserializer<BaseResponse> {
        private Wrapper wrapper;

        public JsonAdapter(Wrapper wrapper) {
            this.wrapper = wrapper;
        }

        @Override
        public BaseResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                String jsonRoot = json.getAsJsonObject().toString() ;
                BaseResponse response = new BaseResponse();
                JSONObject jsobRespData = new JSONObject(jsonRoot) ;
                response.code = jsobRespData.getInt(wrapper==null?"status":wrapper.codeKey) ;
                String msg = wrapper == null ? "msg" : wrapper.msgKey;
                if (!jsobRespData.isNull(msg)){
                    response.msg = jsobRespData.getString(msg) ;
                }
                response.data = jsobRespData.get(wrapper==null?"data":wrapper.dataKey) ;
                return response;
            } catch (JSONException e) {
                throw new JsonParseException(e) ;
            }
        }
    }
}
