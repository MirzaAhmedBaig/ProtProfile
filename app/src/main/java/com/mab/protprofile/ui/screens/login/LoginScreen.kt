package com.mab.protprofile.ui.screens.login

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import com.mab.protprofile.R
import com.mab.protprofile.data.model.ErrorMessage
import com.mab.protprofile.ui.components.StandardButton
import com.mab.protprofile.ui.data.AuthStateNew
import com.mab.protprofile.ui.navigation.RouteInfo
import com.mab.protprofile.ui.theme.ProtProfileTheme
import kotlinx.serialization.Serializable
import timber.log.Timber

@Serializable
object LoginRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    goto: (RouteInfo) -> Unit,
    showErrorSnackbar: (ErrorMessage) -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    Timber.d("LoginScreenContent recomposed")
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsState()
    var number by remember { mutableStateOf("") }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    Scaffold(
        Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        ConstraintLayout(
            modifier =
                Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                focusManager.clearFocus()
                            },
                        )
                    }
                    .padding(innerPadding),
        ) {
            val (form) = createRefs()

            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }

            Column(
                modifier =
                    Modifier
                        .constrainAs(form) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                            height = Dimension.fillToConstraints
                        },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Image(
                    modifier = Modifier.size(88.dp),
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = stringResource(R.string.app_logo),
                )

                OutlinedTextField(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .padding(horizontal = 24.dp),
                    value = number,
                    prefix = { Text("+91") },
                    singleLine = true,
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Done,
                        ),
                    onValueChange = {
                        if (it.length <= 10) {
                            number = it
                        }
                    },
                    label = { Text(stringResource(R.string.phone_number)) },
                )

                Spacer(Modifier.size(24.dp))

                when (authState) {
                    is AuthStateNew.Loading -> CircularProgressIndicator()
                    is AuthStateNew.CodeSent -> {
                        val verificationId = (authState as AuthStateNew.CodeSent).verificationId
                        LaunchedEffect(verificationId) {
                            goto(
                                RouteInfo.OtpVerification(
                                    verificationId,
                                ),
                            )
                        }
                    }

                    is AuthStateNew.Verified -> {
                        LaunchedEffect(Unit) { goto(RouteInfo.Home) }
                    }

                    else -> {
                        var errorMessage: String?
                        if (authState is AuthStateNew.Error) {
                            errorMessage = "Error: ${(authState as AuthStateNew.Error).message}"
                            showErrorSnackbar(ErrorMessage.StringError(errorMessage))
                        }
                        StandardButton(
                            label = R.string.sign_in,
                            onButtonClick = {
                                Timber.d("Sign in button clicked")
                                if (number.isBlank() || number.length < 10) {
                                    Timber.w("Invalid Mobile Number, showing error.")
                                    showErrorSnackbar(ErrorMessage.StringError("Invalid Mobile Number"))
                                    return@StandardButton
                                }

                                if (context is Activity) {
                                    viewModel.startPhoneNumberVerification(
                                        "+91$number",
                                        context,
                                        showErrorSnackbar,
                                    )
                                }
                            },
                        )
                    }
                }

                Spacer(Modifier.size(40.dp))
            }
        }
    }
}

@Composable
@Preview
fun LoginScreenPreview() {
    ProtProfileTheme {
        Surface {
            LoginScreen(
                goto = { },
                showErrorSnackbar = {},
            )
        }
    }
}
