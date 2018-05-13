package guessnext.noesis;

/**
 * Created by taanveer on 5/8/16.
 */
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import guessnext.noesis.guessnext.MainActivity;
import guessnext.noesis.guessnext.R;

/**
 * this activity starts via intent when start game button is pressed from the main activtiy
 */

public class GameModes extends AppCompatActivity implements View.OnClickListener {

    /**
     * initialization if the variables
     */

    Button normal,marathon,extreme,retry,exit,back;
    TextView gamemodestxt;
    Typeface fnt;

    final android.os.Handler handler = new android.os.Handler();

    static String IMEI;

    ImageView blink;

    ImageView one, two,three;

    Configuration config;

    MediaPlayer mp;

    /**
     * the URL that contains the question as jsor string
     */

    private static final String JSON_URL = "http://games.bulkstudio.com/guessnext/gn.php?question=true";
    public  static String JSON = "",activity="";


    public JSONObject jsonObject;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // if (isNetworkAvailable()){

        /**
         * checking the device screen config and seting the layout accordingly
         */

        config = getResources().getConfiguration();

        if (config.smallestScreenWidthDp < 600 && config.smallestScreenWidthDp > 320) {
            setContentView(R.layout.content_game_modes);

        } else if (config.smallestScreenWidthDp >= 600 && config.smallestScreenWidthDp < 700) {

            setContentView(R.layout.content_game_modes_tab);

        }
        else if ( config.smallestScreenWidthDp >= 700){

            setContentView(R.layout.content_game_modes_tab_large);
        } else {
            setContentView(R.layout.content_game_modes_medium);
        }

        mp = MediaPlayer.create(this, R.raw.click);

        // setContentView(R.layout.content_game_modes);

        //}else {

           // setContentView(R.layout.content_oops);
      //  }

        //buttons initialization

        normal = (Button) findViewById(R.id.bnormal);
        marathon = (Button) findViewById(R.id.bmarathon);
        extreme = (Button) findViewById(R.id.bextreme);
        back = (Button) findViewById(R.id.bback);

        back.setOnClickListener(this);

        Intent intent = getIntent();


        IMEI = GetIMEI.IMEI;



        gamemodestxt = (TextView) findViewById(R.id.gamemodestxt);

        // creating the font
        fnt = Typeface.createFromAsset(getAssets(),"gnfont.ttf");

        //setting the fonts
        gamemodestxt.setTypeface(fnt);
        normal.setTypeface(fnt);
        marathon.setTypeface(fnt);
        extreme.setTypeface(fnt);
        //
        normal.setOnClickListener(this);
        marathon.setOnClickListener(this);
        extreme.setOnClickListener(this);



    }

    /**
     * on click listener for the buttons
     * @param v
     */

 @Override public void onClick (View v){

     if (GetIMEI.sound)mp.start();



     switch(v.getId()){

         case R.id.bback:
             finish();
             break;

         case R.id.bnormal:

             activity = "guessnext.noesis.NormalMode"; //passing string for the starting the normal mode activity
             //getJsonAndPass(JSON_URL);
             checkAndStart();
             break;

         case R.id.bmarathon:

             activity = "guessnext.noesis.MarathonMode"; //passing string for the starting the marathon mode activity
            // getJsonAndPass(JSON_URL);
             checkAndStart();
             break;

         case R.id.bextreme:

             activity = "guessnext.noesis.ExtremeMode"; //passing string for the starting the extreme mode activity
            // getJsonAndPass(JSON_URL);
             checkAndStart();
             break;

         case R.id.bretry:
             checkAndStart();
             break;
         case R.id.bexit:
             finish();
             break;
     }


 }

    /**
     * checking for internet connection
     * if there is internet returns true
     * otherwise false
     * @return
     */

    public  boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * this method check the conectivity and
     * starts the new activity
     * start game
     * if no connection showing no internet access
     */


    public void checkAndStart (){


        if (isNetworkAvailable()){

            //Intent n = new Intent("guessnext.noesis.NormalMode");
            //n.putExtra("jsonObject", jsonObject.toString());
            //startActivity(n);
            getJsonAndPass(JSON_URL);

        }

        else
        {
            Toast.makeText(this, "No Internet Access.",
                    Toast.LENGTH_LONG).show();
            setContentView(R.layout.content_oops);
            retry = (Button) findViewById(R.id.bretry);
            exit = (Button) findViewById(R.id.bexit);
            retry.setTypeface(fnt);
            retry.setOnClickListener(this);
            exit.setOnClickListener(this);


        }
    }


    /**
     * function to retrive json string from server
     *
     * @param url
     */


    public void getJsonAndPass(String url){


        /**
         * this class is for background to retrive json string
         */

        class GetJSON extends AsyncTask<String, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(NormalMode.this, "Please Wait...", null, true, true);

                config = getResources().getConfiguration();

                /**
                 * setting the loader screen for the according  to the screen
                 */

                if (config.smallestScreenWidthDp < 600 )
                    setContentView(R.layout.loader);
                else setContentView(R.layout.loader_tab);

                one = (ImageView) findViewById(R.id.imageView);
                two = (ImageView) findViewById(R.id.imageView2);
                three = (ImageView) findViewById(R.id.imageView3);

                one.setVisibility(View.INVISIBLE);
                two.setVisibility(View.INVISIBLE);
                three.setVisibility(View.INVISIBLE);

                load (); //defined function


              // if (config.smallestScreenWidthDp >= 600) {

                 //   setContentView(R.layout.splash_tab);

              //  } else {
               //     setContentView(R.layout.splash);
               // }
               // setContentView(R.layout.splash);

              //  blink = (ImageView) findViewById(R.id.imageView);

              //  final Animation animation = new AlphaAnimation(1, .1f); // Change alpha from fully visible to slightly invisible
              //  animation.setDuration(500); // duration - half a second
              //  animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
               // animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
               // animation.setRepeatMode(Animation.REVERSE);
              //  blink.startAnimation(animation);


            }

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

                    return sb.toString().trim(); //returning the json string

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
              //  blink.clearAnimation();
                try {
                    jsonObject = new JSONObject(s);
                    Intent n = new Intent(activity);
                    n.putExtra(JSON, jsonObject.toString());
                    //n.putExtra(IMEI, IMEI);
                    finish();
                    startActivity(n);

                }catch (Exception ex){

                }

               // loading.dismiss();


            }
        }

        /**
         * creating the object of GetJSON class to start aysnctask
         */
        GetJSON gj = new GetJSON();
        gj.execute(url);



    }

    /**
     * this function is for the loading animation manually created
     */

    public void load () {


        /**
         * two images are overlapped in the layout
         * after a certain period of time they are visible and invisible
         * that makes the loading
         */

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                one.setVisibility(View.VISIBLE);
            }
        }, 300);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                one.setVisibility(View.INVISIBLE);
                two.setVisibility(View.VISIBLE);

            }
        }, 600);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                two.setVisibility(View.INVISIBLE);
                three.setVisibility(View.VISIBLE);
            }
        }, 900);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        }, 1200);


    }

    /**
     * refresing the images means making them invisible again
     */

    void refresh (){

        // Toast.makeText(MainActivity.this, "refresh", Toast.LENGTH_LONG).show();

        one.setVisibility(View.VISIBLE);
        // two.setVisibility(View.INVISIBLE);
        three.setVisibility(View.INVISIBLE);

        load(); //again calling it creates looping
    }




}


