package id.petersam.duitmu.model.google

import android.net.Uri

data class User(
    val name: String,
    val email: String,
    val profilePicture: Uri?
)
