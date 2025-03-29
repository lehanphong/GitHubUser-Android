package ntd.molea.githubuser.data.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import ntd.molea.githubuser.data.remote.GitHubApi
import ntd.molea.githubuser.data.local.UserDao
import ntd.molea.githubuser.data.local.UserEntity
import ntd.molea.githubuser.data.model.User
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class UserRepositoryTest {

    @Mock
    private lateinit var api: GitHubApi

    @Mock
    private lateinit var dao: UserDao

    private lateinit var repository: UserRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        repository = UserRepositoryImpl(testDispatcher, api, dao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getUsers returns cached users when available`() = runTest {
        // Given
        val cachedUsers = listOf(
            UserEntity(
                id = 1,
                login = "user1",
                avatarUrl = "https://avatar1.com",
                htmlUrl = "https://github.com/user1"
            ),
            UserEntity(
                id = 2,
                login = "user2",
                avatarUrl = "https://avatar2.com",
                htmlUrl = "https://github.com/user2"
            )
        )
        `when`(dao.getUsersWithPagination(any(), any())).thenReturn(cachedUsers)

        // When
        val result = repository.getUsers()

        // Then
        assertEquals(2, result.size)
        assertEquals("user1", result[0].login)
        assertEquals("user2", result[1].login)
        verify(dao).getUsersWithPagination(20, 0)
        verifyNoMoreInteractions(api)
    }

    @Test
    fun `getUsers fetches from API when cache is empty`() = runTest {
        // Given
        val apiUsers = listOf(
            User(
                id = 1,
                login = "user1",
                avatarUrl = "https://avatar1.com",
                htmlUrl = "https://github.com/user1"
            ),
            User(
                id = 2,
                login = "user2",
                avatarUrl = "https://avatar2.com",
                htmlUrl = "https://github.com/user2"
            )
        )
        `when`(dao.getUsersWithPagination(any(), any())).thenReturn(emptyList())
        `when`(api.getUsers(any(), any())).thenReturn(apiUsers)

        // When
        val result = repository.getUsers()

        // Then
        assertEquals(2, result.size)
        assertEquals("user1", result[0].login)
        assertEquals("user2", result[1].login)
        verify(dao).getUsersWithPagination(20, 0)
        verify(api).getUsers(20, 0)
        verify(dao).insertUsers(any())
    }

    @Test
    fun `getUserDetails returns cached user when available with details`() = runTest {
        // Given
        val cachedUser = UserEntity(
            id = 1,
            login = "user1",
            avatarUrl = "https://avatar1.com",
            htmlUrl = "https://github.com/user1",
            location = "Vietnam",
            followers = 100,
            following = 50
        )
        `when`(dao.getUserByLogin("user1")).thenReturn(cachedUser)

        // When
        val result = repository.getUserDetails("user1")

        // Then
        assertEquals("user1", result.login)
        assertEquals("Vietnam", result.location)
        assertEquals(100, result.followers)
        assertEquals(50, result.following)
        verify(dao).getUserByLogin("user1")
        verifyNoMoreInteractions(api)
    }

    @Test
    fun `getUserDetails fetches from API when cache is empty or incomplete`() = runTest {
        // Given
        val apiUser = User(
            id = 1,
            login = "user1",
            avatarUrl = "https://avatar1.com",
            htmlUrl = "https://github.com/user1",
            location = "Vietnam",
            followers = 100,
            following = 50
        )
        `when`(dao.getUserByLogin("user1")).thenReturn(null)
        `when`(api.getUserDetails("user1")).thenReturn(apiUser)

        // When
        val result = repository.getUserDetails("user1")

        // Then
        assertEquals("user1", result.login)
        assertEquals("Vietnam", result.location)
        assertEquals(100, result.followers)
        assertEquals(50, result.following)
        verify(dao).getUserByLogin("user1")
        verify(api).getUserDetails("user1")
        verify(dao).updateUser(any())
    }

    @Test
    fun `refreshUsers clears cache and fetches from API`() = runTest {
        // Given
        val apiUsers = listOf(
            User(
                id = 1,
                login = "user1",
                avatarUrl = "https://avatar1.com",
                htmlUrl = "https://github.com/user1"
            ),
            User(
                id = 2,
                login = "user2",
                avatarUrl = "https://avatar2.com",
                htmlUrl = "https://github.com/user2"
            )
        )
        `when`(api.getUsers(any(), any())).thenReturn(apiUsers)

        // When
        val result = repository.refreshUsers()

        // Then
        assertEquals(2, result.size)
        assertEquals("user1", result[0].login)
        assertEquals("user2", result[1].login)
        verify(dao).deleteAllUsers()
        verify(api).getUsers(20, 0)
        verify(dao).insertUsers(any())
    }
} 