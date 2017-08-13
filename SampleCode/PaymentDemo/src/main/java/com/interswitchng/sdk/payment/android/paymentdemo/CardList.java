package com.interswitchng.sdk.payment.android.paymentdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.interswitchng.sdk.auth.Passport;
import com.interswitchng.sdk.model.RequestOptions;
import com.interswitchng.sdk.payment.IswCallback;
import com.interswitchng.sdk.payment.Payment;
import com.interswitchng.sdk.payment.android.PaymentSDK;
import com.interswitchng.sdk.payment.android.inapp.PayWithToken;
import com.interswitchng.sdk.payment.android.inapp.ValidateCard;
import com.interswitchng.sdk.payment.android.paymentdemo.callback.AddCardCallback;
import com.interswitchng.sdk.payment.android.util.Util;
import com.interswitchng.sdk.payment.model.AuthorizeCardResponse;
import com.interswitchng.sdk.payment.model.Card;
import com.interswitchng.sdk.payment.model.PaymentStatusRequest;
import com.interswitchng.sdk.payment.model.PaymentStatusResponse;
import com.interswitchng.sdk.payment.model.PurchaseResponse;
import com.interswitchng.sdk.util.RandomString;

import java.util.ArrayList;

public class CardList extends AppCompatActivity {
    private ArrayList<AuthorizeCardResponse> arrayList;
    private ListView cardList;
    private Activity activity;
    private Context context;
    private AddCardAdapter adapter;
    private String transactionRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);
        cardList = (ListView) findViewById(R.id.cardList);
        arrayList = new ArrayList<>();
        activity = this;
        context = this;
        Payment.overrideApiBase(Payment.SANDBOX_API_BASE); // used to override the payment api base url.
        Passport.overrideApiBase(Passport.SANDBOX_API_BASE); //used to override the payment api base url.
        adapter = new AddCardAdapter(arrayList, getApplicationContext());
        cardList.setAdapter(adapter);
        cardList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                /*Util.showProgressDialog(context, "Charging card");
                // When clicked, Pay using token
                AddCardResponse response = arrayList.get(position);
                RequestOptions options = RequestOptions.builder().setClientId("IKIA6B0791CBFF07D20A9C0183BED00C2703EBA97092").setClientSecret("zjtlRvzVq2eG8B+CRYScnIWNQsMQG9/GaU4aLStdY9w=").build();
                String customerId = "1407002510";
                String amount = "200";
                String token = response.getToken();
                String expiryDate = response.getTokenExpiryDate();
                //String requestorId = "11179920172";
                String currency = "NGN";
                PayWithToken payWithCardNoUi = new PayWithToken(activity, customerId, amount, token, expiryDate, currency, options, new IswCallback<PurchaseResponse>() {

                    @Override
                    public void onError(Exception error) {
                        Util.hideProgressDialog();
                        Util.notify(context, "Error", error.getMessage(), "Close", false);
                    }

                    @Override
                    public void onSuccess(final PurchaseResponse response) {
                        Util.hideProgressDialog();
                        String transactionIdentifier;
                        transactionIdentifier = response.getTransactionIdentifier();
                        Util.notify(context, "Success", "Ref: " + transactionIdentifier, "Close", false);
                    }
                });
                payWithCardNoUi.start();*/
                android.view.ActionMode mActionMode = CardList.this.startActionMode(new ActionBarCallBack(position));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        transactionRef = RandomString.numeric(12);
        //noinspection SimplifiableIfStatement
        if (id == R.id.addCard) {
            final RequestOptions options = RequestOptions.builder().setClientId("IKIA7A92206C10CA49EB553E9FAB51A38F27F4644551").setClientSecret("e4THPrg8rgXk3eiBsSHPJvAX4Wvpuxsg6aaPlNUoRKc=").build();
            //final RequestOptions options = RequestOptions.builder().setClientId("IKIAB9CAC83B8CB8D064799DB34A58D2C8A7026A203B").setClientSecret("z+xzMgCB8cUu1XRlzj06/TiFgT9p2wuA6q5wiZc5HZo=").build();
            //final RequestOptions options = RequestOptions.builder().setClientId("IKIAF8F70479A6902D4BFF4E443EBF15D1D6CB19E232").setClientSecret("ugsmiXPXOOvks9MR7+IFHSQSdk8ZzvwQMGvd0GJva30=").build();
            AddCardCallback addCardCallback = new AddCardCallback(context, arrayList, adapter);
            final ValidateCard validateCard = new ValidateCard(activity, "1407002510", options, addCardCallback);
            validateCard.start();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public final class AddCardAdapter extends ArrayAdapter<AuthorizeCardResponse> {
        private Context context;
        private ArrayList<AuthorizeCardResponse> validateCardResponses;
        private Drawable mCurrentDrawable;

        public AddCardAdapter(ArrayList<AuthorizeCardResponse> list, Context ctx) {
            super(ctx, R.layout.adapter_list, list);
            this.validateCardResponses = list;
            this.context = ctx;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            // First let's verify the convertView is not null
            if (convertView == null) {
                // This a new view we inflate the new layout
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.adapter_list, parent, false);
            }
            ImageView img = (ImageView) convertView.findViewById(R.id.cardType);
            TextView cardDescription = (TextView) convertView.findViewById(R.id.list_content);
            AuthorizeCardResponse response = validateCardResponses.get(position);
            cardDescription.setText("PERSONAL ****" + response.getPanLast4Digits());
            if (response.getCardType().equalsIgnoreCase(Card.MASTERCARD)) {
                mCurrentDrawable = context.getResources().getDrawable(R.mipmap.mastercard);
                assert mCurrentDrawable != null;
                mCurrentDrawable.setBounds(0, 0, 75, 60);
                img.setImageDrawable(mCurrentDrawable);
            } else if (response.getCardType().equalsIgnoreCase(Card.VERVE)) {
                mCurrentDrawable = context.getResources().getDrawable(R.mipmap.verve);
                assert mCurrentDrawable != null;
                mCurrentDrawable.setBounds(0, 0, 75, 60);
                img.setImageDrawable(mCurrentDrawable);
            } else if (response.getCardType().equalsIgnoreCase(Card.VISA)) {
                mCurrentDrawable = context.getResources().getDrawable(R.mipmap.visa);
                assert mCurrentDrawable != null;
                mCurrentDrawable.setBounds(0, 0, 75, 60);
                img.setImageDrawable(mCurrentDrawable);
            }
            return convertView;
        }
    }

    class ActionBarCallBack implements android.view.ActionMode.Callback {
        int position;

        public ActionBarCallBack(int position) {
            this.position = position;
        }

        @Override
        public boolean onCreateActionMode(android.view.ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.contextual_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.view.ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(android.view.ActionMode actionMode, MenuItem menuItem) {
            AuthorizeCardResponse response = arrayList.get(position);
            //final RequestOptions options = RequestOptions.builder().setClientId("IKIAF8F70479A6902D4BFF4E443EBF15D1D6CB19E232").setClientSecret("ugsmiXPXOOvks9MR7+IFHSQSdk8ZzvwQMGvd0GJva30=").build();
            //final RequestOptions options = RequestOptions.builder().setClientId("IKIAB9CAC83B8CB8D064799DB34A58D2C8A7026A203B").setClientSecret("z+xzMgCB8cUu1XRlzj06/TiFgT9p2wuA6q5wiZc5HZo=").build();
            final RequestOptions options = RequestOptions.builder().setClientId("IKIA7A92206C10CA49EB553E9FAB51A38F27F4644551").setClientSecret("e4THPrg8rgXk3eiBsSHPJvAX4Wvpuxsg6aaPlNUoRKc=").build();
            String customerId = "1407002510";
            String amount = "200";
            String token = response.getToken();
            String expiryDate = response.getTokenExpiryDate();
            //String requestorId = "11179920172";
            String currency = "NGN";


            // When clicked, Pay using token
            PayWithToken payWithToken = new PayWithToken(activity, customerId, amount, token, expiryDate, currency, response.getCardType(), response.getPanLast4Digits(), "Payment for Gbagada to Victoria Island", options, new IswCallback<PurchaseResponse>() {

                @Override
                public void onError(Exception error) {
                    Util.hideProgressDialog();
                    Util.notify(context, "Error", error.getMessage(), "Close", false);
                }

                @Override
                public void onSuccess(final PurchaseResponse response) {
                    Util.hideProgressDialog();
                    String transactionIdentifier;
                    transactionIdentifier = response.getTransactionRef();
                    Util.notify(context, "Success", "Ref: " + transactionIdentifier, "Close", false);
                    getPaymentStatus(response, options);
                }
            });
            payWithToken.start();
            return false;
        }

        private void getPaymentStatus(PurchaseResponse response, RequestOptions options) {
            PaymentStatusRequest request = new PaymentStatusRequest();
            request.setTransactionRef(response.getTransactionRef());
            request.setAmount(response.getAmount());
            new PaymentSDK(context, options).paymentStatus(request, new IswCallback<PaymentStatusResponse>() {
                @Override
                public void onError(Exception error) {
                    // Handle and notify user of error
                }

                @Override
                public void onSuccess(PaymentStatusResponse response) {
                    System.out.println(response.getMessage());
                    System.out.println(response.getTransactionRef());
                    System.out.println(response.getAmount());

                }
            });
        }
        @Override
        public void onDestroyActionMode(android.view.ActionMode actionMode) {

        }
    }

}
