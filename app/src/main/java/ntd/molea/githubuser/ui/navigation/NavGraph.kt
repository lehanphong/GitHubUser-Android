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

sealed class Screen(val route: String) {
    object UserList : Screen("users")
    object UserDetail : Screen("user/{userId}") {
        fun createRoute(userId: String) = "user/$userId"
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
                    navArgument("userId") {
                        type = NavType.StringType
                    }
                )
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getString("userId") ?: return@composable
                // In a real app, you would fetch user details here using userId
                val dummyUser = User(
                    login = userId,
                    id = 1,
                    avatarUrl = "https://avatars.githubusercontent.com/u/101?v=4",
                    htmlUrl = "https://github.com/$userId"
                )
                UserDetailScreen(
                    user = dummyUser,
                    onBackClick = { navController.navigateUp() }
                )
            }
        }
    }
} 