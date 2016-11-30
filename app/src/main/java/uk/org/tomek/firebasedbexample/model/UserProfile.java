package uk.org.tomek.firebasedbexample.model;

import com.google.auto.value.AutoValue;
import com.google.firebase.database.DataSnapshot;

import java.util.Date;

import me.mattlogan.auto.value.firebase.adapter.FirebaseAdapter;
import me.mattlogan.auto.value.firebase.annotation.FirebaseValue;

@AutoValue
@FirebaseValue
public abstract class UserProfile {

    abstract int id();

    abstract String name();

    abstract String surname();

    abstract String email();

    @FirebaseAdapter(DateAdapter.class) public abstract Date birthdate();

    public static UserProfile newInstance(int id, String name, String surname, String email, Date birthDate) {
        return new AutoValue_UserProfile(id, name, email, surname, birthDate);
    }

    public static UserProfile newInstance(DataSnapshot dataSnapshot) {
        return dataSnapshot.getValue(AutoValue_UserProfile.FirebaseValue.class).toAutoValue();
    }


    public Object toFirebaseValue() {
        return new AutoValue_UserProfile.FirebaseValue(this);
    }
}
