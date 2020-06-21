package servent.message;

import java.util.Map;

public class UpdateFractalTableMessage extends BasicMessage {
    private static final long serialVersionUID = 2076361812286328734L;
    private Map<String, String> fractalIds;
    public UpdateFractalTableMessage(int senderPort, int receiverPort) {
        super(MessageType.UPDATE_FRACTAL_TABLE, senderPort, receiverPort);
    }

    public void setFractalIds(Map<String, String> fractalIds) {
        this.fractalIds = fractalIds;
    }

    public Map<String, String> getFractalIds() {
        return fractalIds;
    }
}
