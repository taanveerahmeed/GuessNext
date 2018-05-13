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
public class EditProfile extends AppCompatActivity implements View.OnClickListener {

    TextView editProfile,frstName,lastName;

    Button update;

    EditText frstNameInput,lastNameInput;

    String IFname,ILname,IMEI;

    private static final String JSON_ARRAY ="update";

    Configuration config;


    /**
     *  json array to get the array from the server
     */

    private JSONArray check = null;





    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        config = getResources().getConfiguration();

        /**
         * checking the screen config of the mobile and setting the layout accordingly
         */

        if (config.smallestScreenWidthDp < 600 && config.smallestScreenWidthDp > 320) {

            setContentView(R.layout.content_edit_profile);

        } else if (config.smallestScreenWidthDp >= 600 && config.smallestScreenWidthDp < 700) {

            setContentView(R.layout.content_edit_profile_tab);

        }
        else if ( config.smallestScreenWidthDp >= 700){

            setContentView(R.layout.content_edit_profile_tab_large);
        } else {
            setContentView(R.layout.content_edit_profile_medium);
        }

       // setContentView(R.layout.content_edit_profile);

        /**
         * initialinzing the variables of text views
         */

        editProfile = (TextView) findViewById(R.id.editprofiletxt);
        frstName = (TextView) findViewById(R.id.frstnametxt);
        lastName = (TextView) findViewById(R.id.lastnametxt);
        update = (Button) findViewById(R.id.bupdate);

        /**
         * the user inputs are initializing
         */

        frstNameInput = (EditText) findViewById(R.id.frstnameinput);
        lastNameInput = (EditText) findViewById(R.id.lastnameinput);

        Typeface fnt = Typeface.createFromAsset(getAssets(),"gnfont.ttf"); //creating the font

        /**
         * setting the fonts
         */
        editProfile.setTypeface(fnt);
        frstName.setTypeface(fnt);
        lastName.setTypeface(fnt);
        update.setTypeface(fnt);


        /**
         * the existing names are setting to the fields
         */
        frstNameInput.setText(GetIMEI.firstName);
        lastNameInput.setText(GetIMEI.lastName);
        update.setOnClickListener(this);




    }

    @Override public void onClick (View v){

        IFname = frstNameInput.getText().toString().trim();
        ILname = lastNameInput.getText().toString().trim();
        IMEI = GetIMEI.IMEI;

        /**
         * checking the user inputs and passing to the function fro editing
         */

        if (check(IFname) && check(ILname) || check(IFname) && ILname.length()==0){

            String URL = "http://games.bulkstudio.com/guessnext/gn.php?updateuser=true&firstname="+IFname+"&lastname="+ILname+"&imei="+IMEI;
            //Toast.makeText(this, URL,
            //      Toast.LENGTH_LONG).show();
            getJsonAndParse(URL);


        }
        else {
            Toast.makeText(this, "Name must contain only alphabets.. try again", Toast.LENGTH_LONG).show();
        }


    }

    public void getJsonAndParse(String url){


        /**
         * this class is for background to retrive json string
         */

        class GetJSON extends AsyncTask<String, Void, String> {

         //   ProgressDialog loading;

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
               // loading.dismiss();
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
            check= jsonObject.getJSONArray(JSON_ARRAY); //converting it to an array of users
            JSONObject temp = check.getJSONObject(0);
            //Toast.makeText(this, temp.getString("check"), Toast.LENGTH_LONG).show();
            giveOutput(temp.getString("check"));
            //show(temp.getString("check"));



        } catch (JSONException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }


    }

    /**
     * this function takes the output from the server which is true if successful
     * and showing the output
     * @param s
     */

    public void giveOutput (String s){

         if (s.equals("true")){

        Toast.makeText(this, "Successfull", Toast.LENGTH_LONG).show();
        Intent n = new Intent(this, Settings.class);
             n.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             GetIMEI.firstName = frstNameInput.getText().toString().trim();
             GetIMEI.lastName = lastNameInput.getText().toString().trim();
        finish();
        startActivity(n);
        }
        else{
             Toast.makeText(this, "Not Successfull.Please try again", Toast.LENGTH_LONG).show();
             Intent n = new Intent(this, Settings.class);
             n.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             finish();
             startActivity(n);
         }
    }

    /**
     * this function is checking the user inputs for names
     * and if valid return true if not return false
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

}
