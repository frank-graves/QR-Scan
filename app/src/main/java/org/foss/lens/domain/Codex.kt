// org/foss/lens/domain/Codex.kt
package org.foss.lens.domain

import java.time.Instant

data class Codex(
    val id: Long = 0,
    val payload: String,
    val format: String,
    val timestamp: Instant = Instant.now()
)