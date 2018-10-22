package com.o88o.bluetoothrp8.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.o88o.bluetoothrp8.R;

public class HistogramView extends View{
	/**
     * 第一步：声明画笔
     */
    private Paint mPaint_X;//X坐标轴画笔
    private Paint mPaint_Y;//Y坐标轴画笔
    private Paint mPaint_InsideLine;//内部虚线P
    private Paint mPaint_Text;//字体画笔
    private Paint mPaint_Y_Text;
    private Paint mPaint_Rec;//矩形画笔
    private Paint buttonPaint; // 柱状图像
    //数据
    private int[] data = new int[]{1,1,1,1,1};
    //视图的宽高
    private int width;
    private int height;

    private Bitmap barDownGreen;
    private Bitmap barDownRed;
    private Bitmap barDownYellow;
    private Bitmap barDownGray;
    private Bitmap barDownBright;

    private Bitmap barWaterMax;
    private Bitmap barWaterMaxBlack;
    private Bitmap barWaterMin;
    private Bitmap barWaterMinBlack;

    //坐标轴数据
    private String[] mText_Y = new String[]{"100","66","33",""};
    private String[] mText_Y_label = new String[]{"Full","2/3","1/3","Empty"};
    private String[] mText_X = new String[]{"","FRESH","GREY1","GREY2","BLK","WASH"};//默认X轴坐标

    PathEffect effects ;
    Path path ;

    public HistogramView(Context context) {
        super(context);
        init(context,null);
    }
 
    public HistogramView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }
 
    /**
     * 更新XY轴坐标
     */
    public void upDateTextForX(String[] text_X)
    {
        mText_X = text_X;
        this.postInvalidate();  //更新视图
    }
 
    /**
     * 更新数据
     */
    public void upData(int [] data)
    {
        this.data = data;
        this.postInvalidate();  //更新视图
        //mText_Y = getText_Y(data);
    }
 
    /**
     * 初始化画笔
     */
    private void init(Context context, AttributeSet attrs) {
        mPaint_X = new Paint();
        mPaint_InsideLine = new Paint();
        mPaint_Text = new Paint();
        mPaint_Y_Text = new Paint();
        mPaint_Rec = new Paint();
        mPaint_Y = new Paint();

        buttonPaint = new Paint(Paint.DITHER_FLAG);
        buttonPaint.setAntiAlias(true);

        mPaint_X.setColor(Color.LTGRAY);
        mPaint_X.setStrokeWidth(3);
 
        mPaint_Y.setColor(Color.GRAY);
 
        mPaint_InsideLine.setColor(Color.LTGRAY);
        mPaint_InsideLine.setAntiAlias(true);
        //设置画直线格式
        mPaint_InsideLine.setStyle ( Paint.Style.STROKE ) ;
        //设置虚线效果  已失效
        mPaint_InsideLine.setPathEffect ( new DashPathEffect( new float [ ] { 5, 10 }, 0 ) ) ;
 
        mPaint_Text.setTextSize(25);
        mPaint_Text.setTextAlign(Paint.Align.CENTER);

        mPaint_Y_Text.setTextSize(25);
        mPaint_Y_Text.setTextAlign(Paint.Align.CENTER);
        mPaint_Y_Text.setColor(Color.GREEN);
        mPaint_Rec.setColor(Color.GRAY);

        barDownYellow = BitmapFactory.decodeResource(getResources(), R.mipmap.bar_down_yellow_new);
        barDownGreen = BitmapFactory.decodeResource(getResources(), R.mipmap.bar_down_green_new);
        barDownRed = BitmapFactory.decodeResource(getResources(), R.mipmap.bar_down_red_new);
        barDownGray = BitmapFactory.decodeResource(getResources(), R.mipmap.bar_down_gray_new);;
        barDownBright = BitmapFactory.decodeResource(getResources(), R.mipmap.bar_down_bright_new);

        barWaterMax = BitmapFactory.decodeResource(getResources(), R.mipmap.bar_water_max);
        barWaterMaxBlack = BitmapFactory.decodeResource(getResources(), R.mipmap.bar_water_max_black);
        barWaterMin = BitmapFactory.decodeResource(getResources(), R.mipmap.bar_water_min);
        barWaterMinBlack = BitmapFactory.decodeResource(getResources(), R.mipmap.bar_water_min_black);

        effects = new DashPathEffect(new float[]{5,10},0);
        path = new Path();
    }
 
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getWidth();
        height = getHeight();
        int leftHeight_Every = (height-50 -10)/(mText_Y.length-1); //Y轴每个数据的间距
        int downWeight_Every = (width-50)/mText_X.length;//X轴每个数据的间距
 
        //画X坐标轴
        canvas.drawLine(0, height-10, width, height-10, mPaint_X);
        //画Y坐标轴
        //canvas.drawLine(50, height - 50, 50, 0, mPaint_Y);

        //画内部灰线
        for(int i= 1;i < mText_Y.length+1;i++){
            path.moveTo(50, height - 50 - (i * leftHeight_Every));
            path.lineTo(width - 50, height - 50 - (i * leftHeight_Every));
            mPaint_InsideLine.setPathEffect(effects);
            canvas.drawPath(path, mPaint_InsideLine);
            //canvas.drawLine(50, height - 50 - (i * leftHeight_Every), width - 50, height - 50 - (i * leftHeight_Every), mPaint_InsideLine);
        }
        path.moveTo(50, height - 50 );
        path.lineTo(width - 50, height - 50);
        mPaint_InsideLine.setPathEffect(effects);
        canvas.drawPath(path, mPaint_InsideLine);

        //画X轴坐标
        for(int i =1;i<mText_X.length+1;i++){
            canvas.drawText(mText_X[i - 1], 50 + downWeight_Every * (i - 1), height - 20, mPaint_Y_Text);
        }
 
        if(this.data != null && this.data.length >0){
            //画Y轴坐标
            for(int i =1;i<mText_Y.length+1;i++){
                canvas.drawText(mText_Y_label[i-1],35,leftHeight_Every*(i-1)+20,mPaint_Text);
            }

            int data_Y_One = Integer.parseInt(mText_Y[mText_Y.length-1-1]); //Y轴首个数值
            //可配置
            int min = height-50-(leftHeight_Every/3);
            int max = height-50-((mText_Y.length-1)*leftHeight_Every)+(leftHeight_Every/3);
            int half = height-50-((mText_Y.length-1)*leftHeight_Every)/2;
            //画矩形
            for(int i =1;i<data.length+1;i++){
                double data_Yx = (double)data[i-1]/data_Y_One;
                RectF rect = new RectF();
                rect.left  = 50+ downWeight_Every * i  - 30;
                rect.right = 50+ downWeight_Every * i  + 30;
                rect.top = (height-50-(int)(data_Yx*leftHeight_Every));
                rect.bottom = height-50 + 5;

                canvas.drawBitmap(barDownBright, null, new RectF(rect.left,height-50-10-leftHeight_Every*mText_Y.length,rect.right,rect.bottom), buttonPaint);

                canvas.drawBitmap(barDownGray, null, rect, buttonPaint);

                //color
                if(rect.top > min){
                    canvas.drawBitmap(barDownGray, null, rect, buttonPaint);
                }
                if(rect.top > half && rect.top <= min){
                    canvas.drawBitmap(barDownRed, null, rect, buttonPaint);
                }
                if(rect.top > max && rect.top <= half){
                    canvas.drawBitmap(barDownYellow, null, rect, buttonPaint);
                }
                if(rect.top <= max){
                    canvas.drawBitmap(barDownGreen, null, rect, buttonPaint);
                }
                //min
                if(rect.top > min){
                    canvas.drawBitmap(barWaterMinBlack, null, new RectF(rect.left,min-barWaterMinBlack.getHeight()*(rect.right - rect.left)/barWaterMinBlack.getWidth(),rect.right,min), buttonPaint);
                }else{
                    canvas.drawBitmap(barWaterMin, null, new RectF(rect.left,min-barWaterMin.getHeight()*(rect.right - rect.left)/barWaterMin.getWidth(),rect.right,min), buttonPaint);
                }
                //max
                if(rect.top > max){
                    canvas.drawBitmap(barWaterMaxBlack, null, new RectF(rect.left,max-barWaterMaxBlack.getHeight()*(rect.right - rect.left)/barWaterMaxBlack.getWidth(),rect.right,max), buttonPaint);
                }else{
                    canvas.drawBitmap(barWaterMax, null, new RectF(rect.left,max-barWaterMax.getHeight()*(rect.right - rect.left)/barWaterMax.getWidth(),rect.right,max), buttonPaint);
                }

            }
        }
    }
 
 
 
    /**
     * 获取一组数据的最大值
     */
    public static int getMax(int[] arr) {
        int max = arr[0];
        for(int x=1;x<arr.length;x++)
        {
            if(arr[x]>max)
                max = arr[x];
        }
        return max;
    }
 
    /**
     * 功能：根据传入的数据动态的改变Y轴的坐标
     * 返回：取首数字的前两位并除以2，后面变成0。作为Y轴的基坐标
     */
    public static String[] getText_Y(int[] data){
        String[] text_Y;
        int textY = 0;
 
        String numMax = getMax(data)+"";
        char[] charArray = numMax.toCharArray();
        int dataLength = charArray.length;//数据长度 eg：5684：4位
        String twoNumString = "";
        if(dataLength >= 2){
            for (int i = 0; i < 2; i++) {
                twoNumString += charArray[i];
            }
            int twoNum =Integer.parseInt(twoNumString);
            textY = (int) Math.ceil(twoNum/3);
            //将数据前两位后加上“0” 用于返回前两位的整数
            if(dataLength - 2 == 1){
                textY *= 10;
            }else if(dataLength -2 == 2){
                textY *= 100;
            }else if(dataLength -2 == 3){
                textY *= 1000;
            }else if(dataLength -2 == 4){
                textY *= 10000;
            }else if(dataLength -2 == 5){
                textY *= 100000;
            }
            text_Y = new String[]{"", textY * 3 + "", textY * 2 + "", textY + ""};
 
        }else{
            text_Y = new String[]{"", 15+"",10+"",5+""};
        }
        return text_Y;
    }
 
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
 
        if (widthSpecMode == MeasureSpec.EXACTLY || widthSpecMode == MeasureSpec.AT_MOST) {
            width = widthSpecSize;
        } else {
            width = 0;
        }
        if (heightSpecMode == MeasureSpec.AT_MOST || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            height = dipToPx(200);
        } else {
            height = heightSpecSize;
        }
        setMeasuredDimension(width, height);
    }
 
    private int dipToPx(int dip) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }
}
