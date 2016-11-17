package com.logishub.mobile.launcher.v5.DATA;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CargoData implements Serializable {

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getOid() {
        return oid;
    }

    public void setOid(String oid) {
        this.oid = oid;
    }

    public String getTurnOid() {
        return turnOid;
    }

    public void setTurnOid(String turnOid) {
        this.turnOid = turnOid;
    }

    public String getDriverUsreOid() {
        return driverUsreOid;
    }

    public void setDriverUsreOid(String driverUsreOid) {
        this.driverUsreOid = driverUsreOid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getEarlistPickupTime() {
        return earlistPickupTime;
    }

    public void setEarlistPickupTime(String earlistPickupTime) {
        this.earlistPickupTime = earlistPickupTime;
    }

    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    public String getTimeSpan() {
        return timeSpan;
    }

    public void setTimeSpan(String timeSpan) {
        this.timeSpan = timeSpan;
    }

    public String getTimeSpanText() {
        return timeSpanText;
    }

    public void setTimeSpanText(String timeSpanText) {
        this.timeSpanText = timeSpanText;
    }

    public String getIlDeliveryCustomer() {
        return ilDeliveryCustomer;
    }

    public void setIlDeliveryCustomer(String ilDeliveryCustomer) {
        this.ilDeliveryCustomer = ilDeliveryCustomer;
    }

    public String getDeliveryCustomerName() {
        return deliveryCustomerName;
    }

    public void setDeliveryCustomerName(String deliveryCustomerName) {
        this.deliveryCustomerName = deliveryCustomerName;
    }

    public String getDeliveryCustomerPhone() {
        return deliveryCustomerPhone;
    }

    public void setDeliveryCustomerPhone(String deliveryCustomerPhone) {
        this.deliveryCustomerPhone = deliveryCustomerPhone;
    }

    public String getIlPickupArea() {
        return ilPickupArea;
    }

    public void setIlPickupArea(String ilPickupArea) {
        this.ilPickupArea = ilPickupArea;
    }

    public String getPickupAreaName() {
        return pickupAreaName;
    }

    public void setPickupAreaName(String pickupAreaName) {
        this.pickupAreaName = pickupAreaName;
    }

    public String getPickupTypeCode() {
        return pickupTypeCode;
    }

    public void setPickupTypeCode(String pickupTypeCode) {
        this.pickupTypeCode = pickupTypeCode;
    }

    public String getPickupTypeName() {
        return pickupTypeName;
    }

    public void setPickupTypeName(String pickupTypeName) {
        this.pickupTypeName = pickupTypeName;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getIlArea() {
        return ilArea;
    }

    public void setIlArea(String ilArea) {
        this.ilArea = ilArea;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDetailAddress() {
        return detailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        this.detailAddress = detailAddress;
    }

    public String getDeliveryTimeTypeCode() {
        return deliveryTimeTypeCode;
    }

    public void setDeliveryTimeTypeCode(String deliveryTimeTypeCode) {
        this.deliveryTimeTypeCode = deliveryTimeTypeCode;
    }

    public String getDeliveryTimeTypeName() {
        return deliveryTimeTypeName;
    }

    public void setDeliveryTimeTypeName(String deliveryTimeTypeName) {
        this.deliveryTimeTypeName = deliveryTimeTypeName;
    }

    public String getTonCode() {
        return tonCode;
    }

    public void setTonCode(String tonCode) {
        this.tonCode = tonCode;
    }

    public String getTonName() {
        return tonName;
    }

    public void setTonName(String tonName) {
        this.tonName = tonName;
    }

    public String getContainerTypeCode() {
        return containerTypeCode;
    }

    public void setContainerTypeCode(String containerTypeCode) {
        this.containerTypeCode = containerTypeCode;
    }

    public String getContainerTypeName() {
        return containerTypeName;
    }

    public void setContainerTypeName(String containerTypeName) {
        this.containerTypeName = containerTypeName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentTypeName() {
        return paymentTypeName;
    }

    public void setPaymentTypeName(String paymentTypeName) {
        this.paymentTypeName = paymentTypeName;
    }

    public String getItemKindCode() {
        return itemKindCode;
    }

    public void setItemKindCode(String itemKindCode) {
        this.itemKindCode = itemKindCode;
    }

    public String getItemKindName() {
        return itemKindName;
    }

    public void setItemKindName(String itemKindName) {
        this.itemKindName = itemKindName;
    }

    public String getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(String vehicleID) {
        this.vehicleID = vehicleID;
    }

    public String getLoadingMethod() {
        return loadingMethod;
    }

    public void setLoadingMethod(String loadingMethod) {
        this.loadingMethod = loadingMethod;
    }

    public String getLoadingMethodName() {
        return loadingMethodName;
    }

    public void setLoadingMethodName(String loadingMethodName) {
        this.loadingMethodName = loadingMethodName;
    }

    public String getUnloadingMethod() {
        return unloadingMethod;
    }

    public void setUnloadingMethod(String unloadingMethod) {
        this.unloadingMethod = unloadingMethod;
    }

    public String getUnloadingMethodName() {
        return unloadingMethodName;
    }

    public void setUnloadingMethodName(String unloadingMethodName) {
        this.unloadingMethodName = unloadingMethodName;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    public String getDeliveryTypeName() {
        return deliveryTypeName;
    }

    public void setDeliveryTypeName(String deliveryTypeName) {
        this.deliveryTypeName = deliveryTypeName;
    }

    public String getWidthText() {
        return widthText;
    }

    public void setWidthText(String widthText) {
        this.widthText = widthText;
    }

    public String getLengthText() {
        return lengthText;
    }

    public void setLengthText(String lengthText) {
        this.lengthText = lengthText;
    }

    public String getHeightText() {
        return heightText;
    }

    public void setHeightText(String heightText) {
        this.heightText = heightText;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getCustomOrderType() {
        return customOrderType;
    }

    public void setCustomOrderType(String customOrderType) {
        this.customOrderType = customOrderType;
    }

    public String getCustomOrderTypeName() {
        return customOrderTypeName;
    }

    public void setCustomOrderTypeName(String customOrderTypeName) {
        this.customOrderTypeName = customOrderTypeName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRequestedPickupDate() {
        return requestedPickupDate;
    }

    public void setRequestedPickupDate(String requestedPickupDate) {
        this.requestedPickupDate = requestedPickupDate;
    }

    public String getRequestedDeliveryDate() {
        return requestedDeliveryDate;
    }

    public void setRequestedDeliveryDate(String requestedDeliveryDate) {
        this.requestedDeliveryDate = requestedDeliveryDate;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverMobilePhone() {
        return driverMobilePhone;
    }

    public void setDriverMobilePhone(String driverMobilePhone) {
        this.driverMobilePhone = driverMobilePhone;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getCharged() {
        return charged;
    }

    public void setCharged(String charged) {
        this.charged = charged;
    }

    public String getChargedText() {
        return chargedText;
    }

    public void setChargedText(String chargedText) {
        this.chargedText = chargedText;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserMobilePhone() {
        return userMobilePhone;
    }

    public void setUserMobilePhone(String userMobilePhone) {
        this.userMobilePhone = userMobilePhone;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getTonCodeList() {
        return tonCodeList;
    }

    public void setTonCodeList(String tonCodeList) {
        this.tonCodeList = tonCodeList;
    }

    public String getItemInfo() {
        return itemInfo;
    }

    public void setItemInfo(String itemInfo) {
        this.itemInfo = itemInfo;
    }

    public String getTransStatus() {
        return transStatus;
    }

    public void setTransStatus(String transStatus) {
        this.transStatus = transStatus;
    }

    public String getTurnInfoOid() {
        return turnInfoOid;
    }

    public void setTurnInfoOid(String turnInfoOid) {
        this.turnInfoOid = turnInfoOid;
    }

    public String getOrgOid() {
        return orgOid;
    }

    public void setOrgOid(String orgOid) {
        this.orgOid = orgOid;
    }

    public String getOrgID() {
        return orgID;
    }

    public void setOrgID(String orgID) {
        this.orgID = orgID;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgOwnerName() {
        return orgOwnerName;
    }

    public void setOrgOwnerName(String orgOwnerName) {
        this.orgOwnerName = orgOwnerName;
    }

    public String getVehicleAllocationBy() {
        return vehicleAllocationBy;
    }

    public void setVehicleAllocationBy(String vehicleAllocationBy) {
        this.vehicleAllocationBy = vehicleAllocationBy;
    }

    public String getMultiOrderroute() {
        return multiOrderroute;
    }

    public void setMultiOrderroute(String multiOrderroute) {
        this.multiOrderroute = multiOrderroute;
    }

    public String getVehicleInfo() {
        return vehicleInfo;
    }

    public void setVehicleInfo(String vehicleInfo) {
        this.vehicleInfo = vehicleInfo;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getMultiOrder() {
        return multiOrder;
    }

    public void setMultiOrder(String multiOrder) {
        this.multiOrder = multiOrder;
    }

    public String getUserOid() {
        return userOid;
    }

    public void setUserOid(String userOid) {
        this.userOid = userOid;
    }

    public String getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(String chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public String getTurnStatus() {
        return turnStatus;
    }

    public void setTurnStatus(String turnStatus) {
        this.turnStatus = turnStatus;
    }

    public String getTransOrderCount() {
        return transOrderCount;
    }

    public void setTransOrderCount(String transOrderCount) {
        this.transOrderCount = transOrderCount;
    }

    public String getTransOrder() {
        return transOrder;
    }

    public void setTransOrder(String transOrder) {
        this.transOrder = transOrder;
    }

    public String getPickupTimeTypeName() {
        return pickupTimeTypeName;
    }

    public void setPickupTimeTypeName(String pickupTimeTypeName) {
        this.pickupTimeTypeName = pickupTimeTypeName;
    }

    public String getTransTypeName() {
        return transTypeName;
    }

    public void setTransTypeName(String transTypeName) {
        this.transTypeName = transTypeName;
    }

    public String getVehicleTypeName() {
        return vehicleTypeName;
    }

    public void setVehicleTypeName(String vehicleTypeName) {
        this.vehicleTypeName = vehicleTypeName;
    }

    public String getOrderTypeName() {
        return orderTypeName;
    }

    public void setOrderTypeName(String orderTypeName) {
        this.orderTypeName = orderTypeName;
    }

    public String getPayPriceTypeName() {
        return payPriceTypeName;
    }

    public void setPayPriceTypeName(String payPriceTypeName) {
        this.payPriceTypeName = payPriceTypeName;
    }

    public String getLatestPickupTime() {
        return latestPickupTime;
    }

    public void setLatestPickupTime(String latestPickupTime) {
        this.latestPickupTime = latestPickupTime;
    }

    public String getEarlistDeliveryTime() {
        return earlistDeliveryTime;
    }

    public void setEarlistDeliveryTime(String earlistDeliveryTime) {
        this.earlistDeliveryTime = earlistDeliveryTime;
    }

    public String getLatestDeliveryTime() {
        return latestDeliveryTime;
    }

    public void setLatestDeliveryTime(String latestDeliveryTime) {
        this.latestDeliveryTime = latestDeliveryTime;
    }

    public String getTransOrderID() {
        return transOrderID;
    }

    public void setTransOrderID(String transOrderID) {
        this.transOrderID = transOrderID;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getPickupCustomerName() {
        return pickupCustomerName;
    }

    public void setPickupCustomerName(String pickupCustomerName) {
        this.pickupCustomerName = pickupCustomerName;
    }

    public String getPickupDepotName() {
        return pickupDepotName;
    }

    public void setPickupDepotName(String pickupDepotName) {
        this.pickupDepotName = pickupDepotName;
    }

    public String getDeliveryDepotName() {
        return deliveryDepotName;
    }

    public void setDeliveryDepotName(String deliveryDepotName) {
        this.deliveryDepotName = deliveryDepotName;
    }

    public String getPickupTimeType() {
        return pickupTimeType;
    }

    public void setPickupTimeType(String pickupTimeType) {
        this.pickupTimeType = pickupTimeType;
    }

    public String getDispatcherName() {
        return dispatcherName;
    }

    public void setDispatcherName(String dispatcherName) {
        this.dispatcherName = dispatcherName;
    }

    public String getDispatcherPhone() {
        return dispatcherPhone;
    }

    public void setDispatcherPhone(String dispatcherPhone) {
        this.dispatcherPhone = dispatcherPhone;
    }

    public String getFreightOwnerPhone() {
        return freightOwnerPhone;
    }

    public void setFreightOwnerPhone(String freightOwnerPhone) {
        this.freightOwnerPhone = freightOwnerPhone;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getLinkStatus() {
        return linkStatus;
    }

    public void setLinkStatus(String linkStatus) {
        this.linkStatus = linkStatus;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public String getAssignedOrg() {
        return assignedOrg;
    }

    public void setAssignedOrg(String assignedOrg) {
        this.assignedOrg = assignedOrg;
    }

    public String getAssignedName() {
        return assignedName;
    }

    public void setAssignedName(String assignedName) {
        this.assignedName = assignedName;
    }

    public String getTurnID() {
        return turnID;
    }

    public void setTurnID(String turnID) {
        this.turnID = turnID;
    }

    public List<ServiceData> getOrgList() {
        return orgList;
    }

    public void setOrgList(List<ServiceData> orgList) {
        this.orgList = orgList;
    }

    private String id = "";
    private String transId = "";
    private String oid = "";
    private String turnOid = "";
    private String driverUsreOid = "";
    private String status = "";
    private String orderDate = "";
    private String earlistPickupTime = "";
    private String transDate = "";
    private String timeSpan = "";
    private String timeSpanText = "";
    private String ilDeliveryCustomer = "";
    private String deliveryCustomerName = "";
    private String deliveryCustomerPhone = "";
    private String ilPickupArea = "";
    private String pickupAreaName = "";
    private String pickupTypeCode = "";
    private String pickupTypeName = "";
    private String pickupTime = "";
    private String deliveryTime = "";
    private String ilArea = "";
    private String areaName = "";
    private String address = "";
    private String detailAddress = "";
    private String deliveryTimeTypeCode = "";
    private String deliveryTimeTypeName = "";
    private String tonCode = "";
    private String tonName = "";
    private String containerTypeCode = "";
    private String containerTypeName = "";
    private String price = "";
    private String paymentType = "";
    private String paymentTypeName = "";
    private String itemKindCode = "";
    private String itemKindName = "";
    private String vehicleID = "";
    private String loadingMethod = "";
    private String loadingMethodName = "";
    private String unloadingMethod = "";
    private String unloadingMethodName = "";
    private String deliveryType = "";
    private String deliveryTypeName = "";
    private String widthText = "";
    private String lengthText = "";
    private String heightText = "";
    private String weight = "";
    private String customOrderType = "";
    private String customOrderTypeName = "";
    private String remark = "";
    private String requestedPickupDate = "";
    private String requestedDeliveryDate = "";
    private String orderTime = "";
    private String driverName = "";
    private String driverMobilePhone = "";
    private String statusName = "";
    private String charged = "";
    private String chargedText = "";
    private String userName = "";
    private String userMobilePhone = "";
    private String ownerName = "";
    private String ownerPhone = "";
    private String vehicleNo = "";
    private String tonCodeList = "";
    private String itemInfo = "";
    private String transStatus = "";
    private String turnInfoOid = "";
    private String orgOid = "";
    private String orgID = "";
    private String orgName = "";
    private String orgOwnerName = "";
    private String vehicleAllocationBy = "";
    private String multiOrderroute = "";
    private String vehicleInfo = "";
    private String route = "";
    private String multiOrder = "";
    private String userOid = "";
    private String chargeAmount = "";
    private String turnStatus = "";
    private String transOrderCount = "";
    private String transOrder = "";
    private String pickupTimeTypeName = "";
    private String transTypeName = "";
    private String vehicleTypeName = "";
    private String orderTypeName = "";
    private String payPriceTypeName = "";
    private String latestPickupTime = "";
    private String earlistDeliveryTime = "";
    private String latestDeliveryTime = "";
    private String transOrderID = "";
    private String driverPhone = "";
    private String pickupCustomerName = "";
    private String pickupDepotName = "";
    private String deliveryDepotName = "";
    private String pickupTimeType = "";
    private String dispatcherName = "";
    private String dispatcherPhone = "";
    private String freightOwnerPhone = "";
    private String siteCode = "";
    private String serviceUrl = "";
    private String linkStatus = "";
    private String statusText = "";
    private String assignedOrg = "";
    private String assignedName = "";
    private String turnID = "";

    private List<ServiceData> orgList = new ArrayList<ServiceData>();
}