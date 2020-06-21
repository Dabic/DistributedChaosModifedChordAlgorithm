package servent.handler;

import app.AppConfig;
import app.ServentInfo;
import app.fractals.FractalCreator;
import servent.message.IAmGoneMessage;
import servent.message.Message;
import servent.message.UpdateFractalTableMessage;
import servent.message.UpdateFractalsQuitMessage;
import servent.message.util.MessageUtil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class UpdateFractalsQuitHandler implements MessageHandler {
    private UpdateFractalsQuitMessage message;
    private FractalCreator fractalCreator;

    public UpdateFractalsQuitHandler(Message message, FractalCreator fractalCreator) {
        this.message = (UpdateFractalsQuitMessage) message;
        this.fractalCreator = fractalCreator;
    }

    @Override
    public void run() {
        if (message.getSenderPort() != AppConfig.myServentInfo.getListenerPort()) {
            UpdateFractalsQuitMessage ufqm = new UpdateFractalsQuitMessage(message.getSenderPort(), AppConfig.chordState.getNextNodePort());
            ufqm.setReceiverIp(AppConfig.chordState.getNextNodeIp());
            ufqm.setFractalTable(message.getFractalTable());
            ufqm.setId(message.getId());
            ufqm.setAllNodes(message.getAllNodes());
            MessageUtil.sendMessage(ufqm);
            if (Integer.parseInt(message.getId()) < AppConfig.myServentInfo.getChordId()) {
                AppConfig.myServentInfo.setChordId(AppConfig.myServentInfo.getChordId() - 1);
            }
            AppConfig.chordState.setFractalsIds(message.getFractalTable());
            AppConfig.chordState.clearAllNodes();
            List<ServentInfo> serventInfos = new ArrayList<>();
            for (ServentInfo serventInfo : message.getAllNodes()) {
                if (serventInfo.getChordId() != AppConfig.myServentInfo.getChordId()) {
                    serventInfos.add(serventInfo);
                }
            }
            AppConfig.chordState.addNodes(serventInfos);
        } else {
            IAmGoneMessage iAmGoneMessage = new IAmGoneMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getNextNodePort());
            iAmGoneMessage.setReceiverIp(AppConfig.chordState.getNextNodeIp());
            MessageUtil.sendMessage(iAmGoneMessage);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }
    }
}
