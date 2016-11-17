package com.logishub.mobile.launcher.v5.DATA;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by SPICE on 2016-10-17.
 */

public class ResponseRecruitData {

    @SerializedName("recruitList")
    private List<RecruitData> recruitList;

    @SerializedName("recruitApplyList")
    private List<RecruitData> recruitApplyList;

    private List<DefineListData> defineList;

    @SerializedName("errorCode")
    private String errorCode;

    @SerializedName("errorContent")
    private String errorContent;

    public List<RecruitData> getRecruitList() {
        return recruitList;
    }

    public void setRecruitList(List<RecruitData> recruitList) {
        this.recruitList = recruitList;
    }

    public List<RecruitData> getRecruitApplyList() {
        return recruitApplyList;
    }

    public void setRecruitApplyList(List<RecruitData> recruitApplyList) {
        this.recruitApplyList = recruitApplyList;
    }

    public List<DefineListData> getDefineList() {
        return defineList;
    }

    public void setDefineList(List<DefineListData> defineList) {
        this.defineList = defineList;
    }

    public String getErrorContent() {
        return errorContent;
    }

    public void setErrorContent(String errorContent) {
        this.errorContent = errorContent;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}
