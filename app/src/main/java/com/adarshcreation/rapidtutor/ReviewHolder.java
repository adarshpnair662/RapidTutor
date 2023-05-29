package com.adarshcreation.rapidtutor;

public class ReviewHolder  {

    private String Name, Content;
    float Rating;

    public ReviewHolder() {
    }

    public ReviewHolder(String name,String content, float rating) {
        Name = name;
        Content = content;
        Rating = rating;
    }

    public float getRating() {
        return Rating;
    }

    public String getName() {
        return Name;
    }

    public String getContent() {
        return Content;
    }

    public void setRating(float rating) {
        Rating = rating;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setContent(String content) {
        Content = content;
    }
}
