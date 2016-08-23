package in.entrylog.entrylog.myprinter;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;

public class ImageProcessing {
    private static int[][] Floyd16x16 = new int[][]{{0, 128, 32, 160, 8, 136, 40, 168, 2, 130, 34, 162, 10, 138, 42, 170}, {192, 64, 224, 96, 200, 72, 232, 104, 194, 66, 226, 98, 202, 74, 234, 106}, {48, 176, 16, 144, 56, 184, 24, 152, 50, 178, 18, 146, 58, 186, 26, 154}, {240, 112, 208, 80, 248, 120, 216, 88, 242, 114, 210, 82, 250, 122, 218, 90}, {12, 140, 44, 172, 4, 132, 36, 164, 14, 142, 46, 174, 6, 134, 38, 166}, {204, 76, 236, 108, 196, 68, 228, 100, 206, 78, 238, 110, 198, 70, 230, 102}, {60, 188, 28, 156, 52, 180, 20, 148, 62, 190, 30, 158, 54, 182, 22, 150}, {252, 124, 220, 92, 244, 116, 212, 84, 254, 126, 222, 94, 246, 118, 214, 86}, {3, 131, 35, 163, 11, 139, 43, 171, 1, 129, 33, 161, 9, 137, 41, 169}, {195, 67, 227, 99, 203, 75, 235, 107, 193, 65, 225, 97, 201, 73, 233, 105}, {51, 179, 19, 147, 59, 187, 27, 155, 49, 177, 17, 145, 57, 185, 25, 153}, {243, 115, 211, 83, 251, 123, 219, 91, 241, 113, 209, 81, 249, 121, 217, 89}, {15, 143, 47, 175, 7, 135, 39, 167, 13, 141, 45, 173, 5, 133, 37, 165}, {207, 79, 239, 111, 199, 71, 231, 103, 205, 77, 237, 109, 197, 69, 229, 101}, {63, 191, 31, 159, 55, 183, 23, 151, 61, 189, 29, 157, 53, 181, 21, 149}, {254, 127, 223, 95, 247, 119, 215, 87, 253, 125, 221, 93, 245, 117, 213, 85}};
    private static int[][] Floyd8x8 = new int[][]{{0, 32, 8, 40, 2, 34, 10, 42}, {48, 16, 56, 24, 50, 18, 58, 26}, {12, 44, 4, 36, 14, 46, 6, 38}, {60, 28, 52, 20, 62, 30, 54, 22}, {3, 35, 11, 43, 1, 33, 9, 41}, {51, 19, 59, 27, 49, 17, 57, 25}, {15, 47, 7, 39, 13, 45, 5, 37}, {63, 31, 55, 23, 61, 29, 53, 21}};
    private static int[][] Floyd4x4 = new int[][]{{0, 8, 2, 10}, {12, 4, 14, 6}, {3, 11, 1, 9}, {15, 7, 13, 5}};

    ImageProcessing() {
    }

    public static Bitmap toGrayscale(Bitmap bmpOriginal) {
        int height = bmpOriginal.getHeight();
        int width = bmpOriginal.getWidth();
        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0.0F);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0.0F, 0.0F, paint);
        return bmpGrayscale;
    }

    public static Bitmap resizeImage(Bitmap bitmap, int w, int h) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = (float)w / (float)width;
        float scaleHeight = (float)h / (float)height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return resizedBitmap;
    }

    public static void format_K_dither16x16(int[] orgpixels, int xsize, int ysize, byte[] despixels) {
        int k = 0;

        for(int y = 0; y < ysize; ++y) {
            for(int x = 0; x < xsize; ++x) {
                if((orgpixels[k] & 255) > Floyd16x16[x & 15][y & 15]) {
                    despixels[k] = 0;
                } else {
                    despixels[k] = 1;
                }

                ++k;
            }
        }

    }

    public static void format_K_dither8x8(int[] orgpixels, int xsize, int ysize, byte[] despixels) {
        int k = 0;

        for(int y = 0; y < ysize; ++y) {
            for(int x = 0; x < xsize; ++x) {
                if((orgpixels[k] & 255) >> 2 > Floyd8x8[x & 7][y & 7]) {
                    despixels[k] = 0;
                } else {
                    despixels[k] = 1;
                }

                ++k;
            }
        }

    }

    public static void format_K_threshold(int[] orgpixels, int xsize, int ysize, byte[] despixels) {
        int graytotal = 0;
        boolean grayave = true;
        int k = 0;

        int i;
        int j;
        int gray;
        for(i = 0; i < ysize; ++i) {
            for(j = 0; j < xsize; ++j) {
                gray = orgpixels[k] & 255;
                graytotal += gray;
                ++k;
            }
        }

        int var10 = graytotal / ysize / xsize;
        k = 0;

        for(i = 0; i < ysize; ++i) {
            for(j = 0; j < xsize; ++j) {
                gray = orgpixels[k] & 255;
                if(gray > var10) {
                    despixels[k] = 0;
                } else {
                    despixels[k] = 1;
                }

                ++k;
            }
        }

    }

    public static void format_K_threshold(Bitmap mBitmap) {
        int graytotal = 0;
        boolean grayave = true;
        int graycnt = 1;
        int ysize = mBitmap.getHeight();
        int xsize = mBitmap.getWidth();

        int gray;
        int i;
        int j;
        for(i = 0; i < ysize; ++i) {
            for(j = 0; j < xsize; ++j) {
                gray = mBitmap.getPixel(j, i) & 255;
                if(gray != 0 && gray != 255) {
                    graytotal += gray;
                    ++graycnt;
                }
            }
        }

        int var9 = graytotal / graycnt;

        for(i = 0; i < ysize; ++i) {
            for(j = 0; j < xsize; ++j) {
                gray = mBitmap.getPixel(j, i) & 255;
                if(gray > var9) {
                    mBitmap.setPixel(j, i, -1);
                } else {
                    mBitmap.setPixel(j, i, -16777216);
                }
            }
        }

    }

    public static Bitmap alignBitmap(Bitmap bitmap, int wbits, int hbits, int color) {
        if(bitmap.getWidth() % wbits == 0 && bitmap.getHeight() % hbits == 0) {
            return bitmap;
        } else {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
            int newwidth = (width + wbits - 1) / wbits * wbits;
            int newheight = (height + hbits - 1) / hbits * hbits;
            int[] newpixels = new int[newwidth * newheight];
            Bitmap newbitmap = Bitmap.createBitmap(newwidth, newheight, Config.ARGB_8888);

            for(int i = 0; i < newheight; ++i) {
                for(int j = 0; j < newwidth; ++j) {
                    if(i < height && j < width) {
                        newpixels[i * newwidth + j] = pixels[i * width + j];
                    } else {
                        newpixels[i * newwidth + j] = color;
                    }
                }
            }

            newbitmap.setPixels(newpixels, 0, newwidth, 0, 0, newwidth, newheight);
            return newbitmap;
        }
    }

    public static Bitmap adjustPhotoRotation(Bitmap bm, int orientationDegree) {
        Matrix m = new Matrix();
        m.setRotate((float)orientationDegree, (float)bm.getWidth() / 2.0F, (float)bm.getHeight() / 2.0F);

        try {
            Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
            return bm1;
        } catch (OutOfMemoryError var4) {
            return null;
        }
    }
}

