package com.example.gobang_app.view.activity;

import android.support.v4.app.Fragment;

import com.example.gobang_app.util.Constants;
import com.example.gobang_app.view.fragment.CoupleGameFragment;
import com.example.gobang_app.view.fragment.NetGameFragment;


/**
 * Created by Administrator on 2015/12/8.
 */
public class GameActivity extends BaseActivity {

    @Override
    protected Fragment createFragment() {
        int gameMode = getIntent().getIntExtra(Constants.GAME_MODE, Constants.INVALID_MODE);
        Fragment fragment = null;

        switch (gameMode){
            case Constants.INVALID_MODE:
                break;
            case Constants.COUPE_MODE:
                fragment = new CoupleGameFragment();
                break;
            case Constants.WIFI_MODE:
            case Constants.BLUE_TOOTH_MODE:
                fragment = NetGameFragment.newInstance(gameMode);
                break;
        }

        return fragment;
    }

    @Override
    public void onBackPressed() {

    }
}
