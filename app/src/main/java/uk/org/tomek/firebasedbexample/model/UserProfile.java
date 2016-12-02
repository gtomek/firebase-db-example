package uk.org.tomek.firebasedbexample.model;

import com.google.auto.value.AutoValue;
import com.google.firebase.database.DataSnapshot;

import java.util.Date;

import me.mattlogan.auto.value.firebase.adapter.FirebaseAdapter;
import me.mattlogan.auto.value.firebase.annotation.FirebaseValue;

@AutoValue
@FirebaseValue
public abstract class UserProfile {

    public abstract int id();

    public abstract String name();

    public abstract String surname();

    public abstract String email();

    @FirebaseAdapter(DateAdapter.class) public abstract Date birthdate();

//    public abstract int medicationId();

    public static UserProfile newInstance(int id, String name, String surname, String email, Date birthDate) {
        return new AutoValue_UserProfile(id, name, surname, email, birthDate);
    }

    public static UserProfile newInstance(DataSnapshot dataSnapshot) {
        return dataSnapshot.getValue(AutoValue_UserProfile.FirebaseValue.class).toAutoValue();
    }


    public Object toFirebaseValue() {
        return new AutoValue_UserProfile.FirebaseValue(this);
    }
}
