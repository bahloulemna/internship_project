package com.gkcrop.scratchthatlogoquiz;

import android.content.Context;
import android.net.ConnectivityManager;

public class Network
{

    private Context context;

    public Network(Context context1)
    {
        context = context1;
    }

    public boolean isOffline()
    {
        return !isOnline();
    }

    public boolean isOnline()
    {
        ConnectivityManager connectivitymanager = (ConnectivityManager)context.getSystemService("connectivity");
        if (connectivitymanager.getActiveNetworkInfo() == null)
        {
            return false;
        } else
        {
            return connectivitymanager.getActiveNetworkInfo().isConnectedOrConnecting();
        }
    }
}
