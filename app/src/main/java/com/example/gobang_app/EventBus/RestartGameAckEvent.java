package com.example.gobang_app.EventBus;

/**
 * Created by Administrator on 2016/1/27.
 */
public class RestartGameAckEvent {

    public boolean mAgreeRestart;

    public RestartGameAckEvent(boolean agreeRestart){
        mAgreeRestart = agreeRestart;
    }
}
