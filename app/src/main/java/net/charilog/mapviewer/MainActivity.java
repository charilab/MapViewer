package net.charilog.mapviewer;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.download.TileDownloadLayer;
import org.mapsforge.map.layer.download.tilesource.OnlineTileSource;
import org.mapsforge.map.layer.renderer.MapWorkerPool;
import org.mapsforge.map.reader.MapFile;
import org.mapsforge.map.scalebar.MetricUnitAdapter;

public class MainActivity extends AppCompatActivity {
    private MapView mapView;
    private TileDownloadLayer mapLayer;

    private TileDownloadLayer createBaseMapLayer() {
        TileCache tileCache = AndroidUtil.createTileCache(getBaseContext(), "charilab",
                mapView.getModel().displayModel.getTileSize(), 10f,
                mapView.getModel().frameBufferModel.getOverdrawFactor(), true);
        mapView.getModel().displayModel.setFixedTileSize(256);
        OnlineTileSource onlineTileSource = new OnlineTileSource(new String[]{
                "tile.charilog.net"}, 80);
        onlineTileSource.setName("Charilog").setAlpha(false)
                .setBaseUrl("/city/").setExtension("png")
                .setParallelRequestsLimit(8).setProtocol("http").setTileSize(256)
                .setZoomLevelMax((byte) 18).setZoomLevelMin((byte) 10);
        onlineTileSource.setUserAgent("Mapsforge Samples");
        mapLayer = new TileDownloadLayer(tileCache,
                mapView.getModel().mapViewPosition, onlineTileSource,
                AndroidGraphicFactory.INSTANCE);
        return mapLayer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidGraphicFactory.createInstance(this.getApplication());
        setContentView(R.layout.activity_main);

        MapWorkerPool.NUMBER_OF_THREADS = MapWorkerPool.DEFAULT_NUMBER_OF_THREADS;
        MapWorkerPool.DEBUG_TIMING = false;
        MapFile.wayFilterEnabled = true;
        MapFile.wayFilterDistance = 20;

        /* create map view */
        mapView = (MapView) findViewById(R.id.mapView);
        mapView.setClickable(true);
        mapView.getMapScaleBar().setVisible(true);
        mapView.setBuiltInZoomControls(true);
        mapView.setZoomLevelMin((byte) 10);
        mapView.setZoomLevelMax((byte) 18);
        AndroidUtil.setMapScaleBar(mapView, MetricUnitAdapter.INSTANCE, null);

        // only once a layer is associated with a mapView the rendering starts
        LatLong defaultLoc = new LatLong(35.126847, 138.909589);
        mapView.getLayerManager().getLayers().add(createBaseMapLayer());
        mapView.setCenter(defaultLoc);
        mapView.setZoomLevel((byte) 15);

        /* create floating buttons */
        ImageButton btnLoc = (ImageButton) findViewById(R.id.location);
        btnLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),
                        "Enable current Location", Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton btnMenu = (ImageButton) findViewById(R.id.menu);
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapLayer.onResume();
    }
}
