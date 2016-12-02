package uk.org.tomek.firebasedbexample.util;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.List;
import uk.org.tomek.firebasedbexample.model.Medication;

/**
 * Utility classes for firebase.
 */
public class FirebaseUtils {

  public static DatabaseReference createAndSaveMedications(FirebaseDatabase database) {
    final DatabaseReference medicationsReference = database.getReference("medications");
    medicationsReference.keepSynced(true);

    List<Medication> medicationsList = new ArrayList<>();
    medicationsList.add(Medication.newInstance(1, "Paracetamol", "Pain Killer", "pain killers"));
    medicationsList.add(Medication.newInstance(2, "Nourofen", "Pain Killer", "pain killers"));
    medicationsList.add(Medication.newInstance(3, "Calpol", "Pain Killer", "children"));
    medicationsList.add(Medication.newInstance(4, "Insulin", "diabetes", "diabetes"));

    for (Medication medication : medicationsList) {
      medicationsReference.setValue(medication.toFirebaseValue());
    }
    return medicationsReference;
  }
}
