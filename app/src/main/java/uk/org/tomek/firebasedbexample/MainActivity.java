package uk.org.tomek.firebasedbexample;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kelvinapps.rxfirebase.RxFirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;
import uk.org.tomek.firebasedbexample.adapter.MedicationSpinnerAdapter;
import uk.org.tomek.firebasedbexample.fragment.DatePickerFragment;
import uk.org.tomek.firebasedbexample.model.Medication;
import uk.org.tomek.firebasedbexample.model.UserProfile;

import static uk.org.tomek.firebasedbexample.model.UserProfile.newInstance;

public class MainActivity extends AppCompatActivity implements DatePickerFragment.DateSelectionCallback {

    @BindView(R.id.edittext_name)
    EditText mNameEditText;
    @BindView(R.id.edittext_surname)
    EditText mSurnameEditText;
    @BindView(R.id.edittext_email)
    EditText mEmailEditText;
    @BindView(R.id.spinner_mediaction)
    Spinner mSpinnerMedications;
    @BindView(R.id.textview_date)
    TextView mDateTextView;

    private int mLastProfileId;
    private DatabaseReference mUserProfileReference;
    private DatabaseReference mMedicationsReference;
    private FirebaseDatabase mDatabase;
    private int mSelectedMedicationId;
    private Subscription mFirebaseSubscription;
    private long mSelectedTimeMs = System.currentTimeMillis();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Write a message to the mDatabase
        mDatabase = FirebaseDatabase.getInstance();
        mUserProfileReference = mDatabase.getReference("userProfile");
        mUserProfileReference.keepSynced(true);

        mMedicationsReference = mDatabase.getReference("medications");
        mMedicationsReference.keepSynced(true);

        // read medications
        mFirebaseSubscription = RxFirebaseDatabase
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
                        }).flatMap(new Func1<List<Medication>, Observable<UserProfile>>() {
                    @Override
                    public Observable<UserProfile> call(List<Medication> medications) {
                        Timber.d("Medications read from Firebase %s", medications);
                        if (!medications.isEmpty()) {
                            MedicationSpinnerAdapter spinnerAdapter = new MedicationSpinnerAdapter(MainActivity.this,
                                    android.R.layout.simple_spinner_item, medications);
                            mSpinnerMedications.setAdapter(spinnerAdapter);
                        }
                        return RxFirebaseDatabase
                                .observeValueEvent(mUserProfileReference.getRoot().child("userProfile"),
                                        new Func1<DataSnapshot, UserProfile>() {
                                            @Override
                                            public UserProfile call(DataSnapshot dataSnapshot) {
                                                return UserProfile.newInstance(dataSnapshot);
                                            }
                                        });
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



        mSpinnerMedications.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedMedicationId = i + 1;
                Timber.v("Selected medication %d", mSelectedMedicationId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onPause() {
        saveUserProfileData();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mFirebaseSubscription.unsubscribe();
        super.onDestroy();
    }

    private void setProfileFields(UserProfile userProfile) {
        mLastProfileId = userProfile.id();
        mNameEditText.setText(userProfile.name());
        mSurnameEditText.setText(userProfile.surname());
        mEmailEditText.setText(userProfile.email());
        mSpinnerMedications.setSelection(userProfile.medicationId() - 1);
        mDateTextView.setText(getResources().getString(R.string.date_f,
                DateFormat.format("dd/MM/yyyy", userProfile.birthdate())));
    }

    @OnClick(R.id.button_save_profile)
    void saveProfileClicked() {
        saveUserProfileData();
    }

    @OnClick(R.id.textview_date)
    void onDateClicked(){
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void saveUserProfileData() {
        UserProfile newProfile = newInstance(mLastProfileId++, mNameEditText.getText().toString(),
                mSurnameEditText.getText().toString(), mEmailEditText.getText().toString(),
                new Date(mSelectedTimeMs), mSelectedMedicationId);
        Timber.v("Saving new profile %s", newProfile);
        if (mUserProfileReference != null) {
            mUserProfileReference.setValue(newProfile.toFirebaseValue());
        }
    }

    @Override
    public void setSelectedDate(int year, int month, int day) {
        Timber.v("Selected y:%d m:%d d:%d", year, month, day);
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        mSelectedTimeMs = cal.getTimeInMillis();
    }
}
