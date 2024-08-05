package vox.com.br.services;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import vox.com.br.dao.AppAbertoDao;
import vox.com.br.dao.ChatWhatsDao;
import vox.com.br.model.AppAberto;
import vox.com.br.model.ChatWhats;
import vox.com.br.model.Message;

public class MyAccessibilityService extends AccessibilityService {

    // Foi mal pelo código ruim, com tempo pode ter certeza que vou melhorar (pelo menos está funcionando)
    // Quando acontecer um Evento de acessibilidade, esse código é executado
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

        Log.e(TAG, "onAccessibilityEvent: " + accessibilityEvent.getEventType());

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


        // Se o evento for do tipo TYPE_VIEW_CLICKED
        if (AccessibilityEvent.TYPE_VIEW_CLICKED == accessibilityEvent.getEventType()) {
            Log.e(TAG, "OnType_View_Clicked: ");

//            try {
//                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(nomeDoApp, 0);
//                CharSequence applicationLabel = packageManager.getApplicationLabel(applicationInfo);
//
//                if (applicationLabel.toString().equals("WhatsApp") && accessibilityEvent.getText().size() > 2) {
//                    Log.e(TAG, "OnTYPE_WINDOW_CONTENT_CHANGED: " + accessibilityEvent.getPackageName());
//
//                    AccessibilityNodeInfo nodeInfo = accessibilityEvent.getSource();
//                    // Pegando o ID
//                    long sourceNodeId = -1;
//                    try {
//                        Field sourceNodeIdField = AccessibilityNodeInfo.class.getDeclaredField("mSourceNodeId");
//                        sourceNodeIdField.setAccessible(true);
//                        sourceNodeId = sourceNodeIdField.getLong(nodeInfo);
//                    } catch (NoSuchFieldException | IllegalAccessException e) {
//                        e.printStackTrace();
//                    }
//                    ChatWhatsDao dao = new ChatWhatsDao();
//                    ChatWhats chatWhats = new ChatWhats(sourceNodeId, accessibilityEvent.getText().get(1).toString());
//
//                    dao.salvar(chatWhats);
//
//                }
//
//            } catch (PackageManager.NameNotFoundException e) {
//                throw new RuntimeException(e);
//            }


        }

        // Se o evento for do tipo TYPE_WINDOW_CONTENT_CHANGED -------------------------------------
        if (AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED == accessibilityEvent.getEventType()) {
            Log.e(TAG, "OnTYPE_WINDOW_CONTENT_CHANGED: ");
            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(nomeDoApp, 0);
                CharSequence applicationLabel = packageManager.getApplicationLabel(applicationInfo);

                //|| applicationLabel.toString().equals("Teams")
                if (applicationLabel.toString().equals("WhatsApp")) {
                    Log.e(TAG, "OnTYPE_WINDOW_CONTENT_CHANGED: " + accessibilityEvent.getPackageName());

                    // Verifica se está no chat para tentar resgatar as mensagens
                    if (verificaSeEstaNoChat(getRootInActiveWindow(), 1)) {
//                        logNodeHeirarchy(getRootInActiveWindow(), 0);
                        getChat(getRootInActiveWindow(),0);
                    }

                }

            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        // se o evento for do tipo TYPE_VIEW_TEXT_CHANGED ------------------------------------------
        if (AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED == accessibilityEvent.getEventType()) {
            // Resgatando o nome do App
            ApplicationInfo applicationInfo;
            try {
                applicationInfo = packageManager.getApplicationInfo(nomeDoApp, 0);
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }
            CharSequence applicationLabel = packageManager.getApplicationLabel(applicationInfo);

            // Resgando oq está sendo digitado pelo usuário em um TextEdit
            List<String> textoDigitado = new ArrayList<>();
            if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
                if (!accessibilityEvent.getText().isEmpty()) {
                    Log.e(TAG, "onAccessibilityEvent: " + accessibilityEvent.getText().get(0).toString());
                    textoDigitado.add(accessibilityEvent.getText().get(0).toString());

                    Log.e(TAG, "onAccessibilityEvent: " + textoDigitado);
                    AppAberto appAbertoAtual = new AppAberto(applicationLabel.toString(), horarioDoOcorrido, now, textoDigitado);
                    appAbertoDao.atualizarDigitados(appAbertoAtual);
                }

            }
        }

        // Se o evento for do tipo TYPE_WINDOW_STATE_CHANGED ---------------------------------------
        if (AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED == accessibilityEvent.getEventType()) {
            try {
                // Resgatando o nome do App
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(nomeDoApp, 0);
                CharSequence applicationLabel = packageManager.getApplicationLabel(applicationInfo);

                // Criando uma intância do AppAberto
                List<String> textoDigitado = new ArrayList<>();
                AppAberto appAbertoAtual = new AppAberto(applicationLabel.toString(), horarioDoOcorrido, now, textoDigitado);

                // Verificando se existe algo no historico (se não existe quer dizer que o app está iniciando agora)
                if (appAbertoDao.getTodosHistorico().isEmpty()) {
                    appAbertoDao.salvarNoHistorico(appAbertoAtual);
                    appAbertoDao.salvar(appAbertoAtual);
                } else if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
                    // nada pode acontecer pois o evento é para atualizar o que foi digitado
                } else { // Executado quando for e evento mudança no estado da tela, e não for a primeira vez iniciando

                    // Verificando se mudou o App, Aqui pego o último item no Historico e comparo o app atual.
                    // Se eles forem iguais quer dizer que houve uma mudança no estado da tela do mesmo app, e não precisamos fazer nada sobre pois estamos no msm app.
                    if (!Objects.equals(appAbertoDao.getTodosHistorico().get(appAbertoDao.getTodosHistorico().size() - 1).getNome(), appAbertoAtual.getNome())) {

                        appAbertoDao.salvarNoHistorico(appAbertoAtual);
                        appAbertoDao.salvar(appAbertoAtual);

                        // Resgando o último app aberto registrado para comparar depois
                        AppAberto ultimoAppAberto = appAbertoDao.getTodos().get(appAbertoDao.getTodos().size() - 1); // pegando o último item da lista getTodos()

                        // último item do historico deve ser diferente do ultimo app aberto (precisamos disso?)
                        if (!Objects.equals(appAbertoDao.getTodosHistorico().get(appAbertoDao.getTodosHistorico().size() - 1).getNome(), ultimoAppAberto.getNome())) {

                            // penultimo item do historico deve ser igual o ultimo app registrado
                            if (Objects.equals(appAbertoDao.getTodosHistorico().get(appAbertoDao.getTodosHistorico().size() - 2).getNome(), ultimoAppAberto.getNome())) {
                                appAbertoDao.atualizarHorario(ultimoAppAberto, appAbertoAtual);
                            }
                        }
                    }
                }
            } catch (
                    PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public void onInterrupt() {
        Log.e(TAG, "onInterrupt: Algo deu errado");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        AccessibilityServiceInfo info = getServiceInfo();

        info.flags |= AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS;
        setServiceInfo(info);

        info.eventTypes |=
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED |
                        AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED |
                        AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED |
                        AccessibilityEvent.TYPE_VIEW_CLICKED
        ;

        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;

        info.notificationTimeout = 100;

        this.setServiceInfo(info);

        // Quando uma conexão com o serviço foi estabelecida (variável para manipular isso?)
        Log.e(TAG, "onServiceConnected: ");
    }

    public static Boolean verificaSeEstaNoChat(AccessibilityNodeInfo nodeInfo, int depth) {

        if (nodeInfo.getChild(0) == null) return false;
        nodeInfo = nodeInfo.getChild(0);
        if (nodeInfo.getClassName().toString().equals("android.widget.LinearLayout") && nodeInfo.getChildCount() == 2 && depth == 6) return true;
        depth++;

        return verificaSeEstaNoChat(nodeInfo, depth);
    }

    public static void logNodeHeirarchy(AccessibilityNodeInfo nodeInfo, int depth) {

        if (nodeInfo == null) return;

        String logString = "";
        String className = nodeInfo.getClassName().toString();

        for (int i = 0; i < depth; ++i) {
            logString += " ";
        }

        logString += "Text: " + nodeInfo.getText() + " " + " Content-Description: " + nodeInfo.getContentDescription() + " ClassName: " + nodeInfo.getClassName().toString() + " Depth: " + depth;



        if (depth == 11 && className.equals("android.view.ViewGroup")) {
            Log.e(TAG, "logNodeHeirarchy: " + contar(nodeInfo) );
        }
        Log.e(TAG, "logNodeHeirarchy: " + logString );
        for (int i = 0; i < nodeInfo.getChildCount(); ++i) {
            logNodeHeirarchy(nodeInfo.getChild(i), depth + 1);
        }
    }

    // Conta o número de filhos de um nó
    public static int contar(AccessibilityNodeInfo nodeInfo) {

        if (nodeInfo == null) {
            return 0;
        }
        int contador = 1;

        for (int i = 0; i < nodeInfo.getChildCount(); ++i) {
            contador += contar(nodeInfo.getChild(i));
        }
        return contador;
    }

    public static ChatWhats nomeDoChat = new ChatWhats(0, "");

    // Lista de mensagens
    public static void getChat(AccessibilityNodeInfo nodeInfo, int depth) {
        if (nodeInfo == null) return;

        String className = nodeInfo.getClassName().toString();

        if (depth == 11 && className.equals("android.widget.FrameLayout") && nodeInfo.getChild(0).getClassName().toString().equals("android.widget.TextView") && nomeDoChat.getMensagens().isEmpty()) {
            AccessibilityNodeInfo chat = nodeInfo.getChild(0);

            // Pegando o ID
            long sourceNodeId = -1;
            try {
                Field sourceNodeIdField = AccessibilityNodeInfo.class.getDeclaredField("mSourceNodeId");
                sourceNodeIdField.setAccessible(true);
                sourceNodeId = sourceNodeIdField.getLong(chat);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            nomeDoChat.setNome(chat.getText().toString());
            nomeDoChat.setId(sourceNodeId);
            Log.e(TAG, "getChat: " + chat );
        }

        if (depth == 11 && className.equals("android.view.ViewGroup")) {

            AccessibilityNodeInfo depth10ListView = nodeInfo.getParent();


            // Pegando o ID
            long sourceNodeId = -1;
            try {
                Field sourceNodeIdField = AccessibilityNodeInfo.class.getDeclaredField("mSourceNodeId");
                sourceNodeIdField.setAccessible(true);
                sourceNodeId = sourceNodeIdField.getLong(nodeInfo);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }

            Message message = new Message();
            message.setId(sourceNodeId);
            getAnything(nodeInfo, message);

            // todo adicionar a mensagem no chat (fazer uma função atualizar())
            nomeDoChat.getMensagens().add(message);


            Log.e(TAG, "getChat: " + message );
        }

        for (int i = 0; i < nodeInfo.getChildCount(); ++i) {
            getChat(nodeInfo.getChild(i), depth + 1);
        }
    }

    public static void getAnything(AccessibilityNodeInfo nodeInfo, Message message) {

        if (nodeInfo == null) return;

        CharSequence text = nodeInfo.getText();
        CharSequence description = nodeInfo.getContentDescription();

        if (text != null) {
            message.addMessage(text.toString());
        }

        if (description != null) {
            message.addMessage(description.toString());
        }

        // Percorre os filhos
        for (int i = 0; i < nodeInfo.getChildCount(); ++i) {
            getAnything(nodeInfo.getChild(i), message);
        }
    }

}
