package com.kftx.threevolumecandles.model

enum class Direction(val label: String, val value: Int) {
    DOWN("down", -1),
    IDLE("idle", 0),
    UP("up", 1)
}