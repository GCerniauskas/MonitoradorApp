package vox.com.br.services;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

import java.util.Calendar;
import java.util.List;

import vox.com.br.dao.AppAbertoDao;
import vox.com.br.model.AppAberto;

public class MyAccessibilityService extends AccessibilityService {

    // Quando acontecer um Evento de acessibilidade, esse código é executado
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

        Log.e(TAG, "onAccessibilityEvent: ");

        // Resgando oq está sendo digitado pelo usuário em um TextEdit
        List<CharSequence> textoDigitado = List.of();
        if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            textoDigitado = accessibilityEvent.getText();

            Log.e(TAG, "onAccessibilityEvent: " + textoDigitado);
        }

        // Pegando uma Instância do AppAbertoDao para receber e popular os dados
        AppAbertoDao appAbertoDao = new AppAbertoDao();




        // Aqui pegamos o nome do pacote do evento estamos usando: (TYPE_WINDOW_STATE_CHANGED)
        String nomeDoApp = accessibilityEvent.getPackageName().toString();

        // Resgatando o Horário com Calendar
        Calendar now = Calendar.getInstance();
        String horarioDoOcorrido = now.get(Calendar.HOUR_OF_DAY) + ":" + now.get(Calendar.MINUTE) + ":" + now.get(Calendar.SECOND);

        PackageManager packageManager = this.getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");

        try {

            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(nomeDoApp, 0);
            CharSequence applicationLabel = packageManager.getApplicationLabel(applicationInfo);

            // Criando e salvando um App Aberto
            AppAberto appAbertoAtual = new AppAberto(applicationLabel.toString(), horarioDoOcorrido, now, textoDigitado);
            appAbertoDao.salvar(appAbertoAtual);

            // Resgando o último app aberto do historico para comparar depois
            if (appAbertoDao.getTodos().get(appAbertoDao.getTodos().size() - 1) != null) { // verificando se foi inicializada
                AppAberto ultimoAppAberto = appAbertoDao.getTodos().get(appAbertoDao.getTodos().size() - 1); // pegando o último item da lista getTodos()
                // Foi criada e populada uma lista de historico, se último item do historico é diferente do appAberto agora preciso atualizar o horário
                if (ultimoAppAberto != null && appAbertoAtual != null) { // se não for nulo
                    Log.e(TAG, "onAccessibilityEvent: " +  appAbertoDao.getTodos().get(appAbertoDao.getTodos().size() - 1) );
                    if (ultimoAppAberto == appAbertoAtual) {
                        appAbertoDao.atualizarHorario(ultimoAppAberto, appAbertoAtual);
                    }
                }
            }

            Log.e(TAG, "onAccessibilityEvent: nome do app é: " + applicationLabel + " Horário: " + horarioDoOcorrido);

        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }




    }

    @Override
    public void onInterrupt() {
        Log.e(TAG, "onInterrupt: Algo deu errado");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        // Eventos de acessibilidade
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes =
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED;

        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;

        info.notificationTimeout = 100;

        this.setServiceInfo(info);

        // Quando uma conexão com o serviço foi estabelecida (variável para manipular isso?)
        Log.e(TAG, "onServiceConnected: ");
    }
}
