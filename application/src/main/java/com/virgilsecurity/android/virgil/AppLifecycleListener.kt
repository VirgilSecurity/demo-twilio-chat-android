import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.virgilsecurity.android.bcommon.data.helper.smack.SmackHelper
import com.virgilsecurity.android.bcommon.data.helper.smack.SmackRx
import com.virgilsecurity.android.bcommon.data.helper.virgil.VirgilHelper
import org.koin.core.KoinComponent
import org.koin.core.inject

class AppLifecycleListener : LifecycleObserver, KoinComponent {
    private val smackHelper: SmackHelper by inject()

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() { // app moved to foreground
        try {
            smackHelper.setPresenceAvailable()
            Log.d("[SMACK]", " Presence: available")
        }
        catch (e: Exception) {
            Log.d("[SMACK]", " Presence: available (failed: $e)")
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onMoveToBackground() { // app moved to background
        try {
            smackHelper.setPresenceUnavailable()
            Log.d("[SMACK]", " Presence: unavailable")
        }
        catch (e: Exception) {
            Log.d("[SMACK]", " Presence: unavailable (failed: $e)")
        }
    }
}