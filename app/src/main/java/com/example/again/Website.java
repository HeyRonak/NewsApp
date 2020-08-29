package com.example.again;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Website {


    public String status;
    public int totalResults;
    public List<Article> articles;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    //    @SerializedName("status")
//    @Expose
//    private String status;
//    @SerializedName("sources")
//    @Expose
//    private List<Source> sources = null;
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public List<Source> getSources() {
//        return sources;
//    }
//
//    public void setSources(List<Source> sources) {
//        this.sources = sources;
//    }


//    private String status;
//    private List<Source> sources = null;
//
//    public Website(String status, List<Source> sources) {
//        this.status = status;
//        this.sources = sources;
//    }
//
//    public Website() {
//    }
//
//    public String getStatus() {
//        return status;
//    }
//
//    public void setStatus(String status) {
//        this.status = status;
//    }
//
//    public List<Source> getSources() {
//        return sources;
//    }
//
//    public void setSources(List<Source> sources) {
//        this.sources = sources;
//    }
}
