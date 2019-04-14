package akshay.com.inclass12;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MessageAdapter extends ArrayAdapter<Message> {
    IData iData;
    public static final String TAG="demo";

    public MessageAdapter(@NonNull Context context, int resource, @NonNull List<Message> objects) {
        super(context, resource, objects);
        iData= (IData) context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Message message = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.message_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvMessage = (TextView) convertView.findViewById(R.id.tvMessage);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
            viewHolder.tvPostTime = (TextView) convertView.findViewById(R.id.tvPostTime);
            viewHolder.ibDelete = (ImageButton) convertView.findViewById(R.id.ibDelete);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvMessage.setText(message.getMessage());
        viewHolder.tvUserName.setText(message.getDisplayName());
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        if (user.getUid().equals(message.getUser_id()))
            viewHolder.ibDelete.setVisibility(View.VISIBLE);
        else
            viewHolder.ibDelete.setVisibility(View.INVISIBLE);

        PrettyTime p = new PrettyTime();

        viewHolder.tvPostTime.setText(p.format(new Date(message.getCreated_at())));

        viewHolder.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: Delete Clicked");
                //      Log.d(TAG, "onClick: Clicked Delete");
                iData.clickedDelete(message.getId());
            }
        });


        return convertView;
    }
    public static class ViewHolder
    {
        TextView tvMessage, tvUserName, tvPostTime;
        ImageButton ibDelete;
    }
    public static interface IData
    {
        public void clickedDelete(String message_id);
    }
}

