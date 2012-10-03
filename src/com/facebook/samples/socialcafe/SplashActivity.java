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
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class SplashActivity extends Activity {

	private long splashDelay = 1500;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		TimerTask task = new TimerTask() {
	        @Override
	        public void run() {
	            finish();
              // Check for session. If none exists go to Login activity
              // Otherwise, check for deep linking data. Go to the appropriate
              // order if possible, otherwise go to the menu
	            
	            if (((SocialCafeApplication)getApplication()).facebook.isSessionValid()) {
	            	
    					Uri target = getIntent().getData();
	    				if (target != null) {
	    					Intent orderIntent = new Intent(SplashActivity.this, OrderDrinkActivity.class);
	    					HashMap<String, Drink> drinksMap = ((SocialCafeApplication)SplashActivity.this.getApplication()).drinks;
	    					
	    					Drink deepLinkToDrink = drinksMap.get(target.toString());
	    					if (deepLinkToDrink != null) {
	        					orderIntent.putExtra("DRINK_URL", target.toString());
	        					startActivity(orderIntent);
	    					}
	    				} else {
	    					startActivity(new Intent().setClass(SplashActivity.this, MenuActivity.class));
	    				}
	            } else {
	            		startActivity(new Intent().setClass(SplashActivity.this, LoginActivity.class));
	            }
	        }
        };
        Timer timer = new Timer();
        timer.schedule(task, splashDelay);
	}
}
