package de.justjanne.modding.kovarna.extensions

import java.util.function.Supplier

@PublishedApi
internal class StaticSupplier<T>(
  private val value: T
) : Supplier<T> {
  override fun get(): T = value
}

internal inline fun <reified T> supplierOf(value: T): Supplier<T> = StaticSupplier(value)
