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

            val orig = Build.PRODUCT
            val patched = "OnePlus8T_EEA"

            val orig = Build.DEVICE
            val patched = "OnePlus8T"

            val orig = Build.MODEL
            val patched = "KB2003"

            val orig = Build.FINGERPRINT
            val patched = "OnePlus/OnePlus8T_EEA/OnePlus8T:13/RKQ1.211119.001/R.d81a34_19a89_3:user/release-keys"

            logDebug("patch build for castore $orig -> $patched")
            Build::class.java.getDeclaredField("PRODUCT").let { field ->
                field.isAccessible = true
                field.set(null, patched)
            }
            logDebug("patch build for castore $orig -> $patched")
            Build::class.java.getDeclaredField("DEVICE").let { field ->
                field.isAccessible = true
                field.set(null, patched)
            }
            logDebug("patch build for castore $orig -> $patched")
            Build::class.java.getDeclaredField("MODEL").let { field ->
                field.isAccessible = true
                field.set(null, patched)
            }
            logDebug("patch build for castore $orig -> $patched")
            Build::class.java.getDeclaredField("FINGERPRINT").let { field ->
                field.isAccessible = true
                field.set(null, patched)
            }

            thread(isDaemon = true) {
                Thread.sleep(PATCH_DURATION)
                logDebug("unpatch")
                Build::class.java.getDeclaredField("PRODUCT").let { field ->
                    field.isAccessible = true
                    field.set(null, orig)
                }
                Build::class.java.getDeclaredField("DEVICE").let { field ->
                    field.isAccessible = true
                    field.set(null, orig)
                }
                Build::class.java.getDeclaredField("MODEL").let { field ->
                    field.isAccessible = true
                    field.set(null, orig)
                }
                Build::class.java.getDeclaredField("FINGERPRINT").let { field ->
                    field.isAccessible = true
                    field.set(null, orig)
                }
            }
        }
        return super.getService(type, algorithm)
    }

    override fun getServices(): MutableSet<Service>? {
        logDebug("Get services")
        return super.getServices()
    }
}
