package com.example.playlistmaker.player.domain.models

sealed class PlayerState {
    object Default : PlayerState()
    object Preparing : PlayerState()
    data class Prepared(val duration: Int) : PlayerState()
    object Playing : PlayerState()
    object Paused : PlayerState()
    object Completed : PlayerState()
}