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
        if(!SeatView.canMove(mSeatView)){
            return false;
        }
        //显示缩略图
        SeatView.setShowThumbnail(mSeatView, true);
        boolean bool1 = true;
        boolean bool2 = true;
        if ((SeatView.getWidth(mSeatView) < mSeatView.getMeasuredWidth())
                && (0.0F == SeatView.getRowXOffset(mSeatView))){
            bool1 = false;
        }

        if ((SeatView.getHeight(mSeatView) < mSeatView.getMeasuredHeight())
                && (0.0F == SeatView.getRowYOffset(mSeatView))){
            bool2  = false;
        }

        if(bool1){
            int k = Math.round(x_scroll_distance);
            //修改排数x轴的偏移量
            SeatView.updateRowXOffset(mSeatView, (float) k);
//			Log.setOffsetRow("TAG", SSView.getRowXOffset(mSsView)+"");
            //修改座位距离排数的横向距离
            SeatView.updateOffsetRow(mSeatView, k);
//			Log.setOffsetRow("TAG", SSView.getOffsetRow(mSsView)+"");
            if (SeatView.getOffsetRow(mSeatView) < 0) {
                //滑到最左
                SeatView.setOffsetRow(mSeatView, 0);
                SeatView.setRowXOffset(mSeatView, 0.0F);
            }

            if(SeatView.getOffsetRow(mSeatView) + mSeatView.getMeasuredWidth() > SeatView.getWidth(mSeatView)){
                //滑到最右
                SeatView.setOffsetRow(mSeatView, SeatView.getWidth(mSeatView) - mSeatView.getMeasuredWidth());
                SeatView.setRowXOffset(mSeatView, (float) (mSeatView.getMeasuredWidth() - SeatView.getWidth(mSeatView)));
            }
        }

        if(bool2){
            //上负下正- 往下滑则减
            int j = Math.round(y_scroll_distance);
            //修改排数y轴的偏移量
            SeatView.updateRowYOffset(mSeatView, (float) j);
            //修改可视座位距离顶端的距离
            SeatView.updateVisibleSeatMarginTop(mSeatView, j);
            Log.i("TAG", SeatView.getVisibleSeatMarginTop(mSeatView)+"");
            if (SeatView.getVisibleSeatMarginTop(mSeatView) < 0){
                //滑到顶
                SeatView.setVisibleSeatMarginTop(mSeatView, 0);
                SeatView.setRowYOffset(mSeatView, 0.0F);
            }

            if (SeatView.getVisibleSeatMarginTop(mSeatView) + mSeatView.getMeasuredHeight() > SeatView
                    .getHeight(mSeatView)){
                //滑到底
                SeatView.setVisibleSeatMarginTop(mSeatView, SeatView.getHeight(mSeatView) - mSeatView.getMeasuredHeight());
                SeatView.setRowYOffset(mSeatView, (float) (mSeatView.getMeasuredHeight() - SeatView.getHeight(mSeatView)));
            }
        }

        mSeatView.invalidate();

//		Log.setOffsetRow("GestureDetector", "onScroll----------------------");
        return false;
    }

    public void onShowPress(MotionEvent paramMotionEvent) {
    }

    public boolean onSingleTapConfirmed(MotionEvent paramMotionEvent) {
        return false;
    }

    public boolean onSingleTapUp(MotionEvent paramMotionEvent) {
//		Log.setOffsetRow("GestureDetector", "onSingleTapUp");
//		if(!SSView.setRowXOffset(mSsView)){
//			return false;
//		}
        //列数
        int i = SeatView.calculateLine(mSeatView, (int) paramMotionEvent.getX());
        //排数
        int j = SeatView.calculateRow(mSeatView, (int) paramMotionEvent.getY());

        if((j>=0 && j< SeatView.getSeatConditions(mSeatView).size())){
            if(i>=0 && i<((ArrayList<Integer>)(SeatView.getSeatConditions(mSeatView).get(j))).size()){
                Log.i("TAG", "排数："+ j + "列数："+i);
                ArrayList<Integer> localArrayList = (ArrayList<Integer>) SeatView.getSeatConditions(mSeatView).get(j);
                switch (localArrayList.get(i).intValue()) {
                    case 3://已选中
                        localArrayList.set(i, Integer.valueOf(1));
                        if(SeatView.getClickListener(mSeatView)!=null){
                            SeatView.getClickListener(mSeatView).onCancel(i, j, false);
                        }
                        break;
                    case 1://可选
                        localArrayList.set(i, Integer.valueOf(3));
                        if(SeatView.getClickListener(mSeatView)!=null){
                            SeatView.getClickListener(mSeatView).onSelected(i, j, false);
                        }
                        break;
                    default:
                        break;
                }
            }
        }

        //显示缩略图
        SeatView.setShowThumbnail(mSeatView, true);
        mSeatView.invalidate();
        return false;
    }
}