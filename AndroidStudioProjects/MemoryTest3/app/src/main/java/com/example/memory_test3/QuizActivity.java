package com.example.memory_test3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {

    private int nowQuizNum = 0;
    private int requiredQuestionNum = 0;
    private int randomNum;

    private ArrayList<ArrayList<String>> quizList = new ArrayList<>(); //タグに合致した問題を格納する二重ArrayList

    private Button answerButton;
    private Button nextQuizButton;
    private ImageButton favoriteButton;
    private TextView pseudonymText;
    private TextView memberText;
    private TextView nowNumberText;
    private ImageView questionImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        answerButton = findViewById(R.id.answerCheckButton);
        nextQuizButton = findViewById(R.id.nextQuizButton);
        pseudonymText = findViewById(R.id.pseudonymText);
        memberText = findViewById(R.id.memberText);
        nowNumberText = findViewById(R.id.nowNumberText);
        favoriteButton = findViewById(R.id.favorite);
        questionImageView = findViewById(R.id.questionImage);

        //setQuizFragment で設定したタグ設定をこのクラスで読み込むために以下で tagConfig.txt から設定を読み込み直していたが、
        //遷移時にBundleを用いてタグのリストを渡してしまえばよいので無駄。要改善
        File tagConfig = new File(getApplicationContext().getFilesDir(),"/tagConfig.txt"); //タグ設定ファイル
        ArrayList<String> tagList = new ArrayList<>();

        boolean isRandomFlag = false;       // タグ選択がなく、すべての問題から出題されるときtrueになるフラグ
        boolean isRemoveNicheFlag = false;  //「ニッチな問題を除く」を選択したかのフラグ

        try{
            FileInputStream fis = new FileInputStream(tagConfig);
            InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String tmp;

            while((tmp = br.readLine()) != null){
                if(tmp.contains("Num")) {
                    requiredQuestionNum = Integer.parseInt(tmp.substring(4));
                }

                if(tmp.contains("Ram")) {
                    if((int)(tmp.charAt(4)) == 49) isRandomFlag = true;
                }

                if(tmp.contains("Fav")) {
                    if((int)(tmp.charAt(4)) == 49) tagList.add("@@"); //お気に入りタグは "@@" で登録している
                }

                if(tmp.contains("Hum")) {
                    if((int)(tmp.charAt(4)) == 49) tagList.add("人物");
                }

                if(tmp.contains("Ani")) {
                    if((int)(tmp.charAt(4)) == 49) tagList.add("アニメ");
                }

                if(tmp.contains("Sin")) {
                    if((int)(tmp.charAt(4)) == 49) tagList.add("歌手");
                }

                if(tmp.contains("Ent")) {
                    if((int)(tmp.charAt(4)) == 49) tagList.add("芸人");
                }

                if(tmp.contains("Ido")) {
                    if((int)(tmp.charAt(4)) == 49) tagList.add("アイドル");
                }

                if(tmp.contains("Ath")) {
                    if((int)(tmp.charAt(4)) == 49) tagList.add("スポーツ選手");
                }

                if(tmp.contains("ONi")) {
                    if((int)(tmp.charAt(4)) == 49) tagList.add("ニッチ");
                }

                if(tmp.contains("RNi")){
                    if((int)(tmp.charAt(4)) == 49) isRemoveNicheFlag = true;
                }

                if(tmp.contains("Bse")) {
                    if((int)(tmp.charAt(4)) == 49) tagList.add("野球");
                }

                if(tmp.contains("Fot")) {
                    if((int)(tmp.charAt(4)) == 49) tagList.add("サッカー");
                }
            }
            fis.close();
            isr.close();
            br.close();
        }catch(Exception e){
            Log.e("tagConfig read ERROR",e.toString());
            Toast.makeText(getApplicationContext(),"tagConfig read ERROR",Toast.LENGTH_LONG).show();
        }
        //ここまでタグの読み込み（読み込んだタグはtagListに格納されている）


        //ここから タグに合致する問題を 問題全体が記述されている input.csv から抜き出し、quizListに格納する
        File inputFile = new File(getApplicationContext().getFilesDir(),"/input.csv"); //内部ストレージ
        //Log.e("path",MyApplication.getMyAppContext().getFilesDir().getPath());
        //File inputFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/input.csv");
        int questionNum = 0;
        try{
            InputStreamReader isr = new InputStreamReader(new FileInputStream(inputFile),"SHIFT-JIS");
            BufferedReader br = new BufferedReader(isr);

            String tmp;
            boolean tagFlag;
            br.readLine(); br.readLine(); //2行読み飛ばす

            //input.csvのすべての行について、タグ一致判定を行い 一致した問題は要素ごとに切り離して quizListに格納する
            while((tmp = br.readLine()) != null){
                tagFlag = true;
                if (!isRandomFlag) { //isRandomFlagがtrue（選択タグなし）であればタグ一致判定は行わない
                    if(isRemoveNicheFlag) {
                        if(tmp.contains("ニッチ")) tagFlag = false;
                    }
                    for (String str : tagList) {
                        tagFlag = tagFlag && (tmp.contains(str));
                    }
                }

                //タグ判定で一致した問題を以下で要素ごとに切り分け、二重ArrayListに格納する
                if(tagFlag) {
                    ArrayList<String> tmpArray = new ArrayList<>();
                    int startNum = 0;
                    int endNum;
                    while ((endNum = tmp.indexOf(",", startNum)) != -1) { //","が見つからなくなるまで繰り返す
                        tmpArray.add(tmp.substring(startNum, endNum)); //要素ごとにtmpArrayに格納
                        startNum = endNum + 1;
                    }
                    tmpArray.add(tmp.substring(startNum)); //最後の要素格納
                    quizList.add(tmpArray); //問題一行分を要素ごとに分けて格納したtmpArrayをquizListに格納
                    questionNum++;
                }
            }
            isr.close();
            br.close();

        }catch(Exception e){
            AlertDialog.Builder inAlert = new AlertDialog.Builder(this);
            inAlert.setTitle("クイズのリスト読み込みエラー \n" + e.toString());
            inAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            inAlert.setCancelable(false);
            inAlert.show();
        }
        //ここまで タグに合致した問題をquizListに格納する処理


        if(questionNum == 0 || quizList.size() == 0){ //クイズスタートボタンを押した段階でこの判定を行っていれば、この処理は不要
            Toast.makeText(getApplicationContext(),"該当問題が存在しません",Toast.LENGTH_LONG).show();
        }else{
            Collections.shuffle(quizList); //quizListに格納された問題の順番をシャッフル
            if(questionNum < requiredQuestionNum) requiredQuestionNum = questionNum; //ユーザーが選択した問題数より該当問題が少なければ、その数に合わせる
            //TextView debugText2 = findViewById(R.id.debugText2);
            //debugText2.setText("questionNum:" + questionNum);
            showQuiz();
        }
        TextView nowNumberText = findViewById(R.id.nowNumberText);
        nowNumberText.setText((nowQuizNum + 1) + "問目");

    } //onCreate関数


    public void nextButtonClick(View view){ //次の問題へボタンが押されたときの動作
        if(nowQuizNum != (requiredQuestionNum - 1)){
            nowQuizNum++;

            answerButton.setText("答えを表示");
            pseudonymText.setText("");
            memberText.setText("");
            nowNumberText.setText((nowQuizNum + 1) + "問目");
            if(nowQuizNum == (requiredQuestionNum - 1)) nextQuizButton.setText("終了");

            showQuiz();

        }else{ //終了時の処理
            LinearLayout layout = findViewById(R.id.quizActivityLayout);
            layout.removeAllViews(); //画面のコンポーネントを全消去

            //動的にViewを生成し、layout下に配置する
            TextView endText = new TextView(this);
            endText.setText(" 終了！");
            endText.setTextSize(50);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            //(width,height)
            //layoutParams.setMargins(0,200,0,0);
            endText.setLayoutParams(layoutParams);
            layout.addView(endText);
            layout.setGravity(Gravity.CENTER);
            //Log.e("タグ","画面遷移");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // 遅らせて実行したい処理
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent); //最初の画面(startActivity)へ遷移する
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); //画面遷移にアニメーションを
                }
            }, 800); // 遅らせたい時間(ミリ秒)
        }
    }


    //前の問題へボタンが押されたときの動作
    //1問題につき2つ画像が用意されているが、その問題が入れ替わってしまうことがある問題を放置してしまっている
    public void prevButtonClick(View view){
        if(nowQuizNum != 0){
            nowQuizNum--;

            answerButton.setText("答えを表示");
            nextQuizButton.setText("次の問題へ");
            pseudonymText.setText("");
            memberText.setText("");
            nowNumberText.setText((nowQuizNum + 1) + "問目");

            showQuiz();
            //showQuiz()では、表示する画像を2枚の内からランダムに決めているので、
            //前に戻るボタンを押すと先ほど表示されていた画像と異なる方の画像が表示される可能性がある
        }
    }


    private void showQuiz(){
        //お気に入りの描画
        if(quizList.get(nowQuizNum).get(7).contains("@@")) favoriteButton.setBackgroundResource(R.drawable.a_favorite);
        else favoriteButton.setBackgroundResource(R.drawable.a_not_favorite);

        //問題画像を表示
        String link;
        Random random = new Random();
        randomNum = random.nextInt(2); //0または1になる（画像は2パターン用意あり、どちらが表示されるかはランダム）
        if(randomNum == 0){
            link = quizList.get(nowQuizNum).get(4);
        }else link = quizList.get(nowQuizNum).get(5);

        int id = getResources().getIdentifier(link,"drawable",getPackageName());
        questionImageView.setImageResource(id);
        //最初drawableの下に他のフォルダを作って問題の画像を入れようとしたけど、drawableの下のフォルダは認識されないらしい
        //左のツリーにも表示されないし、getResources().getIdentifierの戻り値も0になった

        if(id == 0) {
            String path = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + quizList.get(nowQuizNum).get(4) + ".jpg";
            //Log.e("path",path);
            Bitmap bmp = BitmapFactory.decodeFile(path);
            if(bmp == null){
                path = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" +quizList.get(nowQuizNum).get(4) + ".png";
                bmp = BitmapFactory.decodeFile(path);
            }
            questionImageView.setImageBitmap(bmp);
        }


//        ImageAsyncTask async = new ImageAsyncTask(); //画像読み込みのため非同期処理
//        async.execute(link);
    }


    public void answerButtonClick(View view){ //答えを表示ボタンの動作
        answerButton.setText(quizList.get(nowQuizNum).get(1));
        pseudonymText.setText(quizList.get(nowQuizNum).get(2));
        memberText.setText(quizList.get(nowQuizNum).get(3));
    }


    public void favoriteButtonClick(View view){ //お気に入り登録ボタンの動作

        //ボタンが押されたときの問題が、csvファイル上で何番目の問題かを示すid　（+1されているのは下の変数countの初期値が0のため）
        int id = Integer.valueOf(quizList.get(nowQuizNum).get(0)) + 1;

        //input.csvにおいて、該当問題のお気に入り登録タグ部分を書き換える。
        //一部を書き換えることは不可能なので、結局全て出力し直している
        try{
            //File outFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/input.csv");
            File outFile = new File(getApplicationContext().getFilesDir(),"/input.csv"); //内部ストレージから読み込む

            InputStreamReader isr = new InputStreamReader(new FileInputStream(outFile),"SHIFT-JIS");
            BufferedReader br = new BufferedReader(isr);

            File tmpExFile = new File(getApplicationContext().getFilesDir(),"/tmpExFile.csv"); //書き写すための一時ファイル

            String tmp;
            FileOutputStream fos = new FileOutputStream(tmpExFile,true); //書き移すためのFileOutputStream
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos,"SHIFT-JIS"));
            int count = 0;
            //ボタンのクリックを受けてファイルのお気に入りの部分を書き換える
            while((tmp = br.readLine()) != null) {
                if(count != id) { //該当問題の行までは 元ファイルをそのまま書き写す
                    try {
                        bw.write(tmp + "\n");
                        bw.flush();
                    } catch (Exception e) {
                        Log.e("favButton writeERROR", e.toString());
                        Toast.makeText(getApplicationContext(), "favButton ERROR", Toast.LENGTH_LONG).show();
                    }
                }else{
                    if(tmp.contains("@@")) {
                        favoriteButton.setBackgroundResource(R.drawable.a_not_favorite);
                        bw.write(tmp.substring(0,tmp.length() - 3) + "\n");
                        bw.flush();

                        //お気に入りボタン表示を反映させるためだけに、もう一度isFavoriteFlagを読み込み直すのは馬鹿らしいのでquizListを操作
                        quizList.get(nowQuizNum).remove(7); //quizListから お気に入りタグ"@@"を削除
                    }else{
                        favoriteButton.setBackgroundResource(R.drawable.a_favorite);
                        bw.write(tmp + "、@@\n");
                        bw.flush();
                        quizList.get(nowQuizNum).add(7,"@@"); //お気に入りタグを追加
                    }
                }
                count++;
            }
            bw.close();
            fos.close();
            isr.close();
            br.close();

            //fos2.close(); osw2.close(); bw2.close();

            outFile.delete(); //元のinputファイルを消して、お気に入り設定を改めたtmpExFileを新しくinputファイルにする
            tmpExFile.renameTo(new File(getApplicationContext().getFilesDir(),"/input.csv"));

        }catch(Exception e){
            Log.e("favButton ERROR",e.toString());
            Toast.makeText(getApplicationContext(),"favButton ERROR",Toast.LENGTH_LONG).show();
        }
    } //ここまでお気に入り登録ボタンの動作


    public void imageReplaceButtonClick(View view){ //画像切り替えボタンの動作
        String link;
        if(randomNum == 0) { //画像の参照を反転させる
            link = quizList.get(nowQuizNum).get(5);
            randomNum = 1;
        }else{
            link = quizList.get(nowQuizNum).get(4);
            randomNum = 0;
        }

        int id = getResources().getIdentifier(link,"drawable",getPackageName());
        questionImageView.setImageResource(id);
        //最初drawableの下に他のフォルダを作って問題の画像を入れようとしたけど、drawableの下のフォルダは認識されないらしい
        //左のツリーにも表示されないし、getResources().getIdentifierの戻り値も0になった

        if(id == 0) { //リソースフォルダに画像がなかった場合、外部ストレージから探す
            String path = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + quizList.get(nowQuizNum).get(4) + ".jpg";
            //Log.e("path",path);
            Bitmap bmp = BitmapFactory.decodeFile(path);
            if(bmp == null){
                path = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" +quizList.get(nowQuizNum).get(4) + ".png";
                bmp = BitmapFactory.decodeFile(path);
            }
            questionImageView.setImageBitmap(bmp);
        }
    }








}
