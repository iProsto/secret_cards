package com.shevchenko.secretcards;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //# CONST

    private boolean showGame = true;
    private final int CARD_COUNT = 12;
    private final String REF_NAME = "web_url";
    private final String FIRST_START = "first_start";
    private final String STATE_NAME = "state_path";
    public static String webViewURL = "https://forapp.site/click.php?key=4cg631mtg1mndp3y5c47";
    private final int[] orderCardArr = {1, 1, 1, 2, 2, 1, 3, 3, 2, 4, 4, 2};

    //# VIEW

    private ImageView[] images;
    private ImageView firstOpenedCard = null;
    private ImageView[] openedCards;
    private TextView userScore;

    //# GAME VARIABLES
    private int[] randCardArr;
    private int score = 0;
    private boolean gameRunning = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webViewURL = loadLink();
        new RequestToGit(new RequestListener() {
            @Override
            public void waiterForBool(boolean bool) {
                if (showGame != bool) {
                    showGameHideWebView(bool);
                }
            }

            @Override
            public void waiterForLink(String link) {
                if (!webViewURL.equals(link)) {
                    webViewURL = link;
                    showGameHideWebView(showGame);
                }
                showLog(String.valueOf(link));
            }

            @Override
            public void rejection(){
                if(firstStart()){
                    showGameHideWebView(true);
                }
            }
        });

        if (!firstStart()){
            showGameHideWebView(isShowGame());
        }
    }

    public void openWebActivity(String url) {
        Intent intent = new Intent(this, InternetActivity.class);
        intent.putExtra(EXTRA_MESSAGE, url);
        startActivity(intent);
        finish();
    }


    /*#
     * # INIT FUNCTION
     * #
     * */


    private void initialization() {
        randCardArr = new int[CARD_COUNT];
        openedCards = new ImageView[CARD_COUNT];
        randCardArr = randomizeArray(orderCardArr);
//        webView = findViewById(R.id.mainWebView);
//        gameLinear = findViewById(R.id.linearGame);
//        webViewLinear = findViewById(R.id.linearWebView);
        userScore = findViewById(R.id.userScore);
    }

    private void initButton() {
        images = new ImageView[CARD_COUNT];
        images[0] = findViewById(R.id.card1);
        images[1] = findViewById(R.id.card2);
        images[2] = findViewById(R.id.card3);
        images[3] = findViewById(R.id.card4);
        images[4] = findViewById(R.id.card5);
        images[5] = findViewById(R.id.card6);
        images[6] = findViewById(R.id.card7);
        images[7] = findViewById(R.id.card8);
        images[8] = findViewById(R.id.card9);
        images[9] = findViewById(R.id.card10);
        images[10] = findViewById(R.id.card11);
        images[11] = findViewById(R.id.card12);

        for (ImageView img : images) {
            img.setOnClickListener(MainActivity.this);
        }
    }


    /*#
     * # GLOBAL FUNCTION
     * #
     * */


    private void showGameHideWebView(boolean showGame) {
       runOnUiThread(()->{
           this.showGame = showGame;
           saveState(showGame);
           if (!showGame) {
               openWebActivity(webViewURL);
           } else {
               LinearLayout gameScene = findViewById(R.id.gameScene);
               gameScene.setVisibility(View.VISIBLE);
               initialization();
               initButton();
               randomizeCardSpawn();
               firstClosingCards();
           }
       });
    }

    public void saveLink(String url) {
        SharedPreferences sPref;
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(REF_NAME, url);
        ed.commit();
    }

    public String loadLink() {
        SharedPreferences sPref;
        String answerString;
        sPref = getPreferences(MODE_PRIVATE);
        answerString = sPref.getString(REF_NAME, webViewURL);
        return answerString;
    }

    public void saveState(boolean showGame) {
        SharedPreferences sPref;
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean(STATE_NAME, showGame);
        ed.commit();
        showLog("Save Game STATE: " + showGame);
    }


    /*#
     * # GAME FUNCTION
     * #
     * */

    private void firstClosingCards() {
        Timer timer = new Timer();
        timer.schedule(new CardCloser(), 3000);
    }

    private int[] randomizeArray(int[] arr) {
        Random rand = new Random();
        int size = arr.length;
        for (int i = arr.length - 1; i > 0; i--) {
            int randInt = rand.nextInt(i + 1);
            int temp = arr[randInt];
            arr[randInt] = arr[i];
            arr[i] = temp;
        }
        return arr;
    }

    private void randomizeCardSpawn() {
        int i = 0;
        for (ImageView img : images) {
            openCard(randCardArr[i++], img);
        }
    }

    private void openCard(int randId, ImageView img) {
        switch (randId) {
            case 1:
                img.setImageResource(R.drawable.card1);
                break;
            case 2:
                img.setImageResource(R.drawable.card2);
                break;
            case 3:
                img.setImageResource(R.drawable.card3);
                break;
            case 4:
                img.setImageResource(R.drawable.card4);
                break;
        }
    }

    private void closeCard(ImageView img) {
        img.setImageResource(R.drawable.card_background);
    }

    private boolean isOpened(ImageView imageView) {
        for (ImageView img : openedCards)
            if (imageView == img) return true;
        return false;
    }

    private void scoreAdd() {
        userScore.setText(String.valueOf(++score + Integer.valueOf(userScore.getText().toString())));
        if (score == 6) {
            gameRunning = false;
            Timer timer = new Timer();
            timer.schedule(new RestartGame(), 2000);
        }
    }

    private void restartGame() {
        score = 0;
        randCardArr = randomizeArray(orderCardArr);
        openedCards = new ImageView[CARD_COUNT];
        randomizeCardSpawn();
        firstClosingCards();
    }

    private boolean cardCompare(ImageView img1, ImageView img2) {
        int cardNumber1 = imgIdToInt(getResources().getResourceEntryName(img1.getId())) - 1;
        int cardNumber2 = imgIdToInt(getResources().getResourceEntryName(img2.getId())) - 1;
        if (randCardArr[cardNumber1] == randCardArr[cardNumber2]) {
            return true;
        }
        return false;
    }

    private int imgIdToInt(String id) {
        String[] names = {
                "card1", "card2", "card3",
                "card4", "card5", "card6",
                "card7", "card8", "card9",
                "card10", "card11", "card12"
        };
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(id)) return i + 1;
        }
        return -1;
    }

    private boolean isShowGame() {
        SharedPreferences sPref;
        sPref = getPreferences(MODE_PRIVATE);
        showLog("Load Game STATE: " + sPref.getBoolean(STATE_NAME, showGame));
        return sPref.getBoolean(STATE_NAME, showGame);
    }

    private boolean firstStart() {
        boolean answ;
        SharedPreferences sPref;
        sPref = getPreferences(MODE_PRIVATE);
        answ = sPref.getBoolean(FIRST_START, true);
        showLog("Load FIRST: " + sPref.getBoolean(FIRST_START, true));

        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean(FIRST_START, false);
        ed.commit();
        return answ;
    }

    /*#
     * # OVERRIDE FUNCTION
     * #
     * */


    @Override
    public void onClick(View v) {
        ImageView img = (ImageView) v;
        if (gameRunning && !isOpened(img)) {
            int indexInRandArr = imgIdToInt(getResources().getResourceEntryName(img.getId()));
            openCard(randCardArr[indexInRandArr - 1], img);
            if (firstOpenedCard == null) {
                firstOpenedCard = img;
            } else if (firstOpenedCard != img) {
                if (cardCompare(img, firstOpenedCard)) {
                    if (2 * score + 1 < openedCards.length) {
                        openedCards[2 * score] = img;
                        openedCards[2 * score + 1] = firstOpenedCard;
                    }
                    scoreAdd();
                } else {
                    gameRunning = false;
                    Timer timer = new Timer();
                    timer.schedule(new CardCloser(img, firstOpenedCard), 1000);
                }
                firstOpenedCard = null;
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveLink(webViewURL);
        saveState(showGame);
        showLog("Game DESTROY");
    }

//    @Override
//    public void onBackPressed() {
////        showLog("\n");
////        getBackForwardList(webView.copyBackForwardList());
//        if (webView.canGoBack()) {
//            if (webView.copyBackForwardList().getSize() >= 7) {
//                webView.goBackOrForward(-3);
//                bigBack = true;
//            } else {
//                webView.goBack();
//            }
//            return;
//        }
//        super.onBackPressed();
//    }


    /*#
     * # CLASSES AND DEBUG
     * #
     * */

//    public void getBackForwardList(WebBackForwardList currentList) {
//        int currentSize = currentList.getSize();
//        for (int i = 0; i < currentSize; ++i) {
//            WebHistoryItem item = currentList.getItemAtIndex(i);
//            String url = item.getUrl();
//            showLog("List id " + i + " is " + url);
//        }
//    }

    private void showLog(String str) {
        Log.d("Loger", "Debug log: " + str);
    }

    class CardCloser extends TimerTask {
        ImageView[] imgs = {null, null};

        public CardCloser() {
        }

        public CardCloser(ImageView img1, ImageView img2) {
            imgs[0] = img1;
            imgs[1] = img2;
        }

        @Override
        public void run() {
            if (imgs[0] == null) {
                for (ImageView img : images) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeCard(img);
                        }
                    });
                    try {
                        Thread.sleep(100);
                    } catch (Exception ex) {
                    }
                }
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeCard(imgs[0]);
                        closeCard(imgs[1]);
                    }
                });
            }
            gameRunning = true;
        }
    }

    class RestartGame extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    restartGame();
                }
            });
            gameRunning = true;
        }
    }
}