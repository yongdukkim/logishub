package com.logishub.mobile.launcher.v5.Common;

public class Define {

    /** Release*/
    /*public static final String LOGISHUB_WEB_DEFAULT = "http://m.logishub.net/";
    public static final String LOGISHUB_RESTFUL_DEFAULT = "http://restful.logishub.net/";
    public static final String SERVICE_SITE_IMAGE_URL = "http://m.logishub.net/images/Site/";
    public static final String SERVICE_SERVICE_IMAGE_URL = "http://m.logishub.net/css/themes/logishub/images/Regular/";*/

    /** Debug */
    public static final String LOGISHUB_WEB_DEFAULT = "http://dev.logishub.net/portal_dev/";
    public static final String LOGISHUB_RESTFUL_DEFAULT = "http://122.199.182.24/restful/";
    public static final String SERVICE_SITE_IMAGE_URL = "http://dev.logishub.net/portal_dev/images/Site/";
    public static final String SERVICE_SERVICE_IMAGE_URL = "http://dev.logishub.net/portal_dev/css/themes/logishub/images/Regular/";

    public static final String APP_NAME = "LogisHub";
    public static final String MARKET_URL = "market://details?id=com.logishub.mobile.launcher.v5";
    public static final int SPLASH_SCREEN_BACKGROUND_TIME = 700;
    public static final int SPLASH_SCREEN_LOGO_TIME = 1000;
    public static final int SPLASH_SCREEN_TEXT_TIME = 500;
    public static final int HTTP_CONNECTION_TIMEOUT = 15000;
    public static final int HTTP_CONNECTION_READ_TIMEOUT = 15000;
    public static final int BACK_PRESS_TIME = 2000;

    public static final int TEMPLETE_MAX_COUNT = 10;

    public static final String SMS_RECEIVE_TEL = "025668715";
    public static final int SMS_RECEIVE_MILLISINFUTURE  = 20 * 1000;
    public static final int SMS_RECEIVE_COUNT_DOWN_INTERVAL  = 1000;

    public static final int HANDLER_MESSAGE_SUCCESS = 0;
    public static final int HANDLER_MESSAGE_ERROR = 1;
    public static final int HANDLER_MESSAGE_FAIL = 2;
    public static final int HANDLER_MESSAGE_PHONE_NUMBER_SUCCESS = 3;
    public static final int HANDLER_MESSAGE_SUCCESS_RELOAD = 4;
    public static final int HANDLER_MESSAGE_SUCCESS_RELOAD_CALL = 5;
    public static final int HANDLER_MESSAGE_COMMUNITY_SUCCESS = 10;
    public static final int HANDLER_MESSAGE_COMMUNITY_ERROR = 11;
    public static final int HANDLER_MESSAGE_CHECK_ALREADY_MEMBER_YET = 20;
    public static final int HANDLER_MESSAGE_CHECK_ALREADY_MEMBER_NOT_YET = 21;
    public static final int HANDLER_MESSAGE_SITE_SESSION_KEY_SUCCESS = 30;
    public static final int HANDLER_MESSAGE_ORDER_REGISTER_SUCCESS = 40;
    public static final int HANDLER_MESSAGE_UPGRADE = 98;
    public static final int HANDLER_MESSAGE_PHONE_NUMBER_EMPTY = 99;
    public static final int HANDLER_MESSAGE_PORTALDEFINE_SUCCESS = 50;

    public static final int NAVIGATION_MENU_HOME = 0;
    public static final int NAVIGATION_MENU_COMMUNITY = 1;
    public static final int NAVIGATION_MENU_MESSAGE = 2;
    public static final int NAVIGATION_MENU_MORE = 3;
    public static final int NAVIGATION_MENU_LOGISHUB_LOGOUT = 4;
    public static final int NAVIGATION_MENU_KAKAO_LOGOUT = 5;
    public static final int NAVIGATION_MENU_GOOGLE_LOGOUT = 6;
    public static final int NAVIGATION_MENU_FACEBOOK_LOGOUT = 7;
    public static final int NAVIGATION_MENU_NAVER_LOGOUT = 8;

    public static final String NAVIGATION_MENU_HOME_NAME = "홈";
    public static final String NAVIGATION_MENU_COMMUNITY_NAME = "커뮤니티";
    public static final String NAVIGATION_MENU_MESSAGE_NAME = "메세지";
    public static final String NAVIGATION_MENU_MORE_NAME = "더보기";
    public static final String NAVIGATION_MENU_LOGOUT_NAME = "로그아웃";

    public static final String ACT_PUT_REQ_DEVICE_TOKEN = "REQ_DEVICE_TOKEN";
    public static final String ACT_PUT_REQ_PHONE_NUMBER = "REQ_PHONE_NUMBER";
    public static final String ACT_PUT_REQ_NICK_NAME = "REQ_NICK_NAME";
    public static final String ACT_PUT_REQ_LOGIN_TYPE = "REQ_LOGIN_TYPE";
    public static final String ACT_PUT_REQ_PUSH_TITLE = "REQ_PUSH_TITLE";
    public static final String ACT_PUT_REQ_PUSH_MESSAGE = "REQ_PUSH_MESSAGE";
    public static final String ACT_PUT_REQ_PUSH_LINK = "REQ_PUSH_LINK";
    public static final String ACT_PUT_REQ_PUSH_TYPE = "REQ_PUSH_TYPE";
    public static final String ACT_PUT_REQ_AGREEMENT = "REQ_AGREEMENT";
    public static final String ACT_PUT_REQ_AUTH_NUMBER = "REQ_AUTH_NUMBER";
    public static final String ACT_PUT_REQ_USER_LIST = "REQ_USER_LIST";
    public static final String ACT_PUT_REQ_MENU_LIST = "REQ_MENU_LIST";
    public static final String ACT_PUT_REQ_SERVICE_LIST = "REQ_SERVICE_LIST";
    public static final String ACT_PUT_REQ_COMMUNITY_LIST = "REQ_COMMUNITY_LIST";
    public static final String ACT_PUT_REQ_MESSAGE_LIST = "REQ_MESSAGE_LIST";
    public static final String ACT_PUT_REQ_WEB_URL = "REQ_WEB_URL";
    public static final String ACT_PUT_REQ_WEB_TYPE = "REQ_WEB_TYPE";
    public static final String ACT_PUT_REQ_WEB_TITLE = "REQ_WEB_TITLE";
    public static final String ACT_PUT_REQ_WEB_ID = "REQ_WEB_ID";
    public static final String ACT_PUT_REQ_WEB_PASS = "REQ_WEB_PASS";
    public static final String ACT_PUT_REQ_ACT_TITLE = "REQ_ACT_TITLE";
    public static final String ACT_PUT_REQ_SERVICE_OID = "REQ_SERVICE_OID";
    public static final String ACT_PUT_REQ_SERVICE_TYPE = "REQ_SERVICE_TYPE";
    public static final String ACT_PUT_REQ_MENU_OID = "REQ_MENU_OID";
    public static final String ACT_PUT_REQ_SERVICE_CODE = "REQ_SERVICE_CODE";
    public static final String ACT_PUT_REQ_SERVICE_URL = "REQ_SERVICE_URL";
    public static final String ACT_PUT_REQ_RECRUITDETAIL_DATA = "REQ_RECRUITDETAIL_DATA";
    public static final String ACT_PUT_REQ_RECRUITAPPLYDETAIL_DATA = "REQ_RECRUITAPPLYDETAIL_DATA";

    public static final String FRG_PUT_REQ_URL = "REQ_URL";
    public static final String FRG_PUT_REQ_MENU_ID = "REQ_MENU_ID";
    public static final String FRG_PUT_REQ_MENU_TYPE = "REQ_MENU_TYPE";

    public static final String LOGIN_ID = "LoginID";
    public static final String LOGIN_PASS = "LoginPASS";
    public static final String DEVICE_ID = "DeviceId";
    public static final String PHONE_NO = "PhoneNo";
    public static final String NOTIFICATION_FLAGS = "NotificationFlags";

    public static final String GEO_ENABLE_GPS = "GeoEnableGPS";
    public static final String GEO_PROVIDER = "GeoProvider";
    public static final String GEO_LATITUDE = "GeoLatitude";
    public static final String GEO_LONGITUDE = "GeoLongitude";
    public static final String GEO_LAST_UPDATED_TIME = "GeoLastUpdatedTime";
    public static final String GEO_ACCURACY = "GeoAccuracy";
    public static final String GEO_UPDATE_DATE = "GeoUpdateTime";
    public static final String GEO_LOCATION_LOGGER_ENABLE = "EnableGeoLocationLogger";
    public static final String GEO_IS_READY_GPS = "GeoIsReadyGPS";
    public static final int GEO_LOCATION_UPDATE_TIME = 240000;

    public static final String LOGINTYPE_LOGISHUB = "LOGISHUB";
    public static final String LOGINTYPE_KAKAO = "KAKAO";
    public static final String LOGINTYPE_NAVER = "NAVER";
    public static final String LOGINTYPE_GOOGLE = "GOOGLE";
    public static final String LOGINTYPE_FACEBOOK = "FACEBOOK";

    public static final String LOGISHUB_WEB_SERVICE = "ServiceMenu.html";
    public static final String LOGISHUB_WEB_COMMUNITY = "Community.html";
    public static final String LOGISHUB_WEB_MESSAGE_DISPATCHER = "PortalDispatcherConfirmList.html";
    public static final String LOGISHUB_WEB_MESSAGE_DISPATCHER2 = "PortalDispatcherConfirmList2.html";
    public static final String LOGISHUB_WEB_MESSAGE_CARGO_INFO = "CargoInfo2.html";
    public static final String LOGISHUB_WEB_MESSAGE_DELIVERY_INFO = "DeliveryInfo2.html";

    public static final String LOGISHUB_PUSH_TITLE = "title";
    public static final String LOGISHUB_PUSH_MESSAGE = "message";
    public static final String LOGISHUB_PUSH_LINK = "link";
    public static final String LOGISHUB_PUSH_TYPE = "type";

    public static final String AGREEMENT1 = "agreement1";
    public static final String AGREEMENT2 = "agreement2";
    public static final String AGREEMENT3 = "agreement3";
    public static final String AGREEMENT4 = "agreement4";
    public static final String AGREEMENT5 = "agreement5";

    public static final String OAUTH_CLIENT_ID = "yt02o0wRtmD135CPhJZA";
    public static final String OAUTH_CLIENT_SECRET = "QF6uipZTxB";
    public static final String OAUTH_CLIENT_NAME = "네이버 아이디로 로그인";

    public static final String MENU_NAME_FREIGHT_INFO = "화물정보";

    public static final String LOGOUT_FLAG = "LogOut";
    public static final String MENU_WEB_FLAG = "WebFlag";
    public static final String MENU_WEB_MENU_DEFAULT_TYPE = "WebMenuDefaultType";
    public static final String MENU_WEB_MENU_COMMUNITY_TYPE = "WebMenuCommunityType";
    public static final String MENU_WEB_MENU_MESSAGE_TYPE = "WebMenuMessageType";
    public static final String MENU_WEB_MENU_SERVICE_TYPE = "WebMenuServiceType";
    public static final String SEND_BROADCAST_FLAG = "com.logishub.mobile.launcher.v5.SEND_BROAD_CAST";
    public static final String SEND_BROADCAST_LOGIN = "com.logishub.mobile.launcher.v5.SEND_BROAD_LOGIN";

    public static final String MENU_LEVEL1 = "1";
    public static final String MENU_LEVEL2 = "2";
    public static final String MENU_LEVEL3 = "3";
    public static final String MENU_LEVEL4 = "4";

    public static final String MENU_NAME_CARGO_INFO = "화물정보";
    public static final String MENU_NAME_ACCOUNT_SET = "계정설정";
    public static final String MENU_NAME_ALARM_SET = "알람관리";
    public static final String MENU_NAME_CONDITIONS = "이용약관";
    public static final String MENU_NAME_PASS_SET = "비밀번호관리";
    public static final String MENU_NAME_TAX_INVOICE = "세금계산서";
    public static final String MENU_NAME_USER_GUIDE = "사용자가이드";
    public static final String MENU_NAME_WITH_DRAWL = "회원탈퇴";
    public static final String MENU_NAME_CALCULATE = "정산";
    public static final String MENU_NAME_JOB_INFO = "구인정보";
    public static final String MENU_NAME_NOTICE = "공지사항";
    public static final String MENU_NAME_TRACKING = "Tracking";
    public static final String MENU_NAME_TRACKING2 = "화물추적";
    public static final String MENU_NAME_STATISTICS = "현황조회";
    public static final String MENU_NAME_INVENTORY_LIST = "재고정보";
    public static final String MENU_NAME_RECEIVE_RELEASE_LIST = "입출정보";
    public static final String MENU_NAME_ITEM_LIST = "제품정보";
    public static final String MENU_NAME_ADMIN = "Admin";
    public static final String MENU_NAME_FREIGHT_REGISTER = "화물등록";
    public static final String MENU_NAME_FREIGHT_TEMPLATE = "템플릿";
    public static final String MENU_NAME_FREIGHT_MANAGE_LIST = "화물조회(전체)";
    public static final String MENU_NAME_FREIGHT_REGISTER_A = "화물등록A";
    public static final String MENU_NAME_FREIGHT_CARGO_LIST = "화물목록B";
    public static final String MENU_NAME_FREIGHT_CARGO_ALLOCATION_LIST = "배차내역A";
    public static final String MENU_NAME_FREIGHT_STATUS = "등록현황";
    public static final String MENU_NAME_FREIGHT_HISTORY = "등록이력";
    public static final String MENU_NAME_RECRUIT_INFO = "구인정보";
    public static final String MENU_NAME_RECRUIT_APPLY = "지원현황";
    public static final String MENU_NAME_INTEGRATIONFNS_LIST = "화물목록A";
    public static final String MENU_NAME_DISPATCHER_LIST = "배차내역";
    public static final String MENU_NAME_READY_TO_VEHICLE = "공차보고";
    public static final String MENU_NAME_STOCK_INFO = "재고정보";
    public static final String MENU_NAME_IN_OUT_BOUND_INFO = "입출정보";
    public static final String MENU_NAME_ITEM_INFO = "제품정보";



    public static final String MENU_TITLE_RECRUITDETAIL = "구인상세";
    public static final String MENU_TITLE_RECRUITAPPLYDETAIL = "지원상세";

    public static final String SEND_BROADCAST_WEB_FLAG = "FLAG";
    public static final String SEND_BROADCAST_WEB_TYPE = "TYPE";
    public static final String SEND_BROADCAST_WEB_URL = "URL";
    public static final String SEND_BROADCAST_WEB_TITLE = "TITLE";
    public static final String SEND_BROADCAST_LOGIN_USER_NAME = "USERNAME";
    public static final String SEND_BROADCAST_LOGIN_USER_TYPE = "USERTYPE";

    public static final String SERVICE_TYPE_TMS = "TMS";
    public static final String SERVICE_TYPE_TMS_LITE = "TMSLITE";
    public static final String SERVICE_TYPE_WMS = "WMS";
    public static final String SERVICE_TYPE_WMS_LITE = "WMSLITE";
    public static final String SERVICE_TYPE_FNS = "FNS";
    public static final String SERVICE_TYPE_FNS_LITE = "FNSLITE";
    public static final String SERVICE_TYPE_DIS = "DIS";
    public static final String SERVICE_TYPE_DIS_LITE = "DISLITE";
    public static final String SERVICE_TYPE_LMS = "LMS";
    public static final String SERVICE_TYPE_LMS_LITE = "LMSLITE";

    public static final String USER_BELONGTO_1 = "1";
    public static final String USER_BELONGTO_2 = "2";
    public static final String USER_BELONGTO_3 = "3";
    public static final String USER_BELONGTO_4 = "4";
    public static final String USER_BELONGTO_5 = "5";

    public static final int TIME_PICKER_INTERVAL_HOUR = 1;
    public static final int TIMEPICKER_INTERVAL_MINUTE = 5;
    public static final int TIMEPICKER_INTERVAL_SECONDS = 10;

    public static final String DEFINE_PICKUP_TIME_TYPE = "15901";
    public static final String DEFINE_DELIVERY_TIME_TYPE = "16001";
    public static final String DEFINE_VEHICLE_TON_CODE_TYPE = "2111";
    public static final String DEFINE_VEHICLE_CONTAINER_TYPE = "2110";
    public static final String DEFINE_PAYMENT_TYPE = "1301";
    public static final String DEFINE_LOADING_METHOD_TYPE = "16101";
    public static final String DEFINE_UNLOADING_METHOD_TYPE = "16201";
    public static final String DEFINE_DELIVERY_TYPE = "2001";
    public static final String DEFINE_ITEM_KIND_TYPE = "104";
    public static final String DEFINE_CUSTOMER_ORDER_TYPE = "8101";
    public static final String DEFINE_JOB_TYPE = "5101";
    public static final String DEFINE_AREA_TYPE = "1701";

    public static final String SCREEN_MODE_REGISTER = "REGISTER";
    public static final String SCREEN_MODE_MODIFY = "MODIFY";

    public static final String PUSH_TYPE_GENERAL = "GENERAL";
    public static final String PUSH_TYPE_COMMUNITYNOTICE_REG = "COMMUNITYNOTICE_REG";
}
