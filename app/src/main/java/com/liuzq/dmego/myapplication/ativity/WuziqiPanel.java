package com.liuzq.dmego.myapplication.ativity;

import java.util.ArrayList;
import android.app.AlertDialog;
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
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.liuzq.dmego.myapplication.R;

import java.util.List;

/**
 * Created by dmego on 16-11-16.
 */

//android:background="@drawable/bg"
public class WuziqiPanel extends View
{/*自定义view */

    private  int mPanelwidth ;
    private  float mLineHeight ;
    private  int MAX_LINE = 10 ;
    private  int MAX_COUNT_IN_LINE = 5;
    private Paint mPaint = new Paint();
    private int mLineColorId = R.color.Gray;//线的颜色
    private Bitmap mwhitepiece;
    private Bitmap mBlackPiece;
    private MediaPlayer player = MediaPlayer.create(getContext(), R.raw.voice);//落子声
    private float ratioPieceOfLineHeight = 3 * 1.0f / 4;
    //白棋先手，当前轮到白棋下
    private boolean mIsWhite = true;
    private ArrayList<Point> mWhiteArray = new ArrayList<>();
    private ArrayList<Point> mBlackArray = new ArrayList<>();
    private boolean isDialogOver = true;
    private boolean mIsGameOver ;
    private boolean mIsWhiteWinner ;
    public WuziqiPanel(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        init();

    }
    private void init()   /*初始化操作*/ {

        //设置画笔颜色
        mPaint.setColor(0x99000000);//黑色半透明
        mPaint.setColor(getContext().getResources().getColor(mLineColorId));
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);

        mwhitepiece = BitmapFactory.decodeResource(getResources(), R.drawable.piece_w1);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.piece_b);
    }

    @Override        /*决定测量*/
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);  /*辅助类*/
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize,heightSize);/*  */

        if(widthMode == MeasureSpec.UNSPECIFIED)
        {
            width = heightSize;
        }else if(heightMode == MeasureSpec.UNSPECIFIED)
        {
            width = widthSize;
        }

        setMeasuredDimension(width,width);
    }

    @Override  /*对与尺寸相关的成员变量进行初始化*/
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelwidth = w ;
        mLineHeight = mPanelwidth * 1.0f / MAX_LINE ;

        int pieceWidth = (int) (mLineHeight * ratioPieceOfLineHeight);
        /* 使棋子的大小能适合屏幕和画布*/
        mwhitepiece = Bitmap.createScaledBitmap(mwhitepiece,pieceWidth,pieceWidth,false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece,pieceWidth,pieceWidth,false);
    }

    @Override  /* 捕获用户的手势，实现下棋的操作*/
    public boolean onTouchEvent(MotionEvent event)
    {
        //游戏结束不允许落子

        int action = event.getAction();
        if(mIsGameOver) return false;
        if(action == MotionEvent.ACTION_UP)/*取得用户点击的坐标*/
        {
            int x = (int) event.getX();
            int y = (int) event.getY();
            Point p = getValidPoint(x,y); /*棋子位置*/
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
        }

        return true;
    }

    private Point getValidPoint(int x, int y)
    {
        return new Point((int)(x / mLineHeight),(int)(y / mLineHeight));
    }

    @Override /* 绘制棋盘操作*/
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        //绘制棋盘
        drawBoard(canvas);
        //绘制  棋子
        drawPieces(canvas);
        player.start();//落子声
        //判断游戏结束
        checkGameOver();
    }
    //判断游戏结束
    public void checkGameOver()
    {
        boolean whiteWin = checkFiveInLine(mWhiteArray);
        boolean blackWin = checkFiveInLine(mBlackArray);

        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        if (blackWin)
        {
            mIsGameOver = true;
            mIsWhiteWinner = blackWin;
            dialog.setTitle("游戏结束");
            dialog.setMessage("狗蛋赢了!");
            dialog.setCancelable(false);
            dialog.setPositiveButton("再来一波", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    reStart();
                    isDialogOver = true;
                }
            });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {

                    isDialogOver = true;
                }
            });
            dialog.show();
        }
        else if (whiteWin)
        {
            mIsGameOver = true;
            mIsWhiteWinner = whiteWin;
            dialog.setTitle("游戏结束");
            dialog.setMessage("滑稽赢了!");
            dialog.setCancelable(false);
            dialog.setPositiveButton("再来一波", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    reStart();
                    isDialogOver = true;
                }
            });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {

                    isDialogOver = true;
                }
            });
            dialog.show();
        }
        else if (mWhiteArray.size() == 100 || mBlackArray.size() == 100)
        {

            dialog.setMessage("平局!");
            dialog.setCancelable(false);
            dialog.setPositiveButton("再来一波", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    reStart();
                    isDialogOver = true;
                }
            });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {

                    isDialogOver = true;
                }
            });
            dialog.show();
        }
        isDialogOver = false;
    }


    //判断五子相连
    private boolean checkFiveInLine(List<Point> points)
    {
        for(Point p:points)
        {
            int x = p.x;
            int y = p.y;

         boolean win = chackHorizontal(x,y,points);
            if(win) return true;
            win = chackVertical(x,y,points);
            if(win) return  true;
            win = chackLeftDiagonal(x,y,points);
            if(win) return  true;
            win = chackRightDiagonal(x,y,points);
            if(win) return  true;
        }

        return  false;
    }

    /**
     * 判断x，y位置的棋子是否横向有相邻的五个
     * @param x
     * @param y
     * @param points
     * @return
     */
    private boolean chackHorizontal(int x, int y, List<Point> points)
    {
        int count = 1;
        //判断左横
        for(int i = 1; i< MAX_COUNT_IN_LINE ; i++)
        {
            if(points.contains(new Point(x - i,y)))
            {
                count++;
            }else
            {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;
        //判断右横
        for(int i = 1; i< MAX_COUNT_IN_LINE ; i++)
        {
            if(points.contains(new Point(x + i,y)))
            {
                count++;
            }else
            {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;
        return  false;
    }

    //判断竖向是否有五个
    private boolean chackVertical(int x, int y, List<Point> points)
    {
        int count = 1;
        //判断上
        for(int i = 1; i< MAX_COUNT_IN_LINE ; i++)
        {
            if(points.contains(new Point(x ,y - i)))
            {
                count++;
            }else
            {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;
        //判断下
        for(int i = 1; i< MAX_COUNT_IN_LINE ; i++)
        {
            if(points.contains(new Point(x ,y + i)))
            {
                count++;
            }else
            {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;
        return  false;
    }

    //判断左斜是否有五个
    private boolean chackLeftDiagonal(int x, int y, List<Point> points)
    {
        int count = 1;
        //判断左斜上
        for(int i = 1; i< MAX_COUNT_IN_LINE ; i++)
        {
            if(points.contains(new Point(x + i,y - i)))
            {
                count++;
            }else
            {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;
        //判断左斜下
        for(int i = 1; i< MAX_COUNT_IN_LINE ; i++)
        {
            if(points.contains(new Point(x - i ,y + i)))
            {
                count++;
            }else
            {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;
        return  false;
    }

    //判断右斜是否有五个
    private boolean chackRightDiagonal(int x, int y, List<Point> points)
    {
        int count = 1;
        //判断右斜上
        for(int i = 1; i< MAX_COUNT_IN_LINE ; i++)
        {
            if(points.contains(new Point(x - i ,y - i)))
            {
                count++;
            }else
            {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;
        //判断右斜下
        for(int i = 1; i< MAX_COUNT_IN_LINE ; i++)
        {
            if(points.contains(new Point(x + i,y + i)))
            {
                count++;
            }else
            {
                break;
            }
        }
        if (count == MAX_COUNT_IN_LINE) return true;
        return  false;
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
    private void drawBoard(Canvas canvas)
    {
        int w = mPanelwidth;
        float lineHeight = mLineHeight;

        for(int i= 0 ; i < MAX_LINE ; i ++)
        {
            int startX = (int) (lineHeight/2);
            int endX = (int) (w-lineHeight/2);
            int y = (int) ((0.5 + i) * lineHeight);
            canvas.drawLine(startX,y,endX,y,mPaint);
            canvas.drawLine(y,startX,y,endX,mPaint);
        }
    }

    //再来一局
    public void reStart()
    {
        mWhiteArray.clear();
        mBlackArray.clear();
        mIsGameOver = false;
        mIsWhiteWinner = false;
        invalidate();
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
}
