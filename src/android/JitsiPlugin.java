package com.cordova.plugin.jitsi;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.facebook.react.modules.core.PermissionListener;
import com.google.gson.Gson;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginResult;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetActivityDelegate;
import org.jitsi.meet.sdk.JitsiMeetActivityInterface;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.jitsi.meet.sdk.JitsiMeetUserInfo;
import org.jitsi.meet.sdk.JitsiMeetView;
import org.jitsi.meet.sdk.JitsiMeetViewListener;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.Map;

public class JitsiPlugin extends CordovaPlugin implements JitsiMeetActivityInterface {
    private static final String TAG = JitsiPlugin.class.getName();

    public static final int TAKE_PIC_SEC = 0;

    private class JitsiSettings implements Serializable {
        String url;
        String subject;
        String jwt;
        Boolean chatEnabled = false;
        Boolean inviteEnabled = false;
        Boolean calendarEnabled = false;
        Boolean welcomePageEnabled = false;
        Boolean pipEnabled = false;
        Boolean audioOnly = false;
        Boolean audioMuted = false;
        Boolean videoMuted = false;
        JitsiMeetUserInfo userInfo;

        public JitsiMeetConferenceOptions asJitsiOptions() {
            JitsiMeetConferenceOptions.Builder builder = new JitsiMeetConferenceOptions.Builder()
                    .setRoom(url);

            if (jwt != null) {
                builder.setToken(jwt);
            }
            if (welcomePageEnabled != null) {
                builder.setWelcomePageEnabled(welcomePageEnabled);
            }
            if (audioOnly != null) {
                builder.setAudioOnly(audioOnly);
            }
            if (audioMuted != null) {
                builder.setAudioMuted(audioMuted);
            }
            if (videoMuted != null) {
                builder.setVideoMuted(videoMuted);
            }
            if (inviteEnabled != null) {
                builder.setFeatureFlag("invite.enabled", inviteEnabled);
            }
            if (calendarEnabled != null) {
                builder.setFeatureFlag("calendar.enabled", calendarEnabled);
            }
            if (pipEnabled != null) {
                builder.setFeatureFlag("pip.enabled", pipEnabled);
            }
            if (chatEnabled != null) {
                builder.setFeatureFlag("chat.enabled", chatEnabled);
            }
            if (subject != null) {
                builder.setSubject(subject);
            }
            if (userInfo != null) {
                builder.setUserInfo(userInfo);
            }

            return builder.build();
        }
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        // CB-10120: The CAMERA permission does not need to be requested unless it is declared
        // in AndroidManifest.xml. This plugin does not declare it, but others may and so we must
        // check the package info to determine if the permission is present.

        checkPermission();

        if (action.equals("loadURL")) {
            JitsiSettings settings = new Gson().fromJson(args.getJSONObject(0).toString(), JitsiSettings.class);
            this.loadURL(settings, callbackContext);
            return true;
        } else if (action.equals("destroy")) {
            //WIP
            //this.destroy(callbackContext);
            callbackContext.success("NOT IMPLEMENTED");
            return true;
        } else if (action.equals("initCall")) {
            //WIP
            //JitsiSettings settings = new Gson().fromJson(args.getJSONObject(0).toString(), JitsiSettings.class);
            //this.initCall(settings, callbackContext);
            callbackContext.success("NOT IMPLEMENTED");
            return true;
        }
        return false;
    }


    private void checkPermission() {
        boolean takePicturePermission = PermissionHelper.hasPermission(this, Manifest.permission.CAMERA);
        boolean micPermission = PermissionHelper.hasPermission(this, Manifest.permission.RECORD_AUDIO);

        // CB-10120: The CAMERA permission does not need to be requested unless it is declared
        // in AndroidManifest.xml. This plugin does not declare it, but others may and so we must
        // check the package info to determine if the permission is present.

        Log.d(TAG, "tp : " + takePicturePermission);
        Log.d(TAG, "mp : " + micPermission);

        if (!takePicturePermission) {
            takePicturePermission = true;

            try {
                PackageManager packageManager = this.cordova.getActivity().getPackageManager();
                String[] permissionsInPackage = packageManager.getPackageInfo(this.cordova.getActivity().getPackageName(), PackageManager.GET_PERMISSIONS).requestedPermissions;

                if (permissionsInPackage != null) {
                    for (String permission : permissionsInPackage) {
                        if (permission.equals(Manifest.permission.CAMERA)) {
                            takePicturePermission = false;
                            break;
                        }
                    }
                }
                Log.e(TAG, "10 : ");
            } catch (NameNotFoundException e) {
                // We are requesting the info for our package, so this should
                // never be caught
                Log.e(TAG, e.getMessage());
            }
        }

        if (!takePicturePermission) {
            PermissionHelper.requestPermissions(this, TAKE_PIC_SEC,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO});
        }
    }

    private void loadURL(final JitsiSettings settings, final CallbackContext callbackContext) {
        Log.d(TAG, "loadURL called : " + settings.url);

        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                JitsiMeetActivity.launch(cordova.getActivity(), settings.asJitsiOptions());
                callbackContext.success();
            }
        });
    }

    int jitsiViewContainerId = 13377331;
    JitsiMeetView jitsiMeetView;

    /**
     * WIP
     * @param settings
     * @param callbackContext
     */
    private void initCall(final JitsiSettings settings, final CallbackContext callbackContext) {
        Log.e(TAG, "loadURL called : " + settings.url);

        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Activity ctx = cordova.getActivity();
                //  Initialize default options for Jitsi Meet conferences.

                FrameLayout containerView = ctx.findViewById(jitsiViewContainerId);
                if (containerView == null) {
                    containerView = new FrameLayout(ctx);
                    // size
                    containerView.setId(jitsiViewContainerId);
                    FrameLayout.LayoutParams containerLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    int topMargin = dpToPixel(180);
                    int margin = dpToPixel(32);
                    containerLayoutParams.setMargins(margin, topMargin, margin, margin);
                    ctx.addContentView(containerView, containerLayoutParams);
                }
                jitsiMeetView = new JitsiMeetView(ctx);
                containerView.addView(jitsiMeetView);
                FrameLayout.LayoutParams viewLayoutParams = new FrameLayout.LayoutParams(dpToPixel(180), dpToPixel(180));
                viewLayoutParams.gravity = Gravity.CENTER;
                jitsiMeetView.setListener(new JitsiMeetViewListener() {
                    PluginResult pluginResult;

                    private void on(String name, Map<String, Object> data) {
                        Log.d("ReactNative", JitsiMeetViewListener.class.getSimpleName() + " " + name + " " + data);
                    }

                    @Override
                    public void onConferenceJoined(Map<String, Object> data) {
                        on("CONFERENCE_JOINED", data);
                        pluginResult = new PluginResult(PluginResult.Status.OK, "CONFERENCE_JOINED");
                        pluginResult.setKeepCallback(true);
                        callbackContext.sendPluginResult(pluginResult);
                    }

                    @Override
                    public void onConferenceTerminated(Map<String, Object> data) {
                        on("CONFERENCE_TERMINATED", data);
                        pluginResult = new PluginResult(PluginResult.Status.OK, "CONFERENCE_TERMINATED");
                        pluginResult.setKeepCallback(false);
                        FrameLayout containerView = ctx.findViewById(jitsiViewContainerId);
                        if (containerView != null) {
                            containerView.removeAllViews();
                            ((ViewGroup) containerView.getParent()).removeView(containerView);
                        }
                        callbackContext.sendPluginResult(pluginResult);
                    }

                    @Override
                    public void onConferenceWillJoin(Map<String, Object> data) {
                        on("CONFERENCE_WILL_JOIN", data);
                        pluginResult = new PluginResult(PluginResult.Status.OK, "CONFERENCE_WILL_JOIN");
                        pluginResult.setKeepCallback(true);
                        callbackContext.sendPluginResult(pluginResult);
                    }

                });
                jitsiMeetView.join(settings.asJitsiOptions());
            }
        });
    }

    /**
     * Function to calculate pixels for different densities of screen
     *
     * @param dp measurement of element
     * @return pixels
     */
    private int dpToPixel(int dp) {
        return dp * (cordova.getContext().getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    private void destroy(final CallbackContext callbackContext) {
        cordova.getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Activity ctx = cordova.getActivity();
                FrameLayout containerView = ctx.findViewById(jitsiViewContainerId);
                if (containerView != null) {
                    if(jitsiMeetView != null){
                        jitsiMeetView.setListener(null);
                        jitsiMeetView.dispose();
                    }
                    containerView.removeAllViews();
                    ((ViewGroup) containerView.getParent()).removeView(containerView);
                }
                callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, "DESTROYED"));
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(
            final int requestCode,
            final String[] permissions,
            final int[] grantResults) {
        JitsiMeetActivityDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void requestPermissions(String[] permissions, int requestCode, PermissionListener listener) {
        JitsiMeetActivityDelegate.requestPermissions(cordova.getActivity(), permissions, requestCode, listener);
    }

    @Override
    public boolean shouldShowRequestPermissionRationale(String permissions) {
        return true;
    }

    @Override
    public int checkSelfPermission(String permission) {
        return 0;
    }

    @Override
    public int checkPermission(String permission, int pid, int uid) {
        return 0;
    }

}
