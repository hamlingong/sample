package com.myapp.mostlife.seatview.view;

import java.util.ArrayList;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * 手势监听，控制View缩放
 *
 * @author hamlingong
 */
class GestureListener extends GestureDetector.SimpleOnGestureListener {
    private SeatView mSeatView;

    GestureListener(SeatView paramSeatView) {
        mSeatView = paramSeatView;
    }

    public boolean onDoubleTap(MotionEvent paramMotionEvent) {
        return super.onDoubleTap(paramMotionEvent);
    }

    public boolean onDoubleTapEvent(MotionEvent paramMotionEvent) {
        return super.onDoubleTapEvent(paramMotionEvent);
    }

    public boolean onDown(MotionEvent paramMotionEvent) {
        return false;
    }

    public boolean onFling(MotionEvent paramMotionEvent1,
                           MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2) {
        return false;
    }

    public void onLongPress(MotionEvent paramMotionEvent) {
    }

    public boolean onScroll(MotionEvent paramMotionEvent1,
                            MotionEvent paramMotionEvent2, float x_scroll_distance, float y_scroll_distance) {
        //是否可以移动和点击
        if(!SeatView.a(mSeatView)){
            return false;
        }
        //显示缩略图
        SeatView.a(mSeatView,true);
        boolean bool1 = true;
        boolean bool2 = true;
        if ((SeatView.s(mSeatView) < mSeatView.getMeasuredWidth())
                && (0.0F == SeatView.v(mSeatView))){
            bool1 = false;
        }

        if ((SeatView.u(mSeatView) < mSeatView.getMeasuredHeight())
                && (0.0F == SeatView.w(mSeatView))){
            bool2  = false;
        }

        if(bool1){
            int k = Math.round(x_scroll_distance);
            //修改排数x轴的偏移量
            SeatView.c(mSeatView, (float)k);
//			Log.i("TAG", SSView.v(mSsView)+"");
            //修改座位距离排数的横向距离
            SeatView.k(mSeatView, k);
//			Log.i("TAG", SSView.r(mSsView)+"");
            if (SeatView.r(mSeatView) < 0) {
                //滑到最左
                SeatView.i(mSeatView, 0);
                SeatView.a(mSeatView, 0.0F);
            }

            if(SeatView.r(mSeatView) + mSeatView.getMeasuredWidth() > SeatView.s(mSeatView)){
                //滑到最右
                SeatView.i(mSeatView, SeatView.s(mSeatView) - mSeatView.getMeasuredWidth());
                SeatView.a(mSeatView, (float)(mSeatView.getMeasuredWidth() - SeatView.s(mSeatView)));
            }
        }

        if(bool2){
            //上负下正- 往下滑则减
            int j = Math.round(y_scroll_distance);
            //修改排数y轴的偏移量
            SeatView.updateRowYOffset(mSeatView, (float) j);
            //修改可视座位距离顶端的距离
            SeatView.l(mSeatView, j);
            Log.i("TAG", SeatView.t(mSeatView)+"");
            if (SeatView.t(mSeatView) < 0){
                //滑到顶
                SeatView.j(mSeatView, 0);
                SeatView.b(mSeatView, 0.0F);
            }

            if (SeatView.t(mSeatView) + mSeatView.getMeasuredHeight() > SeatView
                    .u(mSeatView)){
                //滑到底
                SeatView.j(mSeatView, SeatView.u(mSeatView) - mSeatView.getMeasuredHeight());
                SeatView.b(mSeatView, (float)(mSeatView.getMeasuredHeight() - SeatView.u(mSeatView)));
            }
        }

        mSeatView.invalidate();

//		Log.i("GestureDetector", "onScroll----------------------");
        return false;
    }

    public void onShowPress(MotionEvent paramMotionEvent) {
    }

    public boolean onSingleTapConfirmed(MotionEvent paramMotionEvent) {
        return false;
    }

    public boolean onSingleTapUp(MotionEvent paramMotionEvent) {
//		Log.i("GestureDetector", "onSingleTapUp");
//		if(!SSView.a(mSsView)){
//			return false;
//		}
        //列数
        int i = SeatView.a(mSeatView, (int)paramMotionEvent.getX());
        //排数
        int j = SeatView.b(mSeatView, (int) paramMotionEvent.getY());

        if((j>=0 && j< SeatView.b(mSeatView).size())){
            if(i>=0 && i<((ArrayList<Integer>)(SeatView.b(mSeatView).get(j))).size()){
                Log.i("TAG", "排数："+ j + "列数："+i);
                ArrayList<Integer> localArrayList = (ArrayList<Integer>) SeatView.b(mSeatView).get(j);
                switch (localArrayList.get(i).intValue()) {
                    case 3://已选中
                        localArrayList.set(i, Integer.valueOf(1));
                        if(SeatView.addClickListener(mSeatView)!=null){
                            SeatView.addClickListener(mSeatView).onCancel(i, j, false);
                        }
                        break;
                    case 1://可选
                        localArrayList.set(i, Integer.valueOf(3));
                        if(SeatView.addClickListener(mSeatView)!=null){
                            SeatView.addClickListener(mSeatView).onSelected(i, j, false);
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        //显示缩略图
        SeatView.a(mSeatView,true);
        mSeatView.invalidate();
        return false;
    }
}