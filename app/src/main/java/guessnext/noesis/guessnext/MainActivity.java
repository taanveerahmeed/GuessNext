package guessnext.noesis.guessnext;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import guessnext.noesis.GetIMEI;
import guessnext.noesis.Registration;

/**
 * @author taanveer ahmeed
 */

/**
 * this is the main activity of this app
 * when the application runs this activity will run at first
 */

public class MainActivity extends AppCompatActivity {

    /**
     * declaration of variables
     */
    public Button startGame,howToPlay,highScores,about,quit,settings,regsiter,retry,exit,oppsexit;
    MediaPlayer mp;
    Configuration config;
    TextView gnTxt;
    Typeface fnt;

    ImageView blink;

    /**
     * the URL that contains the question as jsor string
     */

    private static  String JSON_URL = "http://games.bulkstudio.com/guessnext/gn.php?checkuser=true&imei=";

    public static String IMEI;

    /**
     * name of the array that should be same to the json server array name
     */

    private static final String JSON_ARRAY ="userinfo";


    /**
     *  json array to get the array from the server
     */

    private JSONArray exist = null;
    TelephonyManager telephonyManager;

    /**
     * this method exectues first when the activity creates
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        fnt = Typeface.createFromAsset(getAssets(), "gnfont.ttf");

        telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);

        IMEI = telephonyManager.getDeviceId();// getting the imei number of the phone
        GetIMEI.IMEI = IMEI; //saving it to the runtime variable for future access

        Boolean soundOn = getSharedPreferences("preference", MODE_PRIVATE).getBoolean("SOUND", true);

        if (soundOn){

            Toast.makeText(MainActivity.this, "sound on", Toast.LENGTH_LONG).show();
            GetIMEI.sound = true;
        }

        else {

            Toast.makeText(MainActivity.this, "sound off", Toast.LENGTH_LONG).show();
            GetIMEI.sound = false;
        }

        checkAndStart();






    }

    /**
     * checking wheather user exists or not
     * if exists thne starts the game either go for registration
     * @param s
     */
    public void show (String s) {


        if (s.equals("false")){


            Intent intent = new Intent(this, Registration.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(IMEI, telephonyManager.getDeviceId());
            finish();
            startActivity(intent);


        }

    else{
            /**
             * checking device screen configuration
             * here content_main is for normal screens, small is for smaller and tab is for larger screens
             */
        config = getResources().getConfiguration();



        if (config.smallestScreenWidthDp < 600 && config.smallestScreenWidthDp > 320) {
            setContentView(R.layout.content_main);

        } else if (config.smallestScreenWidthDp >= 600 && config.smallestScreenWidthDp < 700) {

            setContentView(R.layout.content_main_tab);

        }
        else if ( config.smallestScreenWidthDp >= 700){

            setContentView(R.layout.content_main_tab_large);
        } else {
            setContentView(R.layout.content_main_medium);
        }


        /**
         * initializing the variables
         * from layout by their id in xmls
         */


        startGame = (Button) findViewById(R.id.bstartGame);
        howToPlay = (Button) findViewById(R.id.bhowToPlay);
        highScores = (Button) findViewById(R.id.bhighScores);
        quit = (Button) findViewById(R.id.bquit);
        settings = (Button) findViewById(R.id.bsettings);
        mp = MediaPlayer.create(this, R.raw.click);            //click sound
        gnTxt = (TextView) findViewById(R.id.gntxt);


        gnTxt.setTypeface(fnt);
        startGame.setTypeface(fnt);
        howToPlay.setTypeface(fnt);
        highScores.setTypeface(fnt);
        settings.setTypeface(fnt);
        quit.setTypeface(fnt);

        /**
         * defining the start game buttons
         */

        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (GetIMEI.sound)mp.start();

                Intent n = new Intent("guessnext.noesis.GameModes");
                //n.putExtra(IMEI, telephonyManager.getDeviceId());
                startActivity(n);


            }
        });

        highScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (GetIMEI.sound)mp.start();
                Intent n = new Intent("guessnext.noesis.HighScore");
                startActivity(n);
            }
        });


        howToPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent n = new Intent("guessnext.noesis.HowToPlay");
                startActivity(n);
                if (GetIMEI.sound)mp.start();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GetIMEI.sound)mp.start();
                Intent n = new Intent("guessnext.noesis.Settings");
                startActivity(n);
            }
        });


        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (GetIMEI.sound) mp.start();
                exitConfirmation();
            }
        });
      }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * this function is called when user interrupt to close
     * pressing back button or exit button
     * asks for confirmation
     */
    public  void exitConfirmation (){

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirmation")
                .setMessage("Are you sure to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();


    }

    /**
     * when the navigation button (back built in device) is pressed
     * automatically called
     */

    @Override
    public void onBackPressed() {

        exitConfirmation();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /**
     * function to retrive json string from server
     *
     * @param url
     */


    public void getJsonAndParse(String url){


        /**
         * this class is for background to retrive json string
         */

        class GetJSON extends AsyncTask<String, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(MainActivity.this, "Please Wait...", null, true, true);

                config = getResources().getConfiguration();

                /**
                 * splash screen is used as a loader when it search for the user existance
                 */

                 if (config.smallestScreenWidthDp >= 600) {

                    setContentView(R.layout.splash_tab);
                } else {
                     setContentView(R.layout.splash);
                }


                blink = (ImageView) findViewById(R.id.imageView);

                final Animation animation = new AlphaAnimation(1, .1f); // Change alpha from fully visible to slightly invisible
                animation.setDuration(500); // duration - half a second
                animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
                animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
                animation.setRepeatMode(Animation.REVERSE);
                blink.startAnimation(animation);



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
               //loading.dismiss();
                //iv.startAnimation(an2);
                blink.clearAnimation();
                parseAndShow(s);
                //startTime = SystemClock.uptimeMillis();
                //handler.postDelayed(updateTimerThread, 0);
                //startTimer();

            }
        }

        /**
         * creating the object of GetJSON class to start aysnctask
         */
        GetJSON gj = new GetJSON();
        gj.execute(url);



    }


    public void parseAndShow (String jSONString){


        try {
            /**
             * getting json array from the json string
             * this will at first make the whole data to an object then split it into an array
             */
            JSONObject jsonObject = new JSONObject(jSONString);
            exist = jsonObject.getJSONArray(JSON_ARRAY); //converting it to an array of users
            JSONObject temp = exist.getJSONObject(0);
            //Toast.makeText(this, temp.getString("firstname")+" "+temp.getString("lastname")+" "+temp.getString("score"),
                      //  Toast.LENGTH_LONG).show();
            GetIMEI.firstName= temp.getString("firstname");
            GetIMEI.lastName = temp.getString("lastname");
            GetIMEI.score = temp.getString("score");

           //Toast.makeText(this, GetIMEI.firstName+" "+GetIMEI.lastName+" "+GetIMEI.score,
              //      Toast.LENGTH_LONG).show();

            show(temp.getString("check"));



        } catch (JSONException e) {
            e.printStackTrace();
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

           // Intent n = new Intent("guessnext.noesis.NormalMode");
            //startActivity(n);
            getJsonAndParse(JSON_URL+IMEI);

        }

        else
        {
            Toast.makeText(this, "No Internet Access.",
                    Toast.LENGTH_LONG).show();

            config = getResources().getConfiguration();

            if (config.smallestScreenWidthDp < 600 && config.smallestScreenWidthDp > 320) {
                setContentView(R.layout.content_oops);

            } else if (config.smallestScreenWidthDp >= 600 && config.smallestScreenWidthDp < 700) {

                setContentView(R.layout.content_oops_tab);

            }
            else if ( config.smallestScreenWidthDp >= 700){

                setContentView(R.layout.content_oops_tab_large);
            }
            else {
                setContentView(R.layout.content_oops_medium);
            }
           // setContentView(R.layout.content_oops);

            retry = (Button) findViewById(R.id.bretry);
            exit = (Button) findViewById(R.id.bexit);
            retry.setTypeface(fnt);
           // retry.setOnClickListener(this);
           // exit.setOnClickListener(this);
            retry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkAndStart();
                }
            });
            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish ();
                }
            });

        }


    }






}
