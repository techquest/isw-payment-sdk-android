package com.interswitchng.sdk.payment.android.carddemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.interswitchng.sdk.auth.Passport;
import com.interswitchng.sdk.model.RequestOptions;
import com.interswitchng.sdk.payment.IswCallback;
import com.interswitchng.sdk.payment.Payment;
import com.interswitchng.sdk.payment.android.PaymentSDK;
import com.interswitchng.sdk.payment.android.util.Util;
import com.interswitchng.sdk.payment.android.util.Validation;
import com.interswitchng.sdk.payment.model.AuthorizeOtpRequest;
import com.interswitchng.sdk.payment.model.AuthorizeOtpResponse;
import com.interswitchng.sdk.payment.model.PurchaseRequest;
import com.interswitchng.sdk.payment.model.PurchaseResponse;
import com.interswitchng.sdk.util.RandomString;
import com.interswitchng.sdk.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity implements Util.PromptResponseHandler {

    private EditText customerID;
    private EditText amount;
    private EditText pan;
    private EditText pin;
    private EditText expiry;

    private Context context;

    private Button payNow;
    private String otpTransactionIdentifier;
    private String transactionIdentifier;
    private RequestOptions options = RequestOptions.builder().setClientId("IKIA14BAEA0842CE16CA7F9FED619D3ED62A54239276").setClientSecret("Z3HnVfCEadBLZ8SYuFvIQG52E472V3BQLh4XDKmgM2A=").build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        context = this;

        payNow = (Button) findViewById(R.id.payButton);

        customerID = (EditText) findViewById(R.id.customerid);
        amount = (EditText) findViewById(R.id.amount);
        pan = (EditText) findViewById(R.id.cardpan);
        pin = (EditText) findViewById(R.id.cardpin);
        expiry = (EditText) findViewById(R.id.expirydate);

        payNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executePay();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    public void executePay() {
        Payment.overrideApiBase("https://qa.interswitchng.com"); // used to override the payment api base url.
        Passport.overrideApiBase("https://qa.interswitchng.com/passport"); //used to override the payment api base url.
        List<EditText> fields = new ArrayList<>();
        fields.clear();

        fields.add(customerID);
        fields.add(amount);
        fields.add(pan);
        fields.add(pin);
        fields.add(expiry);

        if (Validation.isValidEditboxes(fields)) {
            if (Util.isNetworkAvailable(this)) {
                final PurchaseRequest request = new PurchaseRequest();
                request.setCustomerId(customerID.getText().toString());
                request.setAmount(amount.getText().toString());
                request.setPan(pan.getText().toString());
                request.setPinData(pin.getText().toString());
                request.setExpiryDate(expiry.getText().toString());
                request.setRequestorId("11179920172");
                request.setCurrency("NGN");
                request.setTransactionRef(RandomString.numeric(12));
                Util.hide_keyboard(this);
                Util.showProgressDialog(context, "Sending Payment");
                new PaymentSDK(context, options).purchase(request, new IswCallback<PurchaseResponse>() {

                    @Override
                    public void onError(Exception error) {
                        Util.hideProgressDialog();
                        Util.notify(context, "Error", error.getLocalizedMessage(), "Close", false);
                    }

                    @Override
                    public void onSuccess(PurchaseResponse response) {
                        Util.hideProgressDialog();
                        transactionIdentifier = response.getTransactionIdentifier();
                        if (StringUtils.hasText(response.getOtpTransactionIdentifier())) {
                            otpTransactionIdentifier = response.getOtpTransactionIdentifier();
                            Util.prompt(SplashActivity.this, "OTP", response.getMessage(), "Close", "Continue", true, 1L);
                        } else {
                            Util.notify(context, "Success", "Ref: " + transactionIdentifier, "Close", false);
                        }
                    }
                });
            } else {
                Util.notifyNoNetwork(this);
            }
        }
    }

    @Override
    public void promptResponse(String response, long requestId) {
        if (requestId == 1 && StringUtils.hasText(response)) {
            AuthorizeOtpRequest request = new AuthorizeOtpRequest();
            request.setOtpTransactionIdentifier(otpTransactionIdentifier);
            request.setOtp(response);
            Util.hide_keyboard(this);
            Util.showProgressDialog(context, "Verifying OTP");
            new PaymentSDK(context, options).authorizeOtp(request, new IswCallback<AuthorizeOtpResponse>() {
                @Override
                public void onError(Exception error) {
                    Util.hideProgressDialog();
                    Util.notify(context, "Error", error.getLocalizedMessage(), "Close", false);
                }

                @Override
                public void onSuccess(AuthorizeOtpResponse otpResponse) {
                    Util.hideProgressDialog();
                    Util.notify(context, "Success", "Ref: " + transactionIdentifier, "Close", false);
                }
            });
        }

    }


}
