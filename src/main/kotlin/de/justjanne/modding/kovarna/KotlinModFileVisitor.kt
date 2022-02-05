package de.justjanne.modding.kovarna

import net.minecraftforge.forgespi.language.ModFileScanData
import org.objectweb.asm.Type
import java.util.function.Consumer

internal object KotlinModFileVisitor : Consumer<ModFileScanData> {
  private val MOD_ANNOTATION = Type.getType("Lnet/minecraftforge/fml/common/Mod;")

  override fun accept(scan: ModFileScanData) {
    scan.addLanguageLoader(
      scan.annotations
        .asSequence()
        .filter { it.annotationType == MOD_ANNOTATION }
        .mapNotNull { KotlinModTarget.of(it) }
        .associateBy { it.modId }
    )
  }
}
