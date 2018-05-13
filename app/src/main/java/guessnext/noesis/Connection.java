package guessnext.noesis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Created by taanveer on 5/5/16.
 */
public class Connection {

    public String url,jSONString = null;

    Connection(){


    }
    Connection (String url){

        this.url = url;
        generateJSONString(url);

    }

    public void generateJSONString (String url){


        /**
         * this class is for background to retrive json string
         */

        class GetJSON extends AsyncTask<String, Void, String>{


            /**
             * automatically called method
             * this the function which will run the background to retrive json string in the background and return the json string
             * @param params
             * @return string (if not found null)
             */
            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();//creating connection
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));// getting the json string

                    String json;
                    while((json = bufferedReader.readLine())!= null){
                        sb.append(json+"\n");
                    }

                    jSONString = sb.toString().trim();
                    return sb.toString().trim(); //returning the json string
                    // return "haha";

                }catch(Exception e){
                    return null;
                }

            }

            /**
             * automatically called method
             * this method is getting the returned string from the doInBackground method and passing the value to the parseAndShow method
             * @param s
             */

            @Override
            protected void onPostExecute(String s) {

                super.onPostExecute(s);
                jSONString = s;
                //parseAndShow(s);

            }
        }

        /**
         * creating the object of GetJSON class to start aysnctask
         */
        GetJSON gj = new GetJSON();
        gj.execute(url);


    }


    public String getJSONString (){

        return jSONString;
    }





}
