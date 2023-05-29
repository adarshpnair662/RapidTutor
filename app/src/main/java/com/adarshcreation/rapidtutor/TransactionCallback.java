package com.adarshcreation.rapidtutor;

public interface TransactionCallback {
    void onCallback(String time,String amount,String sender,String receiver,String transaction,String date);
}