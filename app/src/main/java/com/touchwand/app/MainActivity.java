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

	//	// HTTP GET request
	//		private void sendGet() throws Exception {
	//
	//			String url = "http://www.google.com/search?q=mkyong";
	//
	//			URL obj = new URL(url);
	//			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	//
	//			// optional default is GET
	//			con.setRequestMethod("GET");
	//
	//			//add request header
	//			con.setRequestProperty("User-Agent", USER_AGENT);
	//
	//			int responseCode = con.getResponseCode();
	//			System.out.println("\nSending 'GET' request to URL : " + url);
	//			System.out.println("Response Code : " + responseCode);
	//
	//			BufferedReader in = new BufferedReader(
	//			        new InputStreamReader(con.getInputStream()));
	//			String inputLine;
	//			StringBuffer response = new StringBuffer();
	//
	//			while ((inputLine = in.readLine()) != null) {
	//				response.append(inputLine);
	//			}
	//			in.close();
	//
	//			//print result
	//			System.out.println(response.toString());
	//
	//		}

	//		// HTTP POST request
	//		private WebResourceResponse sendRequest(WebResourceRequest request) throws Exception {
	//
	//			URL obj = new URL(request.getUrl().getScheme(),request.getUrl().getHost(),request.getUrl().getPort(),request.getUrl().getPath());
	//			HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
	//
	//			//add reuqest header
	//			con.setRequestMethod(request.getMethod());
	//			con.setRequestProperty("User-Agent", "");
	//			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	//
	//
	//			// Send post request
	//			con.setDoOutput(true);
	//			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
	//			wr.writeBytes(request.);
	//			wr.flush();
	//			wr.close();
	//
	//			int responseCode = con.getResponseCode();
	//			System.out.println("\nSending 'POST' request to URL : " + url);
	//			System.out.println("Post parameters : " + urlParameters);
	//			System.out.println("Response Code : " + responseCode);
	//
	//			BufferedReader in = new BufferedReader(
	//			        new InputStreamReader(con.getInputStream()));
	//			String inputLine;
	//			StringBuffer response = new StringBuffer();
	//
	//			while ((inputLine = in.readLine()) != null) {
	//				response.append(inputLine);
	//			}
	//			in.close();
	//
	//			return response.toString();
	//			
	//
	//		}

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

		//webView.getSettings().setAppCachePath(getApplicationContext().getCacheDir().getAbsolutePath());

		//webView.getSettings().setAppCacheEnabled(true); 

//		webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);

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
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
				handler.proceed(); // Ignore SSL certificate errors
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if(!url.startsWith("https://"))
				{
					webView.loadUrl(url);
				}
				return false;
			}


			//			@Override
			//			public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
			//
			//				if(request.getMethod().equalsIgnoreCase("GET") && (request.getUrl().getPath().endsWith(".html") || request.getUrl().getPath().endsWith(".jpg") || request.getUrl().getPath().endsWith(".png") || request.getUrl().getPath().endsWith(".jpeg"))){
			//					try {
			//						URL url = new URL(request.getUrl().getScheme(),request.getUrl().getHost(),request.getUrl().getPort(), request.getUrl().getPath());
			//						HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			//						connection.setRequestProperty("User-Agent", "");
			//						connection.setRequestMethod("GET");
			//						connection.setDoInput(true);
			//						connection.connect();
			//
			//						InputStream inputStream = connection.getInputStream();
			//						return new WebResourceResponse(connection.getContentType().split("\\;")[0], connection.getContentEncoding(), inputStream);
			//					} catch (MalformedURLException e) {
			//						// TODO Auto-generated catch block
			//						e.printStackTrace();
			//					} catch (ProtocolException e) {
			//						// TODO Auto-generated catch block
			//						e.printStackTrace();
			//					} catch (IOException e) {
			//						// TODO Auto-generated catch block
			//						e.printStackTrace();
			//					}
			//				}
			//
			//				return super.shouldInterceptRequest(view, request);
			//			}



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
		//webView.getSettings().setDomStorageEnabled(true);
		//webView.getSettings().setDatabaseEnabled(true);
		//if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
		//	webView.getSettings().setDatabasePath("/data/data/" + webView.getContext().getPackageName() + "/databases/");
		//}
		webView.addJavascriptInterface(this, "Android");



		new DiscoveryThread().start();
	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
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
				socket.setSoTimeout(2000);
				while(true){
					socket.receive(p);

					//if("Wanderfull".equals(new String(data))){

					runOnUiThread(new Runnable() {
						public void run() {

							currAddress = p.getAddress().getHostAddress();
							//webView.loadUrl("file:///android_asset/index.html");
							webView.loadUrl("https://"+p.getAddress().getHostAddress());

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
