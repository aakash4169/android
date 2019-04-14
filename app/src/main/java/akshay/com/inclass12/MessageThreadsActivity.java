package akshay.com.inclass12;


/*
Assignment : InClass12
Name:  Aakash Pradeep Kulkarni
FileName: MessageThreadsActivity.java
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.Date;

public class MessageThreadsActivity extends AppCompatActivity implements ThreadAdapter.MyData {
    public static final String THREAD_ID_LIST = "thread_id";
    TextView tvDisplayName, tvCurrentThreads;
    ImageButton ibLogOut,ibAddThread;
    ListView listView;
    EditText etAddThread;
    private FirebaseUser user;
    private DatabaseReference mDataRef;
    ArrayList<Threads> threadList;
    ThreadAdapter adapter;
    private static final String TAG="demo";
    public static final String THREAD_ID="thread_id";

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.MessageThreadsActivityHeader);
        setContentView(R.layout.activity_message_threads);

        tvDisplayName=(TextView)findViewById(R.id.tvDisplayName);
        tvCurrentThreads=(TextView)findViewById(R.id.tvCurrentThreads);
        listView=(ListView)findViewById(R.id.listView);
        ibLogOut=(ImageButton)findViewById(R.id.ibLogOut);
        etAddThread=(EditText)findViewById(R.id.etAddThread);
        ibAddThread=(ImageButton)findViewById(R.id.ibAddThread);


        mDataRef= FirebaseDatabase.getInstance().getReference();;
        user= FirebaseAuth.getInstance().getCurrentUser();
        Log.d(TAG, "onCreate: DisplayName"+user.getDisplayName());
        if (getIntent().getExtras()!=null)
        {
            String text=getIntent().getStringExtra(SignUpActivity.DISPLAY_NAME_KEY);
            tvDisplayName.setText(text);
        }
        else
            tvDisplayName.setText(user.getDisplayName());

        threadList = new ArrayList<>();
        setAdapter();
        mDataRef.child("threads").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                threadList.clear();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    Threads threads =  dsp.getValue(Threads.class);
                    threads.setId(dsp.getKey());
                    threadList.add(threads);
                }
                refreshAdapter();
                Log.d(TAG, "onDataChange: ThreadList"+threadList.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MessageThreadsActivity.this,
                        "Failed to retrieve threads",Toast.LENGTH_SHORT).show();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(MessageThreadsActivity.this,ChatroomActivity.class);
//                intent.putExtra(THREAD_ID,threadIDList.get(i));
                intent.putExtra(THREAD_ID, threadList.get(i).getId());
                startActivity(intent);
                finish();
            }
        });


        ibLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(MessageThreadsActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        ibAddThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text=etAddThread.getText().toString();
                if (text.length()>0) {
                    DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd hh:mm:ss z yyyy");
                    Date date = new Date();
                    mDataRef.child("threads").push()
                            .setValue(new Threads(user.getDisplayName(), user.getUid(), text, dateFormat.format(date)));
                    etAddThread.setText("");
                }
                else
                    Toast.makeText(MessageThreadsActivity.this,"Thread title cannot be blank",Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void setAdapter()
    {
        adapter=new ThreadAdapter(MessageThreadsActivity.this,R.layout.thread_item,threadList);
        listView.setAdapter(adapter);
    }
    public void refreshAdapter()
    {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void deleteClicked(final String id) {
        mDataRef.child("threads").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataSnapshot.child(id).getRef().removeValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MessageThreadsActivity.this,"Error deleting thread",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
