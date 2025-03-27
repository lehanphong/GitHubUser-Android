package ntd.molea.githubuser.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ntd.molea.githubuser.data.model.User
import ntd.molea.githubuser.ui.screens.UserDetailScreen
import ntd.molea.githubuser.ui.screens.UserListScreen
import ntd.molea.githubuser.ui.theme.GitHubUserTheme
import ntd.molea.githubuser.ui.viewmodels.UserDetailViewModel
import org.koin.androidx.compose.koinViewModel

sealed class Screen(val route: String) {
    object UserList : Screen("users")
    object UserDetail : Screen("user/{login}") {
        fun createRoute(login: String) = "user/$login"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.UserList.route
) {
    GitHubUserTheme {
        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable(Screen.UserList.route) {
                UserListScreen(
                    onUserClick = { user ->
                        navController.navigate(Screen.UserDetail.createRoute(user.login))
                    },
                    onBackClick = {
                        navController.navigateUp()
                    }

                )
            }

            composable(
                route = Screen.UserDetail.route,
                arguments = listOf(
                    navArgument("login") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val login = backStackEntry.arguments?.getString("login") ?: return@composable
                UserDetailScreen(
                    login = login,
                    onBackClick = { navController.navigateUp() }
                )
            }
        }
    }
} 