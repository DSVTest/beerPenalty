package com.mytoys.android.beerpenalty;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.amazonaws.amplify.generated.graphql.ListPenaltysQuery;
import com.amazonaws.mobileconnectors.appsync.fetcher.AppSyncResponseFetchers;
import com.apollographql.apollo.GraphQLCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.amazonaws.mobileconnectors.lambdainvoker.*;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;

import java.util.ArrayList;

import javax.annotation.Nonnull;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    MyAdapter mAdapter;

    private ArrayList<ListPenaltysQuery.Item> mTodos;
    private final String TAG = MainActivity.class.getSimpleName();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recycler_view);

        // use a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(this, new NameListener() {
            @Override
            public void nameSelected(String name) {
                beerAdd(name);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fabRefresh = (FloatingActionButton) findViewById(R.id.refresh);
        fabRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                query();
            }
        });

        ClientFactory.init(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        query();
    }

    public void query(){
        ClientFactory.appSyncClient().query(ListPenaltysQuery.builder().build())
                .responseFetcher(AppSyncResponseFetchers.CACHE_AND_NETWORK)
                .enqueue(queryCallback);
    }

    @SuppressLint("StaticFieldLeak")
    public void beerAdd(String name) {
        CognitoCachingCredentialsProvider cognitoProvider = new CognitoCachingCredentialsProvider(
                this.getApplicationContext(), "eu-central-1:38b1bdf2-319c-4b67-9a3c-819fba667a69", Regions.EU_CENTRAL_1);

        LambdaInvokerFactory factory = new LambdaInvokerFactory(this.getApplicationContext(),
                Regions.EU_CENTRAL_1, cognitoProvider);

        final LambdaInterface myInterface = factory.build(LambdaInterface.class);

        Name penalty = new Name(name);

        new AsyncTask<Name, Void, String>() {
            @Override
            protected String doInBackground(Name... params) {
                try {
                    return myInterface.addBeer(params[0]);
                } catch (LambdaFunctionException lfe) {
                    Log.e(TAG, "Failed to invoke echo", lfe);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                query();
            }
        }.execute(penalty);
    }

    private GraphQLCall.Callback<ListPenaltysQuery.Data> queryCallback = new GraphQLCall.Callback<ListPenaltysQuery.Data>() {
        @Override
        public void onResponse(@Nonnull Response<ListPenaltysQuery.Data> response) {

            mTodos = new ArrayList<>(response.data().listPenaltys().items());

            Log.i(TAG, "Retrieved list items: " + mTodos.toString());

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.setItems(mTodos);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        public void onFailure(@Nonnull ApolloException e) {
            Log.e(TAG, e.toString());
        }
    };
}
