package com.example.han.referralproject.network;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.example.han.referralproject.BuildConfig;
import com.example.han.referralproject.application.MyApplication;
import com.example.han.referralproject.bean.AllDoctor;
import com.example.han.referralproject.bean.AlreadyYuyue;
import com.example.han.referralproject.bean.BUA;
import com.example.han.referralproject.bean.BloodOxygenHistory;
import com.example.han.referralproject.bean.BloodPressureHistory;
import com.example.han.referralproject.bean.BloodSugarHistory;
import com.example.han.referralproject.bean.CholesterolHistory;
import com.example.han.referralproject.bean.ClueInfoBean;
import com.example.han.referralproject.bean.ContractInfo;
import com.example.han.referralproject.bean.DataInfoBean;
import com.example.han.referralproject.bean.DiseaseResult;
import com.example.han.referralproject.bean.Doctor;
import com.example.han.referralproject.bean.ECGHistory;
import com.example.han.referralproject.bean.HeartRateHistory;
import com.example.han.referralproject.bean.MeasureResult;
import com.example.han.referralproject.bean.MessagesOfCenter;
import com.example.han.referralproject.bean.MonthlyReport;
import com.example.han.referralproject.bean.PulseHistory;
import com.example.han.referralproject.bean.RobotAmount;
import com.example.han.referralproject.bean.SymptomBean;
import com.example.han.referralproject.bean.SymptomResultBean;
import com.example.han.referralproject.bean.TemperatureHistory;
import com.example.han.referralproject.bean.UserInfo;
import com.example.han.referralproject.bean.VersionInfoBean;
import com.example.han.referralproject.bean.WeeklyReport;
import com.example.han.referralproject.bean.WeightHistory;
import com.example.han.referralproject.bean.XfGroupInfo;
import com.example.han.referralproject.bean.YuYueInfo;
import com.example.han.referralproject.bean.YzInfoBean;
import com.example.han.referralproject.children.model.SheetModel;
import com.example.han.referralproject.children.model.SongModel;
import com.example.han.referralproject.health.model.WeekReportModel;
import com.example.han.referralproject.radio.RadioEntity;
import com.example.han.referralproject.recyclerview.Docter;
import com.example.han.referralproject.recyclerview.OnlineTime;
import com.example.han.referralproject.shopping.Goods;
import com.example.han.referralproject.shopping.Order;
import com.example.han.referralproject.shopping.Orders;
import com.example.han.referralproject.util.LocalShared;
import com.example.han.referralproject.util.Utils;
import com.example.han.referralproject.video.VideoEntity;
import com.google.gson.reflect.TypeToken;
import com.gzq.lib_core.base.Box;
import com.gzq.lib_core.bean.UserInfoBean;
import com.medlink.danbogh.cache.Repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class NetworkApi {
//        public static final String BasicUrl = "http://116.62.36.12:8080";
//    public static final String BasicUrl = "http://118.31.238.207:8080";
//    public static final String BasicUrl = "http://47.96.98.60:8060";
    public static final String BasicUrl = BuildConfig.SERVER_ADDRESS;
//    public static final String BasicUrl = "http://192.168.200.103:8080";//孙高峰

    //  public static final String BasicUrl="http://192.168.200.111:8080";//韩琦本地

//    public static final String BasicUrl = "http://192.168.200.157:8080";//文博本地


    public static final String LoginUrl = BasicUrl + "/ZZB/login/applogin";
    public static final String RegisterUrl = BasicUrl + "/ZZB/br/appadd";
    public static final String AddMhUrl = BasicUrl + "/ZZB/br/mhrecord";
    public static final String ClueUrl = BasicUrl + "/ZZB/br/selOneUserClueAll";
    public static final String BindDocUrl = BasicUrl + "/ZZB/br/qianyue";
    public static final String GetAllSymUrl = BasicUrl + "/ZZB/bl/selAllSym";
    //次层病症和首层诊断结果
    public static final String AnalyseUrl = BasicUrl + "/ZZB/bl/selcon";
    public static final String GetYZUrl = BasicUrl + "/ZZB/bl/selYzAndTime";
    public static final String GetVersionUrl = BasicUrl + "/ZZB/vc/selone";
    public static final String UploadDataUrl = BasicUrl + "/ZZB/bl/doaddbl";
    public static final String CHARGE_URL = BasicUrl + "/ZZB/eq/koufei";
    public static final String PAY_URL = BasicUrl + "/ZZB/br/chongzhi";
    public static final String DOCTOR_URL = BasicUrl + "/ZZB/docter/search_OneDocter";
    public static final String DOCTOR_INFO_URL = BasicUrl + "/ZZB/docter/sel_one_doctor_con";
    public static final String PERSON_URL = BasicUrl + "/ZZB/br/selOneUser_con";
    public static final String GetInfo_URL = BasicUrl + "/ZZB/br/selMoreUser";
    public static final String PERSON_AMOUNT = BasicUrl + "/ZZB/eq/eq_amount";
    public static final String YUYUE_URL = BasicUrl + "/ZZB/bl/insertReserve";
    public static final String YUYUE_URL_INFO = BasicUrl + "/ZZB/bl/selAllreserveByDoidAndUserid";
    public static final String YUYUE_URL_HISTORY = BasicUrl + "/ZZB/bl/selReserveByDoidAndUseridAndState";


    public static final String YUYUE_ALREADY = BasicUrl + "/ZZB/bl/selReserveStart_time";

    public static final String YUYUE_URL_CANCEL = BasicUrl + "/ZZB/bl/delReserveByRid";
    public static final String TOKEN_URL = BasicUrl + "/ZZB/br/seltoken";
    public static final String RETURN_IMAGE_URL = BasicUrl + "/ZZB/br/upUser_photo";
    public static final String ADD_EAT_MEDICAL_URL = BasicUrl + "/ZZB/br/addeatmod";
    public static final String GET_CONTRACT_INFO = BasicUrl + "/ZZB/docter/docter_user";

    public static final String GET_MY_BASE_DATA = BasicUrl + "/ZZB/br/selOneUserEverything";

    public static final String DOCTER_APPRAISER = BasicUrl + "/ZZB/pj/insert";

    public static final String GOODS_INSERT = BasicUrl + "/ZZB/order/insert_one";

    public static final String UPDATE_STATUS = BasicUrl + "/ZZB/bl/app_update_reserve_state";


    public static final String DOCTER_LIST = BasicUrl + "/ZZB/docter/seldoctors";

    public static final String ONLINE_DOCTER_LIST = BasicUrl + "/ZZB/docter/search_online_status";

    public static final String ONLINE_DOCTER_ZIXUN = BasicUrl + "/ZZB/docter/online_consulting";


    public static final String ORDER_INFO = BasicUrl + "/ZZB/order/panding_pay";

    public static final String PAY_STATUS = BasicUrl + "/ZZB/order/pay_pro";

    public static final String PAY_CANCEL = BasicUrl + "/ZZB/order/delivery_del";

    public static final String ORDER_LIST = BasicUrl + "/ZZB/order/one_more_orders";

    public static final String Get_HealthRecord = BasicUrl + "/ZZB/br/cl_data";//正式服务器
    //    public static final String Get_HealthRecord=BasicUrl+"/ZZB/br/cl";
    //全部医生
    public static final String Get_AllDotor = BasicUrl + "/ZZB/docter/seldoctors";
    public static final String FIND_ACCOUNT = BasicUrl + "/ZZB/acc/sel_account";
    public static final String SET_PASSWORD = BasicUrl + "/ZZB/acc/update_account_pwd";


    //修改个人基本信息
    public static final String Alert_Basedata = BasicUrl + "/ZZB/br/update_user_onecon";
    public static final String Get_jibing = BasicUrl + "/ZZB/bl/selSugByBname";
    public static final String IS_PHONE_REGISTERED = BasicUrl + "/ZZB/login/tel_isClod";

    public static final String EQ_PRE_AMOUNT = BasicUrl + "/ZZB/eq/selPaidAmountByEqid";
    public static final String CANCEL_CONTRACT = BasicUrl + "/ZZB/br/updateUserState";
    public static final String CHECK_CONTRACT = BasicUrl + "/ZZB/eq/selCountPaidAmountByEqid";

    public static final String GET_VIDEO_LIST = BasicUrl + "/ZZB/vc/selAllUpload";
    public static final String GET_CODE = BasicUrl + "/ZZB/br/GainCode";

    public static final String GET_FM = BasicUrl + "/ZZB/rep/selSomeImitate";
    public static final String Add_Group = BasicUrl + "/ZZB/xf/insert_group_record";
    public static final String Change_Group_Status = BasicUrl + "/ZZB/xf/update_group_record";

    //    public static final String Query_Group=BasicUrl+"/ZZB/xf/select_group_record";
    public static final String Query_Group_118 = "http://118.31.238.207:8080/ZZB/xf/select_group_record";
    public static final String Query_Group_116 = "http://116.62.36.12:8080/ZZB/xf/select_group_record";
    public static final String Get_Week_or_Month_Report = BasicUrl + "/ZZB/ai/sel";
    public static final String Get_Eat_And_Sport = BasicUrl + "/ZZB/ai//EatAndSport";

    public static final String Query_Group = BasicUrl + "/ZZB/xf/select_group_record";

    public static final String GET_SHEET_LIST = BasicUrl + "/ZZB/rep/sel_music_danforapp";

    private static final String GET_SONG_LIST = BasicUrl + "/ZZB/rep/selSomeImitate";

    public static final String Get_Message = BasicUrl + "/ZZB/xf/select_tuisong";
    public static final String Get_Week_Report = BasicUrl + "/AI/ai/selmap";

    public static final String POST_HEAlTH_DIARY = BasicUrl + "/ZZB/ai/insert_influence";

    public static final String POST_TEL_MESSAGE = BasicUrl + "/ZZB/br/br_teltixing";

    public static final String AUTH_IS_REGISTERED_ID_CARD = BasicUrl + "/ZZB/login/user_sfz_login";

    public static void isRegisteredByIdCard(String idCard, NetworkManager.SuccessCallback<UserInfoBean> successCallback,
                                            NetworkManager.FailedCallback failedCallback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("sfz", idCard);
        NetworkManager.getInstance().postResultClass(AUTH_IS_REGISTERED_ID_CARD, params, UserInfoBean.class, successCallback, failedCallback);
    }

    public static void getEatAndSport(String userId, NetworkManager.SuccessCallback<WeeklyReport> successCallback,
                                      NetworkManager.FailedCallback failedCallback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("userid", userId);
        params.put("state", "1");
        NetworkManager.getInstance().getResultClass(Get_Eat_And_Sport, params, WeekReportModel.class, successCallback, failedCallback);
    }

    public static void postTelMessage(
            String tel,
            String name,
            NetworkManager.SuccessCallback<Object> successCallback,
            NetworkManager.FailedCallback failedCallback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("tel", tel);
        params.put("name", name);
        NetworkManager.getInstance().postResultClass(
                POST_TEL_MESSAGE,
                params,
                Object.class,
                successCallback,
                failedCallback
        );
    }

    public static void postHealthDiary(
            double salt,
            int sports,
            int drink,
            NetworkManager.SuccessCallback<Object> successCallback,
            NetworkManager.FailedCallback failedCallback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("userid", MyApplication.getInstance().userId);
        params.put("na", String.valueOf(salt));
        params.put("sports", String.valueOf(sports));
        params.put("drink", String.valueOf(drink));
        NetworkManager.getInstance().postResultClass(
                POST_HEAlTH_DIARY,
                params,
                Object.class,
                successCallback,
                failedCallback
        );
    }

    public static void getChildEduSheetList(
            int page,
            int limit,
            NetworkManager.SuccessCallback<List<SheetModel>> successCallback,
            NetworkManager.FailedCallback failedCallback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(page));
        params.put("limit", String.valueOf(limit));
        NetworkManager.getInstance().getResultClass(
                GET_SHEET_LIST,
                params,
                new TypeToken<List<SheetModel>>() {
                }.getType(),
                successCallback,
                failedCallback
        );
    }

    public static void getChildEduSongListBySheetId(
            int page,
            int limit,
            int sheetId,
            int type,
            String singer,
            NetworkManager.SuccessCallback<List<SongModel>> successCallback,
            NetworkManager.FailedCallback failedCallback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", String.valueOf(page));
        params.put("limit", String.valueOf(limit));
        params.put("mid", String.valueOf(sheetId));
        params.put("type", String.valueOf(type));
        params.put("wr", singer);
        NetworkManager.getInstance().getResultClass(
                GET_SONG_LIST,
                params,
                new TypeToken<List<SongModel>>() {
                }.getType(),
                successCallback,
                failedCallback);
    }


    public static void getFM(
            String type,
            String page,
            String limit,
            NetworkManager.SuccessCallback<List<RadioEntity>> successCallback,
            NetworkManager.FailedCallback failedCallback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("type", type);
        params.put("page", page);
        params.put("limit", limit);
        params.put("mid", "0");
        NetworkManager.getInstance().getResultClass(GET_FM, params, new TypeToken<List<RadioEntity>>() {
        }.getType(), successCallback, failedCallback);
    }

    public static void getCode(
            String phone,
            NetworkManager.SuccessCallback<String> successCallback,
            NetworkManager.FailedCallback failedCallback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("mobile", phone);
        NetworkManager.getInstance().getResultString(GET_CODE, params, successCallback, failedCallback);
    }


    public static void getVideoList(
            int category,
            String version,
            String flag,
            int page,
            int pageSize,
            NetworkManager.SuccessCallback<List<VideoEntity>> successCallback,
            NetworkManager.FailedCallback failedCallback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("tag1", String.valueOf(category));
        params.put("tag2", version);
        params.put("flag", flag);
        params.put("page", String.valueOf(page));
        params.put("pagesize", String.valueOf(pageSize));
        NetworkManager.getInstance().postResultClass(GET_VIDEO_LIST, params, new TypeToken<List<VideoEntity>>() {
        }.getType(), successCallback, failedCallback);
    }

    public static void cancelContract(
            String bid,
            NetworkManager.SuccessCallback<Object> successCallback,
            NetworkManager.FailedCallback failedCallback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("bid", bid);
        NetworkManager.getInstance().postResultClass(CANCEL_CONTRACT, params, Object.class, successCallback, failedCallback);
    }


    public static void checkNotContract(String bid, NetworkManager.SuccessCallback<Object> successCallback, NetworkManager.FailedCallback failedCallback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("bid", bid);
        NetworkManager.getInstance().postResultClass(CHECK_CONTRACT, params, Object.class, successCallback, failedCallback);
    }

    public static void getEqPreAmount(NetworkManager.SuccessCallback<RobotAmount> successCallback, NetworkManager.FailedCallback failedCallback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("eqid", Utils.getDeviceId());
        NetworkManager.getInstance().postResultClass(EQ_PRE_AMOUNT, params, RobotAmount.class, successCallback, failedCallback);
    }

    public static void canRegister(
            String phone,
            String state,
            NetworkManager.SuccessCallback<Object> successCallback,
            NetworkManager.FailedCallback failedCallback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("tel", phone);
        params.put("state", state);
        NetworkManager.getInstance().postResultClass(IS_PHONE_REGISTERED, params, Object.class, successCallback, failedCallback);
    }


    public static final String GOODS_LIST = BasicUrl + "/ZZB/order/OneType_state";


    public static void login(String phoneNum, String pwd, NetworkManager.SuccessCallback<UserInfoBean> listener, NetworkManager.FailedCallback failedCallback) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("username", phoneNum);
        paramsMap.put("password", pwd);
        NetworkManager.getInstance().postResultClass(LoginUrl, paramsMap, UserInfoBean.class, listener, failedCallback);
    }


    public static void PayInfo(String eqid, String bba, String time, String bid, NetworkManager.SuccessCallback<String> listener, NetworkManager.FailedCallback failedCallback) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("eqid", eqid);
        paramsMap.put("bba", bba);
        paramsMap.put("time", time);
        paramsMap.put("bid", bid);
        NetworkManager.getInstance().postResultClass(PAY_URL, paramsMap, String.class, listener, failedCallback);
    }

    public static void DoctorInfo(String bid, NetworkManager.SuccessCallback<Doctor> listener, NetworkManager.FailedCallback failedCallback) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("bid", bid);

        NetworkManager.getInstance().postResultClass(DOCTOR_URL, paramsMap, Doctor.class, listener, failedCallback);
    }

    public static void getDoctorInfo(String doctorId, NetworkManager.SuccessCallback<Doctor> listener, NetworkManager.FailedCallback failedCallback) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("docterid", doctorId);
        NetworkManager.getInstance().postResultClass(DOCTOR_INFO_URL, paramsMap, Doctor.class, listener, failedCallback);
    }

    public static void PersonInfo(String bid, final NetworkManager.SuccessCallback<UserInfo> listener, final NetworkManager.FailedCallback failedCallback) {
        if (TextUtils.isEmpty(bid)) {
            return;
        }
        String netless = LocalShared.getInstance(Box.getApp()).getString("netless");
        if (!TextUtils.isEmpty(netless)) {
            Repository repository = Repository.getInstance(Box.getApp());
            repository.current()
                    .map(new Function<UserInfoBean, UserInfo>() {
                        @Override
                        public UserInfo apply(UserInfoBean data) throws Exception {
                            UserInfo user = new UserInfo();
                            user.doctername = "";
                            user.sfz = data.sfz;
                            user.sex = data.sex;
                            user.eqid = data.eqid;
                            user.bname = data.bname;
                            user.dz = data.dz;
                            user.tel = data.tel;
                            user.categoryname = "";
                            user.mh = data.mh;
                            user.state = data.state;
                            user.bid = data.bid;
                            user.age = data.age;
                            user.categoryid = data.categoryid;
                            user.user_photo = data.userPhoto;
                            user.amount = "0.00";
                            user.xfid = data.xfid;
                            return user;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<UserInfo>() {
                        @Override
                        public void accept(UserInfo user) throws Exception {
                            if (listener != null) {
                                listener.onSuccess(user);
                            }
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            if (failedCallback != null) {
                                failedCallback.onFailed("请重新登录");
                            }
                        }
                    });
        }

        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("bid", bid);

        NetworkManager.getInstance().postResultClass(PERSON_URL, paramsMap, UserInfo.class, listener, failedCallback);
    }

    public static void Person_Amount(String eqid, NetworkManager.SuccessCallback<RobotAmount> listener, NetworkManager.FailedCallback failedCallback) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("eqid", eqid);

        NetworkManager.getInstance().postResultClass(PERSON_AMOUNT, paramsMap, RobotAmount.class, listener, failedCallback);
    }

    public static void YuYue(String start_time, String end_time, String userid, String docterid, NetworkManager.SuccessCallback<String> listener, NetworkManager.FailedCallback failedCallback) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("start_time", start_time);
        paramsMap.put("end_time", end_time);
        paramsMap.put("userid", userid + "");
        paramsMap.put("docterid", docterid + "");
        NetworkManager.getInstance().postResultClass(YUYUE_URL, paramsMap, String.class, listener, failedCallback);
    }


    public static void YuYue_info(String userid, String docterid, NetworkManager.SuccessCallback<ArrayList<YuYueInfo>> listener, NetworkManager.FailedCallback failedCallback) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("userid", userid);
        paramsMap.put("docterid", docterid);
        NetworkManager.getInstance().postResultClass(YUYUE_URL_INFO, paramsMap, new TypeToken<ArrayList<YuYueInfo>>() {
        }.getType(), listener, failedCallback);
    }


    public static void YuYue_history(String userid, String docterid, int state, int page, int limit, NetworkManager.SuccessCallback<ArrayList<YuYueInfo>> listener, NetworkManager.FailedCallback failedCallback) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("userid", userid);
        paramsMap.put("docterid", docterid);
        paramsMap.put("state", state + "");
        paramsMap.put("page", page + "");
        paramsMap.put("limit", limit + "");

        NetworkManager.getInstance().postResultClass(YUYUE_URL_HISTORY, paramsMap, new TypeToken<ArrayList<YuYueInfo>>() {
        }.getType(), listener, failedCallback);
    }


    public static void YuYue_cancel(String rid, NetworkManager.SuccessCallback<String> listener, NetworkManager.FailedCallback failedCallback) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("rid", rid);
        NetworkManager.getInstance().postResultClass(YUYUE_URL_CANCEL, paramsMap, String.class, listener, failedCallback);
    }

    public static void YuYue_already(String docterid, NetworkManager.SuccessCallback<ArrayList<AlreadyYuyue>> listener, NetworkManager.FailedCallback failedCallback) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("docterid", docterid);

        NetworkManager.getInstance().postResultClass(YUYUE_ALREADY, paramsMap, new TypeToken<ArrayList<AlreadyYuyue>>() {
        }.getType(), listener, failedCallback);
    }


    public static void update_status(String rid, String state, NetworkManager.SuccessCallback<String> listener, NetworkManager.FailedCallback failedCallback) {
        Map<String, String> paramsMap = new HashMap<>();

        paramsMap.put("rid", rid);
        paramsMap.put("state", state);

        NetworkManager.getInstance().postResultString(UPDATE_STATUS, paramsMap, listener, failedCallback);
    }


    public static void get_token(NetworkManager.SuccessCallback<String> listener, NetworkManager.FailedCallback failedCallback) {
        Map<String, String> paramsMap = new HashMap<>();

        NetworkManager.getInstance().postResultString(TOKEN_URL, paramsMap, listener, failedCallback);
    }

    public static void appraise(String docterid, String bid, String content, int score, String time, int doid,
                                NetworkManager.SuccessCallback<String> listener, NetworkManager.FailedCallback failedCallback) {
        Map<String, String> paramsMap = new HashMap<>();

        paramsMap.put("docterid", docterid + "");
        paramsMap.put("bid", bid + "");
        paramsMap.put("content", content);
        paramsMap.put("score", score + "");
        paramsMap.put("time", time);
        paramsMap.put("daid", String.valueOf(doid));

        NetworkManager.getInstance().postResultString(DOCTER_APPRAISER, paramsMap, listener, failedCallback);
    }

    public static void insertGoods(String goodsname, String goodsimage, double goodsprice, int goodstate, NetworkManager.SuccessCallback<String> listener, NetworkManager.FailedCallback failedCallback) {
        Map<String, String> paramsMap = new HashMap<>();

        paramsMap.put("goodsname", goodsname);
        paramsMap.put("goodsimage", goodsimage);
        paramsMap.put("goodsprice", goodsprice + "");
        paramsMap.put("goodstate", goodstate + "");

        NetworkManager.getInstance().postResultString(GOODS_INSERT, paramsMap, listener, failedCallback);
    }


    public static void return_imageUrl(String user_photo, String bid, String xfid, NetworkManager.SuccessCallback<Object> listener, NetworkManager.FailedCallback failedCallback) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("user_photo", user_photo);
        paramsMap.put("bid", bid);
        paramsMap.put("xfid", xfid);
        NetworkManager.getInstance().postResultString(RETURN_IMAGE_URL, paramsMap, listener, failedCallback);
    }


    public static void doctor_list(int start, int limit, NetworkManager.SuccessCallback<ArrayList<Docter>> listener, NetworkManager.FailedCallback failedCallback) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("start", start + "");
        paramsMap.put("limit", limit + "");
        NetworkManager.getInstance().postResultClass(DOCTER_LIST, paramsMap, new TypeToken<ArrayList<Docter>>() {
        }.getType(), listener, failedCallback);
    }

    public static void onlinedoctor_list(int online_status, String doctername, int page, int pagesize, NetworkManager.SuccessCallback<ArrayList<Docter>> listener, NetworkManager.FailedCallback failedCallback) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("online_status", online_status + "");
        paramsMap.put("doctername", doctername);
        paramsMap.put("page", page + "");
        paramsMap.put("pagesize", pagesize + "");

        NetworkManager.getInstance().postResultClass(ONLINE_DOCTER_LIST, paramsMap, new TypeToken<ArrayList<Docter>>() {
        }.getType(), listener, failedCallback);
    }

    public static void goods_list(int state, NetworkManager.SuccessCallback<ArrayList<Goods>> listener, NetworkManager.FailedCallback failedCallback) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("state", state + "");

        NetworkManager.getInstance().postResultClass(GOODS_LIST, paramsMap, new TypeToken<ArrayList<Goods>>() {
        }.getType(), listener, failedCallback);
    }


    public static void onlinedoctor_zixun(String docterid, String userid, int state, NetworkManager.SuccessCallback<OnlineTime> listener, NetworkManager.FailedCallback failedCallback) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("docterid", docterid + "");
        paramsMap.put("userid", userid + "");
        paramsMap.put("state", state + "");

        NetworkManager.getInstance().postResultClass(ONLINE_DOCTER_ZIXUN, paramsMap, new TypeToken<OnlineTime>() {
        }.getType(), listener, failedCallback);
    }


    public static void order_info(String userid, String eqid, String articles, String number, String price, String photo, String time, NetworkManager.SuccessCallback<Order> listener, NetworkManager.FailedCallback failedCallback) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("userid", userid);
        paramsMap.put("eqid", eqid);
        paramsMap.put("articles", articles);
        paramsMap.put("number", number);
        paramsMap.put("price", price);
        paramsMap.put("photo", photo);
        paramsMap.put("time", time);

        NetworkManager.getInstance().postResultClass(ORDER_INFO, paramsMap, Order.class, listener, failedCallback);
    }

    public static void pay_status(String userid, String eqid, String orderid, NetworkManager.SuccessCallback<String> listener, NetworkManager.FailedCallback failedCallback) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("userid", userid);
        paramsMap.put("eqid", eqid);
        paramsMap.put("orderid", orderid);
        NetworkManager.getInstance().postResultString(PAY_STATUS, paramsMap, listener, failedCallback);
    }

    public static void pay_cancel(String pay_state, String delivery_state, String display_state, String orderid, NetworkManager.SuccessCallback<String> listener, NetworkManager.FailedCallback failedCallback) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("pay_state", pay_state);
        paramsMap.put("delivery_state", delivery_state);
        paramsMap.put("display_state", display_state);
        paramsMap.put("orderid", orderid);
        NetworkManager.getInstance().postResultString(PAY_CANCEL, paramsMap, listener, failedCallback);
    }


    public static void order_list(String pay_state, String delivery_state, String display_state, String bname, String page, String limit, NetworkManager.SuccessCallback<ArrayList<Orders>> listener, NetworkManager.FailedCallback failedCallback) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("pay_state", pay_state);
        paramsMap.put("delivery_state", delivery_state);
        paramsMap.put("display_state", display_state);
        paramsMap.put("bname", bname);
        paramsMap.put("page", page);
        paramsMap.put("limit", limit);

        NetworkManager.getInstance().postResultClass(ORDER_LIST, paramsMap, new TypeToken<ArrayList<Orders>>() {
        }.getType(), listener, failedCallback);
    }


    public static void registerUser(
            String name,
            String sex,
            String address,
            String sfz,
            String telephone,
            String pwd,
            float height,
            float weight,
            String bloodType,
            String eat,
            String smoke,
            String drink,
            String sports,
            NetworkManager.SuccessCallback<UserInfoBean> listener,
            NetworkManager.FailedCallback failedCallback) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("age", "50");
        paramsMap.put("bname", name);
        paramsMap.put("sex", sex);
        paramsMap.put("eqid", Utils.getDeviceId());
        paramsMap.put("tel", telephone);
        paramsMap.put("pwd", pwd);
        paramsMap.put("dz", address);
        paramsMap.put("sfz", sfz);
        paramsMap.put("height", String.valueOf(height));
        paramsMap.put("weight", String.valueOf(weight));
        paramsMap.put("blood_type", bloodType);
        paramsMap.put("eating_habits", eat);
        paramsMap.put("smoke", smoke);
        paramsMap.put("drink", drink);
        paramsMap.put("exercise_habits", sports);
        NetworkManager.getInstance().postResultClass(RegisterUrl, paramsMap, UserInfoBean.class, listener, failedCallback);
    }

    /**
     * 获取所有症状
     *
     * @param callback
     */
    public static void getAllSym(NetworkManager.SuccessCallback<ArrayList<SymptomBean>> callback) {
        NetworkManager.getInstance().getResultClass(GetAllSymUrl, null, new TypeToken<ArrayList<SymptomBean>>() {
        }.getType(), callback);
    }

    public static void getAllUsers(String accounts, NetworkManager.SuccessCallback<ArrayList<UserInfoBean>> callback) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("p", accounts);
        NetworkManager.getInstance().getResultClass(GetInfo_URL, paramsMap, new TypeToken<ArrayList<UserInfoBean>>() {
        }.getType(), callback);
    }

    /**
     * 获取下一层病症和结果
     *
     * @param params
     * @param callback
     */
    public static void analyseSym(String params, NetworkManager.SuccessCallback<SymptomResultBean> callback) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("parameter", params);
        NetworkManager.getInstance().getResultClass(AnalyseUrl, paramsMap, SymptomResultBean.class, callback);
    }

    public static void clueNotify(NetworkManager.SuccessCallback<ArrayList<ClueInfoBean>> callback) {
        if (TextUtils.isEmpty(MyApplication.getInstance().userId)) {
            return;
        }
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("bid", MyApplication.getInstance().userId);
        NetworkManager.getInstance().getResultClass(ClueUrl, paramsMap, new TypeToken<ArrayList<ClueInfoBean>>() {
        }.getType(), callback);
    }

    public static void getYzList(NetworkManager.SuccessCallback<ArrayList<YzInfoBean>> callback) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("userid", MyApplication.getInstance().userId);
        NetworkManager.getInstance().getResultClass(GetYZUrl, paramsMap, new TypeToken<ArrayList<YzInfoBean>>() {
        }.getType(), callback);
    }

    public static void setUserMh(String mh, NetworkManager.SuccessCallback<String> callback, NetworkManager.FailedCallback failedCallback) {
        if (TextUtils.isEmpty(MyApplication.getInstance().userId)) {
            return;
        }
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("bid", MyApplication.getInstance().userId);
        paramsMap.put("mh", mh);
        NetworkManager.getInstance().postResultString(AddMhUrl, paramsMap, callback, failedCallback);
    }

    public static void bindDoctor(String bid, int doctorId, NetworkManager.SuccessCallback<String> callback) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("bid", bid);
        paramsMap.put("doid", String.valueOf(doctorId));
        NetworkManager.getInstance().postResultString(BindDocUrl, paramsMap, callback);
    }

    public static void getVersionInfo(NetworkManager.SuccessCallback<VersionInfoBean> callback, NetworkManager.FailedCallback failedCallback) {
        ApplicationInfo appInfo = null;
        String msg = "";
        try {
            appInfo = Box.getApp().getPackageManager()
                    .getApplicationInfo(Box.getApp().getPackageName(), PackageManager.GET_META_DATA);
            msg = appInfo.metaData.getString("com.gcml.version");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("vname", msg);
        NetworkManager.getInstance().getResultClass(GetVersionUrl, paramsMap, VersionInfoBean.class, callback, failedCallback);
    }

    public static void postData(DataInfoBean info, final NetworkManager.SuccessCallback<MeasureResult> successCallback, final NetworkManager.FailedCallback failedCallback) {
        if (info == null) {
            return;
        }
        String netless = LocalShared.getInstance(Box.getApp()).getString("netless");
        String noNetless = LocalShared.getInstance(Box.getApp()).getString("noNetless");
        if (TextUtils.isEmpty(noNetless)) {
            if (!TextUtils.isEmpty(netless)) {
                Repository repository = Repository.getInstance(Box.getApp());
                repository.measureData(MyApplication.getInstance().userId, info)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<DataInfoBean>() {
                            @Override
                            public void accept(DataInfoBean data) throws Exception {
                                if (successCallback != null) {
                                    successCallback.onSuccess(null);
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                throwable.printStackTrace();
                                if (failedCallback != null) {
                                    failedCallback.onFailed("unknown error");
                                }
                            }
                        });
                return;
            }
        }
        NetworkManager.getInstance().postResultClass(UploadDataUrl, info.getParamsMap(), MeasureResult.class, successCallback, failedCallback);
//        NetworkManager.getInstance().postResultString(UploadDataUrl, info.getParamsMap(), successCallback);
    }


    public static void charge(int minute, int doctorId, String bId,
                              NetworkManager.SuccessCallback<String> successCallback,
                              NetworkManager.FailedCallback failedCallback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("docterid", String.valueOf(doctorId));
        params.put("eqid", Utils.getDeviceId());
        params.put("time", String.valueOf(minute));
        params.put("bid", bId);
        NetworkManager.getInstance().postResultString(CHARGE_URL, params, successCallback, failedCallback);
    }

    public static void addEatMedicalRecord(
            String userName,
            String content,
            String state,
            NetworkManager.SuccessCallback<Object> successCallback,
            NetworkManager.FailedCallback failedCallback) {
        HashMap<String, String> params = new HashMap<>();
        if (TextUtils.isEmpty(userName)) {
            if (failedCallback != null) {
                failedCallback.onFailed("请重新登录");
            }
            return;
        }
        params.put("username", userName);
        params.put("jl", content);
        params.put("time", String.valueOf(Calendar.getInstance().getTimeInMillis()));
        params.put("state", state);
        NetworkManager.getInstance().postResultClass(ADD_EAT_MEDICAL_URL, params, Object.class, successCallback, failedCallback);
    }

    public static void getContractInfo(
            String doctorId,
            NetworkManager.SuccessCallback<ContractInfo> successCallback,
            NetworkManager.FailedCallback failedCallback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("userid", MyApplication.getInstance().userId);
        params.put("docterid", doctorId);
        NetworkManager.getInstance().getResultClass(GET_CONTRACT_INFO, params, ContractInfo.class, successCallback, failedCallback);

    }

    /**
     * 获取个人的基本信息
     *
     * @param successCallback
     * @param failedCallback
     */
    public static void  getMyBaseData(
            NetworkManager.SuccessCallback<UserInfoBean> successCallback, NetworkManager.FailedCallback failedCallback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("bid", MyApplication.getInstance().userId);
        NetworkManager.getInstance().getResultClass(GET_MY_BASE_DATA, params, UserInfoBean.class, successCallback, failedCallback);
    }

    /**
     * 获取体温历史数据
     *
     * @param successCallback
     */


    public static void getTemperatureHistory(String start, String end, String temp, final NetworkManager.SuccessCallback<ArrayList<TemperatureHistory>> successCallback, final NetworkManager.FailedCallback failedCallback
    ) {

        String netless = LocalShared.getInstance(Box.getApp()).getString("netless");
        String noNetless = LocalShared.getInstance(Box.getApp()).getString("noNetless");
        if (TextUtils.isEmpty(noNetless)) {
            if (!TextUtils.isEmpty(netless)) {
                Repository repository = Repository.getInstance(Box.getApp());
                repository.measureDatas(MyApplication.getInstance().userId, start, end, temp)
                        .map(new Function<List<DataInfoBean>, ArrayList<TemperatureHistory>>() {
                            @Override
                            public ArrayList<TemperatureHistory> apply(List<DataInfoBean> datas) throws Exception {
                                ArrayList<TemperatureHistory> histories = new ArrayList<>();
                                for (DataInfoBean data : datas) {
                                    TemperatureHistory history = new TemperatureHistory(Float.valueOf(data.temper_ature), data.time);
                                    histories.add(history);
                                }
                                return histories;
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ArrayList<TemperatureHistory>>() {
                            @Override
                            public void accept(ArrayList<TemperatureHistory> histories) throws Exception {
                                if (successCallback != null) {
                                    successCallback.onSuccess(histories);
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (throwable != null) {
                                    throwable.printStackTrace();
                                }
                                if (failedCallback != null) {
                                    failedCallback.onFailed("未知错误");
                                }
                            }
                        });
                return;
            }
        }
        HashMap<String, String> params = new HashMap<>();

        params.put("bid", MyApplication.getInstance().userId);
//        params.put("bid","100001");
        params.put("temp", temp);
        params.put("starttime", start);
        params.put("endtime", end);
        NetworkManager.getInstance().getResultClass(Get_HealthRecord, params, new TypeToken<ArrayList<TemperatureHistory>>() {
                }.getType(),
                successCallback, failedCallback);
    }

    /**
     * 获取血压的历史数据
     *
     * @param temp
     * @param successCallback
     */
    public static void getBloodpressureHistory(String start, String end, String temp, final NetworkManager.SuccessCallback<ArrayList<BloodPressureHistory>> successCallback, final NetworkManager.FailedCallback failedCallback
    ) {
        String netless = LocalShared.getInstance(Box.getApp()).getString("netless");
        String noNetless = LocalShared.getInstance(Box.getApp()).getString("noNetless");
        if (TextUtils.isEmpty(noNetless)) {
            if (!TextUtils.isEmpty(netless)) {
                Repository repository = Repository.getInstance(Box.getApp());
                repository.measureDatas(MyApplication.getInstance().userId, start, end, temp)
                        .map(new Function<List<DataInfoBean>, ArrayList<BloodPressureHistory>>() {
                            @Override
                            public ArrayList<BloodPressureHistory> apply(List<DataInfoBean> datas) throws Exception {
                                ArrayList<BloodPressureHistory> histories = new ArrayList<>();
                                for (DataInfoBean data : datas) {
                                    BloodPressureHistory history = new BloodPressureHistory(data.low_pressure, data.high_pressure, data.time);
                                    histories.add(history);
                                }
                                return histories;
                            }
                        }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ArrayList<BloodPressureHistory>>() {
                            @Override
                            public void accept(ArrayList<BloodPressureHistory> histories) throws Exception {
                                if (successCallback != null) {
                                    successCallback.onSuccess(histories);
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (throwable != null) {
                                    throwable.printStackTrace();
                                }
                                if (failedCallback != null) {
                                    failedCallback.onFailed("未知错误");
                                }
                            }
                        });
                return;
            }
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("bid", MyApplication.getInstance().userId);
//        params.put("bid","100001");
        params.put("temp", temp);
        params.put("starttime", start);
        params.put("endtime", end);
        NetworkManager.getInstance().getResultClass(Get_HealthRecord, params, new TypeToken<ArrayList<BloodPressureHistory>>() {
                }.getType(),
                successCallback, failedCallback);
    }

    /**
     * 血糖
     *
     * @param temp
     * @param successCallback
     */

    public static void getBloodSugarHistory(String start, String end, String temp, final NetworkManager.SuccessCallback<ArrayList<BloodSugarHistory>> successCallback, final NetworkManager.FailedCallback failedCallback
    ) {

        String netless = LocalShared.getInstance(Box.getApp()).getString("netless");
        String noNetless = LocalShared.getInstance(Box.getApp()).getString("noNetless");
        if (TextUtils.isEmpty(noNetless)) {
            if (!TextUtils.isEmpty(netless)) {
                Repository repository = Repository.getInstance(Box.getApp());
                repository.measureDatas(MyApplication.getInstance().userId, start, end, temp)
                        .map(new Function<List<DataInfoBean>, ArrayList<BloodSugarHistory>>() {
                            @Override
                            public ArrayList<BloodSugarHistory> apply(List<DataInfoBean> datas) throws Exception {
                                ArrayList<BloodSugarHistory> histories = new ArrayList<>();
                                for (DataInfoBean data : datas) {
                                    BloodSugarHistory history = new BloodSugarHistory(Float.valueOf(data.blood_sugar), data.time);
                                    histories.add(history);
                                }
                                return histories;
                            }
                        }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ArrayList<BloodSugarHistory>>() {
                            @Override
                            public void accept(ArrayList<BloodSugarHistory> histories) throws Exception {
                                if (successCallback != null) {
                                    successCallback.onSuccess(histories);
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (throwable != null) {
                                    throwable.printStackTrace();
                                }
                                if (failedCallback != null) {
                                    failedCallback.onFailed("未知错误");
                                }
                            }
                        });
                return;
            }
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("bid", MyApplication.getInstance().userId);
        params.put("temp", temp);
        params.put("starttime", start);
        params.put("endtime", end);
        NetworkManager.getInstance().getResultClass(Get_HealthRecord, params, new TypeToken<ArrayList<BloodSugarHistory>>() {
                }.getType(),
                successCallback, failedCallback);
    }

    /**
     * 血氧
     *
     * @param temp
     * @param successCallback
     */
    public static void getBloodOxygenHistory(String start, String end, String temp, final NetworkManager.SuccessCallback<ArrayList<BloodOxygenHistory>> successCallback, final NetworkManager.FailedCallback failedCallback
    ) {
        String netless = LocalShared.getInstance(Box.getApp()).getString("netless");
        String noNetless = LocalShared.getInstance(Box.getApp()).getString("noNetless");
        if (TextUtils.isEmpty(noNetless)) {
            if (!TextUtils.isEmpty(netless)) {
                Repository repository = Repository.getInstance(Box.getApp());
                repository.measureDatas(MyApplication.getInstance().userId, start, end, temp)
                        .map(new Function<List<DataInfoBean>, ArrayList<BloodOxygenHistory>>() {
                            @Override
                            public ArrayList<BloodOxygenHistory> apply(List<DataInfoBean> datas) throws Exception {
                                ArrayList<BloodOxygenHistory> histories = new ArrayList<>();
                                for (DataInfoBean data : datas) {
                                    BloodOxygenHistory history = new BloodOxygenHistory(Float.valueOf(data.blood_oxygen), data.time);
                                    histories.add(history);
                                }
                                return histories;
                            }
                        }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ArrayList<BloodOxygenHistory>>() {
                            @Override
                            public void accept(ArrayList<BloodOxygenHistory> histories) throws Exception {
                                if (successCallback != null) {
                                    successCallback.onSuccess(histories);
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (throwable != null) {
                                    throwable.printStackTrace();
                                }
                                if (failedCallback != null) {
                                    failedCallback.onFailed("未知错误");
                                }
                            }
                        });
                return;
            }
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("bid", MyApplication.getInstance().userId);
        params.put("temp", temp);
        params.put("starttime", start);
        params.put("endtime", end);
        NetworkManager.getInstance().getResultClass(Get_HealthRecord, params, new TypeToken<ArrayList<BloodOxygenHistory>>() {
                }.getType(),
                successCallback, failedCallback);
    }

    /**
     * 心率
     *
     * @param temp
     * @param successCallback
     */
    public static void getHeartRateHistory(String start, String end, String temp, final NetworkManager.SuccessCallback<ArrayList<HeartRateHistory>> successCallback, final NetworkManager.FailedCallback failedCallback
    ) {

        String netless = LocalShared.getInstance(Box.getApp()).getString("netless");
        String noNetless = LocalShared.getInstance(Box.getApp()).getString("noNetless");
        if (TextUtils.isEmpty(noNetless)) {
            if (!TextUtils.isEmpty(netless)) {
                Repository repository = Repository.getInstance(Box.getApp());
                repository.measureDatas(MyApplication.getInstance().userId, start, end, temp)
                        .map(new Function<List<DataInfoBean>, ArrayList<HeartRateHistory>>() {
                            @Override
                            public ArrayList<HeartRateHistory> apply(List<DataInfoBean> datas) throws Exception {
                                ArrayList<HeartRateHistory> histories = new ArrayList<>();
                                for (DataInfoBean data : datas) {
                                    HeartRateHistory history = new HeartRateHistory(data.heart_rate, data.time);
                                    histories.add(history);
                                }
                                return histories;
                            }
                        }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ArrayList<HeartRateHistory>>() {
                            @Override
                            public void accept(ArrayList<HeartRateHistory> histories) throws Exception {
                                if (successCallback != null) {
                                    successCallback.onSuccess(histories);
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (throwable != null) {
                                    throwable.printStackTrace();
                                }
                                if (failedCallback != null) {
                                    failedCallback.onFailed("未知错误");
                                }
                            }
                        });
                return;
            }
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("bid", MyApplication.getInstance().userId);
        params.put("temp", temp);
        params.put("starttime", start);
        params.put("endtime", end);
        NetworkManager.getInstance().getResultClass(Get_HealthRecord, params, new TypeToken<ArrayList<HeartRateHistory>>() {
                }.getType(),
                successCallback, failedCallback);
    }

    /**
     * 脉搏
     *
     * @param temp
     * @param successCallback
     */
    public static void getPulseHistory(String start, String end, String temp, final NetworkManager.SuccessCallback<ArrayList<PulseHistory>> successCallback, final NetworkManager.FailedCallback failedCallback
    ) {

        String netless = LocalShared.getInstance(Box.getApp()).getString("netless");
        String noNetless = LocalShared.getInstance(Box.getApp()).getString("noNetless");
        if (TextUtils.isEmpty(noNetless)) {
            if (!TextUtils.isEmpty(netless)) {
                Repository repository = Repository.getInstance(Box.getApp());
                repository.measureDatas(MyApplication.getInstance().userId, start, end, temp)
                        .map(new Function<List<DataInfoBean>, ArrayList<PulseHistory>>() {
                            @Override
                            public ArrayList<PulseHistory> apply(List<DataInfoBean> datas) throws Exception {
                                ArrayList<PulseHistory> histories = new ArrayList<>();
                                for (DataInfoBean data : datas) {
                                    PulseHistory history = new PulseHistory(data.pulse, data.time);
                                    histories.add(history);
                                }
                                return histories;
                            }
                        }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ArrayList<PulseHistory>>() {
                            @Override
                            public void accept(ArrayList<PulseHistory> histories) throws Exception {
                                if (successCallback != null) {
                                    successCallback.onSuccess(histories);
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (throwable != null) {
                                    throwable.printStackTrace();
                                }
                                if (failedCallback != null) {
                                    failedCallback.onFailed("未知错误");
                                }
                            }
                        });
                return;
            }
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("bid", MyApplication.getInstance().userId);
        params.put("temp", temp);
        params.put("starttime", start);
        params.put("endtime", end);
        NetworkManager.getInstance().getResultClass(Get_HealthRecord, params, new TypeToken<ArrayList<PulseHistory>>() {
                }.getType(),
                successCallback, failedCallback);
    }

    /**
     * 胆固醇
     *
     * @param temp
     * @param successCallback
     */
    public static void getCholesterolHistory(String start, String end, String temp, final NetworkManager.SuccessCallback<ArrayList<CholesterolHistory>> successCallback, final NetworkManager.FailedCallback failedCallback
    ) {

        String netless = LocalShared.getInstance(Box.getApp()).getString("netless");
        String noNetless = LocalShared.getInstance(Box.getApp()).getString("noNetless");
        if (TextUtils.isEmpty(noNetless)) {
            if (!TextUtils.isEmpty(netless)) {
                Repository repository = Repository.getInstance(Box.getApp());
                repository.measureDatas(MyApplication.getInstance().userId, start, end, temp)
                        .map(new Function<List<DataInfoBean>, ArrayList<CholesterolHistory>>() {
                            @Override
                            public ArrayList<CholesterolHistory> apply(List<DataInfoBean> datas) throws Exception {
                                ArrayList<CholesterolHistory> histories = new ArrayList<>();
                                for (DataInfoBean data : datas) {
                                    CholesterolHistory history = new CholesterolHistory(Float.valueOf(data.cholesterol), data.time);
                                    histories.add(history);
                                }
                                return histories;
                            }
                        }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ArrayList<CholesterolHistory>>() {
                            @Override
                            public void accept(ArrayList<CholesterolHistory> histories) throws Exception {
                                if (successCallback != null) {
                                    successCallback.onSuccess(histories);
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (throwable != null) {
                                    throwable.printStackTrace();
                                }
                                if (failedCallback != null) {
                                    failedCallback.onFailed("未知错误");
                                }
                            }
                        });
                return;
            }
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("bid", MyApplication.getInstance().userId);
//        params.put("bid","100001");
        params.put("temp", temp);
        params.put("starttime", start);
        params.put("endtime", end);
        NetworkManager.getInstance().getResultClass(Get_HealthRecord, params, new TypeToken<ArrayList<CholesterolHistory>>() {
                }.getType(),
                successCallback, failedCallback);
    }

    /**
     * 血尿酸
     *
     * @param temp
     * @param successCallback
     */
    public static void getBUAHistory(String start, String end, String temp, final NetworkManager.SuccessCallback<ArrayList<BUA>> successCallback, final NetworkManager.FailedCallback failedCallback
    ) {
        String netless = LocalShared.getInstance(Box.getApp()).getString("netless");
        String noNetless = LocalShared.getInstance(Box.getApp()).getString("noNetless");
        if (TextUtils.isEmpty(noNetless)) {
            if (!TextUtils.isEmpty(netless)) {
                Repository repository = Repository.getInstance(Box.getApp());
                repository.measureDatas(MyApplication.getInstance().userId, start, end, temp)
                        .map(new Function<List<DataInfoBean>, ArrayList<BUA>>() {
                            @Override
                            public ArrayList<BUA> apply(List<DataInfoBean> datas) throws Exception {
                                ArrayList<BUA> histories = new ArrayList<>();
                                for (DataInfoBean data : datas) {
                                    BUA history = new BUA(Float.valueOf(data.uric_acid), data.time);
                                    histories.add(history);
                                }
                                return histories;
                            }
                        }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ArrayList<BUA>>() {
                            @Override
                            public void accept(ArrayList<BUA> histories) throws Exception {
                                if (successCallback != null) {
                                    successCallback.onSuccess(histories);
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (throwable != null) {
                                    throwable.printStackTrace();
                                }
                                if (failedCallback != null) {
                                    failedCallback.onFailed("未知错误");
                                }
                            }
                        });
                return;
            }
        }
        HashMap<String, String> params = new HashMap<>();
        params.put("bid", MyApplication.getInstance().userId);
//        params.put("bid","100001");
        params.put("temp", temp);
        params.put("starttime", start);
        params.put("endtime", end);
        NetworkManager.getInstance().getResultClass(Get_HealthRecord, params, new TypeToken<ArrayList<BUA>>() {
                }.getType(),
                successCallback, failedCallback);
    }

    /**
     * 心电
     *
     * @param temp
     * @param successCallback
     */
    public static void getECGHistory(String start, String end, String temp, final NetworkManager.SuccessCallback<ArrayList<ECGHistory>> successCallback, final NetworkManager.FailedCallback failedCallback
    ) {

        String netless = LocalShared.getInstance(Box.getApp()).getString("netless");
        String noNetless = LocalShared.getInstance(Box.getApp()).getString("noNetless");
        if (TextUtils.isEmpty(noNetless)) {
            if (!TextUtils.isEmpty(netless)) {
                Repository repository = Repository.getInstance(Box.getApp());
                repository.measureDatas(MyApplication.getInstance().userId, start, end, temp)
                        .map(new Function<List<DataInfoBean>, ArrayList<ECGHistory>>() {
                            @Override
                            public ArrayList<ECGHistory> apply(List<DataInfoBean> datas) throws Exception {
                                ArrayList<ECGHistory> histories = new ArrayList<>();
                                for (DataInfoBean data : datas) {
                                    ECGHistory history = new ECGHistory(String.valueOf(data.ecg), String.valueOf(data.time));
                                    histories.add(history);
                                }
                                return histories;
                            }
                        }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<ArrayList<ECGHistory>>() {
                            @Override
                            public void accept(ArrayList<ECGHistory> histories) throws Exception {
                                if (successCallback != null) {
                                    successCallback.onSuccess(histories);
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                if (throwable != null) {
                                    throwable.printStackTrace();
                                }
                                if (failedCallback != null) {
                                    failedCallback.onFailed("未知错误");
                                }
                            }
                        });
                return;
            }
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("bid", MyApplication.getInstance().userId);
//        params.put("bid", "100001");
        params.put("temp", temp);
        params.put("starttime", start);
        params.put("endtime", end);
        NetworkManager.getInstance().getResultClass(Get_HealthRecord, params, new TypeToken<ArrayList<ECGHistory>>() {
                }.getType(),
                successCallback, failedCallback);
    }

    /**
     * 体重
     *
     * @param start
     * @param end
     * @param temp
     * @param successCallback
     * @param failedCallback
     */
    public static void getWeight(String start, String end, String temp, NetworkManager.SuccessCallback<ArrayList<WeightHistory>> successCallback, NetworkManager.FailedCallback failedCallback
    ) {
        HashMap<String, String> params = new HashMap<>();

        params.put("bid", MyApplication.getInstance().userId);
//        params.put("bid","100001");
        params.put("temp", temp);
        params.put("starttime", start);
        params.put("endtime", end);
        NetworkManager.getInstance().getResultClass(Get_HealthRecord, params, new TypeToken<ArrayList<WeightHistory>>() {
                }.getType(),
                successCallback, failedCallback);
    }

    /**
     * 获取所有医生
     *
     * @param successCallback
     */
    public static void getAllDoctor(String doctorname, String start, String limit, NetworkManager.SuccessCallback<ArrayList<AllDoctor>> successCallback
    ) {
        HashMap<String, String> params = new HashMap<>();
        if (null != doctorname) {
            params.put("doctorname", doctorname);
        }
        params.put("start", start);
        params.put("limit", limit);
        NetworkManager.getInstance().getResultClass(Get_AllDotor, params, new TypeToken<ArrayList<AllDoctor>>() {
                }.getType(),
                successCallback);
    }


    public static void findAccount(
            String cate,
            String account,
            NetworkManager.SuccessCallback<String> successCallback,
            NetworkManager.FailedCallback failedCallback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("cate", cate);
        params.put("account", account);
        NetworkManager.getInstance().postResultClass(FIND_ACCOUNT, params, Object.class, successCallback, failedCallback);
    }

    public static void setPassword(
            String account,
            String pwd,
            NetworkManager.SuccessCallback<String> successCallback,
            NetworkManager.FailedCallback failedCallback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("account", account);
        params.put("pwd", pwd);
        NetworkManager.getInstance().postResultClass(SET_PASSWORD, params, Object.class, successCallback, failedCallback);
    }

    /**
     * 修改个人基本信息
     *
     * @param bid
     * @param height
     * @param weight
     * @param eating_habits
     * @param smoke
     * @param drink
     * @param exercise_habits
     * @param successCallback
     * @param failedCallback
     */
    public static void alertBasedata(String bid, String height, String weight, String eating_habits, String smoke, String drink, String exercise_habits, String mh, String dz,
                                     NetworkManager.SuccessCallback<Object> successCallback,
                                     NetworkManager.FailedCallback failedCallback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("bid", bid);
        params.put("height", height);
        params.put("weight", weight);
        params.put("eating_habits", eating_habits);
        params.put("smoke", smoke);
        params.put("drink", drink);
        params.put("exercise_habits", exercise_habits);
        params.put("mh", mh);
        params.put("dz", dz);
        NetworkManager.getInstance().postResultClass(Alert_Basedata, params, Object.class, successCallback, failedCallback);
    }

    public static void updateBloodType(
            String bid, String bloodType,
            NetworkManager.SuccessCallback<Object> successCallback,
            NetworkManager.FailedCallback failedCallback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("bid", bid);
        params.put("blood_type", bloodType);
        params.put("height", "0");
        params.put("weight", "0");
        params.put("eating_habits", "");
        params.put("smoke", "");
        params.put("drink", "");
        params.put("exercise_habits", "");
        params.put("mh", "");
        params.put("dz", "");
        NetworkManager.getInstance().postResultClass(Alert_Basedata, params, Object.class, successCallback, failedCallback);
    }

    /**
     * 更具语音获取疾病结果
     *
     * @param bname
     * @param successCallback
     * @param failedCallback
     */
    public static void getJibing(String bname, NetworkManager.SuccessCallback<DiseaseResult> successCallback,
                                 NetworkManager.FailedCallback failedCallback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("bname", bname);
        NetworkManager.getInstance().getResultClass(Get_jibing, params, DiseaseResult.class, successCallback, failedCallback);
    }

    /**
     * 添加组到后台
     *
     * @param callback
     * @param failedCallback
     */
    public static void recordGroup(String gid, String xfid, NetworkManager.SuccessCallback<String> callback, NetworkManager.FailedCallback failedCallback) {
        if (TextUtils.isEmpty(MyApplication.getInstance().userId)) {
            return;
        }
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("userid", MyApplication.getInstance().userId);
        paramsMap.put("gid", gid);
        paramsMap.put("xfid", xfid);
        NetworkManager.getInstance().postResultString(Add_Group, paramsMap, callback, failedCallback);
    }

    /**
     * 更改组的显示状态（是否成功从讯飞服务器上删除组）
     *
     * @param gid
     * @param status
     * @param callback
     * @param failedCallback
     */
    public static void changeGroupStatus(String gid, String status, NetworkManager.SuccessCallback<String> callback, NetworkManager.FailedCallback failedCallback) {
        Map<String, String> paramsMap = new HashMap<>();
        paramsMap.put("grid", gid);
        paramsMap.put("state", status);
        NetworkManager.getInstance().postResultString(Change_Group_Status, paramsMap, callback, failedCallback);
    }

    /**
     * 查询所有储存在后台的组信息
     *
     * @param gid
     * @param xfid
     * @param successCallback
     */

    public static void getXfGroupInfo(String ip, String gid, String xfid, NetworkManager.SuccessCallback<ArrayList<XfGroupInfo>> successCallback
    ) {
        HashMap<String, String> params = new HashMap<>();
        if (TextUtils.isEmpty(MyApplication.getInstance().userId)) {
            return;
        }
        params.put("userid", "0");
//        params.put("gid", gid);
//        params.put("xfid", xfid);
        params.put("state", "0");
        if ("116".equals(ip))
            NetworkManager.getInstance().getResultClass(Query_Group_116, params, new TypeToken<ArrayList<XfGroupInfo>>() {
                    }.getType(),
                    successCallback);
        if ("118".equals(ip))
            NetworkManager.getInstance().getResultClass(Query_Group_118, params, new TypeToken<ArrayList<XfGroupInfo>>() {
                    }.getType(),
                    successCallback);
    }

    /**
     * 消息中心的消息
     */
    public static void getMessages(String docterid, String dis_state, NetworkManager.SuccessCallback<ArrayList<MessagesOfCenter>> successCallback
    ) {
        if (TextUtils.isEmpty(MyApplication.getInstance().userId)) {
            return;
        }
        HashMap<String, String> params = new HashMap<>();
//        params.put("userid", MyApplication.getInstance().userId);
        params.put("userid", "100002");
        params.put("docterid", docterid);
        params.put("dis_state", dis_state);
        NetworkManager.getInstance().getResultClass(Get_Message, params, new TypeToken<ArrayList<MessagesOfCenter>>() {
                }.getType(),
                successCallback);
    }

    /**
     * 获得周生活报告
     *
     * @param successCallback
     * @param failedCallback
     */
    public static void getWeekReport(String userId, NetworkManager.SuccessCallback<WeekReportModel> successCallback,
                                     NetworkManager.FailedCallback failedCallback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("userid", userId);
        params.put("state", "1");
        NetworkManager.getInstance().getResultClass(Get_Week_or_Month_Report, params, WeekReportModel.class, successCallback, failedCallback);
    }

    /**
     * 或得月报告
     *
     * @param userId
     * @param successCallback
     * @param failedCallback
     */
    public static void getMonthReport(String userId, NetworkManager.SuccessCallback<MonthlyReport> successCallback,
                                      NetworkManager.FailedCallback failedCallback) {
        HashMap<String, String> params = new HashMap<>();
        params.put("userid", userId);
        params.put("state", "2");
        NetworkManager.getInstance().getResultClass(Get_Week_or_Month_Report, params, MonthlyReport.class, successCallback, failedCallback);
    }
}
