package com.trickstival.visualizadordelucro;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public static URL URL_BTC;

    private static String cotacao;

    private TextView txtBTC, txtRS, txtCotacao;

    private static class CotacaoTask extends AsyncTask {


        @Override
        protected Object doInBackground(Object[] objects) {

            MainActivity self = (MainActivity) objects[0];

            try {
                //Enviando requisição
                URL_BTC = new URL("https://api.bitvalor.com/v1/ticker.json");
                HttpURLConnection con = (HttpURLConnection) URL_BTC.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("Content-Type", "application/json");

                //Recebendo resposta
                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuffer buffer = new StringBuffer();
                String line;

                while((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                reader.close();

                cotacao = getCotacao(buffer.toString());


            } catch (MalformedURLException e) {
                return null;
            } catch (IOException e) {
                return null;
            }

            return self;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if(o == null) return;
            ((MainActivity) o).mostrarCotacao();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initData();
        initComponents();

    }

    private void initData(){
        getCotacaoTask().execute(this);
    }

    private static CotacaoTask getCotacaoTask(){
        return new CotacaoTask();
    }

    private void mostrarCotacao(){
        String quantia = "0.001";
        txtCotacao.setText("BRL " + new BigDecimal(cotacao).setScale(2, BigDecimal.ROUND_UP));
        txtBTC.setText("BTC " + quantia);
        txtRS.setText("BRL " + new BigDecimal(cotacao).multiply(new BigDecimal(quantia)).setScale(2, BigDecimal.ROUND_UP));
    }


    private static String getCotacao(String bufferResult){
        JSONObject json;
        try {
            json = new JSONObject(bufferResult);
            return json.getJSONObject("ticker_24h").getJSONObject("total").getString("vwap");
        } catch (JSONException e) {
            return null;
        }
    }

    public void atualizar(View btn){
        getCotacaoTask().execute(this);
    }

    private void initComponents(){
        txtBTC = findViewById(R.id.txtBTC);
        txtRS = findViewById(R.id.txtRS);
        txtCotacao = findViewById(R.id.txtCotacao);
    }
}
