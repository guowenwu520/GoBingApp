package com.example.gobang_app.view.fragment;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.logansquare.LoganSquare;
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
import com.example.gobang_app.util.GameJudger;
import com.example.gobang_app.util.MessageWrapper;
import com.example.gobang_app.util.OperationQueue;
import com.example.gobang_app.util.ToastUtil;
import com.example.gobang_app.view.dialog.DialogCenter;
import com.example.gobang_app.widget.GoBangBoard;
import com.example.materialdesign.views.ButtonRectangle;
import com.example.salut.SalutDevice;
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

    private boolean mIsHost;
    private boolean mIsMePlay = false;
    private boolean mIsGameEnd = false;
    private boolean mIsOpponentLeaved = false;
    private boolean mCanClickConnect = true;
    private int mLeftMoveBackTimes = MOVE_BACK_TIMES;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        ButtonRectangle exitGame = (ButtonRectangle) view.findViewById(R.id.btn_exit);
        exitGame.setOnClickListener(this);

        mMoveBack = (ButtonRectangle) view.findViewById(R.id.btn_move_back);
        mMoveBack.setOnClickListener(this);
        mMoveBack.setText(makeMoveBackString());
    }

    private void init() {
        mDialogCenter = new DialogCenter(getActivity());
        mDialogCenter.showCompositionDialog();
        Bundle bundle = getArguments();
        int gameMode = bundle.getInt(NET_MODE);
        mNetPresenter = new NetPresenter(getActivity(), this, gameMode);
        mNetPresenter.init();
        mOperationQueue = new OperationQueue();
    }

    private void unInit() {
        mNetPresenter.unInit();
    }

    private void reset() {
        mBoard.clearBoard();
        mIsMePlay = mIsHost;
        mIsGameEnd = false;
        mOperationQueue.clear();
        mLeftMoveBackTimes = MOVE_BACK_TIMES;
        mMoveBack.setText(makeMoveBackString());
        mMoveBack.setEnabled(true);
    }

    private void sendMessage(Message message) {
        mNetPresenter.sendToDevice(message, mIsHost);
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
    public void onGetPairedToothPeers(List<BluetoothDevice> deviceList) {
        mDialogCenter.updateBlueToothPeers(deviceList, false);
    }

    @Override
    public void onFindBlueToothPeers(List<BluetoothDevice> deviceList) {
        mDialogCenter.updateBlueToothPeers(deviceList, true);
    }

    @Override
    public void onBlueToothDeviceConnected() {
        ToastUtil.showShort(getActivity(), "蓝牙连接成功");
        if (mIsHost) {
            mDialogCenter.enableWaitingPlayerDialogsBegin();
        }
    }

    @Override
    public void onBlueToothDeviceConnectFailed() {
        ToastUtil.showShort(getActivity(), "蓝牙连接失败");
        mCanClickConnect = true;
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
            Message message = LoganSquare.parse(str, Message.class);
            int type = message.mMessageType;
            switch (type) {
                case Message.MSG_TYPE_HOST_BEGIN:
                    //joiner
                    mDialogCenter.dismissPeersAndComposition();
                    Message ack = MessageWrapper.getHostBeginAckMessage();
                    sendMessage(ack);
                    ToastUtil.showShort(getActivity(), "游戏开始");
                    mCanClickConnect = true;
                    break;
                case Message.MSG_TYPE_BEGIN_ACK:
                    //host
                    mDialogCenter.dismissWaitingAndComposition();
                    mIsMePlay = true;
                    break;
                case Message.MSG_TYPE_GAME_DATA:
                    mBoard.putChess(message.mIsWhite, message.mGameData.x, message.mGameData.y);
                    mIsMePlay = true;
                    break;
                case Message.MSG_TYPE_GAME_END:
                    ToastUtil.showShortDelay(getActivity(), message.mMessage, 1000);
                    mIsMePlay = false;
                    mIsGameEnd = true;
                    break;
                case Message.MSG_TYPE_GAME_RESTART_REQ:
                    if (mIsGameEnd) {
                        Message resp = MessageWrapper.getGameRestartRespMessage(true);
                        sendMessage(resp);
                        reset();
                    } else {
                        mDialogCenter.showRestartAckDialog();
                    }
                    break;
                case Message.MSG_TYPE_GAME_RESTART_RESP:
                    if (message.mAgreeRestart) {
                        reset();
                        ToastUtil.showShort(getActivity(), "游戏开始");
                    } else {
                        ToastUtil.showShort(getActivity(), "对方不同意重新开始游戏");
                    }
                    mDialogCenter.dismissRestartWaitingDialog();
                    break;
                case Message.MSG_TYPE_MOVE_BACK_REQ:
                    if (mIsMePlay) {
                        mDialogCenter.showMoveBackAckDialog();
                    }
                    break;
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!mIsGameEnd && mIsMePlay) {
                    float x = motionEvent.getX();
                    float y = motionEvent.getY();
                    Point point = mBoard.convertPoint(x, y);
                    if (mBoard.putChess(mIsHost, point.x, point.y)) {
                        Message data = MessageWrapper.getSendDataMessage(point, mIsHost);
                        sendMessage(data);
                        mIsMePlay = false;
                    }
                }
                break;
        }
        return false;
    }

    @Override
    public void onPutChess(int[][] board, int x, int y) {
        if (mIsMePlay && GameJudger.isGameEnd(board, x, y)) {
            ToastUtil.showShort(getActivity(), "你赢了");
            Message end = MessageWrapper.getGameEndMessage("你输了");
            sendMessage(end);
            mIsMePlay = false;
            mIsGameEnd = true;
        }
        Point point = new Point();
        point.setXY(x, y);
        mOperationQueue.addOperation(point);
    }

    @Subscribe
    public void onCreateGame(WifiCreateGameEvent event) {
        mIsHost = true;
        mDialogCenter.showWaitingPlayerDialog();
        mNetPresenter.startService();
    }

    @Subscribe
    public void onJoinGame(WifiJoinGameEvent event) {
        mIsHost = false;
        mDialogCenter.showPeersDialog();
        mNetPresenter.findPeers();
    }

    @Subscribe
    public void onCancelCompositionDialog(WifiCancelCompositionEvent event) {
        getActivity().finish();
    }

    @Subscribe
    public void onConnectPeer(ConnectPeerEvent event) {
        if (mCanClickConnect) {
            mNetPresenter.connectToHost(event.mSalutDevice, event.mBlueToothDevice);
            mCanClickConnect = false;
        }
    }

    @Subscribe
    public void onCancelConnectPeer(WifiCancelPeerEvent event) {
        mDialogCenter.dismissPeersDialog();
    }

    @Subscribe
    public void onBeginGame(WifiBeginWaitingEvent event) {
        Message begin = MessageWrapper.getHostBeginMessage();
        sendMessage(begin);

    }

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
            reset();
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