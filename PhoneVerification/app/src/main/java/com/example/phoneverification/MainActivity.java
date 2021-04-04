package com.example.phoneverification;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {


    private FirebaseAuth mAuth;


    private EditText edtPhone, edtOTP;


    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();


        edtPhone = findViewById(R.id.idEdtPhoneNumber);
        edtOTP = findViewById(R.id.idEdtOtp);
        Button verifyOTPBtn = findViewById(R.id.idBtnVerify);
        Button generateOTPBtn = findViewById(R.id.idBtnGetOtp);


        generateOTPBtn.setOnClickListener(v -> {

            if (TextUtils.isEmpty(edtPhone.getText().toString())) {

                Toast.makeText(MainActivity.this, "Please enter a valid phone number.", Toast.LENGTH_SHORT).show();
            } else {

                String phone = "+91" + edtPhone.getText().toString();
                sendVerificationCode(phone);
            }
        });


        verifyOTPBtn.setOnClickListener(v -> {

            if (TextUtils.isEmpty(edtOTP.getText().toString())) {

                Toast.makeText(MainActivity.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
            } else {

                verifyCode(edtOTP.getText().toString());
            }
        });
    }

    private void signInWithCredential(PhoneAuthCredential credential) {

        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this::onComplete);
    }


    private void sendVerificationCode(String number) {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                this,
                mCallBack
        );
    }


    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks


            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {


        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            verificationId = s;
        }


        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            final String code = phoneAuthCredential.getSmsCode();


            if (code != null) {

                edtOTP.setText(code);

                verifyCode(code);
            }
        }


        @Override
        public void onVerificationFailed(FirebaseException e) {

            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };


    private void verifyCode(String code) {

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);


        signInWithCredential(credential);
    }

    private void onComplete(Task<AuthResult> task) {
        if (task.isSuccessful()) {

            Intent i = new Intent(MainActivity.this, HomePage.class);
            startActivity(i);
            finish();
        } else {

            Toast.makeText(MainActivity.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}