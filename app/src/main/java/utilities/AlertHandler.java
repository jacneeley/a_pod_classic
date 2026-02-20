package utilities;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.example.musicdemoapp.MainActivity;
import com.example.musicdemoapp.MusicActivity;

public class AlertHandler {

    /** Create an Alert Dialog with OK that is not cancellable
    * @param context: context object
    * @param title: tile of the alert
    * @param message: message to be displayed
    *  @return builder object
    */
    public static AlertDialog.Builder okAlert(Context context, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);

        return builder;
    }

    /**
     * Show alert to user if music files fail to load.
     * Handles errors related to REPO find methods gracefully.
     * @param context
     */
    public static void handleEmptySongList(Activity actv, Context context) {
            AlertDialog.Builder builder = AlertHandler.okAlert(
                    context,
                    "Alert:",
                    "ERROR: Music could be loaded...");
            builder.setPositiveButton("OK", (dialog, which) -> { /*do nothing */ });

            //TODO: give option to reset/restart app?
            builder.setNegativeButton("RESTART", (dialog, which) -> {
                actv.finish();
                actv.startActivity(new Intent(context, MainActivity.class));
            });

            AlertDialog alert = builder.create();
            alert.show();
    }

    /**
     * Uses AlertDialog.Builder to build app specific generic Alerts.
     * Restarts app after user clicks 'OK'.
     * Designed to handle errors gracefully.
     * @param actv
     * @param context
     */
    public static void genericOkAlert(Activity actv, Context context) {
        AlertDialog.Builder builder = AlertHandler.okAlert(
                context,
                "ERROR:",
                "Unexpected Error Occurred...");

        builder.setPositiveButton("OK", (DialogInterface.OnClickListener) (dialog, which) -> {
            actv.finish();
            actv.startActivity(new Intent(context, MainActivity.class));
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Uses AlertDialog.Builder to build app specific generic alerts with a provided message.
     * Restarts app after user clicks 'OK'.
     * Designed to handle errors gracefully.
     * @param actv
     * @param context
     * @param msg string message to display to user
     */
    public static void genericOkAlert(Activity actv, Context context, String msg) {
        AlertDialog.Builder builder = AlertHandler.okAlert(
                context,
                "ERROR:",
                msg);

        builder.setPositiveButton("OK", (DialogInterface.OnClickListener) (dialog, which) -> {
            actv.finish();
            actv.startActivity(new Intent(context, MainActivity.class));
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}
