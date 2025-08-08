package org.example.project

import com.multiplatform.webview.jsbridge.IJsMessageHandler
import com.multiplatform.webview.jsbridge.JsMessage
import com.multiplatform.webview.jsbridge.dataToJsonString
import com.multiplatform.webview.web.WebViewNavigator
import kotlinx.serialization.Serializable

class InitJsBridgeHandler(): IJsMessageHandler  {
    override fun methodName(): String {
        return "Init"
    }

    override fun handle(
        message: JsMessage,
        navigator: WebViewNavigator?,
        callback: (String) -> Unit
    ) {
        //TODO : the jsbridge unescapes the newlines in the string for some reason, file bug upstream
        // in the meanwhile, we double escape it.
        val data = InitResult("""
            This is a multiline kotlin string.
            This should be displayed in the webview.
            The quick brown fox jumps over the lazy dog.
        """.trimIndent())
        val jsonString = dataToJsonString(data)
        callback(jsonString)
    }

}

data class InitResult(val text : String)