package com.runningmusic.newfrag;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.runningmusic.oauth.SucceedAndFailedHandler;
import com.runningmusic.oauth.ThirdLoginOAuth;
import com.runningmusic.runninspire.R;
import com.runningmusic.utils.Log;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    private static String TAG = LoginFragment.class.getName();

    private AQuery aQuery;
    private Activity context;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        context = this.getActivity();
        aQuery = new AQuery(view);
        aQuery.id(R.id.login_logo).clickable(true).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e(TAG, "login_button " + "onClick");
                ThirdLoginOAuth.getInstance().loginWechat(context, new SucceedAndFailedHandler() {
                    @Override
                    public void onSuccess(Object obj) {

                    }

                    @Override
                    public void onFailure(int errorCode) {

                    }
                });
            }
        });

        return view;
    }

}
