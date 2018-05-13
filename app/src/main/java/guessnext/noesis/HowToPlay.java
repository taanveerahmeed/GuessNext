package guessnext.noesis;

import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import guessnext.noesis.guessnext.R;

public class HowToPlay extends AppCompatActivity {

    TextView hs,one,two,three,four,five;
    Configuration config;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.content_how_to_play);

        config = getResources().getConfiguration();

        if (config.smallestScreenWidthDp < 600 && config.smallestScreenWidthDp > 320) {
            setContentView(R.layout.content_how_to_play);

        } else if (config.smallestScreenWidthDp >= 600 && config.smallestScreenWidthDp < 700) {

            setContentView(R.layout.content_how_to_play_tab);

        }
        else if ( config.smallestScreenWidthDp >= 700){

            setContentView(R.layout.content_how_to_play_tab_large);
        } else {
            setContentView(R.layout.content_how_to_play_medium);
        }

        hs = (TextView) findViewById(R.id.textView19);
        one = (TextView) findViewById(R.id.textView14);
        two = (TextView) findViewById(R.id.textView16);
        three = (TextView) findViewById(R.id.textView17);
        four = (TextView) findViewById(R.id.textView18);
        if (config.smallestScreenWidthDp>320) five = (TextView) findViewById(R.id.textView13);
        Typeface fnt = Typeface.createFromAsset(getAssets(),"regular.ttf");
        Typeface fnt1 = Typeface.createFromAsset(getAssets(),"gnfont.ttf");

        hs.setTypeface(fnt1);
        one.setTypeface(fnt);
        two.setTypeface(fnt);
        three.setTypeface(fnt);
        four.setTypeface(fnt);
        if (config.smallestScreenWidthDp>320)five.setTypeface(fnt);



    }

}
