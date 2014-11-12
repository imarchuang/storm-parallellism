package messaging.zmq;

import org.zeromq.ZMQ;

public class hwclient {

	/**
	08.
	* @param args
	09.
	*/

	public static void main(String[] args) {

		ZMQ.Context context = ZMQ.context(1);

		// Socket to talk to server

		System.out.println("Connecting to hello world server");

		ZMQ.Socket socket = context.socket(ZMQ.REQ);

		socket.connect ("tcp://localhost:5555");

		for(int requestNbr = 0; requestNbr != 10; requestNbr++) {
			String request = "Hello" ;
			System.out.println("Sending Hello " + requestNbr );
			socket.send(request.getBytes (), 0);

			byte[] reply = socket.recv(0);
			System.out.println("Received " + new String (reply) + " " + requestNbr);

		}

		socket.close();
		context.term();

	}

}
