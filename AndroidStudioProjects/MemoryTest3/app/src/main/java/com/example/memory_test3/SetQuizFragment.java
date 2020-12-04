package com.example.memory_test3;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link com.example.memory_test3.SetQuizFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

//順番がややこしいが、StartQuizFragmentで「開始ボタン」を押すと、クイズの設定のためのSetQuizFragmentが表示される。
//クイズの設定が終わって、その上でSetQuizFragment内の「開始ボタン」を押すと、QuizActivityが呼ばれて クイズが始まる。
public class SetQuizFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String questionNumStr;

    private CheckBox RandomCheckBox;
    private CheckBox FavoriteCheckBox;
    private CheckBox HumanCheckBox;
    private CheckBox AnimeCheckBox;
    private CheckBox SingerCheckBox;
    private CheckBox EntertainerCheckBox;
    private CheckBox IdolCheckBox;
    private CheckBox AthleteCheckBox;
    private CheckBox RemoveNicheCheckBox;
    private CheckBox OnlyNicheCheckBox;
    private CheckBox BaseballCheckBox;
    private CheckBox FootballCheckBox;

    private Spinner spinner;
    private TextView hitNumText;


    public SetQuizFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SetQuizFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static com.example.memory_test3.SetQuizFragment newInstance(String param1, String param2) {
        com.example.memory_test3.SetQuizFragment fragment = new com.example.memory_test3.SetQuizFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    //おそらくだが、Fragmentのレイアウトを記述しているxmlファイルのonClick属性が機能しないのは、ActivityのonCreate関数内setContentViewのようにxmlファイルと
    //紐づけする関数がFragmentにないためではないか？
    //だから、Fragmentでレイアウトはわざわざ下記のようにインフレーターで生成しているのではないだろうか。
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_set_quiz, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //ActivityからFragmentに変えた影響でレイアウトxmlの onClick属性 が使えなくなり、大量のonClickListenerの記述が必要になった…。
        //クイズのタグセッティングはActivityでもよかったかもしれない。Fragmentが使いたかったのはViewPagerのためだけだし。
        RandomCheckBox = view.findViewById(R.id.isRandomCheckBox);            RandomCheckBox.setOnClickListener(checkBoxClickListener);
        FavoriteCheckBox = view.findViewById(R.id.isFavoriteCheckBox);        FavoriteCheckBox.setOnClickListener(checkBoxClickListener);
        HumanCheckBox = view.findViewById(R.id.isHumanCheckBox);              HumanCheckBox.setOnClickListener(checkBoxClickListener);
        AnimeCheckBox = view.findViewById(R.id.isAnimeCheckBox);              AnimeCheckBox.setOnClickListener(checkBoxClickListener);
        SingerCheckBox = view.findViewById(R.id.isSingerCheckBox);            SingerCheckBox.setOnClickListener(checkBoxClickListener);
        EntertainerCheckBox = view.findViewById(R.id.isEntertainerCheckBox);  EntertainerCheckBox.setOnClickListener(checkBoxClickListener);
        IdolCheckBox = view.findViewById(R.id.isIdolCheckBox);                IdolCheckBox.setOnClickListener(checkBoxClickListener);
        AthleteCheckBox = view.findViewById(R.id.isAthleteCheckBox);          AthleteCheckBox.setOnClickListener(checkBoxClickListener);
        RemoveNicheCheckBox = view.findViewById(R.id.isRemoveNicheCheckBox);  RemoveNicheCheckBox.setOnClickListener(checkBoxClickListener);
        OnlyNicheCheckBox = view.findViewById(R.id.isOnlyNicheCheckBox);      OnlyNicheCheckBox.setOnClickListener(checkBoxClickListener);
        BaseballCheckBox = view.findViewById(R.id.isBaseballCheckBox);        BaseballCheckBox.setOnClickListener(checkBoxClickListener);
        FootballCheckBox = view.findViewById(R.id.isFootballCheckBox);        FootballCheckBox.setOnClickListener(checkBoxClickListener);

        reflectOldConfig(); //前使用時のタグ設定を反映

        hitNumText = view.findViewById(R.id.numberOfHit);
        hitNumText.setText(String.valueOf(hitNumberCheck(createTagList()))); //タグに合致する問題の件数を表示


        spinner = view.findViewById(R.id.questionNumSpinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { //出題する問題数を選択するためのSpinner
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                questionNumStr = (String)parent.getAdapter().getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //クイズ開始ボタンへ ClickListener をセット
        view.findViewById(R.id.set_ok_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuiz();
            }
        });
    }

    private void startQuiz(){
        //「クイズ開始」ボタンが押されたときの動作
        // 「前回のタグ設定を反映する」機能の為に config.txt というファイルにタグ設定を記録している。
        // それが完了したら QuizActivity へ遷移する。

        //File tagConfig = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/tagConfig.txt"); //デバッグのため外部へ
        File tagConfig = new File(getContext().getFilesDir(),"/tagConfig.txt");
        tagConfig.delete();
        try{ //catch(Exception e)で大雑把にまとめて捕捉したら、こんなにtry-catchを書く必要はない でも一応
            FileOutputStream fos = new FileOutputStream(tagConfig);
            try{
                OutputStreamWriter osw = new OutputStreamWriter(fos,"UTF-8");
                BufferedWriter bw = new BufferedWriter(osw);

                try{
                    bw.write("Num:" + questionNumStr + "\n");

                    if(RandomCheckBox.isChecked()) bw.write("Ran:1\n");
                    else bw.write("Ran:0\n");

                    if(FavoriteCheckBox.isChecked()) bw.write("Fav:1\n");
                    else bw.write("Fav:0\n");

                    if(HumanCheckBox.isChecked()) bw.write("Hum:1\n");
                    else bw.write("Hum:0\n");

                    if(AnimeCheckBox.isChecked()) bw.write("Ani:1\n");
                    else bw.write("Ani:0\n");

                    if(SingerCheckBox.isChecked()) bw.write("Sin:1\n");
                    else bw.write("Sin:0\n");

                    if(EntertainerCheckBox.isChecked()) bw.write("Ent:1\n");
                    else bw.write("Ent:0\n");

                    if(IdolCheckBox.isChecked()) bw.write("Ido:1\n");
                    else bw.write("Ido:0\n");

                    if(AthleteCheckBox.isChecked()) bw.write("Ath:1\n");
                    else bw.write("Ath:0\n");

                    if(RemoveNicheCheckBox.isChecked()) bw.write("RNi:1\n");
                    else bw.write("RNi:0\n");

                    if(OnlyNicheCheckBox.isChecked()) bw.write("ONi:1\n");
                    else bw.write("ONi:0\n");

                    if(BaseballCheckBox.isChecked()) bw.write("Bse:1\n");
                    else bw.write("Bse:0\n");

                    if(FootballCheckBox.isChecked()) bw.write("Fot:1\n");
                    else bw.write("Fot:0\n");

                    bw.flush();
                    osw.close();
                    bw.close();

                }catch (IOException e){
                    Log.e("Buffered Writer ERROR:",e.toString());
                }
                fos.close();

            }catch(UnsupportedEncodingException e){
                Log.e("Encoding ERROR:",e.toString());
            }catch (IOException e2){
                Log.e("fos close IO ERROR:",e2.toString());
            }

        }catch(FileNotFoundException e){ //FileOutputStreamのチェック例外
            Log.e("File not found ERROR:",e.toString());
            try{
                tagConfig.createNewFile();
            }catch(IOException e2){
                Log.e("createNewFile ERROR:",e2.toString());
            }
        }
        Intent intent = new Intent(getContext(), com.example.memory_test3.QuizActivity.class);
        startActivity(intent); //QuizActivityへ遷移
    }


    private View.OnClickListener checkBoxClickListener = new View.OnClickListener() { //チェックボックスへセットするクリックリスナー
        @Override
        public void onClick(View view) {    //viewはクリックされたコンポーネント（この場合はチェックボックス）を指す
            boolean checked = ((CheckBox)view).isChecked();
            switch(view.getId()){           //レイアウトのxmlファイルにおいてチェックボックスごとに割り振られているIdで どれが押されたかを判定
                case R.id.isRandomCheckBox:
                    if(checked){
                        FavoriteCheckBox.setChecked(false);
                        HumanCheckBox.setChecked(false);
                        AnimeCheckBox.setChecked(false);
                        SingerCheckBox.setChecked(false);
                        EntertainerCheckBox.setChecked(false);
                        IdolCheckBox.setChecked(false);
                        AthleteCheckBox.setChecked(false);
                        RemoveNicheCheckBox.setChecked(false);
                        OnlyNicheCheckBox.setChecked(false);
                        BaseballCheckBox.setChecked(false);
                        FootballCheckBox.setChecked(false);
                    }
                    break;

                case R.id.isFavoriteCheckBox:
                case R.id.isEntertainerCheckBox:
                case R.id.isIdolCheckBox:
                    if(checked){
                        RandomCheckBox.setChecked(false);
                    }
                    break;

                case R.id.isHumanCheckBox:
                    if(checked){
                        RandomCheckBox.setChecked(false);
                        AnimeCheckBox.setChecked(false);
                    }
                    break;

                case R.id.isAnimeCheckBox:
                    if(checked){
                        RandomCheckBox.setChecked(false);
                        HumanCheckBox.setChecked(false);
                        SingerCheckBox.setChecked(false);
                        EntertainerCheckBox.setChecked(false);
                        IdolCheckBox.setChecked(false);
                        BaseballCheckBox.setChecked(false);
                        FootballCheckBox.setChecked(false);
                    }
                    break;

                case R.id.isSingerCheckBox:
                    if(checked){
                        RandomCheckBox.setChecked(false);
                        AthleteCheckBox.setChecked(false);
                        BaseballCheckBox.setChecked(false);
                        FootballCheckBox.setChecked(false);
                    }
                    break;

                case R.id.isAthleteCheckBox:
                    if(checked){
                        RandomCheckBox.setChecked(false);
                        SingerCheckBox.setChecked(false);
                    }
                    break;

                case R.id.isRemoveNicheCheckBox:
                    if(checked){
                        RandomCheckBox.setChecked(false);
                        OnlyNicheCheckBox.setChecked(false);
                    }
                    break;

                case R.id.isOnlyNicheCheckBox:
                    if(checked){
                        RandomCheckBox.setChecked(false);
                        RemoveNicheCheckBox.setChecked(false);
                    }
                    break;

                case R.id.isBaseballCheckBox:
                    if(checked){
                        RandomCheckBox.setChecked(false);
                        AnimeCheckBox.setChecked(false);
                        SingerCheckBox.setChecked(false);
                        EntertainerCheckBox.setChecked(false);
                        IdolCheckBox.setChecked(false);
                        FootballCheckBox.setChecked(false);
                    }
                    break;

                case R.id.isFootballCheckBox:
                    if(checked){
                        RandomCheckBox.setChecked(false);
                        AnimeCheckBox.setChecked(false);
                        SingerCheckBox.setChecked(false);
                        EntertainerCheckBox.setChecked(false);
                        IdolCheckBox.setChecked(false);
                        BaseballCheckBox.setChecked(false);
                    }
                    break;
            }
            //チェックボックスのクリックを踏まえ、タグに該当する問題の件数を更新
            hitNumText.setText(String.valueOf(hitNumberCheck(createTagList())));
        }
    };


    private void reflectOldConfig(){ //前回 書き込んだ設定ファイルを参照して反映させる
        File tagConfig = new File(getContext().getFilesDir(),"/tagConfig.txt");
        if(tagConfig.exists()) {
            try {
                FileInputStream fis = new FileInputStream(tagConfig);
                InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                String tmp;
                String qNum = null;
                while ((tmp = br.readLine()) != null) {
                    if (tmp.contains("Num")) {
                        qNum = tmp.substring(4);
                        //ここでspinner.setSelection((Integer.parseInt(qNum) / 5) - 1);を実行したら何故かタグの設定が反映されなかったので後ろへ
                    }

                    if (tmp.contains("Ran")) {
                        if ((int) (tmp.charAt(4)) == 49)
                            RandomCheckBox.setChecked(true); //半角数字1はUTF-8で0x31 つまり10進数49
                    }

                    if (tmp.contains("Fav")) {
                        if ((int) (tmp.charAt(4)) == 49) FavoriteCheckBox.setChecked(true);
                    }

                    if (tmp.contains("Hum")) {
                        if ((int) (tmp.charAt(4)) == 49) HumanCheckBox.setChecked(true);
                    }

                    if (tmp.contains("Ani")) {
                        if ((int) (tmp.charAt(4)) == 49) AnimeCheckBox.setChecked(true);
                    }

                    if (tmp.contains("Sin")) {
                        if ((int) (tmp.charAt(4)) == 49) SingerCheckBox.setChecked(true);
                    }

                    if (tmp.contains("Ent")) {
                        if ((int) (tmp.charAt(4)) == 49) EntertainerCheckBox.setChecked(true);
                    }

                    if (tmp.contains("Ido")) {
                        if ((int) (tmp.charAt(4)) == 49) IdolCheckBox.setChecked(true);
                    }

                    if (tmp.contains("Ath")) {
                        if ((int) (tmp.charAt(4)) == 49) AthleteCheckBox.setChecked(true);
                    }

                    if (tmp.contains("RNi")) {
                        if ((int) (tmp.charAt(4)) == 49) RemoveNicheCheckBox.setChecked(true);
                    }

                    if (tmp.contains("ONi")) {
                        if ((int) (tmp.charAt(4)) == 49) OnlyNicheCheckBox.setChecked(true);
                    }

                    if (tmp.contains("Bse")) {
                        if ((int) (tmp.charAt(4)) == 49) BaseballCheckBox.setChecked(true);
                    }

                    if (tmp.contains("Fot")) {
                        if ((int) (tmp.charAt(4)) == 49) FootballCheckBox.setChecked(true);
                    }
                }
                spinner.setSelection((Integer.parseInt(qNum) / 5) - 1); //問題数をスピナーのどこが選択されているかを示すindexに直して格納
                fis.close();
                isr.close();
                br.close();
            } catch (Exception e) {
                Log.e("reflectOldConfig ERROR", e.toString());
            }
        }
    }


    private int hitNumberCheck(ArrayList<String> tagList){ //該当件数を把握するための関数
        //タグ「全て」はtagList = nullとして扱う
        int hitCount = 0;
        try{
            File inputFile = new File(getContext().getFilesDir(),"/input.csv");
            FileInputStream fis = new FileInputStream(inputFile);
            InputStreamReader isr = new InputStreamReader(fis,"SHIFT-JIS");
            BufferedReader br = new BufferedReader(isr);

            String tmp;
            boolean tagFlag;
            boolean isRemoveNicheFlag = tagList.contains("ニッチな問題を除く");
            if(isRemoveNicheFlag) tagList.remove("ニッチな問題を除く");


            //読み込むファイルはinput.csv（内部ストレージにコピーしたクイズ問題のリスト）で、
            //それら一問ずつ（一行に相当）について、選択したタグ条件を満たすかどうかを判定する。
            //br.readLine()で一行ずつファイルを読み込む ファイルの最後まで行くとnullを返すので止まる。
            //最終的に tagFrag がtrueならその問題は該当問題としてカウントされる。
            while((tmp = br.readLine()) != null){
                tagFlag = true;
                if(isRemoveNicheFlag){
                    if(tmp.contains("ニッチ")) tagFlag = false;
                }
                for(String str: tagList){
                    tagFlag = tagFlag && (tmp.contains(str));
                    //一度でもtmp.contains(str)が falseになれば（その問題の行に選択したタグが含まれていないことがあれば）、
                    //tagFragは falseとなる
                }
                if(tagFlag) hitCount++;
            }
            fis.close(); isr.close(); br.close();
        }catch(Exception e){
            Log.e("hitNumberCheck error",e.toString());
            Toast.makeText(getContext(),"hitNumberCheck() error",Toast.LENGTH_LONG).show();
        }
        return hitCount;

        //「ニッチな問題を除く」という条件のみ別処理になっているのは、他のタグと違って
        // 「ニッチな問題を除く」という名前でニッチではない問題をタグ登録していないからである
        //（一部の問題がニッチなだけであって、それ以外に全て「ニッチではない」というタグをつけるのは憚られた。
        // また、「ニッチではない」という名前でタグ登録すると、その行を「ニッチ」が含まれるかで判定すると trueになり
        // 面倒だった）。
    }


    private ArrayList<String> createTagList(){ //どのチェックボックスがチェックされているかを調べて、該当するタグをArrayListに入れる
        ArrayList<String> tagList = new ArrayList<>();

        if(!RandomCheckBox.isChecked()) {
            if(FavoriteCheckBox.isChecked()) tagList.add("@@");
            if(HumanCheckBox.isChecked()) tagList.add("人物");
            if(AnimeCheckBox.isChecked()) tagList.add("アニメ");
            if(SingerCheckBox.isChecked()) tagList.add("歌手");
            if(EntertainerCheckBox.isChecked()) tagList.add("芸人");
            if(IdolCheckBox.isChecked()) tagList.add("アイドル");
            if(AthleteCheckBox.isChecked()) tagList.add("スポーツ選手");
            if(RemoveNicheCheckBox.isChecked()) tagList.add("ニッチな問題を除く");
            if(OnlyNicheCheckBox.isChecked()) tagList.add("ニッチ");
            if(BaseballCheckBox.isChecked()) tagList.add("野球");
            if(FootballCheckBox.isChecked()) tagList.add("サッカー");
        }
        return tagList;
    }
}
