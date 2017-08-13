package com.interswitchng.sdk.payment.android.paymentdemo.callback;

import android.content.Context;

import com.interswitchng.sdk.payment.IswCallback;
import com.interswitchng.sdk.payment.android.util.Util;
import com.interswitchng.sdk.payment.model.PurchaseResponse;

/**
 * Created by Akinyemi.Akindele on 4/27/2017.
 */

public class PaymentCallback extends IswCallback<PurchaseResponse> {
    private transient Context context;

    public PaymentCallback(Context context) {
        this.context = context;
    }

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

}
