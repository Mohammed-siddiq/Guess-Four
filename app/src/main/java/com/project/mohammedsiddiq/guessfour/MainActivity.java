package com.project.mohammedsiddiq.guessfour;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private static final int CHOOSE_PLAYER_ONE = 1;
    private static final int CHOOSE_PLAYER_TWO = 2;
    private static final int INITIAL_GUESS = 3;
    private static final String PLAYER_1_PICKED_NUMBER = "PLAYER_1_PICKED_NUMBER";
    private static final String PLAYER_2_PICKED_NUMBER = "PLAYER_2_PICKED_NUMBER";
    private static final int RESPOND_TO_OTHER_PLAYERS_GUESS = 5;
    private static final int RESPONSE_BY_P2 = 6;
    private static final int RESPONSE_BY_P1 = 4;
    private static final int RESPONSE_ON_MY_GUESS = 7;
    private static final int PLAYER_2s_NEW_GUESS = 8;
    private static final int PLAYER_1s_NEW_GUESS = 9;
    private static final int maxTurnsAllowed = 20;


    private static final String TAG = "UI Handler";
    private static final int GAME_OVER = 10;
    private TextView playerOneGuess;
    private TextView playerTwoGuess;
    private ListView listViewForPlayer1;
    private ListView listViewForPlayer2;
    PlayerOne playerOne;
    PlayerTwo playerTwo;
    private Handler playerOneHandler;
    private Handler playerTwoHandler;
    private boolean guessLocked;
    private ArrayList<String> player1Responses = new ArrayList<>();
    private ArrayList<String> player2Responses = new ArrayList<>();
    int playerOneCounter = 0;
    int playerTwoCounter = 0;
    int gameOutCome = 0; //0: draw, 1 = player1 winner, 2 = player2 winner
    GameHelper gameHelper = new GameHelper();
    Button gameButton;
    Boolean winnerDeclared = false;


    public static boolean GAME_STARTED = false;
    @SuppressLint("HandlerLeak")
    public Handler uiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case CHOOSE_PLAYER_ONE:
                    playerOneGuess.setText(msg.getData().getString(PLAYER_1_PICKED_NUMBER));
                    break;
                case CHOOSE_PLAYER_TWO:
                    playerTwoGuess.setText(msg.getData().getString(PLAYER_2_PICKED_NUMBER));
                    break;
                case RESPONSE_BY_P2:
                    Log.d(TAG, "Received response from Player 2 for P1's guess -> " + msg.obj.toString());
                    Response response = (Response) msg.obj;
                    if (response.numberOfBulls == 4) {
                        if (!winnerDeclared) {
                            gameHelper.showToast("Player ALPHA WINS!!", MainActivity.this);
                            gameButton.setText("ALPHA WINS! PLAY AGAIN?");
                            gameHelper.communicate(playerOneHandler, GAME_OVER, "");
                            gameHelper.communicate(playerTwoHandler, GAME_OVER, "");

                            gameOutCome = 1;
                            winnerDeclared = true;
                        }

                    }
                    if (playerOneCounter >= maxTurnsAllowed && playerTwoCounter >= maxTurnsAllowed) {
                        //quit game;

                        if (!winnerDeclared) {
                            gameHelper.communicateUsingRunnable(playerOneHandler, createRunnableToQuit());
                            gameHelper.communicateUsingRunnable(playerTwoHandler, createRunnableToQuit());
                            gameButton.setText("DRAW! PLAY AGAIN?");
                            gameOutCome = 0;
                        }
                        //Posting Runnable

                    }
                    player2Responses.add(msg.obj.toString());
                    displayTheListView(player2Responses, listViewForPlayer1, 1);
                    break;
                case RESPONSE_BY_P1:
                    Log.d(TAG, "Received response from Player 1 for p2's guess -> " + msg.obj.toString());
                    response = (Response) msg.obj;
                    if (response.numberOfBulls == 4) {

                        if (!winnerDeclared) {
                            gameHelper.showToast("Player BETA WINS!!", MainActivity.this);
                            gameHelper.communicate(playerOneHandler, GAME_OVER, "");
                            gameHelper.communicate(playerTwoHandler, GAME_OVER, "");
                            gameButton.setText("BETA WINS! PLAY AGAIN?");
                            gameOutCome = 2;
                            winnerDeclared = true;
                        }


                    }
                    if (playerTwoCounter >= maxTurnsAllowed && playerTwoCounter >= maxTurnsAllowed) {

                        //Communicating using Runnable
                        if (!winnerDeclared) {
                            gameHelper.communicateUsingRunnable(playerOneHandler, createRunnableToQuit());
                            gameHelper.communicateUsingRunnable(playerTwoHandler, createRunnableToQuit());
                            gameButton.setText("DRAW! PLAY AGAIN?");
                            gameOutCome = 0;
                            Toast.makeText(MainActivity.this, "Draw Match", Toast.LENGTH_LONG).show();
                        }

                    }
                    player1Responses.add(msg.obj.toString());
                    displayTheListView(player1Responses, listViewForPlayer2, 2);

            }
        }
    };

    private Runnable createRunnableToQuit() {
        return new Runnable() {
            @Override
            public void run() {
                Looper.myLooper().quit();
            }
        };
    }

    private void displayTheListView(ArrayList<String> responses, ListView listView, int which) {

        if (which == 2) {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                    (this, android.R.layout.simple_list_item_1, responses) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);

                    TextView tv = view.findViewById(android.R.id.text1);

                    tv.setTextColor(Color.parseColor("#ffffbb33"));

                    return view;
                }
            };

            listView.setAdapter(arrayAdapter);
            listView.setDivider(new ColorDrawable(Color.parseColor("#ffffbb33")));   //0xAARRGGBB
            listView.setDividerHeight(1);
        } else {

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>
                    (this, android.R.layout.simple_list_item_1, responses);


            listView.setAdapter(arrayAdapter);
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameButton = findViewById(R.id.action_button);

        playerOneGuess = findViewById(R.id.player1_tv_guessed);
        playerTwoGuess = findViewById(R.id.player2_tv_guessed);
        listViewForPlayer1 = findViewById(R.id.response_for_player1);
        listViewForPlayer2 = findViewById(R.id.response_for_player2);


        gameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playerOne = new PlayerOne();
                Thread tPlayerOne = new Thread(playerOne);

                playerTwo = new PlayerTwo();

                Thread tPlayerTwo = new Thread(new PlayerTwo());
                if (!GAME_STARTED) {
                    GAME_STARTED = true;
                    gameButton.setText("STOP AND RESTART GAME! ");
                    tPlayerOne.start();
                    tPlayerTwo.start();
                } else {
                    restartGame();
                    gameButton.setText("STOP AND RESTART GAME! ");
                    tPlayerOne.interrupt();
                    tPlayerTwo.interrupt();
                    tPlayerOne.start();
                    tPlayerTwo.start();
                    GAME_STARTED = true;

                }
            }
        });


    }

    private void restartGame() {
        GAME_STARTED = false;
        playerOneCounter = 0;
        playerTwoCounter = 0;
        playerOneHandler.getLooper().quit();
        playerTwoHandler.getLooper().quit();
        listViewForPlayer1.setAdapter(null);
        listViewForPlayer2.setAdapter(null);
        player1Responses.clear();
        player2Responses.clear();
        winnerDeclared = false;
    }


    public class PlayerOne implements Runnable {


        static final String TAG = "player one";
        private String myPickedNumber;
        private Response myResponse;
        private Response lastPlayer2Response;
        private List<Response> player2Responses;
        private String myGuess;
        private String player2Guess;
        Message playerOneMsg;
        private List<String> allMyGuesses;


        private GameHelper gameHelper = new GameHelper();


        PlayerOne() {
            myPickedNumber = gameHelper.generateRandomNumber();

            player2Responses = new ArrayList<>(20);
            allMyGuesses = new ArrayList<>(20);


        }

        @SuppressLint("HandlerLeak")
        @Override
        public void run() {

            playerOneMsg = uiHandler.obtainMessage(MainActivity.CHOOSE_PLAYER_ONE);
            Bundle bundle = new Bundle();
            bundle.putString(MainActivity.PLAYER_1_PICKED_NUMBER, myPickedNumber);
            playerOneMsg.setData(bundle);
            uiHandler.sendMessage(playerOneMsg);

            gameHelper.SleepNow(1000);

            playerOneMsg = playerTwoHandler.obtainMessage(INITIAL_GUESS);
            playerOneMsg.obj = "I have picked, give me your guess";
            playerTwoHandler.sendMessage(playerOneMsg);

            //communicating to player two

            Looper.prepare();
            playerOneHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case INITIAL_GUESS:
                            // Initial Guess...
                            while (guessLocked) {
                                //wait till the guess is locked
                                Log.d(TAG, "Guess was Locked!!");
                            }
                            guessLocked = true;
                            myGuess = gameHelper.generateRandomNumber();
                            guessLocked = false;
                            Log.d(TAG, "Player 1's PICK -> " + myGuess);
                            gameHelper.communicate(playerTwoHandler, RESPOND_TO_OTHER_PLAYERS_GUESS, myGuess);
                            allMyGuesses.add(myGuess);
                            break;
                        case RESPOND_TO_OTHER_PLAYERS_GUESS:
                            player2Guess = msg.obj.toString();
                            Log.d(TAG, "Player 2's Next Guess -> " + player2Guess);
                            myResponse = gameHelper.processAndRespond(player2Guess, myPickedNumber);
//
//                            //Communicating to player 2
                            gameHelper.communicate(playerTwoHandler, RESPONSE_ON_MY_GUESS, myResponse);

                            //communicating to UI
                            gameHelper.communicate(uiHandler, RESPONSE_BY_P1, myResponse);
                            break;
                        case RESPONSE_ON_MY_GUESS:
                            Log.d(TAG, "Received response from player2 -> " + msg.obj);
                            lastPlayer2Response = (Response) msg.obj;
                            player2Responses.add(lastPlayer2Response);
                            do {
                                myGuess = gameHelper.player1Strategy(lastPlayer2Response);
                            } while (allMyGuesses.contains(myGuess));
                            allMyGuesses.add(myGuess);
                            gameHelper.communicate(playerTwoHandler, RESPOND_TO_OTHER_PLAYERS_GUESS, myGuess);

                            gameHelper.SleepNow(2000);
                            gameHelper.communicate(uiHandler, PLAYER_1s_NEW_GUESS, myGuess);
                            break;
                        case GAME_OVER:
                            Looper.myLooper().quit();


                    }
                }
            };
            Looper.loop();

        }
    }


    public class PlayerTwo implements Runnable {


        private static final String TAG = "player two";
        private String myPickedNumber;

        private String myGuess;
        private String player1Guess;
        private Response myResponse;
        private Response lastPlayer1Response;
        private List<Response> player1Responses;
        private GameHelper gameHelper = new GameHelper();
        private List<String> allMyGuesses;


        PlayerTwo() {
            myPickedNumber = gameHelper.generateRandomNumber();
            player1Responses = new ArrayList<>(20);
            allMyGuesses = new ArrayList<>(20);
        }


        @SuppressLint("HandlerLeak")
        @Override
        public void run() {

            Message playerTwoMsg = uiHandler.obtainMessage(MainActivity.CHOOSE_PLAYER_TWO);
            final Bundle bundle = new Bundle();
            bundle.putString(MainActivity.PLAYER_2_PICKED_NUMBER, myPickedNumber);
            playerTwoMsg.setData(bundle);
            uiHandler.sendMessage(playerTwoMsg);

            Looper.prepare();
            playerTwoHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case INITIAL_GUESS:
                            guessLocked = true; // acquire the lock.
                            gameHelper.SleepNow(1000);
                            myGuess = gameHelper.generateRandomNumber();
                            guessLocked = false;//release the lock
                            Log.d(TAG, " Player 2's PICK -> " + myGuess);
                            gameHelper.communicate(playerOneHandler, INITIAL_GUESS, "I have picked, give me your guess");
                            gameHelper.communicate(playerOneHandler, RESPOND_TO_OTHER_PLAYERS_GUESS, myGuess);
                            allMyGuesses.add(myGuess);
                            playerTwoCounter++;
                            break;
                        case RESPOND_TO_OTHER_PLAYERS_GUESS:
                            player1Guess = msg.obj.toString();
                            Log.d(TAG, "Player 1's Next Guess -> " + player1Guess);
                            myResponse = gameHelper.processAndRespond(player1Guess, myPickedNumber);
                            // Communicating to player 1
                            gameHelper.communicate(playerOneHandler, RESPONSE_ON_MY_GUESS, myResponse);
                            // communicating to UI
                            gameHelper.communicate(uiHandler, RESPONSE_BY_P2, myResponse);
                            break;
                        case RESPONSE_ON_MY_GUESS:
                            Log.d(TAG, "Received response from player1 -> " + msg.obj);
                            lastPlayer1Response = (Response) msg.obj;
                            player1Responses.add(lastPlayer1Response);

                            do {
                                myGuess = gameHelper.player2Strategy(lastPlayer1Response);
                            } while (allMyGuesses.contains(myGuess));
                            allMyGuesses.add(myGuess);
                            playerTwoCounter++;

                            gameHelper.communicate(playerOneHandler, RESPOND_TO_OTHER_PLAYERS_GUESS, myGuess);

                            gameHelper.SleepNow(3000);

                            gameHelper.communicate(uiHandler, PLAYER_2s_NEW_GUESS, myResponse);


                            break;
                        case GAME_OVER:
                            Looper.myLooper().quit();//quitting looper


                    }
                }
            };
            Looper.loop();
        }
    }


}


