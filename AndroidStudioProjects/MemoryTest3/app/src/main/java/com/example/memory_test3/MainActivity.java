package com.example.memory_test3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File exFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + "/exFile.csv");
        if(!exFile.exists()){ //アプリの初回起動時のみ外部ストレージ上のファイルexFileを作成するため、リソースのrawフォルダoriginal.csvをコピーする
            try {
                Resources resources = this.getResources();
                InputStream is = resources.openRawResource(R.raw.original); //resフォルダ内ファイルの命名規則により半角英数のみ(おそらく大文字もアウト)
                InputStreamReader isr = new InputStreamReader(is,"UTF-8");
                BufferedReader br = new BufferedReader(isr);

                FileOutputStream fos = new FileOutputStream(exFile,true);
                OutputStreamWriter osw = new OutputStreamWriter(fos,"SHIFT-JIS");
                BufferedWriter bw = new BufferedWriter(osw);

                String tmp;
                while((tmp = br.readLine()) != null){
                    bw.write(tmp + "\n");
                    bw.flush();
                }
                is.close(); isr.close(); br.close();
                fos.close(); osw.close(); bw.close();
            }catch(Exception e){
                Log.e("exFile copy error",e.getMessage());
            }

            importCSV();
        }


        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_container, new MainFragment());
        fragmentTransaction.commit();
    }

    private void importCSV(){ //自分がエクセルで作ったCSVファイルが、携帯のアプリデータ（外部）の中に入っていたら読み込む
        try{
            File outFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + "/exFile.csv");
            Log.e("exFile",getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + "/exFile.csv");
            //FileReader fr = new FileReader(outFile);  これ使うと文字コードが指定できず文字化け

            //文字コードがUTF-8だとエクセルで開いたとき文字化けした
            InputStreamReader isr = new InputStreamReader(new FileInputStream(outFile),"SHIFT-JIS");
            BufferedReader br = new BufferedReader(isr); //BufferedReaderみたいなフィルタの引数は、なんとかReader

            //File inputFile = new File(MyApplication.getMyAppContext().getFilesDir(),"/input.csv"); ↓の書き方へ変更
            File inputFile = new File(MainActivity.this.getApplicationContext().getFilesDir(),"/input.csv"); //携帯繋げても通常見えない内部データ
            //File inputFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString() + "/input.csv"); //デバッグのため
            if(inputFile.exists()){ //既にinputFileが存在すれば
                inputFile.delete(); //古いファイルがあったら消去
            }

            FileOutputStream fos = new FileOutputStream(inputFile,true);

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos,"SHIFT-JIS"));
            String tmp;
            while((tmp = br.readLine()) != null) {
                try{
                    bw.write(tmp + "\n");
                    bw.flush();
                }catch(Exception e) {
                    AlertDialog.Builder inAlert = new AlertDialog.Builder(this);
                    inAlert.setTitle("importCSV ERROR in while try-catch  \n" + e.toString());
                    inAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    inAlert.setCancelable(false);
                    inAlert.show();
                }
            }
            bw.close();
            fos.close();
            isr.close();
            br.close();
            Toast.makeText(MainActivity.this.getApplicationContext(),"CSVファイルを読み込みました", Toast.LENGTH_SHORT).show();
        }catch(Exception e){
            AlertDialog.Builder inAlert = new AlertDialog.Builder(this);
            inAlert.setTitle(e.toString());
            inAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            inAlert.setCancelable(false);
            inAlert.show();
            Log.e("importCSV error",e.getMessage());
        }
    }

}