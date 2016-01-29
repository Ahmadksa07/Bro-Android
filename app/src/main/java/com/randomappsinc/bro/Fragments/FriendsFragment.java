package com.randomappsinc.bro.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.randomappsinc.bro.Activities.MainActivity;
import com.randomappsinc.bro.Adapters.FriendsAdapter;
import com.randomappsinc.bro.Models.Friend;
import com.randomappsinc.bro.Models.Record;
import com.randomappsinc.bro.Persistence.PreferencesManager;
import com.randomappsinc.bro.R;
import com.randomappsinc.bro.Utils.BroUtils;
import com.randomappsinc.bro.Utils.FormUtils;
import com.rey.material.widget.CheckBox;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;
import butterknife.OnTextChanged;

/**
 * Created by alexanderchiou on 8/18/15.
 */
public class FriendsFragment extends Fragment {
    @Bind(R.id.link_spam_checkbox) CheckBox sendInviteCheckbox;
    @Bind(R.id.friends_list) ListView friendsList;
    @Bind(R.id.friend_input) EditText friendInput;

    private FriendsAdapter friendsAdapter;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.friends, container, false);
        ButterKnife.bind(this, rootView);
        sendInviteCheckbox.setCheckedImmediately(true);
        context = getActivity();

        friendsAdapter = new FriendsAdapter(getActivity());
        friendsList.setAdapter(friendsAdapter);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnTextChanged(value = R.id.friend_input, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged (Editable s)
    {
        friendsAdapter.updateWithPrefix(s.toString());
    }

    @OnItemClick(R.id.friends_list)
    public void onItemClick(int position) {
        FormUtils.hideKeyboard(getActivity());

        final Friend friend = friendsAdapter.getItem(position);
        String message = PreferencesManager.get().getMessage();
        String confirmationMessage = "Do you want to text \"" + message + "\" to " + friend.getName() + "?";
        if (PreferencesManager.get().getShouldConfirm()) {
            new MaterialDialog.Builder(getActivity())
                    .title(R.string.confirm_message)
                    .content(confirmationMessage)
                    .positiveText(android.R.string.yes)
                    .negativeText(android.R.string.no)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            sendBro(friend);
                        }
                    })
                    .show();
        }
        else {
            sendBro(friend);
        }
    }

    private void sendBro(Friend friend) {
        String message = PreferencesManager.get().getMessage();
        int recordId = PreferencesManager.get().getHighestRecordId() + 1;
        Record record = new Record(recordId, friend.getPhoneNumber(), friend.getName(), message);
        String statusMessage = BroUtils.processBro(context, record, sendInviteCheckbox.isChecked());
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.showSnackbar(statusMessage);
    }

    @OnClick(R.id.clear_input)
    public void onClearInputClick() {
        friendInput.setText("");
    }
}
