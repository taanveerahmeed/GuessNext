package guessnext.noesis;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import guessnext.noesis.guessnext.R;

/**
 *@author taanveer ahmeed
 */


/**
 * this activity is called via intent when extreme mode button is pressed
 */

public class ExtremeMode extends AppCompatActivity implements View.OnClickListener {

    /**
     * declaration of variables
     */

    Button A, B, C,lifeLine,back;
    TextView optA, optB, optC, question,timer,score,counterTxt,ansA,ansB,ansC;
    String correctAnswer;
    Configuration config;
    MediaPlayer correct,wrong,bg;
    public int index=0;
    final Handler handler = new Handler();
    public int arrayLengthJson = 0;
    private long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    int userScore = 0;
    int counterTick = 11;
    LinearLayout optionA,optionB,optionC;
    ImageView lifeOne,lifeTwo,lifeThree,lifeFour,lifeFive;
    static String userScoreString,flag;
    Animation animation;
    int errorCount = 0;

    CountDownTimer countDownTimer;




    /**
     * the URL that contains the question as jsor string
     */

    private static final String JSON_URL = "http://games.bulkstudio.com/guessnext/gn.php?question=true";

    /**
     * name of the array that should be same to the json server array name
     */

    private static final String JSON_ARRAY ="questions";
    public static String IMEI;


    /**
     *  json array to get the array from the server
     */

    private JSONArray questions = null;


    /**
     * on create function of the start game activity
     * automatically called when the activity fires
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * checking device screen config
         * and setting the layout suitable for the device
         */

        config = getResources().getConfiguration();

        if (config.smallestScreenWidthDp < 400 && config.smallestScreenWidthDp > 320)
        {
            setContentView(R.layout.content_start_game);
            // Toast.makeText(this, "Normal", Toast.LENGTH_LONG).show();
        }

        else if (config.smallestScreenWidthDp < 600 && config.smallestScreenWidthDp >= 400) {

            setContentView(R.layout.content_start_game_large);
            //Toast.makeText(this, "tab", Toast.LENGTH_LONG).show();
        }

        else if (config.smallestScreenWidthDp>=600 && config.smallestScreenWidthDp <750){

            setContentView(R.layout.content_start_game_tab);

        }

        else if (config.smallestScreenWidthDp >=750){

            setContentView(R.layout.content_start_game_tab_large);
        }


        else {
            setContentView(R.layout.content_start_game_medium);
            //Toast.makeText(this, "medium", Toast.LENGTH_LONG).show();
        }


        Intent intent = getIntent();

        String jsonString = intent.getStringExtra(GameModes.JSON);
        IMEI =  GetIMEI.IMEI;

     //  Toast.makeText(this, IMEI,
           //    Toast.LENGTH_LONG).show();


        /**
         * intitialization of variables
         */

        //buttons initializing

        A = (Button) findViewById(R.id.buttonA);
        B = (Button) findViewById(R.id.buttonB);
        C = (Button) findViewById(R.id.buttonC);
        lifeLine = (Button) findViewById(R.id.buttonLife);
        back = (Button) findViewById(R.id.back);

        //end of buttons

        //image view initialization

        lifeOne = (ImageView) findViewById(R.id.lifeone);
        lifeTwo = (ImageView) findViewById(R.id.lifetwo);
        lifeThree = (ImageView) findViewById(R.id.lifethree);
        lifeFour = (ImageView) findViewById(R.id.lifefour);
        lifeFive = (ImageView) findViewById(R.id.lifefive);

        lifeOne.setVisibility(View.INVISIBLE);
        lifeTwo.setVisibility(View.INVISIBLE);
        lifeThree.setVisibility(View.INVISIBLE);
        lifeFour.setVisibility(View.INVISIBLE);
        lifeFive.setVisibility(View.INVISIBLE);

        //end of image view initialization

        //text views initializing

        optA = (TextView) findViewById(R.id.optionA);
        optB = (TextView) findViewById(R.id.optionB);
        optC = (TextView) findViewById(R.id.optionC);
        ansA = (TextView) findViewById(R.id.ansa);
        ansB = (TextView) findViewById(R.id.ansb);
        ansC = (TextView) findViewById(R.id.ansc);
        question = (TextView) findViewById(R.id.question);
        score = (TextView) findViewById(R.id.score);
        timer = (TextView) findViewById(R.id.timer);
        TextView txtTime = (TextView) findViewById(R.id.tm);
        TextView txtScore = (TextView) findViewById(R.id.scr);
        counterTxt = (TextView) findViewById(R.id.countertxt);

        //end of text views initializing
        /**
         * creating and setting the font
         */
        Typeface fnt = Typeface.createFromAsset(getAssets(),"gnfont.ttf");

        timer.setTypeface(fnt);
        score.setTypeface(fnt);
        txtTime.setTypeface(fnt);
        txtScore.setTypeface(fnt);


        A.setTypeface(fnt);
        B.setTypeface(fnt);
        C.setTypeface(fnt);
        counterTxt.setTypeface(fnt);
        ansA.setTypeface(fnt);
        ansB.setTypeface(fnt);
        ansC.setTypeface(fnt);



        optionA = (LinearLayout) findViewById(R.id.layoutA);
        optionB = (LinearLayout) findViewById(R.id.layoutB);
        optionC = (LinearLayout) findViewById(R.id.layoutC);

        //sounds  initializing

        correct = MediaPlayer.create(this, R.raw.correctringtone);
        wrong = MediaPlayer.create(this, R.raw.wrong);
        bg = MediaPlayer.create(this, R.raw.hrtbtldringtone);

        //end of sounds initializing


        //checking the user settings sound on or off
       if (GetIMEI.sound){
           bg.start();
           bg.setLooping(true);         //playing the heart beat sound
       }



        /**
         *  heart beat animation
         */


        animation = new AlphaAnimation(1, .1f); // Change alpha from fully visible to slightly invisible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE);
        lifeLine.startAnimation(animation);

        //end of animation


        /**
         * setting the action listener for the buttons
         * on click what will happen is defined onClick method
         */

        A.setOnClickListener(this);
        B.setOnClickListener(this);
        C.setOnClickListener(this);
        lifeLine.setOnClickListener(this);
        back.setOnClickListener(this);


        // getJsonAndParse(JSON_URL); //calling the parsing function

        parseAndShow(jsonString);
        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(updateTimerThread, 0);




    }


// end of onCreate method

    /**
     * this the defenition of time that is showed in the game screen
     * right most is milisecond
     * middle is second
     * left most is minute
     */

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updatedTime / 1000);
            int mins = secs / 60;
            int hour = mins/ 60;
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            timer.setText("0" + hour + ":"
                    + String.format("%02d", mins) + ":"
                    + String.format("%02d", secs));
            handler.postDelayed(this, 0);
        }

    };


    /**
     * definiton of navigation button (built in device)
     * if back button is pressed what will happen is defined here
     */

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirmation")
                .setMessage("Will you finish the game ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        countDownTimer.cancel();
                        bg.stop();
                        Intent intent = new Intent("guessnext.noesis.GameModes");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        finish();
                        startActivity(intent);
                    }

                })
                .setNegativeButton("No", null)
                .show();
        // finish();
        //super.onBackPressed();
        //bg.stop();
        //countDownTimer.cancel();
    }


    /**
     * onClick method defines if the buttons are pressed what will happen
     * button A,B,C and life line are defined here
     * @param v
     */

    @Override
    public void onClick(View v) {



        switch(v.getId()) {

            // defining if A is pressed

            case R.id.buttonA:

                countDownTimer.cancel();


                //button B & C will be disabled

                A.setEnabled(false);
                B.setEnabled(false);
                C.setEnabled(false);

                //if correct ans lies in option A

                if (correctAnswer.equals("A")){

                    if (GetIMEI.sound) correct.start();
                    A.setBackgroundResource(R.drawable.btn_green);
                    optionA.setBackgroundResource(R.drawable.answer_green);
                    userScore = userScore+10;



                }
                //if correct ans lies in option B

                else if (correctAnswer.equals("B"))

                {
                    if (GetIMEI.sound) wrong.start();
                    A.setBackgroundResource(R.drawable.btn_red);
                    optionA.setBackgroundResource(R.drawable.answer_red);
                    optionB.setBackgroundResource(R.drawable.answer_green);

                    //userScore = userScore-10;
                    errorCount++;
                    //score.setText(userScore);

                }

                //if correct ans is in option C

                else {


                    if (GetIMEI.sound) wrong.start();
                    A.setBackgroundResource(R.drawable.btn_red);
                    optionA.setBackgroundResource(R.drawable.answer_red);
                    optionC.setBackgroundResource(R.drawable.answer_green);
                    errorCount++;
                    // userScore = userScore-10;


                }
                index++;
                score.setText(" "+userScore);
                countWrongAndDisappearLife();
                /**
                 * this block waits for 1 sec to show the reaction
                 * delayed is used for showing the visual effect in UI
                 * if not delayed the effects will not be shown
                 */
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        show();
                    }
                }, 1000);
                counterTick = 11;
                break;
            //end of A defenition

//defining B is pressed

            case R.id.buttonB:
                countDownTimer.cancel();

                //option A and C will be disabled

                A.setEnabled(false);
                B.setEnabled(false);
                C.setEnabled(false);

                //if correct ans lies in B

                if (correctAnswer.equals("B")){

                    if (GetIMEI.sound) correct.start();
                    B.setBackgroundResource(R.drawable.btn_green);
                    optionB.setBackgroundResource(R.drawable.answer_green);

                    userScore = userScore+10;



                }

                //if correct ans lies in A

                else if (correctAnswer.equals("A")){

                    if (GetIMEI.sound) wrong.start();
                    B.setBackgroundResource(R.drawable.btn_red);
                    optionA.setBackgroundResource(R.drawable.answer_green);
                    optionB.setBackgroundResource(R.drawable.answer_red);
                    // userScore = userScore-10;
                    errorCount++;


                }

                //if option C is the correct answer

                else {


                    if (GetIMEI.sound) wrong.start();
                    B.setBackgroundResource(R.drawable.btn_red);
                    optionC.setBackgroundResource(R.drawable.answer_green);
                    optionB.setBackgroundResource(R.drawable.answer_red);
                    errorCount++;
                    //userScore = userScore-10;


                }
                index++;

                /**
                 * this block waits for 1 sec to show the reaction
                 * delayed is used for showing the visual effect in UI
                 * if not delayed the effects will not be shown
                 */

                score.setText(" "+userScore);

                countWrongAndDisappearLife();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        show();
                    }
                }, 1000);
                counterTick = 11;

                break;
//end of defenition if button B

//defenition of button C

            case R.id.buttonC:

                countDownTimer.cancel();

                //button A and C will be disabled

                A.setEnabled(false);
                B.setEnabled(false);
                C.setEnabled(false);


                //correct ans is in C
                if  (correctAnswer.equals("C")){

                    if (GetIMEI.sound)correct.start();
                    C.setBackgroundResource(R.drawable.btn_green);
                    optionC.setBackgroundResource(R.drawable.answer_green);
                    userScore = userScore+10;


                }
                //correct ans is in A

                else if (correctAnswer.equals("A")){


                    if (GetIMEI.sound) wrong.start();
                    C.setBackgroundResource(R.drawable.btn_red);
                    optionA.setBackgroundResource(R.drawable.answer_green);
                    optionC.setBackgroundResource(R.drawable.answer_red);
                    errorCount++;
                    // userScore = userScore-10;


                }

                //correct ans is in B

                else{

                    if (GetIMEI.sound) wrong.start();
                    C.setBackgroundResource(R.drawable.btn_red);
                    optionB.setBackgroundResource(R.drawable.answer_green);
                    optionC.setBackgroundResource(R.drawable.answer_red);
                    // userScore = userScore-10;
                    errorCount++;


                }

                index++;

                /**
                 * this block waits for 1 sec to show the reaction
                 * delayed is used for showing the visual effect in UI
                 * if not delayed the effects will not be shown
                 */
                score.setText(" "+userScore);

                countWrongAndDisappearLife();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        show();
                    }
                }, 1000);

                counterTick = 11;

                break;
//end of button C

            case R.id.buttonLife:
                lifeLine.setEnabled(false);
                v.clearAnimation();  //on clicking heart bit will stop
                if (userScore>=5) userScore = userScore-5;
                checkAndSuggest();
                break;

            case R.id.back:


                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Confirmation")
                        .setMessage("Will you finish the game ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                countDownTimer.cancel();
                                bg.stop();
                                Intent intent = new Intent("guessnext.noesis.GameModes");
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                finish();
                                startActivity(intent);
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
                break;



        }

    }


    /**
     * this funtion count down for 10 seconds. if player dont ans
     * new question will arrive
     */

    public void startTimer (){

        countDownTimer = new CountDownTimer(11000, 1000) {

            public void onTick(long millisUntilFinished) {

                counterTxt.setText(""+ (--counterTick));
            }

            public void onFinish() {
                index++;
                errorCount++;
                countWrongAndDisappearLife();
                if (errorCount < 1)show();
                counterTick = 11;

            }


        }.start();

    }


    /**
     * this function is for parsing the obtained json string from the server
     * after getting the json string this funcrion is called from the post method in asynctask class
     * @param jSONString
     */



    public void parseAndShow (String jSONString){



        try {
            /**
             * getting json array from the json string
             * this will at first make the whole data to an object then split it into an array
             */
            JSONObject jsonObject = new JSONObject(jSONString);
            questions = jsonObject.getJSONArray(JSON_ARRAY); //converting it to an array of users
            questions = shuffleJsonArray(questions); //shuffling the json array for random selection of questions
            arrayLengthJson = questions.length(); //initializing arraylength json to know when the array is finished. it will be needed later
            show ();

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * this function is for shuffling the json array
     * here simple swapping algorithm has been used
     * random indices have been used for shuffling
     * @param array
     * @return
     * @throws JSONException
     */

    public static JSONArray shuffleJsonArray (JSONArray array) throws JSONException {

        Random rnd = new Random();

        for (int i = array.length() - 1; i >= 0; i--)
        {
            int j = rnd.nextInt(i + 1);
            Object object = array.get(j);
            array.put(j, array.get(i));
            array.put(i, object);
        }
        return array;
    }


    /**
     * this function is for shuffling the answers array
     * same method used in shuffling the json array has been used here
     * @param array
     * @return
     */

    public static String[] shuffleAmswers (String array[])  {

        Random rnd = new Random();
        for (int i = array.length - 1; i >= 0; i--)
        {
            int j = rnd.nextInt(i + 1);
            String object = array[j];
            array[j] = array[i];
            array[i] = object;
        }
        return array;
    }


    /**
     * this method is for showing the questions and answers in the UI
     * here index is a public variable that is used for accessing the json array
     * json array is already shuffled
     * every time this function is called from on click index is increamented by 1
     */

    public void show () {

        if (arrayLengthJson != 0) { //checking wheather it is reached to the end of the json array

            try {

                /**
                 * accessing to the array by index number
                 * and accessing them by their attribute name
                 * finally showing the output
                 */

                JSONObject jsonObject = questions.getJSONObject(index); //getting a json objct from the json array by index
                question.setText(jsonObject.getString("question"));    //setting the question

                /**
                 * getiing the answers from the json object and shuffling the answers
                 * also getting the correct answer
                 */
                String[] questionsArray =
                        {jsonObject.getString("A").trim(),
                                jsonObject.getString("B").trim(),
                                jsonObject.getString("C").trim()};

                questionsArray = shuffleAmswers(questionsArray);
                String correct = (jsonObject.getString("correct")).toLowerCase().trim();

                /**
                 * comaparing the answers with the correct answer
                 * and setting the correct answer
                 * correctAnswer is a public variable of the class
                 */

                for (int i = 0; i < questionsArray.length; i++) {

                    if (correct.equals(questionsArray[i].toLowerCase())) {

                        switch (i) {

                            case 0:
                                correctAnswer = "A";
                                break;
                            case 1:
                                correctAnswer = "B";
                                break;
                            case 2:
                                correctAnswer = "C";
                                break;

                        }
                    }


                }

                /**
                 * checking the length of the answers
                 * if >30 it starts from the begining else its alignmnt will be center
                 */

                if (questionsArray[0].length()<=30) optA.setGravity(Gravity.CENTER);
                else optA.setGravity(Gravity.CENTER_VERTICAL);

                if (questionsArray[1].length()<=30) optB.setGravity(Gravity.CENTER);
                else optB.setGravity(Gravity.CENTER_VERTICAL);

                if (questionsArray[2].length()<=30) optC.setGravity(Gravity.CENTER);
                else optC.setGravity(Gravity.CENTER_VERTICAL);

                /**
                 * settign the options for selection
                 * answers are shullfeld
                 */
                optA.setText(questionsArray[0]);
                optB.setText(questionsArray[1]);
                optC.setText(questionsArray[2]);


                resetInterface();
                arrayLengthJson--;
                startTimer();





            } catch (Exception ex) {

            }
        }
        else { // if it is the end of json array

            Toast.makeText(this, "No More questions", Toast.LENGTH_LONG).show();
            bg.stop();

            userScoreString = ""+userScore;
            Intent intent = new Intent(this, GameOver.class);
            //intent.putExtra(IMEI, IMEI);
            //intent.putExtra(userScoreString, score.getText().toString()+"-"+timer.getText().toString());
            //intent.putExtra(flag, "extrememode");
            GetScoreAndTime.Score = score.getText().toString().trim();
            GetScoreAndTime.Time = timer.getText().toString();
            finish();
            startActivity(intent);

        }


    }


    /**
     * this function is for setting all the components of the UI to its initial state
     */

    public void resetInterface (){

        optionA.setBackgroundResource(R.drawable.answer_regular);
        optionB.setBackgroundResource(R.drawable.answer_regular);
        optionC.setBackgroundResource(R.drawable.answer_regular);

        A.setBackgroundResource(R.drawable.btn_regular);
        B.setBackgroundResource(R.drawable.btn_regular);
        C.setBackgroundResource(R.drawable.btn_regular);

        A.setEnabled(true);
        B.setEnabled(true);
        C.setEnabled(true);

        lifeLine.setEnabled(true);

        lifeLine.startAnimation(animation);


    }

    /**
     * this is for if user leave the game
     * like home button is pressed or anything leaving hint is found
     * called automatically
     */

    @Override
    protected void onUserLeaveHint(){

        bg.stop();
        finish();
        countDownTimer.cancel();
        super.onUserLeaveHint();
    }

    /**
     * this method counts the error of the user and disappear life
     */

    public void countWrongAndDisappearLife () {

        switch (errorCount){


            case 1:
                //lifeOne.setVisibility(View.INVISIBLE);
                // Toast.makeText(this, "Game Over", Toast.LENGTH_LONG).show();
                bg.stop();
                userScoreString = ""+userScore;
                Intent intent = new Intent(this, GameOver.class);
                //intent.putExtra(IMEI, IMEI);
                //intent.putExtra(userScoreString, score.getText().toString()+"-"+timer.getText().toString());
                //intent.putExtra(flag, "extrememode");
                GetScoreAndTime.Score = score.getText().toString().trim();
                GetScoreAndTime.Time = timer.getText().toString();
                finish();
                startActivity(intent);
                break;



        }

    }

    /**
     * this function check the correct answer and randomly suggest an answer
     */

    public void checkAndSuggest (){

        Random r = new Random();
        int random = r.nextInt(2);

        switch (correctAnswer){

            case "A":
                if (random == 1){

                    C.setEnabled(false);
                    optionC.setBackgroundResource(R.drawable.answer_red);

                }

                else{

                    B.setEnabled(false);
                    optionB.setBackgroundResource(R.drawable.answer_red);
                }
                break;
            case "B":
                if (random == 1){

                    C.setEnabled(false);
                    optionC.setBackgroundResource(R.drawable.answer_red);

                }

                else{

                    A.setEnabled(false);
                    optionA.setBackgroundResource(R.drawable.answer_red);
                }
                break;
            case "C":
                if (random == 1){

                    B.setEnabled(false);
                    optionB.setBackgroundResource(R.drawable.answer_red);

                }

                else{

                    A.setEnabled(false);
                    optionA.setBackgroundResource(R.drawable.answer_red);
                }
                break;
        }



    }


}




