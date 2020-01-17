package com.example.gobang_app.util;

import android.os.Build;
import android.support.annotation.RequiresApi;

import com.example.gobang_app.bean.Point;
import com.example.gobang_app.widget.GoBangBoard;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static com.example.gobang_app.util.Constants.CHESS_NONE;
import static com.example.gobang_app.widget.GoBangBoard.LINE_COUNT;


public class RobotAlgorithm {
    private Random r = new Random();
    private List<Point> pieces;
    private GoBangBoard mGoBangBoard;
   private  boolean mCurrentWhite;
   private  int ischuang=0,unischuang=0;
    private int[][] mBoard = new int[LINE_COUNT][LINE_COUNT];
    //创建Ai
    public RobotAlgorithm(GoBangBoard mGoBangBoard, boolean mCurrentWhite) {
            this.pieces=new ArrayList<>();
            this.mCurrentWhite=mCurrentWhite;
            this.mGoBangBoard=mGoBangBoard;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            step();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void step() {
        if(mCurrentWhite){
            ischuang=1;
            unischuang=2;
        }else{
            ischuang=2;
            unischuang=1;
        }
        //计算下一步直接出棋
        Point point = up();
        mGoBangBoard.putChess(mCurrentWhite,point.x,point.y);
    }


    //随缘下棋
    private Point test(){
        for (int i =0;i<3;i++){
            int x = r.nextInt(3)+5;
            int y = r.nextInt(3)+5;
            if(mBoard[x][y] ==CHESS_NONE){
                return new Point(x,y,0,mCurrentWhite);
            }
        }
        while (true){
            int x = r.nextInt(LINE_COUNT);
            int y = r.nextInt(LINE_COUNT);
            if(mBoard[x][y] ==CHESS_NONE){
                return new Point(x,y,0,mCurrentWhite);
            }
        }
    }


    // 智能算法下棋
    @RequiresApi(api = Build.VERSION_CODES.N)
    private Point up(){
      //  pieces.removeAll(pieces);
        mBoard = mGoBangBoard.getTable();
        for (int x = 0;x <LINE_COUNT; x++){
            for (int y = 0;y <LINE_COUNT; y++){

                //判断空余地方获胜几率
                if (mBoard[x][y] == CHESS_NONE){
                    //判断自己权重
                    sub(x,y,mCurrentWhite);
                    //判断对手权重
                    sub(x,y,!mCurrentWhite);
                }
            }
        }
        return get();
    }

    //计算权重
    private void sub(int x,int y,boolean c){
        int xx = 0,yy=0;
        int num = 0 ;
        for (int i =0 ;i<8 ;i++ ){
            switch (i){
                case 0:
                    xx = 0;yy = 1;
                    break;
                case 1:
                    xx=-1;yy=-1;
                    break;
                case 2:
                    xx=-1;yy=1;
                    break;
                case 3:
                    xx=-1;yy=0;
                    break;
                case 4:
                    xx = 1;yy = -1;
                    break;
                case 5:
                    xx = 1;yy = 1;
                    break;
                case 6:
                    xx = 1 ;yy = 0;
                    break;
                case 7:
                    xx = 0;yy = -1;
                    break;

            }
            if(c==mCurrentWhite){
                //查自己下子权重
                int a = ishas(x, y, xx, yy, 0,c)+ishas(x, y, -xx, -yy, 0,c);
                if (a>num)num=a;
            }else{
                //检测对手威胁权重
                int a = ishas(x, y, xx, yy, 0,c)+ishas(x, y, -xx, -yy, 0,c);
                if (a>num)num=a;
            }
        }
        pieces.add(new Point(x,y,num,c));
    }


    // 检测周围有没有棋子
    @RequiresApi(api = Build.VERSION_CODES.N)
    private Point get(){

//        挑选权重最大的
        pieces.sort(new Comparator<Point>() {
            @Override
            public int compare(Point o1, Point o2) {
                return  o1.getInf()< o2 .getInf() ? 1:(o1.getInf()== o2.getInf() ? 0: -1);
            }
        });
        //随缘棋子
      //  System.out.println(pieces);
        if(pieces.size()==0 || pieces.get(0).getInf() == 0)return test();
        int max = pieces.get(0).getInf();
        Point index = pieces.get(0);
        for(Point ps : pieces ){

            if(ps.getInf()<max){
                return index;
            }
            index = ps;

        }
        return index ;
    }


    // 检测棋子
    private int ishas(int x,int y,int xx,int yy,int size,boolean c){
        if((x==0&&xx==-1)|| (x==LINE_COUNT-1&&xx==1) || (y==0&&yy==-1) || (y== LINE_COUNT-1&&yy==1)) return size;
        if(c==mCurrentWhite) {
            if (mBoard[x + xx][y + yy] == ischuang) {
                return ishas(x + xx, y + yy, xx, yy, size + 1, c);
            } else if (mBoard[x + xx][y + yy] == unischuang) {
                return size > 3 ? size + 2 : size - 1;
            }
        }else{
            if (mBoard[x + xx][y + yy] == unischuang) {
                return ishas(x + xx, y + yy, xx, yy, size + 1, c);
            } else if (mBoard[x + xx][y + yy] == ischuang) {
                return size > 3 ? size + 2 : size - 1;
            }
        }
        return size;
    }




    /**判断危机**/

}
