package com.logishub.mobile.launcher.v5.DATA;

import android.os.Parcel;
import android.os.Parcelable;

public class FnsTemplateData implements Parcelable, Cloneable {
    private String RowOid = "";
    private String SiteCode = "";
    private String ID = "";
    private String TransID = "";
    private String Oid = "";
    private String TurnOid = "";
    private String DriverUserOid = "";
    private String Status = "";
    private String OrderDate = "";
    private String EarlistPickupTime = "";
    private String TransDate = "";
    private String TimeSpan = "";
    private String TimeSpanText = "";
    private String IlDeliveryCustomer = "";
    private String DeliveryCustomerName = "";
    private String DeliveryCustomerPhone = "";
    private String IlPickupArea = "";
    private String PickupAreaName = "";
    private String PickupTypeCode = "";
    private String PickupTypeName = "";
    private String PickupTime = "";
    private String DeliveryTime = "";
    private String IlArea = "";
    private String AreaName = "";
    private String Address = "";
    private String DetailAddress = "";
    private String DeliveryTimeTypeCode = "";
    private String DeliveryTimeTypeName = "";
    private String TonCode = "";
    private String TonName = "";
    private String ContainerTypeCode = "";
    private String ContainerTypeName = "";
    private String Price = "";
    private String PaymentType = "";
    private String PaymentTypeName = "";
    private String ItemKindCode = "";
    private String ItemKindName = "";
    private String VehicleID = "";
    private String LoadingMethod = "";
    private String LoadingMethodName = "";
    private String UnloadingMethod = "";
    private String UnloadingMethodName = "";
    private String DeliveryType = "";
    private String DeliveryTypeName = "";
    private String WidthText = "";
    private String LengthText = "";
    private String HeightText = "";
    private String Weight = "";
    private String CustomOrderType = "";
    private String CustomOrderTypeName = "";
    private String Remark = "";
    private String RequestedPickupDate = "";
    private String RequestedDeliveryDate = "";
    private String OrderTime = "";
    private String DriverName = "";
    private String DriverMobilePhone = "";
    private String StatusName = "";
    private String Charged = "";
    private String ChargedText = "";
    private String UserName = "";
    private String UserMobilePhone = "";
    private String OwnerName = "";
    private String OwnerPhone = "";
    private String VehicleNo = "";


    public FnsTemplateData(){}

    public static final Parcelable.Creator CREATOR	= new Parcelable.Creator()
    {
        public FnsTemplateData createFromParcel(Parcel in)
        {
            return new FnsTemplateData(in);
        }

        public FnsTemplateData[] newArray(int size)
        {
            return new FnsTemplateData[size];
        }
    };

    @Override
    public int describeContents()
    {
        return 0;
    }

    FnsTemplateData(Parcel src)
    {
        this.RowOid = src.readString();
        this.SiteCode = src.readString();
        this.ID = src.readString();
        this.TransID = src.readString();
        this.Oid = src.readString();
        this.TurnOid = src.readString();
        this.DriverUserOid = src.readString();
        this.Status = src.readString();
        this.OrderDate = src.readString();
        this.EarlistPickupTime = src.readString();
        this.TransDate = src.readString();
        this.TimeSpan = src.readString();
        this.TimeSpanText = src.readString();
        this.IlDeliveryCustomer = src.readString();
        this.DeliveryCustomerName = src.readString();
        this.DeliveryCustomerPhone = src.readString();
        this.IlPickupArea = src.readString();
        this.PickupAreaName = src.readString();
        this.PickupTypeCode = src.readString();
        this.PickupTypeName = src.readString();
        this.PickupTime = src.readString();
        this.DeliveryTime = src.readString();
        this.IlArea = src.readString();
        this.AreaName = src.readString();
        this.Address = src.readString();
        this.DetailAddress = src.readString();
        this.DeliveryTimeTypeCode = src.readString();
        this.DeliveryTimeTypeName = src.readString();
        this.TonCode = src.readString();
        this.TonName = src.readString();
        this.ContainerTypeCode = src.readString();
        this.ContainerTypeName = src.readString();
        this.Price = src.readString();
        this.PaymentType = src.readString();
        this.PaymentTypeName = src.readString();
        this.ItemKindCode = src.readString();
        this.ItemKindName = src.readString();
        this.VehicleID = src.readString();
        this.LoadingMethod = src.readString();
        this.LoadingMethodName = src.readString();
        this.UnloadingMethod = src.readString();
        this.UnloadingMethodName = src.readString();
        this.DeliveryType = src.readString();
        this.DeliveryTypeName = src.readString();
        this.WidthText = src.readString();
        this.LengthText = src.readString();
        this.HeightText = src.readString();
        this.Weight = src.readString();
        this.CustomOrderType = src.readString();
        this.CustomOrderTypeName = src.readString();
        this.Remark = src.readString();
        this.RequestedPickupDate = src.readString();
        this.RequestedDeliveryDate = src.readString();
        this.OrderTime = src.readString();
        this.DriverName = src.readString();
        this.DriverMobilePhone = src.readString();
        this.StatusName = src.readString();
        this.Charged = src.readString();
        this.ChargedText = src.readString();
        this.UserName = src.readString();
        this.UserMobilePhone = src.readString();
        this.OwnerName = src.readString();
        this.OwnerPhone = src.readString();
        this.VehicleNo = src.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(this.RowOid);
        dest.writeString(this.SiteCode);
        dest.writeString(this.ID);
        dest.writeString(this.TransID);
        dest.writeString(this.Oid);
        dest.writeString(this.TurnOid);
        dest.writeString(this.DriverUserOid);
        dest.writeString(this.Status);
        dest.writeString(this.OrderDate);
        dest.writeString(this.EarlistPickupTime);
        dest.writeString(this.TransDate);
        dest.writeString(this.TimeSpan);
        dest.writeString(this.TimeSpanText);
        dest.writeString(this.IlDeliveryCustomer);
        dest.writeString(this.DeliveryCustomerName);
        dest.writeString(this.DeliveryCustomerPhone);
        dest.writeString(this.IlPickupArea);
        dest.writeString(this.PickupAreaName);
        dest.writeString(this.PickupTypeCode);
        dest.writeString(this.PickupTypeName);
        dest.writeString(this.PickupTime);
        dest.writeString(this.DeliveryTime);
        dest.writeString(this.IlArea);
        dest.writeString(this.AreaName);
        dest.writeString(this.Address);
        dest.writeString(this.DetailAddress);
        dest.writeString(this.DeliveryTimeTypeCode);
        dest.writeString(this.DeliveryTimeTypeName);
        dest.writeString(this.TonCode);
        dest.writeString(this.TonName);
        dest.writeString(this.ContainerTypeCode);
        dest.writeString(this.ContainerTypeName);
        dest.writeString(this.Price);
        dest.writeString(this.PaymentType);
        dest.writeString(this.PaymentTypeName);
        dest.writeString(this.ItemKindCode);
        dest.writeString(this.ItemKindName);
        dest.writeString(this.VehicleID);
        dest.writeString(this.LoadingMethod);
        dest.writeString(this.LoadingMethodName);
        dest.writeString(this.UnloadingMethod);
        dest.writeString(this.UnloadingMethodName);
        dest.writeString(this.DeliveryType);
        dest.writeString(this.DeliveryTypeName);
        dest.writeString(this.WidthText);
        dest.writeString(this.LengthText);
        dest.writeString(this.HeightText);
        dest.writeString(this.Weight);
        dest.writeString(this.CustomOrderType);
        dest.writeString(this.CustomOrderTypeName);
        dest.writeString(this.Remark);
        dest.writeString(this.RequestedPickupDate);
        dest.writeString(this.RequestedDeliveryDate);
        dest.writeString(this.OrderTime);
        dest.writeString(this.DriverName);
        dest.writeString(this.DriverMobilePhone);
        dest.writeString(this.StatusName);
        dest.writeString(this.Charged);
        dest.writeString(this.ChargedText);
        dest.writeString(this.UserName);
        dest.writeString(this.UserMobilePhone);
        dest.writeString(this.OwnerName);
        dest.writeString(this.OwnerPhone);
        dest.writeString(this.VehicleNo);
    }

    public String getRowOid() {
        return RowOid;
    }

    public void setRowOid(String RowOid) {
        this.RowOid = RowOid;
    }

    public String getSiteCode() {
        return SiteCode;
    }

    public void setSiteCode(String siteCode) {
        SiteCode = siteCode;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getTransID() {
        return TransID;
    }

    public void setTransID(String transID) {
        TransID = transID;
    }

    public String getOid() {
        return Oid;
    }

    public void setOid(String oid) {
        Oid = oid;
    }

    public String getTurnOid() {
        return TurnOid;
    }

    public void setTurnOid(String turnOid) {
        TurnOid = turnOid;
    }

    public String getDriverUserOid() {
        return DriverUserOid;
    }

    public void setDriverUserOid(String driverUserOid) {
        DriverUserOid = driverUserOid;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public String getEarlistPickupTime() {
        return EarlistPickupTime;
    }

    public void setEarlistPickupTime(String earlistPickupTime) {
        EarlistPickupTime = earlistPickupTime;
    }

    public String getTransDate() {
        return TransDate;
    }

    public void setTransDate(String transDate) {
        TransDate = transDate;
    }

    public String getTimeSpan() {
        return TimeSpan;
    }

    public void setTimeSpan(String timeSpan) {
        TimeSpan = timeSpan;
    }

    public String getTimeSpanText() {
        return TimeSpanText;
    }

    public void setTimeSpanText(String timeSpanText) {
        TimeSpanText = timeSpanText;
    }

    public String getIlDeliveryCustomer() {
        return IlDeliveryCustomer;
    }

    public void setIlDeliveryCustomer(String ilDeliveryCustomer) {
        IlDeliveryCustomer = ilDeliveryCustomer;
    }

    public String getDeliveryCustomerName() {
        return DeliveryCustomerName;
    }

    public void setDeliveryCustomerName(String deliveryCustomerName) {
        DeliveryCustomerName = deliveryCustomerName;
    }

    public String getDeliveryCustomerPhone() {
        return DeliveryCustomerPhone;
    }

    public void setDeliveryCustomerPhone(String deliveryCustomerPhone) {
        DeliveryCustomerPhone = deliveryCustomerPhone;
    }

    public String getIlPickupArea() {
        return IlPickupArea;
    }

    public void setIlPickupArea(String ilPickupArea) {
        IlPickupArea = ilPickupArea;
    }

    public String getPickupAreaName() {
        return PickupAreaName;
    }

    public void setPickupAreaName(String pickupAreaName) {
        PickupAreaName = pickupAreaName;
    }

    public String getPickupTypeCode() {
        return PickupTypeCode;
    }

    public void setPickupTypeCode(String pickupTypeCode) {
        PickupTypeCode = pickupTypeCode;
    }

    public String getPickupTypeName() {
        return PickupTypeName;
    }

    public void setPickupTypeName(String pickupTypeName) {
        PickupTypeName = pickupTypeName;
    }

    public String getPickupTime() {
        return PickupTime;
    }

    public void setPickupTime(String pickupTime) {
        PickupTime = pickupTime;
    }

    public String getDeliveryTime() {
        return DeliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        DeliveryTime = deliveryTime;
    }

    public String getIlArea() {
        return IlArea;
    }

    public void setIlArea(String ilArea) {
        IlArea = ilArea;
    }

    public String getAreaName() {
        return AreaName;
    }

    public void setAreaName(String areaName) {
        AreaName = areaName;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getDetailAddress() {
        return DetailAddress;
    }

    public void setDetailAddress(String detailAddress) {
        DetailAddress = detailAddress;
    }

    public String getDeliveryTimeTypeCode() {
        return DeliveryTimeTypeCode;
    }

    public void setDeliveryTimeTypeCode(String deliveryTimeTypeCode) {
        DeliveryTimeTypeCode = deliveryTimeTypeCode;
    }

    public String getDeliveryTimeTypeName() {
        return DeliveryTimeTypeName;
    }

    public void setDeliveryTimeTypeName(String deliveryTimeTypeName) {
        DeliveryTimeTypeName = deliveryTimeTypeName;
    }

    public String getTonCode() {
        return TonCode;
    }

    public void setTonCode(String tonCode) {
        TonCode = tonCode;
    }

    public String getTonName() {
        return TonName;
    }

    public void setTonName(String tonName) {
        TonName = tonName;
    }

    public String getContainerTypeCode() {
        return ContainerTypeCode;
    }

    public void setContainerTypeCode(String containerTypeCode) {
        ContainerTypeCode = containerTypeCode;
    }

    public String getContainerTypeName() {
        return ContainerTypeName;
    }

    public void setContainerTypeName(String containerTypeName) {
        ContainerTypeName = containerTypeName;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getPaymentType() {
        return PaymentType;
    }

    public void setPaymentType(String paymentType) {
        PaymentType = paymentType;
    }

    public String getPaymentTypeName() {
        return PaymentTypeName;
    }

    public void setPaymentTypeName(String paymentTypeName) {
        PaymentTypeName = paymentTypeName;
    }

    public String getItemKindCode() {
        return ItemKindCode;
    }

    public void setItemKindCode(String itemKindCode) {
        ItemKindCode = itemKindCode;
    }

    public String getItemKindName() {
        return ItemKindName;
    }

    public void setItemKindName(String itemKindName) {
        ItemKindName = itemKindName;
    }

    public String getVehicleID() {
        return VehicleID;
    }

    public void setVehicleID(String vehicleID) {
        VehicleID = vehicleID;
    }

    public String getLoadingMethod() {
        return LoadingMethod;
    }

    public void setLoadingMethod(String loadingMethod) {
        LoadingMethod = loadingMethod;
    }

    public String getLoadingMethodName() {
        return LoadingMethodName;
    }

    public void setLoadingMethodName(String loadingMethodName) {
        LoadingMethodName = loadingMethodName;
    }

    public String getUnloadingMethod() {
        return UnloadingMethod;
    }

    public void setUnloadingMethod(String unloadingMethod) {
        UnloadingMethod = unloadingMethod;
    }

    public String getUnloadingMethodName() {
        return UnloadingMethodName;
    }

    public void setUnloadingMethodName(String unloadingMethodName) {
        UnloadingMethodName = unloadingMethodName;
    }

    public String getDeliveryType() {
        return DeliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        DeliveryType = deliveryType;
    }

    public String getDeliveryTypeName() {
        return DeliveryTypeName;
    }

    public void setDeliveryTypeName(String deliveryTypeName) {
        DeliveryTypeName = deliveryTypeName;
    }

    public String getWidthText() {
        return WidthText;
    }

    public void setWidthText(String widthText) {
        WidthText = widthText;
    }

    public String getLengthText() {
        return LengthText;
    }

    public void setLengthText(String lengthText) {
        LengthText = lengthText;
    }

    public String getHeightText() {
        return HeightText;
    }

    public void setHeightText(String heightText) {
        HeightText = heightText;
    }

    public String getWeight() {
        return Weight;
    }

    public void setWeight(String weight) {
        Weight = weight;
    }

    public String getCustomOrderType() {
        return CustomOrderType;
    }

    public void setCustomOrderType(String customOrderType) {
        CustomOrderType = customOrderType;
    }

    public String getCustomOrderTypeName() {
        return CustomOrderTypeName;
    }

    public void setCustomOrderTypeName(String customOrderTypeName) {
        CustomOrderTypeName = customOrderTypeName;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getRequestedPickupDate() {
        return RequestedPickupDate;
    }

    public void setRequestedPickupDate(String requestedPickupDate) {
        RequestedPickupDate = requestedPickupDate;
    }

    public String getRequestedDeliveryDate() {
        return RequestedDeliveryDate;
    }

    public void setRequestedDeliveryDate(String requestedDeliveryDate) {
        RequestedDeliveryDate = requestedDeliveryDate;
    }

    public String getOrderTime() {
        return OrderTime;
    }

    public void setOrderTime(String orderTime) {
        OrderTime = orderTime;
    }

    public String getDriverName() {
        return DriverName;
    }

    public void setDriverName(String driverName) {
        DriverName = driverName;
    }

    public String getDriverMobilePhone() {
        return DriverMobilePhone;
    }

    public void setDriverMobilePhone(String driverMobilePhone) {
        DriverMobilePhone = driverMobilePhone;
    }

    public String getStatusName() {
        return StatusName;
    }

    public void setStatusName(String statusName) {
        StatusName = statusName;
    }

    public String getCharged() {
        return Charged;
    }

    public void setCharged(String charged) {
        Charged = charged;
    }

    public String getChargedText() {
        return ChargedText;
    }

    public void setChargedText(String chargedText) {
        ChargedText = chargedText;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getUserMobilePhone() {
        return UserMobilePhone;
    }

    public void setUserMobilePhone(String userMobilePhone) {
        UserMobilePhone = userMobilePhone;
    }

    public String getOwnerName() {
        return OwnerName;
    }

    public void setOwnerName(String ownerName) {
        OwnerName = ownerName;
    }

    public String getOwnerPhone() {
        return OwnerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        OwnerPhone = ownerPhone;
    }

    public String getVehicleNo() {
        return VehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        VehicleNo = vehicleNo;
    }

    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }
}