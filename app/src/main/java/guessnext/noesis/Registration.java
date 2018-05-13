package guessnext.noesis;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
public class Registration extends AppCompatActivity implements View.OnClickListener {

    TextView firstName,lastName,registration;
    EditText eFirstName,eLastName;
    Button register;
    Typeface fnt;

    Configuration config;

    /**
     * the URL that contains the question as jsor string
     */

    String IMEI,fname,lname;



    /**
     * name of the array that should be same to the json server array name
     */

   // private static final String JSON_ARRAY ="register";
    private static final String JSON_ARRAY ="userinfo";


    /**
     *  json array to get the array from the server
     */

    private JSONArray registerArray = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();


        IMEI = intent.getStringExtra(MainActivity.IMEI);

        fnt = Typeface.createFromAsset(getAssets(), "gnfont.ttf");

        config = getResources().getConfiguration();

        //setting layout according to the config

        if (config.smallestScreenWidthDp < 600 && config.smallestScreenWidthDp > 320) {

            setContentView(R.layout.content_registration);

        } else if (config.smallestScreenWidthDp >= 600 && config.smallestScreenWidthDp < 700) {

            setContentView(R.layout.content_registration_tab);

        }
        else if ( config.smallestScreenWidthDp >= 700){

            setContentView(R.layout.content_registration_tab_large);
        } else {
            setContentView(R.layout.content_registration_medium);
        }

       // setContentView(R.layout.content_registration);
        firstName = (TextView) findViewById(R.id.regfrstnametxt);
        lastName = (TextView) findViewById(R.id.reglastnametxt);
        registration = (TextView) findViewById(R.id.registrationtxt);

        register = (Button) findViewById(R.id.bregister);

        eFirstName = (EditText) findViewById(R.id.regfrstnameinput);
        eLastName = (EditText) findViewById(R.id.reglastnameinput);

        firstName.setTypeface(fnt);
        lastName.setTypeface(fnt);
        registration.setTypeface(fnt);
        register.setTypeface(fnt);

        register.setOnClickListener(this);


    }

        @Override
        public void onClick (View v){



            lname =  eLastName.getText().toString().trim();
            fname =  eFirstName.getText().toString().trim();

            if (check(fname) && check(lname) || check(fname) && lname.length()==0){

               // Toast.makeText(this, "true", Toast.LENGTH_LONG).show();
                String JSON_URL = "http://games.bulkstudio.com/guessnext/gn.php?newuser=true&firstname="+fname+"&lastname="+lname+"&imei="+IMEI;
                getJsonAndParse(JSON_URL);
            }
            else {
                Toast.makeText(this, "Name must contain only alphabets.. try again", Toast.LENGTH_LONG).show();
            }
          //  String JSON_URL = "http://games.bulkstudio.com/guessnext/gn.php?newuser=true&firstname="+fname+"&lastname="+lname+"&imei="+IMEI;
            //getJsonAndParse(JSON_URL);
            //giveOutput(null);


        }

    public void getJsonAndParse(String url){


        /**
         * this class is for background to retrive json string
         */

        class GetJSON extends AsyncTask<String, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Registration.this, "Please Wait...", null, true, true);
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
                loading.dismiss();
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
            registerArray= jsonObject.getJSONArray(JSON_ARRAY); //converting it to an array of users
            JSONObject temp = registerArray.getJSONObject(0);
            //Toast.makeText(this, temp.getString("check"), Toast.LENGTH_LONG).show();
            giveOutput(temp.getString("check"));
            //show(temp.getString("check"));



        } catch (JSONException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }


    }

    /**
     * checking the user input
     * @param s
     * @return
     */
    public boolean check (String s){

        boolean value = false;

        if (s.length()>=3){

            for (int i= 0;i<s.length();i++){

                if (s.charAt(i)>= 'A' && s.charAt(i)<= 'Z' || s.charAt(i) >='a' &&s.charAt(i)<='z'){

                    value = true;
                }
                else {
                    value = false;
                    return value;


                }
            }
        }


        return value;
    }

    public void giveOutput (String s){

       // if (s.equals("true")){

            Toast.makeText(this, "Successfully Registered.", Toast.LENGTH_LONG).show();
            Intent n = new Intent(this, MainActivity.class);
            finish();
            startActivity(n);
       // }
    }



    @Override
    public void onBackPressed (){

        super.onBackPressed();
        //MainActivity.class
    }

}
