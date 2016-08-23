package in.entrylog.entrylog.util;

/**
 * Created by Chetan G on 7/7/2016.
 */
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;

public class PrintPic {
    private byte[] bitbuf = null;
    private Bitmap bm = null;
    private Canvas canvas = null;
    private float length = 0.0F;
    private Paint paint = null;
    private int width = 256;

    public void drawImage(float paramFloat1, float paramFloat2,
                          String paramString) {
        try {
            Bitmap bm1 = BitmapFactory.decodeFile(paramString);
            this.canvas.drawBitmap(bm1, paramFloat1, paramFloat2, null);
            //if (this.length < bm1.getHeight() + paramFloat2)
            this.length = (bm1.getHeight() + paramFloat2);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void drawImage(float paramFloat1, float paramFloat2,Bitmap bitmap) {
        try {
            this.canvas.drawBitmap(bitmap, paramFloat1, paramFloat2, null);
            //if (this.length < bitmap.getHeight() + paramFloat2)
            this.length = (bitmap.getHeight() + paramFloat2);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int getLength() {
        return (int) this.length;
    }

    public int getWidth() {
        return this.width;
    }

    public void initCanvas(int paramIntx,int paramInty) {
        this.bm = Bitmap.createBitmap(paramIntx, paramInty,
                Bitmap.Config.ARGB_4444);
        this.canvas = new Canvas(this.bm);
        this.canvas.drawColor(-1);
        this.width = paramIntx;
        this.bitbuf = new byte[this.width / 8];
    }

    public void initPaint() {
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setColor(-16777216);
        this.paint.setStyle(Paint.Style.STROKE);
    }

    public byte[] printDraw() {
        byte[] var2;
        if(this.getLength() == 0) {
            var2 = null;
        } else {
            Bitmap tmpBitmap = Bitmap.createBitmap(this.bm, 0, 0, this.width, this.getLength());
            var2 = new byte[this.width / 8 * this.getLength()];
            int var3 = 0;

            byte var6;
            byte var7;
            byte var8;
            byte var9;
            byte var10;
            byte var11;
            byte var12;
            byte var13;

            for(int var4 = 0; var4 < this.getLength(); var4++) {
                for(int var5 = 0; var5 < this.width / 8; var5++) {
                    if(tmpBitmap.getPixel(0 + var5 * 8, var4) == -1) {
                        var6 = 0;
                    } else {
                        var6 = 1;
                    }
                    if(tmpBitmap.getPixel(1 + var5 * 8, var4) == -1) {
                        var7 = 0;
                    } else {
                        var7 = 1;
                    }
                    if(tmpBitmap.getPixel(2 + var5 * 8, var4) == -1) {
                        var8 = 0;
                    } else {
                        var8 = 1;
                    }
                    if(tmpBitmap.getPixel(3 + var5 * 8, var4) == -1) {
                        var9 = 0;
                    } else {
                        var9 = 1;
                    }
                    if(tmpBitmap.getPixel(4 + var5 * 8, var4) == -1) {
                        var10 = 0;
                    } else {
                        var10 = 1;
                    }
                    if(tmpBitmap.getPixel(5 + var5 * 8, var4) == -1) {
                        var11 = 0;
                    } else {
                        var11 = 1;
                    }
                    if(tmpBitmap.getPixel(6 + var5 * 8, var4) == -1) {
                        var12 = 0;
                    } else {
                        var12 = 1;
                    }
                    if(tmpBitmap.getPixel(7 + var5 * 8, var4) == -1) {
                        var13 = 0;
                    } else {
                        var13 = 1;
                    }
                    int var14 = var13 + var6 * 128 + var7 * 64 + var8 * 32 + var9 * 16 + var10 * 8 + var11 * 4 + var12 * 2;
                    this.bitbuf[var5] = (byte)var14;
                }

                int var15 = 0;
                int var16 = this.width / 8;
                while(true) {
                    if(var15 >= var16) {
                        break;
                    }
                    var2[var3] = this.bitbuf[var15];
                    var3++;
                    var15++;
                }
            }
        }

        return var2;
    }

}
