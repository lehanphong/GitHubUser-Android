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
import ntd.molea.githubuser.utils.MockLogger
import ntd.molea.githubuser.utils.Vlog
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class UsersViewModelTest {

    @Mock
    private lateinit var repository: UserRepository

    private lateinit var viewModel: UsersViewModel
    private val testDispatcher = StandardTestDispatcher()
    private val mockLogger = MockLogger()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        Vlog.setLogger(mockLogger)
        viewModel = UsersViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mockLogger.clear()
    }

    @Test
    fun `initial state should fetch users automatically`() = runTest {
        // Given
        val initialUsers = listOf(
            User("mojombo", 1, "avatar1", "html1"),
            User("defunkt", 2, "avatar2", "html2")
        )
        whenever(repository.getUsers(0, 20)).thenReturn(initialUsers)

        // When
        viewModel = UsersViewModel(repository) // Recreate viewModel to trigger init
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.users.test {
            val users = awaitItem()
            println("Initial users: $users")
            assertEquals(initialUsers, users)
        }

        viewModel.isLoading.test {
            assertFalse(awaitItem()) // Final state should be not loading
        }
    }

    @Test
    fun `refreshUsers should clear and update users list`() = runTest {
        // Given
        val newUsers = listOf(
            User("mojombo", 1, "avatar1", "html1"),
            User("defunkt", 2, "avatar2", "html2")
        )
        whenever(repository.refreshUsers(0, 20)).thenReturn(newUsers)

        // When
        viewModel.refreshUsers()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.users.test {
            val users = awaitItem()
            println("Refreshed users: $users")
            assertEquals(newUsers, users)
        }

        viewModel.isLoading.test {
            assertFalse(awaitItem()) // Should end with not loading
        }
    }

    @Test
    fun `loadMore should fetch next page of users`() = runTest {
        // Given
        val firstPageUsers = listOf(User("user1", 1, "avatar1", "html1"))
        val secondPageUsers = listOf(User("user2", 2, "avatar2", "html2"))
        
        whenever(repository.getUsers(0, 20)).thenReturn(firstPageUsers)
        whenever(repository.getUsers(20, 20)).thenReturn(secondPageUsers)

        // When - First page (auto-fetch from init)
        viewModel = UsersViewModel(repository)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - First page
        viewModel.users.test {
            val initialUsers = awaitItem()
            println("First page users: $initialUsers")
            assertEquals(firstPageUsers, initialUsers)
        }

        // When - Second page
        viewModel.loadMore()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then - Both pages
        viewModel.users.test {
            val allUsers = awaitItem()
            println("All users after loadMore: $allUsers")
            assertEquals(firstPageUsers + secondPageUsers, allUsers)
        }
    }

    @Test
    fun `loadMore should not fetch when already loading`() = runTest {
        // Given
        whenever(repository.getUsers(any(), any())).thenReturn(listOf())
        testDispatcher.scheduler.advanceUntilIdle()
        
        // When
        viewModel.loadMore()
        viewModel.loadMore() // Second call while still loading
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        verify(repository).getUsers(20, 20) // Verify chỉ gọi một lần với offset = 20 (trang thứ 2)
    }

    @Test
    fun `loading state should be updated correctly during fetch`() = runTest {
        // Given
        whenever(repository.getUsers(any(), any())).thenReturn(listOf())

        // When & Then
        viewModel.isLoading.test {
            val initial = awaitItem()
            println("Initial loading state: $initial")
            assertFalse(initial) // Initial state should be false

            viewModel.loadMore() // Trigger loading

            val loading = awaitItem()
            println("During loading state: $loading")
            assertTrue(loading) // Should be true while loading

            testDispatcher.scheduler.advanceUntilIdle() // Let coroutine complete

            val completed = awaitItem()
            println("Completed loading state: $completed")
            assertFalse(completed) // Should be false after completion
        }
    }

    @Test
    fun `error during fetch should not crash and reset loading state`() = runTest {
        // Given
        whenever(repository.getUsers(any(), any()))
            .thenThrow(RuntimeException("Network error"))

        // When
        viewModel.loadMore()
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        viewModel.isLoading.test {
            assertFalse(awaitItem()) // Should end with not loading
        }

        viewModel.users.test {
            val users = awaitItem()
            println("Users after error: $users")
            assertTrue(users.isEmpty()) // Should maintain empty list
        }
    }
}
