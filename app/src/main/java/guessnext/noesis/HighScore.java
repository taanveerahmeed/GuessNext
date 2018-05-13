package guessnext.noesis;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
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

import guessnext.noesis.guessnext.R;

/**
 * this activity is called via intent when highscores button is pressed
 */
public class HighScore extends AppCompatActivity {

    /**
     * declaration of variables
     */

    TextView highScore,yourScore,topScores,contentHighScores,score;
    Button prev,nxt,back;
    Configuration config;

    MediaPlayer mp;

    TextView name,scores, name1,name2,name3,name4,name5,score1,score2,score3,score4,score5;

    int arrayLengthJson,index,count;


    /**
     * the URL that contains the question as jsor string
     */

    private static final String JSON_URL = "http://games.bulkstudio.com/guessnext/gn.php?highscores=true";

    /**
     * name of the array that should be same to the json server array name
     */

    private static final String JSON_ARRAY ="highscores";


    /**
     *  json array to get the array from the server
     */

    private JSONArray usersAll = null;


    /**
     * on create function of the start game activity
     * automatically called when the activity fires
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        config = getResources().getConfiguration();

        //checking the phone display confing and setting layout accordingly

        if (config.smallestScreenWidthDp < 600 && config.smallestScreenWidthDp > 320) {
            setContentView(R.layout.content_high_score);
        } else if (config.smallestScreenWidthDp >= 600) {

            setContentView(R.layout.content_high_score_tab);
        }
        else {
            setContentView(R.layout.content_high_score_medium);
        }
        //setContentView(R.layout.content_high_score);

        highScore = (TextView) findViewById(R.id.highscoretxt2);
        yourScore = (TextView) findViewById(R.id.txtyourscore);
        topScores = (TextView) findViewById(R.id.topscorestxt);
        //contentHighScores = (TextView) findViewById(R.id.hglobal);
        score = (TextView) findViewById(R.id.txtyourscore2);

        //texview names
        name = (TextView) findViewById(R.id.textView);
        name1 = (TextView) findViewById(R.id.textView3);
        name2 = (TextView) findViewById(R.id.textView5);
        name3 = (TextView) findViewById(R.id.textView7);
        name4 = (TextView) findViewById(R.id.textView9);
        name5 = (TextView) findViewById(R.id.textView11);

        //text view scores
        scores = (TextView) findViewById(R.id.textView2);
        score1 = (TextView) findViewById(R.id.textView4);
        score2 = (TextView) findViewById(R.id.textView6);
        score3 = (TextView) findViewById(R.id.textView8);
        score4 = (TextView) findViewById(R.id.textView10);
        score5 = (TextView) findViewById(R.id.textView12);

        back = (Button) findViewById(R.id.hghback);

        mp = MediaPlayer.create(this, R.raw.click);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (GetIMEI.sound) mp.start();
                finish();
            }
        });

        prev = (Button) findViewById(R.id.bprevious);
        nxt = (Button) findViewById(R.id.bnext);

        //creating font
        Typeface fnt = Typeface.createFromAsset(getAssets(), "gnfont.ttf");

        //setting fonts
        highScore.setTypeface(fnt);
        yourScore.setTypeface(fnt);
        topScores.setTypeface(fnt);
        name.setTypeface(fnt);
        scores.setTypeface(fnt);
        score.setTypeface(fnt);

        prev.setTypeface(fnt);
        nxt.setTypeface(fnt);
        prev.setEnabled(false);
        prev.setVisibility(View.INVISIBLE);
        nxt.setVisibility(View.INVISIBLE);

        /**
         * at first prev button is disabled
         * in every screen 5 scores are displayed
         * when prev button is pressed it sets the iteration of the array 5 scores back and count to 10 scores back
         */
        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GetIMEI.sound) mp.start();

               // setContentView(R.layout.content_oops);
                if (index>=5){

                    clear();
                    index = index - 5;
                    count = count - 10;
                    if (count == 0){   //if count is 0 that indicates it is in the first page so the previous is disabled
                        prev.setEnabled(false);
                        prev.setVisibility(View.INVISIBLE);
                    }
                    show ();
                    nxt.setEnabled(true);
                    nxt.setVisibility(View.VISIBLE);

                }

                else{

                    prev.setEnabled(false);
                    prev.setVisibility(View.VISIBLE);
                }
            }

        });

        nxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GetIMEI.sound) mp.start();

                if (count < arrayLengthJson){

                    clear();
                    index = index+5;
                    show ();
                    prev.setEnabled(true);
                    prev.setVisibility(View.VISIBLE);
                }



                else
                {
                    nxt.setEnabled(false);
                    nxt.setVisibility(View.INVISIBLE);
                }


            }
        });
        score.setText(GetIMEI.score);

        getJsonAndPass(JSON_URL);


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
                //setContentView(R.layout.splash);

                //blink = (ImageView) findViewById(R.id.imageView);

               // final Animation animation = new AlphaAnimation(1, .1f); // Change alpha from fully visible to slightly invisible
               // animation.setDuration(500); // duration - half a second
               // animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
               // animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
               // animation.setRepeatMode(Animation.REVERSE);
               // blink.startAnimation(animation);

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
               // blink.clearAnimation();
                parseAndShow (s);

                // loading.dismiss();


            }
        }

        /**
         * creating the object of GetJSON class to start aysnctask
         */
        GetJSON gj = new GetJSON();
        gj.execute(url);



    }

    public void parseAndShow (String jSONString){

       // Toast.makeText(this, jSONString, Toast.LENGTH_LONG).show();

        try {
            /**
             * getting json array from the json string
             * this will at first make the whole data to an object then split it into an array
             */
            JSONObject jsonObject = new JSONObject(jSONString);
            usersAll = jsonObject.getJSONArray(JSON_ARRAY); //converting it to an array of users
           // questions = shuffleJsonArray(questions); //shuffling the json array for random selection of questions
            arrayLengthJson = usersAll.length(); //initializing arraylength json to know when the array is finished. it will be needed later
            if (arrayLengthJson > 5){

                nxt.setEnabled(true);
                nxt.setVisibility(View.VISIBLE);
            }
            show ();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * this funtion is for showing the scores to the interface
     * it counts the displayed scores by count variable
     * if count and length of the array becomes same the next button is disabled and disappears
     */

    public void show () {

       // Toast.makeText(this, ""+arrayLengthJson, Toast.LENGTH_LONG).show();

        count= count+5;
        if (count >= arrayLengthJson){

            nxt.setEnabled(false);
            nxt.setVisibility(View.INVISIBLE);

        }

       // Toast.makeText(this, ""+count, Toast.LENGTH_LONG).show();

        if (arrayLengthJson != 0) { //checking wheather it is reached to the end of the json array

            try {



                name1.setText(usersAll.getJSONObject(index).getString("firstname"));
                score1.setText(usersAll.getJSONObject(index).getString("score"));

                name2.setText(usersAll.getJSONObject(index+1).getString("firstname"));
                score2.setText(usersAll.getJSONObject(index+1).getString("score"));

                name3.setText(usersAll.getJSONObject(index+2).getString("firstname"));
                score3.setText(usersAll.getJSONObject(index+2).getString("score"));

                name4.setText(usersAll.getJSONObject(index+3).getString("firstname"));
                score4.setText(usersAll.getJSONObject(index+3).getString("score"));

                name5.setText(usersAll.getJSONObject(index+4).getString("firstname"));
                score5.setText(usersAll.getJSONObject(index+4).getString("score"));

               // count= count+5;
                //Toast.makeText(this, ""+count, Toast.LENGTH_LONG).show();

              //  if (count>= arrayLengthJson){
                 //   index = index - 5;
             //   }


            } catch (Exception ex) {

            }
        }
        else { // if it is the end of json array

           // Toast.makeText(this, "No More questions", Toast.LENGTH_LONG).show();
            //bg.stop();
           // finish();

        }


    }

    /**
     * clearing the text fields
     */

    public void clear (){

        name1.setText("");
        score1.setText("");

        name2.setText("");
        score2.setText("");

        name3.setText("");
        score3.setText("");

        name4.setText("");
        score4.setText("");

        name5.setText("");
        score5.setText("");

    }




}
