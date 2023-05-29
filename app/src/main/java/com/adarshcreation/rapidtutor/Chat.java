package com.adarshcreation.rapidtutor;


public class Chat {

    private String Message,Uid,Doc;
    private boolean Offer, Status, Pay;
    float Amount;

    public Chat() {
    }

    public Chat(String doc,String message,String uid, boolean offer, float amount, boolean status,boolean pay)
    {
        Message = message;
        Uid = uid;
        Offer = offer;
        Status = status;
        Doc = doc;
        Pay = pay;
        Amount= amount;
    }

    public boolean getOffer() {
        return Offer;
    }

    public float getAmount() {
        return Amount;
    }

    public String getMessage() {
        return Message;
    }

    public String getUid() {
        return Uid;
    }

    public boolean getStatus() {
        return Status;
    }

    public void setStatus(Boolean status) {
        Status = status;
    }

    public boolean getPay() {
        return Pay;
    }

    public String getDoc() {
        return Doc;
    }

    public void setStatus(boolean status) {
        Status = status;
    }

    public void setDoc(String doc) {
        Doc = doc;
    }

    public void setPay(boolean pay) {
        Pay = pay;
    }

    public void setAmount(float amount) {
        Amount = amount;
    }

    public void setOffer(boolean offer) {
        Offer = offer;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public void setMessage(String message) {
        Message = message;
    }
}