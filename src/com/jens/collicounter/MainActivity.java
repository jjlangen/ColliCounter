package com.jens.collicounter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.internal.widget.IcsAdapterView;
import com.actionbarsherlock.internal.widget.IcsAdapterView.OnItemSelectedListener;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class MainActivity extends SherlockActivity implements
		OnItemSelectedListener {

	private EditText user_input;
	private TextView text2;
	private TextView text3;
	private TextView text4;
	private TextView text5;
	private int totalColliCount;
	private int newTicketColli;
	private int finishedColli;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			// Restore value of members from saved state
			totalColliCount = savedInstanceState.getInt("totalColliCount");
			newTicketColli = savedInstanceState.getInt("newTicketColli");
			finishedColli = savedInstanceState.getInt("finishedColli");
			Log.d("DBG", "onRestoreInstanceState was called");
		} else {
			totalColliCount = 0;
			newTicketColli = 0;
			finishedColli = 0;
		}

		setContentView(R.layout.activity_main);
		user_input = (EditText) findViewById(R.id.editText1);
		text2 = (TextView) findViewById(R.id.textView6);
		text3 = (TextView) findViewById(R.id.textView7);
		text4 = (TextView) findViewById(R.id.textView8);
		text5 = (TextView) findViewById(R.id.textView9);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("totalColliCount", totalColliCount);
		outState.putInt("newTicketColli", newTicketColli);
		outState.putInt("finishedColli", finishedColli);
		Log.d("DBG", "onSaveInstanceState was called");
	}

	// NEW
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}

	// NEW
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			Toast.makeText(this, "Action refresh selected", Toast.LENGTH_SHORT)
					.show();
			break;
		case R.id.action_settings:
			Toast.makeText(this, "Action Settings selected", Toast.LENGTH_SHORT)
					.show();
			break;

		default:
			break;
		}

		return true;
	}

	// Hides the keypad
	private void hideKeypad() {
		((InputMethodManager) getSystemService("input_method"))
				.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
	}

	// Check to see if the input is not blank
	private void validateInput(EditText in) {
		if (in.getText().length() == 0) {
			Toast.makeText(this, getString(R.string.toast_no_user_input),
					Toast.LENGTH_LONG).show();
			return;
		}
	}

	// This method is called when the process button is clicked
	public void onProcess(View view) {
		hideKeypad();
		validateInput(user_input);

		int newTicketColli = Integer.parseInt(user_input.getText().toString());
		totalColliCount += newTicketColli;
		int finishedColli = Math.abs(totalColliCount - newTicketColli);
		text2.setText(String.valueOf(newTicketColli)); // Set current ticket
		text3.setText(String.valueOf(finishedColli)); // Set finished colli
		user_input.setText(""); // Empty the input field
		Toast.makeText(this,
				"Nieuwe bon met " + newTicketColli + " colli toegevoegd.",
				Toast.LENGTH_LONG).show();
		// Current date and time
		GregorianCalendar gregorianCalendar1 = new GregorianCalendar();
		// Current date and 7am
		GregorianCalendar gregorianCalendar2 = new GregorianCalendar(
				gregorianCalendar1.get(Calendar.YEAR),
				gregorianCalendar1.get(Calendar.MONTH),
				gregorianCalendar1.get(Calendar.DAY_OF_MONTH), 7, 0, 0);
		// Calculates the time difference between now and 7am
		float f1 = (gregorianCalendar1.getTime().getTime() - gregorianCalendar2
				.getTime().getTime()) / 1000.0F / 60.0F / 60.0F;
		float f2 = 0; // Initialize the resulting variable

		if (f1 <= 2.0F)
			f2 = f1;
		if ((f1 > 2.0F) && (f1 <= 2.25D)) {
			f2 = f1 - (f1 - 2.0F);
		}
		if ((f1 >= 2.25D) && (f1 <= 5.0F)) {
			f2 = f1 - 0.25F;
		}
		if ((f1 >= 5.0F) && (f1 <= 5.5D)) {
			f2 = f1 - (f1 - 5.0F) - 0.25F;
		}
		if ((f1 >= 5.5D) && (f1 <= 7.75D)) {
			f2 = f1 - 0.75F;
		}
		if ((f1 >= 7.75D) && (f1 <= 8.0F)) {
			f2 = f1 - (f1 - 7.75F - 0.75F);
		}
		boolean bool = f1 > 8.0F;
		if (bool)
			f2 = f1 - 1.0F;

		float lol = Float.valueOf(f2);
		text4.setText(String.format("%.1f", lol)); // Print hours worked with
													// only 1 decimal

		float f3 = finishedColli / f2;
		float lol2 = Float.valueOf(f3);
		text5.setText(String.format("%.1f", lol2)); // Print colli per hour with
													// only 1 decimal
	}

	// This method is called when the refresh button is clicked
	public void onRefresh(View view) {
		user_input.setText("0");
		onProcess(view);
	}

	@Override
	public void onItemSelected(IcsAdapterView<?> parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onNothingSelected(IcsAdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

}
