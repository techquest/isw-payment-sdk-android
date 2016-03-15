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
import com.interswitchng.sdk.payment.android.inapp.PayWithToken;
import com.interswitchng.sdk.payment.android.inapp.ValidateCard;
import com.interswitchng.sdk.payment.android.util.Util;
import com.interswitchng.sdk.payment.model.Card;
import com.interswitchng.sdk.payment.model.PurchaseResponse;
import com.interswitchng.sdk.payment.model.ValidateCardResponse;

import java.util.ArrayList;

public class CardList extends AppCompatActivity {
    private ArrayList<ValidateCardResponse> arrayList;
    private ListView cardList;
    private Activity activity;
    private Context context;
    private AddCardAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);
        cardList = (ListView) findViewById(R.id.cardList);
        arrayList = new ArrayList<ValidateCardResponse>();
        activity = this;
        context = this;
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.addCard) {
            Payment.overrideApiBase("https://qa.interswitchng.com"); // used to override the payment api base url.
            Passport.overrideApiBase("https://qa.interswitchng.com/passport"); //used to override the payment api base url.
            RequestOptions options = RequestOptions.builder().setClientId("IKIAD6DC1B942D95035FBCC5A4449C893D36536B5D54").setClientSecret("X1u1M6UNyASzslufiyxZnLb3u78TYODVnbRi7OxLNew=").build();
            final ValidateCard validateCard = new ValidateCard(activity, "1407002510", "NGN", options, new IswCallback<ValidateCardResponse>() {

                @Override
                public void onError(Exception error) {
                    Util.notify(context, "Error", error.getMessage(), "Close", false);
                }

                @Override
                public void onSuccess(final ValidateCardResponse response) {
                    String transactionIdentifier;
                    transactionIdentifier = response.getTransactionIdentifier();
                    Util.notify(getApplicationContext(), "Success", "Ref: " + transactionIdentifier, "Close", false);
                    if (!arrayList.contains(response)) {
                        arrayList.add(response);
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            validateCard.start();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public final class AddCardAdapter extends ArrayAdapter<ValidateCardResponse> {
        private Context context;
        private ArrayList<ValidateCardResponse> validateCardResponses;
        private Drawable mCurrentDrawable;

        public AddCardAdapter(ArrayList<ValidateCardResponse> list, Context ctx) {
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
            ValidateCardResponse response = validateCardResponses.get(position);
            cardDescription.setText("PERSONAL ****" + response.getPanLast4Digits());
            if (response.getCardType().equalsIgnoreCase(Card.MASTERCARD)) {
                mCurrentDrawable = context.getResources().getDrawable(R.mipmap.mastercard);
                mCurrentDrawable.setBounds(0, 0, 75, 60);
                img.setImageDrawable(mCurrentDrawable);
            } else if (response.getCardType().equalsIgnoreCase(Card.VERVE)) {
                mCurrentDrawable = context.getResources().getDrawable(R.mipmap.verve);
                mCurrentDrawable.setBounds(0, 0, 75, 60);
                img.setImageDrawable(mCurrentDrawable);
            } else if (response.getCardType().equalsIgnoreCase(Card.VISA)) {
                mCurrentDrawable = context.getResources().getDrawable(R.mipmap.visa);
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
            Payment.overrideApiBase("https://qa.interswitchng.com"); // used to override the payment api base url.
            Passport.overrideApiBase("https://qa.interswitchng.com/passport"); //used to override the payment api base url.
            ValidateCardResponse response = arrayList.get(position);
            RequestOptions options = RequestOptions.builder().setClientId("IKIAD6DC1B942D95035FBCC5A4449C893D36536B5D54").setClientSecret("X1u1M6UNyASzslufiyxZnLb3u78TYODVnbRi7OxLNew=").build();
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
                    transactionIdentifier = response.getTransactionIdentifier();
                    Util.notify(context, "Success", "Ref: " + transactionIdentifier, "Close", false);
                }
            });
            payWithToken.start();
            return false;
        }

        @Override
        public void onDestroyActionMode(android.view.ActionMode actionMode) {

        }
    }

}
