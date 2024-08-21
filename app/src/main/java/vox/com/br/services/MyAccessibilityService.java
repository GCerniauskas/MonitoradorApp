package vox.com.br.services;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.accessibilityservice.AccessibilityService;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import vox.com.br.dao.AppAbertoDao;
import vox.com.br.dao.ChatWhatsDao;
import vox.com.br.model.AppAberto;
import vox.com.br.model.ChatWhats;
import vox.com.br.model.Message;

// Foi mal pelo código ruim, com tempo pode ter certeza que vou melhorar
public class MyAccessibilityService extends AccessibilityService {

    public boolean accessibilityServiceEnabled = false;
    private static boolean isWhatsAppConversationOpen = false;
    public static ChatWhats nomeDoChat = new ChatWhats("");
    public static String timestampWhatsApp = "";

    public MyAccessibilityService() {
        super();
    }

    public boolean isAccessibilityServiceEnabled() {
        return accessibilityServiceEnabled;
    }

    // Testes para verificar se o scroll parou
    boolean scrollstate = false;
    private final Timer timer = new Timer();
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            Log.e(TAG, "executado! ");
            scrollstate = false;
        }
    };

    // Quando acontecer um Evento de acessibilidade, esse código é executado
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        Log.e(TAG, "onAccessibilityEvent Type:" + event.getEventType());

        AppAbertoDao appAbertoDao = new AppAbertoDao();

        // Aqui pegamos o nome do pacote do evento estamos usando: (TYPE_WINDOW_STATE_CHANGED)
        String nomeDoApp = event.getPackageName().toString();

        Calendar now = Calendar.getInstance();
        String horarioDoOcorrido = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.getDefault()).format(now.getTime());


        PackageManager packageManager = this.getPackageManager();
//        Intent intent = new Intent("android.intent.action.MAIN");
//        intent.addCategory("android.intent.category.HOME");

        // Se o evento for do tipo TYPE_VIEW_SCROLLED ----------------------------------------------
//        if (AccessibilityEvent.TYPE_VIEW_SCROLLED == event.getEventType()) {
//            Log.e(TAG, "OnTYPE_VIEW_SCROLLED: ");
//
//            try {
//                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(nomeDoApp, 0);
//                CharSequence applicationLabel = packageManager.getApplicationLabel(applicationInfo);
//
//                if (applicationLabel.toString().equals("WhatsApp")) {
//                    Log.e(TAG, "OnTYPE_WINDOW_CONTENT_CHANGED: " + event.getPackageName());
//
//                    // Verifica se está no chat para tentar resgatar as mensagens
//                    if (verificaSeEstaNoChat(packageManager, event)) {
//                        // Logica para ver se parou de scrollar a tela
//                        if (scrollstate) {
//                            timerTask.cancel();
//                            timerTask = new TimerTask() {
//                                @Override
//                                public void run() {
//                                    Log.e(TAG, "executado! ");
////                                    getChat(getRootInActiveWindow(), 0);
//                                    scrollstate = false;
//                                }
//                            };
//                            timer.schedule(timerTask, 1000);
//                        } else {
//                            timerTask = new TimerTask() {
//                                @Override
//                                public void run() {
//                                    Log.e(TAG, "executado! ");
////                                    getChat(getRootInActiveWindow(), 0);
//                                    scrollstate = false;
//                                }
//                            };
//                            timer.schedule(timerTask, 1000);
//                        }
//                        scrollstate = true;
//                    }
//
//                }
//            } catch (PackageManager.NameNotFoundException e) {
//                throw new RuntimeException(e);
//            }
//
//        }

        // Se o evento for do tipo TYPE_VIEW_CLICKED -----------------------------------------------
        if (AccessibilityEvent.TYPE_VIEW_CLICKED == event.getEventType()) {
            Log.e(TAG, "OnTYPE_VIEW_CLICKED: " + event.getPackageName());

            // Check if is going to enter in a chat
            if (event.getPackageName().toString().equals("com.whatsapp") && event.getText().size() >= 4) {
                timestampWhatsApp = event.getText().get(2).toString();
                Log.i("ViewClicked", "entrou no chat" + timestampWhatsApp);

                // https://stackoverflow.com/questions/4216745/java-string-to-date-conversion
                // todo: Preciso de uma função que retorna a data com base nessa string.


            }

        }

        // Se o evento for do tipo TYPE_WINDOW_CONTENT_CHANGED -------------------------------------
        if (AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED == event.getEventType()) {
            Log.e(TAG, "OnTYPE_WINDOW_CONTENT_CHANGED: " + event.getPackageName());

            if (isWhatsAppConversationOpen) {
//                getChat(getRootInActiveWindow(), 0);
                Log.i(TAG, "WhatsApp está aberto, ativa a função.: ");
            }

        }

        // se o evento for do tipo TYPE_VIEW_TEXT_CHANGED ------------------------------------------
        if (AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED == event.getEventType()) {
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
            if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) { // ??????????????????????????????????????????? isso aqui é sempre true seu louco
                if (!event.getText().isEmpty()) {
                    Log.e(TAG, "onAccessibilityEvent: " + event.getText().get(0).toString());
                    textoDigitado.add(event.getText().get(0).toString());

                    Log.e(TAG, "onAccessibilityEvent: " + textoDigitado);
                    AppAberto appAbertoAtual = new AppAberto(applicationLabel.toString(), horarioDoOcorrido, now, textoDigitado);
                    appAbertoDao.atualizarDigitados(appAbertoAtual);
                }
            }
        }

        // Se o evento for do tipo TYPE_WINDOW_STATE_CHANGED ---------------------------------------
        if (AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED == event.getEventType()) {
            try {

                // Resgatando o nome do App
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(nomeDoApp, 0);
                CharSequence applicationLabel = packageManager.getApplicationLabel(applicationInfo);

                // Criando uma intância do AppAberto
                List<String> textoDigitado = new ArrayList<>();
                AppAberto appAbertoAtual = new AppAberto(applicationLabel.toString(), horarioDoOcorrido, now, textoDigitado);

                if (event.getClassName() != null) { // Check if the user is in the chat
                    if (event.getClassName().toString().equals("com.whatsapp.Conversation")) {
                        isWhatsAppConversationOpen = true;
//                            logNodeHeirarchy(getRootInActiveWindow(),  0);
                        getChat(getRootInActiveWindow(), 0);
                    } else {
                        isWhatsAppConversationOpen = false;
                    }
                } else {
                    isWhatsAppConversationOpen = false;
                }

                // Verificando se existe algo no historico (se não existe quer dizer que o app está iniciando agora)
                if (appAbertoDao.getTodosHistorico().isEmpty()) {
                    appAbertoDao.salvarNoHistorico(appAbertoAtual);
                    appAbertoDao.salvar(appAbertoAtual);
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
        accessibilityServiceEnabled = true;
        Log.e(TAG, "onServiceConnected: ");
        super.onServiceConnected();
    }

    public static void logNodeHeirarchy(AccessibilityNodeInfo nodeInfo, int depth) {

        if (nodeInfo == null) return;

        String logString = "";
        String className = nodeInfo.getClassName().toString();
        String resourceName = nodeInfo.getViewIdResourceName();

        for (int i = 0; i < depth; ++i) {
            logString += " ";
        }

        logString += "Text: " + nodeInfo.getText() + " " + " Content-Description: " + nodeInfo.getContentDescription() + " ClassName: " + nodeInfo.getClassName().toString() + " ResourceName: " + resourceName + " Depth: " + depth;


        if (depth == 11 && className.equals("android.view.ViewGroup")) {
            Log.e(TAG, "logNodeHeirarchy: " + contar(nodeInfo));
        }
        Log.e(TAG, "logNodeHeirarchy: " + logString);
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

    public static void getChat(AccessibilityNodeInfo nodeInfo, int depth) {
        if (nodeInfo == null) return;

        ChatWhatsDao chatWhatsDao = new ChatWhatsDao();
        String className = nodeInfo.getClassName().toString();

        // Resgatar o nome do chat
        if (depth == 11 && className.equals("android.widget.FrameLayout") && nodeInfo.getChild(0).getClassName().toString().equals("android.widget.TextView") && nomeDoChat.getMensagens().isEmpty()) {
            AccessibilityNodeInfo chat = nodeInfo.getChild(0);

            // Criando o chat
            nomeDoChat.setNome(chat.getText().toString());

            // Salvando o chat (nessa função já faz a verifica se existe ou não)
            chatWhatsDao.salvar(nomeDoChat);
            Log.e(TAG, "getChat: " + chat);
        }

        // Resgatar as mensagens
        if (depth == 11 && className.equals("android.view.ViewGroup")) {

            Message message = new Message();
            getAnything(nodeInfo, message);

            chatWhatsDao.atualizarChatComMensagem(nomeDoChat, message);

            Log.e(TAG, "getChat: " + message);
        }

        for (int i = 0; i < nodeInfo.getChildCount(); ++i) {
            getChat(nodeInfo.getChild(i), depth + 1);
        }
    }

    public static void getAnything(AccessibilityNodeInfo nodeInfo, Message message) {
        if (nodeInfo == null) return;

        CharSequence text = nodeInfo.getText();
        CharSequence description = nodeInfo.getContentDescription();
        CharSequence resourceName = nodeInfo.getViewIdResourceName();

        // If something exists, add to it
        if (resourceName != null) {
            message.addTudo(resourceName.toString());
        }

        if (text != null) {
            message.addTudo(text.toString());

            if (resourceName != null) {
                if (resourceName.toString().equals("com.whatsapp:id/message_text")) {
                    message.addMessage(text.toString());
                }
                if (resourceName.toString().equals("com.whatsapp:id/date")) {
                    message.setHorario(text.toString());
                }
            }

        }

        if (description != null) {
            message.addTudo(description.toString());

            if (resourceName != null) {
                if (resourceName.toString().equals("com.whatsapp:id/status")) {
                    message.setStatus(Message.MessageStatus.ENVIADO);
                }
            }
        }

        // Percorre os filhos
        for (int i = 0; i < nodeInfo.getChildCount(); ++i) {
            getAnything(nodeInfo.getChild(i), message);
        }
    }

    public static String stringToDate(String date) {

        Calendar c = Calendar.getInstance();

        // Formatos que precisamos
        DateFormat dateComplete = new SimpleDateFormat("EEE dd/MM/yyyy", Locale.getDefault());
        DateFormat dateString = new SimpleDateFormat("EEE", Locale.getDefault());
        DateFormat dateNumber = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        if (date.equalsIgnoreCase("Hoje")) {
            return dateNumber.format(c.getTime());
        } else if (date.equals("Ontem")) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -1);
            return dateNumber.format(calendar.getTime());
        }

        c.add(Calendar.DATE, -2);
        for (int i = 0; i <= 7; i++) {

            String diaDaSemana = dateString.format(c.getTime());
            if (date.toLowerCase().contains(diaDaSemana)) {
                return dateNumber.format(c.getTime());
            }
            c.add(Calendar.DATE, -1); // less one day

        }

        Log.e("stringToDate", "String in the parameter is not valid.");
        return "";
    }

}
