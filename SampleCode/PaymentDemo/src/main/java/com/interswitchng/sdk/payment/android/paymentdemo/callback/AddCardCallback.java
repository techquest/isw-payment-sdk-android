package com.interswitchng.sdk.payment.android.paymentdemo.callback;

import android.content.Context;

import com.interswitchng.sdk.payment.IswCallback;
import com.interswitchng.sdk.payment.android.paymentdemo.CardList;
import com.interswitchng.sdk.payment.android.util.Util;
import com.interswitchng.sdk.payment.model.AuthorizeCardResponse;

import java.util.ArrayList;

/**
 * Created by Akinyemi.Akindele on 4/27/2017.
 */

public class AddCardCallback extends IswCallback<AuthorizeCardResponse> {
    private transient Context context;
    private transient ArrayList arrayList;
    private transient CardList.AddCardAdapter addCardAdapter;

    public AddCardCallback(Context context, ArrayList arrayList, CardList.AddCardAdapter addCardAdapter) {
        this.context = context;
        this.arrayList = arrayList;
        this.addCardAdapter = addCardAdapter;
    }

    @Override
    public void onError(Exception error) {
        Util.notify(context, "Error", error.getLocalizedMessage(), "Close", false);
    }

    @Override
    public void onSuccess(AuthorizeCardResponse response) {
        String transactionRef = response.getTransactionRef();
        Util.notify(context, "Success", "Ref: " + transactionRef, "Close", false);
        if (!arrayList.contains(response)) {
            arrayList.add(response);
            addCardAdapter.notifyDataSetChanged();
        }
    }

}
