package de.justjanne.modding.kovarna.extensions

internal inline val <T> Class<T>.kotlinInstance: T?
  get() {
    try {
      val field = getDeclaredField("INSTANCE")
      if (field.type != this) {
        return null
      }
      @Suppress("UNCHECKED_CAST")
      return field.get(null) as T?
    } catch (_: ReflectiveOperationException) {
      return null
    }
  }
