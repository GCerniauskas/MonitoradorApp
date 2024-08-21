package vox.com.br;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import vox.com.br.activity.InfoScreen;
import vox.com.br.dao.AppAbertoDao;
import vox.com.br.listener.MyNotificationListener;
import vox.com.br.services.MyAccessibilityService;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Lista de aplicativos usados");

        Button permitirPermissoes = findViewById(R.id.button);
        // OnClick permitirPermissoes
        permitirPermissoes.setOnClickListener(view -> checkRequestAccessNotification());
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

    // Permissão de notificação
    private final ActivityResultLauncher<Intent> notificationResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->
                    checkRequestAccessNotification());

    private void checkRequestAccessNotification() {
        String enabledNotificationListeners = Settings.Secure.getString(this.getContentResolver(), "enabled_notification_listeners");
        if (enabledNotificationListeners == null || !enabledNotificationListeners.contains(getApplicationContext().getPackageName())) {
            requestAccessNotification();
        } else {
            Intent notificationIntent = new Intent(this, MyNotificationListener.class);
            this.startService(notificationIntent);
            checkRequestAccessibilityService();
        }
    }

    private void requestAccessNotification() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.enable_access_notification)
                .setMessage(R.string.enable_access_notification_dialog_desc)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                    notificationResultLauncher.launch(intent);
                })
                .show();
    }

    // Permissão de acessibilidade
    private final ActivityResultLauncher<Intent> accessibilityServiceResultLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result ->
                    checkRequestAccessibilityService());

    private void checkRequestAccessibilityService() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (!isAccessibilityServiceEnabled()) {
                requestAccessibilityService();
            }
        }
    }

    private boolean isAccessibilityServiceEnabled() {
        MyAccessibilityService myAccessibilityService = new MyAccessibilityService();
        return myAccessibilityService.isAccessibilityServiceEnabled();
    }


    private void requestAccessibilityService() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.enable_service_dialog_title)
                .setMessage(R.string.enable_service_dialog_desc)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    accessibilityServiceResultLauncher.launch(intent);
                })
                .show();
    }

}