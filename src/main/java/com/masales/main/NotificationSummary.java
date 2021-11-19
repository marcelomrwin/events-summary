package com.masales.main;

import java.util.Date;

public class NotificationSummary {

    private Date creationTimeOnPod;
    private Date deliveryTimeOnPod;
    private Date creationTimeOnEms;
    private Date deliveryTimeOnEms;
    private Boolean existOnPod = Boolean.FALSE;
    private Boolean existOnEms = Boolean.FALSE;

    public Date getCreationTimeOnEms() {
        return creationTimeOnEms;
    }

    public void setCreationTimeOnEms(Date creationTimeOnEms) {
        this.creationTimeOnEms = creationTimeOnEms;
    }

    public Date getDeliveryTimeOnEms() {
        return deliveryTimeOnEms;
    }

    public void setDeliveryTimeOnEms(Date deliveryTimeOnEms) {
        this.deliveryTimeOnEms = deliveryTimeOnEms;
    }

    public Date getCreationTimeOnPod() {
        return creationTimeOnPod;
    }

    public void setCreationTimeOnPod(Date creationTimeOnPod) {
        this.creationTimeOnPod = creationTimeOnPod;
    }

    public Date getDeliveryTimeOnPod() {
        return deliveryTimeOnPod;
    }

    public void setDeliveryTimeOnPod(Date deliveryTimeOnPod) {
        this.deliveryTimeOnPod = deliveryTimeOnPod;
    }

    public Boolean getExistOnPod() {
        return existOnPod;
    }

    public void setExistOnPod(Boolean existOnPod) {
        this.existOnPod = existOnPod;
    }

    public Boolean getExistOnEms() {
        return existOnEms;
    }

    public void setExistOnEms(Boolean existOnEms) {
        this.existOnEms = existOnEms;
    }

    public String getNotificationId() {
        return notification.getNotificationId();
    }

    public String getType() {
        return notification.getType();
    }

    public Date getCreated() {
        return notification.getCreated();
    }

    private final Notification notification;

    public NotificationSummary(Notification notification) {
        this.notification = notification;
    }
}
