package com.logishub.mobile.launcher.v5.Common;

import com.logishub.mobile.launcher.v5.DATA.RequestDefineData;
import com.logishub.mobile.launcher.v5.DATA.RequestFreightRegisterData;
import com.logishub.mobile.launcher.v5.DATA.RequestIntegrationFNSData;
import com.logishub.mobile.launcher.v5.DATA.RequestRecruitData;
import com.logishub.mobile.launcher.v5.DATA.RequestUserData;
import com.logishub.mobile.launcher.v5.DATA.RequestUserRegisterData;
import com.logishub.mobile.launcher.v5.DATA.ResponseCargoData;
import com.logishub.mobile.launcher.v5.DATA.ResponseDefineData;
import com.logishub.mobile.launcher.v5.DATA.ResponseFreightData;
import com.logishub.mobile.launcher.v5.DATA.ResponseFreightRegisterData;
import com.logishub.mobile.launcher.v5.DATA.ResponseIntegrationFNSData;
import com.logishub.mobile.launcher.v5.DATA.ResponseRecruitData;
import com.logishub.mobile.launcher.v5.DATA.ResponseUserRegisterData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface HttpService {

    @GET("api/v1/User/SendSMS")
    Call<List<ResponseUserRegisterData>> getAuthNum(@Query("mobilePhone") String mobilePhone);

    @Headers("Content-Type: application/json")
    @POST("api/v1/User/FindPassword")
    Call<List<ResponseUserRegisterData>> findPassword(@Body RequestUserRegisterData data);

    @Headers("Content-Type: application/json")
    @POST("api/v1/User/CheckAlreadyMember")
    Call<List<ResponseUserRegisterData>> checkAlreadyMember(@Body RequestUserRegisterData data);

    @Headers("Content-Type: application/json")
    @POST("api/v1/User/Login")
    Call<List<ResponseUserRegisterData>> getUser(@Body RequestUserRegisterData data);

    @Headers("Content-Type: application/json")
    @POST("api/v1/User/LoginMobile")
    Call<List<ResponseUserRegisterData>> getUserSNS(@Body RequestUserRegisterData data);

    @Headers("Content-Type: application/json")
    @POST("api/v1/User/Register")
    Call<List<ResponseUserRegisterData>> register(@Body RequestUserRegisterData data);

    @Headers("Content-Type: application/json")
    @POST("api/v1/User/MessageList")
    Call<List<ResponseUserRegisterData>> getMessageList(@Body RequestUserRegisterData data);

    @Headers("Content-Type: application/json")
    @POST("api/v1/User/AcceptCommunityInvitation")
    Call<List<ResponseUserRegisterData>> registerCommunityInvitation(@Body RequestUserRegisterData data);

    @Headers("Content-Type: application/json")
    @POST("api/v1/Recruit/RecruitList")
    Call<ResponseRecruitData> getRecruitList(@Body RequestRecruitData data);

    @Headers("Content-Type: application/json")
    @POST("api/v1/Common/SiteDefineList")
    Call<ResponseDefineData> getDefineData(@Body RequestDefineData data);

    @Headers("Content-Type: application/json")
    @POST("api/v1/Common/SiteCheckSession")
    Call<ResponseUserRegisterData> getSiteSessionData(@Body RequestUserData data);

    @Headers("Content-Type: application/json")
    @POST("api/v1/FNS/RegisterOrder")
    Call<ResponseFreightRegisterData> registerFNSOrder(@Body RequestFreightRegisterData data);

    @Headers("Content-Type: application/json")
    @POST("api/v1/FNS/ListOrders")
    Call<ResponseFreightData> getFNSOrder(@Body RequestFreightRegisterData data);

    @Headers("Content-Type: application/json")
    @POST("api/v1/FNS/ListOrdersAll")
    Call<ResponseFreightData> getFNSOrderAll(@Body RequestFreightRegisterData data);

    @Headers("Content-Type: application/json")
    @POST("api/v1/FNS/ListOrderAccounts")
    Call<ResponseFreightData> getFNSHistory(@Body RequestFreightRegisterData data);

    @Headers("Content-Type: application/json")
    @POST("api/v1/FNS/ListFNSOrdersB")
    Call<ResponseCargoData> getFNSOrderB(@Body RequestFreightRegisterData data);

    @Headers("Content-Type: application/json")
    @POST("api/v1/FNS/RequestFNSOrder")
    Call<ResponseCargoData> requestFNSOrderB(@Body RequestFreightRegisterData data);

    @Headers("Content-Type: application/json")
    @POST("api/v1/FNS/ListAssignedOrders")
    Call<ResponseCargoData> getAssignedFNSOrder(@Body RequestFreightRegisterData data);

    @Headers("Content-Type: application/json")
    @POST("api/v1/FNS/CompleteAssignedOrder")
    Call<ResponseCargoData> completeAssignedFNSOrder(@Body RequestFreightRegisterData data);

    @Headers("Content-Type: application/json")
    @POST("api/v1/FNS/CancelAssignedOrder")
    Call<ResponseCargoData> cancelAssignedFNSOrder(@Body RequestFreightRegisterData data);

    @Headers("Content-Type: application/json")
    @POST("api/v1/FNS/DeleteOrder")
    Call<ResponseFreightData> deleteFNSOrder(@Body RequestFreightRegisterData data);

    @Headers("Content-Type: application/json")
    @POST("api/v1/FNS/CancelOrder")
    Call<ResponseFreightData> cancelFNSOrder(@Body RequestFreightRegisterData data);

    @Headers("Content-Type: application/json")
    @POST("api/v1/Recruit/RecruitApply")
    Call<ResponseRecruitData> setRecruitApply(@Body RequestRecruitData data);

    @Headers("Content-Type: application/json")
    @POST("api/v1/Recruit/RecruitApplyList")
    Call<ResponseRecruitData> getRecruitApplyList(@Body RequestRecruitData data);

    @Headers("Content-Type: application/json")
    @POST("api/v1/Common/PortalDefineList")
    Call<ResponseDefineData> getPortalDefineList(@Body RequestDefineData data);

    @Headers("Content-Type: application/json")
    @POST("api/v1/IntegrationFNS/IntegrationFNSList")
    Call<ResponseIntegrationFNSData> getIntegrationFNSList(@Body RequestIntegrationFNSData data);
}