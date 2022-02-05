package de.justjanne.modding.kovarna

import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.ModLoadingContext

class KotlinModLoadingContext internal constructor(
  private val container: KotlinModContainer
) {
  /**
   * The mod's event bus, to allow subscription to Mod specific events
   */
  val modEventBus: IEventBus get() = container.eventBus

  companion object {
    /**
     *  Helper to get the right instance from the [ModLoadingContext] correctly.
     */
    @JvmStatic
    fun get(): KotlinModLoadingContext {
      return ModLoadingContext.get().extension()
    }
  }
}
