package cc.kenai.demo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import net.frakbot.jumpingbeans.JumpingBeans;

import hugo.weaving.DebugLog;

public class MainActivity extends Activity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    SharedPreferences preferences;

    TextView name, summary;


    boolean ifback;
    @Override
    public void onBackPressed() {
        ifback=true;
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Window win = getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        winParams.flags |= bits;
        win.setAttributes(winParams);

//        ViewGroup rootView = (ViewGroup) ((ViewGroup) this.findViewById(
//                android.R.id.content)).getChildAt(0);
//        rootView.setFitsSystemWindows(true);
//        rootView.setClipToPadding(true);


        startService(new Intent(this, MainService.class));

        getActionBar().hide();

        preferences = getSharedPreferences("demo", 0);
        preferences.registerOnSharedPreferenceChangeListener(this);

        name = (TextView) findViewById(R.id.name);
        summary = (TextView) findViewById(R.id.summary);

        findViewById(R.id.model1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.edit().putString("demo", "0").apply();
            }
        });
        findViewById(R.id.model2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.edit().putString("demo", "1").apply();
            }
        });
        findViewById(R.id.model3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                preferences.edit().putString("demo", "2").apply();
            }
        });

        changeDemo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        build.stopJumping();
        if(!ifback)
        stopService(new Intent(this, MainService.class));
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("demo")) {
            changeDemo();
        }
    }
    JumpingBeans build;

    @DebugLog
    String changeDemo() {
//        // Append jumping dots
//        new JumpingBeans.Builder()
//                .appendJumpingDots(name)
//                .build();
//
//        // Make the first word's letters jump
//        new JumpingBeans.Builder()
//                .makeTextJump(summary, 0, 3)
//                .build();

        switch (Integer.valueOf(preferences.getString("demo", "" + 0))) {
            case 0:
            default:
                name.setText("模式一：直接拖动");
                build = new JumpingBeans.Builder().makeTextJump(name, 0, 3).build();
                summary.setText("直接拖动圆圈即可位移屏幕\n\n长按圆圈后可移动圆圈位置");
                return "stable";
            case 1:
                name.setText("模式二：单击呼出");
                build=new JumpingBeans.Builder().makeTextJump(name, 0, 3).build();
                summary.setText("点击后从屏幕任意位置可以位移屏幕");
                return "bottom";
            case 2:
                name.setText("模式三：手势操作");
                build=new JumpingBeans.Builder().makeTextJump(name, 0, 3).build();
                summary.setText("通过对圆圈做甩动手势位移屏幕\n\n长按圆圈后可移动圆圈位置");
                return "free";
        }
    }
}
