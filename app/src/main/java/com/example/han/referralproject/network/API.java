package com.example.han.referralproject.network;

import com.gzq.lib_core.bean.UserInfoBean;
import com.gzq.lib_core.http.model.HttpResult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface API {
    @GET("ZZB/br/selOneUserEverything")
    Observable<HttpResult<UserInfoBean>> queryUserInfo(
            @Query("bid") String bid
    );
}
