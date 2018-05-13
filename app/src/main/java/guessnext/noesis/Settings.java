package guessnext.noesis;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
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
import java.util.logging.Handler;

import guessnext.noesis.guessnext.R;

/**
 * Created by taanveer on 5/9/16.
 */


public class Settings extends AppCompatActivity implements View.OnClickListener {

    Button sound,editProfile,terminate,credit,back;

    TextView settingstxt;

    final android.os.Handler handler = new android.os.Handler();

    public String URL = "http://games.bulkstudio.com/guessnext/gn.php?terminate=true&imei="+GetIMEI.IMEI;

    private static final String JSON_ARRAY ="terminate";
    Configuration config;
    MediaPlayer mp;


    /**
     *  json array to get the array from the server
     */

    private JSONArray array = null;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        config = getResources().getConfiguration();

        //setting the layout accordingly

        if (config.smallestScreenWidthDp < 600 && config.smallestScreenWidthDp > 320) {
            setContentView(R.layout.content_settings);

        } else if (config.smallestScreenWidthDp >= 600 && config.smallestScreenWidthDp < 700) {

            setContentView(R.layout.content_settings_tab);

        }
        else if ( config.smallestScreenWidthDp >= 700){

            setContentView(R.layout.content_settings_tab_large);
        } else {
            setContentView(R.layout.content_settings_medium);
        }
       // setContentView(R.layout.content_settings);

        sound = (Button) findViewById(R.id.bsound);
        editProfile = (Button) findViewById(R.id.beditprofile);
        terminate = (Button) findViewById(R.id.bterminate);
        credit = (Button) findViewById(R.id.bcredit);
        back = (Button) findViewById(R.id.bback);

        settingstxt = (TextView) findViewById(R.id.settingstxt);

        Typeface fnt = Typeface.createFromAsset(getAssets(),"gnfont.ttf");

        if (!GetIMEI.sound){
            sound.setText("sound off");
        }

        settingstxt.setTypeface(fnt);
        sound.setTypeface(fnt);
        editProfile.setTypeface(fnt);
        terminate.setTypeface(fnt);
        credit.setTypeface(fnt);

        mp = MediaPlayer.create(this, R.raw.click);

        editProfile.setOnClickListener(this);
        terminate.setOnClickListener(this);
        credit.setOnClickListener(this);
        back.setOnClickListener(this);
        sound.setOnClickListener(this);

       // Toast.makeText(this, URL,
              //      Toast.LENGTH_LONG).show();


    }

    @Override public void onClick (View v){

        if (GetIMEI.sound)mp.start();

        switch(v.getId()) {

            case R.id.bsound:

                /**
                 * for saving the user settings shared preferences is used here
                 */

                if (GetIMEI.sound){
                    
                    GetIMEI.sound = false;
                    getSharedPreferences("preference",MODE_PRIVATE).edit().putBoolean("SOUND", false).commit();
                    sound.setText("sound off");
                }

                else {

                    GetIMEI.sound = true;
                    getSharedPreferences("preference",MODE_PRIVATE).edit().putBoolean("SOUND", true).commit();
                    sound.setText("sound on");
                }
                break;

            case R.id.beditprofile:

                Intent n = new Intent("guessnext.noesis.EditProfile");
                startActivity(n);

                break;

            case R.id.bterminate:
               // Intent n1 = new Intent("guessnext.noesis.GameOver");
                //startActivity(n1);
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Confirmation")
                        .setMessage("This will remove your account and scores. Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //finish();
                                terminateAccount (URL);
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

                break;
            case R.id.bcredit:
               // setContentView(R.layout.content_oops);
                TextView txt = new TextView(this);
               /* new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.btn_star_big_off)
                        .setTitle("Credit")
                        .setMessage(" © Team Noesis 2016\n IEEE AIUB STUDENT BRANCH")
                        .setNegativeButton("OK", null)
                        .show();*/
                AlertDialog.Builder popupBuilder = new AlertDialog.Builder(this);
                TextView myMsg = new TextView(this);
                myMsg.setText("© Team Noesis 2016\nIEEE AIUB STUDENT BRANCH");
                myMsg.setGravity(Gravity.CENTER);
                myMsg.setTextColor(Color.BLACK);
                myMsg.setTextSize(myMsg.getTextSize()-1);
                popupBuilder.setView(myMsg);
                popupBuilder.setIcon(android.R.drawable.btn_star_big_off);
                popupBuilder.setTitle("Credit");
                popupBuilder.setNegativeButton("OK", null);
                popupBuilder.show();
               // txt.setText(" © Team Noesis 2016\n IEEE AIUB STUDENT BRANCH");
                break;
            case R.id.bback:
                finish();
        }
    }

    /**
     * this function is used for terminating the account
     * @param url
     */
    private void terminateAccount(String url) {

        class GetJSON extends AsyncTask<String, Void, String> {

           // ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                //loading = ProgressDialog.show(Registration.this, "Please Wait...", null, true, true);
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
                    java.net.URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();//creating connection
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));// getting the json string

                    String json;
                    json = bufferedReader.readLine();

                    return json.trim(); //returning the json string

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
            array= jsonObject.getJSONArray(JSON_ARRAY); //converting it to an array of users
            JSONObject temp =array.getJSONObject(0);
            //Toast.makeText(this, temp.getString("check"), Toast.LENGTH_LONG).show();
            //giveOutput(temp.getString("check"));
            //show(temp.getString("check"));

            config = getResources().getConfiguration();

             if (config.smallestScreenWidthDp >= 600) {

                setContentView(R.layout.good_bye_tab);

            } else {
                setContentView(R.layout.good_bye);
            }
            //setContentView(R.layout.good_bye);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    completeExit();
                }
            }, 2000);





        } catch (JSONException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }


    }

    /***
     * this function completely exit from the app and launch the home screen
     */

    public void completeExit (){


       // finish();

        Intent intent = new Intent(Intent.ACTION_MAIN);
       intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);


    }
}
