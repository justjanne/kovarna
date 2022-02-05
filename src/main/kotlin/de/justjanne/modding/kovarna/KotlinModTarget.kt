package de.justjanne.modding.kovarna

import de.justjanne.modding.kovarna.extensions.getData
import de.justjanne.modding.kovarna.extensions.newModContainer
import net.minecraftforge.forgespi.language.IModInfo
import net.minecraftforge.forgespi.language.IModLanguageProvider
import net.minecraftforge.forgespi.language.ModFileScanData

internal class KotlinModTarget(
  val modId: String,
  val className: String
) : IModLanguageProvider.IModLanguageLoader {
  @Suppress("UNCHECKED_CAST")
  override fun <T : Any> loadMod(info: IModInfo, modFileScanResults: ModFileScanData, layer: ModuleLayer): T {
    return newModContainer<KotlinModContainer>(
      info,
      className,
      modFileScanResults,
      layer
    ) as T
  }

  companion object {
    @JvmStatic
    fun of(annotation: ModFileScanData.AnnotationData): KotlinModTarget? {
      val modId = annotation.getData<String>("value") ?: return null
      val target = annotation.clazz.className
      return KotlinModTarget(modId, target)
    }
  }
}
