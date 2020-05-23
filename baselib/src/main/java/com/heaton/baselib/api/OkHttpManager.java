package com.heaton.baselib.api;

import android.text.TextUtils;

import com.heaton.baselib.BaseCoreAPI;
import com.heaton.baselib.api.cookies.PersistentCookieStore;
import com.heaton.baselib.utils.AppUtils;
import com.heaton.baselib.utils.LogUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateFactory;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * author: jerry
 * date: 20-5-22
 * email: superliu0911@gmail.com
 * des:
 */
public class OkHttpManager {
    private static final String TAG = "OkHttpManager";
    private static OkHttpClient mOkHttpClient;
    private static SSLSocketFactory socketFactory;
    //设置缓存目录
    private static File cacheDirectory = new File(BaseCoreAPI.getContext().getCacheDir().getAbsolutePath(), "OkHttpCache");
    private static Cache cache = new Cache(cacheDirectory, 10 * 1024 * 1024);
    /**
     * 获取OkHttpClient对象
     * @return
     */
    public static OkHttpClient getOkHttpClient() {
        if (null == mOkHttpClient) {
            //添加HttpLogging拦截器，方便观察，上传和返回json
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            //同样okhttp3后也使用build设计模式
            mOkHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LogInterceptor())
                    //设置一个自动管理cookies的管理器
                    //.cookieJar(new CookiesManager())
                    //添加拦截器
                    .addInterceptor(new MyIntercepter())
                    //添加网络连接器
                    // .addNetworkInterceptor(new CookiesInterceptor(App.getInstance().getApplicationContext()))
                    //设置请求读写的超时时间
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .cache(cache)
                    //设置https
                    /*.hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    .sslSocketFactory(setCertificates(App.getInstance().getAssets().open("https.cer")))*/
                    .build();
        }
        return mOkHttpClient;
    }

    /**
     * 拦截器
     */
    private static class MyIntercepter implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            if (!AppUtils.isNetworkReachable(BaseCoreAPI.getContext())) {
                LogUtils.logi("MyIntercepter>>>[intercept]: 无网络");
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)//无网络时只从缓存中读取
                        .build();
            }
            Response response = chain.proceed(request);
            if (AppUtils.isNetworkReachable(BaseCoreAPI.getContext())) {
                int maxAge = 60 * 60; // 有网络时 设置缓存超时时间1个小时
                response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                int maxStale = 60 * 60 * 24 * 28; // 无网络时，设置超时为4周
                response.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
            return response;
            //添加公共头部
           /* Request request = chain.request()
                    .newBuilder()
                    .addHeader("token", TextUtils.isEmpty(App.getToken()) ? "" : App.getToken())
                    .addHeader("type", "app")
                    .addHeader("uid", TextUtils.isEmpty(App.getUid()) ? "" : App.getUid())
                    .build();
            return chain.proceed(request);*/
        }
    }


    /**
     * 自动管理Cookies
     */
    private static class CookiesManager implements CookieJar {
        private final PersistentCookieStore cookieStore = new PersistentCookieStore(BaseCoreAPI.getContext());

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            if (cookies != null && cookies.size() > 0) {
                for (Cookie item : cookies) {
                    cookieStore.add(url, item);
                }
            }
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            List<Cookie> cookies = cookieStore.get(url);
            return cookies;
        }
    }

    public static SSLSocketFactory setCertificates(InputStream... certificates) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            int index = 0;
            for (InputStream certificate : certificates) {
                String certificateAlias = Integer.toString(index++);
                keyStore.setCertificateEntry(certificateAlias, certificateFactory.generateCertificate(certificate));
                try {
                    if (certificate != null)
                        certificate.close();
                } catch (IOException e) {
                }
            }
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory =
                    TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            sslContext.init(null,
                    trustManagerFactory.getTrustManagers(),
                    new SecureRandom()
            );
            socketFactory = sslContext.getSocketFactory();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return socketFactory;
    }

    // OkHttp3 的监听
    private static class LogInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            okhttp3.Response response = chain.proceed(chain.request());
            okhttp3.MediaType mediaType = response.body().contentType();
            String content = response.body().string();
            Request request = chain.request();
//            LogUtils.loge(String.format("Sending request %s on %s%n%s", request.url(),  chain.connection(), request.headers()));
            LogUtils.loge(String.format("链接地址>>>>>%s", request.url()) + "\n"+content);
            return response.newBuilder().body(okhttp3.ResponseBody.create(mediaType, content)).build();
        }
    }
}
