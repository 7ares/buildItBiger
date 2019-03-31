package com.udacity.gradle;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.udacity.gradle.builditbigger.MainActivity;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestMainActivity {
    String verifyString = null ;
    private IdlingResource mIdlingResource;
@Rule
    public ActivityTestRule<MainActivity> testRule =
        new ActivityTestRule<>(MainActivity.class);
@Before
public void stubAllInternalIntents() {
    mIdlingResource = testRule.getActivity().getIdleResource();
    IdlingRegistry.getInstance().register(mIdlingResource);
}
    @After
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) IdlingRegistry.getInstance().unregister(mIdlingResource);
    }
@Test
    public void testAsyncTasks (){
    MainActivity mainActivity = new MainActivity();
    MainActivity.EndpointsAsyncTask task = mainActivity.new EndpointsAsyncTask();

    task.setListener(new MainActivity.AsyncTaskListener() {
        @Override
        public String onComplete(String result) {
            verifyString = result ;
            return result;
        }
    }).execute("hares");
    org.junit.Assert.assertEquals(verifyString , "hares");

}}
