package de.justjanne.modding.kovarna

import de.justjanne.modding.kovarna.extensions.kotlinInstance
import de.justjanne.modding.kovarna.model.EventBusSubscriberMeta
import net.minecraftforge.fml.Bindings
import net.minecraftforge.fml.Logging.LOADING
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus
import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraftforge.forgespi.language.ModFileScanData

internal object AutoKotlinEventBusSubscriber {
  fun inject(mod: KotlinModContainer, scanData: ModFileScanData, classLoader: ClassLoader) {
    LOGGER.debug(LOADING, "Subscribing @EventBusSubscriber objects to the event bus for ${mod.modId}")

    val annotations = scanData.annotations.mapNotNull { EventBusSubscriberMeta.of(it) }
    for (annotation in annotations) {
      val bus = when (annotation.bus) {
        Bus.FORGE -> Bindings.getForgeBus().get()
        Bus.MOD -> mod.eventBus
      }

      if (mod.modId == annotation.modId && FMLEnvironment.dist in annotation.sides) {
        val targetClass = Class.forName(annotation.target, true, classLoader)
        val targetObject = targetClass.kotlinInstance

        if (targetObject != null) {
          LOGGER.debug(LOADING, "Auto-subscribing object ${annotation.target} to ${annotation.bus}")
          bus.register(targetObject)
        } else {
          LOGGER.debug(LOADING, "Auto-subscribing class ${annotation.target} to ${annotation.bus}")
          bus.register(targetClass)
        }
      }
    }
  }
}
