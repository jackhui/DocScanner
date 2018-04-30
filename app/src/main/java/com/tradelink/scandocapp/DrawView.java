package com.tradelink.scandocapp;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.tradelink.scandocapp.model.Vertex;

public class DrawView extends View {

    Point[] points = new Point[4];

    /**
     * point 1 and point 3 are of same group and same as point 2 and point4
     */
    int groupId = -1;
    private ArrayList<Vertex> vertices = new ArrayList<>();
    // array that holds the balls
    private int balID = 0;
    // variable to know what ball is being dragged
    Paint paint;
    Canvas canvas;
    private Bitmap mDocument;
    private int canvasW, canvasH;
    private int previousX = -1, previousY = -1;
    private boolean rectangleResize = false, insideRectangle = false;;

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
        /*paint.setColor(Color.RED);
        paint.setAlpha(80);
        canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);
        paint.setAlpha(256);*/
        if(points[3]==null) //point4 null when user did not touch and move on screen.
            return;
        canvasW = canvas.getWidth();
        canvasH = canvas.getHeight();
        Log.d("DrawView", "canvas width " + canvas.getWidth() + " height " + canvas.getHeight());
        int left, top, right, bottom;
        left = points[0].x;
        top = points[0].y;
        right = points[0].x;
        bottom = points[0].y;
        for (int i = 1; i < points.length; i++) {
            left = left > points[i].x ? points[i].x:left;
            top = top > points[i].y ? points[i].y:top;
            right = right < points[i].x ? points[i].x:right;
            bottom = bottom < points[i].y ? points[i].y:bottom;
        }
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(5);

        //draw stroke
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#AADB1255"));
        paint.setStrokeWidth(2);
        canvas.drawRect(
                left + vertices.get(0).getWidthOfBall() / 2,
                top + vertices.get(0).getWidthOfBall() / 2,
                right + vertices.get(2).getWidthOfBall() / 2,
                bottom + vertices.get(2).getWidthOfBall() / 2, paint);
        //fill the rectangle
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#55DB1255"));
        paint.setStrokeWidth(0);
        canvas.drawRect(
                left + vertices.get(0).getWidthOfBall() / 2,
                top + vertices.get(0).getWidthOfBall() / 2,
                right + vertices.get(2).getWidthOfBall() / 2,
                bottom + vertices.get(2).getWidthOfBall() / 2, paint);

        //draw the corners
        BitmapDrawable bitmap = new BitmapDrawable();
        // draw the balls on the canvas
        paint.setColor(Color.BLUE);
        paint.setTextSize(18);
        paint.setStrokeWidth(0);
        for (int i = 0; i < vertices.size(); i ++) {
            Vertex ball = vertices.get(i);
            canvas.drawBitmap(ball.getBitmap(), ball.getX(), ball.getY(),
                    paint);

            canvas.drawText("" + (i+1), ball.getX(), ball.getY(), paint);
        }
    }

    // events when touching the screen
    public boolean onTouchEvent(MotionEvent event) {
        int eventaction = event.getAction();

        int X = (int) event.getX();
        int Y = (int) event.getY();

        switch (eventaction) {
            case MotionEvent.ACTION_DOWN: // touch down so check if the finger is on
                // a ball
                if (points[0] == null) {
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

                    balID = 2;
                    groupId = 1;
                    // declare each ball with the ColorBall class
                    for (Point pt : points) {
                        vertices.add(new Vertex(getContext(), R.drawable.ui_crop_corner_handle, pt));
                    }
                } else {
                    //resize rectangle
                    balID = -1;
                    groupId = -1;
                    for (int i = vertices.size()-1; i>=0; i--) {
                        Vertex ball = vertices.get(i);
                        // check if inside the bounds of the ball (circle)
                        // get the center for the ball
                        int centerX = ball.getX() + ball.getWidthOfBall();
                        int centerY = ball.getY() + ball.getHeightOfBall();
                        paint.setColor(Color.CYAN);
                        // calculate the radius from the touch to the center of the
                        // ball
                        double radCircle = Math
                                .sqrt((double) (((centerX - X) * (centerX - X)) + (centerY - Y)
                                        * (centerY - Y)));

                        if (radCircle < ball.getWidthOfBall()) {

                            balID = ball.getID();
                            if (balID == 1 || balID == 3) {
                                groupId = 2;
                            } else {
                                groupId = 1;
                            }
                            rectangleResize = true;
                            invalidate();
                            break;
                        }
                        invalidate();
                    }
                }
                previousX = X;
                previousY = Y;
                if(vertices.size() != 0) {
                    insideRectangle = inRectangle(vertices, X, Y);
                }
                break;

            case MotionEvent.ACTION_MOVE: // touch drag with the ball
                Log.d("motion ", "x " + X + " y " + Y + " canvas w " + canvas.getWidth() + " h " + canvas.getHeight());
                if (X + 50 > canvasW || Y + 50 > canvasH) {
                    break;
                }

                if (!insideRectangle || rectangleResize) {
                    Log.d("ActionMove", "not enter");
                    if (balID > -1 && balID < 4) {
                        // move the balls the same as the finger
                        vertices.get(balID).setX(X);
                        vertices.get(balID).setY(Y);

                        paint.setColor(Color.CYAN);
                        if (groupId == 1) {
                            vertices.get(1).setX(vertices.get(0).getX());
                            vertices.get(1).setY(vertices.get(2).getY());
                            vertices.get(3).setX(vertices.get(2).getX());
                            vertices.get(3).setY(vertices.get(0).getY());
                        } else {
                            vertices.get(0).setX(vertices.get(1).getX());
                            vertices.get(0).setY(vertices.get(3).getY());
                            vertices.get(2).setX(vertices.get(3).getX());
                            vertices.get(2).setY(vertices.get(1).getY());
                        }

                        invalidate();
                    }
                } else {
                    if (previousX != -1 && previousY != -1) {
                        vertices.get(0).setX(vertices.get(0).getX() + (X - previousX));
                        vertices.get(0).setY(vertices.get(0).getY() + (Y - previousY));
                        vertices.get(1).setX(vertices.get(1).getX() + (X - previousX));
                        vertices.get(1).setY(vertices.get(1).getY() + (Y - previousY));
                        vertices.get(2).setX(vertices.get(2).getX() + (X - previousX));
                        vertices.get(2).setY(vertices.get(2).getY() + (Y - previousY));
                        vertices.get(3).setX(vertices.get(3).getX() + (X - previousX));
                        vertices.get(3).setY(vertices.get(3).getY() + (Y - previousY));
                    }

                    previousX = X;
                    previousY = Y;
                    invalidate();
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

    private boolean inRectangle(ArrayList<Vertex> balls, int x, int y) {
        int leftX, rightX, topY, bottomY;
        if (balls.get(0).getX() > balls.get(3).getX()) {
            leftX = balls.get(3).getX() + 50;
            rightX = balls.get(0).getX() - 50;
        } else {
            leftX = balls.get(0).getX() + 50;
            rightX = balls.get(3).getX() - 50;
        }
        if (balls.get(0).getY() > balls.get(1).getY()) {
            topY = balls.get(1).getY() + 50;
            bottomY = balls.get(0).getY() - 50;
        } else {
            topY = balls.get(0).getY() + 50;
            bottomY = balls.get(1).getY() - 50;
        }
        return leftX < x && x < rightX && topY < y && y < bottomY;
    }

    public void setDocument(Bitmap document) {
        mDocument = document;
    }
}