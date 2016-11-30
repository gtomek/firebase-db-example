package uk.org.tomek.firebasedbexample.model;

import java.util.Date;

import me.mattlogan.auto.value.firebase.adapter.TypeAdapter;

/**
 * Adapter for date field in user profile.
 */
public class DateAdapter implements TypeAdapter<Date, Long> {
    @Override
    public Date fromFirebaseValue(Long value) {
        return new Date(value);
    }

    @Override
    public Long toFirebaseValue(Date value) {
        return value.getTime();
    }
}