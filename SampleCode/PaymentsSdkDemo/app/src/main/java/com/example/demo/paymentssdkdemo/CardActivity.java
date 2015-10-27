package com.example.demo.paymentssdkdemo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.interswitchng.sdk.auth.Passport;
import com.interswitchng.sdk.model.RequestOptions;
import com.interswitchng.sdk.payment.IswCallback;
import com.interswitchng.sdk.payment.Payment;
import com.interswitchng.sdk.payment.android.PaymentSDK;
import com.interswitchng.sdk.payment.model.AuthorizeOtpRequest;
import com.interswitchng.sdk.payment.model.AuthorizeOtpResponse;
import com.interswitchng.sdk.payment.model.PurchaseRequest;
import com.interswitchng.sdk.payment.model.PurchaseResponse;
import com.interswitchng.sdk.util.RandomString;
import com.interswitchng.sdk.util.StringUtils;



public class CardActivity extends AppCompatActivity {
    private EditText customerId;
    private EditText amount;
    private EditText cardno;
    private EditText pin;
    private EditText expiry;
    private Button payBtn;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);
        customerId = (EditText) findViewById(R.id.identifier);
        amount = (EditText) findViewById(R.id.amount);
        cardno = (EditText) findViewById(R.id.pan);
        pin = (EditText) findViewById(R.id.password);
        expiry = (EditText) findViewById(R.id.expiry);
        payBtn = (Button) findViewById(R.id.cardPay);
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardPay();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_card, menu);
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

    public void cardPay(){
        Passport.overrideApiBase("https://qa.interswitchng.com/passport");
        Payment.overrideApiBase("https://qa.interswitchng.com");
        progressDialog = ProgressDialog.show(this, "Transaction Processing",
                "Processing", true);
        //For testing purposes only, you can override the API base


        final RequestOptions options = RequestOptions.builder().setClientId("IKIA3E267D5C80A52167A581BBA04980CA64E7B2E70E").setClientSecret("SagfgnYsmvAdmFuR24sKzMg7HWPmeh67phDNIiZxpIY=").build();

        final PurchaseRequest request = new PurchaseRequest();
        request.setCustomerId(customerId.getText().toString()); //Optional email, mobile no, BVN etc to uniquely identify the customer
        request.setAmount(amount.getText().toString()); //Amount in Naira
        request.setPan(cardno.getText().toString()); //Card No
        request.setPinData(pin.getText().toString()); //Card PIN
        request.setExpiryDate(expiry.getText().toString()); // expiry date in YYMM format
        request.setCurrency("NGN");
        request.setTransactionRef(RandomString.numeric(12)); //unique transaction reference

        final Context context = this; // reference to your Android Activity

//Send payment

//

        new PaymentSDK(context, options).purchase(request, new IswCallback<PurchaseResponse>() {

            @Override
            public void onError(Exception error) {
                progressDialog.dismiss();
                // Handle and notify user of error
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(CardActivity.this);
                alertDialog.setTitle("Transaction Result");
                alertDialog.setMessage(error.getMessage());
                alertDialog.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

            }

            //Ask user for OTP and authorize transaction using the otpTransactionIdentifier
            String otp;

            @Override
            public void onSuccess(PurchaseResponse response) {
                progressDialog.dismiss();
                if (StringUtils.hasText(response.getOtpTransactionIdentifier())) { //OTP required
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(CardActivity.this);
                    alertDialog.setTitle("PASSWORD");
                    alertDialog.setMessage("Enter One-time Password");
                    final EditText input = new EditText(CardActivity.this);
                    alertDialog.setPositiveButton("Submit",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    otp = input.getText().toString();
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    alertDialog.show();

                    //Authorize OTP
                    final AuthorizeOtpRequest request = new AuthorizeOtpRequest();
                    request.setOtpTransactionIdentifier(customerId.getText().toString()); //otpTransactionIdentifier from the first leg
                    request.setOtp(otp); //OTP from user

                    new PaymentSDK(context, options).authorizeOtp(request, new IswCallback<AuthorizeOtpResponse>() {
                        @Override
                        public void onError(Exception error) {
                            // Handle and notify user of error
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(CardActivity.this);
                            alertDialog.setTitle("Transaction Result");
                            alertDialog.setMessage(error.getMessage());
                            alertDialog.setPositiveButton("Okay",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }

                        @Override
                        public void onSuccess(AuthorizeOtpResponse otpResponse) {
                            //Handle and notify user of successful transaction
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(CardActivity.this);
                            alertDialog.setTitle("Transaction Result");
                            alertDialog.setMessage("Transaction succesful");
                            final EditText input = new EditText(CardActivity.this);
                            alertDialog.setPositiveButton("Okay",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            otp = input.getText().toString();
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                    });


                } else { //OTP not required
                    //Handle and notify user of successful transaction
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(CardActivity.this);
                    alertDialog.setTitle("Transaction Result");
                    alertDialog.setMessage("Transaction Succeeded");
                    alertDialog.setPositiveButton("Okay",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            }
        });
    }
}