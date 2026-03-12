package com.privacyguard.service

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.privacyguard.ml.PrivacyModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Manages the lifecycle of the PrivacyModel, ensuring it is initialized
 * when the app starts and cleaned up when it's no longer needed.
 */
class ModelLifecycleManager(
    private val context: Context,
    private val personalKey: String,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : DefaultLifecycleObserver {

    private val model = PrivacyModel.getInstance()

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        scope.launch {
            model.initialize(context, personalKey)
            model.warmUp()
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        model.close()
    }

    fun getModel(): PrivacyModel = model
}
