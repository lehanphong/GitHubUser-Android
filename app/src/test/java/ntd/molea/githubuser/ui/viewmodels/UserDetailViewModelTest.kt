package ntd.molea.githubuser.ui.viewmodels

import app.cash.turbine.test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import ntd.molea.githubuser.data.model.User
import ntd.molea.githubuser.data.repository.UserRepository
import ntd.molea.githubuser.ui.userdetail.UserDetailViewModel
import ntd.molea.githubuser.utils.MockLogger
import ntd.molea.githubuser.utils.DLog
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class UserDetailViewModelTest {

    @Mock
    private lateinit var repository: UserRepository

    private lateinit var viewModel: UserDetailViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val mockLogger = MockLogger()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        DLog.setLogger(mockLogger)
        viewModel = UserDetailViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mockLogger.clear()
    }

    @Test
    fun `initial state should be null`() = runTest {
        viewModel.user.test {
            assertNull(awaitItem())
        }
    }

    @Test
    fun `getUserDetails should update user state when successful`() = runTest {
        // Given
        val expectedUser = User(
            login = "mojombo",
            id = 1,
            avatarUrl = "https://avatars.githubusercontent.com/u/1?v=4",
            htmlUrl = "https://github.com/mojombo",
            location = "San Francisco",
            followers = 100,
            following = 50
        )
        whenever(repository.getUserDetails("mojombo")).thenReturn(expectedUser)

        // When
        viewModel.getUserDetails("mojombo")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.user.test {
            val actualUser = awaitItem()
            println("Updated user: $actualUser")
            assertEquals(expectedUser, actualUser)
        }
    }

    @Test
    fun `getUserDetails should set user to null when error occurs`() = runTest {
        // Given
        whenever(repository.getUserDetails("invalid-user"))
            .thenThrow(RuntimeException("Network error"))

        // When
        viewModel.getUserDetails("invalid-user")
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.user.test {
            val errorResult = awaitItem()
            println("Error result: $errorResult")
            assertNull(errorResult)
        }
    }

    @Test
    fun `getUserDetails should call repository once`() = runTest {
        // Given
        val username = "mojombo"
        whenever(repository.getUserDetails(username))
            .thenReturn(User("mojombo", 1, "avatar_url", "html_url"))

        // When
        viewModel.getUserDetails(username)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(repository).getUserDetails(username)
    }
} 