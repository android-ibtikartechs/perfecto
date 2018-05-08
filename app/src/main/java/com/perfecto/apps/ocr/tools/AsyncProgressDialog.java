package com.perfecto.apps.ocr.tools;

import android.app.ProgressDialog;
import android.content.Context;


/**
 * Created by hosam azzam on 04/11/2017.
 */

public class AsyncProgressDialog {
    private Context context;
    private int Dialogs_Count = 1;
    private ProgressDialog progressDialog;
    private String title = "Progress Dialog", msg = "Loading..";
    private boolean cancleable = true;
    private StatusListener listener;

    public AsyncProgressDialog(Context context) {
        this.context = context;
    }

    public AsyncProgressDialog(Context context, int count) {
        this.context = context;
        this.Dialogs_Count = count;
    }

    public AsyncProgressDialog(Context context, int count, String title) {
        this.context = context;
        this.Dialogs_Count = count;
        this.title = title;
    }

    public AsyncProgressDialog(Context context, int count, String title, String massage) {
        this.context = context;
        this.Dialogs_Count = count;
        this.title = title;
        this.msg = massage;
    }

    public AsyncProgressDialog(Context context, int count, String title, String massage, Boolean cancleable) {
        this.context = context;
        this.Dialogs_Count = count;
        this.title = title;
        this.msg = massage;
        this.cancleable = cancleable;
    }

    public void setStatusListener(StatusListener statusListener) {
        this.listener = statusListener;
    }

    public Context getContext() {
        return this.context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public int getDialog_Count() {
        return this.Dialogs_Count;
    }

    public void setDialog_Count(int dialog_Count) {
        this.Dialogs_Count = dialog_Count;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isCancleable() {
        return this.cancleable;
    }

    public void setCancleable(boolean cancleable) {
        this.cancleable = cancleable;
    }

    public void build() {
        if (this.context != null) {
            try {
                this.progressDialog = new ProgressDialog(this.context, this.Dialogs_Count);
                this.progressDialog.setTitle(this.title);
                this.progressDialog.setMessage(this.msg);
                this.progressDialog.setCancelable(this.cancleable);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void rebuild() {
        if (this.context != null) {
            if (this.progressDialog.isShowing())
                this.progressDialog.dismiss();
            this.progressDialog = new ProgressDialog(context);
            this.progressDialog.setTitle(this.title);
            this.progressDialog.setMessage(this.msg);
            this.progressDialog.setCancelable(this.cancleable);
            this.progressDialog.show();
            if (this.listener != null) {
                this.listener.onShow();
            }
        }
    }

    public void show() {
        if (this.progressDialog != null) {
            if (this.Dialogs_Count > 0) {
                if (!this.progressDialog.isShowing()) {
                    this.progressDialog.show();
                    if (this.listener != null) {
                        this.listener.onShow();
                    }
                }
            }
        }
    }

    public void terminate() {
        if (this.progressDialog != null) {
            this.progressDialog.dismiss();
            if (this.listener != null) {
                this.listener.onDismiss();
            }
        }
    }

    public void close() {
        if (this.progressDialog != null) {
            if (this.Dialogs_Count > 0) {
                if (this.progressDialog.isShowing()) {
                    this.Dialogs_Count--;
                    if (this.listener != null) {
                        this.listener.onNext(this.Dialogs_Count);
                    }
                }
            } else {
                terminate();
            }
        }
    }

    public interface StatusListener {
        void onShow();

        void onNext(int remainCount);

        void onDismiss();
    }

}
