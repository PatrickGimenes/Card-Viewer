package ifsp.sjbv.edu.com.service;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ifsp.sjbv.edu.com.service.Card.Card;
//import ifsp.sjbv.edu.com.service.Card.CardService;

public class MainActivity extends AppCompatActivity {

    private EditText cardName;
    private TextView dataCard;
    private WebView imgCard;

    private Card card;

    private ProgressDialog load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cardName = (EditText) findViewById(R.id.edCardName);
        dataCard = (TextView) findViewById(R.id.txtCardDetails);
        imgCard = (WebView) findViewById(R.id.wvCard);
    }

    public void pesquisar (View v){
        CardService cs = new CardService();
        cs.execute(cardName.getText().toString());
    }
    private class CardService extends AsyncTask<String, Void, String> {


        @Override
        protected void onPreExecute() {
            load = ProgressDialog.show(MainActivity.this, "Por favor Aguarde ...",
                    "Procurando Dados ...");
        }

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            try {


                URL url = new URL("https://db.ygoprodeck.com/api/v7/cardinfo.php?name="+params[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(inputStream));


                String linha;
                StringBuffer buffer = new StringBuffer();
                while((linha = reader.readLine()) != null) {
                    buffer.append(linha);
                    buffer.append("\n");
                }
                card = convertJsonToObject(buffer.toString());

                String dados = "Dados da Carta:\nNome: "+card.getName()
                        +"\nDescrição: "+card.getDescription()
                        +"\n: Tipo"+card.getType()
                        +"\n: Nivel"+card.getLevel()
                        +"\n: Ataque:"+card.getAtk()
                        +"\n: Defesa"+card.getDef()
                        ;

                return dados;
            } catch (Exception e) {
                e.printStackTrace();
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String dados) {
            dataCard.setText(dados);
            imgCard.loadUrl(card.getImage());
            load.dismiss();
        }
    }
    public Card convertJsonToObject(String dados)
    {
        Card card = null;

        try {
            card = new Card();
            JSONObject jsonObj = new JSONObject(dados);
            JSONArray array = jsonObj.getJSONArray("data");
            JSONObject objArray = array.getJSONObject(0);
            card.setName(objArray.getString("name"));

            card.setDescription(objArray.getString("desc"));
            card.setLevel(objArray.getInt("level"));
            card.setType(objArray.getString("type"));
            card.setAtk(objArray.getInt("atk"));
            card.setDef(objArray.getInt("def"));
            card.setImage(objArray.getString("https://images.ygoprodeck.com/images/cards_cropped/46986421.jpg"));




        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return card;

    }
}