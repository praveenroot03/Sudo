package com.project.sudo;

import org.junit.Test;

import java.net.URL;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public static boolean isValidURL(String url) {
        /* Try creating a valid URL */
        try {
            new URL(url).toURI();
            return true;
        }

        // If there was an Exception
        // while creating URL object
        catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidPhnum(String str) {

        if (str.matches("^[0-9]{10}$")) {
            return true;
        }
        return false;
    }

    @Test
    public void testIsNumber() {
        assertEquals(true, isNumeric("45"));
        assertEquals(false, isNumeric("string"));
    }

    @Test
    public void testIsValidEmail() {
        assertEquals(true, isValidEmail("aravi@gmail.com"));
        assertEquals(false, isValidEmail("ravi.com"));
    }

    @Test
    public void testIsValidURL() {
        assertEquals(true, isValidURL("http://uandi.com"));
        assertEquals(false, isValidURL("http://bagl ."));
    }

    @Test
    public void testIsValidPhnum() {
        assertEquals(true, isValidPhnum("7708792727"));
        assertEquals(false, isValidPhnum("77087927243"));
    }

}