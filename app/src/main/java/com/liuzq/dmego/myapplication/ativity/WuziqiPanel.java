package com.liuzq.dmego.myapplication.ativity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.liuzq.dmego.myapplication.R;
import com.liuzq.dmego.myapplication.ativity.utils.WuziqiUtils;

import java.util.ArrayList;
/**
 * Created by dmego on 16-11-16.
 */

//android:background="@drawable/bg"

    //网格棋盘（面板）/*自定义view */
public class WuziqiPanel extends View
{

    private  int mPanelwidth ;//棋盘宽度，棋盘为正方形
    private  float mLineHeight ;//每一个格子的高度，注意为folat类型
    private static final int MAX_LINE = 10 ;//每一行10格，总格数是10*10
    public static final int MAX_PIECES_NUMBER=MAX_LINE*MAX_LINE;//用于判断是否没点可以下，没有既是和棋
    private int mLineColorId = R.color.Gray;//线的颜色
    private Paint mPaint = new Paint();
    private Bitmap mwhitepiece;
    private Bitmap mBlackPiece;
    private MediaPlayer player = MediaPlayer.create(getContext(), R.raw.voice);//落子声
    private float ratioPieceOfLineHeight = 3 * 1.0f / 4;//设置棋子的大小为棋盘格子的3/4
    //白棋先手，当前轮到白棋下
    private boolean mIsWhite = true;
    private ArrayList<Point> mWhiteArray = new ArrayList<>();
    private ArrayList<Point> mBlackArray = new ArrayList<>();
    private int mResult;//0-和棋，1-白子赢，2-黑子赢
    public static final int DRAW=0;//平局
    public static final int WHITE_WON=1;
    public static final int BLACK_WON=2;
    private boolean isDialogOver = true;
    private boolean mIsGameOver ;

    public WuziqiPanel(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    private void init()   /*初始化操作*/
    {
        //设置画笔颜色
        mPaint.setColor(0x99000000);//黑色半透明
        mPaint.setColor(getContext().getResources().getColor(mLineColorId));
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mwhitepiece = BitmapFactory.decodeResource(getResources(), R.drawable.piece_w1);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.piece_b);
    }

    @Override    /*决定测量*/
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);  /*辅助类*/
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        //想把网格棋盘绘制成正方形
        //如果传入的是一个精确的值，就直接取值
        //同时也考虑到获得的widthSize与heightSize是设置的同样的值(如固定的100dp)，
        // 但也有可能是match_parent，所以在这里取最小值

        int width = Math.min(widthSize,heightSize);
        if(widthMode == MeasureSpec.UNSPECIFIED)
        {
            width = heightSize;
        }else if(heightMode == MeasureSpec.UNSPECIFIED)
        {
            width = widthSize;
        }

        //将宽和高设置为同样的值
        //在重写onMeasure方法时，必需要调用该方法存储测量好的宽高值
        setMeasuredDimension(width,width);
    }

    @Override  /*对与尺寸相关的成员变量进行初始化*/
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelwidth = w ;
        mLineHeight = mPanelwidth * 1.0f / MAX_LINE ;
         //根据实际的棋盘格子的宽度按照一定的比例缩小棋子
        int pieceWidth = (int) (mLineHeight * ratioPieceOfLineHeight);
        /* 使棋子的大小能适合屏幕和画布*/
        mwhitepiece = Bitmap.createScaledBitmap(mwhitepiece,pieceWidth,pieceWidth,false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece,pieceWidth,pieceWidth,false);
    }

    @Override /* 绘制棋盘操作*/
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        //绘制棋盘
        drawBoard(canvas);
        //绘制棋子
        drawPieces(canvas);
        player.start();//落子声
        //先判断是否和棋
        checkifFull();
        //判断游戏结束
        checkGameOver();
    }

    //判断游戏结束
    private void checkGameOver()
    {

        boolean whiteWin = WuziqiUtils.checkFiveInLine(mWhiteArray);
        boolean blackWin = WuziqiUtils.checkFiveInLine(mBlackArray);
        if(whiteWin || blackWin)
        {
            mIsGameOver=true;
            mResult=whiteWin ? WHITE_WON : BLACK_WON;
            showResult(mResult);
            return;
        }

    }
    //判断是否和棋
    private void checkifFull()
        {
            boolean isFull = WuziqiUtils.checkIsFull(mWhiteArray.size()+mBlackArray.size());
            if (isFull)
            {
                mResult = DRAW;
                showResult(mResult);
            }
        }
    //输出消息框
    public void showResult(int result)
    {
        String text=(result==WuziqiPanel.DRAW)?("打成平手!"):(result==WuziqiPanel.WHITE_WON?"滑稽赢了!":"狗蛋赢了!");
        AlertDialog.Builder dialog=new AlertDialog.Builder(getContext());
        //dialog.setIcon(android.R.drawable.ic_dialog_info);
        dialog.setTitle("对战结果:");
        dialog.setMessage(text);
        dialog.setCancelable(false);
        dialog.setPositiveButton("再来一波", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {
                reStart();
                isDialogOver = true;
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface arg0, int arg1)
            {
                isDialogOver = true;
            }
        });
        dialog.show();
        isDialogOver = false;
    }

    //绘制棋子
    private void drawPieces(Canvas canvas)
    {
        for(int i = 0,n = mWhiteArray.size() ; i < n; i++)
        {
            Point whitePoint  = mWhiteArray.get(i);
            canvas.drawBitmap(mwhitepiece,
                    (whitePoint.x + (1 - ratioPieceOfLineHeight) /2 ) * mLineHeight,
                    (whitePoint.y + (1 - ratioPieceOfLineHeight) /2 ) * mLineHeight,null);
        }

        for(int i = 0,n = mBlackArray.size() ; i < n; i++)
        {
            Point blackPoint  = mBlackArray.get(i);
            canvas.drawBitmap(mBlackPiece,
                    (blackPoint.x + (1 - ratioPieceOfLineHeight) / 2 ) * mLineHeight,
                    (blackPoint.y + (1 - ratioPieceOfLineHeight) / 2 ) * mLineHeight,null);
        }

    }

    //绘制棋盘

    /**@param canvas
     * 因为棋子可以下在边界的点上，所以边界的线与View的边界还是有一定距离的(左右上下的情况是一样的)
     * 所以这里设定边界线距离View的边界有1/2mLinearHeight
     *
     */
    private void drawBoard(Canvas canvas)
    {
        int w = mPanelwidth;
        float lineHeight = mLineHeight;

        for(int i= 0 ; i < MAX_LINE ; i ++)
        {
            int startX = (int) (lineHeight/2);
            int endX = (int) (w-lineHeight/2);

            int y = (int) ((0.5 + i) * lineHeight);
            //首先画横线
            canvas.drawLine(startX,y,endX,y,mPaint);
            //然后再画纵线(与横线的坐标是相反的)
            canvas.drawLine(y,startX,y,endX,mPaint);
        }
    }

    @Override  /* 捕获用户的手势，实现下棋的操作*/
    public boolean onTouchEvent(MotionEvent event)
    {
        //游戏结束不允许落子
        if(mIsGameOver) return false;

        //首先需要设置该View是对MotionEvent.ACTION_UP事件感兴趣的
        //return true就表示告诉父View自己的态度，表明可以响应MotionEvent.ACTION_UP事件
        //（而至于父View是否将该事件交给你处理还是拦截下来，那是父View的事了），这里只是表明自己的态度
        int action = event.getAction();

        if(action == MotionEvent.ACTION_UP)/*取得用户点击的坐标*/
        {
            int x = (int) event.getX();
            int y = (int) event.getY();
            Point p = getValidPoint(x,y); /*棋子位置*/

            //这里还需要考虑contains方法是间接的通过equals方法比较的
            //而Point中的equals方法是通过比较x和y的值来实现的，
            // 而不是比较两个引用变量是否指向同一地址（因为在getValidPoint方法中每次都new了一个Point实例）
            //所以这里用contains方法是符合要求的

            if(mWhiteArray.contains(p) || mBlackArray.contains(p))
            {
                return false;
            }
            if(mIsWhite)
            {
                mWhiteArray.add(p);
            }else
            {
                mBlackArray.add(p);
            }

            invalidate(); /*请求重绘*/

            mIsWhite = !mIsWhite; /* 改变该值*/
            return true;
        }
        //如果返回true，表示该方法消费了此事，
        //如果为false，那么表明该方法并未处理完全，该事件任然需要以某种方法传递下去继续等待处理
        return true;
    }

    /**
     * 通过传入的坐标得到一个合法的落子位置
     */
    private Point getValidPoint(int x, int y)
    {
        return new Point((int)(x / mLineHeight),(int)(y / mLineHeight));
    }

    /**
     * view 的存储与恢复(别忘了在布局文件里添加id)
     */
    private static final String INSTANCE = "instance";
    private static final String INSTANCE_GAME_OVER= "instance_game_over";
    private static final String INSTANCE_WHITE_ARRAY= "instance_white_array";
    private static final String INSTANCE_BLACK_ARRAY= "instance_black_array";

    @Override
    protected Parcelable onSaveInstanceState()
    {
        Bundle bundle = new Bundle();
        //保存系统属性
        bundle.putParcelable(INSTANCE,super.onSaveInstanceState());
        //保存游戏结束状态
        bundle.putBoolean(INSTANCE_GAME_OVER,mIsGameOver);
        //保存白棋状态
        bundle.putParcelableArrayList(INSTANCE_WHITE_ARRAY,mWhiteArray);
        //保存黑棋状态
        bundle.putParcelableArrayList(INSTANCE_BLACK_ARRAY,mBlackArray);
        return bundle;
    }
    @Override
    protected void onRestoreInstanceState(Parcelable state)
    {
        if(state instanceof Bundle)
        {
            Bundle bundle =(Bundle) state;
            mIsGameOver = bundle.getBoolean(INSTANCE_GAME_OVER);
            mWhiteArray = bundle.getParcelableArrayList(INSTANCE_WHITE_ARRAY);
            mBlackArray = bundle.getParcelableArrayList(INSTANCE_BLACK_ARRAY);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    //再来一局
    protected  void reStart()
    {
        mWhiteArray.clear();
        mBlackArray.clear();
        mIsGameOver = false;
        invalidate();//请求重绘
    }


}
