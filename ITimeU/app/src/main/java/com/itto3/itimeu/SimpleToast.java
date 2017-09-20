package com.itto3.itimeu;

import android.content.Context;
import android.widget.Toast;

final class SimpleToast {
    private Context context;

    SimpleToast(Context context) {
        this.context = context;
    }

    void showShortTimeToast(int messageResource) {
        Toast.makeText(context, messageResource, Toast.LENGTH_SHORT).show();
    }

    void showLongTimeToast(int messageResource) {
        Toast.makeText(context, messageResource, Toast.LENGTH_LONG).show();
    }
}
