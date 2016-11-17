package com.logishub.mobile.launcher.v5.DATA;

import java.io.Serializable;

public class ServiceData implements Serializable {

    public String getServiceOid() {
        return serviceOid;
    }

    public void setServiceOid(String serviceOid) {
        this.serviceOid = serviceOid;
    }

    public String getServiceStatus() {
        return serviceStatus;
    }

    public void setServiceStatus(String serviceStatus) {
        this.serviceStatus = serviceStatus;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceSite() {
        return serviceSite;
    }

    public void setServiceSite(String serviceSite) {
        this.serviceSite = serviceSite;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getServiceImgName() {
        return serviceImgName;
    }

    public void setServiceImgName(String serviceImgName) {
        this.serviceImgName = serviceImgName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getHasImage() {
        return hasImage;
    }

    public void setHasImage(String hasImage) {
        this.hasImage = hasImage;
    }

    public String getIsManager() {
        return isManager;
    }

    public void setIsManager(String isManager) {
        this.isManager = isManager;
    }

    public String getIsFNSLike() {
        return isFNSLike;
    }

    public void setIsFNSLike(String isFNSLike) {
        this.isFNSLike = isFNSLike;
    }

    private String serviceOid = "";
    private String serviceStatus = "";
    private String serviceType = "";
    private String serviceSite = "";
    private String serviceUrl = "";
    private String serviceImgName = "";
    private String serviceName = "";
    private String hasImage = "";
    private String isManager = "";
    private String isFNSLike = "";
}