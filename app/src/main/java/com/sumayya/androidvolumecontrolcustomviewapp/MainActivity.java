package com.sumayya.androidvolumecontrolcustomviewapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

    private EditText VolumeInput;
    private EditText scaleInput;
    private Button VolumeBtn;
    private Button ScaleBtn;
    private VolumeControlView volumeControl;
    private boolean volumeInputValid = false;
    private boolean scaleInputValid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        VolumeInput = findViewById(R.id.CurrentVolume);
        scaleInput = findViewById(R.id.Volumebar);
        VolumeBtn = findViewById(R.id.SetVolume);
        ScaleBtn = findViewById(R.id.SetScale);
        volumeControl = findViewById(R.id.volumeControl);

        VolumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(volumeInputValid){
                    volumeControl.setVolumeLevel(Integer.parseInt(VolumeInput.getText().toString()));
                }
            }
        });

        ScaleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scaleInputValid) {
                    volumeControl.setVolumeScale(Integer.parseInt(scaleInput.getText().toString()));
                }
            }
        });

        VolumeInput.addTextChangedListener(new TextValidator(VolumeInput) {
            @Override
            public void validate(TextView textView, String text) {
                if(text.isEmpty()) {
                    volumeInputValid = false;
                    VolumeBtn.setEnabled(false);
                }else {
                    int currentVolumeInput = Integer.parseInt(text);
                    if (currentVolumeInput < 0 || currentVolumeInput > 100) {
                        textView.setError(getString(R.string.volume_input_validation_message));
                        volumeInputValid = false;
                        VolumeBtn.setEnabled(false);
                    } else {
                        volumeInputValid = true;
                        VolumeBtn.setEnabled(true);
                    }
                }
            }
        });

        scaleInput.addTextChangedListener(new TextValidator(scaleInput) {
            @Override
            public void validate(TextView textView, String text) {
                if(text.isEmpty()) {
                    scaleInputValid = false;
                    ScaleBtn.setEnabled(false);
                }else {
                    scaleInputValid = true;
                    ScaleBtn.setEnabled(true);
                }
            }
        });

        volumeControl.setEventListener(new VolumeControlView.IVolumeControlEventListener() {
            @Override
            public void onVolumeControlChanged() {
                String volumeLevel = String.valueOf(volumeControl.getVolumeLevel());
                String volumeScale = String.valueOf(volumeControl.getVolumeScale());

                if(!volumeLevel.equals(VolumeInput.getText().toString())){
                    VolumeInput.setText(volumeControl.getVolumeLevel() + "");
                }

                if(!volumeScale.equals(scaleInput.getText().toString())) {
                    scaleInput.setText(volumeControl.getVolumeScale() + "");
                }
            }
        });

        if(savedInstanceState != null){
            if(savedInstanceState.containsKey("VolumeLevel")){
                volumeControl.setVolumeLevel(savedInstanceState.getInt("VolumeLevel"));
            }

            if(savedInstanceState.containsKey("VolumeScale")){
                volumeControl.setVolumeScale(savedInstanceState.getInt("VolumeScale"));
            }
        }

        VolumeInput.setText(String.valueOf(volumeControl.getVolumeLevel()));
        scaleInput.setText(String.valueOf(volumeControl.getVolumeScale()));
    }

    private abstract class TextValidator implements TextWatcher {
        private final TextView textView;

        public TextValidator(TextView textView) {
            this.textView = textView;
        }

        public abstract void validate(TextView textView, String text);

        @Override
        final public void afterTextChanged(Editable s) {
            String text = textView.getText().toString();
            validate(textView, text);
        }

        @Override
        final public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }

        @Override
        final public void onTextChanged(CharSequence s, int start, int before, int count) {  }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("VolumeLevel", volumeControl.getVolumeLevel());
        outState.putInt("VolumeScale", volumeControl.getVolumeScale());
    }
}
