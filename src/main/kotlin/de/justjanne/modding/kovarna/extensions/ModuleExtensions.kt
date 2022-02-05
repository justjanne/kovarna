package de.justjanne.modding.kovarna.extensions

internal fun Module.loadClass(className: String): Class<*> =
  Class.forName(this, className)
