package com.project.sudo;

import android.app.Activity;
import android.app.Instrumentation;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.test.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PaymentAmountTest {

    private static final String NAME_KEY = "Name";
    private static final String EMAIL_KEY = "Email";
    @Rule
    public ActivityTestRule<PaymentAmount> paymentAmountActivityTestRule = new ActivityTestRule<PaymentAmount>(PaymentAmount.class);
    Instrumentation.ActivityMonitor monitor = getInstrumentation().addMonitor(GooglePay.class.getName(), null, false);
    private Activity activity = null;

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Before
    public void setUp() throws Exception {

        activity = paymentAmountActivityTestRule.getActivity();
        FirebaseApp.initializeApp(InstrumentationRegistry.getContext());

    }

    @Test
    public void testTextview() {

        assertNotNull(activity.findViewById(R.id.payableamount));

        getInstrumentation().waitForMonitorWithTimeout(monitor, 10000);

        TextView textView = activity.findViewById(R.id.payableamount);
        assertEquals(isNumeric(textView.getText().toString()), true);
    }

    @Test
    public void testDB() {

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

    @After
    public void tearDown() throws Exception {
        activity = null;
    }
}