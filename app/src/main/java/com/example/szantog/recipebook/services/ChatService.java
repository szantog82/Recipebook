package com.example.szantog.recipebook.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.szantog.recipebook.R;
import com.example.szantog.recipebook.WrapperActivity;
import com.example.szantog.recipebook.controllers.DatabaseHandlerChat;
import com.example.szantog.recipebook.fragments.ChatFragment;
import com.example.szantog.recipebook.models.ChatItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatService extends Service {

    private Socket socket;
    private Boolean connected = false;
    private Gson gson = new GsonBuilder().create();
    private SharedPreferences settings;
    private String loginName;
    private DatabaseHandlerChat databaseHandlerChat;

    private Intent broadcastIntent;
    public static String CHATITEM_SEND_ACTION = "chatitem_send_action";
    public static String FROM_SERVICE_CHATITEM_EXTRA = "from_service_chatitem_extra";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        broadcastIntent = new Intent();
        broadcastIntent.setAction(CHATITEM_SEND_ACTION);
        ChatItem item = null;
        try {
            item = (ChatItem) intent.getSerializableExtra(ChatFragment.INPUT_CHAT_EXTRA);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!(item == null)) {
            JsonObject obj = new JsonObject();
            obj.addProperty("name", item.getFrom());
            obj.addProperty("message", item.getMessage());
            final Boolean[] successUpload = {false};
            try {
                socket.emit("fromclient", obj, new Ack() {
                    @Override
                    public void call(Object... args) {
                        Log.e("upload", "sikeres");
                        successUpload[0] = true;
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            broadcastIntent.putExtra(FROM_SERVICE_CHATITEM_EXTRA, new ChatItem(System.currentTimeMillis(), item.getFrom(), item.getMessage()));
            broadcastIntent.putExtra("newmessage", true);
            broadcastIntent.putExtra("successfulupload", successUpload[0]);
            getApplication().sendBroadcast(broadcastIntent);
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        databaseHandlerChat = new DatabaseHandlerChat(this);
        settings = getApplication().getSharedPreferences(ChatFragment.PREF_NAME, 0);
        loginName = settings.getString(ChatFragment.PREF_NAME, "null");

        final RemoteViews notificationView = new RemoteViews(getApplication().getPackageName(), R.layout.chat_notification_layout);

        final NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplication());
        notification.setContent(notificationView);
        notification.setSmallIcon(R.drawable.refresh64x64);

        Intent intent = new Intent(getApplication(), WrapperActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplication(), 0, intent, 0);
        notification.setContentIntent(pendingIntent);
        notification.setAutoCancel(true);
        final NotificationManager notificationManager = (NotificationManager) getApplication().getSystemService(getApplication().NOTIFICATION_SERVICE);

        /*DatabaseHandlerChat chat = new DatabaseHandlerChat(this);
        ArrayList<ChatItem> items = chat.getAllData();
        String str = "";
        for (ChatItem item : items) {
            str = str + item.getFrom() + ": ";
            if (item.getMessage().length() >= 15)
                str = str + item.getMessage().substring(0, 12) + "... \n";
            else str = str + item.getMessage() + "... \n";
        }
        notificationView.setTextViewText(R.id.notif_chat_message, str);
        notificationManager.notify(0, notification.build());*/


        Log.e("ChatService", "Chatservice started");
        socket = null;

        try {
            socket = IO.socket(ChatFragment.CHATSERVER_URL);
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                public void call(Object... arg0) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("name", loginName);
                    socket.emit("login", obj);
                }
            }).on("fromserver", new Emitter.Listener() {

                @Override
                public void call(final Object... arg0) {
                    Log.e("ChatService", "Connection established");
                    connected = true;
                    ArrayList<ChatItem> itemsToUpload = databaseHandlerChat.GetFailedUploadData();
                    for (ChatItem item : itemsToUpload) {
                        JsonObject obj = new JsonObject();
                        obj.addProperty("name", loginName);
                        obj.addProperty("message", item.getMessage());
                        socket.emit("login", obj);
                        databaseHandlerChat.ChangeDataToUploaded(item);
                    }

                    String json = arg0[0].toString();
                    ChatItem message = gson.fromJson(json, ChatItem.class);
                    if (message.getFrom().equals("Server")) {
                        Log.e("ChatService", "Server is welcoming back...");
                    } else {
                        broadcastIntent.putExtra(FROM_SERVICE_CHATITEM_EXTRA, new ChatItem(System.currentTimeMillis(), message.getFrom(), message.getMessage()));
                        broadcastIntent.putExtra("newmessage", true);
                        broadcastIntent.putExtra("successfulupload", true);
                        getApplication().sendBroadcast(broadcastIntent);
                        if (!ChatFragment.visibilityForService) {
                            if (message.getMessage().length() > 16) {
                                notificationView.setTextViewText(R.id.notif_chat_message, message.getFrom() + ": " + message.getMessage().substring(0, 12) + "... \n");
                            } else {
                                notificationView.setTextViewText(R.id.notif_chat_message, message.getFrom() + ": " + message.getMessage() + " \n");
                            }
                            notificationManager.notify(0, notification.build());
                        }
                    }
                }
            });
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.e("ChatService-OnDestroy", "Service stopped, socket disconnecting...");
        socket.disconnect();
    }
}
