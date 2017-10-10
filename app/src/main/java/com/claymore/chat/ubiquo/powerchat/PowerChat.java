package com.claymore.chat.ubiquo.powerchat;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.github.tamir7.contacts.Contacts;
import es.dmoral.toasty.Toasty;
import io.github.kbiakov.codeview.classifier.CodeProcessor;


public class PowerChat extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        Toasty.Config.getInstance().apply();
        CodeProcessor.init(this);
        Contacts.initialize(this);

    }
}
