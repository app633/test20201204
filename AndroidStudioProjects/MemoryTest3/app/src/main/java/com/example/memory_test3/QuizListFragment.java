package com.example.memory_test3;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link com.example.memory_test3.QuizListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuizListFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView.LayoutManager layoutManager;
    private MyAdapter myAdapter;


    public QuizListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QuizListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static com.example.memory_test3.QuizListFragment newInstance(String param1, String param2) {
        com.example.memory_test3.QuizListFragment fragment = new com.example.memory_test3.QuizListFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quiz_list, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<ArrayList<String>> quizAllList = setQuizList(null);
        myAdapter = new MyAdapter(quizAllList);


        RecyclerView recyclerView = view.findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true); //アダプターのコンテンツの大きさによってリサイクルビューの大きさが変わらないならtrueで最適化されるらしい

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(myAdapter); //RecyclerViewを継承したアダプターをセット
    }

    private ArrayList<ArrayList<String>> setQuizList(ArrayList<String> tagList){

        File inputFile = new File(getContext().getFilesDir(),"/input.csv");
        //File inputFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/input.csv");
        ArrayList<ArrayList<String>> quizList = new ArrayList<>();
        try{
            InputStreamReader isr = new InputStreamReader(new FileInputStream(inputFile),"SHIFT-JIS");
            BufferedReader br = new BufferedReader(isr);

            String tmp;
            boolean tagFlag;
            boolean isRemoveNicheFlag;
            if(tagList != null) isRemoveNicheFlag = tagList.contains("ニッチな問題を除く");
            else isRemoveNicheFlag = false;

            if(isRemoveNicheFlag) tagList.remove("ニッチな問題を除く");
            Log.e("isRemoveNiche",String.valueOf(isRemoveNicheFlag));

            br.readLine(); br.readLine(); //2行を読み飛ばす
            while((tmp = br.readLine()) != null){
                tagFlag = true;
                if (tagList != null) {
                    if(isRemoveNicheFlag){
                        if(tmp.contains("ニッチ")) tagFlag = false;
                    }
                    for (String str : tagList) {
                        //Log.i("str",str);
                        //Log.i("tmp.contains(str)",String.valueOf(tmp.contains(str)));
                        //Log.i("tagFlag",String.valueOf(tagFlag));
                        tagFlag = tagFlag && (tmp.contains(str)); //該当タグが全て含まれているかどうかを論理積を全てで取ることで判定する
                        //Log.i("tagFlagAfter",String.valueOf(tagFlag));
                    }
                }

                if(tagFlag) { //タグが全て一致すれば
                    ArrayList<String> tmpArray = new ArrayList<>();
                    int startNum = 0;
                    int endNum;
                    while ((endNum = tmp.indexOf(",", startNum)) != -1) { //","が見つからなくなるまで
                        String separatedStr = tmp.substring(startNum, endNum);
                        tmpArray.add(separatedStr);
                        startNum = endNum + 1;
                    }
                    tmpArray.add(tmp.substring(startNum));
                    quizList.add(tmpArray);
                }
            }
            isr.close();
            br.close();

        }catch(Exception e){
            AlertDialog.Builder inAlert = new AlertDialog.Builder(getActivity());
            inAlert.setTitle("クイズのリスト読み込みエラー \n" + e.toString());
            inAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            inAlert.setCancelable(false);
            inAlert.show();
        }

        if(quizList.size() == 0){
            Toast.makeText(getContext(),"該当問題が存在しません",Toast.LENGTH_LONG).show();
        }

        return quizList; //関数setQuizListの戻り値
    }



    class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder>{
        private ArrayList<ArrayList<String>> dataList;

        class MyViewHolder extends RecyclerView.ViewHolder{
            private TextView rvGroupName;
            private TextView rvName;
            private TextView rvPseudonym;
            private ImageView rvImage;
            private ImageButton rvFavorite;
            private int id;

            private MyViewHolder(View v) {
                super(v); //親クラス（RecyclerView.ViewHolder）のインスタンス生成時にコンストラクタへ引数を
                rvGroupName = v.findViewById(R.id.rvGroupName); //Activityじゃなくて、Viewの持つfindViewById()
                rvName = v.findViewById(R.id.rvName);
                rvPseudonym = v.findViewById(R.id.rvPseudonym);
                rvImage = v.findViewById(R.id.rvImage);
                rvFavorite = v.findViewById(R.id.rvFavorite);
            }
        }


        //コンストラクタ  引数の二重ArrayList<String>にはクイズのデータが渡される
        MyAdapter(ArrayList<ArrayList<String>> data){
            dataList = data;
        }


        //ViewHolderはリストの一列分のデータを格納されるView   RecyclerViewでは画面外へ外れたViewHolderに新たにデータを格納し直しているらしい
        //そのため、リストの全要素分のViewHolderが生成されているわけではない。
        //リスト一列分のレイアウトは、xmlファイルで作ったものからinflaterで生成して、ViewHolderのコンストラクタへ渡す
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            //↑内部でLayoutInflater inflater = (LayoutInflater) context.SystemService(Context.LAYOUT_INFLATER_SERVICE);を呼んで返しているらしい
            View inflateView = inflater.inflate(R.layout.list_layout, parent,false);
            //inflater.inflate(int resource, ViewGroup root, boolean attachToRoot) attachToRootがtrueだとViewGroup rootがルートになる
            MyViewHolder myViewHolder = new MyViewHolder(inflateView);
            return myViewHolder;
        }


        //ViewHolderにデータを紐づけするメソッド。
        //RecyclerViewが自動で呼んでいるので深くは分からない。
        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position){ //positionはデータを紐づけするViewHolderの番号
            holder.rvName.setText(dataList.get(position).get(1));
            holder.rvPseudonym.setText(dataList.get(position).get(2));
            holder.rvGroupName.setText(dataList.get(position).get(3));
            holder.id = Integer.valueOf(dataList.get(position).get(0));
            if(dataList.get(position).get(7).contains("@@")) {
                //Log.e("INFO","@@");
                //Log.e("INFO",String.valueOf(R.drawable.favorite));
                holder.rvFavorite.setBackgroundResource(R.drawable.a_favorite);
            }else {
                //Log.e("INFO","NO");
                //Log.e("INFO",String.valueOf(R.drawable.not_favorite));
                holder.rvFavorite.setBackgroundResource(R.drawable.a_not_favorite);
            }
            holder.rvFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rvFavoriteButtonClick(holder.id,v); //お気に入り登録
                }
            });

            //ファイル名に合致するファイルが リソースフォルダ drawable内に存在すれば、そのidを取得し、画像をImageViewへ表示できる。
            int id = getContext().getResources().
                    getIdentifier(dataList.get(position).get(4),"drawable",getContext().getPackageName());
            holder.rvImage.setImageResource(id);
            //Log.e("id",String.valueOf(id));

            //画像がリソースフォルダにない場合はアプリの外部ファイルにないかを参照する（ユーザーが問題を作成するときに追加することを想定していた）
            if(id == 0){
                String path = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" +dataList.get(position).get(4) + ".jpg";
                //Log.e("path",path);
                Bitmap bmp = BitmapFactory.decodeFile(path);
                if(bmp == null){
                    path = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" +dataList.get(position).get(4) + ".png";
                    bmp = BitmapFactory.decodeFile(path);
                }
                holder.rvImage.setImageBitmap(bmp);
                if(bmp != null) holder.rvImage.setImageBitmap(bmp);
            }
            //ImageAsyncTask async = new ImageAsyncTask(holder.rvImage);
            //async.execute(dataList.get(position).get(4));
        }


        @Override
        public int getItemCount(){
            return dataList.size();
        } //リストの全要素数を返すようにする（RecyclerViewが処理に利用する）



        private void rvFavoriteButtonClick(int id, View v) { //お気に入り登録ボタンの動作
            ImageButton favoriteButton = (ImageButton)v;

            try {
                File inputFile = new File(getContext().getFilesDir(),"/input.csv");

                InputStreamReader isr = new InputStreamReader(new FileInputStream(inputFile), "SHIFT-JIS");
                BufferedReader br = new BufferedReader(isr);

                File tmpExFile = new File(getContext().getFilesDir(),"/tmpExFile.csv"); //一時的に書き写すため

                String tmp;
                FileOutputStream fos = new FileOutputStream(tmpExFile, true); //書き移すためのFileOutputStream
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos, "SHIFT-JIS"));
                int count = 0;
                while ((tmp = br.readLine()) != null) {
                    if (count != id + 1) {
                        Log.e("count",String.valueOf(count));
                        try {
                            bw.write(tmp + "\n");
                            bw.flush();
                        } catch (Exception e) {
                            Log.e("favButton writeERROR", e.toString());
                            Toast.makeText(getContext(), "favButton ERROR", Toast.LENGTH_LONG);
                        }
                    } else {
                        if (tmp.contains("@@")) {
                            Log.e("fav","@@");
                            favoriteButton.setBackgroundResource(R.drawable.a_not_favorite);
                            bw.write(tmp.substring(0, tmp.length() - 3) + "\n");
                            bw.flush();
                        } else {
                            Log.e("fav","@@");
                            favoriteButton.setBackgroundResource(R.drawable.a_favorite);
                            bw.write(tmp + "、@@\n");
                            bw.flush();
                        }
                    }
                    count++;
                }
                bw.close();
                fos.close();
                isr.close();
                br.close();

                inputFile.delete();
                tmpExFile.renameTo(new File(getContext().getFilesDir(),"/input.csv"));

            } catch (Exception e) {
                Log.e("favButton ERROR", e.toString());
                Toast.makeText(getContext(), "favButton ERROR", Toast.LENGTH_LONG);
            }
        }
    } //ここまでclass MyAdapter

}
