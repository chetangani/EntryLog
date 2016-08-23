package in.entrylog.entrylog.myprinter;

/**
 * Created by Admin on 23-Jun-16.
 */
public class DES2 {
    static final byte[] PC1_Table = new byte[]{(byte)57, (byte)49, (byte)41, (byte)33, (byte)25, (byte)17, (byte)9, (byte)1, (byte)58, (byte)50, (byte)42, (byte)34, (byte)26, (byte)18, (byte)10, (byte)2, (byte)59, (byte)51, (byte)43, (byte)35, (byte)27, (byte)19, (byte)11, (byte)3, (byte)60, (byte)52, (byte)44, (byte)36, (byte)63, (byte)55, (byte)47, (byte)39, (byte)31, (byte)23, (byte)15, (byte)7, (byte)62, (byte)54, (byte)46, (byte)38, (byte)30, (byte)22, (byte)14, (byte)6, (byte)61, (byte)53, (byte)45, (byte)37, (byte)29, (byte)21, (byte)13, (byte)5, (byte)28, (byte)20, (byte)12, (byte)4};
    static final byte[] PC2_Table = new byte[]{(byte)14, (byte)17, (byte)11, (byte)24, (byte)1, (byte)5, (byte)3, (byte)28, (byte)15, (byte)6, (byte)21, (byte)10, (byte)23, (byte)19, (byte)12, (byte)4, (byte)26, (byte)8, (byte)16, (byte)7, (byte)27, (byte)20, (byte)13, (byte)2, (byte)41, (byte)52, (byte)31, (byte)37, (byte)47, (byte)55, (byte)30, (byte)40, (byte)51, (byte)45, (byte)33, (byte)48, (byte)44, (byte)49, (byte)39, (byte)56, (byte)34, (byte)53, (byte)46, (byte)42, (byte)50, (byte)36, (byte)29, (byte)32};
    static final byte[] Shift_Table = new byte[]{(byte)1, (byte)1, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)1, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)2, (byte)1};
    static final byte[] IP_Table = new byte[]{(byte)58, (byte)50, (byte)42, (byte)34, (byte)26, (byte)18, (byte)10, (byte)2, (byte)60, (byte)52, (byte)44, (byte)36, (byte)28, (byte)20, (byte)12, (byte)4, (byte)62, (byte)54, (byte)46, (byte)38, (byte)30, (byte)22, (byte)14, (byte)6, (byte)64, (byte)56, (byte)48, (byte)40, (byte)32, (byte)24, (byte)16, (byte)8, (byte)57, (byte)49, (byte)41, (byte)33, (byte)25, (byte)17, (byte)9, (byte)1, (byte)59, (byte)51, (byte)43, (byte)35, (byte)27, (byte)19, (byte)11, (byte)3, (byte)61, (byte)53, (byte)45, (byte)37, (byte)29, (byte)21, (byte)13, (byte)5, (byte)63, (byte)55, (byte)47, (byte)39, (byte)31, (byte)23, (byte)15, (byte)7};
    static final byte[] E_Table = new byte[]{(byte)32, (byte)1, (byte)2, (byte)3, (byte)4, (byte)5, (byte)4, (byte)5, (byte)6, (byte)7, (byte)8, (byte)9, (byte)8, (byte)9, (byte)10, (byte)11, (byte)12, (byte)13, (byte)12, (byte)13, (byte)14, (byte)15, (byte)16, (byte)17, (byte)16, (byte)17, (byte)18, (byte)19, (byte)20, (byte)21, (byte)20, (byte)21, (byte)22, (byte)23, (byte)24, (byte)25, (byte)24, (byte)25, (byte)26, (byte)27, (byte)28, (byte)29, (byte)28, (byte)29, (byte)30, (byte)31, (byte)32, (byte)1};
    static final byte[][][] S_Box = new byte[][][]{{{(byte)14, (byte)4, (byte)13, (byte)1, (byte)2, (byte)15, (byte)11, (byte)8, (byte)3, (byte)10, (byte)6, (byte)12, (byte)5, (byte)9, (byte)0, (byte)7}, {(byte)0, (byte)15, (byte)7, (byte)4, (byte)14, (byte)2, (byte)13, (byte)1, (byte)10, (byte)6, (byte)12, (byte)11, (byte)9, (byte)5, (byte)3, (byte)8}, {(byte)4, (byte)1, (byte)14, (byte)8, (byte)13, (byte)6, (byte)2, (byte)11, (byte)15, (byte)12, (byte)9, (byte)7, (byte)3, (byte)10, (byte)5, (byte)0}, {(byte)15, (byte)12, (byte)8, (byte)2, (byte)4, (byte)9, (byte)1, (byte)7, (byte)5, (byte)11, (byte)3, (byte)14, (byte)10, (byte)0, (byte)6, (byte)13}}, {{(byte)15, (byte)1, (byte)8, (byte)14, (byte)6, (byte)11, (byte)3, (byte)4, (byte)9, (byte)7, (byte)2, (byte)13, (byte)12, (byte)0, (byte)5, (byte)10}, {(byte)3, (byte)13, (byte)4, (byte)7, (byte)15, (byte)2, (byte)8, (byte)14, (byte)12, (byte)0, (byte)1, (byte)10, (byte)6, (byte)9, (byte)11, (byte)5}, {(byte)0, (byte)14, (byte)7, (byte)11, (byte)10, (byte)4, (byte)13, (byte)1, (byte)5, (byte)8, (byte)12, (byte)6, (byte)9, (byte)3, (byte)2, (byte)15}, {(byte)13, (byte)8, (byte)10, (byte)1, (byte)3, (byte)15, (byte)4, (byte)2, (byte)11, (byte)6, (byte)7, (byte)12, (byte)0, (byte)5, (byte)14, (byte)9}}, {{(byte)10, (byte)0, (byte)9, (byte)14, (byte)6, (byte)3, (byte)15, (byte)5, (byte)1, (byte)13, (byte)12, (byte)7, (byte)11, (byte)4, (byte)2, (byte)8}, {(byte)13, (byte)7, (byte)0, (byte)9, (byte)3, (byte)4, (byte)6, (byte)10, (byte)2, (byte)8, (byte)5, (byte)14, (byte)12, (byte)11, (byte)15, (byte)1}, {(byte)13, (byte)6, (byte)4, (byte)9, (byte)8, (byte)15, (byte)3, (byte)0, (byte)11, (byte)1, (byte)2, (byte)12, (byte)5, (byte)10, (byte)14, (byte)7}, {(byte)1, (byte)10, (byte)13, (byte)0, (byte)6, (byte)9, (byte)8, (byte)7, (byte)4, (byte)15, (byte)14, (byte)3, (byte)11, (byte)5, (byte)2, (byte)12}}, {{(byte)7, (byte)13, (byte)14, (byte)3, (byte)0, (byte)6, (byte)9, (byte)10, (byte)1, (byte)2, (byte)8, (byte)5, (byte)11, (byte)12, (byte)4, (byte)15}, {(byte)13, (byte)8, (byte)11, (byte)5, (byte)6, (byte)15, (byte)0, (byte)3, (byte)4, (byte)7, (byte)2, (byte)12, (byte)1, (byte)10, (byte)14, (byte)9}, {(byte)10, (byte)6, (byte)9, (byte)0, (byte)12, (byte)11, (byte)7, (byte)13, (byte)15, (byte)1, (byte)3, (byte)14, (byte)5, (byte)2, (byte)8, (byte)4}, {(byte)3, (byte)15, (byte)0, (byte)6, (byte)10, (byte)1, (byte)13, (byte)8, (byte)9, (byte)4, (byte)5, (byte)11, (byte)12, (byte)7, (byte)2, (byte)14}}, {{(byte)2, (byte)12, (byte)4, (byte)1, (byte)7, (byte)10, (byte)11, (byte)6, (byte)8, (byte)5, (byte)3, (byte)15, (byte)13, (byte)0, (byte)14, (byte)9}, {(byte)14, (byte)11, (byte)2, (byte)12, (byte)4, (byte)7, (byte)13, (byte)1, (byte)5, (byte)0, (byte)15, (byte)10, (byte)3, (byte)9, (byte)8, (byte)6}, {(byte)4, (byte)2, (byte)1, (byte)11, (byte)10, (byte)13, (byte)7, (byte)8, (byte)15, (byte)9, (byte)12, (byte)5, (byte)6, (byte)3, (byte)0, (byte)14}, {(byte)11, (byte)8, (byte)12, (byte)7, (byte)1, (byte)14, (byte)2, (byte)13, (byte)6, (byte)15, (byte)0, (byte)9, (byte)10, (byte)4, (byte)5, (byte)3}}, {{(byte)12, (byte)1, (byte)10, (byte)15, (byte)9, (byte)2, (byte)6, (byte)8, (byte)0, (byte)13, (byte)3, (byte)4, (byte)14, (byte)7, (byte)5, (byte)11}, {(byte)10, (byte)15, (byte)4, (byte)2, (byte)7, (byte)12, (byte)9, (byte)5, (byte)6, (byte)1, (byte)13, (byte)14, (byte)0, (byte)11, (byte)3, (byte)8}, {(byte)9, (byte)14, (byte)15, (byte)5, (byte)2, (byte)8, (byte)12, (byte)3, (byte)7, (byte)0, (byte)4, (byte)10, (byte)1, (byte)13, (byte)11, (byte)6}, {(byte)4, (byte)3, (byte)2, (byte)12, (byte)9, (byte)5, (byte)15, (byte)10, (byte)11, (byte)14, (byte)1, (byte)7, (byte)6, (byte)0, (byte)8, (byte)13}}, {{(byte)4, (byte)11, (byte)2, (byte)14, (byte)15, (byte)0, (byte)8, (byte)13, (byte)3, (byte)12, (byte)9, (byte)7, (byte)5, (byte)10, (byte)6, (byte)1}, {(byte)13, (byte)0, (byte)11, (byte)7, (byte)4, (byte)9, (byte)1, (byte)10, (byte)14, (byte)3, (byte)5, (byte)12, (byte)2, (byte)15, (byte)8, (byte)6}, {(byte)1, (byte)4, (byte)11, (byte)13, (byte)12, (byte)3, (byte)7, (byte)14, (byte)10, (byte)15, (byte)6, (byte)8, (byte)0, (byte)5, (byte)9, (byte)2}, {(byte)6, (byte)11, (byte)13, (byte)8, (byte)1, (byte)4, (byte)10, (byte)7, (byte)9, (byte)5, (byte)0, (byte)15, (byte)14, (byte)2, (byte)3, (byte)12}}, {{(byte)13, (byte)2, (byte)8, (byte)4, (byte)6, (byte)15, (byte)11, (byte)1, (byte)10, (byte)9, (byte)3, (byte)14, (byte)5, (byte)0, (byte)12, (byte)7}, {(byte)1, (byte)15, (byte)13, (byte)8, (byte)10, (byte)3, (byte)7, (byte)4, (byte)12, (byte)5, (byte)6, (byte)11, (byte)0, (byte)14, (byte)9, (byte)2}, {(byte)7, (byte)11, (byte)4, (byte)1, (byte)9, (byte)12, (byte)14, (byte)2, (byte)0, (byte)6, (byte)10, (byte)13, (byte)15, (byte)3, (byte)5, (byte)8}, {(byte)2, (byte)1, (byte)14, (byte)7, (byte)4, (byte)10, (byte)8, (byte)13, (byte)15, (byte)12, (byte)9, (byte)0, (byte)3, (byte)5, (byte)6, (byte)11}}};
    static final byte[] P_Table = new byte[]{(byte)16, (byte)7, (byte)20, (byte)21, (byte)29, (byte)12, (byte)28, (byte)17, (byte)1, (byte)15, (byte)23, (byte)26, (byte)5, (byte)18, (byte)31, (byte)10, (byte)2, (byte)8, (byte)24, (byte)14, (byte)32, (byte)27, (byte)3, (byte)9, (byte)19, (byte)13, (byte)30, (byte)6, (byte)22, (byte)11, (byte)4, (byte)25};
    static final byte[] IPR_Table = new byte[]{(byte)40, (byte)8, (byte)48, (byte)16, (byte)56, (byte)24, (byte)64, (byte)32, (byte)39, (byte)7, (byte)47, (byte)15, (byte)55, (byte)23, (byte)63, (byte)31, (byte)38, (byte)6, (byte)46, (byte)14, (byte)54, (byte)22, (byte)62, (byte)30, (byte)37, (byte)5, (byte)45, (byte)13, (byte)53, (byte)21, (byte)61, (byte)29, (byte)36, (byte)4, (byte)44, (byte)12, (byte)52, (byte)20, (byte)60, (byte)28, (byte)35, (byte)3, (byte)43, (byte)11, (byte)51, (byte)19, (byte)59, (byte)27, (byte)34, (byte)2, (byte)42, (byte)10, (byte)50, (byte)18, (byte)58, (byte)26, (byte)33, (byte)1, (byte)41, (byte)9, (byte)49, (byte)17, (byte)57, (byte)25};
    byte[][] szSubKeys = new byte[16][48];
    byte[] szCiphertextRaw = new byte[64];
    byte[] szPlaintextRaw = new byte[64];
    byte[] szCiphertextInBytes = new byte[8];
    byte[] szPlaintextInBytes = new byte[8];
    byte[] szCiphertextInBinary = new byte[65];
    byte[] szCiphertextInHex = new byte[17];
    byte[] szPlaintext = new byte[9];

    DES2() {
    }

    public void yxyDES2_InitializeKey(byte[] srcBytes) {
        byte[] sz_64key = new byte[64];
        byte[] sz_56key = new byte[56];
        boolean k = false;
        this.yxyDES2_Bytes2Bits(srcBytes, sz_64key, 64);

        for(int var5 = 0; var5 < 56; ++var5) {
            sz_56key[var5] = sz_64key[PC1_Table[var5] - 1];
        }

        this.yxyDES2_CreateSubKey(sz_56key);
    }

    void yxyDES2_EncryptData(byte[] _srcBytes) {
        byte[] szSrcBits = new byte[64];
        byte[] sz_IP = new byte[64];
        byte[] sz_Li = new byte[32];
        byte[] sz_Ri = new byte[32];
        byte[] sz_Final64 = new byte[64];
        boolean i = false;
        boolean j = false;
        this.yxyDES2_Bytes2Bits(_srcBytes, szSrcBits, 64);
        this.yxyDES2_InitialPermuteData(szSrcBits, sz_IP);
        this.memcpy(sz_Li, 0, sz_IP, 0, 32);
        this.memcpy(sz_Ri, 0, sz_IP, 32, 32);

        for(int var9 = 0; var9 < 16; ++var9) {
            this.yxyDES2_FunctionF(sz_Li, sz_Ri, var9);
        }

        this.memcpy(sz_Final64, 0, sz_Ri, 0, 32);
        this.memcpy(sz_Final64, 32, sz_Li, 0, 32);

        for(int var10 = 0; var10 < 64; ++var10) {
            this.szCiphertextRaw[var10] = sz_Final64[IPR_Table[var10] - 1];
        }

        this.yxyDES2_Bits2Bytes(this.szCiphertextInBytes, this.szCiphertextRaw, 64);
    }

    void yxyDES2_DecryptData(byte[] _srcBytes) {
        byte[] szSrcBits = new byte[64];
        byte[] sz_IP = new byte[64];
        byte[] sz_Li = new byte[32];
        byte[] sz_Ri = new byte[32];
        byte[] sz_Final64 = new byte[64];
        boolean i = false;
        boolean j = false;
        this.yxyDES2_Bytes2Bits(_srcBytes, szSrcBits, 64);
        this.yxyDES2_InitialPermuteData(szSrcBits, sz_IP);
        this.memcpy(sz_Ri, 0, sz_IP, 0, 32);
        this.memcpy(sz_Li, 0, sz_IP, 32, 32);

        for(int var9 = 0; var9 < 16; ++var9) {
            this.yxyDES2_FunctionF(sz_Ri, sz_Li, 15 - var9);
        }

        this.memcpy(sz_Final64, 0, sz_Li, 0, 32);
        this.memcpy(sz_Final64, 32, sz_Ri, 0, 32);

        for(int var10 = 0; var10 < 64; ++var10) {
            this.szPlaintextRaw[var10] = sz_Final64[IPR_Table[var10] - 1];
        }

        this.yxyDES2_Bits2Bytes(this.szPlaintextInBytes, this.szPlaintextRaw, 64);
    }

    byte[] getPlaintext() {
        return this.szPlaintextInBytes;
    }

    public int yxyDES2_EncryptAnyLength(byte[] _srcBytes, byte[] dst, int _bytesLength) {
        boolean iParts = false;
        boolean iResidue = false;
        boolean i = false;
        int rsLen = 0;
        int dstIdx = 0;
        byte[] szLast8Bits = new byte[8];
        if(_bytesLength == 8) {
            this.yxyDES2_EncryptData(_srcBytes);
            this.memcpy(dst, 0, this.szCiphertextInBytes, 0, 8);
            dst[8] = 0;
            rsLen = 8;
        } else if(_bytesLength < 8) {
            byte[] _temp8bytes = new byte[8];
            this.memcpy(_temp8bytes, 0, _srcBytes, 0, _bytesLength);
            this.yxyDES2_EncryptData(_temp8bytes);
            this.memcpy(dst, 0, this.szCiphertextInBytes, 0, 8);
            dst[8] = 0;
            rsLen = 8;
        } else if(_bytesLength > 8) {
            int var11 = _bytesLength >> 3;
            int var12 = _bytesLength % 8;

            for(int var13 = 0; var13 < var11; ++var13) {
                this.memcpy(szLast8Bits, 0, _srcBytes, var13 * 8, 8);
                this.yxyDES2_EncryptData(szLast8Bits);
                this.memcpy(dst, dstIdx, this.szCiphertextInBytes, 0, 8);
                dstIdx += 8;
                rsLen += 8;
            }

            if(var12 != 0) {
                this.memset(szLast8Bits, 0, 8);
                this.memcpy(szLast8Bits, 0, _srcBytes, var11 * 8, var12);
                this.yxyDES2_EncryptData(szLast8Bits);
                this.memcpy(dst, 0, this.szCiphertextInBytes, 0, 8);
                dst[8] = 0;
                rsLen += 8;
            }
        }

        return rsLen;
    }

    public int yxyDES2_DecryptAnyLength(byte[] _srcBytes, byte[] dst, int _bytesLength) {
        boolean iParts = false;
        boolean iResidue = false;
        boolean i = false;
        int rsLen = 0;
        int dstIdx = 0;
        byte[] szLast8Bits = new byte[8];
        byte[] _temp8bytes = new byte[8];
        if(_bytesLength == 8) {
            this.yxyDES2_DecryptData(_srcBytes);
            this.memcpy(dst, 0, this.szPlaintextInBytes, 0, 8);
            dst[8] = 0;
            rsLen = 8;
        } else if(_bytesLength < 8) {
            this.memcpy(_temp8bytes, 0, _srcBytes, 0, 8);
            this.yxyDES2_DecryptData(_temp8bytes);
            this.memcpy(dst, 0, this.szPlaintextInBytes, 0, _bytesLength);
            dst[_bytesLength] = 0;
            rsLen = 8;
        } else if(_bytesLength > 8) {
            int var11 = _bytesLength >> 3;
            int var12 = _bytesLength % 8;

            for(int var13 = 0; var13 < var11; ++var13) {
                this.memcpy(szLast8Bits, 0, _srcBytes, var13 << 3, 8);
                this.yxyDES2_DecryptData(szLast8Bits);
                this.memcpy(dst, dstIdx, this.szPlaintextInBytes, 0, 8);
                dstIdx += 8;
                rsLen += 8;
            }

            if(var12 != 0) {
                this.memset(szLast8Bits, 0, 8);
                this.memcpy(szLast8Bits, 0, _srcBytes, var11 << 3, 8);
                this.yxyDES2_DecryptData(szLast8Bits);
                this.memcpy(dst, 0, this.szPlaintextInBytes, 0, var12);
                rsLen += 8;
            }

            dst[8] = 0;
        }

        return rsLen;
    }

    void yxyDES2_Bytes2Bits(byte[] srcBytes, byte[] dstBits, int sizeBits) {
        boolean i = false;

        for(int var5 = 0; var5 < sizeBits; ++var5) {
            dstBits[var5] = (byte)(((srcBytes[var5 >> 3] & 255) << (var5 & 7) & 128) >> 7);
        }

    }

    void yxyDES2_Bits2Bytes(byte[] dstBytes, byte[] srcBits, int sizeBits) {
        boolean i = false;
        this.memset(dstBytes, 0, sizeBits >> 3);

        for(int var5 = 0; var5 < sizeBits; ++var5) {
            dstBytes[var5 >> 3] = (byte)(dstBytes[var5 >> 3] | (srcBits[var5] & 255) << 7 - (var5 & 7));
        }

    }

    void yxyDES2_Int2Bits(int _src, byte[] dstBits) {
        boolean i = false;

        for(int var4 = 0; var4 < 4; ++var4) {
            dstBits[var4] = (byte)((_src << var4 & 8) >> 3);
        }

    }

    void yxyDES2_Bits2Hex(byte[] dstHex, byte[] srcBits, int sizeBits) {
    }

    void yxyDES2_Hex2Bits(byte[] srcHex, byte[] dstBits, int sizeBits) {
    }

    byte[] yxyDES2_GetCiphertextInBinary() {
        return null;
    }

    byte[] yxyDES2_GetCiphertextInHex() {
        return null;
    }

    byte[] yxyDES2_GetCiphertextInBytes() {
        return null;
    }

    byte[] yxyDES2_GetPlaintext() {
        return null;
    }

    void yxyDES2_CreateSubKey(byte[] sz_56key) {
        byte[] szTmpL = new byte[28];
        byte[] szTmpR = new byte[28];
        byte[] szCi = new byte[28];
        byte[] szDi = new byte[28];
        byte[] szTmp56 = new byte[56];
        boolean i = false;
        boolean j = false;
        this.memcpy(szTmpL, 0, sz_56key, 0, 28);
        this.memcpy(szTmpR, 0, sz_56key, 28, 28);

        for(int var9 = 0; var9 < 16; ++var9) {
            this.memcpy(szCi, 0, szTmpL, Shift_Table[var9], 28 - Shift_Table[var9]);
            this.memcpy(szCi, 28 - Shift_Table[var9], szTmpL, 0, Shift_Table[var9]);
            this.memcpy(szDi, 0, szTmpR, Shift_Table[var9], 28 - Shift_Table[var9]);
            this.memcpy(szDi, 28 - Shift_Table[var9], szTmpR, 0, Shift_Table[var9]);
            this.memcpy(szTmp56, 0, szCi, 0, 28);
            this.memcpy(szTmp56, 28, szDi, 0, 28);

            for(int var10 = 0; var10 < 48; ++var10) {
                this.szSubKeys[var9][var10] = szTmp56[PC2_Table[var10] - 1];
            }

            this.memcpy(szTmpL, 0, szCi, 0, 28);
            this.memcpy(szTmpR, 0, szDi, 0, 28);
        }

    }

    void yxyDES2_FunctionF(byte[] sz_Li, byte[] sz_Ri, int iKey) {
        byte[] sz_48R = new byte[48];
        byte[] sz_xor48 = new byte[48];
        byte[] sz_P32 = new byte[32];
        byte[] sz_Rii = new byte[32];
        byte[] sz_Key = new byte[48];
        byte[] s_Compress32 = new byte[32];
        this.memcpy(sz_Key, 0, this.szSubKeys[iKey], 0, 48);
        this.yxyDES2_ExpansionR(sz_Ri, sz_48R);
        this.yxyDES2_XOR(sz_48R, sz_Key, 48, sz_xor48);
        this.yxyDES2_CompressFuncS(sz_xor48, s_Compress32);
        this.yxyDES2_PermutationP(s_Compress32, sz_P32);
        this.yxyDES2_XOR(sz_P32, sz_Li, 32, sz_Rii);
        this.memcpy(sz_Li, 0, sz_Ri, 0, 32);
        this.memcpy(sz_Ri, 0, sz_Rii, 0, 32);
    }

    void yxyDES2_InitialPermuteData(byte[] _src, byte[] _dst) {
        boolean i = false;

        for(int var4 = 0; var4 < 64; ++var4) {
            _dst[var4] = _src[IP_Table[var4] - 1];
        }

    }

    void yxyDES2_ExpansionR(byte[] _src, byte[] _dst) {
        boolean i = false;

        for(int var4 = 0; var4 < 48; ++var4) {
            _dst[var4] = _src[E_Table[var4] - 1];
        }

    }

    void yxyDES2_XOR(byte[] szParam1, byte[] szParam2, int uiParamLength, byte[] szReturnValueBuffer) {
        boolean i = false;

        for(int var6 = 0; var6 < uiParamLength; ++var6) {
            szReturnValueBuffer[var6] = (byte)(szParam1[var6] & 255 ^ szParam2[var6] & 255);
        }

    }

    void yxyDES2_CompressFuncS(byte[] _src48, byte[] _dst32) {
        byte[][] bTemp = new byte[8][6];
        byte[] dstBits = new byte[4];
        boolean i = false;
        boolean iX = false;
        boolean iY = false;
        boolean j = false;

        for(int var9 = 0; var9 < 8; ++var9) {
            this.memcpy(bTemp[var9], 0, _src48, var9 * 6, 6);
            int var10 = (bTemp[var9][0] & 255) * 2 + (bTemp[var9][5] & 255);
            int var11 = 0;

            for(int var12 = 1; var12 < 5; ++var12) {
                var11 += (bTemp[var9][var12] & 255) << 4 - var12;
            }

            this.yxyDES2_Int2Bits(S_Box[var9][var10][var11], dstBits);
            this.memcpy(_dst32, var9 * 4, dstBits, 0, 4);
        }

    }

    void yxyDES2_PermutationP(byte[] _src, byte[] _dst) {
        boolean i = false;

        for(int var4 = 0; var4 < 32; ++var4) {
            _dst[var4] = _src[P_Table[var4] - 1];
        }

    }

    public void memcpy(byte[] dest, int desOffset, byte[] src, int srcOffset, int nLength) {
        for(int i = 0; i < nLength; ++i) {
            dest[desOffset + i] = src[srcOffset + i];
        }

    }

    public void memset(byte[] s, int ch, int n) {
        for(int i = 0; i < n; ++i) {
            s[i] = (byte)ch;
        }

    }
}

