package com.logishub.mobile.launcher.v5.DATA;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by SPICE on 2016-10-17.
 */

public class RequestRecruitData {
    @SerializedName("id")
    private String id;

    @SerializedName("key")
    private String key;

    @SerializedName("jobType")
    private String jobType;

    @SerializedName("workArea")
    private String workArea;

    @SerializedName("oid")
    private String oid;

    private List<DefineListData> defineTypeList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getWorkArea() {
        return workArea;
    }

    public void setWorkArea(String workArea) {
        this.workArea = workArea;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public List<DefineListData> getDefineTypeList() {
        return defineTypeList;
    }

    public void setDefineTypeList(List<DefineListData> defineTypeList) {
        this.defineTypeList = defineTypeList;
    }
}
