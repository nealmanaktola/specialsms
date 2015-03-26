package com.knowroaming.specialsms.interfaces;

import java.io.ObjectOutputStream;

/**
 * Created by Neal on 3/26/2015.
 */

public interface ConnectionDialogListener {
    void onConnectionComplete(ObjectOutputStream out);
}

