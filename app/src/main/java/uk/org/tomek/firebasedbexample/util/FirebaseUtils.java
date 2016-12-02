package uk.org.tomek.firebasedbexample.util;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import uk.org.tomek.firebasedbexample.model.Medication;

/**
 * Utility classes for firebase.
 */
public class FirebaseUtils {

  public static DatabaseReference createAndSaveMedications(FirebaseDatabase database) {
    final DatabaseReference medicationsReference = database.getReference("medications");
    medicationsReference.keepSynced(true);

    Map<String, Object> medicationsList = new HashMap<>();
    medicationsList.put(Integer.toString(1), Medication.newInstance(1, "Paracetamol", "Pain Killer", "pain killers").toFirebaseValue());
    medicationsList.put(Integer.toString(2), Medication.newInstance(2, "Nourofen", "Pain Killer", "pain killers").toFirebaseValue());
    medicationsList.put(Integer.toString(3), Medication.newInstance(3, "Calpol", "Pain Killer", "children").toFirebaseValue());
    medicationsList.put(Integer.toString(4), Medication.newInstance(4, "Insulin", "diabetes", "diabetes").toFirebaseValue());

    medicationsReference.setValue(medicationsList);
    return medicationsReference;
  }
}
