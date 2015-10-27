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
    private int currentHeight = 57;
    /** 每个座位的宽度 */
    private int currentWidth = 57;
    /** 座位之间的间距 */
    private int seatDistance = 5;
    private double T = 1.0D;

    private double t = -1.0D;
    private double u = 1.0D;
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
    private int I = 0;
    private int betweenOffset = 2;
    private int seatCheckSize = 50;
    private ThumbnailSeatView mThumbnailSeatView = null;
    private int thumbnailViewWidth = 120;
    private int thumbnailViewHeight = 90;
    private int thumbnailViewStrokeWidth = 2;
    /** 选座缩略图 */
    private Bitmap mThumbnailViewBitMap = null;
    private volatile int V = 1500;
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

    private ArrayList<SeatInfo> mListSeatInfos = null;
    private ArrayList<ArrayList<Integer>> mListSeatConditions = null;
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

    public void init(int row_count, int rows,
                     ArrayList<SeatInfo> list_seatInfos,
                     ArrayList<ArrayList<Integer>> list_seat_condtions,
                     ThumbnailSeatView paramThumbnailSeatView, int imaxPay) {
        this.iMaxPay = imaxPay;
        this.mThumbnailSeatView = paramThumbnailSeatView;
        this.totalCountEachRow = row_count;
        this.rows = rows;
        this.mListSeatInfos = list_seatInfos;
        this.mListSeatConditions = list_seat_condtions;
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
        this.currentHeight = this.mContext.getResources()
                .getDimensionPixelSize(R.dimen.seat_init_height);
        this.currentWidth = this.mContext.getResources()
                .getDimensionPixelSize(R.dimen.seat_init_width);
        this.seatCheckSize = this.mContext.getResources()
                .getDimensionPixelSize(R.dimen.ss_seat_check_size);//30dp
        this.betweenOffset = this.mContext.getResources()
                .getDimensionPixelSize(R.dimen.ss_between_offset);//5dp
        invalidate();
    }

    public static Bitmap getBitmapFromDrawable(
            BitmapDrawable paramBitmapDrawable) {
        return paramBitmapDrawable.getBitmap();
    }

    /**
     *
     * @param seatNum
     *            每排的座位顺序号
     * @param rowNum
     *            排号
     * @param paramBitmap
     * @param paramCanvas1
     * @param paramCanvas2
     * @param paramPaint
     */
    private void drawSeat(int seatNum, int rowNum, Bitmap paramBitmap,
                          Canvas paramCanvas1, Canvas paramCanvas2, Paint paramPaint) {
        if (paramBitmap == null) {// 走道
            paramCanvas1.drawRect(getSeatRect(seatNum, rowNum), paramPaint);
            if (this.showThumbnail) {
                paramCanvas2.drawRect(d(seatNum, rowNum), paramPaint);
            }
        } else {
            paramCanvas1.drawBitmap(paramBitmap, null, getSeatRect(seatNum, rowNum),
                    paramPaint);
            if (this.showThumbnail) {
                paramCanvas2.drawBitmap(paramBitmap, null, d(seatNum, rowNum),
                        paramPaint);
            }
        }
    }

    /**
     *
     * @param seatNum
     *            每排的座位号
     * @param rowNum
     *            排号
     * @return
     */
    private Rect getSeatRect(int seatNum, int rowNum) {
        try {
            Rect localRect = new Rect(this.marginLeft + seatNum * this.currentWidth + this.seatDistance,
                    this.marginTop + rowNum * this.currentHeight + this.seatDistance, this.marginLeft + (seatNum + 1)
                    * this.currentWidth - this.seatDistance, this.marginTop + (rowNum + 1) * this.currentHeight
                    - this.seatDistance);
            return localRect;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return new Rect();
    }

    private Rect d(int seatNum, int rowNum) {
        try {
            Rect localRect = new Rect(
                    5 + (int) (this.T * (this.marginLeft + seatNum * this.currentWidth + this.seatDistance)),
                    5 + (int) (this.T * (this.marginTop + rowNum * this.currentHeight + this.seatDistance)),
                    5 + (int) (this.T * (this.marginLeft + (seatNum + 1) * this.currentWidth - this.seatDistance)),
                    5 + (int) (this.T * (this.marginTop + (rowNum + 1) * this.currentHeight - this.seatDistance)));
            return localRect;
        } catch (Exception localException) {
            localException.printStackTrace();
        }
        return new Rect();
    }

    private Rect e(int paramInt1, int paramInt2) {
        int i1;
        int i3;
        try {
            if (getMeasuredWidth() < this.mWidth) {
                i1 = getMeasuredWidth();
            } else {
                i1 = this.mWidth;
            }
            if (getMeasuredHeight() < this.mHeight) {
                i3 = getMeasuredHeight();
            } else {
                i3 = this.mHeight;
            }
            return new Rect((int) (5.0D + this.T * paramInt1),
                    (int) (5.0D + this.T * paramInt2), (int) (5.0D + this.T
                    * paramInt1 + i1 * this.T), (int) (5.0D + this.T
                    * paramInt2 + i3 * this.T));

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
        if (this.currentWidth != 0 && this.currentHeight != 0) {

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
                    / (this.currentWidth * this.totalCountEachRow + this.marginLeft + this.marginRight); // -
            // v0/v2
            double d2 = (this.thumbnailViewHeight - 10.0D)
                    / (this.currentHeight * this.rows);
            if (d1 <= d2) {
                this.T = d1;
            } else {
                this.T = d2;
            }
            if(this.showThumbnail){
                localPaint2.setColor(-16777216);
                if(firstLoadBg){
                    firstLoadBg = false;
                    tempX = 5+(int) (this.mWidth * this.T);
                    tempY = 5 + (int) (this.mHeight * this.T);
                }
                this.mThumbnailCanvas.drawRect(5.0F, 5.0F, tempX,
                        tempY, localPaint2);
            }
        }

        paramCanvas.translate(this.rowXOffset, this.rowYOffset);
        this.mWidth = this.marginLeft + this.currentWidth * this.totalCountEachRow + this.marginRight;
        this.mHeight = this.currentHeight * this.rows;

        this.marginLeft = (int) Math.round(this.currentWidth / 2.0D);
        localPaint2.setTextAlign(Paint.Align.CENTER);
        localPaint2.setAntiAlias(true);
        localPaint2.setColor(-16777216);
        for (int i2 = 0; i2 < this.mListSeatConditions.size(); i2++) {
            ArrayList<Integer> localArrayList = (ArrayList<Integer>) this.mListSeatConditions
                    .get(i2);

            for (int i3 = 0; i3 < this.mListSeatInfos.get(i2).getSeatList()
                    .size(); i3++) {// 2344
                // goto5 - 2344
                Seat localSeat = this.mListSeatInfos.get(i2).getSeat(i3);
                switch (((Integer) localArrayList.get(i3)).intValue()) { // 2373
                    case 0: // 2401 - 走道
                        localPaint2.setColor(0);
                        drawSeat(i3, i2, null, paramCanvas, this.mThumbnailCanvas, localPaint2);
                        localPaint2.setColor(-16777216);
                        break;
                    case 1:// 可选
                        drawSeat(i3, i2, this.mBitMapSeatNormal, paramCanvas,
                                this.mThumbnailCanvas, localPaint2);
                        break;
                    case 2://
                        drawSeat(i3, i2, this.mBitMapSeatLock, paramCanvas,
                                this.mThumbnailCanvas, localPaint2);
                        break;
                    case 3: // 2500-一已点击的状态
                        drawSeat(i3, i2, this.mBitMapSeatChecked, paramCanvas,
                                this.mThumbnailCanvas, localPaint2);
                        break;
                    default:
                        break;
                }
            }
            // cond_d - 2538
        }

        // 画排数
        localPaint2.setTextSize(0.4F * this.currentHeight);
        for (int i1 = 0; i1 < this.mListSeatInfos.size(); i1++) {
            localPaint2.setColor(-1308622848);
            paramCanvas.drawRect(new Rect((int) Math.abs(this.rowXOffset), this.marginTop
                    + i1 * this.currentHeight, (int) Math.abs(this.rowXOffset) + this.currentWidth / 2,
                    this.marginTop + (i1 + 1) * this.currentHeight), localPaint2);
            localPaint2.setColor(-1);
            paramCanvas
                    .drawText(((SeatInfo) this.mListSeatInfos.get(i1))
                            .getDesc(), (int) Math.abs(this.rowXOffset) + this.currentWidth / 2
                            / 2, this.marginTop + i1 * this.currentHeight + this.currentHeight / 2 + this.marginBottom
                            / 2, localPaint2);
        }


        if (this.showThumbnail) {
            // 画缩略图的黄色框
            localPaint2.setColor(-739328);
            localPaint2.setStyle(Paint.Style.STROKE);
            localPaint2.setStrokeWidth(this.thumbnailViewStrokeWidth);
            this.mThumbnailCanvas.drawRect(
                    e((int) Math.abs(this.rowXOffset), (int) Math.abs(this.rowYOffset)),
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
     * @param paramMotionEvent
     * @return
     */
    private float getTwoPoiniterDistance(MotionEvent paramMotionEvent) {
        float f1 = paramMotionEvent.getX(0) - paramMotionEvent.getX(1);
        float f2 = paramMotionEvent.getY(0) - paramMotionEvent.getY(1);
        return FloatMath.sqrt(f1 * f1 + f2 * f2);
    }

    private void zoom(MotionEvent paramMotionEvent) {
        double d1 = getTwoPoiniterDistance(paramMotionEvent);
        if (this.t < 0.0D) {
            this.t = d1;
        } else {
            try {
                this.u = (d1 / this.t);
                this.t = d1;
                if ((this.canZoom) && (Math.round(this.u * this.currentWidth) > 0L)
                        && (Math.round(this.u * this.currentHeight) > 0L)) {
                    this.currentWidth = (int) Math.round(this.u * this.currentWidth);
                    this.currentHeight = (int) Math.round(this.u * this.currentHeight);
                    this.marginLeft = (int) Math.round(this.currentWidth / 2.0D);
                    this.marginRight = this.marginLeft;
                    this.seatDistance = (int) Math.round(this.u * this.seatDistance);
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
     * new added
     *
     * @return
     */
    public static int m(SeatView mSeatView) {
        return mSeatView.marginLeft;
    }

    /**
     * new added
     *
     * @param mSeatView
     * @param paramInt
     * @return
     */
    public static int m(SeatView mSeatView, int paramInt) {
        mSeatView.V = mSeatView.V - paramInt;
        return mSeatView.V;
    }

    /**
     * new added
     *
     * @param mSeatView
     * @return
     */
    public static int x(SeatView mSeatView) {
        return mSeatView.V;
    }

    /**
     * new added
     *
     * @param mSeatView
     */
    public static void y(SeatView mSeatView) {
        mSeatView.a();
    }

    private void a() {
        // postDelayed(new ag(this), 500L);
    }

    /**
     * new added
     *
     * @param mSeatView
     * @return
     */
    public static float w(SeatView mSeatView) {
        return mSeatView.rowYOffset;
    }

    /**
     * 获取排数x轴偏移量
     *
     * @param mSeatView
     * @return
     */
    public static float v(SeatView mSeatView) {
        return mSeatView.rowXOffset;
    }

    /**
     * 获取整个view的高度
     *
     * @param mSeatView
     * @return
     */
    public static int u(SeatView mSeatView) {
        return mSeatView.mHeight;
    }

    /**
     * 获取可视座位距离顶端的距离
     *
     * @param mSeatView
     * @return
     */
    public static int t(SeatView mSeatView) {
        return mSeatView.visibleSeatMarginTop;
    }

    /**
     * 获取整个view的宽度
     *
     * @param mSeatView
     * @return
     */
    public static int s(SeatView mSeatView) {
        return mSeatView.mWidth;
    }

    /**
     * 获取座位距离排数的横向距离
     *
     * @param mSeatView
     * @return
     */
    public static int r(SeatView mSeatView) {
        return mSeatView.offsetRow;
    }

    /**
     * new added
     *
     * @param mSeatView
     * @return
     */
    public static int q(SeatView mSeatView) {
        return mSeatView.marginTop;
    }

    /**
     * new added
     *
     * @param mSeatView
     * @return
     */
    public static int p(SeatView mSeatView) {
        return mSeatView.rows;
    }

    /**
     * new added
     *
     * @param mSeatView
     * @return
     */
    public static int o(SeatView mSeatView) {
        return mSeatView.marginRight;
    }

    /**
     * new added
     *
     * @param mSeatView
     * @return
     */
    public static int n(SeatView mSeatView) {
        return mSeatView.totalCountEachRow;
    }

    /**
     * 修改可见座位距离顶端的距离
     *
     * @param mSeatView
     * @return
     */
    public static int l(SeatView mSeatView, int paramInt) {
        mSeatView.visibleSeatMarginTop = mSeatView.visibleSeatMarginTop + paramInt;
        return mSeatView.visibleSeatMarginTop;
    }

    /**
     * new added
     *
     * @param mSeatView
     * @return
     */
    public static int l(SeatView mSeatView) {
        return mSeatView.currentWidth;
    }

    /**
     * new added
     *
     * @param mSeatView
     * @return
     */
    public static int k(SeatView mSeatView) {
        return mSeatView.currentWidth;
    }

    /**
     * new added
     *
     * @param mSeatView
     * @param paramInt
     * @return
     */
    public static int k(SeatView mSeatView, int paramInt) {
        mSeatView.offsetRow = mSeatView.offsetRow + paramInt;
        return mSeatView.offsetRow;
    }

    /**
     * new added
     *
     * @param mSeatView
     * @return
     */
    public static int j(SeatView mSeatView) {
        return mSeatView.currentHeight;
    }

    /**
     * 设置可视座位距离顶端的距离
     *
     * @param mSeatView
     * @param paramInt
     * @return
     */
    public static int j(SeatView mSeatView, int paramInt) {
        mSeatView.visibleSeatMarginTop = paramInt;
        return mSeatView.visibleSeatMarginTop;
    }

    /**
     * 设置座位距离排数的横向距离
     *
     * @param mSeatView
     * @return
     */
    public static int i(SeatView mSeatView, int paramInt) {
        mSeatView.offsetRow = paramInt;
        return mSeatView.offsetRow;
    }

    /**
     * new added
     *
     * @param mSeatView
     * @return
     */
    public static boolean i(SeatView mSeatView) {
        return mSeatView.showThumbnail;
    }

    /**
     * new added
     *
     * @param mSeatView
     * @return
     */
    public static int h(SeatView mSeatView, int paramInt) {
        return mSeatView.mHeight;
    }

    /**
     * new added
     *
     * @param mSeatView
     * @return
     */
    public static int h(SeatView mSeatView) {
        return mSeatView.I + 1;
    }

    /**
     * new added
     *
     * @param mSeatView
     * @return
     */
    public static int g(SeatView mSeatView, int paramInt) {
        return mSeatView.mWidth;
    }

    /**
     * 获取最大支付座位数
     *
     * @param mSeatView
     * @return
     */
    public static int getImaxPay(SeatView mSeatView) {
        return mSeatView.iMaxPay;
    }

    /**
     * new added
     *
     * @param mSeatView
     * @param param
     * @return
     */
    public static boolean a(SeatView mSeatView, boolean param) {
        mSeatView.showThumbnail = param;
        return mSeatView.showThumbnail;
    }

    /**
     * 设置排数x轴偏移量
     *
     * @param mSeatView
     * @param param
     * @return
     */
    public static float a(SeatView mSeatView, float param) {
        mSeatView.rowXOffset = param;
        return mSeatView.rowXOffset;
    }

    /**
     * 计算是第几列
     *
     * @param mSeatView
     * @param param
     * @return
     */
    public static int a(SeatView mSeatView, int param) {
        return mSeatView.a(param);
    }

    /**
     * 计算是第几列
     *
     * @param paramInt
     * @return
     */
    private int a(int paramInt) {
        try {
            int i1 = (paramInt + this.offsetRow - this.marginLeft) / this.currentWidth;
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
     * @param param1
     * @param param2
     * @return
     */
    public static Rect a(SeatView mSeatView, int param1, int param2) {
        return mSeatView.f(param1, param2);
    }

    private Rect f(int paramInt1, int paramInt2) {
        try {
            int v1 = this.currentWidth * paramInt1 + this.marginLeft - this.offsetRow - this.seatDistance;
            int v2 = this.currentHeight * paramInt2 + this.marginTop - this.visibleSeatMarginTop - this.seatDistance;
            int v3 = (paramInt1 + 1) * this.currentWidth + this.marginLeft - this.offsetRow + this.seatDistance;
            int v4 = (this.marginTop + 1) * this.currentHeight + this.marginTop - this.visibleSeatMarginTop + this.seatDistance;
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
    public static boolean a(SeatView mSeatView) {
        return mSeatView.canMove;
    }

    private int b() {
        return (int) Math.round(this.currentWidth / this.seatCheckSize
                * this.betweenOffset);
    }

    /**
     * 修改排数x轴的
     *
     * @param mSeatView
     * @param param
     * @return
     */
    public static float c(SeatView mSeatView, float param) {
        mSeatView.rowXOffset = mSeatView.rowXOffset - param;
        return mSeatView.rowXOffset;
    }

    /**
     * 设置每个座位的高度
     *
     * @param mSeatView
     * @param param
     * @return
     */
    public static float c(SeatView mSeatView, int param) {
        mSeatView.currentHeight = param;
        return mSeatView.currentHeight;
    }

    /**
     * new added
     *
     * @param mSeatView
     * @return
     */
    public static ArrayList c(SeatView mSeatView) {
        return mSeatView.mListSeatInfos;
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
     * new added
     *
     * @param mSeatView
     * @param param
     * @return
     */
    public static int getSeatWidth(SeatView mSeatView, int param) {
        mSeatView.currentWidth = param;
        return mSeatView.currentWidth;
    }

    /**
     * new added
     *
     * @param mSeatView
     * @return
     */
    public static OnSeatClickListener addClickListener(SeatView mSeatView) {
        return mSeatView.mOnSeatClickListener;
    }

    /**
     * 设置排数y轴偏移量
     *
     * @param mSeatView
     * @param param
     * @return
     */
    public static float b(SeatView mSeatView, float param) {
        mSeatView.rowYOffset = param;
        return mSeatView.rowYOffset;
    }

    /**
     * 计算是第几排
     *
     * @param mSeatView
     * @param param
     * @return
     */
    public static int b(SeatView mSeatView, int param) {
        return mSeatView.b(param);
    }

    /**
     * new added
     *
     * @param mSeatView
     * @return
     */
    public static ArrayList b(SeatView mSeatView) {
        return mSeatView.mListSeatConditions;
    }

    /**
     * 计算是第几排
     *
     * @param paramInt
     * @return
     */
    private int b(int paramInt) {
        try {
            int i1 = (paramInt + this.visibleSeatMarginTop - this.marginTop) / this.currentHeight;
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
     * @param param
     * @return
     */
    public static int e(SeatView mSeatView, int param) {
        mSeatView.marginLeft = param;
        return mSeatView.marginLeft;
    }

    /**
     * new added
     *
     * @param mSeatView
     * @return
     */
    public static int e(SeatView mSeatView) {
        mSeatView.I--;
        return mSeatView.I;
    }

    /**
     * new added
     *
     * @param mSeatView
     * @return
     */
    public static int f(SeatView mSeatView) {
        return mSeatView.I;
    }

    /**
     * new added
     *
     * @param mSeatView
     * @param param
     * @return
     */
    public static int f(SeatView mSeatView, int param) {
        mSeatView.marginRight = param;
        return mSeatView.marginRight;
    }

    /**
     * 设置按钮点击事件
     *
     * @param paramOnSeatClickLinstener
     */
    public void setOnSeatClickListener(
            OnSeatClickListener paramOnSeatClickLinstener) {
        this.mOnSeatClickListener = paramOnSeatClickLinstener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getPointerCount() == 1) {
            if (this.canZoom) {
                this.canZoom = false;
                this.canMove = false;
                this.t = -1.0D;
                this.u = 1.0D;
            }else{
                this.canMove = true;
            }

            // Toast.makeText(mContext, "单点触控", Toast.LENGTH_SHORT).show();
            while (this.currentWidth < this.minWidth || this.currentHeight < this.minHeight) {
                this.currentWidth++;
                this.currentHeight++;
                this.marginLeft = (int) Math.round(this.currentWidth / 2.0D);
                this.marginRight = this.marginLeft;
                this.seatDistance = b();
                // 滑到最左和最上
                SeatView.i(this, 0);
                SeatView.a(this, 0.0F);
                SeatView.j(this, 0);
                SeatView.b(this, 0.0F);
                invalidate();
            }
            while ((this.currentWidth > this.maxWidth) || (this.currentHeight > this.maxHeight)) {
                this.currentWidth--;
                this.currentHeight--;
                this.marginLeft = (int) Math.round(this.currentWidth / 2.0D);
                this.marginRight = this.marginLeft;
                this.seatDistance = b();
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
