package cc.kenai.demo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import hugo.weaving.DebugLog;

public class MainActivity extends Activity implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    SharedPreferences preferences;

    TextView name, summary;


    boolean ifback;

    @Override
    public void onBackPressed() {
        ifback = true;
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


        startService(new Intent(this, MainService.class));

        getActionBar().hide();

        preferences = getSharedPreferences("floattouch", 0);
        preferences.registerOnSharedPreferenceChangeListener(this);

        name = (TextView) findViewById(R.id.name);
        summary = (TextView) findViewById(R.id.summary);

//        findViewById(R.id.model2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                preferences.edit().putString("demo", "1").apply();
//            }
//        });
//        findViewById(R.id.model3).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                preferences.edit().putString("demo", "2").apply();
//            }
//        });

        changeDemo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        if (!ifback)
            stopService(new Intent(this, MainService.class));
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("floattouch")) {
            changeDemo();
        }
    }

    @DebugLog
    String changeDemo() {

        switch (Integer.valueOf(preferences.getString("floattouch", "" + 0))) {
            case 0:
            default:
                name.setText("直接拖动");
                summary.setText("直接拖动圆圈即可位移屏幕\n\n长按圆圈后可移动圆圈位置" +
                        "\n\n" +
                        "由于有一个接口还需要修改，因此切换页面后拖动圆圈可能会无效");
                return "stable";
//            case 1:
//                name.setText("模式二：单击呼出");
//                summary.setText("点击后从屏幕任意位置可以位移屏幕");
//                return "bottom";
//            case 2:
//                name.setText("模式三：手势操作");
//                summary.setText("通过对圆圈做甩动手势位移屏幕\n\n长按圆圈后可移动圆圈位置");
//                return "free";
        }
    }
}
