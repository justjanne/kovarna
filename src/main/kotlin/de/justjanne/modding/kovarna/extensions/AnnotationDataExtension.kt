package de.justjanne.modding.kovarna.extensions

import net.minecraftforge.forgespi.language.ModFileScanData

internal inline fun <reified T> ModFileScanData.AnnotationData.getData(key: String): T? =
  annotationData[key] as? T
