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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;

import static se.codemill.codeplay.R.id.listItemID;
import static se.codemill.codeplay.R.id.videoView;
import static se.codemill.codeplay.R.layout.convertview;
import static se.codemill.codeplay.R.layout.listitem;
import static se.codemill.codeplay.R.layout.videoinfo;


public class Videolistadapter extends ArrayAdapter<JSONObject> {
    JSONArray mVideoItemData;
    LayoutInflater inflater;

    final int VIDEO_BAR_VIEW = 0;
    final int LIST_ITEM_VIEW = 1;

    public Videolistadapter(Context context, int resource, JSONArray videoItemData) {
        super(context,resource);
        mVideoItemData = videoItemData;
        inflater = (LayoutInflater)context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mVideoItemData.length();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {



        if (convertView == null) {
            convertView = inflater.inflate(listitem, null);
            convertView.setTag("LISTITEM");
        }

        if (convertView.getTag() == "LISTITEM") {
            try {
                URL url = new URL("http://10.12.10.132:8000/bilder/" + mVideoItemData.getJSONObject(position).getString("Image"));
                new DownloadThumbnailTask(convertView, mVideoItemData.getJSONObject(position).getString("VideoID")).execute(url);

                ((TextView) convertView.findViewById(listItemID)).setText(mVideoItemData.getJSONObject(position).getString("VideoID"));
                ((TextView) convertView.findViewById(R.id.listTime)).setText(mVideoItemData.getJSONObject(position).getString("Time"));
                ((TextView) convertView.findViewById(R.id.listCreator)).setText(mVideoItemData.getJSONObject(position).getString("Creator"));
                //((TextView) convertView.findViewById(R.id.listDate)).setText(mVideoItemData.getJSONObject(position).getString("Date"));
                ((TextView) convertView.findViewById(R.id.listTitle)).setText(mVideoItemData.getJSONObject(position).getString("Title"));
                ((TextView) convertView.findViewById(R.id.listDescription)).setText(mVideoItemData.getJSONObject(position).getString("Description"));
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
            result = mVideoItemData.getJSONObject(position);
        } catch (Exception e) {
            System.out.println("Failed to get object");
        }
        return result;
    }


    private class DownloadThumbnailTask extends AsyncTask<URL, Integer, Bitmap> {
        WeakReference<View> mViewRef;
        String mVideoID;
        public DownloadThumbnailTask(View view, String videoID) {
            mViewRef = new WeakReference<View>(view);
            mVideoID = videoID;
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

            View view = mViewRef.get();

            if (view != null) {
                // jämför videoID med id i mViewRef
                String currentID = ((TextView) view.findViewById(listItemID)).getText().toString();

                if (currentID.equals(mVideoID)) {
                    // om samma spara i view
                    ImageView imageView = ((ImageView) view.findViewById(R.id.imageView));
                    imageAnimation(getContext(), imageView, bitmap);
                    //imageView.setImageBitmap(bitmap);
                }
            }
        }
    }

    private static void imageAnimation(Context c, final ImageView v, final Bitmap new_image) {
        final Animation anim_out = AnimationUtils.loadAnimation(c, android.R.anim.fade_out);
        final Animation anim_in  = AnimationUtils.loadAnimation(c, android.R.anim.fade_in);
        anim_out.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation)
            {
                v.setImageBitmap(new_image);
                anim_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override public void onAnimationStart(Animation animation) {}
                    @Override public void onAnimationRepeat(Animation animation) {}
                    @Override public void onAnimationEnd(Animation animation) {}
                });
                v.startAnimation(anim_in);
            }
        });
        v.startAnimation(anim_out);
    }


    @Override
    public int getItemViewType(int position) {
            return LIST_ITEM_VIEW;
    }
}
