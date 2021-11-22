package com.masales.main;

import java.util.Date;

public class NotificationSummary {

    private final Notification notification;
    private Date creationTimeOnPod;
    private String creationTimeOnPodString;
    private Date deliveryTimeOnPod;
    private String deliveryTimeOnPodString;
    private Date creationTimeOnEms;
    private String creationTimeOnEmsString;
    private Date deliveryTimeOnEms;
    private String deliveryTimeOnEmsString;
    private Boolean existOnPod = Boolean.FALSE;
    private Boolean existOnEms = Boolean.FALSE;

    public NotificationSummary(Notification notification) {
        this.notification = notification;
    }

    public String getCreationTimeOnPodString() {
        return creationTimeOnPodString;
    }

    public void setCreationTimeOnPodString(String creationTimeOnPodString) {
        this.creationTimeOnPodString = creationTimeOnPodString;
    }

    public String getDeliveryTimeOnPodString() {
        return deliveryTimeOnPodString;
    }

    public void setDeliveryTimeOnPodString(String deliveryTimeOnPodString) {
        this.deliveryTimeOnPodString = deliveryTimeOnPodString;
    }

    public String getCreationTimeOnEmsString() {
        return creationTimeOnEmsString;
    }

    public void setCreationTimeOnEmsString(String creationTimeOnEmsString) {
        this.creationTimeOnEmsString = creationTimeOnEmsString;
    }

    public String getDeliveryTimeOnEmsString() {
        return deliveryTimeOnEmsString;
    }

    public void setDeliveryTimeOnEmsString(String deliveryTimeOnEmsString) {
        this.deliveryTimeOnEmsString = deliveryTimeOnEmsString;
    }

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

    public String getCreatedString() {
        return notification.getCreatedString();
    }
}
