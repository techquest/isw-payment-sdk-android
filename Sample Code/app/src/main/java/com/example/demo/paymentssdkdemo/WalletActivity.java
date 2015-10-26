package com.example.demo.paymentssdkdemo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.interswitchng.sdk.auth.Passport;
import com.interswitchng.sdk.model.RequestOptions;
import com.interswitchng.sdk.payment.IswCallback;
import com.interswitchng.sdk.payment.Payment;
import com.interswitchng.sdk.payment.android.PaymentSDK;
import com.interswitchng.sdk.payment.android.WalletSDK;
import com.interswitchng.sdk.payment.model.AuthorizeOtpRequest;
import com.interswitchng.sdk.payment.model.AuthorizeOtpResponse;
import com.interswitchng.sdk.payment.model.PaymentMethod;
import com.interswitchng.sdk.payment.model.PurchaseRequest;
import com.interswitchng.sdk.payment.model.PurchaseResponse;
import com.interswitchng.sdk.payment.model.WalletRequest;
import com.interswitchng.sdk.payment.model.WalletResponse;
import com.interswitchng.sdk.util.RandomString;
import com.interswitchng.sdk.util.StringUtils;



public class WalletActivity extends ActionBarActivity {
    private ArrayAdapter<String> adapter;
    private Spinner paymentSpinner;
    private EditText pin, customerId, amount;
    private Button walletBtn, payBtn;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        paymentSpinner = (Spinner) findViewById(R.id.paymentMethodSpinner);
        walletBtn = (Button) findViewById(R.id.reloadButton);
        payBtn = (Button) findViewById(R.id.payButton);
        customerId = (EditText) findViewById(R.id.identifier);
        amount = (EditText) findViewById(R.id.amount);
        walletBtn.setBackgroundColor(Color.BLUE);
        walletBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadWallet();
            }
        });
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                payWithThisCard();
            }
        });
        loadWallet();



    }

    public void loadWallet(){
        Passport.overrideApiBase("https://qa.interswitchng.com/passport");
        Payment.overrideApiBase("https://qa.interswitchng.com");
        progressDialog = ProgressDialog.show(this, "Laoding Wallet",
                "Processing", true);
        final RequestOptions options = RequestOptions.builder().setClientId("IKIA3E267D5C80A52167A581BBA04980CA64E7B2E70E").setClientSecret("SagfgnYsmvAdmFuR24sKzMg7HWPmeh67phDNIiZxpIY=").build();
        //Load Wallet
        final WalletRequest request = new WalletRequest();
        request.setTransactionRef(RandomString.numeric(12));
        final Context context = this; // reference to your Android Activity
        new WalletSDK(context, options).getPaymentMethods(request, new IswCallback<WalletResponse>() {
            @Override
            public void onError(Exception error) {
                // Handle and notify user of error
                Toast.makeText(getApplication(),error.getMessage(),Toast.LENGTH_LONG);
                progressDialog.dismiss();
            }

            @Override
            public void onSuccess(WalletResponse response) {
                PaymentMethod[] paymentMethods = response.getPaymentMethods(); //Display payment methods in a Spinner
                ArrayAdapter<PaymentMethod> adapter = new ArrayAdapter(context, R.layout.support_simple_spinner_dropdown_item, paymentMethods);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                paymentSpinner.setAdapter(adapter);
                progressDialog.dismiss();

            }
        });
    }

    public void payWithThisCard(){
        Passport.overrideApiBase("https://qa.interswitchng.com/passport");
        Payment.overrideApiBase("https://qa.interswitchng.com");
        progressDialog = ProgressDialog.show(this, "Transaction Processing",
                "Processing", true);
        final RequestOptions options = RequestOptions.builder().setClientId("IKIA3E267D5C80A52167A581BBA04980CA64E7B2E70E").setClientSecret("SagfgnYsmvAdmFuR24sKzMg7HWPmeh67phDNIiZxpIY=").build();
        final Context context = this;
        pin = (EditText) findViewById(R.id.pinOfCard);

        //Pay with Wallet Item
        final PurchaseRequest request = new PurchaseRequest();
        request.setCustomerId(customerId.getText().toString()); //Optional email, mobile no, BVN etc to uniquely identify the customer
        request.setAmount(amount.getText().toString()); //Amount in Naira
        if (paymentSpinner.getSelectedItem() == null) {
            return;
        }
        request.setPan(((PaymentMethod) paymentSpinner.getSelectedItem()).getToken());
        request.setPinData(pin.getText().toString());
        request.setCurrency("NGN");
        request.setTransactionRef(RandomString.numeric(12));

//Send payment
        new WalletSDK(context, options).purchase(request, new IswCallback<PurchaseResponse>() {

            @Override
            public void onError(Exception error) {
                // Handle and notify user of error
                progressDialog.dismiss();
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(WalletActivity.this);
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

             String otp;
            @Override
            public void onSuccess(PurchaseResponse response) {
                progressDialog.dismiss();
                if (StringUtils.hasText(response.getOtpTransactionIdentifier())) { //OTP required
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(WalletActivity.this);
                    alertDialog.setTitle("PASSWORD");
                    alertDialog.setMessage("Enter One-time Password");
                    final EditText input = new EditText(WalletActivity.this);
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
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(WalletActivity.this);
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

                        @Override
                        public void onSuccess(AuthorizeOtpResponse otpResponse) {
                            //Handle and notify user of successful transaction
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(WalletActivity.this);
                            alertDialog.setTitle("Transaction Result");
                            alertDialog.setMessage("Transaction succesful");
                            alertDialog.setPositiveButton("Okay",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }
                    });


                } else { //OTP not required
                    //Handle and notify user of successful transaction
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(WalletActivity.this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wallet, menu);
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
