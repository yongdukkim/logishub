package com.logishub.mobile.launcher.v5;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.logishub.mobile.launcher.v5.Common.CustomAlertDialog;
import com.logishub.mobile.launcher.v5.Common.CustomProgressDialog;
import com.logishub.mobile.launcher.v5.Common.Define;
import com.logishub.mobile.launcher.v5.Common.Func;
import com.logishub.mobile.launcher.v5.Common.HttpHelper;
import com.logishub.mobile.launcher.v5.Common.HttpService;
import com.logishub.mobile.launcher.v5.DATA.MessageListData;
import com.logishub.mobile.launcher.v5.DATA.RequestCommunityInvitationData;
import com.logishub.mobile.launcher.v5.DATA.RequestUserData;
import com.logishub.mobile.launcher.v5.DATA.RequestUserRegisterData;
import com.logishub.mobile.launcher.v5.DATA.ResponseUserRegisterData;
import com.logishub.mobile.launcher.v5.DATA.UserData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageFragment extends Fragment {
    public View mView;
    private static ArrayList<UserData> mArrUserList = null;
    private static ArrayList<MessageListData> mArrMessageList = null;
    public static MessageListAdapter mMessageListAdapter = null;
    public ListView mMessageList;
    private CustomProgressDialog mProgressDialog;
    private String mErrorMessage;
    private HttpHelper<HttpService> mHttpHelper;
    private HttpService mHttpService;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView tvNoData;
    private static String mSelectedUserToOrgLinkOid;

    public MessageFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_message, container, false);

        mArrUserList = new ArrayList<UserData>();
        mArrUserList = ((MainActivity)getActivity()).mArrUserList;
        mArrMessageList = new ArrayList<MessageListData>();
        mArrMessageList = ((MainActivity)getActivity()).mArrMessageList;

        setLayout(mView);

        mMessageListAdapter.notifyDataSetChanged();

        onRefreshLayout();

        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mView = null;
        mArrUserList = null;
        mArrMessageList = null;
        mMessageListAdapter = null;
        mMessageList = null;
        mProgressDialog = null;
        mErrorMessage = null;
        mHttpHelper = null;
        mHttpService = null;
        mSwipeRefreshLayout = null;
        tvNoData = null;
        mSelectedUserToOrgLinkOid = null;

        hideProgressDialog();
    }

    private void setLayout(View v) {
        mMessageList = (ListView) v.findViewById(R.id.iv_message_list);
        tvNoData = (TextView) v.findViewById(R.id.tv_no_data);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        mMessageListAdapter = new MessageListAdapter(getActivity(), R.layout.messagelist_list, mArrMessageList);
        mMessageList.setAdapter(mMessageListAdapter);
        mMessageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                onListItemClick(parent, v, position, id);
            }
        });

        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_red_light);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onLoadData();
            }
        });
    }

    private void onLoadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    onMessageList();
                } catch (Exception e) {
                    handler.sendEmptyMessage(Define.HANDLER_MESSAGE_ERROR);
                }
            }
        }).start();
    }

    private void onMessageList() {
        ArrayList<RequestUserData> userLists = new ArrayList<>();
        RequestUserData requestUserData = new RequestUserData();
        RequestUserRegisterData requestUserRegisterData = new RequestUserRegisterData();
        requestUserData.setId(Func.checkStringNull(mArrUserList.get(0).getUserID()));
        requestUserData.setKey(mArrUserList.get(0).getUserKey());
        userLists.add(requestUserData);
        requestUserRegisterData.setUserList(userLists);

        mHttpHelper = new HttpHelper<>();
        mHttpService = mHttpHelper.getClient(HttpService.class);
        Call<List<ResponseUserRegisterData>> call = mHttpService.getMessageList(requestUserRegisterData);
        call.enqueue(new Callback<List<ResponseUserRegisterData>>() {
            @Override
            public void onResponse(Call<List<ResponseUserRegisterData>> call, Response<List<ResponseUserRegisterData>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        List<ResponseUserRegisterData> data = response.body();

                        if (data.get(0).getErrorCode().equals("0")) {

                            mArrMessageList.clear();

                            int mMessageSize = data.get(0).getMessageList().size();
                            if (mMessageSize > 0) {
                                for (int i = 0; i < mMessageSize; i++) {
                                    MessageListData messageData = new MessageListData();
                                    messageData.setOid(data.get(0).getMessageList().get(i).getOid());
                                    messageData.setType(data.get(0).getMessageList().get(i).getType());
                                    messageData.setDate(data.get(0).getMessageList().get(i).getDate());
                                    messageData.setTime(data.get(0).getMessageList().get(i).getTime());
                                    messageData.setMobilePhone(data.get(0).getMessageList().get(i).getMobilePhone());
                                    messageData.setTitle(data.get(0).getMessageList().get(i).getTitle());
                                    messageData.setContent(data.get(0).getMessageList().get(i).getContent());
                                    messageData.setServiceUrl(data.get(0).getMessageList().get(i).getServiceUrl());
                                    messageData.setSiteCode(data.get(0).getMessageList().get(i).getSiteCode());
                                    messageData.setTransOid(data.get(0).getMessageList().get(i).getTransOid());
                                    messageData.setTransDate(data.get(0).getMessageList().get(i).getTransDate());
                                    messageData.setOrgID(data.get(0).getMessageList().get(i).getOrgID());
                                    messageData.setOrgName(data.get(0).getMessageList().get(i).getOrgName());
                                    messageData.setInvitation(data.get(0).getMessageList().get(i).getInvitation());
                                    messageData.setCheckOrg(data.get(0).getMessageList().get(i).getCheckOrg());
                                    messageData.setCheckOrgStatus(data.get(0).getMessageList().get(i).getCheckOrgStatus());
                                    messageData.setCarrierName(data.get(0).getMessageList().get(i).getCarrierName());
                                    messageData.setTurnInfo(data.get(0).getMessageList().get(i).getTurnInfo());
                                    messageData.setTurnID(data.get(0).getMessageList().get(i).getTurnID());
                                    messageData.setOrgOid(data.get(0).getMessageList().get(i).getOrgOid());
                                    messageData.setUserToOrgLinkOid(data.get(0).getMessageList().get(i).getUserToOrgLinkOid());
                                    mArrMessageList.add(messageData);
                                }
                            }

                            mMessageListAdapter.notifyDataSetChanged();

                            handler.sendEmptyMessage(Define.HANDLER_MESSAGE_SUCCESS);
                        }
                        else {
                            mErrorMessage = Func.checkStringNull(data.get(0).getErrorContent());
                            handler.sendEmptyMessage(Define.HANDLER_MESSAGE_ERROR);
                        }

                    } else if (response.isSuccessful()) {
                        handler.sendEmptyMessage(Define.HANDLER_MESSAGE_FAIL);
                    } else {
                        handler.sendEmptyMessage(Define.HANDLER_MESSAGE_FAIL);
                    }
                } catch (Exception e) {
                    handler.sendEmptyMessage(Define.HANDLER_MESSAGE_FAIL);
                }
            }

            @Override
            public void onFailure(Call<List<ResponseUserRegisterData>> call, Throwable t) {
                handler.sendEmptyMessage(Define.HANDLER_MESSAGE_FAIL);
            }

        });
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            hideProgressDialog();
            switch(msg.what)
            {
                case Define.HANDLER_MESSAGE_SUCCESS:
                    break;
                case Define.HANDLER_MESSAGE_ERROR:
                    break;
                case Define.HANDLER_MESSAGE_FAIL:
                    break;
                case Define.HANDLER_MESSAGE_COMMUNITY_SUCCESS:
                    onLoadData();
                    break;
                case Define.HANDLER_MESSAGE_COMMUNITY_ERROR:
                    new CustomAlertDialog(getActivity(), "알림\n\n" + mErrorMessage);
                    break;
                default:
                    break;
            }

            onRefreshLayout();
            mSwipeRefreshLayout.setRefreshing(false);
            return true;
        }
    });

    private void onRefreshLayout() {
        if (mArrMessageList.size() > 0) {
            tvNoData.setVisibility(View.GONE);
        } else {
            tvNoData.setVisibility(View.VISIBLE);
        }
    }

    /** List Adapter */
    public class MessageListAdapter extends ArrayAdapter<MessageListData>
    {
        private ArrayList<MessageListData> itemList;
        private Context context;
        private int rowResourceId;

        public MessageListAdapter(Context context, int textViewResourceId, ArrayList<MessageListData> itemList)
        {
            super(context, textViewResourceId, itemList);
            this.itemList = itemList;
            this.context = context;
            this.rowResourceId = textViewResourceId;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View v = convertView;
            MessageListData item = itemList.get(position);
            if(item != null)
            {
                v = SetVehicleOperationList(context, v, this.rowResourceId, item);
            }

            return v;
        }
    }

    private View SetVehicleOperationList(Context context, View v, int rowResourceId, MessageListData Data)
    {
        LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = vi.inflate(R.layout.messagelist_list, null);

        TextView tvMessageContent = (TextView) v.findViewById(R.id.tv_message_detail);
        TextView tvDuration = (TextView) v.findViewById(R.id.tv_duration);

        tvMessageContent.setText(Html.fromHtml(""+ Data.getContent() + ""));
        tvDuration.setText(Html.fromHtml(""+ Data.getDate() + ""));

        return v;
    }

    protected void onListItemClick(AdapterView<?> parent, View v, int position, long id)
    {
        try {
            if (mArrMessageList.size() > 0) {
                if (!mArrMessageList.get(position).getServiceUrl().equals("") && !mArrMessageList.get(position).getSiteCode().equals("") && !mArrMessageList.get(position).getMobilePhone().equals("") && !mArrMessageList.get(position).getTransOid().equals("")) {
                    if (mArrMessageList.get(position).getInvitation().equals("true") && !mArrMessageList.get(position).getOrgName().equals("")) {
                        if (mArrMessageList.get(position).getCheckOrg().equals("true") && !mArrMessageList.get(position).getCheckOrgStatus().equals("6")) {
                            if (mArrMessageList.get(position).getCheckOrgStatus().equals("3")) {
                                new CustomAlertDialog(getActivity(), "알림" + "\n\n"+ mArrMessageList.get(0).getOrgName() +" 초대를 거절 했습니다.");
                            } else {
                                new CustomAlertDialog(getActivity(), "알림" + "\n\n"+ mArrMessageList.get(0).getOrgName() +" 에 가입 되었습니다.");
                            }
                        } else {
                            mSelectedUserToOrgLinkOid = mArrMessageList.get(position).getUserToOrgLinkOid();
                            onShowAlertDialog();
                        }
                    } else {
                        Intent sendIntent = new Intent(Define.SEND_BROADCAST_FLAG);
                        sendIntent.putExtra(Define.SEND_BROADCAST_WEB_FLAG, Define.MENU_WEB_FLAG);
                        sendIntent.putExtra(Define.SEND_BROADCAST_WEB_TYPE, Define.MENU_WEB_MENU_MESSAGE_TYPE);

                        if (mArrUserList.get(0).getUserBelongTo().equals(Define.USER_BELONGTO_1)) {
                            if (!(mArrMessageList.get(0).getOrgOid().equals("") || mArrMessageList.get(0).getOrgOid().equals("0") || mArrMessageList.get(0).getOrgOid().equals("null"))) {
                                if (mArrUserList.get(0).getUserCagill().equals("1")) {
                                    sendIntent.putExtra(Define.SEND_BROADCAST_WEB_URL, Define.LOGISHUB_WEB_MESSAGE_DISPATCHER2 + "?orgOid=" + mArrMessageList.get(position).getOrgOid() + "&turnID=" + mArrMessageList.get(position).getTurnID());
                                } else {
                                    sendIntent.putExtra(Define.SEND_BROADCAST_WEB_URL, Define.LOGISHUB_WEB_MESSAGE_DISPATCHER + "?orgOid=" + mArrMessageList.get(position).getOrgOid() + "&turnID=" + mArrMessageList.get(position).getTurnID());
                                }
                            }
                        } else if (mArrUserList.get(0).getUserBelongTo().equals(Define.USER_BELONGTO_2) || mArrUserList.get(0).getUserBelongTo().equals(Define.USER_BELONGTO_4)) {
                            sendIntent.putExtra(Define.SEND_BROADCAST_WEB_URL, Define.LOGISHUB_WEB_MESSAGE_CARGO_INFO + "?orgOid=" + mArrMessageList.get(position).getOrgOid() + "&turnID=" + mArrMessageList.get(position).getTurnID() + "&TransOrderOid=" + mArrMessageList.get(position).getTransOid());
                        } else if (mArrUserList.get(0).getUserBelongTo().equals(Define.USER_BELONGTO_3)) {
                            sendIntent.putExtra(Define.SEND_BROADCAST_WEB_URL, Define.LOGISHUB_WEB_MESSAGE_DELIVERY_INFO + "?orgOid=" + mArrMessageList.get(position).getOrgOid() + "&turnID=" + mArrMessageList.get(position).getTurnID() + "&TransOrderOid=" + mArrMessageList.get(position).getTransOid());
                        }

                        sendIntent.putExtra(Define.SEND_BROADCAST_WEB_TITLE, mArrMessageList.get(position).getTitle());
                        getActivity().sendBroadcast(sendIntent);
                    }
                }
            }

        } catch (Exception ex) {}
    }

    private void onShowAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("알림")
                .setMessage(mArrMessageList.get(0).getOrgName() + " 커뮤니티에 가입 하시겠습니까?")
                .setCancelable(true)
                .setPositiveButton("수락", new DialogInterface.OnClickListener(){

                    public void onClick(DialogInterface dialog, int whichButton){
                        onComunityInvitation(true);
                    }
                })
                .setNegativeButton("거절", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton){
                        onComunityInvitation(false);
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void onComunityInvitation(boolean mSendType) {
        showProgressDialog();
        ArrayList<RequestUserData> userLists = new ArrayList<>();
        RequestUserData requestUserData = new RequestUserData();
        RequestUserRegisterData requestUserRegisterData = new RequestUserRegisterData();
        requestUserData.setId(Func.checkStringNull(mArrUserList.get(0).getUserID()));
        requestUserData.setOid(mArrUserList.get(0).getUserOid());
        userLists.add(requestUserData);

        ArrayList<RequestCommunityInvitationData> messageLists = new ArrayList<>();
        RequestCommunityInvitationData requestCommunityInvitationData = new RequestCommunityInvitationData();
        requestCommunityInvitationData.setUserToOrgLinkOid(mSelectedUserToOrgLinkOid);
        requestCommunityInvitationData.setStatus(mSendType == true ? "0" : "1");
        messageLists.add(requestCommunityInvitationData);
        requestUserRegisterData.setUserList(userLists);
        requestUserRegisterData.setMessageList(messageLists);

        mHttpHelper = new HttpHelper<>();
        mHttpService = mHttpHelper.getClient(HttpService.class);
        Call<List<ResponseUserRegisterData>> call = mHttpService.registerCommunityInvitation(requestUserRegisterData);
        call.enqueue(new Callback<List<ResponseUserRegisterData>>() {
            @Override
            public void onResponse(Call<List<ResponseUserRegisterData>> call, Response<List<ResponseUserRegisterData>> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        List<ResponseUserRegisterData> data = response.body();

                        if (data.get(0).getErrorCode().equals("0")) {
                            handler.sendEmptyMessage(Define.HANDLER_MESSAGE_COMMUNITY_SUCCESS);
                        }
                        else {
                            mErrorMessage = Func.checkStringNull(data.get(0).getErrorContent());
                            handler.sendEmptyMessage(Define.HANDLER_MESSAGE_COMMUNITY_ERROR);
                        }

                    } else if (response.isSuccessful()) {
                        handler.sendEmptyMessage(Define.HANDLER_MESSAGE_FAIL);
                    } else {
                        handler.sendEmptyMessage(Define.HANDLER_MESSAGE_FAIL);
                    }
                } catch (Exception e) {
                    handler.sendEmptyMessage(Define.HANDLER_MESSAGE_FAIL);
                }
            }

            @Override
            public void onFailure(Call<List<ResponseUserRegisterData>> call, Throwable t) {
                handler.sendEmptyMessage(Define.HANDLER_MESSAGE_FAIL);
            }

        });
    }

    private void showProgressDialog() {
        mProgressDialog = new CustomProgressDialog(getActivity());
        mProgressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }
}