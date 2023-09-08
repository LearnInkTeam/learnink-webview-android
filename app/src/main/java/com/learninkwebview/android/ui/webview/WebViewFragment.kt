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
        // Load the correct url
        myWebView.loadUrl("https://m.learn.ink/acme/learning")
    }
}