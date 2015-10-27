package com.myapp.mostlife.seatview.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 缩略图View
 *
 * @author hamlingong 2015-10-27
 */
public class ThumbnailSeatView extends View
{
  private Bitmap thumbnailViewBitmap = null;
  private Paint paint = null;

  public ThumbnailSeatView(Context context, AttributeSet attributeSet)
  {
    super(context, attributeSet);
  }

 /**
 * 设置缩略图Bitmap
 *
 * @param bitmap
 */
  public void setThumbnailViewBitmap(Bitmap bitmap)
  {
    this.thumbnailViewBitmap = bitmap;
  }

    /**
    * 将缩略图画在view上
    * @param canvas
    */
  protected void onDraw(Canvas canvas)
  {
    super.onDraw(canvas);
    if (this.thumbnailViewBitmap != null)
      canvas.drawBitmap(this.thumbnailViewBitmap, 0.0F, 0.0F, this.paint);
  }
}