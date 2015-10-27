package com.myapp.mostlife.seatview.model;

import java.util.ArrayList;

/**
 * 每一排的座位信息
 *
 * @author hamlignong
 *
 */
public class SeatInfo
{
    private String row = null;
    private String desc = null;
    private ArrayList<Seat> mSeatList = null;

    private String checkAvailable(String paramString)
    {
        if (paramString == null)
            paramString = "";
        return paramString;
    }

    public Seat getSeat(int position)
    {
        if ((position > this.mSeatList.size()) || (position < 0))
            return new Seat();
        return (Seat)this.mSeatList.get(position);
    }

    public String getDesc()
    {
        return checkAvailable(this.desc);
    }

    public void addSeat(Seat seat)
    {
        this.mSeatList.add(seat);
    }

    public void setRow(String strRow)
    {
        this.row = strRow;
    }

    public ArrayList getSeatList()
    {
        return this.mSeatList;
    }

    public void setSeatList(ArrayList<Seat> seatList){
        this.mSeatList = seatList;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }
}