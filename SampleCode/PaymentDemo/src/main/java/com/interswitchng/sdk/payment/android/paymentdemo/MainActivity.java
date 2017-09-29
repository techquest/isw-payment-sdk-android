package com.interswitchng.sdk.payment.android.paymentdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.interswitchng.sdk.auth.Passport;
import com.interswitchng.sdk.model.RequestOptions;
import com.interswitchng.sdk.payment.Payment;
import com.interswitchng.sdk.payment.android.inapp.Pay;
import com.interswitchng.sdk.payment.android.inapp.PayWithCard;
import com.interswitchng.sdk.payment.android.inapp.PayWithWallet;
import com.interswitchng.sdk.payment.android.paymentdemo.callback.PaymentCallback;
import com.interswitchng.sdk.payment.android.util.Util;
import com.interswitchng.sdk.util.RandomString;

import java.io.Serializable;

public class MainActivity extends AppCompatActivity implements Serializable {
    private Activity activity;
    private Context context;
    private Button walletPay;
    private Button selectPayOption;
    private String transactionRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = this;
        context = this;
        Payment.overrideApiBase(Payment.SANDBOX_API_BASE); // used to override the payment api base url.
        Passport.overrideApiBase(Passport.SANDBOX_API_BASE); //used to override the payment api base url.
        Util.hideProgressDialog();
        Button addCard = (Button) findViewById(R.id.newFlow);
        addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CardList.class);
                startActivity(intent);
            }
        });
        final String clientId = "IKIA9614B82064D632E9B6418DF358A6A4AEA84D7218";
        final String clientSecret = "XCTiBtLy1G9chAnyg0z3BcaFK4cVpwDg/GTw2EmjTZ8=";
        final Button payWithCardDialog = (Button) findViewById(R.id.payWithCardDialog);
        payWithCardDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestOptions options = RequestOptions.builder().setClientId(clientId).setClientSecret(clientSecret).build();
                PaymentCallback paymentCallback = new PaymentCallback(context);
                final PayWithCard payWithCard = new PayWithCard(activity, "1407002510", "Payment for trip completed", "1000", "NGN", options, paymentCallback);
                payWithCard.start();
            }
        });
        walletPay = (Button) findViewById(R.id.walletPay);
        walletPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transactionRef = RandomString.numeric(12);
                //System.out.println("Transaction ref :"+transactionRef);
                final RequestOptions options = RequestOptions.builder().setClientId(clientId).setClientSecret(clientSecret).build();
                PaymentCallback paymentCallback = new PaymentCallback(context);
                PayWithWallet payWithWallet = new PayWithWallet(activity, "1407002511", "Payment for Ball Gown", "200", "NGN", options, paymentCallback);
                payWithWallet.start();
            }
        });
        selectPayOption = (Button) findViewById(R.id.select_pay_option);
        selectPayOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transactionRef = RandomString.numeric(12);
                System.out.println("Transaction ref :" + transactionRef);
                /*SplitSettlement[] splitSettlements = new SplitSettlement[2];
                SplitSettlement splitSettlement1 = new SplitSettlement();
                splitSettlement1.setAccountIdentifier("fbn acct");
                splitSettlement1.setAmount("300");
                SplitSettlement splitSettlement2 = new SplitSettlement();
                splitSettlement2.setAccountIdentifier("uba acct");
                splitSettlement2.setAmount("400");
                splitSettlements[0] = splitSettlement1;
                splitSettlements[1] = splitSettlement2;
                */
                final RequestOptions options = RequestOptions.builder().setClientId(clientId).setClientSecret(clientSecret).build();
                PaymentCallback paymentCallback = new PaymentCallback(context);
                Pay pay = new Pay(activity, "1407002510", "Payment for Ball Gown", "1000", "NGN", transactionRef, options, paymentCallback);
                pay.start();
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
}










