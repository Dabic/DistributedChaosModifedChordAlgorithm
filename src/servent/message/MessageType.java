package servent.message;

public enum MessageType {
	NEW_NODE, WELCOME, SORRY, UPDATE, PUT, ASK_GET, TELL_GET, POISON,
	JOB_STARTED, UPDATE_FRACTAL_TABLE, ASK_FOR_POINTS, SEND_POINTS,
	JOB_STATUS, FRACTAL_STATUS, FRACTAL_STATUS_RESPONSE,
	JOB_RESULT, FRACTAL_RESULT, FRACTAL_RESULT_RESPONSE,
	JOB_CREATED, JOB_STOPPED, QUIT, SEND_POINTS_QUIT, RECEIVE_POINTS_QUIT,
	RECEIVE_POINTS_QUIT_RESPONSE, TRANSFER_POINTS_QUIT, ADD_TRANSFER_POINTS_QUIT,
	ADD_TRANSFER_POINTS_QUIT_RESPONSE, UPDATE_FRACTAL_QUIT, I_AM_GONE
}
