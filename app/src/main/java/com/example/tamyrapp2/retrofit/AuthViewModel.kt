import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tamyrapp2.retrofit.AuthResponse
import com.example.tamyrapp2.retrofit.RegisterRequest
import com.example.tamyrapp2.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel : ViewModel() {
    private val _accessToken = MutableLiveData<String?>()
    val accessToken: LiveData<String?> = _accessToken

    private val _refreshToken = MutableLiveData<String?>()
    val refreshToken: LiveData<String?> = _refreshToken

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun registerUser(username: String, email: String, password: String, firstName: String, lastName: String) {
        val request = RegisterRequest(username, email, password, firstName, lastName)
        RetrofitInstance.api.registerUser(request).enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    _accessToken.value = response.body()?.accessToken
                    _refreshToken.value = response.body()?.refreshToken
                } else {
                    _error.value = "Ошибка регистрации: ${response.code()}"
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                _error.value = "Ошибка сети: ${t.message}"
            }
        })
    }
}