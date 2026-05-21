package com.example.careerpilot;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class Connectcounselor extends AppCompatActivity {
    private MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        setContentView(R.layout.activity_connectcounselor);

        map = findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.getController().setZoom(13.0);
        map.getController().setCenter(new GeoPoint(-1.2921, 36.8219));

        addMarker(new GeoPoint(-1.2829, 36.7865), "Cedar Africa Group");
    }

    private void addMarker(GeoPoint p, String title) {
        Marker m = new Marker(map);
        m.setPosition(p);
        m.setTitle(title);
        m.setSnippet("Tap bubble to book appointment");

        m.setOnMarkerClickListener((marker, mapView) -> {
            Intent i = new Intent(Connectcounselor.this, BookAppointment.class);
            i.putExtra("COUNSELOR_NAME", marker.getTitle());
            startActivity(i);
            return true;
        });
        map.getOverlays().add(m);
        map.invalidate();
    }

    @Override protected void onResume() { super.onResume(); map.onResume(); }
    @Override protected void onPause() { super.onPause(); map.onPause(); }
}
