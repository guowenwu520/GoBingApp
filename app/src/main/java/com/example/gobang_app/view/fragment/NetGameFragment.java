package com.example.gobang_app.view.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.gobang_app.EventBus.ConnectPeerEvent;
import com.example.gobang_app.EventBus.ExitGameAckEvent;
import com.example.gobang_app.EventBus.MoveBackAckEvent;
import com.example.gobang_app.EventBus.RestartGameAckEvent;
import com.example.gobang_app.EventBus.WifiBeginWaitingEvent;
import com.example.gobang_app.EventBus.WifiCancelCompositionEvent;
import com.example.gobang_app.EventBus.WifiCancelPeerEvent;
import com.example.gobang_app.EventBus.WifiCancelWaitingEvent;
import com.example.gobang_app.EventBus.WifiCreateGameEvent;
import com.example.gobang_app.EventBus.WifiJoinGameEvent;
import com.example.gobang_app.R;
import com.example.gobang_app.bean.Message;
import com.example.gobang_app.bean.Point;
import com.example.gobang_app.presenter.INetView;
import com.example.gobang_app.presenter.NetPresenter;
import com.example.gobang_app.util.Constants;
import com.example.gobang_app.util.GameJudger;
import com.example.gobang_app.util.MessageWrapper;
import com.example.gobang_app.util.OperationQueue;
import com.example.gobang_app.util.ToastUtil;
import com.example.gobang_app.view.dialog.DialogCenter;
import com.example.gobang_app.widget.GoBangBoard;
import com.example.materialdesign.views.ButtonRectangle;
import com.example.salut.SalutDevice;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.squareup.otto.Subscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Xuf on 2016/1/23.
 */
public class NetGameFragment extends BaseGameFragment implements INetView, GoBangBoard.PutChessListener
        , View.OnTouchListener, View.OnClickListener {

    private static final int MOVE_BACK_TIMES = 2;
  //出牌方
    private boolean mIsHost,Probability;
    //判断出牌
    private boolean mIsMePlay = false;
    private boolean mIsGameEnd = false;
    private boolean mIsOpponentLeaved = false;
    //判读是否刚刚开局
    private boolean misfristStart = true,mTwoStart=false;
   private int netmode;
   //行棋步数，默认一人一步
    private int nextstep=1;
    private boolean mCanClickConnect = true;
    private int mLeftMoveBackTimes = MOVE_BACK_TIMES;

    private ImageView imageView;
    private OperationQueue mOperationQueue;

    private NetPresenter mNetPresenter;

    private DialogCenter mDialogCenter;
    private GoBangBoard mBoard;
    private ButtonRectangle mMoveBack;

    private static final String NET_MODE = "netMode";

    public static NetGameFragment newInstance(int netMode) {
        NetGameFragment netGameFragment = new NetGameFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(NET_MODE, netMode);
        netGameFragment.setArguments(bundle);
        return netGameFragment;
    }
    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(android.os.Message msg) {
            if(msg.what==0){
                TipsMsg((String) msg.obj);
            }
            return false;
        }
    });
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //随机先后
        if(Math.random()*10>5.0){
            Probability=true;
        }else{
            //mCurrentWhite=false;
            Probability=false;
        }
        init();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unInit();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        initView(view);

        return view;
    }

    private void initView(View view) {
        mBoard = (GoBangBoard) view.findViewById(R.id.go_bang_board);
        mBoard.setOnTouchListener(this);
        mBoard.setPutChessListener(this);

        ButtonRectangle restart = (ButtonRectangle) view.findViewById(R.id.btn_restart);
        restart.setOnClickListener(this);
        imageView=view.findViewById(R.id.qustion);
        imageView.setOnClickListener(this);
        ButtonRectangle exitGame = (ButtonRectangle) view.findViewById(R.id.btn_exit);
        exitGame.setOnClickListener(this);

        mMoveBack = (ButtonRectangle) view.findViewById(R.id.btn_move_back);
        mMoveBack.setOnClickListener(this);
        mMoveBack.setText(makeMoveBackString());
    }

    private void init() {
        dialog_Select__TWO_Msg_Mode();

    }

    private void dialog_Select__TWO_Msg_Mode() {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        View view= LayoutInflater.from(getContext()).inflate(R.layout.dialog_select_nextstep,null,false);
        builder.setView(view);
        ButtonRectangle whiteGame = (ButtonRectangle) view.findViewById(R.id.btn_white_game);
        ButtonRectangle blackGame = (ButtonRectangle) view.findViewById(R.id.btn_black_game);
        ButtonRectangle cancel = (ButtonRectangle) view.findViewById(R.id.btn_cancel);
        cancel.setVisibility(View.GONE);
        whiteGame.setText("普通模式");
        blackGame.setText("SWAP2模式");
        AlertDialog alertDialog=builder.show();
        //选普通模式
        whiteGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出选择框，创建还是加入
                mDialogCenter = new DialogCenter(getActivity());
                mDialogCenter.showCompositionDialog();
                Bundle bundle = getArguments();
                int gameMode = bundle.getInt(NET_MODE);
                netmode=Constants.WIFI_MODE;
                mNetPresenter = new NetPresenter(getActivity(), NetGameFragment.this, gameMode);
                mNetPresenter.init();
                mOperationQueue = new OperationQueue();
                alertDialog.dismiss();
            }
        });
        //选黑棋SWAp2
        blackGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出选择框，创建还是加入
                mDialogCenter = new DialogCenter(getActivity());
                mDialogCenter.showCompositionDialog();
                Bundle bundle = getArguments();
                int gameMode = bundle.getInt(NET_MODE);
                netmode=Constants.WIFI_SWAP_MODE;
                mNetPresenter = new NetPresenter(getActivity(), NetGameFragment.this, gameMode);
                mNetPresenter.init();
                mOperationQueue = new OperationQueue();
                alertDialog.dismiss();
            }
        });
    }

    private void unInit() {
        mNetPresenter.unInit();
    }

    private void resetf() {
        if(netmode==Constants.WIFI_MODE){
            if (!mIsHost) {
                TipsMsg("你执黑棋,先行");
                mIsMePlay = true;
            } else {
                TipsMsg("你执白棋,对方先行");
                mIsMePlay = false;
            }
            mBoard.clearBoard();
         //   mIsMePlay = true;
            mIsGameEnd = false;
            misfristStart=true;
            nextstep=1;
            mOperationQueue.clear();
            mLeftMoveBackTimes = MOVE_BACK_TIMES;
            mMoveBack.setText(makeMoveBackString());
            mMoveBack.setEnabled(true);
        }else{
            if (!mIsHost) {
                TipsMsg("你执黑棋,先行三步");
                mIsMePlay = true;
                nextstep = 3;//先行三步
            } else {
                TipsMsg("你执白棋,对方先行三步");
                mIsMePlay = false;
                nextstep = 0;//先行三步
            }
            mBoard.clearBoard();
            mIsGameEnd = false;
            misfristStart = true;
            mOperationQueue.clear();
            mLeftMoveBackTimes = MOVE_BACK_TIMES;
            mMoveBack.setText(makeMoveBackString());
            mMoveBack.setEnabled(true);
        }

    }
    private void resetjie() {
        if(netmode==Constants.WIFI_MODE) {
            mBoard.clearBoard();
            if (!mIsHost) {
                TipsMsg("你执黑棋,先行");
                mIsMePlay = true;
            } else {
                TipsMsg("你执白棋,对方先行");
                mIsMePlay = false;
            }
           // mIsMePlay = true;
            mIsGameEnd = false;
            misfristStart = true;
            nextstep = 0;
            mOperationQueue.clear();
            mLeftMoveBackTimes = MOVE_BACK_TIMES;
            mMoveBack.setText(makeMoveBackString());
            mMoveBack.setEnabled(true);
        }else{
            if (!mIsHost) {
                TipsMsg("你执黑棋,先行三步");
                mIsMePlay = true;
                nextstep = 3;//先行三步
            } else {
                TipsMsg("你执白棋,对方先行三步");
                mIsMePlay = false;
                nextstep = 0;//先行三步
            }
            mBoard.clearBoard();
            mIsGameEnd = false;
            misfristStart = true;
            mOperationQueue.clear();
            mLeftMoveBackTimes = MOVE_BACK_TIMES;
            mMoveBack.setText(makeMoveBackString());
            mMoveBack.setEnabled(true);
        }
    }
    private void sendMessage(Message message) {
        mNetPresenter.sendToDevice(message, mIsHost);
    }
    private void sendMessage2(Message message) {
        mNetPresenter.sendToDevice2(message, mIsHost);
    }
    private void moveBackReq() {
        if (mIsMePlay || mIsGameEnd) {
            return;
        }
        Message message = MessageWrapper.getGameMoveBackReqMessage();
        sendMessage(message);
        mDialogCenter.showMoveBackWaitingDialog();
    }

    private void doMoveBack() {
        mOperationQueue.removeLastOperation();
        Point point = mOperationQueue.getLastOperation();
        mBoard.moveBack(point);
    }

    private String makeMoveBackString() {
        return "悔  棋" + "(" + mLeftMoveBackTimes + ")";
    }

    @Override
    public void onWifiInitFailed(String message) {
        ToastUtil.showShort(getActivity(), message);
        getActivity().finish();
    }

    @Override
    public void onWifiDeviceConnected(SalutDevice device) {
        ToastUtil.showShort(getActivity(), "onWifiDeviceConnected");
        mDialogCenter.enableWaitingPlayerDialogsBegin();
    }

    @Override
    public void onStartWifiServiceFailed() {
        ToastUtil.showShort(getActivity(), "onStartWifiServiceFailed");
    }

    @Override
    public void onFindWifiPeers(List<SalutDevice> deviceList) {
        mDialogCenter.updateWifiPeers(deviceList);
    }

    @Override
    public void onPeersNotFound() {
        ToastUtil.showShort(getActivity(), "found no peers");
        mDialogCenter.updateWifiPeers(new ArrayList<SalutDevice>());
    }

    @Override
    public void onDataReceived(Object o) {
        String str = (String) o;
            try {
                TypeToken<Message> typeToken=new TypeToken<Message>(){};
                Log.e("etdata",str);
                Message message = new Gson().fromJson(str, typeToken.getType());
            int type = message.mMessageType;
            switch (type) {
                case Message.MSG_TYPE_HOST_BEGIN:
                    //joiner

                    mBoard.clearBoard();
                    if (netmode == Constants.WIFI_MODE) {
                        mDialogCenter.dismissPeersAndComposition();
                        Message ack = MessageWrapper.getHostBeginAckMessage();
                        sendMessage(ack);
                        ToastUtil.showShort(getActivity(), "游戏开始");
                        if (!mIsHost) {
                            TipsMsg("你执黑棋,先行");
                            mIsMePlay = true;
                        } else {
                            TipsMsg("你执白棋,对方先行");
                            mIsMePlay = false;
                        }
                        mCanClickConnect = true;
                    } else {
                        mDialogCenter.dismissPeersAndComposition();
                        Message ack = MessageWrapper.getHostBeginAckMessage();
                        sendMessage(ack);
                        ToastUtil.showShort(getActivity(), "游戏开始");
                        if (!mIsHost) {
                            TipsMsg("你执黑棋,先行三步");
                            mIsMePlay = true;
                            nextstep = 3;//先行三步
                        } else {
                            TipsMsg("你执白棋,对方先行三步");
                            mIsMePlay = false;
                        }
                        mCanClickConnect = true;
                    }
                    break;
                case Message.MSG_TYPE_BEGIN_ACK:
                    //host

                    mBoard.clearBoard();
                    if (netmode == Constants.WIFI_MODE) {
                        mDialogCenter.dismissWaitingAndComposition();
                    if (!mIsHost) {
                        TipsMsg("你执黑棋,先行");
                        mIsMePlay = true;
                    } else {
                        TipsMsg("你执白棋,对方先行");
                        mIsMePlay = false;
                    }
            }else{

                        mDialogCenter.dismissWaitingAndComposition();
                    if (!mIsHost) {
                        TipsMsg("你执黑棋,先行三步");
                        mIsMePlay = true;
                        nextstep=3;//先行三步
                    } else {
                        TipsMsg("你执白棋,对方先行三步");
                        mIsMePlay = false;
                    }
                    mCanClickConnect = true;
                }
                    break;
                    //得到对方落子消息
                case Message.MSG_TYPE_GAME_DATA:
                    mBoard.putChess(message.mIsWhite, message.mGameData.x, message.mGameData.y);
                    if(netmode==Constants.WIFI_MODE) {
                        mIsMePlay = true;
                        nextstep=1;
                    }else{
                        //判断先手三次走完
                        if(misfristStart&&message.mNesxtStep==1) {
                            dialog_Select_Msg();
                            misfristStart = false;
                        }
                        else if(!misfristStart&&mTwoStart&&message.mNesxtStep==1) {
                            dialog_Select__TWO_Msg();
                             mTwoStart=false;
                        }
                        //禁止我方出棋
                        if(message.mNesxtStep!=1){
                            mIsMePlay = false;
                        }
                        if(message.mNesxtStep==1&&!misfristStart&&!mTwoStart){
                            mIsMePlay=true;
                            nextstep=1;
                        }
                    }
                    break;
                case Message.MSG_TYPE_GAME_END:
                    android.os.Message msg=new android.os.Message();
                    msg.what=0;
                    msg.obj="你输了";
                    handler.sendMessage(msg);
                    //ToastUtil.showShortDelay(getActivity(), message.mMessage, 1000);
                    mIsMePlay = false;
                    mIsGameEnd = true;
                    break;
                    //发送重新开局请求
                case Message.MSG_TYPE_GAME_RESTART_REQ:
                    if (mIsGameEnd) {
                        Message resp = MessageWrapper.getGameRestartRespMessage(true);
                        sendMessage(resp);
                        resetjie();
                    } else {
                        mDialogCenter.showRestartAckDialog();
                    }
                    break;
                    //响应开局请求
                case Message.MSG_TYPE_GAME_RESTART_RESP:
                    if (message.mAgreeRestart) {
                        resetf();
                        ToastUtil.showShort(getActivity(), "游戏开始");
                    } else {
                        ToastUtil.showShort(getActivity(), "对方不同意重新开始游戏");
                    }
                    mDialogCenter.dismissRestartWaitingDialog();
                    break;
                    //悔棋
                case Message.MSG_TYPE_MOVE_BACK_REQ:
                    if (mIsMePlay) {
                        mDialogCenter.showMoveBackAckDialog();
                    }
                    break;
                    //回应悔棋
                case Message.MSG_TYPE_MOVE_BACK_RESP:
                    if (message.mAgreeMoveBack) {
                        doMoveBack();
                        mIsMePlay = true;
                        mLeftMoveBackTimes--;
                        mMoveBack.setText(makeMoveBackString());
                        if (mLeftMoveBackTimes == 0) {
                            mMoveBack.setEnabled(false);
                        }
                    } else {
                        ToastUtil.showShort(getActivity(), "对方不同意你悔棋");
                    }
                    mDialogCenter.dismissMoveBackWaitingDialog();
                    break;
                case Message.MSG_TYPE_EXIT:
                    ToastUtil.showShort(getActivity(), "对方已离开游戏");
                    mIsMePlay = true;
                    mIsGameEnd = true;
                    mIsOpponentLeaved = true;
                    break;
                    //对方选择了白子
                case Message.MSG_TYPE_SELECT_S1:
                    TipsMsg("你执黑棋先行");
                    mIsMePlay=false;
                    misfristStart=false;
                    mTwoStart=false;
                    nextstep=0;
                    break;
                //让子
                case Message.MSG_TYPE_SELECT_S3:
                    TipsMsg("对方选择让棋");
                    mIsMePlay=false;
                    mTwoStart=true;
                    misfristStart=false;
                    nextstep=0;
                    break;
                    //选黑
                case Message.MSG_TYPE_SELECT_S2:
                    TipsMsg("你执白棋");
                    mIsMePlay=true;
                    mTwoStart=false;
                    misfristStart=false;
                    nextstep=1;
                    break;
                    //回应选择白子
                case Message.MSG_TYPE_SELECT_S1ED:
                    break;
                //回应让子
                case Message.MSG_TYPE_SELECT_S3ED:
                    //回应选择黑子
                    break;
                case Message.MSG_TYPE_SELECT_S2ED:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dialog_Select__TWO_Msg() {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        View view= LayoutInflater.from(getContext()).inflate(R.layout.dialog_select_nextstep,null,false);
        builder.setView(view);
        ButtonRectangle whiteGame = (ButtonRectangle) view.findViewById(R.id.btn_white_game);
        ButtonRectangle blackGame = (ButtonRectangle) view.findViewById(R.id.btn_black_game);
        ButtonRectangle cancel = (ButtonRectangle) view.findViewById(R.id.btn_cancel);
        cancel.setVisibility(View.GONE);
        AlertDialog alertDialog=builder.show();
        //选白棋
        whiteGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message resp = MessageWrapper.getGameSwapMessage(Message.MSG_TYPE_SELECT_S1);
                nextstep=1;
                mIsMePlay=true;
                sendMessage(resp);
                alertDialog.dismiss();
            }
        });
        //选黑棋
        blackGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //机器在走一步
                //  Step(1);
                Message resp = MessageWrapper.getGameSwapMessage(Message.MSG_TYPE_SELECT_S2);
                nextstep=0;
                mIsMePlay=false;
                sendMessage(resp);
                alertDialog.dismiss();
            }
        });
    }

    private void dialog_Select_Msg() {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        View view= LayoutInflater.from(getContext()).inflate(R.layout.dialog_select_nextstep,null,false);
        builder.setView(view);
        ButtonRectangle whiteGame = (ButtonRectangle) view.findViewById(R.id.btn_white_game);
        ButtonRectangle blackGame = (ButtonRectangle) view.findViewById(R.id.btn_black_game);
        ButtonRectangle cancel = (ButtonRectangle) view.findViewById(R.id.btn_cancel);
        AlertDialog alertDialog=builder.show();
        //选白棋
        whiteGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Message resp = MessageWrapper.getGameSwapMessage(Message.MSG_TYPE_SELECT_S1);
                nextstep=1;
                mIsMePlay=true;
                sendMessage(resp);
                alertDialog.dismiss();
            }
        });
        //选黑棋
        blackGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //机器在走一步
              //  Step(1);
                Message resp = MessageWrapper.getGameSwapMessage(Message.MSG_TYPE_SELECT_S2);
                nextstep=0;
                mIsMePlay=false;
                sendMessage(resp);
                alertDialog.dismiss();
            }
        });
        //不选
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   Step(2);
                mTwoStart=true;
                Message resp = MessageWrapper.getGameSwapMessage(Message.MSG_TYPE_SELECT_S3);
                nextstep=2;
                mIsMePlay=true;
                sendMessage(resp);
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public void onSendMessageFailed() {
        ToastUtil.showShort(getActivity(), "send message failed");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_restart:
                sendMessage(MessageWrapper.getGameRestartReqMessage());
                mDialogCenter.showRestartWaitingDialog();
                break;
            case R.id.btn_move_back:
                moveBackReq();
                break;
            case R.id.btn_exit:
                if (mIsOpponentLeaved) {
                    getActivity().finish();
                } else {
                    mDialogCenter.showExitAckDialog();
                }
                break;
                case R.id.qustion:
                showQ();
                break;
        }
    }
    private void showQ() {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        View view= LayoutInflater.from(getContext()).inflate(R.layout.dialog_ask_qustion,null,false);
        ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,300);
        view.setLayoutParams(layoutParams);
        builder.setView(view);
        builder.show();
    }
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mIsGameEnd && mIsMePlay&&nextstep>0) {
                    float x = motionEvent.getX();
                    float y = motionEvent.getY();Log.e("er0",nextstep+"  "+mIsMePlay+" "+misfristStart+" "+mTwoStart);
                    Point point = mBoard.convertPoint(x, y);


                           if(nextstep==2&&misfristStart){
                               if (mBoard.putChess(!mIsHost, point.x, point.y)) {
                                   misfristStart = false;
                                   Log.e("er2",nextstep+"  "+mIsMePlay+" "+misfristStart+" "+mTwoStart+" mIsHost="+mIsHost);
                                   Message data2 = MessageWrapper.getSendDataMessage(point, !mIsHost, nextstep);//发送黑白棋，落子消息，步数
                                   sendMessage2(data2);
                                   nextstep--;
                               }
                           }else if(!misfristStart&&mTwoStart&&nextstep==1){
                               if (mBoard.putChess(!mIsHost, point.x, point.y)) {
                                   Log.e("er3",nextstep+"  "+mIsMePlay+" "+misfristStart+" "+mTwoStart+" mIsHost="+mIsHost);
                                   mTwoStart = false;
                                   Message data2 = MessageWrapper.getSendDataMessage(point, !mIsHost, nextstep);//发送黑白棋，落子消息，步数
                                   sendMessage2(data2);
                                   nextstep--;
                               }
                           }else    if(nextstep>0)
                               if (mBoard.putChess(mIsHost, point.x, point.y)) {
                                   Log.e("e1r",nextstep+"  "+mIsMePlay+" "+misfristStart+" "+mTwoStart+" mIsHost="+mIsHost);
                                   Message data = MessageWrapper.getSendDataMessage(point, mIsHost, nextstep);//发送黑白棋，落子消息，步数
                                   sendMessage(data);
                                   nextstep--;
                               }





                    if(nextstep<=0)
                        mIsMePlay = false;

                    }
                break;
        }
        return false;
    }

    @Override
    public void onPutChess(int[][] board, int x, int y) {
        if (mIsMePlay && GameJudger.isGameEnd(board, x, y)) {
            android.os.Message message=new android.os.Message();
            message.what=0;
            message.obj="你赢了";
            handler.sendMessage(message);
            Message end = MessageWrapper.getGameEndMessage("你输了");
            sendMessage(end);
            mIsMePlay = false;
            mIsGameEnd = true;
        }
        Point point = new Point();
        point.setXY(x, y);
        mOperationQueue.addOperation(point);
    }
//创建游戏
    @Subscribe
    public void onCreateGame(WifiCreateGameEvent event) {
        mIsHost = true;
        mDialogCenter.showWaitingPlayerDialog();
        mNetPresenter.startService();
    }
///加入游戏
    @Subscribe
    public void onJoinGame(WifiJoinGameEvent event) {
        mIsHost =false;
        mDialogCenter.showPeersDialog();
        mNetPresenter.findPeers();
    }
    //弹框提示
    private void TipsMsg(String str) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        View view= LayoutInflater.from(getContext()).inflate(R.layout.dialog_msg_nextstep,null,false);
        builder.setView(view);
        ButtonRectangle whiteGame = (ButtonRectangle) view.findViewById(R.id.btn_tips_game);
        whiteGame.setText(str);
        AlertDialog alertDialog=builder.show();
        whiteGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }
    //取消
    @Subscribe
    public void onCancelCompositionDialog(WifiCancelCompositionEvent event) {
        getActivity().finish();
    }

    @Subscribe
    public void onConnectPeer(ConnectPeerEvent event) {
        if (mCanClickConnect) {
            mNetPresenter.connectToHost(event.mSalutDevice);
            mCanClickConnect = false;
        }
    }

    @Subscribe
    public void onCancelConnectPeer(WifiCancelPeerEvent event) {
        mDialogCenter.dismissPeersDialog();
    }
//开始游戏
    @Subscribe
    public void onBeginGame(WifiBeginWaitingEvent event) {
        Message begin = MessageWrapper.getHostBeginMessage();
        sendMessage(begin);

    }
//取消等待
    @Subscribe
    public void onCancelWaitingDialog(WifiCancelWaitingEvent event) {
        mDialogCenter.dismissWaitingPlayerDialog();
        mNetPresenter.stopService();
    }

    @Subscribe
    public void onRestartAck(RestartGameAckEvent event) {
        Message ack = MessageWrapper.getGameRestartRespMessage(event.mAgreeRestart);
        sendMessage(ack);
        if (event.mAgreeRestart) {
            resetf();
        }
        mDialogCenter.dismissRestartAckDialog();
    }

    @Subscribe
    public void onExitAck(ExitGameAckEvent event) {
        if (event.mExit) {
            Message ack = MessageWrapper.getGameExitMessage();
            sendMessage(ack);
            getActivity().finish();
        } else {
            mDialogCenter.dismissExitAckDialog();
        }
    }

    @Subscribe
    public void onMoveBackAck(MoveBackAckEvent event) {
        Message ack = MessageWrapper.getGameMoveBackRespMessage(event.mAgreeMoveBack);
        sendMessage(ack);
        mDialogCenter.dismissMoveBackAckDialog();
        if (event.mAgreeMoveBack) {
            doMoveBack();
            mIsMePlay = false;
        }
    }
}