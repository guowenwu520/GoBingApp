package com.example.gobang_app.bean;


/**
 * Created by Xuf on 2016/1/10.
 */
public class Message {
    public static final int MSG_TYPE_HOST_BEGIN = 0;//房主开始游戏
    public static final int MSG_TYPE_BEGIN_ACK = 1;//玩家收到房主开始游戏的确认消息
    public static final int MSG_TYPE_GAME_DATA = 2;//游戏数据消息，包括下子位置
    public static final int MSG_TYPE_GAME_END = 3;
    public static final int MSG_TYPE_GAME_RESTART_REQ = 4;//重新开始游戏请求
    public static final int MSG_TYPE_GAME_RESTART_RESP = 5;//重新开始游戏应答
    public static final int MSG_TYPE_EXIT = 6;
    public static final int MSG_TYPE_MOVE_BACK_REQ = 7;//悔棋请求
    public static final int MSG_TYPE_MOVE_BACK_RESP = 8;//悔棋应答
   public  static final int MSG_TYPE_SELECT_SHOU=9;//提示选择方式

    public  static final int MSG_TYPE_SELECT_S1=10;//选择白子
    public  static final int MSG_TYPE_SELECT_S2=11;//选择黑子
    public  static final int MSG_TYPE_SELECT_S3=12;//让对方在行

    public  static final int MSG_TYPE_SELECT_S1ED=13;//相应选白子
    public  static final int MSG_TYPE_SELECT_S2ED=14;//相应选择黑子
    public  static final int MSG_TYPE_SELECT_S3ED=15;//相应让对方在行
    //消息类型
    public int mMessageType;
//行子步数
    public int mNesxtStep;
    //棋子颜色
    public boolean mIsWhite;

    public Point mGameData;

    public String mMessage;

    public boolean mAgreeRestart;

    public boolean mAgreeMoveBack;

    public static int getMsgTypeSelectS3() {
        return MSG_TYPE_SELECT_S3;
    }

    public static int getMsgTypeSelectS1ed() {
        return MSG_TYPE_SELECT_S1ED;
    }

    public static int getMsgTypeSelectS2ed() {
        return MSG_TYPE_SELECT_S2ED;
    }

    public static int getMsgTypeSelectS3ed() {
        return MSG_TYPE_SELECT_S3ED;
    }

    public static int getMsgTypeSelectShou() {
        return MSG_TYPE_SELECT_SHOU;
    }

    public static int getMsgTypeSelectS1() {
        return MSG_TYPE_SELECT_S1;
    }

    public static int getMsgTypeSelectS2() {
        return MSG_TYPE_SELECT_S2;
    }

    public static int getMsgTypeSelect_s3() {
        return MSG_TYPE_SELECT_S3;
    }

    public int getmNesxtStep() {
        return mNesxtStep;
    }

    public void setmNesxtStep(int mNesxtStep) {
        this.mNesxtStep = mNesxtStep;
    }

    public static int getMsgTypeHostBegin() {
        return MSG_TYPE_HOST_BEGIN;
    }

    public static int getMsgTypeBeginAck() {
        return MSG_TYPE_BEGIN_ACK;
    }

    public static int getMsgTypeGameData() {
        return MSG_TYPE_GAME_DATA;
    }

    public static int getMsgTypeGameEnd() {
        return MSG_TYPE_GAME_END;
    }

    public static int getMsgTypeGameRestartReq() {
        return MSG_TYPE_GAME_RESTART_REQ;
    }

    public static int getMsgTypeGameRestartResp() {
        return MSG_TYPE_GAME_RESTART_RESP;
    }

    public static int getMsgTypeExit() {
        return MSG_TYPE_EXIT;
    }

    public static int getMsgTypeMoveBackReq() {
        return MSG_TYPE_MOVE_BACK_REQ;
    }

    public static int getMsgTypeMoveBackResp() {
        return MSG_TYPE_MOVE_BACK_RESP;
    }

    public int getmMessageType() {
        return mMessageType;
    }

    public void setmMessageType(int mMessageType) {
        this.mMessageType = mMessageType;
    }

    public boolean ismIsWhite() {
        return mIsWhite;
    }

    public void setmIsWhite(boolean mIsWhite) {
        this.mIsWhite = mIsWhite;
    }

    public Point getmGameData() {
        return mGameData;
    }

    public void setmGameData(Point mGameData) {
        this.mGameData = mGameData;
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
    }

    public boolean ismAgreeRestart() {
        return mAgreeRestart;
    }

    public void setmAgreeRestart(boolean mAgreeRestart) {
        this.mAgreeRestart = mAgreeRestart;
    }

    public boolean ismAgreeMoveBack() {
        return mAgreeMoveBack;
    }

    public void setmAgreeMoveBack(boolean mAgreeMoveBack) {
        this.mAgreeMoveBack = mAgreeMoveBack;
    }
}
