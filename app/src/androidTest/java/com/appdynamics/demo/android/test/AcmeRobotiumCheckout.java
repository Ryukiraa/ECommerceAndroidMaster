package com.appdynamics.demo.android.test;

import com.appdynamics.demo.android.EntryActivity;
import com.appdynamics.demo.android.ItemDetailActivity;
import com.appdynamics.demo.android.ItemListActivity;
import com.appdynamics.demo.android.LoginActivity;
import com.appdynamics.demo.android.ECommerce.R;

import com.robotium.solo.*;
import android.test.ActivityInstrumentationTestCase2;
import android.util.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class AcmeRobotiumCheckout extends ActivityInstrumentationTestCase2<EntryActivity> {
  	private Solo solo;
    private List<String> users = new ArrayList<>();
    private List<String> passwords = new ArrayList<>();

  	public AcmeRobotiumCheckout() {
		super(EntryActivity.class);
  	}

  	public void setUp() throws Exception {
        super.setUp();
		solo = new Solo(getInstrumentation());
		getActivity();
  	}
  
   	@Override
   	public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        super.tearDown();
  	}

    private List readJsonStream(InputStream in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            return readJsonArray(reader);
        } finally {
                reader.close();
        }
    }

    private List readJsonArray(JsonReader reader) throws IOException {
        List messages = new ArrayList();

        reader.beginArray();
        while (reader.hasNext()) {
            readJson(reader);
        }
        reader.endArray();
        return messages;
    }

    private void readJson(JsonReader reader) throws IOException {
        reader.beginObject();
        int index = 0;
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("email")) {
                users.add(reader.nextString());
            } else if (name.equals("password")) {
                passwords.add(reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    public void testRun() throws IOException {
        InputStream userFile = getInstrumentation().getContext().getResources().getAssets().open("users.json");
        readJsonStream(userFile);

        doLogin();
        assertTrue("ItemListActivity not found", solo.waitForActivity(ItemListActivity.class));

        int noOfBooks = randInt(1,8);
        ArrayList<Integer> shuffled = shuffleItems(0,noOfBooks);
        for (int i = 0; i < noOfBooks; i++) {
            doAddBook(shuffled.get(i));
        }
        doCheckout();

        doLogout();
    }

    private ArrayList<Integer> shuffleItems(int min, int max) {
        ArrayList<Integer> elements = new ArrayList<Integer>();
        for (int i = min; i < max; i++) {
            elements.add(i, i);
        }
        Collections.shuffle(elements);
        return elements;
    }

    private void doLogin () {
        int theUser = randInt(0, users.size() - 1);

        solo.waitForActivity(EntryActivity.class, 2000);
        Timeout.setSmallTimeout(101011);
        solo.clearEditText((android.widget.EditText) solo.getView(R.id.username));
        solo.enterText((android.widget.EditText) solo.getView(R.id.username), users.get(theUser));

        solo.clickOnView(solo.getView(R.id.password));
        solo.clearEditText((android.widget.EditText) solo.getView(R.id.password));
        solo.enterText((android.widget.EditText) solo.getView(R.id.password), passwords.get(theUser));

        solo.clickOnView(solo.getView(R.id.sign_in_button));
    }

    private void doCheckout() {
        solo.clickOnText(java.util.regex.Pattern.quote("Cart"));
        solo.clickOnView(solo.getView(R.id.checkout_button));
        solo.clickOnText(java.util.regex.Pattern.quote("List"));
    }

    private void doAddBook (int book) {
        solo.clickInList(book, 0);
        assertTrue("ItemDetailActivity not found", solo.waitForActivity(ItemDetailActivity.class));
        solo.clickOnView(solo.getView(R.id.add_to_cart_button));
        assertTrue("ItemListActivity not found", solo.waitForActivity(ItemListActivity.class));
    }

    private void doLogout() {
        solo.clickOnView(solo.getView(android.widget.ImageButton.class, 0));
        solo.clickOnActionBarItem(R.id.action_logout);
        assertTrue("LoginActivity not found", solo.waitForActivity(LoginActivity.class));
    }

    private static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }
}
