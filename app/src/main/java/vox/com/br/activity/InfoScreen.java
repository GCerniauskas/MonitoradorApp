package vox.com.br.activity;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import vox.com.br.dao.AppAbertoDao;
import vox.com.br.model.AppAberto;

public class InfoScreen extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        // Resgatando parâmetro
        int idAppAberto = 0;
        Bundle b = getIntent().getExtras();
        if (b != null) {
            idAppAberto = b.getInt("key");
        }
        AppAberto appAberto = new AppAbertoDao().getAppById(idAppAberto);
        Log.e(TAG, "onCreate: " + appAberto );

    }


}
