package com.safevoice.app.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.safevoice.app.MainActivity;
import com.safevoice.app.R;

import java.util.ArrayList;
import java.util.Locale;

/**
 * A foreground service that continuously listens for the voice trigger "Help Help".
 * It uses Android's built-in SpeechRecognizer. To achieve continuous listening,
 * it restarts the recognizer every time it stops (either on a result or an error).
 */
public class VoiceRecognitionService extends Service {

    private static final String TAG = "VoiceRecognitionService";
    private static final String CHANNEL_ID = "VoiceRecognitionChannel";
    private static final int NOTIFICATION_ID = 1;

    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;

    // A public static flag to allow UI components (like HomeFragment) to check if the service is active.
    public static boolean isServiceRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        isServiceRunning = true;

        // Initialize the SpeechRecognizer
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new VoiceRecognitionListener());

        // Set up the intent for the speech recognizer
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start the service in the foreground
        startForeground(NOTIFICATION_ID, createNotification());
        Log.d(TAG, "Service started and is now in the foreground.");

        // Start listening
        startListening();

        // If the service is killed, it will be automatically restarted.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceRunning = false;
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
            speechRecognizer.destroy();
        }
        Log.d(TAG, "Service destroyed.");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // This is a started service, not a bound one, so we return null.
        return null;
    }

    private void startListening() {
        if (speechRecognizer != null) {
            speechRecognizer.startListening(speechRecognizerIntent);
            Log.d(TAG, "Speech recognizer started listening...");
        }
    }

    /**
     * Creates the persistent notification required for a foreground service.
     *
     * @return The Notification object.
     */
    private Notification createNotification() {
        // Create a notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Safe Voice Active Service",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(serviceChannel);
            }
        }

        // Create an intent that will open MainActivity when the notification is tapped
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        // Build the notification
        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Safe Voice is Active")
                .setContentText("Listening for your voice trigger...")
                .setSmallIcon(R.drawable.ic_launcher_foreground) // You should create this icon
                .setContentIntent(pendingIntent)
                .build();
    }

    /**
     * The core RecognitionListener that handles speech-to-text results and errors.
     */
    private class VoiceRecognitionListener implements RecognitionListener {

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (matches != null) {
                for (String result : matches) {
                    Log.d(TAG, "Heard: " + result);
                    // Check if the recognized text contains the trigger phrase (case-insensitive)
                    if (result.toLowerCase().contains("help help")) {
                        Log.i(TAG, "TRIGGER PHRASE DETECTED!");

                        // Launch the EmergencyHandlerService to handle the alert
                        Intent emergencyIntent = new Intent(VoiceRecognitionService.this, EmergencyHandlerService.class);
                        startService(emergencyIntent);

                        // Stop listening after a successful trigger to prevent multiple alerts
                        // The service will need to be manually restarted by the user.
                        // You could also choose to automatically restart after a delay.
                        // For now, we stop the service to be safe.
                        stopSelf();
                        return; // Exit the loop and method
                    }
                }
            }
            // If the trigger phrase was not detected, restart listening for the next utterance.
            startListening();
        }

        @Override
        public void onError(int error) {
            // Most errors are normal (e.g., no speech detected). We just restart the listener.
            Log.d(TAG, "Speech recognizer error: " + error);
            // Restart listening after any error to ensure continuity.
            startListening();
        }

        // --- Other listener methods (can be left empty for this implementation) ---
        @Override
        public void onReadyForSpeech(Bundle params) { Log.d(TAG, "Ready for speech..."); }
        @Override
        public void onBeginningOfSpeech() { Log.d(TAG, "Beginning of speech..."); }
        @Override
        public void onRmsChanged(float rmsdB) { /* Do nothing */ }
        @Override
        public void onBufferReceived(byte[] buffer) { /* Do nothing */ }
        @Override
        public void onEndOfSpeech() { Log.d(TAG, "End of speech."); }
        @Override
        public void onPartialResults(Bundle partialResults) { /* Do nothing */ }
        @Override
        public void onEvent(int eventType, Bundle params) { /* Do nothing */ }
    }
}
