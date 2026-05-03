package returns.mingleday.app.ui.auth

import androidx.lifecycle.ViewModel
import returns.mingleday.app.data.remote.model.auth.Purpose

class AuthViewModel : ViewModel() {
    var email: String? = null
    var name: String? = null
    var password: String? = null
    var nickname: String? = null
    var purpose: Purpose = Purpose.REGISTER
}