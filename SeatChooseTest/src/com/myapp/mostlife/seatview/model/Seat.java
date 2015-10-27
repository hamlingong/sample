package com.myapp.mostlife.seatview.model;

/**
 * 座位信息
 *
 * @author hamlingong
 */
public class Seat
{
    /**序号，当为走道时 为"Z"*/
    private String number = null;
    /**损坏标签*/
    private String damagedFlg = null;
    /**情侣座*/
    private String loveInd = null;

    /**座位状态：0 普通，1 选中，2 锁定*/
    private int status = -1;

    public void setNumber(String numberString)
    {
        this.number = numberString;
    }

    /**
     * 是否是情侣座
     *
     * @return
     */
    public boolean isLoverSeat()
    {
        return ("1".equals(this.loveInd)) || ("2".equals(this.loveInd));
    }

    public String getNumber()
    {
        return this.number;
    }

    public void setDamagedFlg(String paramString)
    {
        this.damagedFlg = paramString;
    }

    public String getDamagedFlg()
    {
        return this.damagedFlg;
    }

    /**
     * 设置情侣座
     *
     * @param paramString
     */
    public void setLoveInd(String paramString)
    {
        this.loveInd = paramString;
    }

    public String getLoveInd()
    {
        return this.loveInd;
    }
}