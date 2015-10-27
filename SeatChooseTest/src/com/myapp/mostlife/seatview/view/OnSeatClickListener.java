package com.myapp.mostlife.seatview.view;

/**
 * 座位选择监听类
 *
 * @author hamlingong
 */
public abstract interface OnSeatClickListener
{
  public abstract void doAction();

    /**
     * 选择事件
     *
     * @param columnNum
     * @param rowNum
     * @param paramBoolean
     * @return
     */
  public abstract boolean onSelected(int columnNum, int rowNum, boolean paramBoolean);

    /**
     * 取消事件
     *
     * @param columnNum
     * @param rowNum
     * @param paramBoolean
     * @return
     */
  public abstract boolean onCancel(int columnNum, int rowNum, boolean paramBoolean);
}