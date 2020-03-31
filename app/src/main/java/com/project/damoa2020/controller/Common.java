package com.project.damoa2020.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class Common {

   public static void showMessage(Activity activity, String str){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setMessage(str);
        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }
}
