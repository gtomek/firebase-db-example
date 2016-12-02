package uk.org.tomek.firebasedbexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
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
    @BindView(R.id.spinner_mediaction)
    Spinner mSpinnerMedications;

    private int mLastProfileId;
    private DatabaseReference mUserProfileReference;
    private DatabaseReference mMedicationsReference;
    CompositeSubscription mCompositeSubscription;
    private FirebaseDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mCompositeSubscription = new CompositeSubscription();

        // Write a message to the mDatabase
        mDatabase = FirebaseDatabase.getInstance();
        mUserProfileReference = mDatabase.getReference("userProfile");
        mUserProfileReference.keepSynced(true);

        mMedicationsReference = mDatabase.getReference("medications");
        mMedicationsReference.keepSynced(true);

        final Subscription userProfileSubscription = RxFirebaseDatabase
                .observeValueEvent(mUserProfileReference.getRoot().child("userProfile"),
                        new Func1<DataSnapshot, UserProfile>() {
                            @Override
                            public UserProfile call(DataSnapshot dataSnapshot) {
                                return UserProfile.newInstance(dataSnapshot);
                            }
                        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<UserProfile>() {
                    @Override
                    public void call(UserProfile userProfile) {
                        Timber.d("Profile read from %s Firebase %s", Thread.currentThread().getName(), userProfile);
                        setProfileFields(userProfile);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Timber.e(throwable, "Error reading Firebase db");
                    }
                });

        mCompositeSubscription.add(userProfileSubscription);

//    final DatabaseReference medicationsReference =
//        FirebaseUtils.createAndSaveMedications(mDatabase);


        final Subscription medicationsSubscription = RxFirebaseDatabase
                .observeValueEvent(mMedicationsReference.getRoot().child("medications"),
                        new Func1<DataSnapshot, List<Medication>>() {
                            @Override
                            public List<Medication> call(DataSnapshot dataSnapshot) {

                                List<Medication> medications = new ArrayList<Medication>();
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    medications.add(Medication.newInstance(snapshot));
                                }
                                return medications;
                            }
                        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Medication>>() {
                    @Override
                    public void call(List<Medication> medications) {
                        Timber.d("Medications read from Firebase %s", medications);
                        if (!medications.isEmpty()) {
                            ArrayAdapter spinnerAdapter = new ArrayAdapter<Medication>(MainActivity.this,
                                    android.R.layout.simple_spinner_item, medications);
                            mSpinnerMedications.setAdapter(spinnerAdapter);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Timber.e(throwable, "Error reading Firebase db");
                    }
                });
        mCompositeSubscription.add(medicationsSubscription);
    }

    @Override
    protected void onPause() {
        saveUserProfileData();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mCompositeSubscription.clear();
        super.onDestroy();
    }

    private void setProfileFields(UserProfile userProfile) {
        mLastProfileId = userProfile.id();
        mNameEditText.setText(userProfile.name());
        mSurnameEditText.setText(userProfile.surname());
        mEmailEditText.setText(userProfile.email());
    }

    @OnClick(R.id.button_save_profile)
    void saveProfileClicked() {
        saveUserProfileData();
    }

    private void saveUserProfileData() {
        UserProfile newProfile = newInstance(mLastProfileId++, mNameEditText.getText().toString(),
                mSurnameEditText.getText().toString(), mEmailEditText.getText().toString(),
                new Date(System.currentTimeMillis()));
        Timber.v("Saving new profile %s", newProfile);
        if (mUserProfileReference != null) {
            mUserProfileReference.setValue(newProfile.toFirebaseValue());
        }
    }
}
