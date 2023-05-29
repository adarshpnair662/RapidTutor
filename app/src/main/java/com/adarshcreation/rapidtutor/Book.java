package com.adarshcreation.rapidtutor;


public class Book {

    private String Uid,Rid,DocID,Bdate,Btime, Name;
    private boolean Read, Status;

    public Book() {
    }

    public Book(String docID,String uid,String rid,String bdate,String btime,boolean read,boolean status, String name)
    {

        Uid = uid;
        Status = status;
        DocID = docID;
        Read = read;
        Name = name;
        Rid = rid;
        Bdate = bdate;
        Btime = btime;
    }

    public String getName() {
        return Name;
    }

    public String getUid() {
        return Uid;
    }

    public String getBdate() {
        return Bdate;
    }

    public String getBtime() {
        return Btime;
    }

    public String getDocID() {
        return DocID;
    }

    public String getRid() {
        return Rid;
    }

    public Boolean getRead()
    {
        return Read;
    }

    public Boolean getStatus()
    {
        return Status;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public void setDocID(String docID) {
        DocID = docID;
    }

    public void setBdate(String bdate) {
        Bdate = bdate;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setBtime(String btime) {
        Btime = btime;
    }

    public void setRid(String rid) {
        Rid = rid;
    }

    public void setStatus(boolean status) {
        Status = status;
    }

    public void setRead(boolean read) {
        Read = read;
    }
}