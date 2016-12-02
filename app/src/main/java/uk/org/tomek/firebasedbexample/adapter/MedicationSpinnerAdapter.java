package uk.org.tomek.firebasedbexample.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import timber.log.Timber;
import uk.org.tomek.firebasedbexample.model.Medication;

/**
 * Adapter for the spinner displaying the list of available medications..
 */
public class MedicationSpinnerAdapter extends ArrayAdapter<Object> {

    private final List<Medication> mMedicationList;

    public MedicationSpinnerAdapter(Context context, int resource, List<Medication> objects) {
        super(context, resource);
        mMedicationList = objects;
        addAll(objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Timber.d("Get view called %d", position);
        return super.getView(position, convertView, parent);
    }

    @Nullable
    @Override
    public String getItem(int position) {
        final Medication medication = mMedicationList.get(position);
        Timber.v("Get item called %s", medication);
        return medication.name();
    }
}
