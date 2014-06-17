package si.noemus.boatguard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;


public class DialogFactory {
    private static DialogFactory instance;
    private AlertDialog alertdialog;

    public static DialogFactory getInstance() {
        if (instance == null) {
            instance = new DialogFactory();
        }
        return instance;
    }

    public final void displayWarning(Context ctx, String title, String message,Boolean cancle) {
        displayWarning(ctx, null, title, message, null,cancle);
    }

    public final void displayWarning(Context ctx, String title, String message, DialogListener dialogListener, Boolean cancle) {
        displayWarning(ctx, null, title, message, dialogListener,cancle);
    }

    public final void displayWarning(Context ctx, String title, String message, DialogListener dialogListener, Boolean cancle,String sOK, String sCANCLE) {
        displayWarning(ctx, null, title, message, dialogListener,cancle, sOK,sCANCLE);
    }

//

    private final void displayWarning(Context ctx, Integer messageId, String title, String message, final DialogListener listener, Boolean cancle) {
        try {
            if (message != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle(title);
                builder.setMessage(message);
                builder.setPositiveButton(ctx.getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(listener!=null)
                        listener.onOK();
                    }
                });
                if (cancle)
                    builder.setNegativeButton(ctx.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            listener.noCancel();
                        }
                    });
                builder.show();
            } else if (messageId == null && message == null) {
                alertdialog = new AlertDialog.Builder(ctx).setMessage("Failure").setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if (listener != null) {
                            listener.onOK();
                        }
                    }

                }).setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (listener != null) {
                            listener.onOK();
                        }

                    }
                }).show();
            }
        } catch (Throwable e) {
        	System.out.println("ERROR");
            e.printStackTrace();
        }
    }

    private final void displayWarning(Context ctx, Integer messageId, String title, String message, final DialogListener listener, Boolean cancle, String sOK, String sCANCLE) {
        try {
            if (message != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setTitle(title);
                builder.setMessage(message);
                builder.setPositiveButton(sOK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        listener.onOK();
                    }
                });
                if (cancle)
                    builder.setNegativeButton(sCANCLE, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            listener.noCancel();
                        }
                    });
                builder.show();
            } else if (messageId == null && message == null) {
                alertdialog = new AlertDialog.Builder(ctx).setMessage("Failure").setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if (listener != null) {
                            listener.onOK();
                        }
                    }

                }).setOnCancelListener(new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (listener != null) {
                            listener.onOK();
                        }

                    }
                }).show();
            }
        } catch (Throwable e) {
            // DO NOTHING
        }
    }

    public interface DialogListener {
        public void onOK();

        public void noCancel();
    }
}
