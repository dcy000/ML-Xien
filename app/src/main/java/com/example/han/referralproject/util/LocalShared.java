package com.example.han.referralproject.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.example.han.referralproject.application.MyApplication;
import com.example.han.referralproject.constant.ConstantData;
import com.example.han.referralproject.speech.setting.IatSettings;
import com.gzq.lib_core.bean.UserInfoBean;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Random;

public class LocalShared {
    private final String SharedName = "ScopeMediaPrefsFile";
    private static LocalShared mInstance;
    public SharedPreferences mShared;

    private final String UserAccounts = "user_accounts";
    private final String UserAccounts_new = "user_accounts_new";
    private final String UserId = "user_id";
    private static final String USER_NAME = "user_name";
    private static final String USER_ID_CARD = "user_id_card";
    private final String UserImg = "user_img";
    private final String XunfeiId = "Xunfei_Id";
    private final String UserPhoneNum = "user_phone_num";
    private final String MAC_Xueya = "mac_xueya";
    private final String MAC_Wendu = "mac_wendu";
    private final String MAC_Xueyang = "mac_xueyang";
    private final String MAC_Xindian = "mac_xindian";
    private final String MAC_Sanheyi = "mac_sanheyi";
    private final String MAC_Xuetang = "mac_xuetang";
    private final String MAC_Tizhong = "mac_tizhong";
    private final String MAC_BreathHome = "mac_breathhome";
    private final String Guide_Add_Click = "guide_add_click";
    private final String Guide_Create_Text = "guide_create_text";
    private final String Guide_Sign_In = "guide_sign_in_two";

    public static final String IS_FIRST_IN = "isFirstIn";

    private Context context;

    private LocalShared(Context context) {
        this.context = context.getApplicationContext();
        mShared = context.getSharedPreferences(SharedName, Context.MODE_PRIVATE);
    }

    public void setString(String key, String value) {
        mShared.edit().putString(key, value).commit();
    }

    public String getString(String key) {
        return mShared.getString(key, "");
    }

    public static LocalShared getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new LocalShared(context);
        }
        return mInstance;
    }

    public boolean getIsFirstIn() {
        return mShared.getBoolean(IS_FIRST_IN, true);
    }

    public void setIsFirstIn(boolean isFirstIn) {
        mShared.edit().putBoolean(IS_FIRST_IN, isFirstIn).apply();
    }

    /**
     * @param bid
     * @param xfid
     */
    public void addAccount(String bid, String xfid) {
        if (TextUtils.isEmpty(bid) || TextUtils.isEmpty(xfid)) {
            return;
        }
        String accountsString = mShared.getString(UserAccounts_new, "");
        if (TextUtils.isEmpty(accountsString)) {
            mShared.edit().putString(UserAccounts_new, bid + "," + xfid + ";").commit();
        } else {
            String[] accountsArray = accountsString.substring(0, accountsString.length() - 1).split(";");
            if (!isContainAccount(accountsArray, bid, xfid)) {
                mShared.edit().putString(UserAccounts_new, accountsString + bid + "," + xfid + ";").commit();
            }
        }
    }

    /**
     * 本地缓存的账号是否满了5个
     *
     * @return
     */
    public boolean isAccountOverflow() {
        String[] accountsArray = getAccounts();
        if (accountsArray == null) {
            return false;
        }
        if (accountsArray.length > 5)
            return true;
        else
            return false;
    }

    public void deleteAccount(String bid, String xfid) {
        String[] accountsArray = getAccounts();
        if (accountsArray == null || TextUtils.isEmpty(bid) || TextUtils.isEmpty(xfid)) {
            return;
        }
        ArrayList<String> accountsList = new ArrayList<>();
        for (String item : accountsArray) {
            if (item.equals(bid + "," + xfid)) {
                continue;
            }
            accountsList.add(item);
        }
        if (accountsList.size() == 0) {
            mShared.edit().putString(UserAccounts_new, "").commit();
            mShared.edit().putString(UserAccounts, "").commit();
        } else {
            StringBuilder mBuilder = new StringBuilder();
            for (String itemAccount : accountsList) {
                mBuilder.append(itemAccount).append(";");
            }
            mShared.edit().putString(UserAccounts_new, mBuilder.toString()).commit();
        }
    }

    /**
     * 删除本机上所有缓存的账户
     */
    public void deleteAllAccount() {
        String[] accountsArray = getAccounts();
        if (accountsArray == null) {
            return;
        }
        mShared.edit().putString(UserAccounts_new, "").commit();
    }

    public String[] getAccounts() {
        String accountsString = mShared.getString(UserAccounts_new, "");

        if (TextUtils.isEmpty(accountsString)) {
            String old_accountsString = mShared.getString(UserAccounts, "");
            if (TextUtils.isEmpty(old_accountsString)) {
                return null;
            } else {
                addAccount(MyApplication.getInstance().userId, MyApplication.getInstance().xfid);
                mShared.edit().putString(UserAccounts, "").commit();
                return new String[]{MyApplication.getInstance().userId + "," + MyApplication.getInstance().xfid};
            }

        }
        return accountsString.substring(0, accountsString.length() - 1).split(";");
    }

    public boolean isContainAccount(String[] accountsArray, String bid, String xfid) {
        if (TextUtils.isEmpty(bid) || accountsArray == null || TextUtils.isEmpty(xfid)) {
            return false;
        }
        for (String item : accountsArray) {
            if ((bid + "," + xfid).equals(item)) {
                return true;
            }
        }
        return false;
    }

    public String getUserId() {
        return mShared.getString(UserId, "");
    }

    public String getPhoneNum() {
        return mShared.getString(UserPhoneNum, "");
    }

    public void setUserInfo(UserInfoBean infoBean) {
        if (infoBean == null) {
            return;
        }

        MyApplication.getInstance().account = infoBean;
        MyApplication.getInstance().userId = infoBean.bid;
        MyApplication.getInstance().telphoneNum = infoBean.tel;
        MyApplication.getInstance().userName = infoBean.bname;
        MyApplication.getInstance().eqid = infoBean.eqid;
        MyApplication.getInstance().xfid = infoBean.xfid;
        mShared.edit()
                .putString(UserId, infoBean.bid)
                .putString(EQID, infoBean.eqid)
                .putString(UserPhoneNum, infoBean.tel)
                .putString(USER_NAME, infoBean.bname)
                .putString(XunfeiId, infoBean.xfid)
                .commit();
        MobclickAgent.onProfileSignIn(infoBean.bid);
    }

    public String getUserName() {
        return mShared.getString(USER_NAME, "");
    }

    public void setUserImg(String imgUrl) {
        mShared.edit().putString(UserImg, imgUrl).commit();
    }

    public String getUserImg() {
        return mShared.getString(UserImg, "");
    }

    public void setXunfeiID(String imgUrl) {
        mShared.edit().putString(XunfeiId, imgUrl).commit();
    }

    public String getXunfeiId() {
        return mShared.getString(XunfeiId, "");
    }

    public void loginOut() {
        //String accountHistory = deleteAccount(MyApplication.getInstance().userId, MyApplication.getInstance().xfid);
        MyApplication.getInstance().userId = null;
        mShared.edit().putString(UserId, "").commit();
    }

    public void reset() {
        MyApplication.getInstance().userId = null;
        mShared.edit().clear().commit();
        if (context != null) {
            context.getSharedPreferences(IatSettings.PREFER_NAME, Context.MODE_PRIVATE)
                    .edit().clear().apply();
            context.getSharedPreferences(ConstantData.DOCTOR_MSG, Context.MODE_PRIVATE)
                    .edit().clear().apply();
        }
    }

    public boolean isShowAddGuide() {
        return mShared.getBoolean(Guide_Add_Click, true);
    }

    public void haveShowSignInGuide() {
        mShared.edit().putBoolean(Guide_Sign_In, false).commit();
    }

    public boolean isShowSignInGuide() {
        return mShared.getBoolean(Guide_Sign_In, true);
    }

    public void haveShowAddGuide() {
        mShared.edit().putBoolean(Guide_Add_Click, false).commit();
    }


    public boolean isShowCreateTextGuide() {
        return mShared.getBoolean(Guide_Create_Text, true);
    }

    public void haveShowCreateTextGuide() {
        mShared.edit().putBoolean(Guide_Create_Text, false).commit();
    }

    private static final String SIGN_UP_NAME = "sign_up_name";
    private static final String SIGN_UP_GENDER = "sign_up_gender";
    private static final String SIGN_UP_ADDRESS = "sign_up_address";
    private static final String SIGN_UP_ID_CARD = "sign_up_id_card";
    private static final String SIGN_UP_PHONE = "sign_up_phone";
    private static final String SIGN_UP_PASSWORD = "sign_up_password";
    private static final String SIGN_UP_HEIGHT = "sign_up_height";
    private static final String SIGN_UP_WEIGHT = "sign_up_weight";
    private static final String SIGN_UP_BLOOD_TYPE = "sign_up_blood_type";
    private static final String SIGN_UP_EAT = "sign_up_eat";
    private static final String SIGN_UP_SMOKE = "sign_up_smoke";
    private static final String SIGN_UP_DRINK = "sign_up_drink";
    private static final String SIGN_UP_SPORT = "sign_up_sport";

    public void setSignUpName(String name) {
        mShared.edit().putString(SIGN_UP_NAME, name).apply();
    }

    public String getSignUpName() {
        return mShared.getString(SIGN_UP_NAME, "");
    }

    public void setSignUpGender(String gender) {
        mShared.edit().putString(SIGN_UP_GENDER, gender).apply();
    }

    public String getSignUpGender() {
        return mShared.getString(SIGN_UP_GENDER, "");
    }

    public void setSignUpAddress(String address) {
        mShared.edit().putString(SIGN_UP_ADDRESS, address).apply();
    }

    public String getSignUpAddress() {
        return mShared.getString(SIGN_UP_ADDRESS, "");
    }

    public void setSignUpIdCard(String idCard) {
        mShared.edit().putString(SIGN_UP_ID_CARD, idCard).apply();
    }

    public String getSignUpIdCard() {
        return mShared.getString(SIGN_UP_ID_CARD, makeIdCard());
    }

    public String makeIdCard() {
        Random random = new Random();
        int a = random.nextInt(8999) + 1000;
        int b = random.nextInt(89999999) + 10000000;
        return "00" + a + "1988" + b;
    }

    public void setSignUpPhone(String phone) {
        mShared.edit().putString(SIGN_UP_PHONE, phone).apply();
    }

    public String getSignUpPhone() {
        return mShared.getString(SIGN_UP_PHONE, "");
    }

    public void setSignUpPassword(String password) {
        mShared.edit().putString(SIGN_UP_PASSWORD, password).apply();
    }

    public String getSignUpPassword() {
        return mShared.getString(SIGN_UP_PASSWORD, "");
    }

    public void setSignUpHeight(float height) {
        mShared.edit().putFloat(SIGN_UP_HEIGHT, height).apply();
    }

    public float getSignUpHeight() {
        return mShared.getFloat(SIGN_UP_HEIGHT, 180f);
    }

    public void setSignUpWeight(float weight) {
        mShared.edit().putFloat(SIGN_UP_WEIGHT, weight).apply();
    }

    public float getSignUpWeight() {
        return mShared.getFloat(SIGN_UP_WEIGHT, 65f);
    }

    public void setSignUpBloodType(String bloodType) {
        mShared.edit().putString(SIGN_UP_BLOOD_TYPE, bloodType).apply();
    }

    public String getSignUpBloodType() {
        return mShared.getString(SIGN_UP_BLOOD_TYPE, "A");
    }

    public void setSignUpEat(String eat) {
        mShared.edit().putString(SIGN_UP_EAT, eat).apply();
    }

    public String getSignUpEat() {
        return mShared.getString(SIGN_UP_EAT, "1");
    }

    public void setSignUpSmoke(String smoke) {
        mShared.edit().putString(SIGN_UP_SMOKE, smoke).apply();
    }

    public String getSignUpSmoke() {
        return mShared.getString(SIGN_UP_SMOKE, "3");
    }

    public void setSignUpDrink(String drink) {
        mShared.edit().putString(SIGN_UP_DRINK, drink).apply();
    }

    public String getSignUpDrink() {
        return mShared.getString(SIGN_UP_DRINK, "2");
    }

    public void setSignUpSport(String sport) {
        mShared.edit().putString(SIGN_UP_SPORT, sport).apply();
    }

    public String getSignUpSport() {
        return mShared.getString(SIGN_UP_SPORT, "3");
    }

    private static final String NIM_ACCOUNT = "nim_account";
    private static final String NIM_TOKEN = "nim_token";

    public void setNimAccount(String account) {
        mShared.edit().putString(NIM_ACCOUNT, account).apply();
    }

    public String getNimAccount() {
        return mShared.getString(NIM_ACCOUNT, "");
    }

    public void setNimToken(String token) {
        mShared.edit().putString(NIM_TOKEN, token).apply();
    }

    public String getNimToken() {
        return mShared.getString(NIM_TOKEN, "");
    }

    private static final String EQID = "eq_id";

    public void setEqID(String eqid) {
        mShared.edit().putString(EQID, eqid).commit();
    }

    public String getEqID() {
        return mShared.getString(EQID, "");
    }

    public String getXueyaMac() {
        return mShared.getString(MAC_Xueya, "");
    }

    public void setXueyaMac(String xueyaMac) {
        mShared.edit().putString(MAC_Xueya, xueyaMac).commit();
    }

    public String getXuetangMac() {
        return mShared.getString(MAC_Xuetang, "");
    }

    public void setXuetangMac(String xuetangMac) {
        mShared.edit().putString(MAC_Xuetang, xuetangMac).commit();
    }

    public String getTizhongMac() {
        return mShared.getString(MAC_Tizhong, "");
    }

    public void setTizhongMac(String xuetangMac) {
        mShared.edit().putString(MAC_Tizhong, xuetangMac).commit();
    }

    public String getWenduMac() {
        return mShared.getString(MAC_Wendu, "");
    }

    public void setWenduMac(String wenduMac) {
        mShared.edit().putString(MAC_Wendu, wenduMac).commit();
    }

    public String getSanheyiMac() {
        return mShared.getString(MAC_Sanheyi, "");
    }

    public void setSanheyiMac(String sanheyiMac) {
        mShared.edit().putString(MAC_Sanheyi, sanheyiMac).commit();
    }

    public String getXueyangMac() {
        return mShared.getString(MAC_Xueyang, "");
    }

    public void setXueyangMac(String xueyangMac) {
        mShared.edit().putString(MAC_Xueyang, xueyangMac).commit();
    }

    public String getBreathHomeMac() {
        return mShared.getString(MAC_BreathHome, "");
    }

    public void setBreathHomeMac(String breathHomeMac) {
        mShared.edit().putString(MAC_BreathHome, breathHomeMac).commit();
    }

    public String getXinDianMac() {
        return mShared.getString(MAC_Xindian, "");
    }

    public void setXinDianMac(String xindianMac) {
        mShared.edit().putString(MAC_Xindian, xindianMac).commit();
    }

    public void setSex(String sex) {
        mShared.edit().putString("user_sex", sex).commit();
    }

    public String getSex() {
        return mShared.getString("user_sex", "");
    }

    public void setUserPhoto(String user_photo) {
        mShared.edit().putString("userPhoto", user_photo).commit();
    }

    public String getUserPhoto() {
        return mShared.getString("userPhoto", "");
    }

    public void setUserAge(String age) {
        mShared.edit().putString("user_age", age).commit();
    }

    public String getUserAge() {
        return mShared.getString("user_age", "0");
    }

    public void setUserHeight(String height) {
        mShared.edit().putString("user_height", height).commit();
    }

    public String getUserHeight() {
        return mShared.getString("user_height", "175");
    }

    /**
     * 讯飞组id
     *
     * @param groupid
     */

    public void setGroupId(String groupid) {
        mShared.edit().putString("group_id", groupid).commit();
    }

    public String getGroupId() {
        return mShared.getString("group_id", "");
    }

    /**
     * 讯飞组创建时候传入的xfid
     *
     * @param xfid
     */
    public void setGroupFirstXfid(String xfid) {
        mShared.edit().putString("group_first_xfid", xfid).commit();
    }

    public String getGroupFirstXfid() {
        return mShared.getString("group_first_xfid", "");
    }

    public void setHealthScore(int fenshuNum) {
        mShared.edit().putInt("health_score", fenshuNum).commit();
    }

    public int getHealthScore() {
        return mShared.getInt("health_score", 0);
    }

    public void setMeasureXueyaFirst(boolean isFirst) {
        mShared.edit().putBoolean("measure_xueya_first", isFirst).commit();
    }

    public boolean getMeasureXueyaFirst() {
        return mShared.getBoolean("measure_xueya_first", true);
    }

    public void setMeasureTiwenFirst(boolean isFirst) {
        mShared.edit().putBoolean("measure_tiwen_first", isFirst).commit();
    }

    public boolean getMeasureTiwenFirst() {
        return mShared.getBoolean("measure_tiwen_first", true);
    }

    public void setMeasureXuetangFirst(boolean isFirst) {
        mShared.edit().putBoolean("measure_xuetang_first", isFirst).commit();
    }

    public boolean getMeasureXuetangFirst() {
        return mShared.getBoolean("measure_xuetang_first", true);
    }

    public void setMeasureXueyangFirst(boolean isFirst) {
        mShared.edit().putBoolean("measure_xueyang_first", isFirst).commit();
    }

    public boolean getMeasureXueyangFirst() {
        return mShared.getBoolean("measure_xueyang_first", true);
    }

    public void setMeasureXindianFirst(boolean isFirst) {
        mShared.edit().putBoolean("measure_xindian_first", isFirst).commit();
    }

    public boolean getMeasureXindianFirst() {
        return mShared.getBoolean("measure_xindian_first", true);
    }

    public void setMeasureSanheyiFirst(boolean isFirst) {
        mShared.edit().putBoolean("measure_sanheyi_first", isFirst).commit();
    }

    public boolean getMeasureSanheyiFirst() {
        return mShared.getBoolean("measure_sanheyi_first", true);
    }
}
