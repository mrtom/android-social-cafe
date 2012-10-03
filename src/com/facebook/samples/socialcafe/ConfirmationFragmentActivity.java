/*
 * Copyright 2012 Facebook, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook.samples.socialcafe;

import java.util.HashMap;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class ConfirmationFragmentActivity extends Fragment {

	private String url;
	private static final int ENJOY_STATE=1;
	protected Handler handler;
	
	HashMap<String, Drink> drinks;
	
	@TargetApi(11)
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);
		handler = new Handler();
		drinks = ((SocialCafeApplication)getActivity().getApplication()).drinks;
		url = getActivity().getIntent().getStringExtra("DRINK_URL");
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.order_confirmation, parent, false);
		getActivity().setTitle(R.string.confirmation);
		
		Drink drink = drinks.get(url);
		
		TextView drinkTitle = (TextView)v.findViewById(R.id.drink_confirmation_title);
		drinkTitle.setText(drink.getTitle());

		TextView drinkInfo = (TextView)v.findViewById(R.id.drink_confirmation_info);
		drinkInfo.setText(drink.getInfo());
		
		ImageView drinkImage = (ImageView)v.findViewById(R.id.drink_confirmation_image);
		drinkImage.setImageResource(drink.getImageID());
		
		TextView ogStory = (TextView)v.findViewById(R.id.og_message);
		ogStory.setText(getString(R.string.og_message, 
				((SocialCafeApplication)getActivity().getApplication()).userName, drink.getTitle()));
		
		ImageButton enjoyButton = (ImageButton)v.findViewById(R.id.enjoy_button);
		enjoyButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle params = new Bundle();
		   		params.putString("object", drinks.get(url).getURL());
				((SocialCafeApplication)getActivity().getApplication()).asyncRunner.request("me/og.likes", params, 
						"POST", new EnjoyedDrinkPostListener(), ENJOY_STATE);
				
			}
		});
		
		ImageButton menuButton = (ImageButton)v.findViewById(R.id.return_to_menu_button);
		menuButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), MenuActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});
		return v;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case android.R.id.home:
				Intent intent = new Intent(getActivity(), MenuActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
    public class EnjoyedDrinkPostListener extends BaseRequestListener {

        public void onComplete(final String response, final Object state) {
        	handler.post(new Runnable() {
                public void run() {
                	new AlertDialog.Builder(getActivity())
        	        .setMessage(getString(R.string.drink_enjoyed, drinks.get(url).getTitle()))
        	        .setPositiveButton(R.string.ok, null)
        	        .show();
                }
            });
			
        }
    }
}
