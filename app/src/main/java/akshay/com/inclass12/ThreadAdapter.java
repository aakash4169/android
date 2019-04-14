package akshay.com.inclass12;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class ThreadAdapter extends ArrayAdapter<Threads> {
    MyData myData;
    public ThreadAdapter(@NonNull Context context, int resource, @NonNull List<Threads> objects) {
        super(context, resource, objects);
        myData=(MyData) context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Threads threads=getItem(position);
        ViewHolder viewHolder;
        if(convertView==null)
        {
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.thread_item,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.tvThreadName=(TextView)convertView.findViewById(R.id.tvThreadName);
            viewHolder.ibDeleteThread=(ImageButton) convertView.findViewById(R.id.ibDeleteThread);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder=(ViewHolder)convertView.getTag();
        }

        viewHolder.tvThreadName.setText(threads.getTitle());

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();


        if(user.getUid().equals(threads.getUser_id()))
            viewHolder.ibDeleteThread.setVisibility(View.VISIBLE);
        else
            viewHolder.ibDeleteThread.setVisibility(View.INVISIBLE);


        viewHolder.ibDeleteThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myData.deleteClicked(threads.getId());
            }
        });

        return convertView;
    }

    public static class ViewHolder
    {
        TextView tvThreadName;
        ImageButton ibDeleteThread;
    }
    public static interface MyData
    {
        public void deleteClicked(String id);
    }
}
