package com.adarshcreation.rapidtutor;

public class TutorProfile {

    private String Price, Rating, Order, Profile_pic,Uid;
    private Boolean Status;

    public TutorProfile() {
    }

    public TutorProfile(String uid,String price,Boolean status,String rating,String order,String profile_pic) {
        Price = price;
        Status = status;
        Uid = uid;
        Rating = rating;
        Order = order;
        Profile_pic = profile_pic;
    }


    public String getPrice() {
        return Price;
    }

    public Boolean getStatus() {
        return Status;
    }

    public String getRating() {
        return Rating;
    }
    public String getOrder() {
        return Order;
    }
    public String getProfile_pic() {
        return Profile_pic;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public void setRating(String rating) {
        Rating = rating;
    }

    public void setStatus(Boolean status) {
        Status = status;
    }

    public void setProfile_pic(String profile_pic) {
        Profile_pic = profile_pic;
    }

    public void setOrder(String order) {
        Order = order;
    }
}
