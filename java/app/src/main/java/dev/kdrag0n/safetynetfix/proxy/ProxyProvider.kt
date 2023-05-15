package dev.kdrag0n.safetynetfix.proxy

import android.os.Build
import dev.kdrag0n.safetynetfix.SecurityHooks
import dev.kdrag0n.safetynetfix.logDebug
import java.security.Provider
import kotlin.concurrent.thread

private const val PATCH_DURATION = 2000L

// This is mostly just a pass-through provider that exists to change the provider's ClassLoader.
// This works because Service looks up the class by name from the *provider* ClassLoader, not
// necessarily the bootstrap one.
class ProxyProvider(
    orig: Provider,
) : Provider(orig.name, orig.version, orig.info) {
    init {
        logDebug("Init proxy provider - wrapping $orig")

        putAll(orig)
        this["KeyStore.${SecurityHooks.PROVIDER_NAME}"] = ProxyKeyStoreSpi::class.java.name
    }

    override fun getService(type: String?, algorithm: String?): Service? {
        logDebug("Provider: get service - type=$type algorithm=$algorithm")
        if (algorithm == "AndroidCAStore") {

            val origProduct = Build.PRODUCT
            val patchedProduct = "OnePlus8T_EEA"

            val origDevice = Build.DEVICE
            val patchedDevice = "OnePlus8T"

            val origModel = Build.MODEL
            val patchedModel = "KB2003"

            val origFingerprint = Build.FINGERPRINT
            val patchedFingerprint = "OnePlus/OnePlus8T_EEA/OnePlus8T:13/RKQ1.211119.001/R.d81a34_19a89_3:user/release-keys"

            logDebug("patch build for castore $origProduct -> $patchedProduct")
            Build::class.java.getDeclaredField("PRODUCT").let { field ->
                field.isAccessible = true
                field.set(null, patchedProduct)
            }
            logDebug("patch build for castore $origDevice -> $patchedDevice")
            Build::class.java.getDeclaredField("DEVICE").let { field ->
                field.isAccessible = true
                field.set(null, patchedDevice)
            }
            logDebug("patch build for castore $origModel -> $patchedModel")
            Build::class.java.getDeclaredField("MODEL").let { field ->
                field.isAccessible = true
                field.set(null, patchedModel)
            }
            logDebug("patch build for castore $origFingerprint -> $patchedFingerprint")
            Build::class.java.getDeclaredField("FINGERPRINT").let { field ->
                field.isAccessible = true
                field.set(null, patchedFingerprint)
            }

        }
        return super.getService(type, algorithm)
    }

    override fun getServices(): MutableSet<Service>? {
        logDebug("Get services")
        return super.getServices()
    }
}
