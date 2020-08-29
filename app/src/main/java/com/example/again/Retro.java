package com.example.again;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Retro {

    @GET("top-headlines")
    Call<Website> getNews(
            @Query("country") String country,
            @Query("apiKey") String apiKey
    );

    @GET("everything")
    Call<Website> getNewsSearch(
            @Query("q") String query,
            @Query("apiKey") String apiKey
    );

//    @GET("/v2/sources?apiKey=b297518b96ef4ade85f75c16e086ea7b")
//    Call<Website> getNews();

}
