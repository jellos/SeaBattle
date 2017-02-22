package com.kipsap.commonsource;

import com.kipsap.jshipbattle.R;


public class JConstants {

    // Strings and ints that are used both on server and client side

    public final static int UPDATE_INTERVAL_BACKGROUND_SERVICE = 60;// 60 request for changes in gamestates/chat in the background service alternating every minute
    public final static int UPDATE_INTERVAL_GAMEPICKER_SCREEN = 60;
    public final static int UPDATE_INTERVAL_IN_GAME = 5;

    public static boolean PAID_VERSION = false;

    //  deze werkt niet ! public static String base64EncodedPublicKey =
    //		"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmfW6nnWP1NhEfkQJU6wWtUY/B6MzalzFu/Boa5DAJ5MMKPBwUotszW45R4pztAZLFUXVuaiYBH7dNAzUBrGWdA0wemKohHfWokqMIILHbTR0zh5ScNTdkZDTk6Oc8jZaRhajKvAA0RSi3/i0/K7KQ9mXSpGN+W04364oS85I252h+UyUvlat+0LBv8UGnqUarNt4DH2N4xfvkFpGydvBKVEkOeWvwkHESrXVEg8QnWURMHQCBXxPcF0RjMHAnZls7ZvrFZ+ooCWPHJ9YijcnwJTCmdkO2O+xCipdKLdd6dG4o60v7N2V1nge13TEhqTxf3D55R8Zqe4BUjZs0KTw4QIDAQAB";

    public static String base64EncodedPublicKey =
            "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArgl+fIhXMIkzQyH7SmouaCOX2+loS1FXNjzTZyAtkup2t/ek4gvznV2ku9aUJAYFaIyyksbM2MP+8E4vA4u1avTlPtK7/fgrozeFu+vaKCnXtKjvKkBK+kAL+/f1R5W+UOPvi4FqI6MPDpX1mRGNCFSv8TrwQ8hIJvon6CxzEbSHLouDpZb36gRGGtWXmyYPQXKMC4UTMLvP5e86wu0ogI/I8NQZEukon+yyQBONTCBMB40N4IB9VQSu5IvQdaLS9XfrTwJor2BbIqzjmsuyXXI6fatxYmGaE2iCJagNhO5aEtZmBTPYybTysRef8VcPpZw7lZJ59gD3UfIFdOQfhQIDAQAB";

    public static final String SKU_PREMIUM_APP = "com.kipsap.jshipbattlepremium";

    public static final int RESULT_OK = 0; //login/signup/game accept/game decline was successful
    public static final int RESULT_NOT_OK = 1;
    public static final int RESULT_UNKNOWN_USER = 2;
    public static final int RESULT_WRONG_PASSWORD = 3;
    public static final int RESULT_USERNAME_ALREADY_EXISTS = 4;
    public static final int RESULT_INVALID_REQUEST = 5;
    public static final int RESULT_NO_RESPONSE = 6;
    public static final int RESULT_ALREADY_RUNNING_GAME = 7;
    public static final int RESULT_CANNOT_PLAY_AGAINST_YOURSELF = 8;
    public static final int RESULT_GENERAL_ERROR = 9;
    public static final int RESULT_NOT_YOUR_TURN = 10;
    public static final int RESULT_GAME_FINISHED = 11;
    public static final int RESULT_ALREADY_SHOT_HERE = 12;
    public static final int RESULT_INCORRECT_VERSION = 13;
    public static final int RESULT_TEXT_NOT_ALLOWED = 14;
    public static final int RESULT_CHAT_LIMIT_EXCEEDED = 15;

    public static int getCountryFlagResourceID(String country)
    {
        if (country.equals("AD"))
            return R.drawable.land_ad;
        if (country.equals("AE"))
            return R.drawable.land_ae;
        if (country.equals("AF"))
            return R.drawable.land_af;
        if (country.equals("AG"))
            return R.drawable.land_ag;
        if (country.equals("AI"))
            return R.drawable.land_ai;
        if (country.equals("AL"))
            return R.drawable.land_al;
        if (country.equals("AM"))
            return R.drawable.land_am;
        if (country.equals("AN"))
            return R.drawable.land_an;
        if (country.equals("AO"))
            return R.drawable.land_ao;
        if (country.equals("AQ"))
            return R.drawable.land_aq;
        if (country.equals("AR"))
            return R.drawable.land_ar;
        if (country.equals("AS"))
            return R.drawable.land_as;
        if (country.equals("AT"))
            return R.drawable.land_at;
        if (country.equals("AU"))
            return R.drawable.land_au;
        if (country.equals("AW"))
            return R.drawable.land_aw;
        if (country.equals("AZ"))
            return R.drawable.land_az;
        if (country.equals("BA"))
            return R.drawable.land_ba;
        if (country.equals("BD"))
            return R.drawable.land_bd;
        if (country.equals("BE"))
            return R.drawable.land_be;
        if (country.equals("BG"))
            return R.drawable.land_bg;
        if (country.equals("BH"))
            return R.drawable.land_bh;
        if (country.equals("BJ"))
            return R.drawable.land_bj;
        if (country.equals("BM"))
            return R.drawable.land_bm;
        if (country.equals("BN"))
            return R.drawable.land_bn;
        if (country.equals("BO"))
            return R.drawable.land_bo;
        if (country.equals("BR"))
            return R.drawable.land_br;
        if (country.equals("BW"))
            return R.drawable.land_bw;
        if (country.equals("BY"))
            return R.drawable.land_by;
        if (country.equals("BZ"))
            return R.drawable.land_bz;
        if (country.equals("CA"))
            return R.drawable.land_ca;
        if (country.equals("CD"))
            return R.drawable.land_cd;
        if (country.equals("CG"))
            return R.drawable.land_cg;
        if (country.equals("CH"))
            return R.drawable.land_ch;
        if (country.equals("CK"))
            return R.drawable.land_ck;
        if (country.equals("CL"))
            return R.drawable.land_cl;
        if (country.equals("CM"))
            return R.drawable.land_cm;
        if (country.equals("CN"))
            return R.drawable.land_cn;
        if (country.equals("CO"))
            return R.drawable.land_co;
        if (country.equals("CR"))
            return R.drawable.land_cr;
        if (country.equals("CU"))
            return R.drawable.land_cu;
        if (country.equals("CY"))
            return R.drawable.land_cy;
        if (country.equals("CZ"))
            return R.drawable.land_cz;
        if (country.equals("DE"))
            return R.drawable.land_de;
        if (country.equals("DK"))
            return R.drawable.land_dk;
        if (country.equals("DJ"))
            return R.drawable.land_dj;
        if (country.equals("DO"))
            return R.drawable.land_do;
        if (country.equals("DZ"))
            return R.drawable.land_dz;
        if (country.equals("EC"))
            return R.drawable.land_ec;
        if (country.equals("EE"))
            return R.drawable.land_ee;
        if (country.equals("EG"))
            return R.drawable.land_eg;
        if (country.equals("EH"))
            return R.drawable.land_eh;
        if (country.equals("ER"))
            return R.drawable.land_er;
        if (country.equals("ES"))
            return R.drawable.land_es;
        if (country.equals("FI"))
            return R.drawable.land_fi;
        if (country.equals("FJ"))
            return R.drawable.land_fj;
        if (country.equals("FK"))
            return R.drawable.land_fk;
        if (country.equals("FO"))
            return R.drawable.land_fo;
        if (country.equals("FR"))
            return R.drawable.land_fr;
        if (country.equals("GB"))
            return R.drawable.land_gb;
        if (country.equals("GD"))
            return R.drawable.land_gd;
        if (country.equals("GE"))
            return R.drawable.land_ge;
        if (country.equals("GF"))
            return R.drawable.land_gf;
        if (country.equals("GH"))
            return R.drawable.land_gh;
        if (country.equals("GL"))
            return R.drawable.land_gl;
        if (country.equals("GR"))
            return R.drawable.land_gr;
        if (country.equals("GT"))
            return R.drawable.land_gt;
        if (country.equals("GY"))
            return R.drawable.land_gy;
        if (country.equals("HK"))
            return R.drawable.land_hk;
        if (country.equals("HN"))
            return R.drawable.land_hn;
        if (country.equals("HR"))
            return R.drawable.land_hr;
        if (country.equals("HT"))
            return R.drawable.land_ht;
        if (country.equals("HU"))
            return R.drawable.land_hu;
        if (country.equals("ID"))
            return R.drawable.land_id;
        if (country.equals("IE"))
            return R.drawable.land_ie;
        if (country.equals("IL"))
            return R.drawable.land_il;
        if (country.equals("IN"))
            return R.drawable.land_in;
        if (country.equals("IO"))
            return R.drawable.land_io;
        if (country.equals("IQ"))
            return R.drawable.land_iq;
        if (country.equals("IR"))
            return R.drawable.land_ir;
        if (country.equals("IS"))
            return R.drawable.land_is;
        if (country.equals("IT"))
            return R.drawable.land_it;
        if (country.equals("JM"))
            return R.drawable.land_jm;
        if (country.equals("JO"))
            return R.drawable.land_jo;
        if (country.equals("JP"))
            return R.drawable.land_jp;
        if (country.equals("KE"))
            return R.drawable.land_ke;
        if (country.equals("KH"))
            return R.drawable.land_kh;
        if (country.equals("KM"))
            return R.drawable.land_km;
        if (country.equals("KP"))
            return R.drawable.land_kp;
        if (country.equals("KR"))
            return R.drawable.land_kr;
        if (country.equals("KW"))
            return R.drawable.land_kw;
        if (country.equals("KZ"))
            return R.drawable.land_kz;
        if (country.equals("LA"))
            return R.drawable.land_la;
        if (country.equals("LB"))
            return R.drawable.land_lb;
        if (country.equals("LI"))
            return R.drawable.land_li;
        if (country.equals("LK"))
            return R.drawable.land_lk;
        if (country.equals("LT"))
            return R.drawable.land_lt;
        if (country.equals("LU"))
            return R.drawable.land_lu;
        if (country.equals("LV"))
            return R.drawable.land_lv;
        if (country.equals("MA"))
            return R.drawable.land_ma;
        if (country.equals("MC"))
            return R.drawable.land_mc;
        if (country.equals("MD"))
            return R.drawable.land_md;
        if (country.equals("ME"))
            return R.drawable.land_me;
        if (country.equals("MG"))
            return R.drawable.land_mg;
        if (country.equals("MK"))
            return R.drawable.land_mk;
        if (country.equals("MM"))
            return R.drawable.land_mm;
        if (country.equals("MN"))
            return R.drawable.land_mn;
        if (country.equals("MO"))
            return R.drawable.land_mo;
        if (country.equals("MT"))
            return R.drawable.land_mt;
        if (country.equals("MU"))
            return R.drawable.land_mu;
        if (country.equals("MX"))
            return R.drawable.land_mx;
        if (country.equals("MY"))
            return R.drawable.land_my;
        if (country.equals("NE"))
            return R.drawable.land_ne;
        if (country.equals("NG"))
            return R.drawable.land_ng;
        if (country.equals("NI"))
            return R.drawable.land_ni;
        if (country.equals("NL"))
            return R.drawable.land_nl;
        if (country.equals("NO"))
            return R.drawable.land_no;
        if (country.equals("NP"))
            return R.drawable.land_np;
        if (country.equals("NR"))
            return R.drawable.land_nr;
        if (country.equals("NZ"))
            return R.drawable.land_nz;
        if (country.equals("OM"))
            return R.drawable.land_om;
        if (country.equals("PA"))
            return R.drawable.land_pa;
        if (country.equals("PE"))
            return R.drawable.land_pe;
        if (country.equals("PH"))
            return R.drawable.land_ph;
        if (country.equals("PK"))
            return R.drawable.land_pk;
        if (country.equals("PL"))
            return R.drawable.land_pl;
        if (country.equals("PR"))
            return R.drawable.land_pr;
        if (country.equals("PT"))
            return R.drawable.land_pt;
        if (country.equals("PW"))
            return R.drawable.land_pw;
        if (country.equals("PY"))
            return R.drawable.land_py;
        if (country.equals("QA"))
            return R.drawable.land_qa;
        if (country.equals("QQ"))
            return R.drawable.land_qq;
        if (country.equals("RE"))
            return R.drawable.land_re;
        if (country.equals("RS"))
            return R.drawable.land_rs;
        if (country.equals("RO"))
            return R.drawable.land_ro;
        if (country.equals("RU"))
            return R.drawable.land_ru;
        if (country.equals("SA"))
            return R.drawable.land_sa;
        if (country.equals("SC"))
            return R.drawable.land_sc;
        if (country.equals("SE"))
            return R.drawable.land_se;
        if (country.equals("SG"))
            return R.drawable.land_sg;
        if (country.equals("SI"))
            return R.drawable.land_si;
        if (country.equals("SK"))
            return R.drawable.land_sk;
        if (country.equals("SN"))
            return R.drawable.land_sn;
        if (country.equals("SO"))
            return R.drawable.land_so;
        if (country.equals("SR"))
            return R.drawable.land_sr;
        if (country.equals("SS"))
            return R.drawable.land_ss;
        if (country.equals("SV"))
            return R.drawable.land_sv;
        if (country.equals("SY"))
            return R.drawable.land_sy;
        if (country.equals("TC"))
            return R.drawable.land_tc;
        if (country.equals("TD"))
            return R.drawable.land_td;
        if (country.equals("TG"))
            return R.drawable.land_tg;
        if (country.equals("TH"))
            return R.drawable.land_th;
        if (country.equals("TL"))
            return R.drawable.land_tl;
        if (country.equals("TN"))
            return R.drawable.land_tn;
        if (country.equals("TR"))
            return R.drawable.land_tr;
        if (country.equals("TT"))
            return R.drawable.land_tt;
        if (country.equals("TW"))
            return R.drawable.land_tw;
        if (country.equals("TZ"))
            return R.drawable.land_tz;
        if (country.equals("UA"))
            return R.drawable.land_ua;
        if (country.equals("US"))
            return R.drawable.land_us;
        if (country.equals("UM"))
            return R.drawable.land_us; // us minor outlying islands = usa
        if (country.equals("UY"))
            return R.drawable.land_uy;
        if (country.equals("UZ"))
            return R.drawable.land_uz;
        if (country.equals("VE"))
            return R.drawable.land_ve;
        if (country.equals("VG"))
            return R.drawable.land_vg;
        if (country.equals("VN"))
            return R.drawable.land_vn;
        if (country.equals("WF"))
            return R.drawable.land_wf;
        if (country.equals("YE"))
            return R.drawable.land_ye;
        if (country.equals("ZA"))
            return R.drawable.land_za;
        if (country.equals("ZM"))
            return R.drawable.land_zm;
        if (country.equals("ZW"))
            return R.drawable.land_zw;

        return R.drawable.land_unknown;
    }

}