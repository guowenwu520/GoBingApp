package com.example.gobang_app.util;


import com.example.gobang_app.bean.Message;
import com.example.gobang_app.bean.Point;

/**
 * Created by lenov0 on 2016/1/25.
 */
public class MessageWrapper {

    public static Message getHostBeginMessage() {
        Message message=new Message();
        message.setmMessageType(Message.MSG_TYPE_HOST_BEGIN);
        return message;
    }

    public static Message getHostBeginAckMessage() {
        Message message=new Message();
        message.setmMessageType(Message.MSG_TYPE_BEGIN_ACK);
        return message;
    }

    public static Message getSendDataMessage(Point point, boolean isWhite,int NextStep) {
        Message message=new Message();
        message.setmMessageType(Message.MSG_TYPE_GAME_DATA);
        message.setmIsWhite(isWhite);
        message.setmGameData(point);
        message.setmNesxtStep(NextStep);
        return message;
    }

    public static Message getGameEndMessage(String endMessage) {
        Message message=new Message();
        message.setmMessageType(Message.MSG_TYPE_GAME_END);
        message.setmMessage(endMessage);
        return message;
    }

    public static Message getGameRestartReqMessage() {
        Message message=new Message();
        message.setmMessageType(Message.MSG_TYPE_GAME_RESTART_REQ);
        return message;
    }

    public static Message getGameRestartRespMessage(boolean agreeRestart) {
        Message message=new Message();
        message.setmMessageType(Message.MSG_TYPE_GAME_RESTART_RESP);
        message.setmAgreeRestart(agreeRestart);
        return message;
    }

    public static Message getGameExitMessage() {
        Message message=new Message();
        message.setmMessageType(Message.MSG_TYPE_EXIT);
        return message;
    }

    public static Message getGameMoveBackReqMessage() {
        Message message=new Message();
        message.setmMessageType(Message.MSG_TYPE_MOVE_BACK_REQ);
        return message;
    }

    public static Message getGameMoveBackRespMessage(boolean agreeMoveBack) {
        Message message=new Message();
        message.setmMessageType(Message.MSG_TYPE_MOVE_BACK_RESP);
        message.setmAgreeMoveBack(agreeMoveBack);
        return message;
    }

    public static Message getGameSwapMessage(int TYPE) {
        Message message=new Message();
        message.setmMessageType(TYPE);
        return message;
    }
}
