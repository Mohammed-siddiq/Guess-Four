package com.project.mohammedsiddiq.guessfour;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class GameHelper {


    Response processAndRespond(String guessedNumber, String pickedNumber) {

        Response response = new Response();
        response.forTheGuess = guessedNumber;
        for (int i = 0; i < guessedNumber.length(); i++) {
            //Number of exact matches
            if (guessedNumber.charAt(i) == pickedNumber.charAt(i)) {
                response.numberOfBulls++;
                response.bullsLocation.set(i, Boolean.TRUE);
                guessedNumber = guessedNumber.replace(guessedNumber.charAt(i), '$');
                pickedNumber = pickedNumber.replace(pickedNumber.charAt(i), '*');
            }
        }

        for (int i = 0; i < guessedNumber.length(); i++) {
            char ch = guessedNumber.charAt(i);
            if (pickedNumber.contains(String.valueOf(ch))) {
                pickedNumber = pickedNumber.replace(ch, '*');
                response.numberOfCows++;

            }
        }
        return response;
    }

    String generateRandomNumber() {

        Random random = new Random();
        int num[] = new int[]{10, 20, 30, 40};
        for (int i = 0; i < num.length; i++) {
            int r = random.nextInt(10);
            while ((i == 0 && r == 0) || r == num[0] || r == num[1] || r == num[2] || r == num[3]) {
                r = random.nextInt(10);

            }
            num[i] = r;
        }

        return Arrays.toString(num).replaceAll(",| ", "").replaceAll("\\[|\\]", "");
    }

    void SleepNow(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //communicating via message
    public void communicate(Handler handler, int what, Object data) {
        Message message = handler.obtainMessage(what);
        message.obj = data;
        handler.sendMessage(message);
    }


    //Communicating via a Runnable
    public void communicateUsingRunnable(Handler handler, Runnable runnable) {
        handler.post(runnable);
    }

    private int[] generateGoodGuess(int[] guessed) {
        Random random = new Random();
        for (int i = 0; i < guessed.length; i++) {
            if (guessed[i] != -1) continue;
            int r = random.nextInt(10);
            while ((i == 0 && r == 0) || r == guessed[0] || r == guessed[1] || r == guessed[2] || r == guessed[3]) {
                r = random.nextInt(10);
            }
            guessed[i] = r;
        }
        return guessed;
    }

    private int[] generateDiffGuess(int[] from) {
        Random random = new Random();
        for (int i = 0; i < from.length; i++) {
            int r = random.nextInt(10);
            while ((i == 0 && r == 0) || r == from[0] || r == from[1] || r == from[2] || r == from[3]) {
                r = random.nextInt(10);
            }
            from[i] = r;
        }

        return from;
    }

    //Build the numbers by considering exact matches till now and other numbers randomly
    public String player2Strategy(Response lastResponse) {

        int newGuess[] = new int[]{-1, -1, -1, -1};

        for (int i = 0; i < 4; i++) {
            if (lastResponse.bullsLocation.get(i)) {

                newGuess[i] = Character.getNumericValue(lastResponse.forTheGuess.charAt(i));

            }
        }


        return convertArrayToString(generateGoodGuess(newGuess));
    }

    private String convertArrayToString(int[] num) {
        return Arrays.toString(num).replaceAll(",| ", "").replaceAll("\\[|\\]", "");
    }


    ///build the numbers considering exact and non exact matches
    public String player1Strategy(Response lastResponse) {
        int newGuess[] = new int[]{-1, -1, -1, -1};

        ArrayList<Integer> possibleNumbers = new ArrayList<>(4);
        if (lastResponse.numberOfBulls == 0 && lastResponse.numberOfCows == 0) {

            for (int i = 0; i < 4; i++) {
                newGuess[i] = Character.getNumericValue(lastResponse.forTheGuess.charAt(i));
            }
            return convertArrayToString(generateDiffGuess(newGuess));//If zero matches generate a different numbers


        }

        if (lastResponse.numberOfCows != 0 && lastResponse.numberOfBulls != 0) {
            for (int i = 0; i < 4; i++) {
                if (lastResponse.bullsLocation.get(i)) {

                    newGuess[i] = Character.getNumericValue(lastResponse.forTheGuess.charAt(i));
                    possibleNumbers.add(i, -1);//Bulls is fixed
                } else {
                    possibleNumbers.add(i, Character.getNumericValue(lastResponse.forTheGuess.charAt(i)));
                }

            }
            newGuess = shuffleNonExactMatches(possibleNumbers, lastResponse, newGuess);

            return convertArrayToString(newGuess);

        }
        if (lastResponse.numberOfBulls != 0) {
            for (int i = 0; i < 4; i++) {
                if (lastResponse.bullsLocation.get(i)) {

                    newGuess[i] = Character.getNumericValue(lastResponse.forTheGuess.charAt(i));
                }
            }
            return convertArrayToString(generateGoodGuess(newGuess));

        } else {
            return convertArrayToString(generateDiffGuess(newGuess));
        }

    }

    //Shuffling the non-exact matches
    private int[] shuffleNonExactMatches(ArrayList<Integer> possibleNumbers, Response lastResponse, int[] newGuess) {
        for (int i = 0; i < newGuess.length; i++) {
            if(possibleNumbers.get(i)!=-1)
            {
                Random random = new Random();
                int r = random.nextInt(10);
                while ((i == 0 && r == 0) || r == newGuess[0] || r == newGuess[1] || r == newGuess[2] || r == newGuess[3]) {
                    r = random.nextInt(10);
                }
                newGuess[i] = r;
            }
        }
        return newGuess;
    }


    // Using Runnable!!
    void showToast(final String message, final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
            }
        });

    }

}
