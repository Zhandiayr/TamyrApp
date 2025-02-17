import com.example.tamyrapp2.retrofit.AuthResponse
import com.example.tamyrapp2.retrofit.RegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("login") // Эндпоинт для входа
    fun registerUser(@Body request: RegisterRequest): Call<AuthResponse>
}