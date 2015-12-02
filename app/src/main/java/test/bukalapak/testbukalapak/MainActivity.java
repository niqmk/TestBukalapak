package test.bukalapak.testbukalapak;

import android.app.Activity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {
	private Socket socket;
	private PrintWriter out = null;
	private BufferedReader in = null;
	private Listener listener;
	private Timer timer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if(socket == null) {
			new ConnectTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		try {
			if(socket != null) {
				socket.close();
			}
			if(timer != null) {
				timer.cancel();
			}
		}catch(IOException ex) {}
		super.onDestroy();
	}
	private class ConnectTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			try {
				socket = new Socket("xinuc.org", 7387);
				out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
				setTimer();
			} catch (UnknownHostException ex) {
			} catch (IOException ex) {
			}
			return null;
		}
	}
	public class Listener implements Runnable
	{

		@Override
		public void run() {

			try {
				String line = null;

				while((line = in.readLine()) != null)
				{
					setType(line);
				}
			} catch (Exception e) {
				// ...
			}

		}

	}
	private void setTimer() {
		timer = new Timer();
		timer.schedule(new SocketTimer(), 5000, 5000);
	}
	private class SocketTimer extends TimerTask {
		@Override
		public void run() {
			try {
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				if(listener == null)
				{
					listener = new Listener();
					Thread thread = new Thread(listener);
					thread.start();
				}
			}catch(IOException ex) {}
		}
	}
	private void setType(final String line) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				clear();
				//output.setText("");
				final StringBuffer buff = new StringBuffer();
				String[] separated = line.split(" ");
				for(int counter = 0; counter < separated.length; counter++) {
					write(buff, separated[counter]);
				}
				//output.setText(buff.toString());
			}
		});
	}
	private void write(StringBuffer buff, final String text) {
		/*K: White King
		Q: White Queen
		B: White Bishop
		N: White Knight
		R: White Rook
		k: Black King
		q: Black Queen
		b: Black Bishop
		n: Black Knight
		r: Black Rook*/
		Map<String, String> key = new HashMap<String, String>();
		key.put("K", "White King");
		key.put("Q", "White Queen");
		key.put("B", "White Bishop");
		key.put("N", "White Knight");
		key.put("R", "White Rook");
		key.put("k", "Black King");
		key.put("q", "Black Queen");
		key.put("b", "Black Bishop");
		key.put("n", "Black Knight");
		key.put("r", "Black Rook");
		String value = key.get(text.substring(0, 1));
		((TextView)findViewById(getId("" + text.substring(1, 2) + text.substring(2, 3)))).setText(text.substring(0, 1));
		value += " - Column : " + text.substring(1, 2) + " - Row : " + text.substring(2, 3) + "\n";
		buff.append(value);
	}
	private int getId(String name) {
		Resources res = getResources();
		return res.getIdentifier(name, "id", getApplicationContext().getPackageName());
	}
	private void clear() {
		String[] key = {"a", "b", "c", "d", "e", "f", "g", "h"};
		for (int x = 0; x < 8; x++) {
			for (int y = 1; y < 9; y++) {
				((TextView)findViewById(getId("" + key[x] + y))).setText("");
			}
		}
	}
}