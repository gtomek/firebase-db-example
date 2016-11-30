package uk.org.tomek.firebasedbexample.model;

import com.google.auto.value.AutoValue;
import com.google.firebase.database.DataSnapshot;

import me.mattlogan.auto.value.firebase.annotation.FirebaseValue;


@AutoValue
@FirebaseValue
public abstract class Medication {

    abstract int id();

    abstract String name();

    abstract String category();

    abstract String detail();

    public static Medication newInstance(int id, String name, String detail, String category) {
        return new AutoValue_Medication(id, name, category, detail);
    }

    public static Medication newInstance(DataSnapshot dataSnapshot) {
        return dataSnapshot.getValue(AutoValue_Medication.FirebaseValue.class).toAutoValue();
    }


    public Object toFirebaseValue() {
        return new AutoValue_Medication.FirebaseValue(this);
    }

}
