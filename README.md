# Learn.ink WebView Android (Kotlin)

[Full documentation on adding a WebView to an Android app](https://developer.android.com/develop/ui/views/layout/webapps/webview) for both Kotlin and Java can be found on the Android developers page.

### Enabling Javascript, DOM storage and content access

You will need to enable Javascript, DOM storage and content access for your WebView to ensure it runs as expected. More information on WebView settings can be found on the [Android WebSettings reference page](https://developer.android.com/reference/android/webkit/WebSettings)

### Handling external links

You will also need to ensure that any external links are opened outside of the WebView. External links are used in 3 key places:

1. Learn.ink uses WhatsApp as a backup verification when a user cannot receive an SMS OTP. As part of this process the user will click on a button which will redirect them to WhatsApp. If you attempt to do this inside the WebView it will fail and the user will see an error screen.
2. Course creators can add external links as buttons inside lessons. These external links should open in the browser, rather than the WebView, to ensure the user does not get “lost” inside the WebView.
3. Course certificates can be downloaded as PDFs and should also be opened in an external browser rather than inside the WebView.

We recommend testing that external links are being handled correctly in your app before going live.

### Sample code

An example WebView integration fragment is shown below and can be found in the [WebViewFragment.kt file](https://github.com/LearnInkTeam/learnink-webview-android/blob/master/app/src/main/java/com/learninkwebview/android/ui/webview/WebViewFragment.kt) inside this repo.

```kotlin
// WebViewFragment.kt
package com.learninkwebview.android.ui.webview

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebResourceRequest
import androidx.fragment.app.Fragment
import com.learninkwebview.android.R
import android.net.Uri

class WebViewFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_webview, container, false)
    }

    fun handleUrlOverride(uri: Uri?): Boolean {
        if (uri == null) {  // When the uri is malformed, let's override but not pass an intent (essentially doing nothing and ignoring the request)
            return true
        }
        val internalHosts = listOf("m.learn.ink")

        return if (internalHosts.contains(uri.host)) {  // Or some other applicable filtering strategy
            false
        } else {
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
            true
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val myWebView: WebView = view.findViewById(R.id.webView)
        myWebView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return handleUrlOverride(request?.url)
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean { // We override the deprecated function too for backwards compat
                return handleUrlOverride(Uri.parse(url))
            }
        }

        myWebView.settings.javaScriptEnabled = true
        myWebView.settings.allowContentAccess = true
        myWebView.settings.domStorageEnabled = true
        // Load the correct url, replacing <your-org-id> with the 
        // id assigned to your organisation
        myWebView.loadUrl("https://m.learn.ink/<your-org-id>/learning")
    }
}
```