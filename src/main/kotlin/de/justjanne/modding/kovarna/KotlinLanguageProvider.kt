package de.justjanne.modding.kovarna

import net.minecraftforge.forgespi.language.ILifecycleEvent
import net.minecraftforge.forgespi.language.IModLanguageProvider
import net.minecraftforge.forgespi.language.ModFileScanData
import java.util.function.Consumer
import java.util.function.Supplier

class KotlinLanguageProvider : IModLanguageProvider {
  override fun name() = "kovarna"

  override fun getFileVisitor(): Consumer<ModFileScanData> = KotlinModFileVisitor

  override fun <R : ILifecycleEvent<R>> consumeLifecycleEvent(consumer: Supplier<R>) = Unit
}
