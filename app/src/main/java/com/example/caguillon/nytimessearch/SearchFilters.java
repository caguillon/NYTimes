package com.example.caguillon.nytimessearch;

/**
 * Created by caguillon on 6/23/16.
 */
public class SearchFilters {

    //filter attributes/variables:
    String begin_date;
    String sort;
    String news_desk;

    //getters:
    public String getBegin_date() {
        return begin_date;
    }

    public String getSort() {
        return sort;
    }

    public String getNews_desk() {
        return news_desk;
    }

    //setters:
    public void setBegin_date(String begin_date) {
        this.begin_date = begin_date;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public void setNews_desk(String news_desk) {
        this.news_desk = news_desk;
    }

}
