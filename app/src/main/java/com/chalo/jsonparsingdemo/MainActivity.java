package com.chalo.jsonparsingdemo;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.chalo.jsonparsingdemo.models.MovieModels;

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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tv ;
    ArrayList<String> movielisst = new ArrayList<String>() ;
    ArrayAdapter<ArrayList> adapter ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv  = (TextView)findViewById(R.id.textView2) ;
        Button btnHit = (Button)findViewById(R.id.button) ;
      //  tv = (TextView)findViewById(R.id.textView);
        tv.setVisibility(View.INVISIBLE);

        btnHit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tv.setVisibility(View.VISIBLE);
                new JsonTask().execute("http://jsonparsing.parseapp.com/jsonData/moviesDemoList.txt"); ;
            }
        });

    }

    public class JsonTask extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader bufferedReader = null;

            try{
                URL url = new URL(params[0]) ;
                connection  = (HttpURLConnection)url.openConnection() ;
                connection.connect();

                InputStream inputStream = connection.getInputStream() ;
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream)) ;
                StringBuffer stringBuffer = new StringBuffer() ;
                String line = "" ;

                while((line=bufferedReader.readLine())!=null){
                    stringBuffer.append(line) ;
                }

               String jsonFinal = stringBuffer.toString() ;
                StringBuffer listMovies = new StringBuffer() ;
                JSONObject jsonObjectParent = new JSONObject(jsonFinal) ;
              JSONArray jsonArry = jsonObjectParent.getJSONArray("movies") ;
                for(int i=0;i<jsonArry.length();i++)
                {
                    JSONObject jsonObjectFinal = jsonArry.getJSONObject(i) ;
                    MovieModels movieModels = new MovieModels() ;
                    movieModels.setMovie(jsonObjectFinal.getString("movie"));
                    movieModels.setYear(jsonObjectFinal.getInt("year"));
                    movieModels.setRating((float)jsonObjectFinal.getDouble("rating"));
                    movieModels.setDirector(jsonObjectFinal.getString("director"));
                    movieModels.setDuration(jsonObjectFinal.getString("duration"));
                    movieModels.setTagline(jsonObjectFinal.getString("tagline"));
                    movieModels.setImage(jsonObjectFinal.getString("image"));
                    movieModels.setStory(jsonObjectFinal.getString("story"));

                    List<MovieModels.Cast> castList = new ArrayList<>() ;
                    for(int j=0;j<jsonObjectFinal.getJSONArray("cast").length();j++)
                    {

                        MovieModels.Cast cast = new MovieModels.Cast() ;
                        cast.setName(jsonObjectFinal.getJSONArray("cast").getJSONObject(j).getString("name"));
                        castList.add(cast) ;

                    }
               //     movielisst.add(moviename+"("+movieyear+")") ;
                }

                return  null ;
            }catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(connection!=null)connection.disconnect();
                try {
                    if(bufferedReader!=null) bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Context context = MainActivity.this ;
            adapter = new ArrayAdapter(context,R.layout.list_item, R.id.movieItem,movielisst);
            ListView lv = (ListView)findViewById(R.id.listView) ;
            lv.setAdapter(adapter);
        }
    }
}
