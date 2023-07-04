package top.limuyang2.pohotopicker

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.Size
import androidx.core.content.ContextCompat

fun hasPermissions(
    context: Context, @Size(min = 1) vararg perms: String
): Boolean {
    // Always return true for SDK < M, let the system deal with the permissions
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        return true
    }

    // Null context may be passed if we have detected Low API (less than M) so getting
    // to this point with a null context should not be possible.
    for (perm in perms) {
        if (ContextCompat.checkSelfPermission(context, perm)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
    }
    return true
}