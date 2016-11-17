package com.logishub.mobile.launcher.v5;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.logishub.mobile.launcher.v5.Common.Define;
import com.logishub.mobile.launcher.v5.Common.Func;
import com.logishub.mobile.launcher.v5.DATA.UserData;

import java.util.ArrayList;

public class ShowMsgActivity extends AppCompatActivity {
    public static String mPushTitle;
    public static String mPushMessage;
    public static String mPushLink;
    public static String mPushType;
    public static ArrayList<UserData> mArrUserList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setCustomView(R.layout.actionbar_main);
        }

        Uri uri = RingtoneManager.getActualDefaultRingtoneUri(getApplicationContext(), RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
        ringtone.play();

        Intent intent = getIntent();
        mPushTitle = intent.getExtras().getString(Define.ACT_PUT_REQ_PUSH_TITLE);
        mPushMessage = intent.getExtras().getString(Define.ACT_PUT_REQ_PUSH_MESSAGE);
        mPushLink = intent.getExtras().getString(Define.ACT_PUT_REQ_PUSH_LINK);
        mPushType = intent.getExtras().getString(Define.ACT_PUT_REQ_PUSH_TYPE);
        mArrUserList = (ArrayList<UserData>) getIntent().getSerializableExtra(Define.ACT_PUT_REQ_USER_LIST);
        //new CustomAlertDialogFinish(this, mPushTitle + "\n\n" + mPushMessage);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                //링크가 있을경우 웹뷰로이동
                if(mPushType.equals(Define.PUSH_TYPE_COMMUNITYNOTICE_REG)) {
                    if(!Func.checkStringNull(mPushLink).equals("")) {
                        String mSendTitle = "";
                        String[] mArrPushLink = mPushLink.split("\\?");

                        if(mArrPushLink.length > 1) {
                            String[] mArrPushLinkParam = mArrPushLink[1].split("&");

                            for(int i = 0; i < mArrPushLinkParam.length; i++) {
                                String[] mArrParam = mArrPushLinkParam[i].split("=");
                                if(mArrParam[0].equals("orgName")) {
                                    mSendTitle = mArrParam[1] + "'s 공지사항";
                                }
                            }

                            Intent sendIntent = new Intent(Define.SEND_BROADCAST_FLAG);
                            sendIntent.putExtra(Define.SEND_BROADCAST_WEB_FLAG, Define.MENU_WEB_FLAG);
                            sendIntent.putExtra(Define.SEND_BROADCAST_WEB_TYPE, Define.MENU_WEB_MENU_COMMUNITY_TYPE);
                            sendIntent.putExtra(Define.SEND_BROADCAST_WEB_URL, mPushLink);
                            sendIntent.putExtra(Define.SEND_BROADCAST_WEB_TITLE, mSendTitle);
                            ShowMsgActivity.this.sendBroadcast(sendIntent);
                        }
                    }
                }

                finish();
            }
        });
        alert.setCancelable(false);
        alert.setTitle(mPushTitle);
        alert.setMessage(mPushMessage);
        alert.show();

    }
}
