package vox.com.br;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import vox.com.br.activity.InfoScreen;
import vox.com.br.dao.AppAbertoDao;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Lista de aplicativos usados");

        Button permitirPermissoes = findViewById(R.id.button);
        // OnClick permitirPermissoes
        permitirPermissoes.setOnClickListener(view -> startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppAbertoDao appAberto = new AppAbertoDao();
        ListView listView = findViewById(R.id.list_view);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appAberto.getTodos()));

        // On Click na lista
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            Log.e(TAG, "onItemClick: " + view);
            Intent intent = new Intent(MainActivity.this, InfoScreen.class);
            Bundle b = new Bundle();
            b.putInt("key", i);
            intent.putExtras(b);
            startActivity(intent);
        });
    }

}