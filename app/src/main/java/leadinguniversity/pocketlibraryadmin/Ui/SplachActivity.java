package leadinguniversity.pocketlibraryadmin.Ui;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import leadinguniversity.pocketlibraryadmin.R;
import leadinguniversity.pocketlibraryadmin.Util.ActivityLancher;


public class SplachActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splach);

        //wait 3000 mSecs  --------------------------------
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    ActivityLancher.openLoginActivity(SplachActivity.this);

                } else {
                    ActivityLancher.openBooksActivity(SplachActivity.this);

                }
                finish();
            }
        }, 3000);

    }
}