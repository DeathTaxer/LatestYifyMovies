package com.clicsixdev.yifymovies;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by aravind on 11-02-2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

   private List<Movie> movieList;




   public MovieAdapter(List<Movie> movieList){
       this.movieList = movieList;
   }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.movie_item,parent,false);

        return new MovieViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final MovieViewHolder holder, int position) {

      final Movie movie = movieList.get(position);

       holder.mv_name.setText(movie.getName());
       holder.img_url = movie.getImg();



        class ImageDownloadTask extends AsyncTask<String,Void,Bitmap>{

            String img_url;



            @Override
           protected Bitmap doInBackground(String... strings) {
               Bitmap bitmap = null;
               img_url = strings[0];
               URL url = createUrl(strings[0]);

               Log.v("stringurl",strings[0]);


               try {
                   bitmap = makeHttpRequest(url);

               } catch (IOException e) {

               }

               return bitmap;
           }

            @Override
            protected void onPostExecute(Bitmap bitmap) {

               if(holder.img_url.equals(img_url))
                       holder.mv_img.setImageBitmap(bitmap);
               else holder.mv_img.setImageDrawable(null);
            }

            private URL createUrl(String StringUrl){
               URL url = null;

               try {
                   url = new URL(StringUrl);
               } catch (MalformedURLException e) {
                   return null;
               }

               return url;
           }

           private Bitmap makeHttpRequest(URL url) throws IOException{
               HttpURLConnection urlConnection = null;
               InputStream stream = null;
               Bitmap bitmap = null;
               try {
                   urlConnection = (HttpURLConnection) url.openConnection();
                   urlConnection.setRequestMethod("GET");
                   urlConnection.setConnectTimeout(10000);
                   urlConnection.setReadTimeout(15000);
                   urlConnection.connect();

                   stream = urlConnection.getInputStream();
                   bitmap = BitmapFactory.decodeStream(stream);
                   //Log.v("image:",bitmap.toString());
                   return bitmap;



               }

               catch (IOException e){

               }

               finally {
                   if (urlConnection != null) urlConnection.disconnect();
                   if (stream != null) stream.close();
               }

               return bitmap;

           }
       }

        new ImageDownloadTask().execute(movie.getImg());


    }


    @Override
    public int getItemCount() {
        return movieList == null ? 0 : movieList.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder{
       ImageView mv_img;
       TextView mv_name;
       String img_url;

       public  MovieViewHolder(View itemView){
           super(itemView);

           mv_img = (ImageView) itemView.findViewById(R.id.mv_img);
           mv_name = (TextView) itemView.findViewById(R.id.mv_name);


       }
   }





}
