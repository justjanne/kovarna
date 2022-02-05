package de.justjanne.modding.kovarna.extensions

internal fun ModuleLayer.module(moduleName: String): Module =
  findModule(moduleName).orElseThrow()
