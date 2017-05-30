package se.codemill.codeplay;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import static se.codemill.codeplay.R.id.userTitle;
import static se.codemill.codeplay.R.id.userListID;
import static se.codemill.codeplay.R.layout.convertview;
import static se.codemill.codeplay.R.layout.listusers;

public class Userlistadapter extends ArrayAdapter<JSONObject>{
    JSONArray mUserItemData;
    LayoutInflater inflater;

    //final int VIDEO_BAR_VIEW = 0;
    final int LIST_USERS_VIEW = 0;

    public Userlistadapter(Context context, int resource, JSONArray videoItemData) {
        super(context,resource);
        mUserItemData = videoItemData;
        inflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mUserItemData.length();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        System.out.println("#### getView pos= " + position);

        if (convertView == null) {
            convertView = inflater.inflate(listusers, null);
            convertView.setTag("LISTUSERS");
        }

        if (convertView.getTag() == "LISTUSERS") {
            try {
                URL url = new URL("http://10.12.10.132:8000/bilder/" + mUserItemData.getJSONObject(position).getString("ProfileImage"));
                ((TextView) convertView.findViewById(userTitle)).setText(mUserItemData.getJSONObject(position).getString("UserName"));
                ((TextView) convertView.findViewById(R.id.userDescription)).setText(mUserItemData.getJSONObject(position).getString("Description"));
            } catch (Exception e) {
                System.out.println("######## ERROR" + e);
            }
            return convertView;
        }
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Nullable
    @Override
    public JSONObject getItem(int position) {
        System.out.println("######### getItem pos= " + position);
        JSONObject result = null;
        try {
            result = mUserItemData.getJSONObject(position);
        } catch (Exception e) {
            System.out.println("Failed to get object");
        }
        return result;
    }

    private class DownloadThumbnailTask extends AsyncTask<URL, Integer, Bitmap> {
        WeakReference<View> mUserViewRef;
        String mUserID;
        public DownloadThumbnailTask(View view, String userID) {
            mUserViewRef = new WeakReference<View>(view);
            mUserID = userID;
        }

        @Override
        protected Bitmap doInBackground(URL... params) {
            URL url = params[0];

            try {
                return BitmapFactory.decodeStream(url.openStream());
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            View view = mUserViewRef.get();

            if (view != null) {
                // jämför videoID med id i mViewRef

                String currentID = ((TextView) view.findViewById(userListID)).getText().toString();

                if (currentID.equals(mUserID)) {

                    // om samma spara i view
                    ((ImageView) view.findViewById(R.id.profileImage)).setImageBitmap(bitmap);
                }
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        return LIST_USERS_VIEW;
    }
}

