package com.the.fastorder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SetingsActivity extends AppCompatActivity {


    private ImageButton backBtn;
    private TextView delete_account, logoutBtn;


    private FirebaseAuth firebaseAuth;
    AlertDialog.Builder dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setings);


        backBtn = findViewById(R.id.backBtn);
        logoutBtn = findViewById(R.id.logoutBtn);
        delete_account = findViewById(R.id.delete_account);

        firebaseAuth = FirebaseAuth.getInstance();


        backBtn.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {
                                           onBackPressed();
                                       }
                                   }
        );


        dialog = new AlertDialog.Builder(this);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setTitle("Logout")
                        .setCancelable(true)
                        .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                firebaseAuth.signOut();
                                checkUser();
                            }
                        }).show();

            }
        });

        delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setTitle("Delete account")
                        .setCancelable(true)
                        .setPositiveButton("Delete account", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteCurrentUser();

                            }
                        }).show();
            }
        });
    }


    private void deleteCurrentUser() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .setValue(null)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        FirebaseAuth.getInstance().getCurrentUser().delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Intent intent = new Intent(SetingsActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                        } else {
                                            Toast.makeText(SetingsActivity.this, "Deleate account failed", Toast.LENGTH_SHORT);

                                        }
                                    }
                                });
                    }
                });
    }

    ;


    private void checkUser() {


        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user == null) {

            startActivity(new Intent(SetingsActivity.this, LoginActivity.class));
            finish();
        } else {
            Toast.makeText(SetingsActivity.this, "logout", Toast.LENGTH_SHORT);
        }
    }


}
