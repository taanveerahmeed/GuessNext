package guessnext.noesis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
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

import guessnext.noesis.guessnext.MainActivity;
import guessnext.noesis.guessnext.R;

/**
 * Created by taanveer on 5/9/16.
 */
public class GameOver extends AppCompatActivity implements View.OnClickListener{


    TextView txtScore,score,txtHighScore,highScore,txtgHighScore,gHighScore;
    String userScore,time;

    Button menu;

    public static String IMEI;

    private static final String JSON_ARRAY ="highscore";

    private JSONArray highScoreArray = null;

    Configuration config;



        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            config = getResources().getConfiguration();

            if (config.smallestScreenWidthDp < 600 && config.smallestScreenWidthDp > 320) {
                setContentView(R.layout.content_game_over);

            } else if (config.smallestScreenWidthDp >= 600 && config.smallestScreenWidthDp < 700) {

                setContentView(R.layout.content_game_over_tab);

            }
            else if ( config.smallestScreenWidthDp >= 700){

                setContentView(R.layout.content_game_over_tab_large);
            } else {
                setContentView(R.layout.content_game_over_medium);
            }
       // setContentView(R.layout.content_game_over);


            IMEI =GetIMEI.IMEI;






            userScore = GetScoreAndTime.Score;
            time = GetScoreAndTime.Time;

         //  Toast.makeText(this, userScore+" "+time,
             //     Toast.LENGTH_LONG).show();

        txtScore = (TextView) findViewById(R.id.newscoretxt);
        score = (TextView) findViewById(R.id.newscore);
        txtHighScore = (TextView) findViewById(R.id.highscoretxt);
        highScore = (TextView) findViewById(R.id.highscore);
        txtgHighScore = (TextView) findViewById(R.id.ghighscoretxt);
        gHighScore = (TextView) findViewById(R.id.ghighscore);

        menu = (Button) findViewById(R.id.bmenu);
            menu.setOnClickListener(this);
        Typeface fnt = Typeface.createFromAsset(getAssets(),"gnfont.ttf");

        txtScore.setTypeface(fnt);
        score.setTypeface(fnt);
        txtHighScore.setTypeface(fnt);
        highScore.setTypeface(fnt);
        txtgHighScore.setTypeface(fnt);
        gHighScore.setTypeface(fnt);

            String JSON_URL = "http://games.bulkstudio.com/guessnext/gn.php?updatescore=true&imei="+IMEI+"&score="+userScore.trim()+"&time="+time.trim();
            getJsonAndParse(JSON_URL);



    }


    public void getJsonAndParse(String url){


        /**
         * this class is for background to retrive json string
         */

        class GetJSON extends AsyncTask<String, Void, String> {

           // ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
               // loading = ProgressDialog.show(Registration.this, "Please Wait...", null, true, true);
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

                parseAndShow(s);


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
            highScoreArray= jsonObject.getJSONArray(JSON_ARRAY); //converting it to an array of users
            JSONObject temp = highScoreArray.getJSONObject(0);
            //Toast.makeText(this, temp.getString("newscore"), Toast.LENGTH_LONG).show();
            score.setText(temp.getString("newscore"));
            highScore.setText(temp.getString("userscore"));
            gHighScore.setText(temp.getString("globalscore"));

            if (Integer.parseInt(highScore.getText().toString().trim())> Integer.parseInt(GetIMEI.score)){

                GetIMEI.score = highScore.getText().toString().trim();
            }
            //giveOutput(temp.getString("check"));
            //show(temp.getString("check"));




        } catch (JSONException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }


    }






    @Override
    public void onBackPressed (){

        super.onBackPressed();
        finish();
        Intent intent = new Intent(this, GameModes.class);
        startActivity(intent);
    }


    @Override
    public void onClick (View v){


        switch(v.getId()) {

            case R.id.bmenu:
                finish();
               // Intent intent = new Intent(this, MainActivity.class);
               // startActivity(intent);
                break;


        }
    }
}
