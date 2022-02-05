package de.justjanne.modding.kovarna

import de.justjanne.modding.kovarna.extensions.kotlinInstance
import de.justjanne.modding.kovarna.extensions.loadClass
import de.justjanne.modding.kovarna.extensions.module
import de.justjanne.modding.kovarna.extensions.supplierOf
import net.minecraftforge.eventbus.EventBusErrorMessage
import net.minecraftforge.eventbus.api.BusBuilder
import net.minecraftforge.eventbus.api.Event
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.eventbus.api.IEventExceptionHandler
import net.minecraftforge.eventbus.api.IEventListener
import net.minecraftforge.fml.ModContainer
import net.minecraftforge.fml.ModLoadingException
import net.minecraftforge.fml.ModLoadingStage
import net.minecraftforge.fml.event.IModBusEvent
import net.minecraftforge.fml.loading.LogMarkers.LOADING
import net.minecraftforge.forgespi.language.IModInfo
import net.minecraftforge.forgespi.language.ModFileScanData
import java.util.*
import java.util.function.Consumer

internal class KotlinModContainer(
  info: IModInfo,
  className: String,
  private val scanResults: ModFileScanData,
  layer: ModuleLayer
) : ModContainer(info) {
  private lateinit var modInstance: Any

  private val modClass = layer.module(info.owningFile.moduleName()).loadClass(className)

  val eventBus: IEventBus = BusBuilder.builder()
    .setExceptionHandler(IEventExceptionHandler(::onEventFailed))
    .setTrackPhases(false)
    .markerType(IModBusEvent::class.java)
    .build()

  init {
    LOGGER.debug(LOADING, "Creating FMLModContainer instance for {}", className)

    activityMap[ModLoadingStage.CONSTRUCT] = Runnable(this::onConstruct)
    configHandler = Optional.of(Consumer { eventBus.post(it.self()) })
    contextExtension = supplierOf(KotlinModLoadingContext(this))
  }

  private fun onEventFailed(
    bus: IEventBus,
    event: Event,
    listeners: Array<IEventListener>,
    index: Int,
    throwable: Throwable
  ) {
    LOGGER.error(EventBusErrorMessage(event, index, listeners, throwable))
  }

  private fun onConstruct() {
    try {
      LOGGER.trace(LOADING, "Loading mod instance {} of type {}", getModId(), modClass.name)
      modInstance = modClass.kotlinInstance ?: modClass.getDeclaredConstructor().newInstance()
      LOGGER.trace(LOADING, "Loaded mod instance {} of type {}", getModId(), modClass.name)
    } catch (t: Throwable) {
      LOGGER.error(LOADING, "Failed to create mod instance. ModID: $modId, class ${modClass.name}", t)
      throw ModLoadingException(modInfo, ModLoadingStage.CONSTRUCT, "fml.modloading.failedtoloadmod", t, modClass)
    }

    try {
      LOGGER.trace(LOADING, "Injecting Automatic event subscribers for $modId")
        AutoKotlinEventBusSubscriber.inject(this, scanResults, this.modClass.classLoader)
      LOGGER.trace(LOADING, "Completed Automatic event subscribers for $modId")
    } catch (t: Throwable) {
      LOGGER.error(LOADING, "Failed to register automatic subscribers. ModID: $modId, class ${modClass.name}", t)
      throw ModLoadingException(modInfo, ModLoadingStage.CONSTRUCT, "fml.modloading.failedtoloadmod", t, modClass)
    }
  }

  override fun matches(mod: Any): Boolean {
    return mod === modInstance
  }

  override fun getMod(): Any = modInstance

  override fun <T> acceptEvent(event: T) where T : Event?, T : IModBusEvent? {
    try {
      LOGGER.trace(LOADING, "Firing event for modid $modId : $event")
      eventBus.post(event)
      LOGGER.trace(LOADING, "Fired event for modid $modId : $event")
    } catch (cause: Throwable) {
      LOGGER.error(LOADING, "Caught exception during event $event dispatch for modid $modId", cause)
      throw ModLoadingException(modInfo, modLoadingStage, "fml.modloading.errorduringevent", cause)
    }
  }
}
