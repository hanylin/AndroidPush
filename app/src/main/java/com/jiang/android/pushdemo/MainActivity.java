package com.jiang.android.pushdemo;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jiang.android.push.*;
import com.jiang.android.push.BuildConfig;
import com.jiang.android.push.utils.RomUtil;
import com.jiang.android.rvadapter.BaseAdapter;
import com.jiang.android.rvadapter.BaseViewHolder;
import com.jiang.android.rvadapter.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private AppCompatButton register;
    private AppCompatButton unregister;
    private AppCompatButton open;
    private AppCompatButton close;
    private RecyclerView recyclerView;

    List<String> mDatas = new ArrayList<>();
    PushInterface pushInterface = new PushInterface() {
        @Override
        public void onRegister(Context context, String registerID) {
            addData("onRegister id:" + registerID+"; target: "+ RomUtil.rom().toString());
        }

        @Override
        public void onUnRegister(Context context) {
            addData("onUnRegister");
        }

        @Override
        public void onPaused(Context context) {
            addData("onPaused");
        }

        @Override
        public void onResume(Context context) {
            addData("onResume");
        }

        @Override
        public void onMessage(Context context, Message message) {

            addData(message.toString());
        }

        @Override
        public void onMessageClicked(Context context, Message message) {

            addData("MessageClicked: " + message.toString());
        }

        @Override
        public void onCustomMessage(Context context, Message message) {

            addData(message.toString());
        }

        @Override
        public void onAlias(Context context, String alias) {
            addData("alias: " + alias);
        }
    };
    private AppCompatButton alias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_PHONE_STATE},
                    100);
        }

        //android.permission.WRITE_SETTINGS
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.WRITE_SETTINGS},
                    1300);
        }
        register = (AppCompatButton) findViewById(R.id.register);
        register.setOnClickListener(this);
        unregister = (AppCompatButton) findViewById(R.id.unregister);
        unregister.setOnClickListener(this);
        open = (AppCompatButton) findViewById(R.id.open);
        open.setOnClickListener(this);
        close = (AppCompatButton) findViewById(R.id.close);
        alias = (AppCompatButton) findViewById(R.id.alias);
        close.setOnClickListener(this);
        alias.setOnClickListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.rv);

        Push.setPushInterface(pushInterface);

        mDatas.add("--------- log ----------");
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        BaseAdapter adapter = new BaseAdapter() {
            @Override
            protected void onBindView(BaseViewHolder baseViewHolder, int i) {

                TextView title = baseViewHolder.getView(R.id.tv);
                title.setText(mDatas.get(i));
            }

            @Override
            protected int getLayoutID(int i) {
                return R.layout.item;
            }

            @Override
            public int getItemCount() {
                return mDatas.size();
            }
        };

        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int i) {
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setText(mDatas.get(i));
                Toast.makeText(MainActivity.this, "复制成功", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register:
                register();
                toast("开始注册");
                break;
            case R.id.unregister:
                Push.unregister(this);
                toast("取消注册");
                break;
            case R.id.open:
                Push.resume(this);
                toast("开启推送");
                break;
            case R.id.close:
                Push.pause(this);
                toast("开始暂停");
                break;
            case R.id.alias:
                Push.setAlias(this, "haha");
                toast("alias");
                break;
        }

    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    private void register() {
        Push.register(this, BuildConfig.DEBUG);

    }

    private void addData(String value) {
        mDatas.add(value);
        recyclerView.getAdapter().notifyDataSetChanged();

    }
}
