package com.interswitchng.sdk.payment.android.carddemo;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;

import com.interswitchng.sdk.auth.Passport;
import com.interswitchng.sdk.model.RequestOptions;
import com.interswitchng.sdk.payment.IswCallback;
import com.interswitchng.sdk.payment.Payment;
import com.interswitchng.sdk.payment.android.AuthorizeWebView;
import com.interswitchng.sdk.payment.android.PaymentSDK;
import com.interswitchng.sdk.payment.android.util.Util;
import com.interswitchng.sdk.payment.android.util.Validation;
import com.interswitchng.sdk.payment.model.AuthorizePurchaseRequest;
import com.interswitchng.sdk.payment.model.AuthorizePurchaseResponse;
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
    private EditText cvv2;
    private Context context;

    private Button payNow;
    private String paymentId;
    private String transactionIdentifier;
    private String authData;
    private WebView webView;

    private RequestOptions options = RequestOptions.builder().setClientId("IKIAF8F70479A6902D4BFF4E443EBF15D1D6CB19E232").setClientSecret("ugsmiXPXOOvks9MR7+IFHSQSdk8ZzvwQMGvd0GJva30=").build();

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
        cvv2 = (EditText) findViewById(R.id.cardCvv2);
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
        Payment.overrideApiBase(Payment.QA_API_BASE); // used to override the payment api base url.
        Passport.overrideApiBase(Passport.QA_API_BASE); //used to override the payment api base url.
        List<EditText> fields = new ArrayList<>();
        fields.clear();

        fields.add(customerID);
        fields.add(amount);
        fields.add(pan);
        fields.add(pin);
        fields.add(expiry);
        fields.add(cvv2);
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
                request.setCvv2(cvv2.getText().toString());
                Util.hide_keyboard(this);
                Util.showProgressDialog(context, "Sending Payment");
                new PaymentSDK(context, options).purchase(request, new IswCallback<PurchaseResponse>() {
                    @Override
                    public void onError(Exception error) {
                        Util.hideProgressDialog();
                        Util.notify(context, "Error", error.getLocalizedMessage(), "Close", false);
                    }

                    @Override
                    public void onSuccess(final PurchaseResponse response) {
                        Util.hideProgressDialog();
                        transactionIdentifier = response.getTransactionIdentifier();
                        if (StringUtils.hasText(response.getResponseCode())) {
                            if (PaymentSDK.SAFE_TOKEN_RESPONSE_CODE.equals(response.getResponseCode())) {
                                paymentId = response.getPaymentId();
                                authData = request.getAuthData();
                                Util.prompt(SplashActivity.this, "OTP", response.getMessage(), "Close", "Continue", true, 1L);
                            }
                            if (PaymentSDK.CARDINAL_RESPONSE_CODE.equals(response.getResponseCode())) {
                                final Dialog cardinalDialog = new Dialog(context) {
                                    @Override
                                    public void onBackPressed() {
                                        super.onBackPressed();
                                    }
                                };
                                webView = new AuthorizeWebView(context, response) {
                                    @Override
                                    public void onPageDone() {
                                        Util.showProgressDialog(context, "Processing...");
                                        AuthorizePurchaseRequest cardinalRequest = new AuthorizePurchaseRequest();
                                        cardinalRequest.setAuthData(request.getAuthData());
                                        cardinalRequest.setPaymentId(response.getPaymentId());
                                        cardinalRequest.setTransactionId(response.getTransactionId());
                                        cardinalRequest.setEciFlag(response.getEciFlag());
                                        new PaymentSDK(context, options).authorizePurchase(cardinalRequest, new IswCallback<AuthorizePurchaseResponse>() {
                                            @Override
                                            public void onError(Exception error) {
                                                Util.hideProgressDialog();
                                                cardinalDialog.dismiss();
                                                Util.notify(context, "Error", error.getMessage(), "Close", false);
                                            }

                                            @Override
                                            public void onSuccess(AuthorizePurchaseResponse response) {
                                                Util.hideProgressDialog();
                                                cardinalDialog.dismiss();
                                                Util.notify(context, "Success", response.getMessage(), "Close", false);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onPageError(Exception error) {
                                        Util.notify(context, "Error", error.getMessage(), "Close", false);
                                    }
                                };
                                cardinalDialog.setContentView(webView);
                                cardinalDialog.show();
                                cardinalDialog.setCancelable(true);
                                webView.requestFocus(View.FOCUS_DOWN);
                                webView.getSettings().setJavaScriptEnabled(true);
                                webView.setVerticalScrollBarEnabled(true);
                                WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                                Display display = wm.getDefaultDisplay();
                                Point size = new Point();
                                display.getSize(size);
                                int width = size.x;
                                int height = size.y;
                                Window window = cardinalDialog.getWindow();
                                window.setLayout(width, height);
                            }

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
            AuthorizePurchaseRequest request = new AuthorizePurchaseRequest();
            request.setPaymentId(paymentId);
            request.setOtp(response);
            request.setAuthData(authData);
            Util.hide_keyboard(this);
            Util.showProgressDialog(context, "Verifying OTP");
            new PaymentSDK(context, options).authorizePurchase(request, new IswCallback<AuthorizePurchaseResponse>() {
                @Override
                public void onError(Exception error) {
                    Util.hideProgressDialog();
                    Util.notify(context, "Error", error.getLocalizedMessage(), "Close", false);
                }

                @Override
                public void onSuccess(AuthorizePurchaseResponse otpResponse) {
                    Util.hideProgressDialog();
                    Util.notify(context, "Success", "Ref: " + transactionIdentifier, "Close", false);
                }
            });
        }

    }
}
