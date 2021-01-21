package com.bixin.launcher_tw.view.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bixin.launcher_tw.R;
import com.bixin.launcher_tw.model.LauncherApp;
import com.bixin.launcher_tw.model.adapter.RecyclerGridViewAdapter;
import com.bixin.launcher_tw.model.bean.AppInfo;
import com.bixin.launcher_tw.model.listener.OnAppUpdateListener;
import com.bixin.launcher_tw.model.listener.OnRecyclerViewItemListener;
import com.bixin.launcher_tw.model.tool.InterfaceCallBackManagement;
import com.bixin.launcher_tw.model.tool.StartActivityTool;

import java.util.ArrayList;

/**
 * @author Altair
 * @date :2019.10.28 上午 10:03
 * @description: AppList页面
 */
public class AppListActivity extends Activity implements OnRecyclerViewItemListener,
        OnAppUpdateListener {
    private final static String TAG = "AppListActivity";
    private LauncherApp myApplication;
    private ArrayList<AppInfo> appInfoArrayList = new ArrayList<>();
    private Context mContext;
    private StartActivityTool mActivityTools;
    RecyclerGridViewAdapter mRecyclerGridViewAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
        InterfaceCallBackManagement.getInstance().setOnAppUpdateListener(this);
        init();
        initData();
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init() {
        mContext = this;
        myApplication = LauncherApp.getInstance();
//        mActivityTools = new StartActivityTool(mContext);
        mActivityTools = new StartActivityTool();

    }

    private void initView() {
        RecyclerView recyclerView = findViewById(R.id.rcv_app);
        GridLayoutManager manager = new GridLayoutManager(this, 5);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        mRecyclerGridViewAdapter = new RecyclerGridViewAdapter(this, appInfoArrayList);
        recyclerView.setAdapter(mRecyclerGridViewAdapter);
        mRecyclerGridViewAdapter.setOnRecyclerViewItemListener(this);
    }


    private void initData() {
        getAppList();
    }

    public void getAppList() {
        appInfoArrayList.clear();
//        if (myApplication.getShowAppList().size() <= 3) {
            myApplication.initAppList();
//        }
        appInfoArrayList = myApplication.getShowAppList();
    }

    @Override
    public void onItemClickListener(int position, String packageName) {
        mActivityTools.launchAppByPackageName(packageName);
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void updateAppList() {
        Log.d(TAG, "updateAppList: ");
        if (mRecyclerGridViewAdapter != null) {
            getAppList();
            mRecyclerGridViewAdapter.setAppInfoArrayList(appInfoArrayList);
            mRecyclerGridViewAdapter.notifyDataSetChanged();
        }
    }
}