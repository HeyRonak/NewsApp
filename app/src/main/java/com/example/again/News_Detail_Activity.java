package com.example.again;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

public class News_Detail_Activity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener{


    private ImageView imageView;
    private TextView appbar_title,appbar_subTitle,date,time,title;
    private boolean isHideToolbar = false;
    FrameLayout dateBehaviour;
    LinearLayout TitleAppbar;
    Toolbar toolbar;
    TextToSpeech textToSpeech;

    private String mUrl,mTitle,mImage,mDate,mSource,mAuthor;
    private String mDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news__detail_);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        CollapsingToolbarLayout collapsingToolbarLayout =  findViewById(R.id.collapsing_toolbar);



        AppBarLayout appBarLayout = findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(this);

        dateBehaviour = findViewById(R.id.date_behavior);
        TitleAppbar = findViewById(R.id.title_appbar);

        imageView = findViewById(R.id.backdrop);
        appbar_title = findViewById(R.id.title_on_appbar);
        appbar_subTitle = findViewById(R.id.subtitle_on_appbar);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        title = findViewById(R.id.title);

        Intent intent = getIntent();

        mUrl = intent.getStringExtra("url");
        mImage = intent.getStringExtra("img");
        mTitle = intent.getStringExtra("title");
        mDate = intent.getStringExtra("date");
        mSource = intent.getStringExtra("source");
        mAuthor = intent.getStringExtra("author");


        mDescription = intent.getStringExtra("description");





        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(Utils.getRandomDrawbleColor());

        Glide.with(this)
                .load(mImage)
                .apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);

        appbar_title.setText(mSource);
        appbar_subTitle.setText(mUrl);
        date.setText(Utils.DateFormat(mDate));
        title.setText(mTitle);

        String author = null;

        if (mAuthor != null || mAuthor!= ""){
            mAuthor = " \u2022 " + Utils.DateToTimeFormat(mDate);
        }else {
            author = "";
        }

        time.setText(  mSource + author +" \u2020 " +Utils.DateToTimeFormat(mDate));
        initWebview(mUrl);

    }

    public void initWebview(String Url){

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl(Url);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        supportFinishAfterTransition();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
      int maxScroll = appBarLayout.getTotalScrollRange();
      float percent = (float) Math.abs(i) / (float) maxScroll;

        if (percent == 1f && isHideToolbar){
                dateBehaviour.setVisibility(View.GONE);
                TitleAppbar.setVisibility(View.VISIBLE);
                isHideToolbar = !isHideToolbar;
        }else if (percent < 1f && isHideToolbar){
            dateBehaviour.setVisibility(View.VISIBLE);
            TitleAppbar.setVisibility(View.GONE);
            isHideToolbar = !isHideToolbar;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.news_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
           int id = item.getItemId();

           if (id == R.id.web){
               Intent intent = new Intent(Intent.ACTION_VIEW);
               intent.setData(Uri.parse(mUrl));
               startActivity(intent);
               return true;
           }else if (id == R.id.share){
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT,mSource);
                String body =  mTitle +  "\n" + mUrl + "\n" + "Share from the App" + "\n";
                intent.putExtra(Intent.EXTRA_TEXT,body);
                startActivity(Intent.createChooser(intent,"Share with :"));

           }else if (id == R.id.speak){

               ReadNews();

           }

           return super.onOptionsItemSelected(item);
    }

    private void ReadNews() {
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onInit(int i) {


                if (i == TextToSpeech.SUCCESS){

                    int result = textToSpeech.setLanguage(Locale.US);
                     textToSpeech.setSpeechRate(0.8f);

                    if ((result == TextToSpeech.LANG_MISSING_DATA ) || (result == TextToSpeech.LANG_NOT_SUPPORTED)){
                        Toast.makeText(News_Detail_Activity.this, "This Language is not supported", Toast.LENGTH_LONG).show();
                    }

                    // read news from url


//
//                    Thread thread = new Thread(){
//
//                        @Override
//                        public void run() {
//
//                            try {
//                                URL url = new URL(mUrl);
//
//                                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
//                                String line;
//                                StringBuilder builder = new StringBuilder();
//
//                                while ((line = reader.readLine()) != null){
//
//                                    builder.append(line);
//                                    builder.append(System.lineSeparator());
//                                }
//
//                                Log.d("whole data",builder + "");
//                            } catch (MalformedURLException e) {
//                                e.printStackTrace();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//
//
//                        }
//                    };
//                    thread.start();

                    textToSpeech.speak("" + mDescription ,TextToSpeech.QUEUE_FLUSH,null);

                }else {
                    Toast.makeText(News_Detail_Activity.this, "Initilization Failed!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}
