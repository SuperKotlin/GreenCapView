package com.zhuyong.greencapview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by zhuyong on 2017/11/14.
 */

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private GreenCapView mGreenCapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Toolbar
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mGreenCapView = (GreenCapView) findViewById(R.id.green_cap_view_luonima);
        setSupportActionBar(mToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_luonima, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_automatic_pattern:
                mGreenCapView.automaticPatternSwitch();
                boolean status = mGreenCapView.ismAutomaticPatternStatus();
                item.setTitle(status ? "关闭自动模式" : "打开自动模式");
                break;
            case R.id.action_clear:
                mGreenCapView.removeAllGreenCaps();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
