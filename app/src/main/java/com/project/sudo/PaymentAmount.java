package com.project.sudo;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.PaymentDataRequest;
import com.google.android.gms.wallet.PaymentsClient;
import com.google.android.gms.wallet.Wallet;
import com.google.android.gms.wallet.WalletConstants;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Optional;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentAmount extends AppCompatActivity {

    /**
     * A client for interacting with the Google Pay API
     *
     * @see <a
     *     href="https://developers.google.com/android/reference/com/google/android/gms/wallet/PaymentsClient">PaymentsClient</a>
     */
    private PaymentsClient mPaymentsClient;

    /**
     * A Google Pay payment button presented to the viewer for interaction
     *
     * @see <a href="https://developers.google.com/pay/api/android/guides/brand-guidelines">Google Pay
     *     payment button brand guidelines</a>
     */
    private View mGooglePayButton;

    /** A constant integer you define to track a request for payment data activity */
    private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 42;

    /**
     * Initialize the Google Pay API on creation of the activity
     *
     * @see Activity#onCreate(android.os.Bundle)
     */

    private Button dbPay;
    private TextView amount;
    private ArrayList<Transaction> TransHis = new ArrayList<>();
    private ReturnTest retval ;

    //Firebase Instances
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userscol = db.collection("users");
    FirebaseAuth mauth = FirebaseAuth.getInstance();
    FirebaseUser mcurrentUser = mauth.getCurrentUser();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_amount);

        dbPay = findViewById(R.id.dbpay);
        amount = findViewById(R.id.payableamount);

        dbPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userscol.document(mcurrentUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        UserDetails userDetails = documentSnapshot.toObject(UserDetails.class);

                        String orgName = getIntent().getStringExtra("orgName");
                        String sender = mauth.getCurrentUser().getUid();
                        String amnt = amount.getText().toString();
                        String transID;
                        if(userDetails.getTransList()!=null){
                         transID = String.valueOf(userDetails.getTransList().size());}
                        else{
                            transID = "1";
                        }
                        retval=TransactionHistory.sending(transID,sender,orgName,amnt,TransHis);
                        if(retval.Trans.size()!=0){
                            TransHis = retval.Trans;
                        }
                        Toast.makeText(getApplicationContext(),retval.Check,Toast.LENGTH_SHORT).show();

                        DocumentReference userRef = db.collection("users").document(mcurrentUser.getUid());
                        // Atomically add a new region to the "regions" array field.

                        if(TransHis.size()>2){
                            userRef.update("transList", FieldValue.arrayUnion(TransHis.get(TransHis.size()-1).Hash));

                        }
                        else {
                            for (int i = 0; i < TransHis.size(); i++) {
                                userRef.update("transList", FieldValue.arrayUnion(TransHis.get(i).Hash));
                            }
                        }
                    }
                });



            }
        });

        Toast.makeText(this,"Be a philanthropist !!",Toast.LENGTH_SHORT).show();

        // initialize a Google Pay API client for an environment suitable for testing
        mPaymentsClient =
                Wallet.getPaymentsClient(PaymentAmount.this,
                        new Wallet.WalletOptions.Builder()
                                .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                                .build());

        possiblyShowGooglePayButton();
        }

    /**
     * Determine the viewer's ability to pay with a payment method supported by your app and display a
     * Google Pay payment button
     *
     * @see <a
     *     href="https://developers.google.com/android/reference/com/google/android/gms/wallet/PaymentsClient#isReadyToPay(com.google.android.gms.wallet.IsReadyToPayRequest)">PaymentsClient#IsReadyToPay</a>
     */
    private void possiblyShowGooglePayButton() {
        final Optional<JSONObject> isReadyToPayJson = GooglePay.getIsReadyToPayRequest();
        if (!isReadyToPayJson.isPresent()) {
            return;
        }
        IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.get().toString());
        if (request == null) {
            return;
        }
        Task<Boolean> task = mPaymentsClient.isReadyToPay(request);
        task.addOnCompleteListener(
                new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        try {
                            boolean result = task.getResult(ApiException.class);
                            if (result) {
                                // show Google as a payment option
                                mGooglePayButton = findViewById(R.id.googlepay);
                                mGooglePayButton.setOnClickListener(
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                requestPayment(view);
                                            }
                                        });
                                mGooglePayButton.setVisibility(View.VISIBLE);

                            }
                        } catch (ApiException exception) {
                            // handle developer errors
                        }
                    }
                });
    }

    /**
     * Display the Google Pay payment sheet after interaction with the Google Pay payment button
     *
     * @param view optionally uniquely identify the interactive element prompting for payment
     */
    public void requestPayment(View view) {
        Optional<JSONObject> paymentDataRequestJson = GooglePay.getPaymentDataRequest();
        if (!paymentDataRequestJson.isPresent()) {
            return;
        }
        PaymentDataRequest request =
                PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString());
        if (request != null) {
            AutoResolveHelper.resolveTask(
                    mPaymentsClient.loadPaymentData(request), this, LOAD_PAYMENT_DATA_REQUEST_CODE);
        }
    }

    /**
     * Handle a resolved activity from the Google Pay payment sheet
     *
     * @param requestCode the request code originally supplied to AutoResolveHelper in
     *     requestPayment()
     * @param resultCode the result code returned by the Google Pay API
     * @param data an Intent from the Google Pay API containing payment or error data
     * @see <a href="https://developer.android.com/training/basics/intents/result">Getting a result
     *     from an Activity</a>
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // value passed in AutoResolveHelper
            case LOAD_PAYMENT_DATA_REQUEST_CODE:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        PaymentData paymentData = PaymentData.getFromIntent(data);
                        String json = paymentData.toJson();
                        // if using gateway tokenization, pass this token without modification
                        String paymentMethodData = null;
                        JSONObject paymentMethodData_json = null;
                        try {
                            paymentMethodData_json = new JSONObject(json)
                                    .getJSONObject("paymentMethodData");
                            paymentMethodData = paymentMethodData_json.toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            String paymentToken = paymentMethodData_json
                                    .getJSONObject("tokenizationData")
                                    .getString("token");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                    case AutoResolveHelper.RESULT_ERROR:
                        Status status = AutoResolveHelper.getStatusFromIntent(data);
                        // Log the status for debugging.
                        // Generally, there is no need to show an error to the user.
                        // The Google Pay payment sheet will present any account errors.
                        break;
                    default:
                        // Do nothing.
                }
                break;
            default:
                // Do nothing.
        }

    }

    public static class PayableAmountActivity extends AppCompatActivity {
        /**
         * A client for interacting with the Google Pay API
         *
         * @see <a
         *     href="https://developers.google.com/android/reference/com/google/android/gms/wallet/PaymentsClient">PaymentsClient</a>
         */
        private PaymentsClient mPaymentsClient;

        /**
         * A Google Pay payment button presented to the viewer for interaction
         *
         * @see <a href="https://developers.google.com/pay/api/android/guides/brand-guidelines">Google Pay
         *     payment button brand guidelines</a>
         */
        private View mGooglePayButton;

        /** A constant integer you define to track a request for payment data activity */
        private static final int LOAD_PAYMENT_DATA_REQUEST_CODE = 42;

        /**
         * Initialize the Google Pay API on creation of the activity
         *
         * @see Activity#onCreate(Bundle)
         */
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            // initialize a Google Pay API client for an environment suitable for testing
            mPaymentsClient =
                    Wallet.getPaymentsClient(
                            this,
                            new Wallet.WalletOptions.Builder()
                                    .setEnvironment(WalletConstants.ENVIRONMENT_TEST)
                                    .build());

            possiblyShowGooglePayButton();
        }

        /**
         * Determine the viewer's ability to pay with a payment method supported by your app and display a
         * Google Pay payment button
         *
         * @see <a
         *     href="https://developers.google.com/android/reference/com/google/android/gms/wallet/PaymentsClient#isReadyToPay(com.google.android.gms.wallet.IsReadyToPayRequest)">PaymentsClient#IsReadyToPay</a>
         */
        private void possiblyShowGooglePayButton() {
            final Optional<JSONObject> isReadyToPayJson = GooglePay.getIsReadyToPayRequest();
            if (!isReadyToPayJson.isPresent()) {
                return;
            }
            IsReadyToPayRequest request = IsReadyToPayRequest.fromJson(isReadyToPayJson.get().toString());
            if (request == null) {
                return;
            }
            Task<Boolean> task = mPaymentsClient.isReadyToPay(request);
            task.addOnCompleteListener(
                    new OnCompleteListener<Boolean>() {
                        @Override
                        public void onComplete(@NonNull Task<Boolean> task) {
                            try {
                                boolean result = task.getResult(ApiException.class);
                                if (result) {
                                    // show Google as a payment option
                                    mGooglePayButton = findViewById(R.id.googlepay);
                                    mGooglePayButton.setOnClickListener(
                                            new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    requestPayment(view);
                                                }
                                            });
                                    mGooglePayButton.setVisibility(View.VISIBLE);
                                }
                            } catch (ApiException exception) {
                                // handle developer errors
                            }
                        }
                    });
        }

        /**
         * Display the Google Pay payment sheet after interaction with the Google Pay payment button
         *
         * @param view optionally uniquely identify the interactive element prompting for payment
         */
        public void requestPayment(View view) {
            Optional<JSONObject> paymentDataRequestJson = GooglePay.getPaymentDataRequest();
            if (!paymentDataRequestJson.isPresent()) {
                return;
            }
            PaymentDataRequest request =
                    PaymentDataRequest.fromJson(paymentDataRequestJson.get().toString());
            if (request != null) {
                AutoResolveHelper.resolveTask(
                        mPaymentsClient.loadPaymentData(request), this, LOAD_PAYMENT_DATA_REQUEST_CODE);
            }
        }

        /**
         * Handle a resolved activity from the Google Pay payment sheet
         *
         * @param requestCode the request code originally supplied to AutoResolveHelper in
         *     requestPayment()
         * @param resultCode the result code returned by the Google Pay API
         * @param data an Intent from the Google Pay API containing payment or error data
         * @see <a href="https://developer.android.com/training/basics/intents/result">Getting a result
         *     from an Activity</a>
         */
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            switch (requestCode) {
                // value passed in AutoResolveHelper
                case LOAD_PAYMENT_DATA_REQUEST_CODE:
                    switch (resultCode) {
                        case RESULT_OK:
                            PaymentData paymentData = PaymentData.getFromIntent(data);
                            String json = paymentData.toJson();
                            // if using gateway tokenization, pass this token without modification
                            String paymentMethodData = null;
                            JSONObject paymentMethodData_json = null;
                            try {
                                paymentMethodData_json = new JSONObject(json)
                                        .getJSONObject("paymentMethodData");
                                paymentMethodData = paymentMethodData_json.toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            try {
                                String paymentToken = paymentMethodData_json
                                        .getJSONObject("tokenizationData")
                                        .getString("token");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                        case RESULT_CANCELED:
                            break;
                        case AutoResolveHelper.RESULT_ERROR:
                            Status status = AutoResolveHelper.getStatusFromIntent(data);
                            // Log the status for debugging.
                            // Generally, there is no need to show an error to the user.
                            // The Google Pay payment sheet will present any account errors.
                            break;
                        default:
                            // Do nothing.
                    }
                    break;
                default:
                    // Do nothing.
            }
        }
    }
}
