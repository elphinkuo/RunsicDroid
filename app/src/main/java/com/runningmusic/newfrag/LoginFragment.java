package com.runningmusic.newfrag;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.runningmusic.event.UserInfoEvent;
import com.runningmusic.network.http.RunsicRestClientUsage;
import com.runningmusic.oauth.SucceedAndFailedHandler;
import com.runningmusic.oauth.ThirdLoginOAuth;
import com.runningmusic.runninspire.R;
import com.runningmusic.utils.Constants;
import com.runningmusic.utils.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    private static String TAG = LoginFragment.class.getName();

    private AQuery aQuery;
    private Activity context;
    private Fragment fragmentContext;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);
        fragmentContext = this;
        context = this.getActivity();
        aQuery = new AQuery(view);
        aQuery.id(R.id.login_button).clickable(true).clicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.e(TAG, "login_button " + "onClick");
                ThirdLoginOAuth.getInstance().loginWechat(context, new SucceedAndFailedHandler() {
                    @Override
                    public void onSuccess(Object obj) {
                        Log.e(TAG, "onSuccess");
                        Bundle value = (Bundle) obj;
                        Log.e(TAG, ""+ value);
                        Constants.access_token = value.getString("access_token");

                        ThirdLoginOAuth.getInstance().getWechatUserInfo(context, new SucceedAndFailedHandler() {
                            @Override
                            public void onSuccess(Object obj) {
                                Log.e(TAG, "getUserInfo is " + obj);
                                HashMap userInfo = (HashMap) obj;
                                Log.e(TAG, "getUserInfo is " + userInfo);


//                                String unionid = userInfo.get("unionid").toString();
//                                String country = userInfo.get("country").toString();
//                                String nickName = userInfo.get("nickname").toString();
//                                String city = userInfo.get("province").toString();
//                                String language = userInfo.get("language").toString();
//                                String headimageURL = userInfo.get("headimageurl").toString();
//                                String sex = userInfo.get("sex").toString();
//                                String openid = userInfo.get("openid").toString();

                                Bundle bundle = new Bundle();
                                bundle.putString("nickname", userInfo.get("nickname").toString());
                                bundle.putString("headimgurl", userInfo.get("headimgurl").toString());
                                bundle.putString("city", userInfo.get("province").toString() + userInfo.get("city").toString());


                                RunsicRestClientUsage.getInstance().createUser(obj);


                                FragmentManager fragmentManager = fragmentContext.getActivity().getSupportFragmentManager();
                                PersonPageFrag personPageFrag = new PersonPageFrag();
                                personPageFrag.setArguments(bundle);
                                fragmentManager.beginTransaction().replace(R.id.content_frame, personPageFrag).commit();
//                                EventBus.getDefault().post(new UserInfoEvent(obj));

                            }

                            @Override
                            public void onFailure(int errorCode) {

                            }
                        });

                    }

                    @Override
                    public void onFailure(int errorCode) {
                        Log.e(TAG, "onFailure");
                    }
                });
            }
        });

        return view;
    }

}
