package de.justjanne.modding.kovarna.model

import de.justjanne.modding.kovarna.extensions.getData
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.loading.moddiscovery.ModAnnotation
import net.minecraftforge.forgespi.language.ModFileScanData
import org.objectweb.asm.Type

internal data class EventBusSubscriberMeta(
  val target: String,
  val modId: String?,
  val sides: Set<Dist>,
  val bus: Mod.EventBusSubscriber.Bus
) {
  companion object {
    private val EVENT_BUS_SUBSCRIBER: Type = Type.getType(Mod.EventBusSubscriber::class.java)
    private val DEFAULT_SIDES = setOf(Dist.CLIENT, Dist.DEDICATED_SERVER)

    @JvmStatic
    fun of(annotation: ModFileScanData.AnnotationData): EventBusSubscriberMeta? {
      if (annotation.annotationType != EVENT_BUS_SUBSCRIBER) {
        return null
      }

      val target = annotation.clazz.className
      val modId = annotation.getData<String>("modid")
      val sides = annotation.getData<List<ModAnnotation.EnumHolder>>("value")
        ?.map { Dist.valueOf(it.value) }
        ?.toSet()
        ?: DEFAULT_SIDES
      val bus = annotation.getData<ModAnnotation.EnumHolder>("bus")
        ?.let { Mod.EventBusSubscriber.Bus.valueOf(it.value) }
        ?: Mod.EventBusSubscriber.Bus.FORGE

      return EventBusSubscriberMeta(target, modId, sides, bus)
    }
  }
}
