package com.example.gobang_app.view.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.example.gobang_app.EventBus.BusProvider;
import com.example.gobang_app.EventBus.WifiBeginWaitingEvent;
import com.example.gobang_app.EventBus.WifiCancelWaitingEvent;
import com.example.gobang_app.R;
import com.example.materialdesign.views.ButtonRectangle;


/**
 * Created by lenov0 on 2015/12/28.
 */
public class WaitingPlayerDialog extends BaseDialog {
    public static final String TAG = "WaitingPlayerDialog";

    private ButtonRectangle mBeginButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        View view = inflater.inflate(R.layout.dialog_waiting_player, container, false);
        mBeginButton = (ButtonRectangle)view.findViewById(R.id.btn_begin);
        mBeginButton.setEnabled(false);
        ButtonRectangle CancelButton = (ButtonRectangle)view.findViewById(R.id.btn_cancel);
        CancelButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 BusProvider.getInstance().post(new WifiCancelWaitingEvent());
             }
         });

        return view;
    }
//有连接了
    public void setBeginEnable(){
        mBeginButton.setEnabled(true);
        //开始
        mBeginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BusProvider.getInstance().post(new WifiBeginWaitingEvent());
            }
        });
    }
}
