package com.myapp.mostlife.seatview;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.myapp.mostlife.seatview.model.Seat;
import com.myapp.mostlife.seatview.model.SeatInfo;
import com.myapp.mostlife.seatview.view.OnSeatClickListener;
import com.myapp.mostlife.seatview.view.ThumbnailSeatView;
import com.myapp.mostlife.seatview.view.SeatView;

import com.myapp.mostlife.R;

public class MainActivity extends Activity {
    private static final int ROW = 20;
    private static final int EACH_ROW_COUNT =30;
    private SeatView mSeatView;
    private ThumbnailSeatView mThumbnailSeatView;
    private ArrayList<SeatInfo> seatInfos = new ArrayList<SeatInfo>();
    private ArrayList<ArrayList<Integer>> seatConditions = new ArrayList<ArrayList<Integer>>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }


    private void init(){
        mSeatView = (SeatView)this.findViewById(R.id.mSSView);
        mThumbnailSeatView = (ThumbnailSeatView)this.findViewById(R.id.ss_ssthumview);
//		mSSView.setXOffset(20);
        setSeatInfo();
        mSeatView.init(EACH_ROW_COUNT, ROW, seatInfos, seatConditions, mThumbnailSeatView, 5);
        mSeatView.setOnSeatClickListener(new OnSeatClickListener() {

            @Override
            public boolean onSelected(int column_num, int row_num, boolean paramBoolean) {
                String desc =  "您选择了第"+(row_num+1)+"排" + " 第" + (column_num+1) +"列";
                Toast.makeText(MainActivity.this,desc.toString(), Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onCancel(int column_num, int row_num, boolean paramBoolean) {
                String desc =  "您取消了第"+(row_num+1)+"排" + " 第" + (column_num+1) +"列";
                Toast.makeText(MainActivity.this,desc.toString(), Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public void doAction() {
                // TODO Auto-generated method stub

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * 初始化座位信息
     */
    private void setSeatInfo(){
        for(int i =0;i<ROW;i++){//8行
            SeatInfo mSeatInfo = new SeatInfo();
            ArrayList<Seat> mSeatList = new ArrayList<Seat>();
            ArrayList<Integer> mConditionList = new ArrayList<Integer>();
            for(int j=0;j<EACH_ROW_COUNT;j++){//每排20个座位
                Seat mSeat = new Seat();
                if(j<3){
                    mSeat.setNumber("Z");
                    mConditionList.add(1);
                }else{
                    mSeat.setNumber(String.valueOf(j - 2));
                    if(j>10){
                        mConditionList.add(2);
                    }else{
                        mConditionList.add(1);
                    }
                }
                mSeat.setDamagedFlg("");
                mSeat.setLoveInd("0");
                mSeatList.add(mSeat);
            }
            mSeatInfo.setDesc(String.valueOf(i+1));
            mSeatInfo.setRow(String.valueOf(i+1));
            mSeatInfo.setSeatList(mSeatList);
            seatInfos.add(mSeatInfo);
            seatConditions.add(mConditionList);
        }
    }
}
