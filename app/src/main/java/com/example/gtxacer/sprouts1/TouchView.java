package com.example.gtxacer.sprouts1;

/**
 * Created by GTX Acer on 22/04/2017.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class TouchView extends View {

    private float x;
    private float y;
    Paint drawLine;
    Paint drawPoint;
    PathMeasure pm;
    int player1 = 0;
    private static Path[] path = new Path[1];
    private static boolean punt = false;
    private boolean startpo = false;
    private boolean startpa = true;
    private static float[] points = {200,200,1500,750};
    private float[] aCoordinates = new float[2];
    public TouchView(Context context) {
        super(context);
    }

    public TouchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        drawLine = new Paint(Paint.DITHER_FLAG);
        drawLine.setAntiAlias(true);
        drawLine.setColor(Color.argb(255,71,71,71));
        drawLine.setStyle(Paint.Style.STROKE);
        drawLine.setStrokeJoin(Paint.Join.ROUND);
        drawLine.setStrokeWidth(10);
        drawPoint = new Paint(Paint.DITHER_FLAG);
        drawPoint.setAntiAlias(true);
        drawPoint.setColor(Color.argb(255,215,27,74));
        drawPoint.setStyle(Paint.Style.STROKE);
        drawPoint.setStrokeJoin(Paint.Join.ROUND);
        drawPoint.setStrokeWidth(30);
        setWillNotDraw(false);
    }

    @Override
    protected void onSizeChanged(int w, int h, int width, int height) {
        super.onSizeChanged(w, h, width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //canvas.drawPath(path, drawLine);
        for (int i = 0; !startpa && i < path.length; ++i)
            canvas.drawPath(path[i], drawLine);
        for (int i = 0;i < points.length; i+=2)
            canvas.drawCircle(points[i],points[i+1],10,drawPoint);
    }

    private boolean intersect(){
        for (int i = 0; i < path.length-1; ++i){
            for (int j = i + 1; j < path.length; ++j){
                Path p = new Path();
                p.op(path[i],path[j],Path.Op.INTERSECT);
                if (!p.isEmpty()){
                    System.out.println(String.valueOf(i) + " " + String.valueOf(j));
                    pm = new PathMeasure(p, false);
                    pm.getPosTan(0, aCoordinates, null);
                    x = aCoordinates[0];
                    y = aCoordinates[1];
                    boolean bo = true;
                    for (int k = 0; bo && k < points.length; k+=2){
                        if (70*70 > (points[k] - x)*(points[k] - x) + (points[k+1] - y)*(points[k+1] - y)){
                            bo = false;
                        }
                    }
                    pm.getPosTan(pm.getLength(), aCoordinates, null);
                    x = aCoordinates[0];
                    y = aCoordinates[1];
                    for (int k = 0; bo && k < points.length; k+=2){
                        if (70*70 > (points[k] - x)*(points[k] - x) + (points[k+1] - y)*(points[k+1] - y)){
                            bo = false;
                        }
                    }
                    if (bo) return true;
                }
            }
        }
        return false;
    }

    static float[] push(float[] p, float x1, float y1){
        float[] res = new float[p.length + 2];
        for (int i = 0; i < p.length; ++i)
            res[i] = p[i];
        res[p.length] = x1;
        res[p.length+1] = y1;
        return res;
    }

    static Path[] pushpa(Path[] p){
        Path[] res = new Path[p.length+1];
        for (int i = 0; i < p.length; ++i)
            res[i] = p[i];
        res[p.length] = new Path();
        return res;
    }

    public void closest_point(float x1, float y1, boolean move){
        float res = (points[0] - x1)*(points[0] - x1) + (points[1] - y1)*(points[1] - y1);
        int pos = 0;
        for (int i = 2; i < points.length; i+=2) {
            if (res > (points[i] - x1)*(points[i] - x1) + (points[i+1] - y1)*(points[i+1] - y1)) {
                res = (points[i] - x1) * (points[i] - x1) + (points[i + 1] - y1) * (points[i + 1] - y1);
                pos = i;
            }
        }
        if (move) path[path.length - 1].moveTo(points[pos], points[pos + 1]);
        else path[path.length - 1].lineTo(points[pos], points[pos + 1]);
    }

    public boolean closest_path(float x1, float y1){
        Path p = new Path();
        float radius = 1;
        while (radius < 50) {
            p.addCircle(x1,y1,radius,Path.Direction.CW);
            for (int i = 0; i < path.length; ++i) {
                Path copy = new Path();
                copy.op(p,path[i], Path.Op.INTERSECT);
                if (!copy.isEmpty()){
                    pm = new PathMeasure(copy, false);
                    pm.getPosTan(pm.getLength() * 0.5f, aCoordinates, null);
                    x = aCoordinates[0];
                    y = aCoordinates[1];
                    return true;
                }
            }
            radius += 0.2;
        }
        return false;
    }

    public static void undo(){
            punt = !punt;
            if (punt && points.length > 4) {
                float[] p = new float[points.length - 2];
                for (int i = 0; i < points.length - 2; i += 2) {
                    p[i] = points[i];
                    p[i + 1] = points[i + 1];
                }
                points = p;
            } else {
                Path[] p = new Path[path.length - 1];
                for (int i = 0; i < path.length - 1; ++i) {
                    p[i] = path[i];
                }
                path = p;
            }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        x = event.getX();
        y = event.getY();
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (!punt) {
                    if (startpa){
                        startpa = false;
                        path[0] = new Path();
                    }
                    else path = pushpa(path);
                    closest_point(x,y,true);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (punt) return true;
                path[path.length-1].lineTo(x, y);
                break;
            case MotionEvent.ACTION_UP:
                punt = !punt;
                if (punt){
                    closest_point(x,y,false);
                    if (intersect())  undo();
                }
                else {
                    boolean a = closest_path(x,y);
                    if (!a) {
                        punt = !punt;
                    }
                    else points = push(points,x,y);
                    if (!punt){
                        if (player1 == 1) player1 = 0;
                        else player1 = 1;
                        MainActivity.setPlayer(player1);
                    }
                }
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }
}