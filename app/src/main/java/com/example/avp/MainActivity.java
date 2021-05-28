package com.example.avp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.sign_in_button).setOnClickListener(view -> signIn());
    }

    private void signIn() {
        startActivity(new Intent(this, SignInWithDrive.class));
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, VideoActivity.class);
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();

        intent.putExtra("message", message);
        startActivity(intent);
    }
}