package com.tradelink.scandocapp;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.tradelink.scandocapp.model.TLKRect;
import com.tradelink.scandocapp.model.TLKVertex;

public class DrawView extends View {

    Point[] points = new Point[4];
    private ArrayList<TLKRect> rects = new ArrayList<>();
    Paint paint;
    Canvas canvas;
    private int canvasW, canvasH;
    private int previousX = -1, previousY = -1;
    private boolean rectangleResize = false;

    public DrawView(Context context) {
        super(context);
        paint = new Paint();
        setFocusable(true); // necessary for getting the touch events
        canvas = new Canvas();
    }

    public DrawView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        setFocusable(true); // necessary for getting the touch events
        canvas = new Canvas();
    }

    // the method that draws the balls
    @Override
    protected void onDraw(Canvas canvas) {
        if(rects.size() == 0) //point4 null when user did not touch and move on screen.
            return;

        for(int j=0; j<rects.size(); j++) {
            canvasW = canvas.getWidth();
            canvasH = canvas.getHeight();
            int left, top, right, bottom;

            left = rects.get(j).getVertices().get(0).getX();
            top = rects.get(j).getVertices().get(0).getY();
            right = rects.get(j).getVertices().get(0).getX();
            bottom = rects.get(j).getVertices().get(0).getY();
            for (int i = 1; i < rects.get(j).getVertices().size(); i++) {
                left = left >rects.get(j).getVertices().get(i).getX() ? rects.get(j).getVertices().get(i).getX():left;
                top = top > rects.get(j).getVertices().get(i).getY() ? rects.get(j).getVertices().get(i).getY():top;
                right = right < rects.get(j).getVertices().get(i).getX() ? rects.get(j).getVertices().get(i).getX():right;
                bottom = bottom < rects.get(j).getVertices().get(i).getY() ? rects.get(j).getVertices().get(i).getY():bottom;
            }
            paint.setAntiAlias(true);
            paint.setDither(true);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(5);

            //draw stroke
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.parseColor("#AA000000"));
            paint.setStrokeWidth(2);
            canvas.drawRect(
                    left + rects.get(j).getVertices().get(0).getWidthOfBall() / 2,
                    top + rects.get(j).getVertices().get(0).getWidthOfBall() / 2,
                    right + rects.get(j).getVertices().get(2).getWidthOfBall() / 2,
                    bottom + rects.get(j).getVertices().get(2).getWidthOfBall() / 2, paint);
            //fill the rectangle
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.parseColor("#25404040"));
            paint.setStrokeWidth(0);
            canvas.drawRect(
                    left + rects.get(j).getVertices().get(0).getWidthOfBall() / 2,
                    top + rects.get(j).getVertices().get(0).getWidthOfBall() / 2,
                    right + rects.get(j).getVertices().get(2).getWidthOfBall() / 2,
                    bottom + rects.get(j).getVertices().get(2).getWidthOfBall() / 2, paint);

            // draw the balls on the canvas
            paint.setColor(Color.BLUE);
            paint.setTextSize(18);
            paint.setStrokeWidth(0);
            for (int i = 0; i < rects.get(j).getVertices().size(); i ++) {
                TLKVertex ball = rects.get(j).getVertices().get(i);
                canvas.drawBitmap(ball.getBitmap(), ball.getX(), ball.getY(),
                        paint);
                canvas.drawText("" + (i+1), ball.getX(), ball.getY(), paint);
            }
        }
        invalidate();
    }

    // events when touching the screen
    public boolean onTouchEvent(MotionEvent event) {
        int X = (int) event.getX();
        int Y = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: // touch down so check if the finger is on
                // a ball
                if (points[0] == null) {
                    TLKRect rect = new TLKRect();
                    //initialize rectangle.
                    points[0] = new Point();
                    points[0].x = X;
                    points[0].y = Y;

                    points[1] = new Point();
                    points[1].x = X;
                    points[1].y = Y + 50;

                    points[2] = new Point();
                    points[2].x = X + 50;
                    points[2].y = Y + 50;

                    points[3] = new Point();
                    points[3].x = X + 50;
                    points[3].y = Y;

                    rect.setBalID(2);
                    rect.setGroupID(1);
                    // declare each ball with the TLKVertex class
                    for (int i=0; i<points.length; i++) {
                        rect.getVertices().add(new TLKVertex(getContext(),
                                R.drawable.ui_crop_corner_handle, points[i], i));
                    }
                    rects.add(rect);
                } else {
                    for(int j=0; j<rects.size(); j++) {
                        //resize rectangle
                        rects.get(j).setBalID(-1);
                        rects.get(j).setGroupID(-1);
                        for (int i = rects.get(j).getVertices().size()-1; i>=0; i--) {
                            TLKVertex ball = rects.get(j).getVertices().get(i);
                            // check if inside the bounds of the ball (circle)
                            // get the center for the ball
                            int centerX = ball.getX() + ball.getWidthOfBall();
                            int centerY = ball.getY() + ball.getHeightOfBall();
                            paint.setColor(Color.CYAN);
                            // calculate the radius from the touch to the center of the ball
                            double radCircle = Math
                                    .sqrt((double) (((centerX - X) * (centerX - X)) + (centerY - Y)
                                            * (centerY - Y)));
                            if (radCircle < ball.getWidthOfBall()) {
                                rects.get(j).setBalID(ball.getID());
                                if (rects.get(j).getBalID() == 1 || rects.get(j).getBalID() == 3) {
                                    rects.get(j).setGroupID(2);
                                } else {
                                    rects.get(j).setGroupID(1);
                                }
                                rectangleResize = true;
                                invalidate();
                                break;
                            }
                            invalidate();
                        }
                        if (rects.get(j).getVertices().size() != 0) {
                            rects.get(j).setInRectangle(inRectangle(rects.get(j).getVertices(), X, Y));
                        }
                    }

                }
                previousX = X;
                previousY = Y;
                break;

            case MotionEvent.ACTION_MOVE: // touch drag with the ball
                if (X + 50 > canvasW || Y + 50 > canvasH) {
                    break;
                }
                for(int j=0; j<rects.size(); j++) {
                    if (!rects.get(j).isInRectangle() || rectangleResize) {
                        rects.get(j).setBalID(rects.get(j).getBalID());
                        if (rects.get(j).getBalID() > -1 && rects.get(j).getBalID() < 4) {
                            // move the balls the same as the finger
                            rects.get(j).getVertices().get(rects.get(j).getBalID()).setX(X);
                            rects.get(j).getVertices().get(rects.get(j).getBalID()).setY(Y);

                            paint.setColor(Color.CYAN);
                            if (rects.get(j).getGroupID() == 1) {
                                rects.get(j).getVertices().get(1).setX(rects.get(j).getVertices().get(0).getX());
                                rects.get(j).getVertices().get(1).setY(rects.get(j).getVertices().get(2).getY());
                                rects.get(j).getVertices().get(3).setX(rects.get(j).getVertices().get(2).getX());
                                rects.get(j).getVertices().get(3).setY(rects.get(j).getVertices().get(0).getY());
                                invalidate();
                                break;
                            } else {
                                rects.get(j).getVertices().get(0).setX(rects.get(j).getVertices().get(1).getX());
                                rects.get(j).getVertices().get(0).setY(rects.get(j).getVertices().get(3).getY());
                                rects.get(j).getVertices().get(2).setX(rects.get(j).getVertices().get(3).getX());
                                rects.get(j).getVertices().get(2).setY(rects.get(j).getVertices().get(1).getY());
                                invalidate();
                                break;
                            }
                        }
                    } else {
                        if (previousX != -1 && previousY != -1) {
                            rects.get(j).getVertices().get(0).setX(rects.get(j).getVertices().get(0).getX() + (X - previousX));
                            rects.get(j).getVertices().get(0).setY(rects.get(j).getVertices().get(0).getY() + (Y - previousY));
                            rects.get(j).getVertices().get(1).setX(rects.get(j).getVertices().get(1).getX() + (X - previousX));
                            rects.get(j).getVertices().get(1).setY(rects.get(j).getVertices().get(1).getY() + (Y - previousY));
                            rects.get(j).getVertices().get(2).setX(rects.get(j).getVertices().get(2).getX() + (X - previousX));
                            rects.get(j).getVertices().get(2).setY(rects.get(j).getVertices().get(2).getY() + (Y - previousY));
                            rects.get(j).getVertices().get(3).setX(rects.get(j).getVertices().get(3).getX() + (X - previousX));
                            rects.get(j).getVertices().get(3).setY(rects.get(j).getVertices().get(3).getY() + (Y - previousY));
                        }

                        previousX = X;
                        previousY = Y;
                        invalidate();
                    }
                }

                break;

            case MotionEvent.ACTION_UP:
                // touch drop - just do things here after dropping
                rectangleResize = false;
                break;
        }
        // redraw the canvas
        invalidate();
        return true;

    }

    private boolean inRectangle(ArrayList<TLKVertex> balls, int x, int y) {
        int leftX, rightX, topY, bottomY;
        if (balls.get(0).getX() > balls.get(3).getX()) {
            leftX = balls.get(3).getX();
            rightX = balls.get(0).getX();
        } else {
            leftX = balls.get(0).getX();
            rightX = balls.get(3).getX();
        }
        if (balls.get(0).getY() > balls.get(1).getY()) {
            topY = balls.get(1).getY();
            bottomY = balls.get(0).getY();
        } else {
            topY = balls.get(0).getY();
            bottomY = balls.get(1).getY();
        }
        return leftX < x && x < rightX && topY < y && y < bottomY;
    }

    public void addBox() {
        TLKRect rect = new TLKRect();
        points[0] = new Point();
        points[0].x = 0;
        points[0].y = 0;

        points[1] = new Point();
        points[1].x = 0;
        points[1].y = 200;

        points[2] = new Point();
        points[2].x = 200;
        points[2].y = 200;

        points[3] = new Point();
        points[3].x = 200;
        points[3].y = 0;

        rect.setBalID(2);
        rect.setGroupID(1);
        // declare each ball with the TLKVertex class
        for (int i=0; i<points.length; i++) {
            rect.getVertices().add(new TLKVertex(getContext(), R.drawable.ui_crop_corner_handle, points[i], i));
        }
        rects.add(rect);
    }
}