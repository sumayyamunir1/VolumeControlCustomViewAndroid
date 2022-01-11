package com.sumayya.androidvolumecontrolcustomviewapp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class VolumeControlView extends View {
    private int volumeScale;
    private int volumeLevelPercentage;
    private int linesColor;

    private Paint mCurrentVolumePaint;
    private Paint mAvailableVolumePaint;
    private Paint mTextPaint;

    private IVolumeControlEventListener volumeControlEventListener;

    private int maxLineHeight = 35;
    private int maxSpaceBetweenLines = 12;
    private int lineHeight;
    private int spaceBetweenLines;
    private int combinedPartHeight;
    private int textSize = 40;
    private int spaceBetweenTextAndLines = 10;

    public VolumeControlView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray xmlProperties = context.getTheme().obtainStyledAttributes(attrs, R.styleable.VolumeControl,0, 0);

        try{
            volumeScale = xmlProperties.getInt(R.styleable.VolumeControl_volumeScale, 20);
            volumeLevelPercentage = xmlProperties.getInt(R.styleable.VolumeControl_volumeLevelPercentage, 50);
            linesColor = xmlProperties.getColor(R.styleable.VolumeControl_linesColor, Color.GRAY);
        }finally {
            xmlProperties.recycle();
        }

        mAvailableVolumePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mAvailableVolumePaint.setStyle(Paint.Style.FILL);
        mAvailableVolumePaint.setColor(Color.LTGRAY);

        mCurrentVolumePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCurrentVolumePaint.setStyle(Paint.Style.FILL);
        mCurrentVolumePaint.setColor(linesColor);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.GRAY);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(textSize);
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        calculateLineAndSpaceHeight(getHeight());

        int currentVolumeLines = (int)Math.ceil(volumeScale * volumeLevelPercentage/100f);
        int thirdWidth = getWidth()/3;
        int left = thirdWidth;
        int right = thirdWidth * 2;
        for(int i=0; i<volumeScale; i++){
            canvas.drawRect(left, i*combinedPartHeight, right, i*combinedPartHeight + lineHeight, volumeScale - i <= currentVolumeLines ? mCurrentVolumePaint : mAvailableVolumePaint);
        }
        Toast.makeText(getContext(), "Volume percentage is..."+this.volumeLevelPercentage, Toast.LENGTH_LONG).show();

    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                //Log.d("DOWN",event.getY() + "");
                return true;
            case (MotionEvent.ACTION_MOVE) :
                //Log.d("MOVE",event.getY() + "");
                if(event.getX() >= getWidth()/3f && event.getX() <= getWidth()/3f*2)
                    setVolumeLevel(getVolumeFromTouch(event.getY()));
                return true;
            case (MotionEvent.ACTION_UP) :
                //Log.d("UP",event.getY() + "");
                if(event.getX() >= getWidth()/3f && event.getX() <= getWidth()/3f*2)
                    setVolumeLevel(getVolumeFromTouch(event.getY()));
                return true;
            default :
                return super.onTouchEvent(event);
        }
    }

    private int getVolumeFromTouch(float y){
        float volumePercentage = 1-(y/(volumeScale*combinedPartHeight-spaceBetweenLines));
        double numberOfLines = Math.ceil(volumePercentage*volumeScale);
        double scaledPercentage = numberOfLines / volumeScale;
        int volume = (int)(scaledPercentage*100);

        if(volume > 100)
            return 100;
        else if(volume < 0)
            return 0;

        return volume;
    }

    private void calculateLineAndSpaceHeight(int controlHeight){
        //size of scale + text at the end
        int maxVolumeScaleHeight = (maxLineHeight + maxSpaceBetweenLines) * volumeScale + (textSize + spaceBetweenTextAndLines);

        //scale cannot be larger than control height
        if(volumeScale > controlHeight - textSize - spaceBetweenTextAndLines){
            volumeScale = controlHeight - textSize - spaceBetweenTextAndLines;
        }

        if(maxVolumeScaleHeight <= controlHeight){
            lineHeight = maxLineHeight;
            spaceBetweenLines = maxSpaceBetweenLines;
        }else{
            //height without text at the end
            int maxPartHeight = (controlHeight - textSize - spaceBetweenTextAndLines) / volumeScale;

            //line height should be 3/4 of available space
            lineHeight = (int)Math.ceil(maxPartHeight * (3f/4));
            spaceBetweenLines = (int)Math.floor(maxPartHeight * (1f/4));
        }

        combinedPartHeight = lineHeight + spaceBetweenLines;
    }

    public interface IVolumeControlEventListener{
        void onVolumeControlChanged();
    }

    public void setEventListener(IVolumeControlEventListener volumeControlEventListener) {
        this.volumeControlEventListener = volumeControlEventListener;
    }

    public int getVolumeScale(){
        return volumeScale;
    }

    public void setVolumeScale (int volumeScale){
        this.volumeScale = volumeScale;
        invalidate();
        requestLayout();
        volumeControlEventListener.onVolumeControlChanged();
    }

    public int getVolumeLevel() {
        return volumeLevelPercentage;
    }

    public void setVolumeLevel(int volumeLevel) {

        this.volumeLevelPercentage = volumeLevel;
        invalidate();
        requestLayout();
        volumeControlEventListener.onVolumeControlChanged();
    }

    public int getLinesColor() {
        return linesColor;
    }

    public void setLinesColor(int linesColor) {
        this.linesColor = linesColor;
        invalidate();
        requestLayout();
        volumeControlEventListener.onVolumeControlChanged();
    }


}
