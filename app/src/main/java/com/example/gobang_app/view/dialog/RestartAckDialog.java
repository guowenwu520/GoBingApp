package com.example.gobang_app.view.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gobang_app.EventBus.BusProvider;
import com.example.gobang_app.EventBus.RestartGameAckEvent;
import com.example.gobang_app.R;
import com.example.materialdesign.views.ButtonRectangle;


/**
 * Created by Administrator on 2016/1/27.
 */
public class RestartAckDialog extends BaseDialog {

    public static final String TAG = "RestartAckDialog";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dialog_ack_restart, container, false);

        ButtonRectangle agree = (ButtonRectangle) view.findViewById(R.id.btn_agree);
        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BusProvider.getInstance().post(new RestartGameAckEvent(true));
            }
        });

        ButtonRectangle disagree = (ButtonRectangle) view.findViewById(R.id.btn_disagree);
        disagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BusProvider.getInstance().post(new RestartGameAckEvent(false));
            }
        });

        return view;
    }
}
