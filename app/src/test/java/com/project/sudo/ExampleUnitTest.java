package com.project.sudo;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {


    private static final String NAME_KEY = "Name";
    private static final String EMAIL_KEY = "Email";



    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void addNewContact() {


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference testCol = db.collection("Testing");
        final Map<String, Object> tempData = new HashMap<>();
        tempData.put(NAME_KEY, "John");
        tempData.put(EMAIL_KEY, "john@gmail.com");
        testCol.document("TempData").set(tempData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        testCol.document("TempData").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                DocumentSnapshot doc = task.getResult();

                                assertEquals(tempData.get("Name"), doc.get("Name"));
                                assertEquals(tempData.get("Email"), doc.get("Email"));
                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }

                });
    }
}