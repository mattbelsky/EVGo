package com.example.evrouteplannerapp;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class UITest {

    @Rule
    public ActivityTestRule<MapsActivity> mActvityTestRule = new ActivityTestRule<>(MapsActivity.class);

    @Test
    public void clickEditText() {

        onView(withId(R.id.tv_origin)).perform(click());
    }
}
