package org.example.project

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.multiplatform.webview.util.addTempDirectoryRemovalHook
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import compose_multiplatform_webview_jsbridge_demo.composeapp.generated.resources.Res
import compose_multiplatform_webview_jsbridge_demo.composeapp.generated.resources.compose_multiplatform
import dev.datlag.kcef.KCEF
import dev.datlag.kcef.KCEFBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.max

@Composable
fun App() {

        addTempDirectoryRemovalHook()

            var restartRequired by remember { mutableStateOf(false) }
            var downloading by remember { mutableStateOf(0F) }
            var initialized by remember { mutableStateOf(false) }
            val download: KCEFBuilder.Download = remember { KCEFBuilder.Download.Builder().github().build() }

            LaunchedEffect(Unit) {
                withContext(Dispatchers.IO) {
                    KCEF.init(builder = {
                        installDir(File("kcef-bundle"))

                        /*
                          Add this code when using JDK 17.
                          Builder().github {
                              release("jbr-release-17.0.10b1087.23")
                          }.buffer(download.bufferSize).build()
                         */
                        progress {
                            onDownloading {
                                println("Downloading: $it")
                                downloading = max(it, 0F)
                            }
                            onInitialized {
                                println("Initialized")
                                initialized = true
                            }
                        }
                        settings {
                            cachePath = File("cache").absolutePath
                        }
                    }, onError = {
                        it?.printStackTrace()
                    }, onRestartRequired = {
                        restartRequired = true
                    })
                }
            }

            Column(Modifier.fillMaxSize().safeContentPadding(), horizontalAlignment = Alignment.CenterHorizontally) {
                if (restartRequired) {
                    println("Restart required")
                    Text(text = "Restart required.")
                } else {
                    if (initialized) {
                        println("Initialized WebView")
                        val webViewState =
                            rememberWebViewState("https://github.com/KevinnZou/compose-webview-multiplatform")

                        WebView(
                            state = webViewState,
                            modifier = Modifier.fillMaxSize(),
                        )
                    } else {
                        println("Downloading $downloading%")
                        Text(text = "Downloading $downloading%")
                    }
                }
            }
            DisposableEffect(Unit) {
                onDispose {
                    KCEF.disposeBlocking()
                }
            }
        }

