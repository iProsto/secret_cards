package com.example.commercialapp;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebHistoryItem;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.applinks.AppLinkData;
import com.onesignal.OneSignal;
import com.tenjin.android.TenjinSDK;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import bolts.AppLinks;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //# CONST

    private boolean showGame = true;
    private final int CARD_COUNT = 12;
    private final String REF_NAME = "web_url";
    private final String STATE_NAME = "state_path";
    public static String webViewURL = "https://forapp.site/click.php?key=4cg631mtg1mndp3y5c47";
    private final int[] orderCardArr = {1, 1, 1, 2, 2, 1, 3, 3, 2, 4, 4, 2};

    //# VIEW

    private ImageView[] images;
//    private LinearLayout gameLinear;
    private ImageView firstOpenedCard = null;
    private ImageView[] openedCards;
    private TextView userScore;

    //# GAME VARIABLES
    private int[] randCardArr;
    private int score = 0;
    private boolean gameRunning = false;

//    //# OTHER
//    public ValueCallback<Uri[]> uploadMessage;
//    private ValueCallback<Uri> mUploadMessage;
//    public static final int REQUEST_SELECT_FILE = 100;
//    private final static int FILECHOOSER_RESULTCODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialization();

//        initWebView();
        initButton();
        randomizeCardSpawn();
        firstClosingCards();
//        initPushMsg();
//        initFB();

        showGameHideWebView(isShowGame());

        new RequestToGit(new RequestListener() {
            @Override
            public void waiterForBool(boolean bool) {
                //bool = !bool; //$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$ Temporarily
                if (showGame != bool) {
                    showGameHideWebView(bool);
                }
                showLog(String.valueOf(bool));
            }

            @Override
            public void waiterForLink(String link) {
                if (!webViewURL.equals(link)) {
                    webViewURL = link;
                    showGameHideWebView(showGame);
                }
                showLog(String.valueOf(link));
            }
        });
    }

    public void openWebActivity(String url) {
        Intent intent = new Intent(this, InternetActivity.class);
        intent.putExtra(EXTRA_MESSAGE, url);
        startActivity(intent);
    }


    /*#
     * # INIT FUNCTION
     * #
     * */

//    @SuppressLint("SetJavaScriptEnabled")
//    private void initWebView() {
//        webViewURL = loadLink();
//        WebSettings settings = webView.getSettings();
//        settings.setDomStorageEnabled(true);
//        settings.setJavaScriptEnabled(true);
//        settings.setAllowContentAccess(true);
//        settings.setAllowFileAccess(true);
//        settings.setAppCacheEnabled(true);
//        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
////        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
//
//        WebViewClient webViewClient = new WebViewClient() {
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                view.loadUrl(url);
//                return true;
//            }
//
//            @TargetApi(Build.VERSION_CODES.N)
//            @Override
//            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                showLog("Load url2: " + request.getUrl().toString());
////                showLog("Load getPath: " + request.getUrl().getPath());
//
//                if (bigBack && request.getUrl().getPath() != "/") {
////                    showLog("Backed");
//                    view.loadUrl(request.getUrl().toString().substring(0, request.getUrl().toString().length() - request.getUrl().getPath().length() + 1));
//                } else {
//                    view.loadUrl(request.getUrl().toString());
//                }
//                bigBack = false;
//                return true;
//            }
//
//            @Override
//            public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
//                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                String message = "SSL Certificate error.";
//                showLog(message);
//                switch (error.getPrimaryError()) {
//                    case SslError.SSL_UNTRUSTED:
//                        message = "The certificate authority is not trusted.";
//                        break;
//                    case SslError.SSL_EXPIRED:
//                        message = "The certificate has expired.";
//                        break;
//                    case SslError.SSL_IDMISMATCH:
//                        message = "The certificate Hostname mismatch.";
//                        break;
//                    case SslError.SSL_NOTYETVALID:
//                        message = "The certificate is not yet valid.";
//                        break;
//                }
//                message += "Do you want to continue anyway?";
//
//                builder.setTitle("SSL Certificate Error");
//                builder.setMessage(message);
//                builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        handler.proceed();
//                    }
//                });
//                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        handler.cancel();
//                    }
//                });
//                final AlertDialog dialog = builder.create();
//                dialog.show();
//            }
//        };
//
//        WebChromeClient myChromeClient = new WebChromeClient() {
//            // For Lollipop 5.0+ Devices
//            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
//                if (uploadMessage != null) {
//                    uploadMessage.onReceiveValue(null);
//                    uploadMessage = null;
//                }
//                uploadMessage = filePathCallback;
//                Intent intent = fileChooserParams.createIntent();
//                try {
//                    startActivityForResult(intent, REQUEST_SELECT_FILE);
//                } catch (ActivityNotFoundException e) {
//                    uploadMessage = null;
//                    Toast.makeText(MainActivity.this, "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
//                    return false;
//                }
//                return true;
//            }
//        };
//
//        webView.setWebChromeClient(myChromeClient);
//        webView.setWebViewClient(webViewClient);
//    }

//    private void initPushMsg() {
//        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
//        OneSignal.startInit(this)
//                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
//                .unsubscribeWhenNotificationsAreDisabled(true)
//                .init();
//    }

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

    //    private void initFireBaseDB() {
//        database = FirebaseDatabase.getInstance();
//        myRef = database.getReference();
//        myRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Map<String, Boolean> value = (Map<String, Boolean>) dataSnapshot.getValue();
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                Log.w("test", "Failed to read value.", error.toException());
//            }
//        });
//    }
//    private void initFB() {
//        FacebookSdk.setApplicationId(String.valueOf(R.string.facebook_app_id));
//        FacebookSdk.sdkInitialize(this);
//        FacebookSdk.fullyInitialize();
//        FacebookSdk.setAutoInitEnabled(true);
//        Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
//        if (targetUrl != null) {
//            setDeepLink(targetUrl.getQuery());
//        } else {
//            AppLinkData.fetchDeferredAppLinkData(
//                    this, String.valueOf(R.string.facebook_app_id),
//                    appLinkData -> {
//                        if (appLinkData != null) {
//                            setDeepLink(appLinkData.getTargetUri().getQuery());
//                        }
//                    });
//        }
//    }


    /*#
     * # GLOBAL FUNCTION
     * #
     * */

//    private void getAdvertisingID() {
//        @SuppressLint("StaticFieldLeak")
//        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
//            @Override
//            protected String doInBackground(Void... params) {
//                AdvertisingIdClient.Info idInfo = null;
//                try {
//                    idInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
//                } catch (GooglePlayServicesNotAvailableException e) {
//                    e.printStackTrace();
//                } catch (GooglePlayServicesRepairableException e) {
//                    e.printStackTrace();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                String advertId = null;
//                try {
//                    advertId = idInfo.getId();
//
//                } catch (NullPointerException e) {
//                    e.printStackTrace();
//                }
//
//                return advertId;
//            }
//
//            @Override
//            protected void onPostExecute(String advertId) {
//                showLog(advertId);
//            }
//
//        };
//        task.execute();
//    }

    private void showGameHideWebView(boolean showGame) {
        this.showGame = showGame;
        saveState(showGame);
//            if (showGame) {
//                webViewLinear.setVisibility(View.GONE);
//                gameLinear.setVisibility(View.VISIBLE);
//            } else {
//                webViewLinear.setVisibility(View.VISIBLE);
//                gameLinear.setVisibility(View.GONE);
//                webView.loadUrl(webViewURL);
//            }
        if (!showGame) {
            openWebActivity(webViewURL);
        }
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
    }

//    private void setDeepLink(String deepLink) {
//        String url = webViewURL;
//        showLog("Link got: " + deepLink);
//        showLog("Link webview: " + webViewURL);
//        if (webViewURL.indexOf('?') == -1) {
//            url += "/?" + deepLink;
//        } else {
//            url += "&" + deepLink;
//        }
//        webView.loadUrl(url);
//        showLog("Link got2: " + url);
//    }


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
        return sPref.getBoolean(STATE_NAME, showGame);
    }


    /*#
     * # OVERRIDE FUNCTION
     * #
     * */

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        super.onActivityResult(requestCode, resultCode, intent);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            if (requestCode == REQUEST_SELECT_FILE) {
//                if (uploadMessage == null)
//                    return;
//                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
//                uploadMessage = null;
//            }
//        } else if (requestCode == FILECHOOSER_RESULTCODE) {
//            if (null == mUploadMessage)
//                return;
//            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
//            mUploadMessage.onReceiveValue(result);
//            mUploadMessage = null;
//        } else
//            Toast.makeText(this, "Failed to Upload Image", Toast.LENGTH_LONG).show();
//    }

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

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        TenjinSDK instance = TenjinSDK.getInstance(this, API_KEY);
//        String[] optInParams = {"ip_address", "advertising_id", "developer_device_id", "limit_ad_tracking", "referrer", "iad"};
//        instance.optInParams(optInParams);
//        instance.connect();
//    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        webView.saveState(outState);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        webView.restoreState(savedInstanceState);
//    }

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