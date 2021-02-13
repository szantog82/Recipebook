package com.example.szantog.recipebook.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.szantog.recipebook.R;
import com.example.szantog.recipebook.adapters.AddNewRecipeRadioButtonAdapter;
import com.example.szantog.recipebook.models.RecipeItem;

public class AddNewRecipeDialogBuilder extends AlertDialog implements View.OnClickListener, CameraDialogBuilder.CameraDialogListener, AddNewRecipeRadioButtonAdapter.OnRadioButtonClickListener {

    public interface OnNewRecipeAddedListener {
        void onRecipeAdded(RecipeItem recipeItem);
    }

    private static final String[] DISH_TYPES = new String[]{"Leves", "Egytálétel", "Főétel", "Saláta", "Desszert", "Pástétom"};

    private final String TAKE_PIC_REQUEST_CODE = "1012854";

    private Context context;

    private ImageView cameraButton;
    private EditText titleEditText;
    private RadioGroup seasonRadioGroup;
    private GridView typesGridView;
    private AddNewRecipeRadioButtonAdapter typesGridViewAdapter;
    private int typeSelection = -1;
    private CheckBox containsDiaryCheckBox;
    private EditText ingredientsEditText;
    private EditText descriptionEditText;
    private Button okButton;
    private Button cancelButton;

    private RecipeItem newRecipeItem;
    private OnNewRecipeAddedListener listener;

    public AddNewRecipeDialogBuilder(Context context, String title, String ingredients, String description) {
        super(context);
        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.add_new_recipe_layout, null);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindow().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        view.setMinimumHeight((int) (metrics.heightPixels * 0.95));
        setView(view);

        cameraButton = view.findViewById(R.id.add_new_recipe_dialog_camera);
        titleEditText = view.findViewById(R.id.add_new_recipe_dialog_title);
        seasonRadioGroup = view.findViewById(R.id.add_new_recipe_dialog_season_radiogroup);
        typesGridViewAdapter = new AddNewRecipeRadioButtonAdapter(context, DISH_TYPES);
        typesGridView = view.findViewById(R.id.add_new_recipe_dialog_gridview);
        typesGridView.setAdapter(typesGridViewAdapter);
        typesGridViewAdapter.setOnRadioButtonClickListener(this);
        containsDiaryCheckBox = view.findViewById(R.id.add_new_recipe_dialog_diary_checkbox);
        ingredientsEditText = view.findViewById(R.id.add_new_recipe_dialog_ingredients);
        descriptionEditText = view.findViewById(R.id.add_new_recipe_dialog_description);
        okButton = view.findViewById(R.id.add_new_recipe_dialog_okbtn);
        cancelButton = view.findViewById(R.id.add_new_recipe_dialog_cancelbtn);

        cameraButton.setOnClickListener(this);
        okButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);

        titleEditText.setText(title);
        ingredientsEditText.setText(ingredients);
        descriptionEditText.setText(description);
    }

    public void showDialog() {
        containsDiaryCheckBox.setChecked(false);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        show();
    }

    public void setNewRecipeAddedListener(OnNewRecipeAddedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_new_recipe_dialog_camera:
                CameraDialogBuilder cameraDialogBuilder = new CameraDialogBuilder(context);
                cameraDialogBuilder.setCameraDialogListener(this);
                cameraDialogBuilder.show();
                break;
            case R.id.add_new_recipe_dialog_okbtn:
                String season;

                switch (seasonRadioGroup.getCheckedRadioButtonId()) {
                    case R.id.add_new_recipe_dialog_season_winter_button:
                        season = "Téli";
                        break;
                    case R.id.add_new_recipe_dialog_season_summer_button:
                        season = "Nyári";
                        break;
                    default:
                        season = "";
                        break;
                }

                String[] ingredients = ingredientsEditText.getText().toString().split(", ");
                if (typeSelection == -1) {
                    Toast.makeText(context, "Típus nincsen kiválasztva", Toast.LENGTH_SHORT).show();
                } else {
                    newRecipeItem = new RecipeItem(String.valueOf(System.currentTimeMillis()), titleEditText.getText().toString(),
                            season, DISH_TYPES[typeSelection], containsDiaryCheckBox.isChecked(),
                            ingredients, descriptionEditText.getText().toString());
                    if (listener != null) {
                        listener.onRecipeAdded(newRecipeItem);
                    }
                    dismiss();
                }
                break;
            case R.id.add_new_recipe_dialog_cancelbtn:
                dismiss();
                break;
        }
    }

    @Override
    public void onCameraButtonClicked(String recognizedText, int target) {
        switch (target) {
            case CameraDialogBuilder.AS_TITLE:
                titleEditText.setText(titleEditText.getText().toString() + recognizedText);
                break;
            case CameraDialogBuilder.AS_INGREDIENT:
                ingredientsEditText.setText(ingredientsEditText.getText().toString() + recognizedText);
                break;
            case CameraDialogBuilder.AS_DESCRIPTION:
                descriptionEditText.setText(descriptionEditText.getText().toString() + recognizedText);
                break;
        }
    }

    @Override
    public void onRadioButtonClicked(int selection) {
        typeSelection = selection;
    }
}
