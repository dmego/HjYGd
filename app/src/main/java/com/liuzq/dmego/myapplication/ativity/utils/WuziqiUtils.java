package com.liuzq.dmego.myapplication.ativity.utils;

import android.graphics.Point;

import com.liuzq.dmego.myapplication.ativity.WuziqiPanel;

import java.util.List;

/**
 * Created by dmego on 16-11-22.
 */

public class WuziqiUtils
{
    private static final int MAX_COUNT_IN_LINE = 5;
    //判断五子相连
    public static boolean checkFiveInLine(List<Point> points)
    {
        checkIsFull(points.size());

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

    //判断棋盘是否已满
    public static boolean checkIsFull(int size)
    {
        if (size == WuziqiPanel.MAX_PIECES_NUMBER)
        {
            return true;
        }
        return false;
    }

    /**
     * 判断x，y位置的棋子是否横向有相邻的五个
     * @param x
     * @param y
     * @param points
     * @return
     */
    private static boolean chackHorizontal(int x, int y, List<Point> points)
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
    private  static boolean chackVertical(int x, int y, List<Point> points)
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
    private  static boolean chackLeftDiagonal(int x, int y, List<Point> points)
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
    private  static boolean chackRightDiagonal(int x, int y, List<Point> points)
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

}
