package com.interswitchng.sdk.payment.android.walletdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.interswitchng.sdk.auth.Passport;
import com.interswitchng.sdk.model.RequestOptions;
import com.interswitchng.sdk.payment.IswCallback;
import com.interswitchng.sdk.payment.Payment;
import com.interswitchng.sdk.payment.android.WalletSDK;
import com.interswitchng.sdk.payment.android.util.Util;
import com.interswitchng.sdk.payment.android.util.Validation;
import com.interswitchng.sdk.payment.model.AuthorizeOtpRequest;
import com.interswitchng.sdk.payment.model.AuthorizeOtpResponse;
import com.interswitchng.sdk.payment.model.PaymentMethod;
import com.interswitchng.sdk.payment.model.PurchaseRequest;
import com.interswitchng.sdk.payment.model.PurchaseResponse;
import com.interswitchng.sdk.payment.model.WalletRequest;
import com.interswitchng.sdk.payment.model.WalletResponse;
import com.interswitchng.sdk.util.RandomString;
import com.interswitchng.sdk.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements Util.PromptResponseHandler {
    private EditText amount;
    private EditText pin;
    private Spinner paymethod;
    private Button payNow;
    private Button refresh;
    private EditText customerID;
    private String otpTransactionIdentifier;
    private String transactionIdentifier;
    private RequestOptions options = RequestOptions.builder().setClientId("IKIA3E267D5C80A52167A581BBA04980CA64E7B2E70E").setClientSecret("SagfgnYsmvAdmFuR24sKzMg7HWPmeh67phDNIiZxpIY=").build();

    private WalletSDK sdk;
    private Context context;
    private PaymentMethod[] paymentMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        Payment.overrideApiBase("https://qa.interswitchng.com"); // used to override the payment api base url.
        Passport.overrideApiBase("https://qa.interswitchng.com/passport"); //used to override the payment api base url.
//        Passport.overrideApiBase("http://172.25.20.91:6060/passport");
//        Payment.overrideApiBase("http://172.25.20.56:9080");
        customerID = (EditText) findViewById(R.id.customerid);
        paymethod = (Spinner) findViewById(R.id.paymethod);
        amount = (EditText) findViewById(R.id.amount);
        pin = (EditText) findViewById(R.id.cardpin);
        payNow = (Button) findViewById(R.id.payButton);
        refresh = (Button) findViewById(R.id.refreshButton);
        payNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executePay();
            }
        });
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadWallet();
            }
        });
        sdk = new WalletSDK(context, options);
        loadWallet();

    }

    private void loadWallet() {
        WalletRequest request = new WalletRequest();
        request.setTransactionRef(RandomString.numeric(12));
        if (Util.isNetworkAvailable(context)) {
            Util.hide_keyboard(this);
            Util.showProgressDialog(context, "Loading Wallet");
            sdk.getPaymentMethods(request, new IswCallback<WalletResponse>() {
                @Override
                public void onError(Exception error) {
                    Util.hideProgressDialog();
                    Util.notify(context, "Error", error.getLocalizedMessage(), "Close", false);
                }

                @Override
                public void onSuccess(WalletResponse response) {
                    Util.hideProgressDialog();
                    paymentMethods = response.getPaymentMethods();
                    ArrayAdapter<PaymentMethod> adapter = new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, paymentMethods);
                    paymethod.setAdapter(adapter);
                }
            });
        } else {
            Util.notifyNoNetwork(this);
        }
    }

    public void executePay() {
//        Payment.overrideApiBase("http://172.25.20.56:9080"); // used to override the payment api base url.
//        Passport.overrideApiBase("http://172.25.20.91:6060/passport"); //used to override the payment api base url.
        List<EditText> fields = new ArrayList<>();
        fields.clear();

        fields.add(customerID);
        fields.add(amount);
        fields.add(pin);

        if (Validation.isValidEditboxes(fields)) {
            if (Util.isNetworkAvailable(this)) {
                final PurchaseRequest request = new PurchaseRequest();
                request.setCustomerId(customerID.getText().toString());
                request.setAmount(amount.getText().toString());
                if (paymethod.getSelectedItem() == null) {
                    Util.notify(context, "Error", "No wallet item selected", "Close", false);
                    return;
                }
                request.setPan(((PaymentMethod) paymethod.getSelectedItem()).getToken());
                request.setPinData(pin.getText().toString());
                request.setRequestorId("11179920172");
                request.setCurrency("NGN");
                request.setTransactionRef(RandomString.numeric(12));
                Util.hide_keyboard(this);
                Util.showProgressDialog(context, "Sending Payment");
                new WalletSDK(context, options).purchase(request, new IswCallback<PurchaseResponse>() {

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
                            Util.prompt(MainActivity.this, "OTP", response.getMessage(), "Close", "Continue", true, 1L);
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
            new WalletSDK(context, options).authorizeOtp(request, new IswCallback<AuthorizeOtpResponse>() {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
