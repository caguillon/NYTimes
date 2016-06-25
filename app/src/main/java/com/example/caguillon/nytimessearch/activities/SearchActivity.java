package com.example.caguillon.nytimessearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.caguillon.nytimessearch.Article;
import com.example.caguillon.nytimessearch.ArticleArrayAdapter;
import com.example.caguillon.nytimessearch.EndlessScrollListener;
import com.example.caguillon.nytimessearch.R;
import com.example.caguillon.nytimessearch.SearchFilters;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends AppCompatActivity {
    String query;
    //EditText etQuery;
    GridView gvResults;
    //Button btnSearch;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    //new instance of SearchFilters
    SearchFilters filters;

    //To test flow and see if it's being connected to FilterActivity
    public static final int AGE_REQUEST_CODE = 55;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setupViews();

        //endless scrolling...
        // Attach the listener to the AdapterView onCreate
        gvResults.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                customLoadMoreDataFromApi(query, page);
                // or customLoadMoreDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });
    }

    public void setupViews(){
        //etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);
        //btnSearch = (Button) findViewById(R.id.btnSearch);
        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(adapter);
        //new instance of SearchFilters
        filters = new SearchFilters();

        //hook up listener for grid click
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //create an intent to display the article
                Intent i = new Intent(getApplicationContext(), ArticleActivity.class);

                //get the article to display
                Article article = articles.get(position);

                //pass in that article into intent
                i.putExtra("article", article);

                //launch the activity
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        /*getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;*/

        //this is hooking up the listener for when a search is performed
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                loadArticles(query, 0);

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        //int id = item.getItemId();

        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                //Toast.makeText(this, "Clicked on settings!", Toast.LENGTH_SHORT).show();
                launchFilterActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);

            //noinspection SimplifiableIfStatement
            //action_search instead of action_settings?

            //return super.onOptionsItemSelected(item);
        }
    }

    private void launchFilterActivity(){
        Intent intent = new Intent(this, FilterActivity.class);

        startActivityForResult(intent, AGE_REQUEST_CODE);
    }

    public void loadArticles(String query, int page){
        if(page == 0){
            adapter.clear();
            /*
            Does the same as code above:
            articles.clear();
            adapter.notifyDataSetChanged();
            */
        }

        //String query = etQuery.getText().toString();
        setQuery(query);

        //Toast.makeText(this, "Searching for " + query, Toast.LENGTH_LONG).show();
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.nytimes.com/svc/search/v2/articlesearch.json";

        RequestParams params = new RequestParams();
        params.put("api-key", "8550f1bf034645e784a177b32055d153");
        params.put("page", page);
        params.put("q", query);
        //begin_date=20160112&sort=oldest&fq=news_desk:("Education"%20"Health")
        if(filters.getBegin_date() != null) {
            params.put("begin_date", filters.getBegin_date());
        }
        if(filters.getSort() != null) {
            params.put("sort", filters.getSort());
        }
        if(filters.getNews_desk() != null){
            params.put("news_desk", filters.getNews_desk());
        }
        //^is news_desk correct?

        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray articleJsonResults = null;

                try{
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    adapter.addAll(Article.fromJSONArray(articleJsonResults));
                    /*
                    Does the same as code above:
                    articles.addAll(Article.fromJSONArray(articleJsonResults));
                    adapter.notifyDataSetChanged();
                    */
                    Log.d("DEBUG", articles.toString());
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }

    public void setQuery(String query){
        //the class variable -query declared earlier- value now equals the query passed through the method
        this.query = query;
    }

    /*public void onArticleSearch(View view) {
        //for a new search: out w/the old, in w/the new!
        loadArticles(0);
    }*/

    //endless scrolling...
    // Append more data into the adapter
    public void customLoadMoreDataFromApi(String query, int offset) {
        // This method probably sends out a network request and appends new data items to your adapter.
        // Use the offset value and add it as a parameter to your API request to retrieve paginated data.
        // Deserialize API response and then construct new objects to append to the adapter.

        loadArticles(query, offset);
    }

    //To test flow and see if it's being connected to FilterActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // requestCode = 55
        if(requestCode == AGE_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                String message = "Can pass data from Filter to Search Activity!";
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            } else{
                //Handle failure case
            }
        }
    }
}
