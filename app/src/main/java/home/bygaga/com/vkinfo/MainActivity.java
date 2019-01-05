package home.bygaga.com.vkinfo;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import static home.bygaga.com.vkinfo.utils.NetworkUtils.generateURL;
import static home.bygaga.com.vkinfo.utils.NetworkUtils.getResponseFromUrl;

public class MainActivity extends AppCompatActivity {

    private EditText searchField;
    private Button searchButton;
    private TextView result;
    private TextView errorMessage;
    private ProgressBar loadingIndicator;

    private void showResultTextView() {
        result.setVisibility(View.VISIBLE);
        errorMessage.setVisibility(View.INVISIBLE);
    }

    private void showErrorTextView() {
        result.setVisibility(View.INVISIBLE);
        errorMessage.setVisibility(View.VISIBLE);
    }

    private void showProgressBar() {
        loadingIndicator.setVisibility(View.VISIBLE);
    }

    class VKQueryTask extends AsyncTask<URL, Void, String> {
        @Override
        protected void onPreExecute() {
            showProgressBar();
        }

        @Override
        protected String doInBackground(URL... urls) {
            String response = null;
            try {
                response = getResponseFromUrl(urls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return response;
        }

        @Override
        protected void onPostExecute(String response) {
            String firstName = null;
            String lastName = null;

            if (response != null && !response.equals("")) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("response");

                    String resultingString = "";
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject userInfo = jsonArray.getJSONObject(i);

                        firstName = userInfo.getString("first_name");
                        lastName = userInfo.getString("last_name");
                        resultingString += "Имя: " + firstName + "\n" + "Фамилия: " + lastName + "\n\n";
                        result.setText(resultingString);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                showResultTextView();
            } else {
                showErrorTextView();
            }
            loadingIndicator.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchField = findViewById(R.id.et_search_field);
        searchButton = findViewById(R.id.b_search_vk);
        result = findViewById(R.id.tv_result);
        errorMessage = findViewById(R.id.tv_error_message);
        loadingIndicator = findViewById(R.id.pb_loading_indicator);

        View.OnClickListener onClickListener = v -> {
            URL generatedURL = generateURL(searchField.getText().toString());
            new VKQueryTask().execute(generatedURL);

        };

        searchButton.setOnClickListener(onClickListener);
    }
}
