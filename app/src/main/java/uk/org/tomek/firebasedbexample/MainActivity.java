package uk.org.tomek.firebasedbexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;

import java.util.Date;

import rx.functions.Action1;
import rx.functions.Func1;
import timber.log.Timber;
import uk.org.tomek.firebasedbexample.model.UserProfile;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // enable local persistance
        database.setPersistenceEnabled(true);
        DatabaseReference userProfileRef = database.getReference("userProfile");
        userProfileRef.keepSynced(true);

        final UserProfile profile = UserProfile
                .newInstance(1, "test name", "test surname", "abc@defcom", new Date(System.currentTimeMillis()));
        userProfileRef.setValue(profile.toFirebaseValue());

        RxFirebaseDatabase.observeValueEvent(userProfileRef.getRoot().child("userProfile"), new Func1<DataSnapshot, UserProfile>() {
            @Override
            public UserProfile call(DataSnapshot dataSnapshot) {
                return UserProfile.newInstance(dataSnapshot);
            }
        }).subscribe(new Action1<UserProfile>() {
            @Override
            public void call(UserProfile userProfile) {
                Timber.d("Profile read from Firebase %s", userProfile);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Timber.e(throwable, "Error reading Firebase db");
            }
        });

    }
}
