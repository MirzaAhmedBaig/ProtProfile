package com.mab.protprofile.ui.navigation


import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.mab.protprofile.ui.screens.add.AddEntryRoute
import com.mab.protprofile.ui.screens.add.AddEntryScreen
import com.mab.protprofile.ui.screens.home.HomeRoute
import com.mab.protprofile.ui.screens.home.HomeScreen
import com.mab.protprofile.ui.screens.login.LoginRoute
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mab.protprofile.data.model.ErrorMessage
import com.mab.protprofile.data.model.Transaction
import com.mab.protprofile.ui.Constants
import com.mab.protprofile.ui.emaillogin.EmailLoginRoute
import com.mab.protprofile.ui.emaillogin.EmailLoginScreen
import com.mab.protprofile.ui.screens.history.HistoryRoute
import com.mab.protprofile.ui.screens.history.HistoryScreen
import com.mab.protprofile.ui.screens.login.LoginScreen
import com.mab.protprofile.ui.otp.OtpVerificationRoute
import com.mab.protprofile.ui.otp.OtpVerificationScreen
import com.mab.protprofile.ui.screens.view.ViewEntriesRoute
import com.mab.protprofile.ui.screens.view.ViewEntriesScreen
import java.net.URLDecoder
import java.net.URLEncoder
import timber.log.Timber

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    showErrorSnackbar: (ErrorMessage) -> Unit,
    navController: NavHostController = rememberNavController(),
    startDestination: Any = LoginRoute,
) {
    Timber.d("AppNavGraph: startDestination=$startDestination")
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<HomeRoute> {
            HomeScreen(
                openAddItemScreen = { transId ->
                    Timber.d("Navigating to AddEntryRoute with transId: $transId")
                    navController.navigate(AddEntryRoute(transId)) { launchSingleTop = false }
                },
                openSignInScreen = {
                    Timber.d("Navigating to LoginRoute")
                    navController.navigate(LoginRoute) {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                },
                openTransactionsScreen = { transactions ->
                    val transactionsJson = URLEncoder.encode(Gson().toJson(transactions), "UTF-8")
                    Timber.d("Navigating to ViewEntriesRoute with transactions: $transactionsJson")
                    navController.navigate(ViewEntriesRoute.createRoute(transactionsJson)) {
                        launchSingleTop = false
                    }
                },
                openTransactionsHistoryScreen = {
                    navController.navigate(HistoryRoute) {
                        launchSingleTop = false
                        Timber.d("Navigating to HistoryRoute")
                    }

                },
                showErrorSnackbar = showErrorSnackbar
            )
        }
        composable<EmailLoginRoute> {
            EmailLoginScreen(
                openHomeScreen = {
                    Timber.d("Navigating to HomeRoute from Email Login")
                    navController.navigate(HomeRoute) {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                },
                showErrorSnackbar = showErrorSnackbar
            )
        }
        composable<AddEntryRoute> {
            AddEntryScreen(
                onBack = { updated ->
                    if (updated) {
                        Timber.d("AddEntryScreen: Popping back stack with updated=true")
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set(Constants.SHOULD_REFRESH_KEY, true)
                    }
                    navController.popBackStack()
                    Timber.d("AddEntryScreen: Popped back stack")
                },
                showErrorSnackbar = showErrorSnackbar
            )
        }

        composable(
            route = ViewEntriesRoute.DESTINATION,
            arguments = listOf(navArgument(ViewEntriesRoute.TRANSACTION_ARG) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val json = backStackEntry.arguments?.getString(ViewEntriesRoute.TRANSACTION_ARG)
            Timber.d("ViewEntriesRoute: Received transactionsJson: $json")
            val transactions = json?.let {
                val decoded = URLDecoder.decode(it, "UTF-8")
                Timber.d("ViewEntriesRoute: Decoded transactionsJson: $decoded")
                val type = object : TypeToken<List<Transaction>>() {}.type
                Gson().fromJson(decoded, type)
            } ?: run {
                Timber.w("ViewEntriesRoute: transactionsJson is null, defaulting to empty list")
                emptyList<Transaction>()
            }
            ViewEntriesScreen(
                transactions = transactions,
                onBack = {
                    Timber.d("ViewEntriesScreen: Popping back stack")
                    navController.popBackStack()
                },
                openEditScreen = { transId ->
                    Timber.d("ViewEntriesScreen: Navigating to AddEntryRoute for edit with transId: $transId")
                    navController.navigate(AddEntryRoute(transId))
                },
                navBackStackEntry = backStackEntry,
                showErrorSnackbar = showErrorSnackbar
            )
        }

        composable<LoginRoute> {
            LoginScreen(
                onCodeSent = { verificationId ->
                    Timber.d("PhoneLoginScreen: onCodeSent")
                    navController.navigate(OtpVerificationRoute(verificationId))
                },
                onVerified = {
                    Timber.d("Navigating to HomeRoute from Number Login")
                    navController.navigate(HomeRoute) {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                },
                showErrorSnackbar = showErrorSnackbar
            )
        }

        composable<OtpVerificationRoute> { backStackEntry ->
            val verificationId =
                backStackEntry.savedStateHandle.toRoute<OtpVerificationRoute>().verificationId
            Timber.d("OtpVerificationRoute: Received verificationId: $verificationId")
            OtpVerificationScreen(
                onVerified = {
                    Timber.d("OtpVerificationScreen: onVerified")
                    navController.navigate(HomeRoute) {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                },
                showErrorSnackbar = showErrorSnackbar
            )
        }
        composable<HistoryRoute> {
            HistoryScreen(
                onBack = {
                    Timber.d("HistoryScreen: Popping back stack")
                    navController.popBackStack()
                },
                showErrorSnackbar = showErrorSnackbar
            )
        }
    }
}
