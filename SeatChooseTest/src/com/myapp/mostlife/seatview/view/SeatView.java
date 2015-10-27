package com.myapp.mostlife.seatview.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.myapp.mostlife.R;
import com.myapp.mostlife.seatview.model.Seat;
import com.myapp.mostlife.seatview.model.SeatInfo;

/**
 * 影院选座View
 *
 * @author hamlingong 2015-10-27
 */
public class SeatView extends View {
    Context mContext;
    int xOffset = 0;

    /** 普通状态 */
    private Bitmap mBitMapSeatNormal = null;
    /** 已锁定 */
    private Bitmap mBitMapSeatLock = null;
    /** 已选中 */
    private Bitmap mBitMapSeatChecked = null;

    /** 缩略图画布 */
    private Canvas mThumbnailCanvas = null;

    /** 是否显示缩略图 */
    private boolean showThumbnail = false;

    /** 每个座位的高度 - 57 */
    private int seatHeight = 57;
    /** 每个座位的宽度 */
    private int seatWidth = 57;
    /** 座位之间的间距 */
    private int seatDistance = 5;
    /** 缩略View的缩略比率*/
    private double thumbRate = 1.0D;

    /** 保存上一次的比率*/
    private double preRate = -1.0D;
    /** 缩放比率*/
    private double rate = 1.0D;
    /** 是否可缩放 */
    private boolean canZoom = false;

    /** 座位最小高度 */
    private int minHeight = 0;
    /** 座位最大高度 */
    private int maxHeight = 0;
    /** 座位最小宽度 */
    private int minWidth = 0;
    /** 座位最大宽度 */
    private int maxWidth = 0;

    private OnSeatClickListener mOnSeatClickListener = null;

    public static double a = 1.0E-006D;
    private int betweenOffset = 2;
    private int seatCheckSize = 50;
    private ThumbnailSeatView mThumbnailSeatView = null;
    private int thumbnailViewWidth = 120;
    private int thumbnailViewHeight = 90;
    private int thumbnailViewStrokeWidth = 2;
    /** 选座缩略图 */
    private Bitmap mThumbnailViewBitMap = null;
    /** 左边距 */
    private int marginLeft = 0;
    /** 右边距 */
    private int marginRight = 0;
    /** 上边距 */
    private int marginTop = 0;
    /** 下边距 */
    private int marginBottom = 0;
    /** 排数x轴偏移量 */
    private float rowXOffset = 0.0F;
    /** 排数y轴偏移量 */
    private float rowYOffset = 0.0F;
    /** 座位距离排数的距离 */
    private int offsetRow = 0;
    /** 可视座位距离顶端的距离 */
    private int visibleSeatMarginTop = 0;
    /** 整个view的宽度 */
    private int mWidth = 0;
    /** 整个view的高度 */
    private int mHeight = 0;
    /** 能否移动 */
    private boolean canMove = true;

    private boolean firstLoadBg = true;
    private int tempX;
    private int tempY;

    GestureDetector mGestureDetector = new GestureDetector(mContext,
            new GestureListener(this));

    private ArrayList<SeatInfo> mSeatInfos = null;
    private ArrayList<ArrayList<Integer>> mSeatConditions = null;
    private int iMaxPay = 0;
    private int totalCountEachRow;
    private int rows;

    public SeatView(Context paramContext, AttributeSet paramAttributeSet) {
        this(paramContext, paramAttributeSet, 0);
    }

    public SeatView(Context paramContext, AttributeSet paramAttributeSet,
                    int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
        this.mContext = paramContext;
    }

    /**
     * 初始化View的参数，包括view的宽，高等
     *
     * @param row_count
     * @param rows
     * @param list_seatInfos
     * @param list_seat_condtions
     * @param paramThumbnailSeatView
     * @param imaxPay
     */
    public void init(int row_count, int rows,
                     ArrayList<SeatInfo> list_seatInfos,
                     ArrayList<ArrayList<Integer>> list_seat_condtions,
                     ThumbnailSeatView paramThumbnailSeatView, int imaxPay) {
        this.iMaxPay = imaxPay;
        this.mThumbnailSeatView = paramThumbnailSeatView;
        this.totalCountEachRow = row_count;
        this.rows = rows;
        this.mSeatInfos = list_seatInfos;
        this.mSeatConditions = list_seat_condtions;
        this.mBitMapSeatNormal = getBitmapFromDrawable((BitmapDrawable) this.mContext
                .getResources().getDrawable(R.drawable.seat_normal));
        this.mBitMapSeatLock = getBitmapFromDrawable((BitmapDrawable) this.mContext
                .getResources().getDrawable(R.drawable.seat_lock));
        this.mBitMapSeatChecked = getBitmapFromDrawable((BitmapDrawable) this.mContext
                .getResources().getDrawable(R.drawable.seat_checked));

        this.thumbnailViewWidth = this.mContext.getResources()
                .getDimensionPixelSize(R.dimen.ss_seat_thum_size_w);
        this.thumbnailViewHeight = this.mContext.getResources()
                .getDimensionPixelSize(R.dimen.ss_seat_thum_size_h);

        this.maxHeight = this.mContext.getResources()
                .getDimensionPixelSize(R.dimen.seat_max_height);
        this.maxWidth = this.mContext.getResources()
                .getDimensionPixelSize(R.dimen.seat_max_width);
        this.minHeight = this.mContext.getResources()
                .getDimensionPixelSize(R.dimen.seat_min_height);
        this.minWidth = this.mContext.getResources()
                .getDimensionPixelSize(R.dimen.seat_min_width);
        this.seatHeight = this.mContext.getResources()
                .getDimensionPixelSize(R.dimen.seat_init_height);
        this.seatWidth = this.mContext.getResources()
                .getDimensionPixelSize(R.dimen.seat_init_width);
        this.seatCheckSize = this.mContext.getResources()
                .getDimensionPixelSize(R.dimen.ss_seat_check_size);//30dp
        this.betweenOffset = this.mContext.getResources()
                .getDimensionPixelSize(R.dimen.ss_between_offset);//5dp
        invalidate();
    }

    public static Bitmap getBitmapFromDrawable(
            BitmapDrawable bitmapDrawable) {
        return bitmapDrawable.getBitmap();
    }

    /**
     * 绘制View与Thumbnail View的Seat的位置
     *
     * @param seatNum
     *            每排的座位顺序号
     * @param rowNum
     *            排号
     * @param paramBitmap
     * @param canvas
     * @param thumbCanvas
     * @param paramPaint
     */
    private void drawSeat(int seatNum, int rowNum, Bitmap paramBitmap,
                          Canvas canvas, Canvas thumbCanvas, Paint paramPaint) {
        if (paramBitmap == null) {// 走道
            canvas.drawRect(getSeatRect(seatNum, rowNum), paramPaint);
            if (this.showThumbnail) {
                thumbCanvas.drawRect(getThumbnailSeatRect(seatNum, rowNum), paramPaint);
            }
        } else {
            canvas.drawBitmap(paramBitmap, null, getSeatRect(seatNum, rowNum),
                    paramPaint);
            if (this.showThumbnail) {
                thumbCanvas.drawBitmap(paramBitmap, null, getThumbnailSeatRect(seatNum, rowNum),
                        paramPaint);
            }
        }
    }

    /**
     * 根据传入的排，列位置计算其在view的Rect
     *
     * @param seatNum
     *            每排的座位号
     * @param rowNum
     *            排号
     * @return
     */
    private Rect getSeatRect(int seatNum, int rowNum) {
        try {
            Rect localRect = new Rect(this.marginLeft + seatNum * this.seatWidth + this.seatDistance,
                    this.marginTop + rowNum * this.seatHeight + this.seatDistance, this.marginLeft + (seatNum + 1)
                    * this.seatWidth - this.seatDistance, this.marginTop + (rowNum + 1) * this.seatHeight
                    - this.seatDistance);
            return localRect;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return new Rect();
    }

    /**
     * 根据传入的排，列位置计算其在view的Rect
     *
     * @param seatNum
     * @param rowNum
     * @return
     */
    private Rect getThumbnailSeatRect(int seatNum, int rowNum) {
        try {
            Rect localRect = new Rect(
                    5 + (int) (this.thumbRate * (this.marginLeft + seatNum * this.seatWidth + this.seatDistance)),
                    5 + (int) (this.thumbRate * (this.marginTop + rowNum * this.seatHeight + this.seatDistance)),
                    5 + (int) (this.thumbRate * (this.marginLeft + (seatNum + 1) * this.seatWidth - this.seatDistance)),
                    5 + (int) (this.thumbRate * (this.marginTop + (rowNum + 1) * this.seatHeight - this.seatDistance)));
            return localRect;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return new Rect();
    }

    private Rect getThumbnailRect(int paramInt1, int paramInt2) {
        int width;
        int height;
        try {
            if (getMeasuredWidth() < this.mWidth) {
                width = getMeasuredWidth();
            } else {
                width = this.mWidth;
            }
            if (getMeasuredHeight() < this.mHeight) {
                height = getMeasuredHeight();
            } else {
                height = this.mHeight;
            }
            return new Rect((int) (5.0D + this.thumbRate * paramInt1),
                    (int) (5.0D + this.thumbRate * paramInt2), (int) (5.0D + this.thumbRate
                    * paramInt1 + width * this.thumbRate), (int) (5.0D + this.thumbRate
                    * paramInt2 + height * this.thumbRate));

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return new Rect();
        }
    }

    @Override
    protected void onDraw(Canvas paramCanvas) {
        super.onDraw(paramCanvas);
        // Log.marginRight("TAG", "onDraw()...");
        if (this.totalCountEachRow == 0 || this.rows == 0) {
            return;
        }

        if (this.rowXOffset + this.mWidth < 0.0f || this.rowYOffset + this.mHeight < 0.0f) {
            this.rowXOffset = 0.0f;
            this.rowYOffset = 0.0f;
            this.offsetRow = 0;
            this.visibleSeatMarginTop = 0;
        }
        Paint localPaint2 = new Paint();
        if (this.seatWidth != 0 && this.seatHeight != 0) {

            this.mThumbnailViewBitMap = Bitmap.createBitmap(this.thumbnailViewWidth,
                    this.thumbnailViewHeight, Bitmap.Config.ARGB_8888);
            this.mThumbnailCanvas = new Canvas();
            this.mThumbnailCanvas.setBitmap(this.mThumbnailViewBitMap);
            this.mThumbnailCanvas.save();

            Paint localPaint1 = new Paint();
            localPaint1.setXfermode(new PorterDuffXfermode(
                    PorterDuff.Mode.CLEAR));
            this.mThumbnailCanvas.drawPaint(localPaint1);

            double d1 = (this.thumbnailViewWidth - 10.0D)
                    / (this.seatWidth * this.totalCountEachRow + this.marginLeft + this.marginRight); // -
            // v0/v2
            double d2 = (this.thumbnailViewHeight - 10.0D)
                    / (this.seatHeight * this.rows);
            if (d1 <= d2) {
                this.thumbRate = d1;
            } else {
                this.thumbRate = d2;
            }
            if(this.showThumbnail){
                localPaint2.setColor(-16777216);
                if(firstLoadBg){
                    firstLoadBg = false;
                    tempX = 5+(int) (this.mWidth * this.thumbRate);
                    tempY = 5 + (int) (this.mHeight * this.thumbRate);
                }
                this.mThumbnailCanvas.drawRect(5.0F, 5.0F, tempX,
                        tempY, localPaint2);
            }
        }

        paramCanvas.translate(this.rowXOffset, this.rowYOffset);
        this.mWidth = this.marginLeft + this.seatWidth * this.totalCountEachRow + this.marginRight;
        this.mHeight = this.seatHeight * this.rows;

        this.marginLeft = (int) Math.round(this.seatWidth / 2.0D);
        localPaint2.setTextAlign(Paint.Align.CENTER);
        localPaint2.setAntiAlias(true);
        localPaint2.setColor(-16777216);
        for (int i = 0; i < this.mSeatConditions.size(); i++) {
            ArrayList<Integer> localArrayList = (ArrayList<Integer>) this.mSeatConditions
                    .get(i);

            for (int j = 0; j < this.mSeatInfos.get(i).getSeatList()
                    .size(); j++) {// 2344
                // goto5 - 2344
                Seat localSeat = this.mSeatInfos.get(i).getSeat(j);
                switch (((Integer) localArrayList.get(j)).intValue()) { // 2373
                    case 0: // 2401 - 走道
                        localPaint2.setColor(0);
                        drawSeat(j, i, null, paramCanvas, this.mThumbnailCanvas, localPaint2);
                        localPaint2.setColor(-16777216);
                        break;
                    case 1:// 可选
                        drawSeat(j, i, this.mBitMapSeatNormal, paramCanvas,
                                this.mThumbnailCanvas, localPaint2);
                        break;
                    case 2://
                        drawSeat(j, i, this.mBitMapSeatLock, paramCanvas,
                                this.mThumbnailCanvas, localPaint2);
                        break;
                    case 3: // 2500-一已点击的状态
                        drawSeat(j, i, this.mBitMapSeatChecked, paramCanvas,
                                this.mThumbnailCanvas, localPaint2);
                        break;
                    default:
                        break;
                }
            }
            // cond_d - 2538
        }

        // 画排数
        localPaint2.setTextSize(0.4F * this.seatHeight);
        for (int i = 0; i < this.mSeatInfos.size(); i++) {
            localPaint2.setColor(-1308622848);
            paramCanvas.drawRect(new Rect((int) Math.abs(this.rowXOffset), this.marginTop
                    + i * this.seatHeight, (int) Math.abs(this.rowXOffset) + this.seatWidth / 2,
                    this.marginTop + (i + 1) * this.seatHeight), localPaint2);
            localPaint2.setColor(-1);
            paramCanvas.drawText(((SeatInfo) this.mSeatInfos.get(i))
                    .getDesc(), (int) Math.abs(this.rowXOffset) + this.seatWidth / 2
                    / 2, this.marginTop + i * this.seatHeight + this.seatHeight / 2 + this.marginBottom
                    / 2, localPaint2);
        }


        if (this.showThumbnail) {
            // 画缩略图的黄色框
            localPaint2.setColor(-739328);
            localPaint2.setStyle(Paint.Style.STROKE);
            localPaint2.setStrokeWidth(this.thumbnailViewStrokeWidth);
            this.mThumbnailCanvas.drawRect(
                    getThumbnailRect((int) Math.abs(this.rowXOffset), (int) Math.abs(this.rowYOffset)),
                    localPaint2);
            localPaint2.setStyle(Paint.Style.FILL);
            // paramCanvas.restore();
            this.mThumbnailCanvas.restore();
        }

        if (this.mThumbnailSeatView != null) {
            this.mThumbnailSeatView.setThumbnailViewBitmap(mThumbnailViewBitMap);
            this.mThumbnailSeatView.invalidate();
        }

    }

    public void setXOffset(int xOffset) {
        this.xOffset = xOffset;
    }

    /**
     * 获取两点的直线距离
     *
     * @param event
     * @return
     */
    private float getTwoPoiniterDistance(MotionEvent event) {
        float f1 = event.getX(0) - event.getX(1);
        float f2 = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(f1 * f1 + f2 * f2);
    }

    /**
     * 缩放操作
     *
     * @param event
     */
    private void zoom(MotionEvent event) {
        double twoPoiniterDistance = getTwoPoiniterDistance(event);
        if (this.preRate < 0.0D) {
            this.preRate = twoPoiniterDistance;
        } else {
            try {
                this.rate = (twoPoiniterDistance / this.preRate);
                this.preRate = twoPoiniterDistance;
                if ((this.canZoom) && (Math.round(this.rate * this.seatWidth) > 0L)
                        && (Math.round(this.rate * this.seatHeight) > 0L)) {
                    this.seatWidth = (int) Math.round(this.rate * this.seatWidth);
                    this.seatHeight = (int) Math.round(this.rate * this.seatHeight);
                    this.marginLeft = (int) Math.round(this.seatWidth / 2.0D);
                    this.marginRight = this.marginLeft;
                    this.seatDistance = (int) Math.round(this.rate * this.seatDistance);
                    if (this.seatDistance <= 0)
                        this.seatDistance = 1;
                }
                invalidate();
            } catch (Exception localException) {
                localException.printStackTrace();
            }
        }
    }

    /**
     * 获得左边距
     *
     * @return
     */
    public static int getMarginLeft(SeatView mSeatView) {
        return mSeatView.marginLeft;
    }

    /**
     * new added
     *
     * @param mSeatView
     * @return
     */
    public static float getRowYOffset(SeatView mSeatView) {
        return mSeatView.rowYOffset;
    }

    /**
     * 获取排数x轴偏移量
     *
     * @param mSeatView
     * @return
     */
    public static float getRowXOffset(SeatView mSeatView) {
        return mSeatView.rowXOffset;
    }

    /**
     * 获取整个view的高度
     *
     * @param mSeatView
     * @return
     */
    public static int getHeight(SeatView mSeatView) {
        return mSeatView.mHeight;
    }

    /**
     * 获取可视座位距离顶端的距离
     *
     * @param mSeatView
     * @return
     */
    public static int getVisibleSeatMarginTop(SeatView mSeatView) {
        return mSeatView.visibleSeatMarginTop;
    }

    /**
     * 获取整个view的宽度
     *
     * @param mSeatView
     * @return
     */
    public static int getWidth(SeatView mSeatView) {
        return mSeatView.mWidth;
    }

    /**
     * 获取座位距离排数的横向距离
     *
     * @param mSeatView
     * @return
     */
    public static int getOffsetRow(SeatView mSeatView) {
        return mSeatView.offsetRow;
    }

    /**
     * 获得上边距离
     *
     * @param mSeatView
     * @return
     */
    public static int getMarginTop(SeatView mSeatView) {
        return mSeatView.marginTop;
    }

    /**
     * 获得排数
     *
     * @param mSeatView
     * @return
     */
    public static int getRows(SeatView mSeatView) {
        return mSeatView.rows;
    }

    /**
     * 获得右边距离
     *
     * @param mSeatView
     * @return
     */
    public static int getMarginRight(SeatView mSeatView) {
        return mSeatView.marginRight;
    }

    /**
     * 获得需要显示的排数
     *
     * @param mSeatView
     * @return
     */
    public static int getTotalCountEachRow(SeatView mSeatView) {
        return mSeatView.totalCountEachRow;
    }

    /**
     * 修改可见座位距离顶端的距离
     *
     * @param mSeatView
     * @return
     */
    public static int updateVisibleSeatMarginTop(SeatView mSeatView, int offset) {
        mSeatView.visibleSeatMarginTop = mSeatView.visibleSeatMarginTop + offset;
        return mSeatView.visibleSeatMarginTop;
    }

    /**
     * 获取座位的宽
     *
     * @param mSeatView
     * @return
     */
    public static int getSeatWidth(SeatView mSeatView) {
        return mSeatView.seatWidth;
    }

    /**
     * 获取座位的高
     *
     * @param mSeatView
     * @return
     */
    public static int getSeatHeight(SeatView mSeatView) {
        return mSeatView.seatHeight;
    }

    /**
     * 修改座位距离排数的距离
     *
     * @param mSeatView
     * @param offset
     * @return
     */
    public static int updateOffsetRow(SeatView mSeatView, int offset) {
        mSeatView.offsetRow = mSeatView.offsetRow + offset;
        return mSeatView.offsetRow;
    }

    /**
     * 设置可视座位距离顶端的距离
     *
     * @param mSeatView
     * @param paramInt
     * @return
     */
    public static int setVisibleSeatMarginTop(SeatView mSeatView, int paramInt) {
        mSeatView.visibleSeatMarginTop = paramInt;
        return mSeatView.visibleSeatMarginTop;
    }

    /**
     * 设置座位距离排数的横向距离
     *
     * @param mSeatView
     * @return
     */
    public static int setOffsetRow(SeatView mSeatView, int paramInt) {
        mSeatView.offsetRow = paramInt;
        return mSeatView.offsetRow;
    }

    /**
     * 是否显示缩略图
     *
     * @param mSeatView
     * @return
     */
    public static boolean isShowThumbnail(SeatView mSeatView) {
        return mSeatView.showThumbnail;
    }



    /**
     * 获取最大支付座位数
     *
     * @param mSeatView
     * @return
     */
    public static int getMaxPay(SeatView mSeatView) {
        return mSeatView.iMaxPay;
    }

    /**
     * 设置是否显示缩略图
     *
     * @param mSeatView
     * @param param
     * @return
     */
    public static boolean setShowThumbnail(SeatView mSeatView, boolean param) {
        mSeatView.showThumbnail = param;
        return mSeatView.showThumbnail;
    }

    /**
     * 设置排数x轴偏移量
     *
     * @param mSeatView
     * @param rowXOffset
     * @return
     */
    public static float setRowXOffset(SeatView mSeatView, float rowXOffset) {
        mSeatView.rowXOffset = rowXOffset;
        return mSeatView.rowXOffset;
    }

    /**
     * 计算是第几列
     *
     * @param mSeatView
     * @param position
     * @return
     */
    public static int calculateLine(SeatView mSeatView, int position) {
        return mSeatView.calculateLine(position);
    }

    /**
     * 计算是第几列
     *
     * @param position
     * @return
     */
    private int calculateLine(int position) {
        try {
            int i1 = (position + this.offsetRow - this.marginLeft) / this.seatWidth;
            return i1;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return -1;
    }

    /**
     * 获取缩放的seat的Rect
     *
     * @param mSeatView
     * @param widthRate
     * @param heightRate
     * @return
     */
    public static Rect getSeatRateRect(SeatView mSeatView, int widthRate, int heightRate) {
        return mSeatView.getSeatRateRect(widthRate, heightRate);
    }

    /**
     * 获取缩放的seat的Rect
     *
     * @param rateWidth
     * @param rateHeight
     * @return
     */
    private Rect getSeatRateRect(int rateWidth, int rateHeight) {
        try {
            int v1 = this.seatWidth * rateWidth + this.marginLeft - this.offsetRow - this.seatDistance;
            int v2 = this.seatHeight * rateHeight + this.marginTop - this.visibleSeatMarginTop - this.seatDistance;
            int v3 = (rateWidth + 1) * this.seatWidth + this.marginLeft - this.offsetRow + this.seatDistance;
            int v4 = (this.marginTop + 1) * this.seatHeight + this.marginTop - this.visibleSeatMarginTop + this.seatDistance;
            return new Rect(v1, v2, v3, v4);
        } catch (Exception e) {
            e.printStackTrace();
            return new Rect();
        }
    }

    /**
     * 是否可以移动和点击
     *
     * @param mSeatView
     * @return
     */
    public static boolean canMove(SeatView mSeatView) {
        return mSeatView.canMove;
    }

    /**
     * 计算座位间的距离
     *
     * @return
     */
    private int calculateSeatDistance() {
        return (int) Math.round(this.seatWidth / this.seatCheckSize
                * this.betweenOffset);
    }

    /**
     * 修改排数x轴的
     *
     * @param mSeatView
     * @param offset
     * @return
     */
    public static float updateRowXOffset(SeatView mSeatView, float offset) {
        mSeatView.rowXOffset = mSeatView.rowXOffset - offset;
        return mSeatView.rowXOffset;
    }

    /**
     * 设置每个座位的高度
     *
     * @param mSeatView
     * @param seatHeight
     * @return
     */
    public static float setSeatHeight(SeatView mSeatView, int seatHeight) {
        mSeatView.seatHeight = seatHeight;
        return mSeatView.seatHeight;
    }

    /**
     * 设置每排的座位信息
     *
     * @param mSeatView
     * @return
     */
    public static ArrayList setSeatInfos(SeatView mSeatView) {
        return mSeatView.mSeatInfos;
    }

    /**
     * 修改排数y轴的偏移量
     *
     * @param mSeatView
     * @param param
     * @return
     */
    public static float updateRowYOffset(SeatView mSeatView, float param) {
        mSeatView.rowYOffset = mSeatView.rowYOffset - param;
        return mSeatView.rowYOffset;
    }

    /**
     * 设置座位的宽
     *
     * @param mSeatView
     * @param param
     * @return
     */
    public static int setSeatWidth(SeatView mSeatView, int param) {
        mSeatView.seatWidth = param;
        return mSeatView.seatWidth;
    }

    /**
     * 获取座位点击事件Listener
     *
     * @param mSeatView
     * @return
     */
    public static OnSeatClickListener getClickListener(SeatView mSeatView) {
        return mSeatView.mOnSeatClickListener;
    }

    /**
     * 设置排数y轴偏移量
     *
     * @param mSeatView
     * @param param
     * @return
     */
    public static float setRowYOffset(SeatView mSeatView, float param) {
        mSeatView.rowYOffset = param;
        return mSeatView.rowYOffset;
    }

    /**
     * 计算是第几排
     *
     * @param mSeatView
     * @param position
     * @return
     */
    public static int calculateRow(SeatView mSeatView, int position) {
        return mSeatView.calculateRow(position);
    }

    /**
     * 获取所有位置状态信息
     *
     * @param mSeatView
     * @return
     */
    public static ArrayList getSeatConditions(SeatView mSeatView) {
        return mSeatView.mSeatConditions;
    }

    /**
     * 计算是第几排
     *
     * @param position
     * @return
     */
    private int calculateRow(int position) {
        try {
            int i1 = (position + this.visibleSeatMarginTop - this.marginTop) / this.seatHeight;
            return i1;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return -1;
    }

    /**
     * new added
     *
     * @param mSeatView
     * @param marginLeft
     * @return
     */
    public static int setMarginLeft(SeatView mSeatView, int marginLeft) {
        mSeatView.marginLeft = marginLeft;
        return mSeatView.marginLeft;
    }

    /**
     * new added
     *
     * @param mSeatView
     * @param marginRight
     * @return
     */
    public static int setMarginRight(SeatView mSeatView, int marginRight) {
        mSeatView.marginRight = marginRight;
        return mSeatView.marginRight;
    }

    /**
     * 设置按钮点击事件
     *
     * @param clickListener
     */
    public void setOnSeatClickListener(OnSeatClickListener clickListener) {
        this.mOnSeatClickListener = clickListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() == 1) {
            if (this.canZoom) {
                this.canZoom = false;
                this.canMove = false;
                this.preRate = -1.0D;
                this.rate = 1.0D;
            }else{
                this.canMove = true;
            }
            // Toast.makeText(mContext, "单点触控", Toast.LENGTH_SHORT).show();
            while (this.seatWidth < this.minWidth || this.seatHeight < this.minHeight) {
                this.seatWidth++;
                this.seatHeight++;
                this.marginLeft = (int) Math.round(this.seatWidth / 2.0D);
                this.marginRight = this.marginLeft;
                this.seatDistance = calculateSeatDistance();
                // 滑到最左和最上
                SeatView.setOffsetRow(this, 0);
                SeatView.setRowXOffset(this, 0.0F);
                SeatView.setVisibleSeatMarginTop(this, 0);
                SeatView.setRowYOffset(this, 0.0F);
                invalidate();
            }
            while ((this.seatWidth > this.maxWidth) || (this.seatHeight > this.maxHeight)) {
                this.seatWidth--;
                this.seatHeight--;
                this.marginLeft = (int) Math.round(this.seatWidth / 2.0D);
                this.marginRight = this.marginLeft;
                this.seatDistance = calculateSeatDistance();
                invalidate();
            }
            // 移动功能-点击事件
            this.mGestureDetector.onTouchEvent(event);
        } else {
            // Toast.makeText(mContext, "多点触控", Toast.LENGTH_SHORT).show();
            this.canZoom = true;
            zoom(event);
        }
        return true;
    }

}
