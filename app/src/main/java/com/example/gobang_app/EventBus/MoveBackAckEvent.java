package com.example.gobang_app.EventBus;

/**
 * Created by Administrator on 2016/1/27.
 */
public class MoveBackAckEvent {

    public boolean mAgreeMoveBack;

    public MoveBackAckEvent(boolean agreeMoveBack){
        mAgreeMoveBack = agreeMoveBack;
    }
}
