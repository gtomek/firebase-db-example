package uk.org.tomek.firebasedbexample;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

import uk.org.tomek.firebasedbexample.model.UserProfile;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userProfileRef = database.getReference("userProfile");

        final UserProfile profile = UserProfile.newInstance(1, "test name", "test surname", "abc@defcom",
                new Date(SystemClock.currentThreadTimeMillis()));
        userProfileRef.setValue(profile.toFirebaseValue());

    }
}
