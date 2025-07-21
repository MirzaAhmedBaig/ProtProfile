package com.mab.protprofile.ui.screens.otp

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
data class OtpVerificationRoute(val verificationId: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerificationScreen(
    showErrorSnackbar: (ErrorMessage) -> Unit,
    goto: (RouteInfo) -> Unit,
    viewModel: OtpVerificationViewModel = hiltViewModel(),
) {
    Timber.d("OtpVerificationScreen composable called")
    var otp by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    Timber.d("Initial authState: $authState")

    Scaffold(
        Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    ) { innerPadding ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    Timber.d("Pointer input detected, clearing focus.")
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
                Timber.d("Requesting focus for OTP field.")
                focusRequester.requestFocus()
            }

            Column(
                modifier = Modifier
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .padding(horizontal = 24.dp),
                    value = otp,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done,
                    ),
                    onValueChange = {
                        Timber.d("OTP value changed: $it")
                        if (it.length <= 6) {
                            otp = it
                        }
                    },
                    label = { Text(stringResource(R.string.otp)) },
                )

                Spacer(Modifier.size(24.dp))

                when (authState) {
                    is AuthStateNew.Loading -> CircularProgressIndicator()
                    is AuthStateNew.CodeSent -> {
                        Timber.w("Invalid State for OTP Screen: AuthStateNew.CodeSent")
                    }

                    is AuthStateNew.Verified -> {
                        Timber.i("OTP Verified, navigating.")
                        LaunchedEffect(Unit) { goto(RouteInfo.Home) }
                    }

                    else -> {
                        var errorMessage: String?
                        if (authState is AuthStateNew.Error) {
                            val errorState = authState as AuthStateNew.Error
                            errorMessage = "Error: ${errorState.message}"
                            Timber.e("OTP Verification Error: $errorMessage")
                            showErrorSnackbar(ErrorMessage.StringError(errorMessage))
                        }
                        StandardButton(
                            label = R.string.verify,
                            onButtonClick = {
                                focusManager.clearFocus()
                                Timber.d("Verify button clicked with OTP: $otp")
                                if (otp.isBlank() || otp.length < 6) {
                                    Timber.w("Invalid OTP, showing error.")
                                    showErrorSnackbar(ErrorMessage.StringError("Invalid OTP"))
                                    return@StandardButton
                                }
                                viewModel.verifyOtp(otp, showErrorSnackbar)
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
            OtpVerificationScreen(
                goto = {},
                showErrorSnackbar = {},
            )
        }

    }
}
