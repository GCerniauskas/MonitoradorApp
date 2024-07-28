package vox.com.br;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.hardware.camera2.CameraExtensionSession;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Set;

import vox.com.br.activity.InfoScreen;
import vox.com.br.dao.AppAbertoDao;
import vox.com.br.model.AppAberto;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Lista de aplicativos usados");

        Button permitirPermissoes = findViewById(R.id.button);

        permitirPermissoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppAbertoDao appAberto = new AppAbertoDao();
        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appAberto.getTodos()));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e(TAG, "onItemClick: " +  view );
                Intent intent = new Intent(MainActivity.this, InfoScreen.class);
                Bundle b = new Bundle();
                b.putInt("key", i);
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }

}