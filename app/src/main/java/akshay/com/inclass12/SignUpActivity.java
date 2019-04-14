package akshay.com.inclass12;

/*
Assignment : InClass12
Name:  Aakash Pradeep Kulkarni
FileName: SignUpActivity.java
 */

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static akshay.com.inclass12.MainActivity.isValidEmail;

public class SignUpActivity extends AppCompatActivity {

    EditText etFirstName, etLastName, etEmail, etPassword, etRepeatPassword;
    Button btnCancel, btnSignUp;
    public  FirebaseAuth mAuth;
    private static final String TAG="demo";
    public static final String DISPLAY_NAME_KEY="displayName";
    private DatabaseReference mDataRef;
    private FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.SignUpActivityHeader);
        setContentView(R.layout.activity_sign_up);

        mAuth=FirebaseAuth.getInstance();

        etFirstName=(EditText)findViewById(R.id.etFirstName);
        etLastName=(EditText)findViewById(R.id.etLastName);
        etEmail=(EditText)findViewById(R.id.etEmail);
        etPassword=(EditText)findViewById(R.id.etPassword);
        etRepeatPassword=(EditText)findViewById(R.id.etRepeatPassword);

        btnCancel=(Button)findViewById(R.id.btnCancel);
        btnSignUp=(Button)findViewById(R.id.btnSignUp);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SignUpActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int flag=0;
                try {

                    final String firstName = etFirstName.getText().toString();
                    int i=firstName.charAt(0);
                    if (!(i>=97&&i<=122)||(i>=65&&i<=90))
                        Toast.makeText(SignUpActivity.this,"FirstName should start with an alphabet",Toast.LENGTH_SHORT).show();
                    final String lastName = etLastName.getText().toString();
                    int j=lastName.charAt(0);
                    if (!(j>=97&&j<=122)||(j>=65&&j<=90))
                        Toast.makeText(SignUpActivity.this,"LastName should start with an alphabet",Toast.LENGTH_SHORT).show();
                    final String email = etEmail.getText().toString();
                    if (!isValidEmail(email)) {
                        flag = 1;
                        Toast.makeText(SignUpActivity.this,"Incorrect Email",Toast.LENGTH_SHORT).show();
                    }
                    final String password = etPassword.getText().toString();
                    final String repeatPassword = etRepeatPassword.getText().toString();
                    if (firstName.equals("")||lastName.equals("")||email.equals("")||password.equals("")) {
                        flag = 1;
                        Toast.makeText(SignUpActivity.this,"Text fields cannot be blank",Toast.LENGTH_SHORT).show();
                    }
                    if (!password.equals(repeatPassword))
                    {
                        flag=1;
                        Toast.makeText(SignUpActivity.this, "Passwords dont match", Toast.LENGTH_SHORT).show();
                    }
                    if (flag==0){
                        if (password.length()<=6)
                            Toast.makeText(SignUpActivity.this,"Password should be more than 6 characters",Toast.LENGTH_SHORT).show();
                        else
                            performSignUp(firstName,lastName,email,password);
                    }
                }
                catch (NullPointerException e)
                {
                    flag=1;
                    Toast.makeText(SignUpActivity.this,"Text fields cannot be blank",Toast.LENGTH_SHORT).show();
                }
                catch (NumberFormatException e)
                {
                    Toast.makeText(SignUpActivity.this,"Enter valid text",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
    public void performSignUp(final String firstName,final String lastName,final String email, final String password)
    {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            createProfile(mAuth.getUid(),firstName, lastName);
                            //   updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(SignUpActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void createProfile(String uid, String firstName, String lastName)
    {
        user = FirebaseAuth.getInstance().getCurrentUser();
        UserProfileChangeRequest profileChangeRequest=new UserProfileChangeRequest.Builder()
                .setDisplayName(firstName+" "+lastName)
                .build();
        mDataRef=FirebaseDatabase.getInstance().getReference();

        user.updateProfile(profileChangeRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(SignUpActivity.this, "Hello "+user.getDisplayName(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        Intent intent=new Intent(SignUpActivity.this,MessageThreadsActivity.class);
        intent.putExtra(DISPLAY_NAME_KEY,firstName +" "+ lastName);
        startActivity(intent);
        finish();


    }
}
