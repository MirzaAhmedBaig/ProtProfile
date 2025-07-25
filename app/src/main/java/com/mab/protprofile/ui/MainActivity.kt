package com.mab.protprofile.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mab.protprofile.data.model.ErrorMessage
import com.mab.protprofile.data.model.getResolvedMessage
import com.mab.protprofile.ui.data.AuthState
import com.mab.protprofile.ui.navigation.AppNavGraph
import com.mab.protprofile.ui.screens.home.HomeRoute
import com.mab.protprofile.ui.theme.ProtProfileTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.d("onCreate")
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val snackbarHostState = remember { SnackbarHostState() }
            val scope: CoroutineScope = rememberCoroutineScope()
            MainScreen(
                snackbarHostState,
                showErrorSnackbar = { errorMessage ->
                    val message = errorMessage.getResolvedMessage(this)
                    Timber.e("Error : $message")
                    scope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(message)
                    }
                },
            )
        }
    }
}

@Composable
fun MainScreen(
    snackbarHostState: SnackbarHostState,
    showErrorSnackbar: (ErrorMessage) -> Unit,
) {
    Timber.d("MainScreen")
    val viewModel: AuthViewModel = viewModel()
    ProtProfileTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            ) { innerPadding ->
                val authState = viewModel.authState.collectAsStateWithLifecycle()

                when (authState.value) {
                    AuthState.Loading -> {
                        Timber.d("AuthState.Loading")
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    is AuthState.Authenticated -> {
                        Timber.d("AuthState.Authenticated")
                        AppNavGraph(
                            modifier = Modifier.padding(innerPadding),
                            startDestination = HomeRoute,
                            showErrorSnackbar = showErrorSnackbar,
                        )
                    }

                    AuthState.Unauthenticated -> {
                        Timber.d("AuthState.Unauthenticated")
                        AppNavGraph(
                            modifier = Modifier.padding(innerPadding),
                            showErrorSnackbar = showErrorSnackbar,
                        )
                    }
                }
            }
        }
    }
    LaunchedEffect(true) {
        Timber.d("LaunchedEffect checkAuthState")
        viewModel.checkAuthState(showErrorSnackbar)
    }
}
