package com.example.szantog.recipebook.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.szantog.recipebook.R;
import com.example.szantog.recipebook.controllers.DatabaseHandlerChat;
import com.example.szantog.recipebook.models.ChatItem;
import com.example.szantog.recipebook.services.ChatService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class ChatFragment extends Fragment implements Runnable {

    SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm");
    public static final String CHATSERVER_URL = "https://recipes-szg-chat.herokuapp.com/";

    private Boolean connected = false;
    private String loginName;
    private static int count = 10;
    public static final String PREF_NAME = "pref_name";

    private RelativeLayout relativeLayout;
    private ScrollView scrollView;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private View dialogView;
    private DatabaseHandlerChat databaseHandlerChat;
    private Boolean visible = false;
    public static Boolean visibilityForService = false;

    private Intent serviceIntent;
    public static final String INPUT_CHAT_EXTRA = "input_chat_extra";
    private BroadcastReceiver receiver;

    private SharedPreferences sharedPrefs;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.optionsmenu_chat, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.chat_login_name) {
            OpenDialog();
        } else if (item.getItemId() == R.id.chat_stopnetwork) {
            DisconnectFromServer();
        } else if (item.getItemId() == R.id.chat_login_deletename) {
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putString(PREF_NAME, null);
            editor.apply();
            loginName = null;
            try {
                Intent serviceIntent = new Intent(getActivity(), ChatService.class);
                getActivity().stopService(serviceIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (item.getItemId() == R.id.chat_delete_all_local_data) {
            removeLocalData();
        } else if (item.getItemId() == R.id.chat_quit) {
            try {
                Intent serviceIntent = new Intent(getActivity(), ChatService.class);
                getActivity().stopService(serviceIntent);
            } catch (Exception e) {
                e.printStackTrace();
            }
            getActivity().finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (visible && isVisibleToUser && loginName != null) {
            visibilityForService = true;
            LoadLocalData();
            ConnectToServer();

        } else if (visible && isVisibleToUser && loginName == null) {
            visibilityForService = false;
            OpenDialog();
        } else if (visible && !isVisibleToUser) {
            visibilityForService = false;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setUserVisibleHint(false);
        return inflater.inflate(R.layout.chat_mainscreen, null);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        visible = true;

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        relativeLayout = view.findViewById(R.id.chat_relativelayout);
        sharedPrefs = getActivity().getSharedPreferences(PREF_NAME, 0);
        loginName = sharedPrefs.getString(PREF_NAME, null);
        scrollView = view.findViewById(R.id.chat_scrollview);
        dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogView = getActivity().getLayoutInflater().inflate(R.layout.chat_dialog, null);
        dialogBuilder.setView(dialogView);
        dialog = dialogBuilder.create();
        databaseHandlerChat = new DatabaseHandlerChat(this.getContext());
        serviceIntent = new Intent(getContext(), ChatService.class);

        LoadLocalData();

        final EditText editText = view.findViewById(R.id.chat_edittext);
        final Button sendButton = view.findViewById(R.id.chat_sendbtn);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (loginName != null) {
                    String input = editText.getText().toString();
                    serviceIntent.putExtra(INPUT_CHAT_EXTRA, new ChatItem(System.currentTimeMillis(), loginName, input));
                    getActivity().startService(serviceIntent);
                    editText.setText("");
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                } else {
                    Toast.makeText(getContext(), "Nincs név beállítva", Toast.LENGTH_SHORT).show();
                }
            }
        });

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                ChatItem item = (ChatItem) intent.getSerializableExtra(ChatService.FROM_SERVICE_CHATITEM_EXTRA);
                Boolean newMessage = intent.getBooleanExtra("newmessage", false);
                Boolean successfulUpload = intent.getBooleanExtra("successfulupload", false);
                AddTextToView(item.getFrom(), item.getMessage(), System.currentTimeMillis(), newMessage, successfulUpload);
            }
        };
        getActivity().registerReceiver(receiver, new IntentFilter(ChatService.CHATITEM_SEND_ACTION));

        Button delbtn = view.findViewById(R.id.chat_delbtn);
        delbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int childCount = relativeLayout.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    if (relativeLayout.getChildAt(i) instanceof TextView) {
                        relativeLayout.removeView(relativeLayout.getChildAt(i));
                    }
                }
                databaseHandlerChat.DeleteAllData();
            }
        });
    }

    private void LoadLocalData() {
        int childCount = relativeLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (relativeLayout.getChildAt(i) instanceof TextView) {
                relativeLayout.removeView(relativeLayout.getChildAt(i));
            }
        }
        try {
            ArrayList<ChatItem> previousItems = databaseHandlerChat.getAllData();
            for (ChatItem items : previousItems) {
                AddTextToView(items.getFrom(), items.getMessage(), items.getTime(), false, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeLocalData() {
        databaseHandlerChat.DeleteAllData();
        relativeLayout.removeAllViews();
    }

    private void ConnectToServer() {
        if (loginName == null) {
            Toast.makeText(getContext(), "Kapcsolódás sikertelen: nincs név beállítva", Toast.LENGTH_SHORT).show();
        } else if (!connected) {
            try {
                Log.e("Socketing", "trying to connect with name: " + loginName + "...");
                serviceIntent.removeExtra(INPUT_CHAT_EXTRA);
                getActivity().startService(serviceIntent);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void DisconnectFromServer() {
        Toast.makeText(getActivity(), "Kapcsolat megszakítása a szerverrel...", Toast.LENGTH_SHORT).show();
        try {
            getActivity().stopService(serviceIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void AddTextToView(String from, String message, long time, Boolean newMessage, Boolean successfulUpload) {
        TextView newText = new TextView(getActivity());
        newText.setId(count);
        newText.setAutoLinkMask(Linkify.WEB_URLS);
        newText.setText(from + ": " + message + "\n" + "(" + dateFormat.format(time) + ")");
        newText.setTextColor(Color.parseColor("#000000"));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(350, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 10, 20, 10);
        newText.setPadding(5, 5, 5, 5);
        newText.setLayoutParams(params);
        newText.setTextSize(12);
        newText.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
        if (count == 10 && from.equals(loginName)) {
            params.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
        } else if (count == 10 && !from.equals(loginName)) {
            params.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
        } else {
            params.addRule(RelativeLayout.BELOW, count - 1);
        }
        if (from.equals(loginName)) {
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            newText.setBackgroundResource(R.drawable.message_background_own);
        } else {
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            newText.setBackgroundResource(R.drawable.message_background_incoming);
        }
        relativeLayout.addView(newText);
        count++;
        if (newMessage)
            databaseHandlerChat.InsertData(new ChatItem(time, from, message), successfulUpload);
        scrollView.post(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        try {
            getActivity().unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        //scroll.scrollTo(0, scroll.getBottom());;
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    public void OpenDialog() {
        dialog.show();
        final EditText editText = dialogView.findViewById(R.id.chatdialog_edittext);
        if (loginName != null) {
            editText.setText(loginName);
        }

        dialogView.findViewById(R.id.chatdialog_okbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputText = editText.getText().toString();
                if (inputText.length() < 4) {
                    Toast.makeText(getContext(), "Túl rövid név", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putString(PREF_NAME, inputText);
                    editor.commit();
                    loginName = inputText;
                    dialog.dismiss();
                    if (loginName != null) {
                        ConnectToServer();
                    }
                }
            }
        });

        dialogView.findViewById(R.id.chatdialog_cancelbtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}
