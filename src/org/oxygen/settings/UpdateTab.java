package org.oxygen.settings;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class UpdateTab extends Activity {

	private static final String LOG_TAG = UpdateTab.class.getSimpleName();

	private DownloadAsyncTask mDownloadAsyncTask;

	private static final String outputFile = "update.zip";
	private static final String outputPath = "/sdcard/" + outputFile;
	private static final String inputPath = "http://download.oxygen.im/roms/latest.zip";

	private Process mProcess;
	private ProgressBar mProgressBar;
	private TextView mProgressPercentageTextView;
	private TextView mProgressCurrentTextView;
	private Button mButtonDownloadApply;
	private Button mButtonCancel;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.updater);

		mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
		mProgressPercentageTextView = (TextView) findViewById(R.id.progress_percentage);
		mProgressCurrentTextView = (TextView) findViewById(R.id.progress_current_text);
		mButtonDownloadApply = (Button) findViewById(R.id.button_download_apply);
		mButtonCancel = (Button) findViewById(R.id.button_cancel);

		final TextView mProcessTextView = (TextView) findViewById(R.id.process);
		try {
			mProcess = Runtime.getRuntime().exec("su");
			mProcessTextView.setText("-- " + mProcess + " --");
		} catch (IOException ioe) {
		}

		mButtonDownloadApply.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mDownloadAsyncTask = new DownloadAsyncTask();
				mDownloadAsyncTask.execute();
				updateUI(true);
			}
		});

		mButtonCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mDownloadAsyncTask.cancel(true);
				mProgressCurrentTextView.setText("Cancelled");
				updateUI(false);
			}
		});
	}

	private void updateUI(final boolean start) {

		if (start) {

			mProgressBar.setVisibility(View.VISIBLE);
			mProgressPercentageTextView.setVisibility(View.VISIBLE);
			mButtonDownloadApply.setEnabled(false);
			mButtonCancel.setEnabled(true);
		} else {
			mProgressBar.setVisibility(View.INVISIBLE);
			mProgressPercentageTextView.setVisibility(View.INVISIBLE);
			mButtonDownloadApply.setEnabled(true);
			mButtonCancel.setEnabled(false);
		}
	}

	class DownloadAsyncTask extends AsyncTask<String, String, Boolean> {

		private boolean mCancel;

		@Override
		protected void onPreExecute() {

			super.onPreExecute();
			mProgressCurrentTextView.setText("Downloading...");
		}

		@Override
		protected Boolean doInBackground(String... arguments) {

			int count;
			try {
				final URL url = new URL(inputPath);
				final URLConnection urlConnection = url.openConnection();
				new File(outputPath).delete();
				urlConnection.connect();

				final int fileContentLength = urlConnection.getContentLength() / 1000;
				final InputStream inputStream = new BufferedInputStream(
						url.openStream());
				final OutputStream outputStream = new FileOutputStream(
						outputPath);

				long total = 0;
				byte data[] = new byte[1024];
				while ((count = inputStream.read(data)) != -1 && !mCancel) {

					total += count;
					publishProgress((int) total / 1000 * 100
							/ fileContentLength + "%");
					outputStream.write(data, 0, count);
				}
				outputStream.flush();
				outputStream.close();
				inputStream.close();

			} catch (Exception e) {
				Log.e(LOG_TAG, "FAIL!", e);
				return false;
			}
			return true;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			mProgressPercentageTextView.setText(values[0]);
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			mCancel = true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			if (!result)
				mProgressCurrentTextView.setText("Failed");
			else {
				mProgressCurrentTextView.setText("Rebooting...");
				final OutputStream outputStream = mProcess.getOutputStream();
				try {
					outputStream
							.write("echo 'boot-recovery'>/cache/recovery/command\n"
									.getBytes());
					final String command = "echo '--update_package=SDCARD:"
							+ outputFile + "'>>/cache/recovery/command\n";

					outputStream.write(command.getBytes());
					outputStream.write("reboot recovery\n".getBytes());

				} catch (IOException e) {
					mProgressCurrentTextView.setText("Failed");
				}
			}
			updateUI(false);
		}
	}
}
