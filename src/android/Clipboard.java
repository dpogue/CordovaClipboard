package com.verso.cordova.clipboard;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.os.Build;
import android.util.Log;

public class Clipboard extends CordovaPlugin {

    private static final String actionCopy = "copy";
    private static final String actionPaste = "paste";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        Object board = cordova.getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        if (action.equals(actionCopy))
        {
            try {
                String text = args.getString(0);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager)board;
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Text", text);
                    clipboard.setPrimaryClip(clip);
                }
                else
                {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager)board;
                    clipboard.setText(text);
                }

                callbackContext.success(text);

                return true;
            } catch (JSONException e) {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.JSON_EXCEPTION));
            } catch (Exception e) {
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR));
            }
        }
        else if (action.equals(actionPaste))
        {
            try {

                String text = null;

                Log.v("Clipboard", "Calling paste");

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                {
                    Log.v("Clipboad", ">= Honeycomb");
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager)board;

                    if (!clipboard.getPrimaryClipDescription().hasMimeType(android.content.ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.NO_RESULT));
                        return true;
                    }

                    android.content.ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                    text = item.getText().toString();
                }
                else
                {
                    Log.v("Clipboad", "< Honeycomb");
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager)board;
                    text = (String)clipboard.getText();
                }

                if (text == null) text = "";

                Log.v("Clipboad", text);

                callbackContext.success(text);

                return true;
            } catch (Exception e) {
                Log.v("Clipboard", Log.getStackTraceString(e));
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR));
            }
        }

        return false;
    }
}


