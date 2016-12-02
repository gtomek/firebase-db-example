package uk.org.tomek.firebasedbexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;
import java.util.Date;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;
import uk.org.tomek.firebasedbexample.model.Medication;
import uk.org.tomek.firebasedbexample.model.UserProfile;

import static uk.org.tomek.firebasedbexample.model.UserProfile.newInstance;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.edittext_name)
    EditText mNameEditText;
    @BindView(R.id.edittext_surname)
    EditText mSurnameEditText;
    @BindView(R.id.edittext_email)
    EditText mEmailEditText;
    private int mLastProfileId;
    private DatabaseReference mUserProfileReference;
    private DatabaseReference mMedicationsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        // enable local persistance
        database.setPersistenceEnabled(true);
        mUserProfileReference = database.getReference("userProfile");
        mUserProfileReference.keepSynced(true);

        RxFirebaseDatabase.observeValueEvent(mUserProfileReference.getRoot().child("userProfile"),
                new Func1<DataSnapshot, UserProfile>() {
                    @Override
                    public UserProfile call(DataSnapshot dataSnapshot) {
                        return UserProfile.newInstance(dataSnapshot);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<UserProfile>() {
            @Override
            public void call(UserProfile userProfile) {
                Timber.d("Profile read from %s Firebase %s", Thread.currentThread().getName(),
                        userProfile);
                setProfileFields(userProfile);
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Timber.e(throwable, "Error reading Firebase db");
            }
        });

        //final DatabaseReference medicationsReference =
        //    FirebaseUtils.createAndSaveMedications(database);

        mMedicationsReference = database.getReference("medications");
        mMedicationsReference.keepSynced(true);

        RxFirebaseDatabase.observeValueEvent(mMedicationsReference.getRoot().child("medications"),
            new Func1<DataSnapshot, Medication>() {
                @Override
                public Medication call(DataSnapshot dataSnapshot) {
                    return Medication.newInstance(dataSnapshot);
                }
            })
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<Medication>() {
                @Override
                public void call(Medication medication) {
                    Timber.d("Medications read from %s Firebase %s", Thread.currentThread().getName(),
                        medication);
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    Timber.e(throwable, "Error reading Firebase db");
                }
            });
    }

    private void setProfileFields(UserProfile userProfile) {
        mLastProfileId = userProfile.id();
        mNameEditText.setText(userProfile.name());
        mSurnameEditText.setText(userProfile.surname());
        mEmailEditText.setText(userProfile.email());
    }


    @OnClick(R.id.button_save_profile)
    void saveProfile() {
        UserProfile newProfile = newInstance(mLastProfileId++, mNameEditText.getText().toString(), mSurnameEditText.getText().toString(),
                        mEmailEditText.getText().toString(), new Date(System.currentTimeMillis()));
        Timber.v("Saving new profile %s", newProfile);
        if (mUserProfileReference != null) {
            mUserProfileReference.setValue(newProfile.toFirebaseValue());
        }

    }

}
