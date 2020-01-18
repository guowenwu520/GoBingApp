package com.example.gobang_app.view.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.gobang_app.R;
import com.example.gobang_app.bean.Point;
import com.example.gobang_app.util.GameJudger;
import com.example.gobang_app.util.RobotAlgorithm;
import com.example.gobang_app.util.ToastUtil;
import com.example.gobang_app.widget.GoBangBoard;
import com.example.materialdesign.views.ButtonRectangle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by Xuf on 2016/2/7.
 */
//集成自定义出棋监听
public class CoupleGameFragment extends Fragment implements GoBangBoard.PutChessListener
        , View.OnClickListener
        , View.OnTouchListener {
    //游戏状态
    private boolean mIsGameStarted = false;
    //黑棋先手
    private boolean mIsWhiteFirst = false;
    private boolean mCurrentWhite;
    private  boolean isPeople=false;
    //思考时间3秒
    private int times=1000;
   //自定义棋盘
    private GoBangBoard mGoBangBoard;
    private ButtonRectangle mStartGame;
    private ImageView imageView;
    Handler handler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what==0){
                //选择
                dialog_Select_Msg();
            }else if(msg.what==2){
                //电脑随机选择黑白
                if(Math.random()*10>5.0){
                    //选白棋
                 //   mCurrentWhite=true;
                    TipsMsg("你执黑棋");
                    Step(1);
                }else{
                    //mCurrentWhite=false;
                    TipsMsg("你执白棋");
                }
            }else if(msg.what==3){
                TipsMsg((String) msg.obj);
            }
            return false;
        }
    });
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_couple_game, container, false);

        initView(view);

        return view;
    }

    private void initView(View view) {
        mGoBangBoard = (GoBangBoard) view.findViewById(R.id.go_bang_board);
        mGoBangBoard.setOnTouchListener(this);
        mGoBangBoard.setPutChessListener(this);

        mCurrentWhite = mIsWhiteFirst;

        imageView=view.findViewById(R.id.qustion);
        imageView.setOnClickListener(this);
        
        mStartGame = (ButtonRectangle) view.findViewById(R.id.btn_start_game);
        mStartGame.setOnClickListener(this);

        ButtonRectangle exitGame = (ButtonRectangle) view.findViewById(R.id.btn_exit_game);
        exitGame.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_game:
                if (!mIsGameStarted) {
                    mIsGameStarted = true;
                    mCurrentWhite = mIsWhiteFirst;
                    setWidgets();
                    Step(3);

                }
                break;
            case R.id.btn_exit_game:
                getActivity().finish();
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

    private void Step(int nexts) {
        new Thread(){
            @Override
            public void run() {
                mGoBangBoard.setEnabled(false);
                //先走3步
                for (int i=0;i<nexts;i++){
                    try {
                        Thread.sleep(times);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    isPeople=false;
                    new RobotAlgorithm(mGoBangBoard,mCurrentWhite);
                    mCurrentWhite = !mCurrentWhite;
                }
                mGoBangBoard.setEnabled(true);
                if(nexts==3) {
                    handler.sendEmptyMessageAtTime(0, 0);
                }else  if(nexts==2){
                    handler.sendEmptyMessageAtTime(2, 0);
                }
            }
        }.start();

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
                alertDialog.dismiss();
            }
        });
        //选黑棋
        blackGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //机器在走一步
                Step(1);
                alertDialog.dismiss();
            }
        });
        //不选
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Step(2);
                alertDialog.dismiss();
            }
        });
    }

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

    @Override
    public void onPutChess(int[][] board, int x, int y) {
        //下子后判断输赢
        if (GameJudger.isGameEnd(board, x, y)) {
            String msg = String.format("%s赢了", mCurrentWhite ? "白棋" : "黑棋");
            Message message=new Message();
            message.what=3;
            message.obj=msg;
           handler.sendMessage(message);
            mIsGameStarted = false;
            resetWidgets();
        }else if(isPeople){
            //机器走一步
            Step(1);
        }
    }

    private void setWidgets() {
        //清空棋子
        mGoBangBoard.clearBoard();
        mStartGame.setEnabled(false);
    }

    private void resetWidgets() {
        mStartGame.setEnabled(true);
    }
    //点击下子
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!mIsGameStarted) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                isPeople=true;
                Point point = mGoBangBoard.convertPoint(x, y);
                //下子成功切换黑白棋
                if (mGoBangBoard.putChess(mCurrentWhite, point.x, point.y)) {
                    mCurrentWhite = !mCurrentWhite;
                }
                break;
        }
        return false;
    }
}
