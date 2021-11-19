package com.masales.main;

import io.quarkus.qson.QsonDate;
import io.quarkus.qson.QsonProperty;

import java.util.Date;
import java.util.Objects;

public class Notification {
    @QsonProperty("notification")
    private String notificationId;

    private String type;

    @QsonDate(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private Date created;

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return getNotificationId().equals(that.getNotificationId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNotificationId());
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId='" + notificationId + '\'' +
                ", type='" + type + '\'' +
                ", created=" + created +
                '}';
    }
}
