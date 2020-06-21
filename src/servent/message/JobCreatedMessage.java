package servent.message;

import app.fractals.FractalCreatorInfo;

public class JobCreatedMessage extends BasicMessage {
    private static final long serialVersionUID = 6588362191530313580L;

    private FractalCreatorInfo fractalCreatorInfo;
    public JobCreatedMessage(int senderPort, int receiverPort) {
        super(MessageType.JOB_CREATED, senderPort, receiverPort);
    }

    public void setFractalCreatorInfo(FractalCreatorInfo fractalCreatorInfo) {
        this.fractalCreatorInfo = fractalCreatorInfo;
    }

    public FractalCreatorInfo getFractalCreatorInfo() {
        return fractalCreatorInfo;
    }
}
