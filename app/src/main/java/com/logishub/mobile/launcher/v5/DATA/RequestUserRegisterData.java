package com.logishub.mobile.launcher.v5.DATA;

import java.util.ArrayList;
import java.util.List;

public class RequestUserRegisterData {

    public List<RequestUserData> getUserList() {
        return userList;
    }

    public void setUserList(List<RequestUserData> userList) {
        this.userList = userList;
    }

    public List<RequestCommunityInvitationData> getMessageList() {
        return messageList;
    }

    public void setMessageList(List<RequestCommunityInvitationData> messageList) {
        this.messageList = messageList;
    }

    private List<RequestUserData> userList = new ArrayList<RequestUserData>();

    private List<RequestCommunityInvitationData> messageList = new ArrayList<RequestCommunityInvitationData>();
}