package in.entrylog.entrylog.myprinter;

/**
 * Created by Admin on 23-Jun-16.
 */
public class ESCCmd {
    public byte[] DES_SETKEY = new byte[]{(byte)31, (byte)31, (byte)0, (byte)8, (byte)0, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1, (byte)1};
    public byte[] DES_ENCRYPT = new byte[]{(byte)31, (byte)31, (byte)1};
    public byte[] DES_ENCRYPT2 = new byte[]{(byte)31, (byte)31, (byte)2};
    public byte[] ERROR = new byte[1];
    public byte[] ESC_ALT = new byte[]{(byte)27, (byte)64};
    public byte[] ESC_L = new byte[]{(byte)27, (byte)76};
    public byte[] ESC_CAN = new byte[]{(byte)24};
    public byte[] FF = new byte[]{(byte)12};
    public byte[] ESC_FF = new byte[]{(byte)27, (byte)12};
    public byte[] ESC_S = new byte[]{(byte)27, (byte)83};
    public byte[] GS_P_x_y = new byte[]{(byte)29, (byte)80, (byte)0, (byte)0};
    public byte[] ESC_R_n = new byte[]{(byte)27, (byte)82, (byte)0};
    public byte[] ESC_t_n = new byte[]{(byte)27, (byte)116, (byte)0};
    public byte[] LF = new byte[]{(byte)10};
    public byte[] CR = new byte[]{(byte)13};
    public byte[] ESC_3_n = new byte[]{(byte)27, (byte)51, (byte)0};
    public byte[] ESC_SP_n = new byte[]{(byte)27, (byte)32, (byte)0};
    public byte[] DLE_DC4_n_m_t = new byte[]{(byte)16, (byte)20, (byte)1, (byte)0, (byte)1};
    public byte[] GS_V_m = new byte[]{(byte)29, (byte)86, (byte)0};
    public byte[] GS_V_m_n = new byte[]{(byte)29, (byte)86, (byte)66, (byte)0};
    public byte[] GS_W_nL_nH = new byte[]{(byte)29, (byte)87, (byte)118, (byte)2};
    public byte[] ESC_dollors_nL_nH = new byte[]{(byte)27, (byte)36, (byte)0, (byte)0};
    public byte[] ESC_a_n = new byte[]{(byte)27, (byte)97, (byte)0};
    public byte[] GS_exclamationmark_n = new byte[]{(byte)29, (byte)33, (byte)0};
    public byte[] ESC_M_n = new byte[]{(byte)27, (byte)77, (byte)0};
    public byte[] GS_E_n = new byte[]{(byte)27, (byte)69, (byte)0};
    public byte[] ESC_line_n = new byte[]{(byte)27, (byte)45, (byte)0};
    public byte[] ESC_lbracket_n = new byte[]{(byte)27, (byte)123, (byte)0};
    public byte[] GS_B_n = new byte[]{(byte)29, (byte)66, (byte)0};
    public byte[] ESC_V_n = new byte[]{(byte)27, (byte)86, (byte)0};
    public byte[] GS_backslash_m = new byte[]{(byte)29, (byte)47, (byte)0};
    public byte[] FS_p_n_m = new byte[]{(byte)28, (byte)112, (byte)1, (byte)0};
    public byte[] GS_H_n = new byte[]{(byte)29, (byte)72, (byte)0};
    public byte[] GS_f_n = new byte[]{(byte)29, (byte)102, (byte)0};
    public byte[] GS_h_n = new byte[]{(byte)29, (byte)104, (byte)-94};
    public byte[] GS_w_n = new byte[]{(byte)29, (byte)119, (byte)3};
    public byte[] GS_k_m_n_ = new byte[]{(byte)29, (byte)107, (byte)65, (byte)12};
    public byte[] GS_k_m_v_r_nL_nH = new byte[]{(byte)29, (byte)107, (byte)97, (byte)0, (byte)2, (byte)0, (byte)0};
    public byte[] ESC_W_xL_xH_yL_yH_dxL_dxH_dyL_dyH = new byte[]{(byte)27, (byte)87, (byte)0, (byte)0, (byte)0, (byte)0, (byte)72, (byte)2, (byte)-80, (byte)4};
    public byte[] ESC_T_n = new byte[]{(byte)27, (byte)84, (byte)0};
    public byte[] GS_dollors_nL_nH = new byte[]{(byte)29, (byte)36, (byte)0, (byte)0};
    public byte[] GS_backslash_nL_nH = new byte[]{(byte)29, (byte)92, (byte)0, (byte)0};
    public byte[] FS_line_n = new byte[]{(byte)28, (byte)45, (byte)0};
    public byte[] GS_leftbracket_k_pL_pH_cn_67_n = new byte[]{(byte)29, (byte)40, (byte)107, (byte)3, (byte)0, (byte)49, (byte)67, (byte)3};
    public byte[] GS_leftbracket_k_pL_pH_cn_69_n = new byte[]{(byte)29, (byte)40, (byte)107, (byte)3, (byte)0, (byte)49, (byte)69, (byte)48};
    public byte[] GS_leftbracket_k_pL_pH_cn_80_m__d1dk = new byte[]{(byte)29, (byte)40, (byte)107, (byte)3, (byte)0, (byte)49, (byte)80, (byte)48};
    public byte[] GS_leftbracket_k_pL_pH_cn_fn_m = new byte[]{(byte)29, (byte)40, (byte)107, (byte)3, (byte)0, (byte)49, (byte)81, (byte)48};

    ESCCmd() {
    }
}

