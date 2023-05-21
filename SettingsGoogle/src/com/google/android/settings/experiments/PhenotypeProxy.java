package com.google.android.settings.experiments;

import android.content.ContentProviderClient;
import android.util.Log;
import android.os.Bundle;
import android.content.Context;
import android.net.Uri;

public class PhenotypeProxy
{
    private static final Uri PROXY_AUTHORITY;
    
    static {
        PROXY_AUTHORITY = new Uri.Builder().scheme("content").authority("com.google.android.settings.intelligence.provider.experimentflags").build();
    }

    public static boolean getFlagByPackageAndKey(Context context, String call, String s, boolean z) {
        Bundle bundle = new Bundle();
        bundle.putString("package_name", call);
        bundle.putString("key", s);
        Bundle bundle2 = null;
        try {
            ContentProviderClient acquireUnstableContentProviderClient = context.getContentResolver().acquireUnstableContentProviderClient(PROXY_AUTHORITY);
            bundle2 = acquireUnstableContentProviderClient.call("getBooleanForPackageAndKey", null, bundle);
            acquireUnstableContentProviderClient.close();
        } catch (Exception ex) {
            Log.e("PhenotypeProxy", "Failed to query experiment provider", ex);
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        if (bundle2 == null) {
            return z;
        }
        return bundle2.getBoolean("value", z);
    }

    public static boolean getBooleanFlagByPackageAndKey(Context context, String call, String s, boolean b) {
        Bundle bundle = new Bundle();
        bundle.putString("package_name", call);
        bundle.putString("key", s);
        Bundle bundle2 = null;
        try {
            ContentProviderClient acquireUnstableContentProviderClient = context.getContentResolver().acquireUnstableContentProviderClient(PROXY_AUTHORITY);
            bundle2 = acquireUnstableContentProviderClient.call("getBooleanForPackageAndKey", null, bundle);
            acquireUnstableContentProviderClient.close();
        } catch (Exception ex) {
            Log.e("PhenotypeProxy", "Failed to query experiment provider", ex);
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        if (bundle2 == null) {
            return b;
        }
        return bundle2.getBoolean("value", b);
    }

    public static String getStringFlagByPackageAndKey(Context context, String call, String s, String s2) {
        Bundle bundle = new Bundle();
        bundle.putString("package_name", call);
        bundle.putString("key", s);
        Bundle bundle2 = null;
        try {
            ContentProviderClient acquireUnstableContentProviderClient = context.getContentResolver().acquireUnstableContentProviderClient(PROXY_AUTHORITY);
            bundle2 = acquireUnstableContentProviderClient.call("getStringForPackageAndKey", null, bundle);
            acquireUnstableContentProviderClient.close();
        } catch (Exception ex) {
            Log.e("PhenotypeProxy", "Failed to query experiment provider", ex);
        } catch (Throwable th) {
            th.addSuppressed(th);
        }
        if (bundle2 == null) {
            return s2;
        }
        return bundle2.getString("value", s2);
    }
}
