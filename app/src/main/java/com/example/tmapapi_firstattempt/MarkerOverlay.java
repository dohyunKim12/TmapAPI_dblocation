package com.example.tmapapi_firstattempt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

import com.skt.Tmap.TMapMarkerItem2;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapView;

public class MarkerOverlay extends TMapMarkerItem2 {

    private DisplayMetrics dm = null;

    private Context mContext = null;
    private BalloonOverlayView balloonView = null;
    private int mAnimationCount = 0;
    private Rect rect;


    @Override
    public Bitmap getIcon() {
        return super.getIcon();
    }

    @Override
    public void setIcon(Bitmap bitmap) {
        super.setIcon(bitmap);
    }

    @Override
    public void setTMapPoint(TMapPoint point) {
        super.setTMapPoint(point);
    }

    @Override
    public TMapPoint getTMapPoint() {
        return super.getTMapPoint();
    }

    @Override
    public void setPosition(float dx, float dy) {
        super.setPosition(dx, dy);
    }

    /**
     * 풍선뷰 영역을 설정한다.
     */
    @Override
    public void setCalloutRect(Rect rect) {
        super.setCalloutRect(rect);
    }

    public MarkerOverlay(Context context, String labelName, String id) {
        this.mContext = context;

        dm = new DisplayMetrics();
        WindowManager wmgr = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        wmgr.getDefaultDisplay().getMetrics(dm);

        balloonView = new BalloonOverlayView(mContext, labelName, id);

        balloonView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        balloonView.layout(0, 0, balloonView.getMeasuredWidth(), balloonView.getMeasuredHeight());
    }

    @Override
    public void draw(Canvas canvas, TMapView mapView, boolean showCallout) {
        int x = mapView.getRotatedMapXForPoint(getTMapPoint().getLatitude(), getTMapPoint().getLongitude());
        int y = mapView.getRotatedMapYForPoint(getTMapPoint().getLatitude(), getTMapPoint().getLongitude());

        canvas.save();
        canvas.rotate(-mapView.getRotate(), mapView.getCenterPointX(), mapView.getCenterPointY());

        float xPos = getPositionX();
        float yPos = getPositionY();

        int nPos_x, nPos_y;

        int nMarkerIconWidth = 0;
        int nMarkerIconHeight = 0;
        int marginX = 0;
        int marginY = 0;

        nMarkerIconWidth = getIcon().getWidth();
        nMarkerIconHeight = getIcon().getHeight();

        nPos_x = (int) (xPos * nMarkerIconWidth);
        nPos_y = (int) (yPos * nMarkerIconHeight);

        if(nPos_x == 0) {
            marginX = nMarkerIconWidth / 2;
        } else {
            marginX = nPos_x;
        }

        if(nPos_y == 0) {
            marginY = nMarkerIconHeight / 2;
        } else {
            marginY = nPos_y;
        }

        canvas.translate(x - marginX, y - marginY);
        canvas.drawBitmap(getIcon(), 0, 0, null);
        canvas.restore();

        if (showCallout) {
            canvas.save();
            canvas.rotate(-mapView.getRotate(), mapView.getCenterPointX(), mapView.getCenterPointY());

            balloonView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            int nTempX =  x - balloonView.getMeasuredWidth() / 2;
            int nTempY =  y - marginY - balloonView.getMeasuredHeight();

            canvas.translate(nTempX, nTempY);
            balloonView.draw(canvas);

            // 풍선뷰 영역 설정
            rect.left = nTempX;
            rect.top = nTempY;
            rect.right = rect.left + balloonView.getMeasuredWidth();
            rect.bottom = rect.top + balloonView.getMeasuredHeight();

            setCalloutRect(rect);
            canvas.restore();
        }
    }

    public boolean onSingleTapUp(PointF point, TMapView mapView) {
        mapView.showCallOutViewWithMarkerItemID(getID());
        return false;
    }

    Handler mHandler = null;

    @Override
    public void startAnimation() {
        super.startAnimation();

        Runnable mRunnable = new Runnable() {
            @Override
            public void run() {
                if (getAnimationIcons().size() > 0) {
                    if(mAnimationCount >= getAnimationIcons().size())
                        mAnimationCount = 0;

                    setIcon(getAnimationIcons().get(mAnimationCount));
//                    mMapView.postInvalidate();
                    mAnimationCount++;
                    mHandler.postDelayed(this, getAniDuration());
                }
            }
        };

        mHandler = new Handler();
        mHandler.post(mRunnable);
    }
}