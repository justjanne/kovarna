package de.justjanne.modding.kovarna.extensions

import de.justjanne.modding.kovarna.LOGGER
import net.minecraftforge.fml.Logging
import net.minecraftforge.fml.ModContainer
import net.minecraftforge.fml.ModLoadingException
import net.minecraftforge.fml.ModLoadingStage
import net.minecraftforge.forgespi.language.IModInfo
import net.minecraftforge.forgespi.language.ModFileScanData
import java.lang.reflect.InvocationTargetException

internal inline fun <reified T: ModContainer> newModContainer(
  info: IModInfo,
  className: String,
  modFileScanResults: ModFileScanData,
  layer: ModuleLayer
): ModContainer = newModContainer(T::class.qualifiedName!!, info, className, modFileScanResults, layer)

internal fun newModContainer(
  containerClassName: String,
  info: IModInfo,
  className: String,
  modFileScanResults: ModFileScanData,
  layer: ModuleLayer
): ModContainer {
  try {
    val containerClass = Thread.currentThread().contextClassLoader.loadClass(containerClassName)
    val constructor = containerClass.getConstructor(
      IModInfo::class.java,
      String::class.java,
      ModFileScanData::class.java,
      ModuleLayer::class.java
    )
    return constructor.newInstance(info, className, modFileScanResults, layer) as ModContainer
  } catch (e: InvocationTargetException) {
    LOGGER.fatal(Logging.LOADING, "Failed to build mod", e)
    if (e.targetException is ModLoadingException) {
      throw e
    } else {
      throw ModLoadingException(info, ModLoadingStage.CONSTRUCT, "fml.modloading.failedtoloadmodclass", e)
    }
  } catch (e: ReflectiveOperationException) {
    LOGGER.fatal(Logging.LOADING, "Unable to load KotlinModContainer", e)
    throw ModLoadingException(info, ModLoadingStage.CONSTRUCT, "fml.modloading.failedtoloadmodclass", e)
  }
}
