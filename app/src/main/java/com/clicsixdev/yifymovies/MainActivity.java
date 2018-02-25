package com.clicsixdev.yifymovies;

import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String YIFY_REQUEST_URL = "https://yts.am/api/v2/list_movies.json?limit=16&page=1";
    RecyclerView recyclerView;
    int k = 1;
    MovieAdapter movieAdapter;
    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 10;
    int firstVisibleItem, visibleItemCount, totalItemCount;



    List<Movie> movieList = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        YifyAsyncTask yifyAsyncTask = new YifyAsyncTask();
        yifyAsyncTask.execute(YIFY_REQUEST_URL);

        Log.v("Movie%count",movieList.size() +"");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this,2);

        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            gridLayoutManager.setSpanCount(2);
            recyclerView.setLayoutManager(gridLayoutManager);}
        else
        {   gridLayoutManager.setSpanCount(3);
            recyclerView.setLayoutManager(gridLayoutManager);}
        //recyclerView.setItemViewCacheSize(20);
        //recyclerView.setDrawingCacheEnabled(true);
        //recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = gridLayoutManager.getItemCount();
                firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();

                if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached

                    Log.v("Yaeye!", "end called");



                    // Do something
                    new YifyAsyncTask().execute("https://yts.am/api/v2/list_movies.json?limit=16&page=" + (++k));

                    loading = true;

                }
            }
        });








    }




     private class YifyAsyncTask extends AsyncTask<String,Void,List<Movie>>{


        @Override
        protected List<Movie> doInBackground(String... urls) {
            URL url = createUrl(urls[0]);

            String jsonresponse = "";

            try{
                jsonresponse = makeHttpRequest(url);
            }
            catch (IOException e){

            }





            return extractMovieFromJson(jsonresponse);

        }

        @Override
        protected void onPostExecute(List<Movie> list) {



            //Log.v("ic",movieAdapter.getItemCount()+"");
            //Log.v("il&&",movieList.size()+"");
            if (movieList.isEmpty()) {
                movieList.addAll(list);
                movieAdapter = new MovieAdapter(movieList);
                recyclerView.setAdapter(movieAdapter);
                movieAdapter.notifyDataSetChanged();

            }
            else {
                int cur = movieAdapter.getItemCount();
                Log.v("ac",cur+"");
                movieList.addAll(list);
                movieAdapter.notifyItemRangeInserted(cur,16);
            }




        }

        private URL createUrl(String stringUrl) {
            URL url = null;

            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException e) {
                return null;
            }

            return url;

        }

        private String makeHttpRequest(URL url) throws IOException{
            String jsonresponse = null;
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.connect();

                inputStream = urlConnection.getInputStream();
                jsonresponse = readFromStream(inputStream);
            }
            catch (IOException e){

            }
            finally {
                if(urlConnection != null)
                    urlConnection.disconnect();
                if (inputStream != null)
                    inputStream.close();
            }

            return jsonresponse;

        }

        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if(inputStream != null){
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream , Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null){
                    output.append(line);
                    line = reader.readLine();
                }
            }
            //Log.v("response",output.toString());
            return  output.toString();
        }


        private List<Movie> extractMovieFromJson(String movieJson){

            List<Movie> movieList = new ArrayList<>();
            if (TextUtils.isEmpty(movieJson))
                return  null;

            try{
                JSONObject basejsonresponse = new JSONObject(movieJson);
                JSONObject data = basejsonresponse.getJSONObject("data");

                JSONArray movieArray = data.getJSONArray("movies");

                for (int i = 0; i < movieArray.length(); i++) {

                    JSONObject movie = movieArray.getJSONObject(i);
                    String movieName = movie.getString("title_long");
                    String movieUrl = movie.getString("url").replaceAll("\\\\","");
                    String movieImg = movie.getString("large_cover_image").replaceAll("\\\\","");

                    //Log.v("url",movieImg);

                    movieList.add(new Movie(movieName,movieUrl,movieImg));


                }
                return movieList;
            }
            catch (JSONException e){

            }
            return null;
        }
    }
}
