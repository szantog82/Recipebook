package com.example.szantog.recipebook.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.szantog.recipebook.R;

public class CameraDialogBuilder extends AlertDialog implements View.OnClickListener {

    public interface CameraDialogListener {
        void onCameraButtonClicked(String recognizedText, int target);
    }

    private Context context;
    private ImageView imageView;
    private TextView recognizedTextView;
    private Button readButton;
    private Button asTitleButton;
    private Button asIngredientsButton;
    private Button asDescriptionButton;

    private CameraDialogListener cameraDialogListener;
    public static final int AS_TITLE = 50;
    public static final int AS_INGREDIENT = 51;
    public static final int AS_DESCRIPTION = 52;

    protected CameraDialogBuilder(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.camera_layout, null);
        setView(view);

        imageView = view.findViewById(R.id.camera_dialog_img);
        recognizedTextView = view.findViewById(R.id.camera_dialog_rectext);

        readButton = view.findViewById(R.id.camera_dialog_read);
        asTitleButton = view.findViewById(R.id.camera_dialog_as_title);
        asIngredientsButton = view.findViewById(R.id.camera_dialog_as_ingredients);
        asDescriptionButton = view.findViewById(R.id.camera_dialog_as_description);
        readButton.setOnClickListener(this);
        asTitleButton.setOnClickListener(this);
        asIngredientsButton.setOnClickListener(this);
        asDescriptionButton.setOnClickListener(this);
    }

    public void setCameraDialogListener(CameraDialogListener cameraDialogListener) {
        this.cameraDialogListener = cameraDialogListener;
    }

    @Override
    public void onClick(View view) {
        if (cameraDialogListener != null) {
            switch (view.getId()) {
                case R.id.camera_dialog_as_title:
                    cameraDialogListener.onCameraButtonClicked(recognizedTextView.getText().toString(), AS_TITLE);
                    dismiss();
                    break;
                case R.id.camera_dialog_as_ingredients:
                    cameraDialogListener.onCameraButtonClicked(recognizedTextView.getText().toString(), AS_INGREDIENT);
                    dismiss();
                    break;
                case R.id.camera_dialog_as_description:
                    cameraDialogListener.onCameraButtonClicked(recognizedTextView.getText().toString(), AS_DESCRIPTION);
                    dismiss();
                    break;
                default:
                    break;
            }
        }
    }
}
