package in.entrylog.entrylog.util;

import android.graphics.Bitmap;

/**
 * Created by Admin on 10-Aug-16.
 */
public class Printer {
    private static final String TAG = "Pos";


    private byte[] pixToCmd(byte[] src, int nWidth, int nMode) {
        int[] p0 = {0, 128};
        int[] p1 = {0, 64};
        int[] p2 = {0, 32};
        int[] p3 = {0, 16};
        int[] p4 = {0, 8};
        int[] p5 = {0, 4};
        int[] p6 = {0, 2};

        int nHeight = src.length / nWidth;
        byte[] data = new byte[8 + src.length / 8];
        data[0] = 29;
        data[1] = 118;
        data[2] = 48;
        data[3] = (byte) (nMode & 0x1);
        data[4] = (byte) (nWidth / 8 % 256);
        data[5] = (byte) (nWidth / 8 / 256);
        data[6] = (byte) (nHeight % 256);
        data[7] = (byte) (nHeight / 256);
        int k = 0;
        for (int i = 8; i < data.length; i++) {
            data[i] =
                    (byte) (p0[src[k]] + p1[src[(k + 1)]] + p2[src[(k + 2)]] +
                            p3[src[(k + 3)]] + p4[src[(k + 4)]] + p5[src[(k + 5)]] +
                            p6[src[(k + 6)]] + src[(k + 7)]);
            k += 8;
        }
        return data;
    }


    /**
     * add command to each line of picture
     *
     * @param src
     * @param nWidth
     * @param nMode
     * @return
     */
    private static byte[] eachLinePixToCmd(byte[] src, int nWidth, int nMode) {
        int[] p0 = {0, 128};
        int[] p1 = {0, 64};
        int[] p2 = {0, 32};
        int[] p3 = {0, 16};
        int[] p4 = {0, 8};
        int[] p5 = {0, 4};
        int[] p6 = {0, 2};

        int nHeight = src.length / nWidth;
        int nBytesPerLine = nWidth / 8;
        byte[] data = new byte[nHeight * (8 + nBytesPerLine)];
        int offset = 0;
        int k = 0;
        for (int i = 0; i < nHeight; i++) {
            offset = i * (8 + nBytesPerLine);
            data[(offset + 0)] = 29;
            data[(offset + 1)] = 118;
            data[(offset + 2)] = 48;
            data[(offset + 3)] = (byte) (nMode & 0x1);
            data[(offset + 4)] = (byte) (nBytesPerLine % 256);
            data[(offset + 5)] = (byte) (nBytesPerLine / 256);
            data[(offset + 6)] = 1;
            data[(offset + 7)] = 0;
            for (int j = 0; j < nBytesPerLine; j++) {
                data[(offset + 8 + j)] =
                        (byte) (p0[src[k]] + p1[src[(k + 1)]] +
                                p2[src[(k + 2)]] + p3[src[(k + 3)]] + p4[src[(k + 4)]] +
                                p5[src[(k + 5)]] + p6[src[(k + 6)]] + src[(k + 7)]);
                k += 8;
            }
        }

        return data;
    }

    private static byte[] bitmapToBWPix(Bitmap mBitmap) {
        int[] pixels = new int[mBitmap.getWidth() * mBitmap.getHeight()];
        byte[] data = new byte[mBitmap.getWidth() * mBitmap.getHeight()];

        mBitmap.getPixels(pixels, 0, mBitmap.getWidth(), 0, 0,
                mBitmap.getWidth(), mBitmap.getHeight());

        ImageProcessing.format_K_dither16x16(pixels, mBitmap.getWidth(),
                mBitmap.getHeight(), data);

        return data;
    }

    public static byte[] POS_PrintPicture(Bitmap mBitmap, int nWidth, int nMode) {
        int width = (nWidth + 7) / 8 * 8;
        int height = mBitmap.getHeight() * width / mBitmap.getWidth();
        height = (height + 7) / 8 * 8;
        Bitmap rszBitmap = ImageProcessing.resizeImage(mBitmap, width, height);
        Bitmap grayBitmap = ImageProcessing.toGrayscale(rszBitmap);
        byte[] dithered = bitmapToBWPix(grayBitmap);

        byte[] data = eachLinePixToCmd(dithered, width, nMode);

        return data;
    }

    private static byte[] thresholdToBWPix(Bitmap mBitmap) {
        int[] pixels = new int[mBitmap.getWidth() * mBitmap.getHeight()];
        byte[] data = new byte[mBitmap.getWidth() * mBitmap.getHeight()];

        mBitmap.getPixels(pixels, 0, mBitmap.getWidth(), 0, 0,
                mBitmap.getWidth(), mBitmap.getHeight());

        ImageProcessing.format_K_threshold(pixels, mBitmap.getWidth(),
                mBitmap.getHeight(), data);

        return data;
    }

    public static byte[] POS_PrintBWPic(Bitmap mBitmap, int nWidth, int nMode) {
        int width = (nWidth + 7) / 8 * 8;
        int height = mBitmap.getHeight() * width / mBitmap.getWidth();
        height = (height + 7) / 8 * 8;

        Bitmap rszBitmap = mBitmap;
        if (mBitmap.getWidth() != width) {
            rszBitmap = ImageProcessing.resizeImage(mBitmap, width, height);
        }
        Bitmap grayBitmap = ImageProcessing.toGrayscale(rszBitmap);

        byte[] dithered = thresholdToBWPix(grayBitmap);

        byte[] data = eachLinePixToCmd(dithered, width, nMode);

        return data;
    }
}
