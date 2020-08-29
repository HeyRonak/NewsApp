package com.example.again;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebViewDatabase;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final String Api_Key = "b297518b96ef4ade85f75c16e086ea7b";
    RecyclerView recyclerView;
    List<Article> articles = new ArrayList<>();
    private Adapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView error_image;
    private TextView error_title,error_message;
    private Button btn_retry;
    private RelativeLayout error_layout;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = findViewById(R.id.swipe_refersh);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorAccent);


        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        error_layout = findViewById(R.id.errorLayout);
        error_image = findViewById(R.id.error_image);
        error_title = findViewById(R.id.error_title);
        error_message = findViewById(R.id.error_message);

        btn_retry = findViewById(R.id.retry);

//        LoadJson("");
        onLoadingSwipeRefresh("");

    }



    public void LoadJson(String keyword){

        error_layout.setVisibility(View.GONE);

        swipeRefreshLayout.setRefreshing(true);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://newsapi.org/v2/")
                .client(Myclient().build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        final Retro api  = retrofit.create(Retro.class);
        String country = Utils.getCountry();
        Call<Website> call;

        if (keyword.length() > 0){
            call  = api.getNewsSearch(keyword,Api_Key);
        }else {
            call = api.getNews(country , Api_Key);
        }

        call.enqueue(new Callback<Website>() {
            @Override
            public void onResponse(Call<Website> call, Response<Website> response) {
                if (response.isSuccessful() && response.body().getArticles() != null){

                    if (!articles.isEmpty()){
                        articles.clear();
                    }

                    articles = response.body().getArticles();
                    adapter = new Adapter(articles,MainActivity.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    swipeRefreshLayout.setRefreshing(false);

                }else {
                    swipeRefreshLayout.setRefreshing(false);
//                    Toast.makeText(MainActivity.this, "No Result!", Toast.LENGTH_SHORT).show();

                        String error_code;
                        switch(response.code()){
                            case 404:
                                error_code = "404 not found!";
                                break;

                            case 500:
                                error_code = "500 server broken!";
                                break;

                             default:
                                 error_code = "unknown error";
                                 break;
                        }
                        showError(R.drawable.no_result,"No result","Please try again! \n" + error_code);
                }
            }

            @Override
            public void onFailure(Call<Website> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                showError(R.drawable.oops,"","Network failure,Please try again! \n" + t.toString());
                Log.d("failure",t.toString());
            }
        });
    }

    public static OkHttpClient.Builder Myclient(){

        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            return builder;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);

        SearchManager searchManager  = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        MenuItem item = menu.findItem(R.id.search);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search Latest News...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                if (s.length() > 2){
//                    LoadJson(s);
                    onLoadingSwipeRefresh(s);
                    swipeRefreshLayout.setRefreshing(false);

                }else {
                    Toast.makeText(MainActivity.this, "Type more than two letters!", Toast.LENGTH_LONG).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
//                LoadJson(s);
                onLoadingSwipeRefresh(s);
                return false;
            }
        });

        item.getIcon().setVisible(false,false);
        return true;
    }

    private void onLoadingSwipeRefresh(String keyword){

        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        LoadJson(keyword);
                    }
                }
        );
    }

    @Override
    public void onRefresh() {
        LoadJson("");
    }

    public  void showError(int imageView,String title,String message){
        if (error_layout.getVisibility() == View.GONE){
            error_layout.setVisibility(View.VISIBLE);
        }

        error_image.setImageResource(imageView);
        error_title.setText(title);
        error_message.setText(message);

        btn_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoadingSwipeRefresh("");
            }
        });

    }
}
