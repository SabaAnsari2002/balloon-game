import android.media.MediaPlayer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

class MusicLifecycleObserver(
    private val mediaPlayer: MediaPlayer
) : LifecycleEventObserver {
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_PAUSE -> {
                if (mediaPlayer.isPlaying) {
                    mediaPlayer.pause()
                }
            }
            Lifecycle.Event.ON_RESUME -> {
                mediaPlayer.start()
            }
            else -> {}
        }
    }
}
