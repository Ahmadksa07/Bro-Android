package com.randomappsinc.bro.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.FontAwesomeIcons;
import com.randomappsinc.bro.Adapters.HomepageTabsAdapter;
import com.randomappsinc.bro.Layouts.SlidingTabLayout;
import com.randomappsinc.bro.Persistence.PreferencesManager;
import com.randomappsinc.bro.R;
import com.randomappsinc.bro.Utils.BroUtils;
import com.randomappsinc.bro.Utils.FormUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends StandardActivity {
    @Bind(R.id.parent) View parent;
    @Bind(R.id.viewpager) ViewPager mViewPager;
    @Bind(R.id.sliding_tabs) SlidingTabLayout slidingTabLayout;

    private HomepageTabsAdapter homepageTabsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        homepageTabsAdapter = new HomepageTabsAdapter(getFragmentManager());
        mViewPager.setAdapter(homepageTabsAdapter);
        slidingTabLayout.setViewPager(mViewPager);

        if (PreferencesManager.get().isFirstTimeUser()) {
            new MaterialDialog.Builder(this)
                    .title(R.string.tutorial)
                    .content(R.string.instructions)
                    .positiveText(android.R.string.yes)
                    .show();
            PreferencesManager.get().rememberShowingTutorial();
        }

        if (PreferencesManager.get().shouldAskForRating()) {
            new MaterialDialog.Builder(this)
                    .content(R.string.please_rate)
                    .positiveText(R.string.will_rate)
                    .negativeText(R.string.decline_rating)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            Uri uri =  Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            if ((getPackageManager().queryIntentActivities(intent, 0).size() > 0)) {
                                startActivity(intent);
                            }
                        }
                    })
                    .show();
        }
    }

    public HomepageTabsAdapter getHomepageTabsAdapter() {
        return homepageTabsAdapter;
    }

    public void showSnackbar(String message) {
        FormUtils.showSnackbar(parent, message);
    }

    public void showChooseMessageDialog() {
        List<String> optionsList = BroUtils.getMessageOptions();
        CharSequence[] options = optionsList.toArray(new CharSequence[optionsList.size()]);

        new MaterialDialog.Builder(this)
                .title(R.string.message_choices)
                .items(options)
                .itemsCallbackSingleChoice(BroUtils.getCurrentMessageIndex(),
                        new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                PreferencesManager.get().setMessage(text.toString());
                                String confirmMessage = "You will now text \"" + text.toString() + "\" in your messages.";
                                showSnackbar(confirmMessage);
                                return true;
                            }
                        })
                .positiveText(R.string.choose)
                .negativeText(android.R.string.no)
                .show();
    }

    public void showChooseConfirmDialog() {
        int currentConfirmIndex = PreferencesManager.get().getShouldConfirm() ? 0 : 1;
        new MaterialDialog.Builder(this)
                .title(R.string.confirm_message_question)
                .items(R.array.yes_no)
                .itemsCallbackSingleChoice(currentConfirmIndex,
                        new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                switch(which) {
                                    case 0:
                                        PreferencesManager.get().setShouldConfirm(true);
                                        showSnackbar(getString(R.string.now_confirming));
                                        break;
                                    case 1:
                                        PreferencesManager.get().setShouldConfirm(false);
                                        showSnackbar(getString(R.string.not_confirming));
                                }
                                return true;
                            }
                        })
                .positiveText(R.string.choose)
                .negativeText(android.R.string.no)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.choose_message).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_wechat)
                        .colorRes(R.color.white)
                        .actionBarSize());
        menu.findItem(R.id.confirm_messages).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_hand_stop_o)
                        .colorRes(R.color.white)
                        .actionBarSize());
        menu.findItem(R.id.settings).setIcon(
                new IconDrawable(this, FontAwesomeIcons.fa_gear)
                        .colorRes(R.color.white)
                        .actionBarSize());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.choose_message:
                showChooseMessageDialog();
                return true;
            case R.id.confirm_messages:
                showChooseConfirmDialog();
                return true;
            case R.id.settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
