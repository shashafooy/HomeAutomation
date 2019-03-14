package com.example.homeautomation;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

public class GlobalDialogBuilders {

    public static AlertDialog.Builder exitAppDialogBuilder(final Context context) {
        AlertDialog.Builder exit_app = new AlertDialog.Builder(context)
                .setTitle("Exit App")
                .setMessage("Are you sure you want close the app?")
                .setCancelable(true)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((Activity) context).finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert);
        return exit_app;
    }
}
