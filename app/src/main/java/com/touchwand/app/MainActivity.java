package com.touchwand.app;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
	protected ProgressDialog progressDialog;
	private boolean found = false;
	private String currAddress = "cloud.touchwand.com";

	private WebView webView;
	private boolean allowedSsl = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		//Remove notification bar
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		int permissionCheck = ContextCompat.checkSelfPermission(this,
				Manifest.permission.CAMERA);

		if(permissionCheck == PackageManager.PERMISSION_DENIED) {
			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.CAMERA},
					MY_PERMISSIONS_REQUEST_CAMERA);
		}

		setContentView(R.layout.activity_main);

		webView = (WebView) findViewById(R.id.webView);


		webView.getSettings().setJavaScriptEnabled(true);


		webView.getSettings().setAllowFileAccessFromFileURLs(true); //Maybe you don't need this rule
		webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
		webView.setWebChromeClient(new WebChromeClient() {
			@Override
			public void onPermissionRequest(PermissionRequest request) {
				request.grant(request.getResources());
			}
		});

		webView.getSettings().setDatabaseEnabled(true);
		webView.getSettings().setDomStorageEnabled(true);
		webView.setWebViewClient(new WebViewClient(){
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				//hide loading image
				findViewById(R.id.splashscreen).setVisibility(View.GONE);
				//show webview
				findViewById(R.id.webView).setVisibility(View.VISIBLE);
			}
			@Override
			public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {

				if(!allowedSsl) {
					final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
					builder.setMessage("Press continue if you sure you are connected to your own controller");
					builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

							handler.proceed();

							allowedSsl = true;
						}
					});
					builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							handler.cancel();
						}
					});
					final AlertDialog dialog = builder.create();
					dialog.show();
				}
			}


		});

		webView.setWebChromeClient(new WebChromeClient(){
			public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
				System.out.println( consoleMessage.message() + " -- From line "
						+ consoleMessage.lineNumber() + " of "
						+ consoleMessage.sourceId());
				return super.onConsoleMessage(consoleMessage);
			}


				@TargetApi(Build.VERSION_CODES.LOLLIPOP)
				@Override
				public void onPermissionRequest(final PermissionRequest request) {
					request.grant(request.getResources());
				}

		});

		webView.addJavascriptInterface(this, "Android");



		new DiscoveryThread().start();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_CAMERA: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {

					// permission was granted, yay! Do the
					// contacts-related task you need to do.

				} else {

					// permission denied, boo! Disable the
					// functionality that depends on this permission.
				}
				return;
			}

			// other 'case' lines to check for other
			// permissions this app might request
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (webView.canGoBack()) {
					webView.goBack();
				} else {
					finish();
				}
				return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}



	protected void showProgressDialog(final String title, final String message) {

		runOnUiThread(new Runnable() {
			public void run() {
				if (progressDialog == null) {
					progressDialog = ProgressDialog.show(MainActivity.this, title,
							message);
				} else {
					progressDialog.setTitle(title);
					progressDialog.setMessage(message);
				}
				progressDialog.show();
			}
		});
	}

	protected void hideProgress() {
		if (progressDialog != null) {
			runOnUiThread(new Runnable() {
				public void run() {
					progressDialog.hide();
				}
			});			
		}
	}

	protected void showAlert(final String title, final String message, final Runnable okRunnable, final Runnable cancelRunnable){
		runOnUiThread(new Runnable() {
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

				builder.setMessage(message)
				.setTitle(title)
				.setCancelable(true)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if(okRunnable != null){
							okRunnable.run();
						}
					}
				}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						if(cancelRunnable != null){
							cancelRunnable.run();
						}
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
			}
		});

	}

	protected void showAlert(final String title, final View layout, final Runnable closeRunnable){

		runOnUiThread(new Runnable() {
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

				builder.setView(layout)
				.setTitle(title)
				.setCancelable(true)
				.setPositiveButton("Close", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if(closeRunnable != null){
							closeRunnable.run();
						}
					}
				});

				AlertDialog alert = builder.create();
				alert.show();
			}
		});
	}

	protected void showAlert(final String title, final String message, final Runnable closeRunnable){

		runOnUiThread(new Runnable() {
			public void run() {
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

				builder.setMessage(message)
				.setTitle(title)
				.setCancelable(true)
				.setPositiveButton("Close", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if(closeRunnable != null){
							closeRunnable.run();
						}
					}
				});

				AlertDialog alert = builder.create();
				alert.show();
			}
		});

	}

	@JavascriptInterface
	public String getCurrAddress() {
		return currAddress;
	}

	@JavascriptInterface
	public void showToast(final String msg) {
		runOnUiThread(new Runnable() {
			public void run() {

				Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();

			}
		});
	}

	private class DiscoveryThread extends Thread{
		@Override
		public void run() {

			DatagramSocket socket = null;

			try{
				socket = new DatagramSocket(35000);

				byte []data = new byte[1500];

				final DatagramPacket p = new DatagramPacket(data, 1500);

				socket.setBroadcast(true);
				socket.setSoTimeout(5000);
				while(true){
					socket.receive(p);

					//if("Wanderfull".equals(new String(data))){

					runOnUiThread(new Runnable() {
						public void run() {

							currAddress = p.getAddress().getHostAddress();
							//webView.loadUrl("file:///android_asset/index.html");
							webView.loadUrl(currAddress);

						}
					});

					found = true;

					return;
					//					}
				}
			}catch (Exception e) {
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(MainActivity.this, "Controller not found in local network", Toast.LENGTH_LONG).show();
						Toast.makeText(MainActivity.this, "Connecting through cloud", Toast.LENGTH_LONG).show();

						//webView.loadUrl("file:///android_asset/index.html");
						webView.loadUrl("https://cloud.touchwand.com");

					}
				});
			}finally {
				socket.close();
			}

		}
	}

}
