package Utilities;

import android.app.AlertDialog;
import android.content.Context;

public class AlertHandler {

    /* Create an Alert Dialog with OK that is not cancellable
    * @param Context context: context object
    * @param String title: tile of the alert
    * @param String message: message to be displayed
    *  @return builder object
    */
    public static AlertDialog.Builder okAlert(Context context, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setCancelable(false);

        return builder;
    }
}
