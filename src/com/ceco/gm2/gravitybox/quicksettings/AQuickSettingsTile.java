/*
 * Copyright (C) 2013 Peter Gregus for GravityBox Project (C3C076@xda)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ceco.gm2.gravitybox.quicksettings;

import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedHelpers;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public abstract class AQuickSettingsTile implements OnClickListener {
    protected static final String PACKAGE_NAME = "com.android.systemui";

    protected Context mContext;
    protected Context mGbContext;
    protected FrameLayout mTile;
    protected OnClickListener mOnClick;
    protected OnLongClickListener mOnLongClick;
    protected Resources mResources;
    protected Resources mGbResources;
    protected int mDrawableId;
    protected String mLabel;
    protected Object mStatusBar;
    protected Object mPanelBar;

    public AQuickSettingsTile(Context context, Context gbContext, Object statusBar, Object panelBar) {
        mContext = context;
        mGbContext = gbContext;
        mResources = mContext.getResources();
        mGbResources = mGbContext.getResources();
        mStatusBar = statusBar;
        mPanelBar = panelBar;
    }

    public void setupQuickSettingsTile(ViewGroup viewGroup, LayoutInflater inflater, XSharedPreferences prefs) {
        int layoutId = mResources.getIdentifier("quick_settings_tile", "layout", PACKAGE_NAME);
        mTile = (FrameLayout) inflater.inflate(layoutId, viewGroup, false);
        onTileCreate();
        viewGroup.addView(mTile);
        if (prefs != null) {
            onPreferenceInitialize(prefs);
        }
        updateResources();
        mTile.setOnClickListener(mOnClick);
        mTile.setOnLongClickListener(mOnLongClick);
        onTilePostCreate();
    }

    public void setupQuickSettingsTile(ViewGroup viewGroup, LayoutInflater inflater) {
        setupQuickSettingsTile(viewGroup, inflater, null);
    }

    protected abstract void onTileCreate();

    protected void onTilePostCreate() { };

    protected abstract void updateTile();

    protected void onPreferenceInitialize(XSharedPreferences prefs) { };

    public void updateResources() {
        if (mTile != null) {
            updateTile();
        }
    }

    @Override
    public final void onClick(View v) {
        mOnClick.onClick(v);
    }

    protected void startActivity(String action){
        Intent intent = new Intent(action);
        startActivity(intent);
    }

    protected void startActivity(Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
        collapsePanels();
    }

    protected void collapsePanels() {
        XposedHelpers.callMethod(mStatusBar, "animateCollapsePanels");
    }
}