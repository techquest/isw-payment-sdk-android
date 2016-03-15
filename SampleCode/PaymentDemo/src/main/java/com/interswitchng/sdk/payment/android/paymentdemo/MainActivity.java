package com.interswitchng.sdk.payment.android.paymentdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.interswitchng.sdk.auth.Passport;
import com.interswitchng.sdk.model.RequestOptions;
import com.interswitchng.sdk.payment.IswCallback;
import com.interswitchng.sdk.payment.Payment;
import com.interswitchng.sdk.payment.android.inapp.Pay;
import com.interswitchng.sdk.payment.android.inapp.PayWithCard;
import com.interswitchng.sdk.payment.android.inapp.PayWithWallet;
import com.interswitchng.sdk.payment.android.util.Util;
import com.interswitchng.sdk.payment.model.PurchaseResponse;

public class MainActivity extends AppCompatActivity {
    private Activity activity;
    private Context context;
    private Button walletPay;
    private Button selectPayOption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        context = this;
        Payment.overrideApiBase("https://qa.interswitchng.com"); // used to override the payment api base url.
        Passport.overrideApiBase("https://qa.interswitchng.com/passport"); //used to override the payment api base url.
        Util.hideProgressDialog();
        Button addCard = (Button) findViewById(R.id.newFlow);
        addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CardList.class);
                startActivity(intent);
            }
        });

        final Button payWithCardDialog = (Button) findViewById(R.id.payWithCardDialog);
        payWithCardDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Payment.overrideApiBase("https://qa.interswitchng.com"); // used to override the payment api base url.
                Passport.overrideApiBase("https://qa.interswitchng.com/passport"); //used to override the payment api base url.
                RequestOptions options = RequestOptions.builder().setClientId("IKIAD6DC1B942D95035FBCC5A4449C893D36536B5D54").setClientSecret("X1u1M6UNyASzslufiyxZnLb3u78TYODVnbRi7OxLNew=").build();
                final PayWithCard payWithCard = new PayWithCard(activity, "1407002510", "Payment for trip completed", "10", "NGN", options, new IswCallback<PurchaseResponse>() {

                    @Override
                    public void onError(Exception error) {
                        Util.notify(context, "Error", error.getMessage(), "Close", false);

                    }

                    @Override
                    public void onSuccess(final PurchaseResponse response) {
                        String transactionIdentifier;
                        transactionIdentifier = response.getTransactionIdentifier();
                        Util.notify(context, "Success", "Ref: " + transactionIdentifier, "Close", false);

                    }
                });
                payWithCard.start();
            }
        });

        walletPay = (Button) findViewById(R.id.walletPay);
        walletPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Payment.overrideApiBase("https://qa.interswitchng.com"); // used to override the payment api base url.
                Passport.overrideApiBase("https://qa.interswitchng.com/passport"); //used to override the payment api base url.
                RequestOptions options = RequestOptions.builder().setClientId("IKIAD6DC1B942D95035FBCC5A4449C893D36536B5D54").setClientSecret("X1u1M6UNyASzslufiyxZnLb3u78TYODVnbRi7OxLNew=").build();

                PayWithWallet payWithWallet = new PayWithWallet(activity, "1407002510", "Payment for Ball Gown", "1000", "NGN", options, new IswCallback<PurchaseResponse>() {
                    @Override
                    public void onError(Exception error) {
                        Util.hideProgressDialog();
                        Util.notify(context, "Error", error.getMessage(), "close", false);
                    }

                    @Override
                    public void onSuccess(PurchaseResponse response) {
                        Util.hideProgressDialog();
                        Util.notify(context, "Success", response.getMessage(), "close", false);
                    }
                });
                payWithWallet.start();
            }
        });

        selectPayOption = (Button) findViewById(R.id.select_pay_option);
        selectPayOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Payment.overrideApiBase("https://qa.interswitchng.com"); // used to override the payment api base url.
                Passport.overrideApiBase("https://qa.interswitchng.com/passport"); //used to override the payment api base url.
                RequestOptions options = RequestOptions.builder().setClientId("IKIAD6DC1B942D95035FBCC5A4449C893D36536B5D54").setClientSecret("X1u1M6UNyASzslufiyxZnLb3u78TYODVnbRi7OxLNew=").build();
                Pay pay = new Pay(activity, "1407002510", "Payment for Ball Gown", "1000", "NGN", options, new IswCallback<PurchaseResponse>() {
                    @Override
                    public void onError(Exception error) {
                        Util.hideProgressDialog();
                        Util.notify(context, "Error", error.getMessage(), "close", false);
                    }

                    @Override
                    public void onSuccess(PurchaseResponse response) {
                        Util.hideProgressDialog();
                        Util.notify(context, "Success", response.getMessage(), "close", false);
                    }
                });
                pay.start();
            }
        });

    }

}










