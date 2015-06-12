/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.hawkular.client.android.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ListView;

import org.hawkular.client.android.R;
import org.hawkular.client.android.adapter.MetricsAdapter;
import org.hawkular.client.android.backend.BackendClient;
import org.hawkular.client.android.backend.model.Metric;
import org.hawkular.client.android.backend.model.Resource;
import org.hawkular.client.android.backend.model.Tenant;
import org.hawkular.client.android.util.Intents;
import org.hawkular.client.android.util.ViewDirector;
import org.jboss.aerogear.android.pipe.callback.AbstractActivityCallback;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public final class MetricsActivity extends AppCompatActivity {
    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.list)
    ListView list;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_list);

        setUpBindings();

        setUpToolbar();

        setUpMetrics();
    }

    private void setUpBindings() {
        ButterKnife.inject(this);
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setUpMetrics() {
        showProgress();

        BackendClient.getInstance().getMetrics(getTenant(), getResource(), this, new MetricsCallback());
    }

    private void showProgress() {
        ViewDirector.of(this).using(R.id.animator).show(R.id.progress);
    }

    private Tenant getTenant() {
        return getIntent().getParcelableExtra(Intents.Extras.TENANT);
    }

    private Resource getResource() {
        return getIntent().getParcelableExtra(Intents.Extras.RESOURCE);
    }

    private void setUpMetrics(List<Metric> metrics) {
        list.setAdapter(new MetricsAdapter(this, metrics));

        hideProgress();
    }

    private void hideProgress() {
        ViewDirector.of(this).using(R.id.animator).show(R.id.list);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private static final class MetricsCallback extends AbstractActivityCallback<List<Metric>> {
        @Override
        public void onSuccess(List<Metric> metrics) {
            Timber.d("Metric type :: Success!");

            MetricsActivity activity = (MetricsActivity) getActivity();

            activity.setUpMetrics(metrics);
        }

        @Override
        public void onFailure(Exception e) {
            Timber.d("Metric type :: Failure...");
        }
    }
}
