package akshay.com.inclass12;

/*
Assignment : InClass12
Name:  Aakash Pradeep Kulkarni
FileName: ChatroomActivity.java
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ChatroomActivity extends AppCompatActivity implements MessageAdapter.IData{

    private static final String TAG = "demo";
    TextView tvThreadTitle;
    ImageButton ibGoHome, ibSend;
    ListView listViewMessages;
    EditText etNewMessage;
    ArrayList<Message> messageList;
    DatabaseReference mChildRef;
    FirebaseUser user;
    String threadID;
    MessageAdapter adapter;

    @Override
    public void onBackPressed() {
        Intent intent=new Intent(ChatroomActivity.this,MessageThreadsActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.ChatroomActivityHeader);
        setContentView(R.layout.activity_chatroom);

        tvThreadTitle=(TextView)findViewById(R.id.tvThreadTitle);
        ibGoHome=(ImageButton)findViewById(R.id.ibGoHome);
        ibSend=(ImageButton)findViewById(R.id.ibSend);
        listViewMessages=(ListView)findViewById(R.id.listViewMessages);
        etNewMessage=(EditText)findViewById(R.id.etNewMessage);

        messageList=new ArrayList<>();
        setAdapter();

        if (getIntent().getExtras().containsKey(MessageThreadsActivity.THREAD_ID)) {
            threadID=getIntent().getStringExtra(MessageThreadsActivity.THREAD_ID);
        }
        mChildRef= FirebaseDatabase.getInstance().getReference().child("threads").child(threadID);;
        user= FirebaseAuth.getInstance().getCurrentUser();
        mChildRef.child("title").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String title=dataSnapshot.getValue(String.class);
                tvThreadTitle.setText(title);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mChildRef.child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                messageList.clear();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    Message message =  dsp.getValue(Message.class);
                    message.setId(dsp.getKey());
                    messageList.add(message);
                }
                refreshAdapter();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ibSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text=etNewMessage.getText().toString();
                if (text.length()>0) {
                    DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy");
                    Calendar cal = Calendar.getInstance();
                    mChildRef.child("messages").push().setValue(new Message(user.getDisplayName(),
                            user.getUid(), text, dateFormat.format(cal.getTime())));
                    etNewMessage.setText("");
                }
                else
                    Toast.makeText(ChatroomActivity.this,"Message cannot be blank",Toast.LENGTH_SHORT).show();
            }
        });

        ibGoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ChatroomActivity.this,MessageThreadsActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
    public void setAdapter()
    {
        adapter = new MessageAdapter(ChatroomActivity.this, R.layout.message_item, messageList);
        listViewMessages.setAdapter(adapter);
    }
    public void refreshAdapter()
    {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void clickedDelete(final String message_id) {
        mChildRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: Delete Clicked");
                dataSnapshot.child("messages").child(message_id).getRef().removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChatroomActivity.this,"Error deleting message",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
