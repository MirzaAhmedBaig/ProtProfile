package com.mab.protprofile.ui.navigation


import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.mab.protprofile.ui.screens.addTransection.AddViewTransactionRoute
import com.mab.protprofile.ui.screens.addTransection.AddViewTransactionScreen
import com.mab.protprofile.ui.screens.home.HomeRoute
import com.mab.protprofile.ui.screens.home.HomeScreen
import com.mab.protprofile.ui.screens.login.LoginRoute
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
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
import com.mab.protprofile.ui.ext.getCustomArg
import com.mab.protprofile.ui.ext.refresh
import com.mab.protprofile.ui.screens.addExpense.AddViewExpenseRoute
import com.mab.protprofile.ui.screens.addExpense.AddViewExpenseScreen
import com.mab.protprofile.ui.screens.emaillogin.EmailLoginRoute
import com.mab.protprofile.ui.screens.emaillogin.EmailLoginScreen
import com.mab.protprofile.ui.screens.investments.InvestmentSummaryRoute
import com.mab.protprofile.ui.screens.investments.InvestmentSummaryScreen
import com.mab.protprofile.ui.screens.transactionsHistory.HistoryRoute
import com.mab.protprofile.ui.screens.transactionsHistory.HistoryScreen
import com.mab.protprofile.ui.screens.login.LoginScreen
import com.mab.protprofile.ui.screens.otp.OtpVerificationRoute
import com.mab.protprofile.ui.screens.otp.OtpVerificationScreen
import com.mab.protprofile.ui.screens.viewExpenses.ViewExpensesRoute
import com.mab.protprofile.ui.screens.viewExpenses.ViewExpensesScreen
import com.mab.protprofile.ui.screens.viewTransactions.ViewTransactionsRoute
import com.mab.protprofile.ui.screens.viewTransactions.ViewTransactionsScreen
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
        modifier = modifier,
    ) {
        composable<HomeRoute> {
            HomeScreen(
                goto = { gotoRoute(it, navController = navController) },
                showErrorSnackbar = showErrorSnackbar,
            )
        }
        composable<EmailLoginRoute> {
            EmailLoginScreen(
                goto = { gotoRoute(it, navController = navController) },
                showErrorSnackbar = showErrorSnackbar,
            )
        }
        composable<AddViewTransactionRoute> {
            AddViewTransactionScreen(
                goto = { gotoRoute(it, navController = navController) },
                showErrorSnackbar = showErrorSnackbar,
            )
        }

        composable(
            route = ViewTransactionsRoute.DESTINATION,
            arguments = listOf(
                navArgument(ViewTransactionsRoute.TRANSACTION_ARG) {
                    type = NavType.StringType
                },
            ),
        ) { backStackEntry ->
            val transactions =
                backStackEntry.getCustomArg<List<Transaction>>(ViewTransactionsRoute.TRANSACTION_ARG)
                    ?: run {
                        Timber.w("ViewEntriesRoute: transactionsJson is null, defaulting to empty list")
                        emptyList()
                    }
            ViewTransactionsScreen(
                transactions = transactions,
                goto = { gotoRoute(it, navController = navController) },
                navBackStackEntry = backStackEntry,
                showErrorSnackbar = showErrorSnackbar,
            )
        }

        composable<LoginRoute> {
            LoginScreen(
                goto = { gotoRoute(it, navController = navController) },
                showErrorSnackbar = showErrorSnackbar,
            )
        }

        composable<OtpVerificationRoute> { backStackEntry ->
            val verificationId =
                backStackEntry.savedStateHandle.toRoute<OtpVerificationRoute>().verificationId
            Timber.d("OtpVerificationRoute: Received verificationId: $verificationId")
            OtpVerificationScreen(
                goto = { gotoRoute(it, navController = navController) },
                showErrorSnackbar = showErrorSnackbar,
            )
        }
        composable<HistoryRoute> {
            HistoryScreen(
                goto = { gotoRoute(it, navController = navController) },
                showErrorSnackbar = showErrorSnackbar,
            )
        }

        composable<InvestmentSummaryRoute> {
            InvestmentSummaryScreen(
                goto = { gotoRoute(it, navController = navController) },
                showErrorSnackbar = showErrorSnackbar,
            )
        }

        composable<ViewExpensesRoute> { backStackEntry ->
            ViewExpensesScreen(
                showErrorSnackbar = showErrorSnackbar,
                goto = { gotoRoute(it, navController = navController) },
                navBackStackEntry = backStackEntry,
            )
        }

        composable<AddViewExpenseRoute> { backStackEntry ->
            AddViewExpenseScreen(
                showErrorSnackbar = showErrorSnackbar,
                goto = { gotoRoute(it, navController = navController) },
            )
        }
    }
}

fun gotoRoute(route: RouteInfo, navController: NavHostController) {
    when (route) {
        is RouteInfo.OnBack -> {
            if (route.shouldRefresh) {
                navController.refresh()
            }
            navController.popBackStack()
        }

        is RouteInfo.Login -> {
            navController.navigate(LoginRoute) {
                popUpTo(0)
                launchSingleTop = true
            }
        }

        is RouteInfo.OtpVerification -> {
            navController.navigate(OtpVerificationRoute(route.verificationId))
        }

        is RouteInfo.EmailLogin -> {

        }

        is RouteInfo.Home -> {
            navController.navigate(HomeRoute) {
                popUpTo(0)
                launchSingleTop = true
            }
        }

        is RouteInfo.AddViewTransaction -> {
            navController.navigate(AddViewTransactionRoute(route.transId))
        }

        is RouteInfo.ViewTransactions -> {
            val transactionsJson = URLEncoder.encode(Gson().toJson(route.transactions), "UTF-8")
            navController.navigate(ViewTransactionsRoute.createRoute(transactionsJson))
        }

        is RouteInfo.AddViewExpense -> {
            navController.navigate(AddViewExpenseRoute(route.expenseId))
        }

        is RouteInfo.ViewExpenses -> {
            navController.navigate(ViewExpensesRoute)
        }

        is RouteInfo.InvestmentSummary -> {
            navController.navigate(InvestmentSummaryRoute(route.totalProfit))
        }

        is RouteInfo.TransactionsHistory -> {
            navController.navigate(HistoryRoute)
        }
    }
}

