package in.entrylog.entrylog.myprinter;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.UnsupportedEncodingException;

/**
 * Created by Admin on 23-Jun-16.
 */
public class Pos {
    private static final String TAG = "Pos";
    public IO IO = new IO();
    private ESCCmd Cmd = new ESCCmd();

    public Pos() {
    }

    public void Set(IO io) {
        this.IO = io;
    }

    private byte[] pixToCmd(byte[] src, int nWidth, int nMode) {
        int[] p0 = new int[]{0, 128};
        int[] p1 = new int[]{0, 64};
        int[] p2 = new int[]{0, 32};
        int[] p3 = new int[]{0, 16};
        int[] p4 = new int[]{0, 8};
        int[] p5 = new int[]{0, 4};
        int[] p6 = new int[]{0, 2};
        int nHeight = src.length / nWidth;
        byte[] data = new byte[8 + src.length / 8];
        data[0] = 29;
        data[1] = 118;
        data[2] = 48;
        data[3] = (byte)(nMode & 1);
        data[4] = (byte)(nWidth / 8 % 256);
        data[5] = (byte)(nWidth / 8 / 256);
        data[6] = (byte)(nHeight % 256);
        data[7] = (byte)(nHeight / 256);
        int k = 0;

        for(int i = 8; i < data.length; ++i) {
            data[i] = (byte)(p0[src[k]] + p1[src[k + 1]] + p2[src[k + 2]] + p3[src[k + 3]] + p4[src[k + 4]] + p5[src[k + 5]] + p6[src[k + 6]] + src[k + 7]);
            k += 8;
        }

        return data;
    }

    private byte[] eachLinePixToCmd(byte[] src, int nWidth, int nMode) {
        int[] p0 = new int[]{0, 128};
        int[] p1 = new int[]{0, 64};
        int[] p2 = new int[]{0, 32};
        int[] p3 = new int[]{0, 16};
        int[] p4 = new int[]{0, 8};
        int[] p5 = new int[]{0, 4};
        int[] p6 = new int[]{0, 2};
        int nHeight = src.length / nWidth;
        int nBytesPerLine = nWidth / 8;
        byte[] data = new byte[nHeight * (8 + nBytesPerLine)];
        boolean offset = false;
        int k = 0;

        for(int i = 0; i < nHeight; ++i) {
            int var18 = i * (8 + nBytesPerLine);
            data[var18 + 0] = 29;
            data[var18 + 1] = 118;
            data[var18 + 2] = 48;
            data[var18 + 3] = (byte)(nMode & 1);
            data[var18 + 4] = (byte)(nBytesPerLine % 256);
            data[var18 + 5] = (byte)(nBytesPerLine / 256);
            data[var18 + 6] = 1;
            data[var18 + 7] = 0;

            for(int j = 0; j < nBytesPerLine; ++j) {
                data[var18 + 8 + j] = (byte)(p0[src[k]] + p1[src[k + 1]] + p2[src[k + 2]] + p3[src[k + 3]] + p4[src[k + 4]] + p5[src[k + 5]] + p6[src[k + 6]] + src[k + 7]);
                k += 8;
            }
        }

        return data;
    }

    private byte[] bitmapToBWPix(Bitmap mBitmap) {
        int[] pixels = new int[mBitmap.getWidth() * mBitmap.getHeight()];
        byte[] data = new byte[mBitmap.getWidth() * mBitmap.getHeight()];
        mBitmap.getPixels(pixels, 0, mBitmap.getWidth(), 0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        ImageProcessing.format_K_dither16x16(pixels, mBitmap.getWidth(), mBitmap.getHeight(), data);
        return data;
    }

    public void POS_PrintPicture(Bitmap mBitmap, int nWidth, int nMode) {
        int width = (nWidth + 7) / 8 * 8;
        int height = /*mBitmap.getHeight() * width / mBitmap.getWidth()*/192;
        /*height = (height + 7) / 8 * 8;*/
        Bitmap rszBitmap = ImageProcessing.resizeImage(mBitmap, width, height);
        Bitmap grayBitmap = ImageProcessing.toGrayscale(rszBitmap);
        byte[] dithered = this.bitmapToBWPix(grayBitmap);
        byte[] data = this.eachLinePixToCmd(dithered, width, nMode);
        this.IO.Write(data, 0, data.length);
    }

    private byte[] thresholdToBWPix(Bitmap mBitmap) {
        int[] pixels = new int[mBitmap.getWidth() * mBitmap.getHeight()];
        byte[] data = new byte[mBitmap.getWidth() * mBitmap.getHeight()];
        mBitmap.getPixels(pixels, 0, mBitmap.getWidth(), 0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        ImageProcessing.format_K_threshold(pixels, mBitmap.getWidth(), mBitmap.getHeight(), data);
        return data;
    }

    public void POS_PrintBWPic(Bitmap mBitmap, int nWidth, int nMode) {
        int width = (nWidth + 7) / 8 * 8;
        int height = /*mBitmap.getHeight() * width / mBitmap.getWidth()*/192;
        //height = 192;
        Bitmap rszBitmap = mBitmap;
        if(mBitmap.getWidth() != width) {
            rszBitmap = ImageProcessing.resizeImage(mBitmap, width, height);
        }

        Bitmap grayBitmap = ImageProcessing.toGrayscale(rszBitmap);
        byte[] dithered = this.thresholdToBWPix(grayBitmap);
        byte[] data = this.eachLinePixToCmd(dithered, width, nMode);
        this.IO.Write(data, 0, data.length);
    }

    public void POS_S_TextOut(String pszString, String encoding, int nOrgx, int nWidthTimes, int nHeightTimes, int nFontType, int nFontStyle) {
        if(!(nOrgx > '\uffff' | nOrgx < 0 | nWidthTimes > 7 | nWidthTimes < 0 | nHeightTimes > 7 | nHeightTimes < 0 | nFontType < 0 | nFontType > 4 | pszString.length() == 0)) {
            this.Cmd.ESC_dollors_nL_nH[2] = (byte)(nOrgx % 256);
            this.Cmd.ESC_dollors_nL_nH[3] = (byte)(nOrgx / 256);
            byte[] intToWidth = new byte[]{(byte)0, (byte)16, (byte)32, (byte)48, (byte)64, (byte)80, (byte)96, (byte)112};
            byte[] intToHeight = new byte[]{(byte)0, (byte)1, (byte)2, (byte)3, (byte)4, (byte)5, (byte)6, (byte)7};
            this.Cmd.GS_exclamationmark_n[2] = (byte)(intToWidth[nWidthTimes] + intToHeight[nHeightTimes]);
            byte[] tmp_ESC_M_n = this.Cmd.ESC_M_n;
            if(nFontType != 0 && nFontType != 1) {
                tmp_ESC_M_n = new byte[0];
            } else {
                tmp_ESC_M_n[2] = (byte)nFontType;
            }

            this.Cmd.GS_E_n[2] = (byte)(nFontStyle >> 3 & 1);
            this.Cmd.ESC_line_n[2] = (byte)(nFontStyle >> 7 & 3);
            this.Cmd.FS_line_n[2] = (byte)(nFontStyle >> 7 & 3);
            this.Cmd.ESC_lbracket_n[2] = (byte)(nFontStyle >> 9 & 1);
            this.Cmd.GS_B_n[2] = (byte)(nFontStyle >> 10 & 1);
            this.Cmd.ESC_V_n[2] = (byte)(nFontStyle >> 12 & 1);
            Object pbString = null;

            byte[] pbString1;
            try {
                pbString1 = pszString.getBytes(encoding);
            } catch (UnsupportedEncodingException var13) {
                return;
            }

            byte[] data = DataUtils.byteArraysToBytes(new byte[][]{this.Cmd.ESC_dollors_nL_nH, this.Cmd.GS_exclamationmark_n, tmp_ESC_M_n, this.Cmd.GS_E_n, this.Cmd.ESC_line_n, this.Cmd.FS_line_n, this.Cmd.ESC_lbracket_n, this.Cmd.GS_B_n, this.Cmd.ESC_V_n, pbString1});
            this.IO.Write(data, 0, data.length);
        }
    }

    public void POS_FeedLine() {
        byte[] data = DataUtils.byteArraysToBytes(new byte[][]{this.Cmd.CR, this.Cmd.LF});
        this.IO.Write(data, 0, data.length);
    }

    public void POS_S_Align(int align) {
        if(align >= 0 && align <= 2) {
            byte[] data = this.Cmd.ESC_a_n;
            data[2] = (byte)align;
            this.IO.Write(data, 0, data.length);
        }
    }

    public void POS_SetLineHeight(int nHeight) {
        if(nHeight >= 0 && nHeight <= 255) {
            byte[] data = this.Cmd.ESC_3_n;
            data[2] = (byte)nHeight;
            this.IO.Write(data, 0, data.length);
        }
    }

    public void POS_S_SetBarcode(String strCodedata, int nOrgx, int nType, int nWidthX, int nHeight, int nHriFontType, int nHriFontPosition) {
        if(!(nOrgx < 0 | nOrgx > '\uffff' | nType < 65 | nType > 73 | nWidthX < 2 | nWidthX > 6 | nHeight < 1 | nHeight > 255)) {
            Object bCodeData = null;

            byte[] bCodeData1;
            try {
                bCodeData1 = strCodedata.getBytes("GBK");
            } catch (UnsupportedEncodingException var10) {
                return;
            }

            this.Cmd.ESC_dollors_nL_nH[2] = (byte)(nOrgx % 256);
            this.Cmd.ESC_dollors_nL_nH[3] = (byte)(nOrgx / 256);
            this.Cmd.GS_w_n[2] = (byte)nWidthX;
            this.Cmd.GS_h_n[2] = (byte)nHeight;
            this.Cmd.GS_f_n[2] = (byte)(nHriFontType & 1);
            this.Cmd.GS_H_n[2] = (byte)(nHriFontPosition & 3);
            this.Cmd.GS_k_m_n_[2] = (byte)nType;
            this.Cmd.GS_k_m_n_[3] = (byte)bCodeData1.length;
            byte[] data = DataUtils.byteArraysToBytes(new byte[][]{this.Cmd.ESC_dollors_nL_nH, this.Cmd.GS_w_n, this.Cmd.GS_h_n, this.Cmd.GS_f_n, this.Cmd.GS_H_n, this.Cmd.GS_k_m_n_, bCodeData1});
            this.IO.Write(data, 0, data.length);
        }
    }

    public void POS_S_SetQRcode(String strCodedata, int nWidthX, int nVersion, int nErrorCorrectionLevel) {
        if(!(nWidthX < 2 | nWidthX > 6 | nErrorCorrectionLevel < 1 | nErrorCorrectionLevel > 4)) {
            if(nVersion >= 0 && nVersion <= 16) {
                Object bCodeData = null;

                byte[] bCodeData1;
                try {
                    bCodeData1 = strCodedata.getBytes("GBK");
                } catch (UnsupportedEncodingException var7) {
                    return;
                }

                this.Cmd.GS_w_n[2] = (byte)nWidthX;
                this.Cmd.GS_k_m_v_r_nL_nH[3] = (byte)nVersion;
                this.Cmd.GS_k_m_v_r_nL_nH[4] = (byte)nErrorCorrectionLevel;
                this.Cmd.GS_k_m_v_r_nL_nH[5] = (byte)(bCodeData1.length & 255);
                this.Cmd.GS_k_m_v_r_nL_nH[6] = (byte)((bCodeData1.length & '\uff00') >> 8);
                byte[] data = DataUtils.byteArraysToBytes(new byte[][]{this.Cmd.GS_w_n, this.Cmd.GS_k_m_v_r_nL_nH, bCodeData1});
                this.IO.Write(data, 0, data.length);
            }
        }
    }

    public void POS_EPSON_SetQRCode(String strCodedata, int nWidthX, int nVersion, int nErrorCorrectionLevel) {
        if(!(nWidthX < 2 | nWidthX > 6 | nErrorCorrectionLevel < 1 | nErrorCorrectionLevel > 4)) {
            if(nVersion >= 0 && nVersion <= 16) {
                Object bCodeData = null;

                byte[] bCodeData1;
                try {
                    bCodeData1 = strCodedata.getBytes("GBK");
                } catch (UnsupportedEncodingException var7) {
                    return;
                }

                this.Cmd.GS_w_n[2] = (byte)nWidthX;
                this.Cmd.GS_leftbracket_k_pL_pH_cn_67_n[7] = (byte)nVersion;
                this.Cmd.GS_leftbracket_k_pL_pH_cn_69_n[7] = (byte)(47 + nErrorCorrectionLevel);
                this.Cmd.GS_leftbracket_k_pL_pH_cn_80_m__d1dk[3] = (byte)(bCodeData1.length + 3 & 255);
                this.Cmd.GS_leftbracket_k_pL_pH_cn_80_m__d1dk[4] = (byte)((bCodeData1.length + 3 & '\uff00') >> 8);
                byte[] data = DataUtils.byteArraysToBytes(new byte[][]{this.Cmd.GS_w_n, this.Cmd.GS_leftbracket_k_pL_pH_cn_67_n, this.Cmd.GS_leftbracket_k_pL_pH_cn_69_n, this.Cmd.GS_leftbracket_k_pL_pH_cn_80_m__d1dk, bCodeData1, this.Cmd.GS_leftbracket_k_pL_pH_cn_fn_m});
                this.IO.Write(data, 0, data.length);
            }
        }
    }

    public void POS_SetKey(byte[] key) {
        byte[] data = this.Cmd.DES_SETKEY;

        for(int i = 0; i < key.length; ++i) {
            data[i + 5] = key[i];
        }

        this.IO.Write(data, 0, data.length);
    }

    public boolean POS_CheckKey(byte[] key) {
        byte[] random = DataUtils.getRandomByteArray(8);
        boolean result = false;
        boolean HeaderSize = true;
        byte[] recHeader = new byte[5];
        Object recData = null;
        boolean rec = false;
        boolean recDataLen = false;
        byte[] randomlen = new byte[]{(byte)(random.length & 255), (byte)(random.length >> 8 & 255)};
        byte[] data = DataUtils.byteArraysToBytes(new byte[][]{this.Cmd.DES_ENCRYPT, randomlen, random});
        this.IO.Write(data, 0, data.length);
        int rec1 = this.IO.Read(recHeader, 0, 5, 1000);
        if(rec1 != 5) {
            return false;
        } else {
            int recDataLen1 = (recHeader[3] & 255) + (recHeader[4] << 8 & 255);
            byte[] recData1 = new byte[recDataLen1];
            rec1 = this.IO.Read(recData1, 0, recDataLen1, 1000);
            if(rec1 != recDataLen1) {
                return false;
            } else {
                byte[] decrypted = new byte[recData1.length + 1];
                DES2 des2 = new DES2();
                des2.yxyDES2_InitializeKey(key);
                des2.yxyDES2_DecryptAnyLength(recData1, decrypted, recData1.length);
                result = DataUtils.bytesEquals(random, 0, decrypted, 0, random.length);
                if(!result) {
                    Log.v("Pos random", DataUtils.bytesToStr(random));
                    Log.v("Pos decryp", DataUtils.bytesToStr(decrypted));
                }

                return result;
            }
        }
    }

    public void POS_Reset() {
        byte[] data = this.Cmd.ESC_ALT;
        this.IO.Write(data, 0, data.length);
    }

    public void POS_SetMotionUnit(int nHorizontalMU, int nVerticalMU) {
        if(nHorizontalMU >= 0 && nHorizontalMU <= 255 && nVerticalMU >= 0 && nVerticalMU <= 255) {
            byte[] data = this.Cmd.GS_P_x_y;
            data[2] = (byte)nHorizontalMU;
            data[3] = (byte)nVerticalMU;
            this.IO.Write(data, 0, data.length);
        }
    }

    public void POS_SetCharSetAndCodePage(int nCharSet, int nCodePage) {
        if(!(nCharSet < 0 | nCharSet > 15 | nCodePage < 0 | nCodePage > 19 | nCodePage > 10 & nCodePage < 16)) {
            this.Cmd.ESC_R_n[2] = (byte)nCharSet;
            this.Cmd.ESC_t_n[2] = (byte)nCodePage;
            byte[] data = DataUtils.byteArraysToBytes(new byte[][]{this.Cmd.ESC_R_n, this.Cmd.ESC_t_n});
            this.IO.Write(data, 0, data.length);
        }
    }

    public void POS_SetRightSpacing(int nDistance) {
        if(!(nDistance < 0 | nDistance > 255)) {
            this.Cmd.ESC_SP_n[2] = (byte)nDistance;
            byte[] data = this.Cmd.ESC_SP_n;
            this.IO.Write(data, 0, data.length);
        }
    }

    public void POS_S_SetAreaWidth(int nWidth) {
        if(!(nWidth < 0 | nWidth > '\uffff')) {
            byte nL = (byte)(nWidth % 256);
            byte nH = (byte)(nWidth / 256);
            this.Cmd.GS_W_nL_nH[2] = nL;
            this.Cmd.GS_W_nL_nH[3] = nH;
            byte[] data = this.Cmd.GS_W_nL_nH;
            this.IO.Write(data, 0, data.length);
        }
    }

    public boolean POS_QueryStatus(byte[] precbuf, int timeout) {
        byte[] pcmdbuf = new byte[]{(byte)29, (byte)114, (byte)1};
        byte[] tmp = new byte[1024];
        this.IO.Read(tmp, 0, tmp.length, 10);
        int retry = 3;

        while(retry > 0) {
            --retry;
            this.IO.Write(pcmdbuf, 0, pcmdbuf.length);
            if(this.IO.Read(precbuf, 0, 1, timeout) == 1) {
                return true;
            }
        }

        return false;
    }

    public boolean POS_RTQueryStatus(byte[] precbuf, int timeout) {
        byte[] pcmdbuf = new byte[]{(byte)16, (byte)4, (byte)1};
        byte[] tmp = new byte[1024];
        this.IO.Read(tmp, 0, tmp.length, 10);
        int retry = 3;

        while(retry > 0) {
            --retry;
            this.IO.Write(pcmdbuf, 0, pcmdbuf.length);
            if(this.IO.Read(precbuf, 0, 1, timeout) == 1) {
                return true;
            }
        }

        return false;
    }

    public boolean POS_QueryOnline(int timeout) {
        byte[] pcmdbuf = new byte[]{(byte)29, (byte)114, (byte)1, (byte)16, (byte)4, (byte)1};
        byte[] tmp = new byte[256];
        this.IO.Read(tmp, 0, tmp.length, 10);
        int retry = 3;

        while(retry > 0) {
            --retry;
            this.IO.Write(pcmdbuf, 0, pcmdbuf.length);
            if(this.IO.Read(tmp, 0, 1, timeout) == 1) {
                return true;
            }
        }

        return false;
    }

    public boolean EMBEDDED_WriteToUart(byte[] buffer, int offset, int count) {
        byte[] header = new byte[]{(byte)31, (byte)82, (byte)(count >> 8 & 255), (byte)(count & 255)};
        byte[] data = new byte[header.length + count];
        System.arraycopy(header, 0, data, 0, header.length);
        System.arraycopy(buffer, offset, data, header.length, count);
        return this.IO.Write(data, 0, data.length) == data.length;
    }
}
