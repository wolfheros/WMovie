package wolfheros.life.home.aboutme;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import wolfheros.life.home.R;

public class About extends AppCompatActivity {

    private int clickTimes;
    MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_me);
        mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.three_little_birds);
        FloatingActionButton floatingActionButton = findViewById(R.id.floating_actionbar);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickTimes+=1;
                while (clickTimes == 5){
                    mediaPlayer.start();
                    Snackbar.make(v,"感谢你的肯定，这首欢快的歌送给你",Snackbar.LENGTH_LONG).show();
                    clickTimes = 0;
                }
            }
        });


        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.tool_bar_collapsing);
        collapsingToolbarLayout.setTitle(getResources().getText(R.string.about));

        Toolbar myChildToolbar = (Toolbar)findViewById(R.id.about_tool_bar);
        setSupportActionBar(myChildToolbar);
        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();
        if (ab !=null){
            // Enable the Up button
            ab.setDisplayHomeAsUpEnabled(true);
            // Set Up button color to white;
            ab.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_white_24);

        }

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        //This method is called when the up button is pressed. Just the pop back stack.
        getSupportFragmentManager().popBackStack();
        this.finish();
        return true;
    }
}
