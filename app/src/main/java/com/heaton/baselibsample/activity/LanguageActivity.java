package com.heaton.baselibsample.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.heaton.baselib.app.language.LanguageManager;
import com.heaton.baselib.base.BaseActivity;
import com.heaton.baselib.base.recycleview.RecyclerAdapter;
import com.heaton.baselib.utils.LogUtils;
import com.heaton.baselibsample.MainActivity;
import com.heaton.baselibsample.R;
import com.heaton.baselibsample.adapter.LanguageAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * description $desc$
 * created by jerry on 2019/8/7.
 */
public class LanguageActivity extends BaseActivity {
    private static final String TAG = "LanguageActivity";
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private LanguageAdapter adapter;

    private String saveLanguage;


    Map<String, String> languages = new HashMap<String, String>(2){
        {
            put("中文", "zh");
            put("English", "en");
        }
    };

    @Override
    protected int layoutId() {
        return R.layout.activity_language;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void bindData() {

        toolbarTitle.setText("语言");

        adapter = new LanguageAdapter(this, new ArrayList<>(languages.keySet()));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        saveLanguage = LanguageManager.getSaveLanguage(this);
        LogUtils.logi("LanguageActivity>>>[bindData]: "+saveLanguage);


        List<String> values = new ArrayList<>(languages.values());
        for (int i=0; i<languages.size(); i++) {
            String lang = values.get(i);
            if (TextUtils.equals(lang, saveLanguage)){
                adapter.setPosition(i);
                adapter.notifyDataSetChanged();
            }
        }

    }

    @Override
    protected void bindListener() {
        super.bindListener();
        adapter.setOnItemClickListener((parent, view, lang, position) -> {
            adapter.setPosition(position);
            adapter.notifyDataSetChanged();
            if (!TextUtils.equals(saveLanguage, languages.get(lang))){
                LanguageManager.setSaveLanguage(this, languages.get(lang));
                LogUtils.logi("LanguageActivity>>>[bindListener]: "+languages.get(lang));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        restartApp(LanguageActivity.this, MainActivity.class);
                    }
                }, 1000);

            }
        });
    }

    //重启APP
    public void restartApp(Activity activity, Class<?> homeClass) {
        Intent intent = new Intent(activity, homeClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        // 杀掉进程
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

}
